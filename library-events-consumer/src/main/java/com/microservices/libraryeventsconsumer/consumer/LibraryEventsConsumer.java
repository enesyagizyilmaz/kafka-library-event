package com.microservices.libraryeventsconsumer.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LibraryEventsConsumer {
    private static final Logger log = LoggerFactory.getLogger(LibraryEventsConsumer.class);

    @KafkaListener(topics = {"library-events"}, groupId = "library-events-producer-group")
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) {
        log.info("ConsumerRecord: {}", consumerRecord);
    }
}
