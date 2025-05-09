package klu.service;

import klu.model.SecurityUser;
import klu.model.Users;
import klu.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(JpaUserDetailsService.class);

    private final UsersRepository usersRepository;

    public JpaUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user by username (email): {}", username);
        // In our case, username is the email
        return usersRepository
                .findById(username) // Find user by email (which is the ID)
                .map(user -> {
                    log.info("User found: {}, Role: {}", username, user.getRole());
                    return new SecurityUser(user);
                })
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new UsernameNotFoundException("Username not found: " + username);
                });
    }
} 