package klu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

import klu.service.JpaUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JpaUserDetailsService jpaUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JpaUserDetailsService jpaUserDetailsService, JwtAuthenticationFilter jwtAuthFilter) {
        this.jpaUserDetailsService = jpaUserDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/users/signup", "/users/signin", "/users/forgotpassword/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/cars/admin/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/cars/admin/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/cars/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/cars/user/**").hasAnyAuthority("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/bookings/user/request").hasAuthority("USER")
                .requestMatchers(HttpMethod.GET, "/bookings/user/mybookings").hasAuthority("USER")
                .requestMatchers(HttpMethod.PUT, "/bookings/user/cancel/**").hasAuthority("USER")
                .requestMatchers(HttpMethod.GET, "/bookings/admin/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.GET, "/bookings/{bookingId}").hasAnyAuthority("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 