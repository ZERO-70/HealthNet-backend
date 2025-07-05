package com.server.HealthNet.Model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO class for appointment data with patient and doctor details
 * Used to optimize appointment queries by including names in a single query
 */
public class AppointmentWithDetails {
    
    private Long appointment_id;
    private Long patient_id;
    private Long doctor_id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean is_pending;
    private boolean is_approved;
    private String patient_name;
    private String doctor_name;
    private String doctor_specialization;

    // Default constructor
    public AppointmentWithDetails() {
    }

    // Full constructor
    public AppointmentWithDetails(Long appointment_id, Long patient_id, Long doctor_id, 
                                 LocalDate date, LocalTime startTime, LocalTime endTime, 
                                 boolean is_pending, boolean is_approved, 
                                 String patient_name, String doctor_name, String doctor_specialization) {
        this.appointment_id = appointment_id;
        this.patient_id = patient_id;
        this.doctor_id = doctor_id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.is_pending = is_pending;
        this.is_approved = is_approved;
        this.patient_name = patient_name;
        this.doctor_name = doctor_name;
        this.doctor_specialization = doctor_specialization;
    }

    // Getters and Setters
    public Long getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(Long appointment_id) {
        this.appointment_id = appointment_id;
    }

    public Long getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(Long patient_id) {
        this.patient_id = patient_id;
    }

    public Long getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(Long doctor_id) {
        this.doctor_id = doctor_id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isIs_pending() {
        return is_pending;
    }

    public void setIs_pending(boolean is_pending) {
        this.is_pending = is_pending;
    }

    public boolean isIs_approved() {
        return is_approved;
    }

    public void setIs_approved(boolean is_approved) {
        this.is_approved = is_approved;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getDoctor_specialization() {
        return doctor_specialization;
    }

    public void setDoctor_specialization(String doctor_specialization) {
        this.doctor_specialization = doctor_specialization;
    }

    @Override
    public String toString() {
        return "AppointmentWithDetails{" +
                "appointment_id=" + appointment_id +
                ", patient_id=" + patient_id +
                ", doctor_id=" + doctor_id +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", is_pending=" + is_pending +
                ", is_approved=" + is_approved +
                ", patient_name='" + patient_name + '\'' +
                ", doctor_name='" + doctor_name + '\'' +
                ", doctor_specialization='" + doctor_specialization + '\'' +
                '}';
    }
}
