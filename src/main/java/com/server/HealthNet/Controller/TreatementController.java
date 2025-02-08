package com.server.HealthNet.Controller;

import com.server.HealthNet.Model.Role;
import com.server.HealthNet.Model.Treatement;
import com.server.HealthNet.Model.UserAuthentication;
import com.server.HealthNet.Service.TreatementService;
import com.server.HealthNet.Service.UserAuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/treatement")
@CrossOrigin(origins = "*")
public class TreatementController {

    @Autowired
    private TreatementService treatmentService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    // no restriction
    @GetMapping
    public List<Treatement> getAllTreatments() {
        return treatmentService.getAllTreatments();
    }

    @GetMapping("/{id}")
    public Treatement getTreatmentById(@PathVariable Long id) {
        return treatmentService.getTreatmentById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('STAFF')")
    public ResponseEntity<String> createTreatment(@RequestBody Treatement treatment) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);

        if (userAuthentication.getRole().equals(Role.DOCTOR)) {
            if (!userAuthentication.getPersonId().equals(treatment.getDoctor_id())) {
                return new ResponseEntity<>("You are not authorized to create this treatment for another doctor",
                        HttpStatus.FORBIDDEN);
            }
        }

        return treatmentService.createTreatment(treatment) > 0
                ? new ResponseEntity<>("Treatment Inserted successfully", HttpStatus.OK)
                : new ResponseEntity<>("Treatment Insertion failed", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('STAFF')")
    public ResponseEntity<String> updateTreatment(@PathVariable Long id, @RequestBody Treatement treatment) {
        treatment.setTreatement_id(id);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);

        if (userAuthentication.getRole().equals(Role.DOCTOR)) {
            if (!userAuthentication.getPersonId().equals(treatment.getDoctor_id())) {
                return new ResponseEntity<>("You are not authorized to update this treatment for another doctor",
                        HttpStatus.FORBIDDEN);
            }
        }

        return treatmentService.updateTreatment(treatment) > 0
                ? new ResponseEntity<>("Treatment Updated successfully", HttpStatus.OK)
                : new ResponseEntity<>("Treatment Update failed", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('STAFF')")
    public ResponseEntity<String> deleteTreatment(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);

        Treatement treatment = treatmentService.getTreatmentById(id);
        if (treatment == null) {
            return new ResponseEntity<>("Treatment not found", HttpStatus.NOT_FOUND);
        }

        if (userAuthentication.getRole().equals(Role.DOCTOR)) {
            if (!userAuthentication.getPersonId().equals(treatment.getDoctor_id())) {
                return new ResponseEntity<>("You are not authorized to delete this treatment for another doctor",
                        HttpStatus.FORBIDDEN);
            }
        }

        return treatmentService.deleteTreatment(id) > 0
                ? new ResponseEntity<>("Treatment deleted successfully", HttpStatus.OK)
                : new ResponseEntity<>("Treatment Deletion failed", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
