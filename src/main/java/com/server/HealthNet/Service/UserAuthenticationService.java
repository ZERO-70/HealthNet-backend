package com.server.HealthNet.Service;

import com.server.HealthNet.Model.Role;
import com.server.HealthNet.Model.Subscription;
import com.server.HealthNet.Model.UserAuthentication;
import com.server.HealthNet.Repository.UserAuthenticationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAuthenticationService {

    private final UserAuthenticationRepository userAuthenticationRepository;

    private BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(6);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTservice jwTservice;

    public UserAuthenticationService(UserAuthenticationRepository userAuthenticationRepository) {
        this.userAuthenticationRepository = userAuthenticationRepository;
    }

    public int createUser(UserAuthentication userAuthentication) {
        // Log the complete request for debugging
        System.out.println("=== CREATE USER REQUEST ===");
        System.out.println("Username: " + userAuthentication.getUsername());
        System.out.println("Password: " + (userAuthentication.getPassword() != null ? "[PROVIDED]" : "[NULL]"));
        System.out.println("Role: " + userAuthentication.getRole());
        System.out.println("Person ID: " + userAuthentication.getPersonId());
        System.out.println("Subscription: " + userAuthentication.getSubscription());
        System.out.println("Full Object: " + userAuthentication.toString());
        System.out.println("=========================");
        
        // Check if username already exists
        if (doesUsernameExist(userAuthentication.getUsername())) {
            System.out.println("ERROR: Username already exists - " + userAuthentication.getUsername());
            return -1; // Return -1 to indicate username already exists
        }

        // Encode the password
        userAuthentication.setPassword(bcrypt.encode(userAuthentication.getPassword()));

        // Set default subscription based on role
        if (userAuthentication.getSubscription() == null) {
            if (userAuthentication.getRole() == Role.ADMIN || userAuthentication.getRole() == Role.STAFF) {
                userAuthentication.setSubscription(Subscription.PLUS);
            } else {
                userAuthentication.setSubscription(Subscription.DEFAULT);
            }
        } else if (userAuthentication.getRole() == Role.ADMIN || userAuthentication.getRole() == Role.STAFF) {
            // Ensure ADMIN and STAFF always have PLUS subscription
            userAuthentication.setSubscription(Subscription.PLUS);
        }

        return userAuthenticationRepository.save(userAuthentication);
    }

    public UserAuthentication getUserByUsername(String username) {
        return userAuthenticationRepository.findByUsername(username);
    }

    public List<UserAuthentication> getAllUsers() {
        return userAuthenticationRepository.findAll();
    }

    public int updateUser(UserAuthentication userAuthentication) {
        userAuthentication.setPassword(bcrypt.encode(userAuthentication.getPassword()));
        return userAuthenticationRepository.update(userAuthentication);
    }

    public int updateUserSubscription(String username, Subscription subscription) {
        return userAuthenticationRepository.updateSubscription(username, subscription.name());
    }

    public int deleteUser(String username) {
        return userAuthenticationRepository.deleteByUsername(username);
    }

    public int deleteUserbyID(Long id) {
        return userAuthenticationRepository.deleteByID(id);
    }

    public String verify(UserAuthentication userAuthentication) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userAuthentication.getUsername(), userAuthentication.getPassword()));

        if (!authentication.isAuthenticated()) {
            return "Not Authenticated";
        }

        // The role is read from the stored account, not from the login request:
        // a client-supplied role would be untrustworthy, and omitting it used to
        // throw a NullPointerException here.
        UserAuthentication stored = userAuthenticationRepository.findByUsername(userAuthentication.getUsername());
        String role = (stored != null && stored.getRole() != null) ? stored.getRole().toString() : "PATIENT";

        return jwTservice.generateToken(userAuthentication.getUsername(), role);
    }

    public boolean doesUsernameExist(String username) {
        UserAuthentication user = userAuthenticationRepository.findByUsername(username);
        return user != null;
    }

    public String extractUsernameFromToken(String token) {
        return jwTservice.extractusername(token);
    }
}
