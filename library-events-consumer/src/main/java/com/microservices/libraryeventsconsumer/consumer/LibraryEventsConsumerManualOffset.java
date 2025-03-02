package com.microservices.libraryeventsconsumer.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

//@Component
public class LibraryEventsConsumerManualOffset implements AcknowledgingMessageListener<Integer,String> {
    private static final Logger log = LoggerFactory.getLogger(LibraryEventsConsumerManualOffset.class);

    @Override
    @KafkaListener(topics = {"library-events"}, groupId = "library-events-producer-group")
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord, Acknowledgment acknowledgment) {
        log.info("ConsumerRecord: {}", consumerRecord);
        if (acknowledgment != null) {
            acknowledgment.acknowledge();
        }
    }
}
