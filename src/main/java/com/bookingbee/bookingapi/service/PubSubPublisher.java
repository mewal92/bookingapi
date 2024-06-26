package com.bookingbee.bookingapi.service;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import com.google.api.core.ApiFuture;

import java.io.IOException;

public class PubSubPublisher {

    public void publishMessage(String projectId, String topicId, String email, String bookingName, String bookingDetails) throws IOException {
        TopicName topicName = TopicName.of(projectId, topicId);
        Publisher publisher = null;
        try {
            publisher = Publisher.newBuilder(topicName).build();

            String messageStr = String.format("Thank you, " + email + " for your booking. Here are your booking details: " + bookingName, bookingDetails);
            ByteString data = ByteString.copyFromUtf8(messageStr);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                    .setData(data)
                    .build();

            ApiFuture<String> future = publisher.publish(pubsubMessage);

            System.out.println("Published message ID: " + future.get());
        } catch (Exception e) {
            System.err.println("Failed to publish: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (publisher != null) {
                publisher.shutdown();
            }
        }
    }
}
