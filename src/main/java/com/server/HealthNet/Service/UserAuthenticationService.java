package com.server.HealthNet.Service;

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
        userAuthentication.setPassword(bcrypt.encode(userAuthentication.getPassword()));
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

    public int deleteUser(String username) {
        return userAuthenticationRepository.deleteByUsername(username);
    }

    public int deleteUserbyID(Long id) {
        return userAuthenticationRepository.deleteByID(id);
    }

    public String verify(UserAuthentication userAuthentication) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userAuthentication.getUsername(), userAuthentication.getPassword()));
        return authentication.isAuthenticated()
                ? jwTservice.generateToken(userAuthentication.getUsername(), userAuthentication.getRole().toString())
                : "Not Authenticated";
    }

    public boolean doesUsernameExist(String username) {
        List<String> usernames = userAuthenticationRepository.getAllUsernames();
        return usernames.contains(username);
    }
}
