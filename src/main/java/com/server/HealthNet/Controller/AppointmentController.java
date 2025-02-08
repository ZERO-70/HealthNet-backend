package com.server.HealthNet.Controller;

import com.server.HealthNet.Model.Appointment;
import com.server.HealthNet.Model.Role;
import com.server.HealthNet.Model.UserAuthentication;
import com.server.HealthNet.Service.AppointmentService;
import com.server.HealthNet.Service.UserAuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
@CrossOrigin(origins = "*")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @GetMapping
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public Appointment getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<String> createAppointment(@RequestBody Appointment appointment) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);
        if (appointment.getPatient_id() != userAuthentication.getPersonId()) {
            return new ResponseEntity<>("Your ID does not match with requested appointment", HttpStatus.FORBIDDEN);
        }

        int res = appointmentService.createAppointment(appointment);
        if (res == 2) {
            return new ResponseEntity<>("Appointment Time Conflict", HttpStatus.CONFLICT);
        } else if (res == 3) {
            return new ResponseEntity<>("Appointment Time range limit exceeded", HttpStatus.FORBIDDEN);
        } else if (res == 4) {
            return new ResponseEntity<>("Appointment Time conflicts Doctor avalibility Range", HttpStatus.FORBIDDEN);
        }
        return res > 0
                ? new ResponseEntity<>("Appointment Insertion successfully", HttpStatus.OK)
                : new ResponseEntity<>("Appointment Insertion failed", HttpStatus.NOT_FOUND);
    }

    @PutMapping
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public ResponseEntity<String> updateAppointment(@RequestBody Appointment appointment) {
        // Get the logged-in user's details
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);

        // Fetch the current appointment from the database
        Appointment existingAppointment = appointmentService.getAppointmentById(appointment.getAppointment_id());
        if (existingAppointment == null) {
            return new ResponseEntity<>("Appointment not found", HttpStatus.NOT_FOUND);
        }

        // Check if the logged-in user is the patient or the doctor associated with the
        // appointment
        if (!userAuthentication.getPersonId().equals(existingAppointment.getPatient_id()) &&
                !userAuthentication.getPersonId().equals(existingAppointment.getDoctor_id())) {
            return new ResponseEntity<>("You are not authorized to update this appointment", HttpStatus.FORBIDDEN);
        }

        // Proceed with the update
        return appointmentService.updateAppointment(appointment) > 0
                ? new ResponseEntity<>("Appointment Updated successfully", HttpStatus.OK)
                : new ResponseEntity<>("Appointment Update failed", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<String> deleteAppointment(@PathVariable Long id) {
        // Get the logged-in user's details
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);

        // Fetch the appointment from the database
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment == null) {
            return new ResponseEntity<>("Appointment not found", HttpStatus.NOT_FOUND);
        }

        // Check if the logged-in user is authorized to delete the appointment
        if (!userAuthentication.getRole().equals(Role.ADMIN) &&
                !userAuthentication.getRole().equals(Role.STAFF) &&
                !userAuthentication.getPersonId().equals(appointment.getPatient_id())) {
            return new ResponseEntity<>("You are not authorized to delete this appointment", HttpStatus.FORBIDDEN);
        }

        // Proceed with deletion
        return appointmentService.deleteAppointment(id) > 0
                ? new ResponseEntity<>("Appointment Deletion successfully", HttpStatus.OK)
                : new ResponseEntity<>("Appointment Deletion failed", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/getmine")
    public List<Appointment> getmyAppointments() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);

        if (userAuthentication.getRole().equals(Role.DOCTOR)) {
            return appointmentService.getallbydoctorid(userAuthentication.getPersonId());
        }
        if (userAuthentication.getRole().equals(Role.PATIENT)) {
            return appointmentService.getallbypatientid(userAuthentication.getPersonId());
        }
        return null;
    }

    // New endpoint: Approve an appointment
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<String> approveAppointment(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);

        // Ensure only the doctor associated with the appointment can approve it
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment == null) {
            return new ResponseEntity<>("Appointment not found", HttpStatus.NOT_FOUND);
        }
        if (!userAuthentication.getPersonId().equals(appointment.getDoctor_id())) {
            return new ResponseEntity<>("You are not authorized to approve this appointment", HttpStatus.FORBIDDEN);
        }

        int result = appointmentService.approveAppointment(id);
        return result > 0
                ? new ResponseEntity<>("Appointment approved successfully", HttpStatus.OK)
                : new ResponseEntity<>("Failed to approve appointment", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/{id}/mark-not-pending")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<String> markAppointmentNotPending(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);

        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment == null) {
            return new ResponseEntity<>("Appointment not found", HttpStatus.NOT_FOUND);
        }

        if (!userAuthentication.getPersonId().equals(appointment.getDoctor_id())) {
            return new ResponseEntity<>("You are not authorized to modify this appointment", HttpStatus.FORBIDDEN);
        }

        appointment.setIs_pending(false);
        int result = appointmentService.updateAppointment(appointment);

        return result > 0
                ? new ResponseEntity<>("Appointment marked as not pending successfully", HttpStatus.OK)
                : new ResponseEntity<>("Failed to mark appointment as not pending", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
