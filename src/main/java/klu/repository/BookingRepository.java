package klu.repository;

import klu.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Custom query to find bookings by user email
    List<Booking> findByUserEmail(String userEmail);

    // You might add queries later to check for overlapping bookings for a specific car
    // e.g., findByCarIdAndEndDateGreaterThanEqualAndStartDateLessThanEqual
} 