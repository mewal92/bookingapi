package com.bookingbee.bookingapi.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PubSubMessages {
    private String email;
    private String subject;
    private String body;
}
