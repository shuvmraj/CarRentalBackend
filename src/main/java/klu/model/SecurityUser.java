package klu.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class SecurityUser implements UserDetails {

    private final Users user;

    public SecurityUser(Users user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return the user's role as a GrantedAuthority
        // Spring Security expects roles prefixed with "ROLE_", but we use hasAuthority(), 
        // so no prefix is strictly needed here unless we switch to hasRole().
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole())); 
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // Use email as the username
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Assuming accounts don't expire
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Assuming accounts don't lock
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Assuming credentials don't expire
    }

    @Override
    public boolean isEnabled() {
        return true; // Assuming accounts are always enabled
    }
} 