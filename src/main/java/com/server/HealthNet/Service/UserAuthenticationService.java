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
        // Check if username already exists
        if (doesUsernameExist(userAuthentication.getUsername())) {
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
        System.out.println(
                "Inside verify .... " + userAuthentication.getUsername() + " " + userAuthentication.getPassword());
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userAuthentication.getUsername(), userAuthentication.getPassword()));
        System.out.println("checking fuck .... " + authentication.isAuthenticated());
        return authentication.isAuthenticated()
                ? jwTservice.generateToken(userAuthentication.getUsername(), userAuthentication.getRole().toString())
                : "Not Authenticated";
    }

    public boolean doesUsernameExist(String username) {
        List<String> usernames = userAuthenticationRepository.getAllUsernames();
        return usernames.contains(username);
    }

    public String extractUsernameFromToken(String token) {
        return jwTservice.extractusername(token);
    }
}
