package com.bookingbee.bookingapi.service;

import com.bookingbee.bookingapi.model.Booking;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firestore.v1.Write;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;

@Service
public class BookingService {

    FirestoreOptions firestoreOptions =
            FirestoreOptions.getDefaultInstance().toBuilder()
                    .setProjectId("interns-melinda")
                    .build();
    Firestore db = firestoreOptions.getService();

    public BookingService() {
    }

    public String createBooking(Booking booking) throws Exception {
        DocumentReference addedDocRef = db.collection("bookings").add(booking).get();
        String id = addedDocRef.getId();
        booking.setId(id);

        db.collection("bookings").document(id).set(booking);

        return "Event was successfully created: " + id;
    }


    public List<Booking> getAllBookings() {
        ApiFuture<QuerySnapshot> future = db.collection("bookings").get();
        List<Booking> bookings = new ArrayList<>();
        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                bookings.add(document.toObject(Booking.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("There was an error while retrieving bookings " + e.getMessage());
        }
        return bookings;
    }


    public Booking getBookingById(String id) {
        DocumentReference docRef = db.collection("bookings").document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        Booking booking = null;
        try {
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                booking = document.toObject(Booking.class);
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Something went wrong: " + e.getMessage());
        }
        return booking;
    }


    public String updateBooking(String id, Booking updatedBooking) {
        DocumentReference docRef = db.collection("bookings").document(id);
        ApiFuture<WriteResult> future = docRef.set(updatedBooking, SetOptions.merge());
        try {
            future.get();
            return "Booking updated successfully.";
        } catch (InterruptedException | ExecutionException e) {
            return "Update failed: " + e.getMessage();
        }
    }


    public String bookEvent(String id, String userEmail, String userId, String startDateString, String endDateString) throws Exception {
        DocumentReference docRef = db.collection("bookings").document(id);
        DocumentSnapshot bookingSnapshot = docRef.get().get();

        if (!bookingSnapshot.exists()) {
            return "Event does not exist.";
        }

        DocumentReference userDocRef = db.collection("users").document(userId);


        Map<String, Object> bookingData = bookingSnapshot.getData();
        if (bookingData.containsKey("userId") && bookingData.get("userId") != null) {
            return "Event is already booked.";
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("userId", userId);

        if (startDateString != null && !startDateString.isEmpty() && endDateString != null && !endDateString.isEmpty()) {
            Timestamp startDate = convertStringToTimestamp(startDateString);
            Timestamp endDate = convertStringToTimestamp(endDateString);

            if (startDate != null && endDate != null) {
                updates.put("fromDate", startDate);
                updates.put("toDate", endDate);
            }
        }

        ApiFuture<WriteResult> writeResult = docRef.update(updates);
        System.out.println("Update time: " + writeResult.get().getUpdateTime());

        publishBookingConfirmation(userEmail, userId, id);

        return "Booking updated successfully with user ID: " + userId;
    }

    public void publishBookingConfirmation(String email, String userId, String bookingId) {
        String projectId = "interns-melinda";
        String topicId = "booking-confirmation";
        String messageJson = String.format("{\"email\": \"%s\", \"userId\": \"%s\", \"bookingId\": \"%s\"}", email, userId, bookingId);

        try {
            new PubSubPublisher().publishMessage(projectId, topicId, email, messageJson);
        } catch (IOException e) {
            System.err.println("Error when trying to publish booking confirmation: " + e.getMessage());
        }
    }


    private Timestamp convertStringToTimestamp(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date parsedDate = dateFormat.parse(dateString);
            return Timestamp.of(parsedDate);
        } catch (ParseException e) {
            System.err.println("Error parsing date string: " + e.getMessage());
            return null;
        }
    }


    public List<Map<String, Object>> getBookingsByUserId(String userId) throws Exception {
        List<Map<String, Object>> userBookings = new ArrayList<>();

        ApiFuture<QuerySnapshot> future = db.collection("bookings")
                .whereEqualTo("userId", userId)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            Map<String, Object> bookingData = document.getData();
            userBookings.add(bookingData);
        }

        return userBookings;
    }

    public void cancelBooking(String id) throws Exception {
        DocumentReference docRef = db.collection("bookings").document(id);
        docRef.update("userId", null);
        docRef.update("fromDate", null);
        docRef.update("toDate", null);
        }
    }