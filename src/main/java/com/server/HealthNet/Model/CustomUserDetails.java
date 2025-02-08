package com.server.HealthNet.Model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final UserAuthentication userAuthentication;

    public CustomUserDetails(UserAuthentication userAuthentication) {
        this.userAuthentication = userAuthentication;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Converting the Role into GrantedAuthority for Spring Security
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userAuthentication.getRole().name()));
    }

    @Override
    public String getPassword() {
        return userAuthentication.getPassword();
    }

    @Override
    public String getUsername() {
        return userAuthentication.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getPersonId() {
        return userAuthentication.getPersonId();
    }
}
