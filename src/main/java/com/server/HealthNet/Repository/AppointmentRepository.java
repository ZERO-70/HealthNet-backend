package com.server.HealthNet.Repository;

import com.server.HealthNet.Model.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public class AppointmentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Map ResultSet to Appointment object
    private Appointment mapRowToAppointment(ResultSet rs, int rowNum) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setAppointment_id(rs.getLong("appointment_id"));
        appointment.setPatient_id(rs.getLong("patient_id"));
        appointment.setDoctor_id(rs.getLong("doctor_id"));
        appointment.setDate(rs.getObject("date", LocalDate.class));
        appointment.setStartTime(rs.getObject("start_time", LocalTime.class)); // Updated
        appointment.setEndTime(rs.getObject("end_time", LocalTime.class));     // New
        appointment.setIs_pending(rs.getBoolean("is_pending"));
        appointment.setIs_approved(rs.getBoolean("is_approved"));
        return appointment;
    }

    // Retrieve all appointments
    public List<Appointment> findAll() {
        String sql = "SELECT * FROM appointments";
        return jdbcTemplate.query(sql, this::mapRowToAppointment);
    }

    // Find an appointment by ID
    public Appointment findById(Long id) {
        String sql = "SELECT * FROM appointments WHERE appointment_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToAppointment, id);
    }

    // Save a new appointment
    public int save(Appointment appointment) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, date, start_time, end_time, is_pending, is_approved) VALUES (?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, 
                appointment.getPatient_id(), 
                appointment.getDoctor_id(),
                appointment.getDate(), 
                appointment.getStartTime(), 
                appointment.getEndTime(), 
                appointment.isIs_pending(), 
                appointment.isIs_approved());
    }

    // Update an existing appointment
    public int update(Appointment appointment) {
        String sql = "UPDATE appointments SET patient_id = ?, doctor_id = ?, date = ?, start_time = ?, end_time = ?, is_pending = ?, is_approved = ? WHERE appointment_id = ?";
        return jdbcTemplate.update(sql, 
                appointment.getPatient_id(), 
                appointment.getDoctor_id(),
                appointment.getDate(), 
                appointment.getStartTime(), 
                appointment.getEndTime(), 
                appointment.isIs_pending(), 
                appointment.isIs_approved(), 
                appointment.getAppointment_id());
    }

    // Delete an appointment by ID
    public int deleteById(Long id) {
        String sql = "DELETE FROM appointments WHERE appointment_id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // Find all appointments by patient ID
    public List<Appointment> findAllByPatientId(Long patientId) {
        String sql = "SELECT * FROM appointments WHERE patient_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToAppointment, patientId);
    }

    // Find all appointments by doctor ID
    public List<Appointment> findAllByDoctorId(Long doctorId) {
        String sql = "SELECT * FROM appointments WHERE doctor_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToAppointment, doctorId);
    }

    public boolean isAppointmentOverlapping(Long doctorId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        String sql = "SELECT COUNT(*) FROM appointments " +
                     "WHERE doctor_id = ? AND date = ? AND is_pending = TRUE AND " +
                     "((start_time < ? AND end_time > ?) OR " +   // Overlapping
                     "(start_time >= ? AND start_time < ?) OR " + // Starts within
                     "(end_time > ? AND end_time <= ?))";         // Ends within
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, 
                doctorId, 
                date, 
                startTime, endTime, 
                startTime, endTime, 
                startTime, endTime);
        return count != null && count > 0; // Returns true if any pending appointment overlaps
    }
}
