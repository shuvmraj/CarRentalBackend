package klu.service;

import klu.model.Booking;
import klu.model.Car;
import klu.model.Users;
import klu.repository.BookingRepository;
import klu.repository.CarRepository;
import klu.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private UsersRepository usersRepository;

    @Transactional // Make this method transactional
    public String createBooking(Long carId, String userEmail, LocalDate startDate, LocalDate endDate) {
        log.info("Attempting to create booking for carId: {}, userEmail: {}, startDate: {}, endDate: {}", 
                 carId, userEmail, startDate, endDate);

        // Validate dates
        if (startDate == null || endDate == null || startDate.isAfter(endDate) || startDate.isBefore(LocalDate.now())) {
            log.warn("Invalid booking dates provided.");
            return "400::Invalid booking dates provided.";
        }

        // Find car and user
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + carId));
        Users user = usersRepository.findById(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // Check car availability
        if (!car.isAvailabilityStatus()) {
            log.warn("Car {} is not available for booking.", carId);
            return "409::Car not available for the selected dates."; // 409 Conflict
        }

        // TODO: Add check for overlapping bookings for the same car in the future

        // Calculate price (Billing - simplified)
        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate) + 1; // Inclusive
        if (numberOfDays <= 0) numberOfDays = 1; // Minimum 1 day rental
        double totalPrice = numberOfDays * car.getRentalPricePerDay();
        log.info("Calculated booking duration: {} days, Total price: {}", numberOfDays, totalPrice);

        // Create and save booking
        Booking booking = new Booking();
        booking.setCar(car);
        booking.setUser(user);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setTotalPrice(totalPrice);
        booking.setBookingStatus("CONFIRMED"); // Initial status

        bookingRepository.save(booking);
        log.info("Booking saved with ID: {}", booking.getId());

        // Update car availability
        car.setAvailabilityStatus(false);
        carRepository.save(car);
        log.info("Car {} availability set to false.", carId);

        return "200::Booking confirmed successfully! Booking ID: " + booking.getId() + ", Total Price: " + totalPrice;
    }

    public List<Booking> getUserBookings(String userEmail) {
        log.info("Fetching bookings for user: {}", userEmail);
        return bookingRepository.findByUserEmail(userEmail);
    }

    public Booking getBookingById(Long bookingId) {
        log.info("Fetching booking by ID: {}", bookingId);
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));
    }

    @Transactional
    public String cancelBooking(Long bookingId, String userEmail) {
        log.info("Attempting to cancel booking ID: {} for user: {}", bookingId, userEmail);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        // Security check: Ensure the user cancelling is the one who made the booking
        if (!booking.getUser().getEmail().equals(userEmail)) {
            log.warn("User {} attempted to cancel booking {} which belongs to {}", 
                     userEmail, bookingId, booking.getUser().getEmail());
            return "403::Forbidden - You can only cancel your own bookings.";
        }

        if (!"CONFIRMED".equalsIgnoreCase(booking.getBookingStatus())) {
             log.warn("Booking {} cannot be cancelled as its status is {}", bookingId, booking.getBookingStatus());
            return "400::Booking cannot be cancelled (status: " + booking.getBookingStatus() + ").";
        }

        // Update booking status
        booking.setBookingStatus("CANCELLED");
        bookingRepository.save(booking);
        log.info("Booking {} status set to CANCELLED.", bookingId);

        // Make car available again
        Car car = booking.getCar();
        car.setAvailabilityStatus(true);
        carRepository.save(car);
        log.info("Car {} availability set to true.", car.getId());

        return "200::Booking successfully cancelled.";
    }
} 