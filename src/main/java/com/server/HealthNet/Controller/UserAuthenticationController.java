package com.server.HealthNet.Controller;

import com.server.HealthNet.Model.Role;
import com.server.HealthNet.Model.UserAuthentication;
import com.server.HealthNet.Service.UserAuthenticationService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user_authentication")
@CrossOrigin(origins = "*")
public class UserAuthenticationController {

    private final UserAuthenticationService userAuthenticationService;

    public UserAuthenticationController(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> createUser(@RequestBody UserAuthentication userAuthentication) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            UserAuthentication rAuthentication = userAuthenticationService.getUserByUsername(username);

            if (userAuthentication.getRole() == Role.STAFF) {
                if (rAuthentication == null || rAuthentication.getRole() != Role.ADMIN) {
                    return ResponseEntity.status(403).build();
                }
            }
        }

        userAuthenticationService.createUser(userAuthentication);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserAuthentication> getUserByUsername(@PathVariable String username) {
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);
        return userAuthentication != null ? ResponseEntity.ok(userAuthentication) : ResponseEntity.notFound().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserAuthentication> getAllUsers() {
        return userAuthenticationService.getAllUsers();
    }

    @PutMapping("/{username}")
    public ResponseEntity<Void> updateUser(@PathVariable String username,
            @RequestBody UserAuthentication userAuthentication) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication rAuthentication = userAuthenticationService.getUserByUsername(name);

        if (name != username && rAuthentication.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).build();
        }
        userAuthentication.setUsername(username);
        userAuthenticationService.updateUser(userAuthentication);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication rAuthentication = userAuthenticationService.getUserByUsername(name);

        if (!name.equals(username) && rAuthentication.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).build();
        }

        if (username.equals(name) && rAuthentication.getRole() == Role.ADMIN) {
            return ResponseEntity.status(403).build();
        }

        userAuthenticationService.deleteUser(username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/personwith/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication rAuthentication = userAuthenticationService.getUserByUsername(name);

        if (id != rAuthentication.getPersonId() && rAuthentication.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).build();
        }
        if (id == rAuthentication.getPersonId() && rAuthentication.getRole() == Role.ADMIN) {
            return ResponseEntity.status(403).build();
        }

        userAuthenticationService.deleteUserbyID(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public String login(@RequestBody UserAuthentication userAuthentication) {
        System.out.println("ACESSSSED THEEEEEEEE LOGINNNNNNNNNNNNNNN");
        System.out.println();
        System.out.println();
        String token = userAuthenticationService.verify(userAuthentication);
        System.out.println("+++" + token);
        return token;
    }

    @GetMapping("/exists/{username}")
    public ResponseEntity<Boolean> doesUsernameExist(@PathVariable String username) {
        boolean exists = userAuthenticationService.doesUsernameExist(username);
        return ResponseEntity.ok(exists);
    }
}
