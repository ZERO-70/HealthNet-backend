package com.server.HealthNet.Controller;

import com.server.HealthNet.Model.Role;
import com.server.HealthNet.Model.Subscription;
import com.server.HealthNet.Model.SubscriptionResponse;
import com.server.HealthNet.Model.SubscriptionUpdateRequest;
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

    @PutMapping("/subscription")
    public ResponseEntity<Void> updateSubscription(@RequestBody SubscriptionUpdateRequest request) {
        // Verify the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        String username = authentication.getName();
        UserAuthentication userAuth = userAuthenticationService.getUserByUsername(username);
        if (userAuth == null) {
            return ResponseEntity.status(404).build(); // Not found
        }

        // Update the subscription
        try {
            Subscription newSubscription = Subscription.valueOf(request.getSubscription());

            // If user is ADMIN or STAFF, subscription should always be PLUS
            if ((userAuth.getRole() == Role.ADMIN || userAuth.getRole() == Role.STAFF)
                    && newSubscription != Subscription.PLUS) {
                return ResponseEntity.status(400).build(); // Bad request - Admin and Staff must have PLUS
            }

            userAuthenticationService.updateUserSubscription(username, newSubscription);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).build(); // Bad request - invalid subscription type
        }
    }

    @GetMapping("/subscription")
    public ResponseEntity<SubscriptionResponse> getSubscription() {
        // Verify the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        String username = authentication.getName();
        UserAuthentication userAuth = userAuthenticationService.getUserByUsername(username);
        if (userAuth == null) {
            return ResponseEntity.status(404).build(); // Not found
        }

        // Return the subscription
        SubscriptionResponse response = new SubscriptionResponse(userAuth.getSubscription().name());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/subscription/token")
    public ResponseEntity<Void> updateSubscriptionWithToken(@RequestHeader("Authorization") String tokenHeader,
            @RequestBody SubscriptionUpdateRequest request) {
        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        String token = tokenHeader.substring(7); // Remove "Bearer " prefix
        String username = userAuthenticationService.extractUsernameFromToken(token);

        if (username == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        UserAuthentication userAuth = userAuthenticationService.getUserByUsername(username);
        if (userAuth == null) {
            return ResponseEntity.status(404).build(); // Not found
        }

        // Update the subscription
        try {
            Subscription newSubscription = Subscription.valueOf(request.getSubscription());

            // If user is ADMIN or STAFF, subscription should always be PLUS
            if ((userAuth.getRole() == Role.ADMIN || userAuth.getRole() == Role.STAFF)
                    && newSubscription != Subscription.PLUS) {
                return ResponseEntity.status(400).build(); // Bad request - Admin and Staff must have PLUS
            }

            userAuthenticationService.updateUserSubscription(username, newSubscription);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).build(); // Bad request - invalid subscription type
        }
    }

    @GetMapping("/subscription/token")
    public ResponseEntity<SubscriptionResponse> getSubscriptionWithToken(
            @RequestHeader("Authorization") String tokenHeader) {
        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        String token = tokenHeader.substring(7); // Remove "Bearer " prefix
        String username = userAuthenticationService.extractUsernameFromToken(token);

        if (username == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        UserAuthentication userAuth = userAuthenticationService.getUserByUsername(username);
        if (userAuth == null) {
            return ResponseEntity.status(404).build(); // Not found
        }

        // Return the subscription
        SubscriptionResponse response = new SubscriptionResponse(userAuth.getSubscription().name());
        return ResponseEntity.ok(response);
    }
}
