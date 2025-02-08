package com.server.HealthNet.Model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {

    public Appointment() {
    }

    public Appointment(Long patient_id, Long doctor_id, LocalDate date, LocalTime startTime, LocalTime endTime, boolean is_pending, boolean is_approved) {
        this.patient_id = patient_id;
        this.doctor_id = doctor_id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.is_pending = is_pending;
        this.is_approved = is_approved;
    }

    private Long appointment_id;
    private Long patient_id;
    private Long doctor_id;
    private LocalDate date;
    private LocalTime startTime; // Renamed
    private LocalTime endTime; // New attribute
    private boolean is_pending;
    private boolean is_approved;

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
}
