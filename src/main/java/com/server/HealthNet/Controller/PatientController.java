package com.server.HealthNet.Controller;

import com.server.HealthNet.Model.Patient;
import com.server.HealthNet.Model.Role;
import com.server.HealthNet.Model.UserAuthentication;
import com.server.HealthNet.Service.PatientService;
import com.server.HealthNet.Service.UserAuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/patient")
@CrossOrigin(origins = "*")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN') or hasRole('STAFF') or hasRole('DOCTOR')")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);
        if (userAuthentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // i hope this works
        if (!userAuthentication.getPersonId().equals(id) && userAuthentication.getRole() != Role.ADMIN
                && userAuthentication.getRole() != Role.STAFF && userAuthentication.getRole() != Role.DOCTOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<Patient> patient = patientService.getPatientById(id);
        return patient.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/getmine")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Patient> getCurrentPatient() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);
        if (userAuthentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Patient> patient = patientService.getPatientById(userAuthentication.getPersonId());
        return patient.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Long> addPatient(@RequestBody Patient patient) {
        Long result = patientService.addPatient(patient);
        if (result > 0) {
            return new ResponseEntity<>(result, HttpStatus.CREATED);

        } else {
            return new ResponseEntity<>(-1L, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<String> updatePatient(@RequestBody Patient patient) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);
        if (userAuthentication.getPersonId() != patient.getId()) {
            return new ResponseEntity<>("Ids don,t match", HttpStatus.FORBIDDEN);
        }

        int rowsAffected = patientService.updatePatient(patient);
        if (rowsAffected > 0) {
            return new ResponseEntity<>("Patient updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Patient update failed", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deletePatientById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);
        if (userAuthentication == null) {
            response.put("success", false);
            response.put("message", "Authentication error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        // Authorization check: patient can only delete their own account, admin can delete any
        if (!userAuthentication.getPersonId().equals(id) && userAuthentication.getRole() != Role.ADMIN) {
            response.put("success", false);
            response.put("message", "You can only delete your own account");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            int rowsAffected = patientService.deletePatientById(id);
            if (rowsAffected > 0) {
                response.put("success", true);
                response.put("message", "Patient deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Patient deletion failed - patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Patient deletion failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
