package klu.controller;

import klu.model.Booking;
import klu.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map; // For request body

@RestController
@CrossOrigin(origins = "*") // Adjust CORS as needed
@RequestMapping("/bookings")
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingService bookingService;

    // Inner class for the booking request payload
    public static class BookingRequest {
        public Long carId;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        public LocalDate startDate;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        public LocalDate endDate;
    }

    @PostMapping("/user/request")
    public ResponseEntity<String> requestBooking(@RequestBody BookingRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
             log.warn("Booking request received without proper authentication.");
            return ResponseEntity.status(401).body("401::Authentication required.");
        }
        String userEmail = authentication.getName(); // Get email from authenticated principal
        log.info("Received booking request from user: {} for carId: {}", userEmail, request.carId);

        try {
            String result = bookingService.createBooking(request.carId, userEmail, request.startDate, request.endDate);
            if (result.startsWith("200::")) {
                return ResponseEntity.ok(result);
            } else {
                // Determine status code based on error message prefix
                int statusCode = 400; // Default Bad Request
                if (result.contains("409::")) statusCode = 409; // Conflict
                if (result.contains("404::")) statusCode = 404; // Not Found
                return ResponseEntity.status(statusCode).body(result);
            }
        } catch (RuntimeException e) {
            log.error("Error creating booking for user {}: {}", userEmail, e.getMessage());
            return ResponseEntity.status(500).body("500::Failed to create booking: " + e.getMessage());
        }
    }

    @GetMapping("/user/mybookings")
    public ResponseEntity<?> getCurrentUserBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).body("401::Authentication required.");
        }
        String userEmail = authentication.getName();
        log.info("Fetching bookings for current user: {}", userEmail);
        try {
            List<Booking> bookings = bookingService.getUserBookings(userEmail);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            log.error("Error fetching bookings for user {}: {}", userEmail, e.getMessage());
            return ResponseEntity.status(500).body("500::Failed to fetch bookings");
        }
    }

     // Endpoint for an admin to potentially get bookings for any user (example)
    @GetMapping("/admin/user/{userEmail}") 
    public ResponseEntity<?> getUserBookingsByAdmin(@PathVariable String userEmail) {
        log.info("Admin fetching bookings for user: {}", userEmail);
        try {
             List<Booking> bookings = bookingService.getUserBookings(userEmail);
             return ResponseEntity.ok(bookings);
        } catch (Exception e) {
             log.error("Error fetching bookings for user {}: {}", userEmail, e.getMessage());
            return ResponseEntity.status(500).body("500::Failed to fetch bookings");
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingDetails(@PathVariable Long bookingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).body("401::Authentication required.");
        }
        String userEmail = authentication.getName();
        log.info("User {} fetching details for booking ID: {}", userEmail, bookingId);
        try {
            Booking booking = bookingService.getBookingById(bookingId);
            // Optional: Add check if user is ADMIN or owns the booking
            // if (!booking.getUser().getEmail().equals(userEmail) && !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            //     return ResponseEntity.status(403).body("403::Forbidden");
            // }
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
             log.error("Error fetching booking {}: {}", bookingId, e.getMessage());
            return ResponseEntity.status(404).body("404::Booking not found.");
        } catch (Exception e) {
            log.error("Error fetching booking {}: {}", bookingId, e.getMessage());
            return ResponseEntity.status(500).body("500::Failed to fetch booking");
        }
    }

    @PutMapping("/user/cancel/{bookingId}")
    public ResponseEntity<String> cancelUserBooking(@PathVariable Long bookingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
             return ResponseEntity.status(401).body("401::Authentication required.");
        }
        String userEmail = authentication.getName();
        log.info("User {} attempting to cancel booking ID: {}", userEmail, bookingId);
        try {
            String result = bookingService.cancelBooking(bookingId, userEmail);
             if (result.startsWith("200::")) {
                return ResponseEntity.ok(result);
            } else {
                int statusCode = 400; // Default Bad Request
                if (result.contains("403::")) statusCode = 403; // Forbidden
                if (result.contains("404::")) statusCode = 404; // Not Found
                return ResponseEntity.status(statusCode).body(result);
            }
        } catch (RuntimeException e) {
            log.error("Error cancelling booking {} for user {}: {}", bookingId, userEmail, e.getMessage());
            return ResponseEntity.status(500).body("500::Failed to cancel booking: " + e.getMessage());
        }
    }
} 