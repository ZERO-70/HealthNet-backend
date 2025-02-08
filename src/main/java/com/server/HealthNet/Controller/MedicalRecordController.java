package com.server.HealthNet.Controller;

import com.server.HealthNet.Model.MedicalRecord;
import com.server.HealthNet.Model.UserAuthentication;
import com.server.HealthNet.Service.MedicalRecordService;
import com.server.HealthNet.Service.UserAuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medical_record")
@CrossOrigin(origins = "*")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @GetMapping
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('STAFF')")
    public List<MedicalRecord> getAllMedicalRecords() {
        return medicalRecordService.getAllMedicalRecords();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('STAFF')")
    public MedicalRecord getMedicalRecordById(@PathVariable Long id) {
        return medicalRecordService.getMedicalRecordById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<String> createMedicalRecord(@RequestBody MedicalRecord record) {
        return medicalRecordService.createMedicalRecord(record) > 0
                ? new ResponseEntity<>("Medical Record Insertion successfully", HttpStatus.OK)
                : new ResponseEntity<>("Medical Record Insertion failed", HttpStatus.NOT_FOUND);
    }

    @PutMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<String> updateMedicalRecord(@RequestBody MedicalRecord record) {
        return medicalRecordService.updateMedicalRecord(record) > 0
                ? new ResponseEntity<>("Medical Record Updated successfully", HttpStatus.OK)
                : new ResponseEntity<>("Medical Record Update failed", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<String> deleteMedicalRecord(@PathVariable Long id) {
        return medicalRecordService.deleteMedicalRecord(id) > 0
                ? new ResponseEntity<>("Medical Record Deleted successfully", HttpStatus.OK)
                : new ResponseEntity<>("Medical Record Deletion failed", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/getmine")
    @PreAuthorize("hasRole('PATIENT')")
    public List<MedicalRecord> getmymedicalRecords() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);
        if (userAuthentication == null) {
            return null;
        }
        return medicalRecordService.getmyMedicalRecords(userAuthentication.getPersonId());
    }

    @GetMapping("/patient/{id}")
    @PreAuthorize("hasRole('STAFF') or hasRole('DOCTOR')")
    public List<MedicalRecord> getmymedicalRecords(@PathVariable Long id) {
        return medicalRecordService.getmyMedicalRecords(id);
    }
}
