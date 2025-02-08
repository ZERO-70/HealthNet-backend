package com.server.HealthNet.Controller;

import com.server.HealthNet.Model.Avalibility;
import com.server.HealthNet.Model.UserAuthentication;
import com.server.HealthNet.Service.AvalibilityService;
import com.server.HealthNet.Service.UserAuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/avalibility")
@CrossOrigin(origins = "*")
public class AvalibilityController {

    @Autowired
    private AvalibilityService service;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<String> addAvalibility(@RequestBody Avalibility avalibility) {
        // Get the logged-in user's details
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);

        // Ensure the logged-in user is the doctor associated with the availability
        if (!userAuthentication.getPersonId().equals(avalibility.getDoctor_id())) {
            return new ResponseEntity<>("You are not authorized to add availability for another doctor",
                    HttpStatus.FORBIDDEN);
        }

        // Proceed with adding availability
        int result = service.addAvalibility(avalibility);
        return result > 0
                ? new ResponseEntity<>("Availability Inserted successfully", HttpStatus.OK)
                : new ResponseEntity<>("Availability Insertion failed", HttpStatus.NOT_FOUND);
    }

    @GetMapping
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public List<Avalibility> getAllAvalibilities() {
        return service.getAllAvalibilities();
    }

    @GetMapping("/getmine")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Optional<Avalibility>> getmyAvalibility() {
        // Get the logged-in user's details
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);

        // Fetch availability based on the logged-in user's doctor_id
        Long doctorId = userAuthentication.getPersonId();
        Optional<Avalibility> avalibility = service.getAvalibilityById(doctorId);

        // Return the availability or a 404 if not found
        return avalibility.isPresent()
                ? new ResponseEntity<>(avalibility, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<String> deleteAvalibility() {
        // Get the logged-in user's details
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);

        // Use the logged-in user's doctor_id to delete their availability
        Long doctorId = userAuthentication.getPersonId();
        int result = service.deleteAvalibilityById(doctorId);

        return result > 0
                ? new ResponseEntity<>("Availability Deleted successfully", HttpStatus.OK)
                : new ResponseEntity<>("No Availability found for deletion", HttpStatus.NOT_FOUND);
    }

    @PutMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<String> updateAvalibility(@RequestBody Avalibility avalibility) {
        // Get the logged-in user's details
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);

        // Ensure the logged-in user's doctor_id matches the doctor_id in the request
        Long doctorId = userAuthentication.getPersonId();
        if (!doctorId.equals(avalibility.getDoctor_id())) {
            return new ResponseEntity<>("You are not authorized to update this availability", HttpStatus.FORBIDDEN);
        }

        // Proceed with the update
        avalibility.toString();
        int result = service.updateAvalibility(doctorId, avalibility);
        return result > 0
                ? new ResponseEntity<>("Availability Updated successfully", HttpStatus.OK)
                : new ResponseEntity<>("Availability Update failed", HttpStatus.NOT_FOUND);
    }

}
