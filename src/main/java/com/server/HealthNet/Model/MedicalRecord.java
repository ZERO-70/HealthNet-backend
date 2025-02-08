package com.server.HealthNet.Model;

import java.time.LocalDate;

public class MedicalRecord {
    private Long record_id;
    private Long department_id;
    private Long patient_id;
    private Long treatement_id;
    private String diagnosis;
    private String bloodpressure;
    private LocalDate date;
    public MedicalRecord() {
    }
    public MedicalRecord(Long department_id, Long patient_id, Long treatement_id, String diagnosis,
            String bloodpressure, LocalDate date) {
        this.department_id = department_id;
        this.patient_id = patient_id;
        this.treatement_id = treatement_id;
        this.diagnosis = diagnosis;
        this.bloodpressure = bloodpressure;
        this.date = date;
    }
    public Long getRecord_id() {
        return record_id;
    }
    public void setRecord_id(Long record_id) {
        this.record_id = record_id;
    }
    public Long getDepartment_id() {
        return department_id;
    }
    public void setDepartment_id(Long department_id) {
        this.department_id = department_id;
    }
    public Long getPatient_id() {
        return patient_id;
    }
    public void setPatient_id(Long patient_id) {
        this.patient_id = patient_id;
    }
    public Long getTreatement_id() {
        return treatement_id;
    }
    public void setTreatement_id(Long treatement_id) {
        this.treatement_id = treatement_id;
    }
    public String getDiagnosis() {
        return diagnosis;
    }
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
    public String getBloodpressure() {
        return bloodpressure;
    }
    public void setBloodpressure(String bloodpressure) {
        this.bloodpressure = bloodpressure;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
}
