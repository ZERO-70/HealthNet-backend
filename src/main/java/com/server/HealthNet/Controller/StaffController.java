package com.server.HealthNet.Controller;

import com.server.HealthNet.Model.Doctor;
import com.server.HealthNet.Model.Role;
import com.server.HealthNet.Model.Staff;
import com.server.HealthNet.Model.UserAuthentication;
import com.server.HealthNet.Service.StaffService;
import com.server.HealthNet.Service.UserAuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/staff")
@CrossOrigin(origins = "*")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<Staff> getStaffById(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);
        if (userAuthentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Updated Long comparison
        if (!userAuthentication.getPersonId().equals(id) && userAuthentication.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<Staff> staff = staffService.getStaffById(id);
        return staff.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/getmine")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Staff> getCurrentDoctor() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);
        if (userAuthentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Staff> staff = staffService.getStaffById(userAuthentication.getPersonId());
        return staff.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // i think , a staff should not be able to see other staffs , will update in
    // future
    @GetMapping
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<List<Staff>> getAllStaff() {
        List<Staff> staffList = staffService.getAllStaff();
        return new ResponseEntity<>(staffList, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> addStaff(@RequestBody Staff staff) {
        Long result = staffService.addStaff(staff);
        if (result > 0) {
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(-1L, HttpStatus.CREATED);
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<String> updateStaff(@RequestBody Staff staff) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);
        // Updated String comparison
        if (!userAuthentication.getPersonId().equals(staff.getId())
                && !userAuthentication.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        int rowsAffected = staffService.updateStaff(staff);
        if (rowsAffected > 0) {
            return new ResponseEntity<>("Staff updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Staff update failed", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<String> deleteStaffById(@PathVariable Long id) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);
        if (userAuthentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Updated Long comparison
        if (!userAuthentication.getPersonId().equals(id) && userAuthentication.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        int rowsAffected = staffService.deleteStaffById(id);
        if (rowsAffected > 0) {
            return new ResponseEntity<>("Staff deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Staff deletion failed", HttpStatus.NOT_FOUND);
        }
    }
}
