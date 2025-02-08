package com.server.HealthNet.Service;

import com.server.HealthNet.Model.Appointment;
import com.server.HealthNet.Model.Avalibility;
import com.server.HealthNet.Repository.AppointmentRepository;
import com.server.HealthNet.Repository.AvalibilityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired 
    AvalibilityRepository avalibilityRepository;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public int createAppointment(Appointment appointment) {
    long durationMinutes = java.time.Duration.between(appointment.getStartTime(), appointment.getEndTime()).toMinutes();
    if (durationMinutes < 5 || durationMinutes > 120) {
        return 3; 
    }

    if (appointmentRepository.isAppointmentOverlapping(
            appointment.getDoctor_id(), 
            appointment.getDate(), 
            appointment.getStartTime(), 
            appointment.getEndTime())) {
        return 2; 
    }

    Optional<Avalibility> avalibilityOpt = avalibilityRepository.findById(appointment.getDoctor_id());
    if (avalibilityOpt.isEmpty()) {
    }

    Avalibility avalibility = avalibilityOpt.get();

    boolean isWithinAvailability = switch (appointment.getDate().getDayOfWeek()) {
        case MONDAY -> isWithinTimeRange(appointment.getStartTime(), appointment.getEndTime(), 
                                         avalibility.getMon_startTime(), avalibility.getMon_endTime());
        case TUESDAY -> isWithinTimeRange(appointment.getStartTime(), appointment.getEndTime(), 
                                          avalibility.getTue_startTime(), avalibility.getTue_endTime());
        case WEDNESDAY -> isWithinTimeRange(appointment.getStartTime(), appointment.getEndTime(), 
                                            avalibility.getWed_startTime(), avalibility.getWed_endTime());
        case THURSDAY -> isWithinTimeRange(appointment.getStartTime(), appointment.getEndTime(), 
                                           avalibility.getThu_startTime(), avalibility.getThu_endTime());
        case FRIDAY -> isWithinTimeRange(appointment.getStartTime(), appointment.getEndTime(), 
                                         avalibility.getFri_startTime(), avalibility.getFri_endTime());
        case SATURDAY -> isWithinTimeRange(appointment.getStartTime(), appointment.getEndTime(), 
                                           avalibility.getSat_startTime(), avalibility.getSat_endTime());
        case SUNDAY -> isWithinTimeRange(appointment.getStartTime(), appointment.getEndTime(), 
                                         avalibility.getSun_startTime(), avalibility.getSun_endTime());
        default -> false;
    };

    if (!isWithinAvailability) {
        return 4; // Appointment falls outside the doctor's available hours
    }

    // Set default approval status and save the appointment
    appointment.setIs_approved(false);
    return appointmentRepository.save(appointment);
    }

    // Helper method to check if a time range falls within a given availability
    private boolean isWithinTimeRange(LocalTime appointmentStart, LocalTime appointmentEnd, 
                                   LocalTime availableStart, LocalTime availableEnd) {
    if (availableStart == null || availableEnd == null) {
        return false; // No availability defined for the day
    }
    return !appointmentStart.isBefore(availableStart) && !appointmentEnd.isAfter(availableEnd);
    }

    

    public int updateAppointment(Appointment appointment) {
        return appointmentRepository.update(appointment);
    }

    public int deleteAppointment(Long id) {
        return appointmentRepository.deleteById(id);
    }

    public List<Appointment> getallbydoctorid(Long id) {
        return appointmentRepository.findAllByDoctorId(id);
    }

    public List<Appointment> getallbypatientid(Long id) {
        return appointmentRepository.findAllByPatientId(id);
    }

    // New method to approve an appointment
    public int approveAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id);
        if (appointment != null) {
            appointment.setIs_approved(true); // Approve the appointment
            return appointmentRepository.update(appointment);
        }
        return 0; // Return 0 if appointment not found
    }
}
