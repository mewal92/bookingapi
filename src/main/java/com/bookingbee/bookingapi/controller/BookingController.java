package com.bookingbee.bookingapi.controller;

import com.bookingbee.bookingapi.model.Booking;
import com.bookingbee.bookingapi.model.PubSubMessages;
import com.bookingbee.bookingapi.service.BookingService;
import com.bookingbee.bookingapi.service.PubSubPublisher;
import com.google.cloud.pubsub.v1.Publisher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.google.cloud.storage.spi.v1.StorageRpc.Option.PROJECT_ID;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;


    @PostMapping("/create")
    public ResponseEntity<String> createBooking(@RequestBody Booking booking) throws Exception {
        String result = bookingService.createBooking(booking);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/all")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable String id) {
        Booking booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateBooking(@PathVariable String id, @RequestBody Booking bookingDetails) {
        String updatedBooking = bookingService.updateBooking(id, bookingDetails);
        return ResponseEntity.ok(updatedBooking);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getBookingsByUserId(@PathVariable String userId) throws Exception {
        List<Map<String, Object>> bookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }


    @PostMapping("/book-event")
    public ResponseEntity<?> bookEvent(@RequestBody Map<String, String> payload) {
        try {
            String id = payload.get("id");
            String userId = payload.get("userId");
            String userEmail = payload.get("userEmail");
            String startDateString = payload.get("startDate");
            String endDateString = payload.get("endDate");
            String bookingName = payload.get("bookingName");
            String bookingDetails = payload.get("bookingDetails");

            bookingService.publishBookingConfirmation(userEmail,bookingName, bookingDetails, startDateString);

            String result = bookingService.bookEvent(id, userId, startDateString, endDateString);
            System.out.println("Received booking payload: email=" + userEmail + " id=" + id + ", userId=" + userId + ", startDate=" + startDateString + ", endDate=" + endDateString);

            return ResponseEntity.ok().body(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to book event");
        }
    }


    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable String id) {
        try {
            bookingService.cancelBooking(id);
            return ResponseEntity.ok().body("Booking cancelled successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to cancel booking");
        }
    }

}