package com.server.HealthNet.Controller;

import com.server.HealthNet.Model.LabResult;
import com.server.HealthNet.Model.MedicalRecord;
import com.server.HealthNet.Model.MedicalRecordAttachment;
import com.server.HealthNet.Model.MedicalRecordAudit;
import com.server.HealthNet.Model.UserAuthentication;
import com.server.HealthNet.Service.MedicalRecordService;
import com.server.HealthNet.Service.UserAuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/medical_record")
@CrossOrigin(origins = "*")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    // Helper method to get current authenticated user
    private UserAuthentication getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userAuthenticationService.getUserByUsername(username);
    }

    @GetMapping
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('STAFF')")
    public List<MedicalRecord> getAllMedicalRecords() {
        return medicalRecordService.getAllMedicalRecords();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('STAFF') or hasRole('PATIENT')")
    public ResponseEntity<?> getMedicalRecordById(@PathVariable Long id) {
        MedicalRecord record = medicalRecordService.getMedicalRecordById(id);

        if (record == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if patient is accessing their own record
        UserAuthentication user = getCurrentUser();
        if (user != null && user.getRole().toString().equals("PATIENT")
                && !record.getPatientId().equals(user.getPersonId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only access your own medical records");
        }

        // Log access to the audit trail
        if (user != null) {
            MedicalRecordAudit audit = new MedicalRecordAudit(id, user.getPersonId(), "VIEW", "Viewed medical record");
            medicalRecordService.getRecordAuditTrail(id);
        }

        return ResponseEntity.ok(record);
    }

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> createMedicalRecord(@RequestBody MedicalRecord record) {
        Map<String, Object> response = new HashMap<>();
        UserAuthentication user = getCurrentUser();

        if (user == null) {
            response.put("success", false);
            response.put("message", "Authentication error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            Long recordId = medicalRecordService.createMedicalRecord(record, user.getPersonId());
            response.put("success", true);
            response.put("message", "Medical Record created successfully");
            response.put("recordId", recordId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Medical Record creation failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> updateMedicalRecord(@PathVariable Long id,
            @RequestBody MedicalRecord record) {
        Map<String, Object> response = new HashMap<>();
        UserAuthentication user = getCurrentUser();

        if (user == null) {
            response.put("success", false);
            response.put("message", "Authentication error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        record.setRecordId(id); // Ensure the ID is set correctly

        try {
            int result = medicalRecordService.updateMedicalRecord(record, user.getPersonId());
            if (result > 0) {
                response.put("success", true);
                response.put("message", "Medical Record updated successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Medical Record update failed - record not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Medical Record update failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> deleteMedicalRecord(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        UserAuthentication user = getCurrentUser();

        if (user == null) {
            response.put("success", false);
            response.put("message", "Authentication error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            int result = medicalRecordService.deleteMedicalRecord(id, user.getPersonId());
            if (result > 0) {
                response.put("success", true);
                response.put("message", "Medical Record deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Medical Record deletion failed - record not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Medical Record deletion failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/patient/records")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> getMyMedicalRecords() {
        UserAuthentication user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication error");
        }

        List<MedicalRecord> records = medicalRecordService.getPatientMedicalRecords(user.getPersonId());
        return ResponseEntity.ok(records);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> getPatientMedicalRecords(@PathVariable Long patientId) {
        List<MedicalRecord> records = medicalRecordService.getPatientMedicalRecords(patientId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> getDoctorMedicalRecords(@PathVariable Long doctorId) {
        List<MedicalRecord> records = medicalRecordService.getDoctorMedicalRecords(doctorId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/patient/{patientId}/daterange")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('STAFF') or hasRole('PATIENT')")
    public ResponseEntity<?> getPatientMedicalRecordsByDateRange(
            @PathVariable Long patientId,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        UserAuthentication user = getCurrentUser();
        if (user != null && user.getRole().toString().equals("PATIENT") && !patientId.equals(user.getPersonId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only access your own medical records");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        List<MedicalRecord> records = medicalRecordService.getPatientMedicalRecordsByDateRange(patientId, start, end);
        return ResponseEntity.ok(records);
    }

    // Lab Results APIs
    @PostMapping("/{recordId}/lab-results")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> addLabResult(
            @PathVariable Long recordId,
            @RequestBody LabResult labResult) {

        Map<String, Object> response = new HashMap<>();
        UserAuthentication user = getCurrentUser();

        if (user == null) {
            response.put("success", false);
            response.put("message", "Authentication error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        labResult.setRecordId(recordId); // Set record ID

        try {
            Long resultId = medicalRecordService.addLabResult(labResult, user.getPersonId());
            response.put("success", true);
            response.put("message", "Lab result added successfully");
            response.put("resultId", resultId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to add lab result: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{recordId}/lab-results")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('STAFF') or hasRole('PATIENT')")
    public ResponseEntity<?> getLabResults(@PathVariable Long recordId) {
        MedicalRecord record = medicalRecordService.getMedicalRecordById(recordId);

        if (record == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if patient is accessing their own record
        UserAuthentication user = getCurrentUser();
        if (user != null && user.getRole().toString().equals("PATIENT")
                && !record.getPatientId().equals(user.getPersonId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only access your own medical records");
        }

        List<LabResult> labResults = medicalRecordService.getLabResults(recordId);
        return ResponseEntity.ok(labResults);
    }

    @PutMapping("/lab-results/{resultId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> updateLabResult(
            @PathVariable Long resultId,
            @RequestBody LabResult labResult) {

        Map<String, Object> response = new HashMap<>();
        UserAuthentication user = getCurrentUser();

        if (user == null) {
            response.put("success", false);
            response.put("message", "Authentication error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        labResult.setResultId(resultId); // Set result ID

        try {
            int result = medicalRecordService.updateLabResult(labResult, user.getPersonId());
            if (result > 0) {
                response.put("success", true);
                response.put("message", "Lab result updated successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Lab result update failed - result not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lab result update failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/lab-results/{resultId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> deleteLabResult(@PathVariable Long resultId) {
        Map<String, Object> response = new HashMap<>();
        UserAuthentication user = getCurrentUser();
        System.out.println("got the user");

        if (user == null) {
            System.out.println("User is null");
            response.put("success", false);
            response.put("message", "Authentication error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            System.out.println("Deleting lab result with ID: " + resultId);
            int result = medicalRecordService.deleteLabResult(resultId, user.getPersonId());
            System.out.println("Result of deletion: " + result);
            if (result > 0) {
                response.put("success", true);
                response.put("message", "Lab result deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Lab result deletion failed - result not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            System.out.println("Exception during deletion: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Lab result deletion failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // File Attachment APIs
    @PostMapping("/{recordId}/attachments")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> addAttachment(
            @PathVariable Long recordId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileType") String fileType,
            @RequestParam(value = "description", required = false) String description) {

        Map<String, Object> response = new HashMap<>();
        UserAuthentication user = getCurrentUser();

        if (user == null) {
            response.put("success", false);
            response.put("message", "Authentication error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            Long attachmentId = medicalRecordService.addAttachment(
                    recordId, file, fileType, description, user.getPersonId());

            response.put("success", true);
            response.put("message", "Attachment uploaded successfully");
            response.put("attachmentId", attachmentId);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to upload attachment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/attachments/{attachmentId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('STAFF') or hasRole('PATIENT')")
    public ResponseEntity<?> downloadAttachment(@PathVariable Long attachmentId) {
        try {
            MedicalRecordAttachment attachment = medicalRecordService.getAttachment(attachmentId);

            if (attachment == null) {
                return ResponseEntity.notFound().build();
            }

            // Check if patient is trying to access and verify it's their record
            UserAuthentication user = getCurrentUser();
            if (user != null && user.getRole().toString().equals("PATIENT")) {
                MedicalRecord record = medicalRecordService.getMedicalRecordById(attachment.getRecordId());
                if (record == null || !record.getPatientId().equals(user.getPersonId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("You can only access attachments from your own medical records");
                }
            }

            // Prepare file for download
            Resource resource = new ByteArrayResource(attachment.getFileData());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + attachment.getFileName() + "\"")
                    .contentType(MediaType.parseMediaType(attachment.getContentType()))
                    .contentLength(attachment.getFileSize())
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to download attachment: " + e.getMessage());
        }
    }

    @DeleteMapping("/attachments/{attachmentId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> deleteAttachment(@PathVariable Long attachmentId) {
        Map<String, Object> response = new HashMap<>();
        UserAuthentication user = getCurrentUser();

        if (user == null) {
            response.put("success", false);
            response.put("message", "Authentication error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            int result = medicalRecordService.deleteAttachment(attachmentId, user.getPersonId());
            if (result > 0) {
                response.put("success", true);
                response.put("message", "Attachment deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Attachment deletion failed - attachment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Attachment deletion failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Audit Trail API
    @GetMapping("/{recordId}/audit-trail")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getAuditTrail(@PathVariable Long recordId) {
        List<MedicalRecordAudit> auditTrail = medicalRecordService.getRecordAuditTrail(recordId);
        return ResponseEntity.ok(auditTrail);
    }
}
