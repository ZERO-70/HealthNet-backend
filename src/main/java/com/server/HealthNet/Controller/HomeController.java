package com.server.HealthNet.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

import com.server.HealthNet.Model.UserAuthentication;
import com.server.HealthNet.Service.UserAuthenticationService;

@RestController
@RequestMapping("/home")
@CrossOrigin(origins = "*")
public class HomeController {

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    /**
     * Returns the signed-in user's identity.
     *
     * This used to return a bare string containing only the role, so the frontend
     * had no way to learn the user's id and stored none — which broke every
     * feature needing it (saving availability, for one).
     *
     * The response is now JSON. The clients already attempt JSON.parse first and
     * fall back to substring-matching the role out of the text, and the "message"
     * field preserves the original wording, so older builds keep working.
     *
     * Note that person_id IS the role id: doctor/patient/staff use shared-primary-key
     * inheritance off person, so doctor.doctor_id equals person.person_id. The
     * role-specific key is included as well because that is what the clients look for.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> welcome() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);

        Map<String, Object> body = new LinkedHashMap<>();

        if (userAuthentication == null) {
            body.put("message", "Welcom to HealthNet---you are not registered");
            body.put("registered", false);
            return ResponseEntity.status(HttpStatus.OK).body(body);
        }

        String role = userAuthentication.getRole().toString();
        Long personId = userAuthentication.getPersonId();

        body.put("message", "Welcom to HealthNet---you are " + role);
        body.put("registered", true);
        body.put("username", username);
        body.put("role", role);
        body.put("person_id", personId);
        body.put("id", personId);
        body.put("subscription", userAuthentication.getSubscription());

        if (personId != null) {
            switch (role) {
                case "DOCTOR" -> body.put("doctor_id", personId);
                case "PATIENT" -> body.put("patient_id", personId);
                case "STAFF" -> body.put("staff_id", personId);
                case "ADMIN" -> body.put("admin_id", personId);
                default -> { }
            }
        }

        return ResponseEntity.ok(body);
    }
}
