package com.server.HealthNet.Service;

import com.server.HealthNet.Model.LabResult;
import com.server.HealthNet.Model.MedicalRecord;
import com.server.HealthNet.Model.MedicalRecordAttachment;
import com.server.HealthNet.Model.MedicalRecordAudit;
import com.server.HealthNet.Repository.LabResultRepository;
import com.server.HealthNet.Repository.MedicalRecordAttachmentRepository;
import com.server.HealthNet.Repository.MedicalRecordAuditRepository;
import com.server.HealthNet.Repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class MedicalRecordService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private MedicalRecordAttachmentRepository attachmentRepository;

    @Autowired
    private LabResultRepository labResultRepository;

    @Autowired
    private MedicalRecordAuditRepository auditRepository;

    public List<MedicalRecord> getAllMedicalRecords() {
        return medicalRecordRepository.findAll();
    }

    public MedicalRecord getMedicalRecordById(Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id);
        if (record != null) {
            // Load attachments
            List<MedicalRecordAttachment> attachments = attachmentRepository.findByRecordId(id);
            record.setAttachments(attachments);

            // Load lab results
            List<LabResult> labResults = labResultRepository.findByRecordId(id);
            record.setLabResults(labResults);
        }
        return record;
    }

    @Transactional
    public Long createMedicalRecord(MedicalRecord record, Long userId) {
        // Print all fields before saving
        System.out.println("======= Creating New Medical Record =======");
        System.out.println("Record ID: " + record.getRecordId());
        System.out.println("Patient ID: " + record.getPatientId());
        System.out.println("Doctor ID: " + record.getDoctorId());
        System.out.println("Department ID: " + record.getDepartmentId());
        System.out.println("Treatment ID: " + record.getTreatmentId());
        System.out.println("Record Type: " + record.getRecordType());
        System.out.println("Title: " + record.getTitle());
        System.out.println("Diagnosis: " + record.getDiagnosis());
        System.out.println("Notes: " + record.getNotes());
        System.out.println("Record Date: " + record.getRecordDate());
        System.out.println("Blood Pressure: " + record.getBloodPressure());
        System.out.println("Heart Rate: " + record.getHeartRate());
        System.out.println("Respiratory Rate: " + record.getRespiratoryRate());
        System.out.println("Temperature: " + record.getTemperature());
        System.out.println("Oxygen Saturation: " + record.getOxygenSaturation());
        System.out.println("Height: " + record.getHeight());
        System.out.println("Weight: " + record.getWeight());
        System.out.println("=======================================");
        
        // Save the medical record
        Long recordId = medicalRecordRepository.save(record);

        // Audit the creation
        MedicalRecordAudit audit = new MedicalRecordAudit(recordId, userId, "CREATE", "Created medical record");
        auditRepository.save(audit);

        return recordId;
    }

    @Transactional
    public int updateMedicalRecord(MedicalRecord record, Long userId) {
        // Print all fields before updating
        System.out.println("======= Updating Medical Record: " + record.getRecordId() + " =======");
        System.out.println("Record ID: " + record.getRecordId());
        System.out.println("Patient ID: " + record.getPatientId());
        System.out.println("Doctor ID: " + record.getDoctorId());
        System.out.println("Department ID: " + record.getDepartmentId());
        System.out.println("Treatment ID: " + record.getTreatmentId());
        System.out.println("Record Type: " + record.getRecordType());
        System.out.println("Title: " + record.getTitle());
        System.out.println("Diagnosis: " + record.getDiagnosis());
        System.out.println("Notes: " + record.getNotes());
        System.out.println("Record Date: " + record.getRecordDate());
        System.out.println("Blood Pressure: " + record.getBloodPressure());
        System.out.println("Heart Rate: " + record.getHeartRate());
        System.out.println("Respiratory Rate: " + record.getRespiratoryRate());
        System.out.println("Temperature: " + record.getTemperature());
        System.out.println("Oxygen Saturation: " + record.getOxygenSaturation());
        System.out.println("Height: " + record.getHeight());
        System.out.println("Weight: " + record.getWeight());
        System.out.println("=======================================");
        
        int result = medicalRecordRepository.update(record);

        if (result > 0) {
            // Audit the update
            MedicalRecordAudit audit = new MedicalRecordAudit(record.getRecordId(), userId, "UPDATE",
                    "Updated medical record");
            auditRepository.save(audit);
        }

        return result;
    }

    @Transactional
    public int deleteMedicalRecord(Long id, Long userId) {
        try {
            // Delete related attachments and lab results
            attachmentRepository.deleteByRecordId(id);
            labResultRepository.deleteByRecordId(id);

            // Delete audit trail entries first to resolve foreign key constraint
            auditRepository.deleteByRecordId(id);

            // Finally delete the medical record
            return medicalRecordRepository.deleteById(id);
        } catch (Exception e) {
            // Log deletion attempt even if it failed
            try {
                MedicalRecordAudit audit = new MedicalRecordAudit(id, userId, "DELETE_ATTEMPT",
                        "Attempted to delete medical record: " + e.getMessage());
                auditRepository.save(audit);
            } catch (Exception ex) {
                // Ignore if we can't even save the audit
            }
            throw e; // Re-throw the exception to be handled by the controller
        }
    }

    public List<MedicalRecord> getPatientMedicalRecords(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId);
    }

    public List<MedicalRecord> getDoctorMedicalRecords(Long doctorId) {
        return medicalRecordRepository.findByDoctorId(doctorId);
    }

    public List<MedicalRecord> getPatientMedicalRecordsByDateRange(Long patientId, LocalDate startDate,
            LocalDate endDate) {
        return medicalRecordRepository.findByPatientIdAndDateRange(patientId, startDate, endDate);
    }

    // Methods for handling attachments
    @Transactional
    public Long addAttachment(Long recordId, MultipartFile file, String fileType, String description, Long userId)
            throws IOException {
        // Create attachment
        MedicalRecordAttachment attachment = new MedicalRecordAttachment();
        attachment.setRecordId(recordId);
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileType(fileType);
        attachment.setContentType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setFileData(file.getBytes());
        attachment.setDescription(description);

        Long attachmentId = attachmentRepository.save(attachment);

        // Audit the attachment addition
        MedicalRecordAudit audit = new MedicalRecordAudit(recordId, userId, "ATTACHMENT_ADD",
                "Added attachment: " + file.getOriginalFilename());
        auditRepository.save(audit);

        return attachmentId;
    }

    public MedicalRecordAttachment getAttachment(Long attachmentId) {
        return attachmentRepository.findById(attachmentId);
    }

    @Transactional
    public int deleteAttachment(Long attachmentId, Long userId) {
        MedicalRecordAttachment attachment = attachmentRepository.findById(attachmentId);
        if (attachment != null) {
            // Audit the attachment deletion
            MedicalRecordAudit audit = new MedicalRecordAudit(attachment.getRecordId(), userId, "ATTACHMENT_DELETE",
                    "Deleted attachment: " + attachment.getFileName());
            auditRepository.save(audit);

            return attachmentRepository.deleteById(attachmentId);
        }
        return 0;
    }

    // Methods for handling lab results
    @Transactional
    public Long addLabResult(LabResult labResult, Long userId) {
        // Print all fields before saving
        System.out.println("======= Adding New Lab Result =======");
        System.out.println("Result ID: " + labResult.getResultId());
        System.out.println("Record ID: " + labResult.getRecordId());
        System.out.println("Test Name: " + labResult.getTestName());
        System.out.println("Test Value: " + labResult.getTestValue());
        System.out.println("Test Unit: " + labResult.getTestUnit());
        System.out.println("Reference Range: " + labResult.getReferenceRange());
        System.out.println("Is Abnormal: " + labResult.getIsAbnormal());
        System.out.println("Notes: " + labResult.getNotes());
        System.out.println("=======================================");
        
        Long resultId = labResultRepository.save(labResult);

        // Audit the lab result addition
        MedicalRecordAudit audit = new MedicalRecordAudit(labResult.getRecordId(), userId, "LAB_RESULT_ADD",
                "Added lab result: " + labResult.getTestName());
        auditRepository.save(audit);

        return resultId;
    }

    public List<LabResult> getLabResults(Long recordId) {
        return labResultRepository.findByRecordId(recordId);
    }

    public LabResult getLabResult(Long resultId) {
        return labResultRepository.findById(resultId);
    }

    @Transactional
    public int updateLabResult(LabResult labResult, Long userId) {
        // Print all fields before updating
        System.out.println("======= Updating Lab Result: " + labResult.getResultId() + " =======");
        System.out.println("Result ID: " + labResult.getResultId());
        System.out.println("Record ID: " + labResult.getRecordId());
        System.out.println("Test Name: " + labResult.getTestName());
        System.out.println("Test Value: " + labResult.getTestValue());
        System.out.println("Test Unit: " + labResult.getTestUnit());
        System.out.println("Reference Range: " + labResult.getReferenceRange());
        System.out.println("Is Abnormal: " + labResult.getIsAbnormal());
        System.out.println("Notes: " + labResult.getNotes());
        System.out.println("=======================================");
        
        int result = labResultRepository.update(labResult);

        if (result > 0) {
            // Audit the lab result update
            MedicalRecordAudit audit = new MedicalRecordAudit(labResult.getRecordId(), userId, "LAB_RESULT_UPDATE",
                    "Updated lab result: " + labResult.getTestName());
            auditRepository.save(audit);
        }

        return result;
    }

    @Transactional
    public int deleteLabResult(Long resultId, Long userId) {
        LabResult labResult = labResultRepository.findById(resultId);
        if (labResult != null) {
            // Audit the lab result deletion
            MedicalRecordAudit audit = new MedicalRecordAudit(labResult.getRecordId(), userId, "LAB_RESULT_DELETE",
                    "Deleted lab result: " + labResult.getTestName());
            auditRepository.save(audit);

            return labResultRepository.deleteById(resultId);
        }
        return 0;
    }

    // Methods for audit trail
    public List<MedicalRecordAudit> getRecordAuditTrail(Long recordId) {
        return auditRepository.findByRecordId(recordId);
    }
}
