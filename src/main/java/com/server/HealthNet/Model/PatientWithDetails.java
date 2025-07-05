package com.server.HealthNet.Model;

import java.time.LocalDate;

/**
 * Optimized DTO class for patient data with additional details
 * Used to optimize patient queries by including additional information in a single query
 * This reduces the N+1 query problem when staff need comprehensive patient information
 * PERFORMANCE OPTIMIZED: Excludes heavy fields like images for faster loading
 */
public class PatientWithDetails {
    
    // Basic patient info (optimized - excluding heavy fields like images)
    private Long patient_id;
    private String name;
    private String gender;
    private Integer age;
    private String contact_info;
    private String address;
    private String weight;
    private String height;
    
    // Additional details for staff portal
    private Integer total_appointments;
    private Integer pending_appointments;
    private Integer approved_appointments;
    private LocalDate last_appointment_date;
    private Integer total_medical_records;
    private LocalDate last_record_date;
    private String last_diagnosis;
    private String emergency_contact;
    private String allergies;
    private String existing_conditions;
    
    // Default constructor
    public PatientWithDetails() {}
    
    // Getters and Setters
    public Long getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(Long patient_id) {
        this.patient_id = patient_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getContact_info() {
        return contact_info;
    }

    public void setContact_info(String contact_info) {
        this.contact_info = contact_info;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public Integer getTotal_appointments() {
        return total_appointments;
    }

    public void setTotal_appointments(Integer total_appointments) {
        this.total_appointments = total_appointments;
    }

    public Integer getPending_appointments() {
        return pending_appointments;
    }

    public void setPending_appointments(Integer pending_appointments) {
        this.pending_appointments = pending_appointments;
    }

    public Integer getApproved_appointments() {
        return approved_appointments;
    }

    public void setApproved_appointments(Integer approved_appointments) {
        this.approved_appointments = approved_appointments;
    }

    public LocalDate getLast_appointment_date() {
        return last_appointment_date;
    }

    public void setLast_appointment_date(LocalDate last_appointment_date) {
        this.last_appointment_date = last_appointment_date;
    }

    public Integer getTotal_medical_records() {
        return total_medical_records;
    }

    public void setTotal_medical_records(Integer total_medical_records) {
        this.total_medical_records = total_medical_records;
    }

    public LocalDate getLast_record_date() {
        return last_record_date;
    }

    public void setLast_record_date(LocalDate last_record_date) {
        this.last_record_date = last_record_date;
    }

    public String getLast_diagnosis() {
        return last_diagnosis;
    }

    public void setLast_diagnosis(String last_diagnosis) {
        this.last_diagnosis = last_diagnosis;
    }

    public String getEmergency_contact() {
        return emergency_contact;
    }

    public void setEmergency_contact(String emergency_contact) {
        this.emergency_contact = emergency_contact;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getExisting_conditions() {
        return existing_conditions;
    }

    public void setExisting_conditions(String existing_conditions) {
        this.existing_conditions = existing_conditions;
    }

    @Override
    public String toString() {
        return "PatientWithDetails{" +
                "patient_id=" + patient_id +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", total_appointments=" + total_appointments +
                ", pending_appointments=" + pending_appointments +
                ", approved_appointments=" + approved_appointments +
                ", total_medical_records=" + total_medical_records +
                '}';
    }
}
