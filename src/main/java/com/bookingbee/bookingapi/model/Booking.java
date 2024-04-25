package com.bookingbee.bookingapi.model;

import com.google.cloud.Timestamp;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Data
public class Booking {
    private String id;
    private String bookingDetails;
    private String bookingName;
    private String bookingArea;
    private Timestamp bookingDate;
    private Timestamp fromDate;
    private Timestamp toDate;
    private String userId;
    private String imageUrl;
    private String bookingPrice;
    private String category;

    public Booking() {}


    public Booking(String bookingName, String id, String bookingDetails, String userId, String bookingArea, Timestamp bookingDate, String imageUrl, String bookingPrice, Timestamp fromDate, Timestamp toDate, String category){
        this.bookingName = bookingName;
        this.id = id;
        this.bookingDetails = bookingDetails;
        this.userId = userId;
        this.bookingArea = bookingArea;
        this.bookingDate = bookingDate;
        this.imageUrl = imageUrl;
        this.bookingPrice = bookingPrice;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.category = category;
    }
}
