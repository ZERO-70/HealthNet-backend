package com.server.HealthNet.Service;

import com.server.HealthNet.Model.UserAuthentication;
import com.server.HealthNet.Repository.UserAuthenticationRepository;
import com.server.HealthNet.Model.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAuthenticationRepository userAuthenticationRepository;

    public CustomUserDetailsService(UserAuthenticationRepository userAuthenticationRepository) {
        this.userAuthenticationRepository = userAuthenticationRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAuthentication userAuthentication = userAuthenticationRepository.findByUsername(username);
        if (userAuthentication == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new CustomUserDetails(userAuthentication);
    }
}
