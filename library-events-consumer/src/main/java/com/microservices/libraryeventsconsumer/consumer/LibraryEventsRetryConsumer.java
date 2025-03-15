package com.microservices.libraryeventsconsumer.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservices.libraryeventsconsumer.service.LibraryEventsService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LibraryEventsRetryConsumer {
    @Autowired
    LibraryEventsService libraryEventsService;

    private static final Logger log = LoggerFactory.getLogger(LibraryEventsRetryConsumer.class);

    @KafkaListener(topics = {"${topics.retry}"},
            groupId = "retry-listener-group",
            autoStartup = "${retryListener.startup:true}")
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        log.info("ConsumerRecord in Retry Consumer: {}", consumerRecord);
        consumerRecord.headers()
                        .forEach(header -> {
                            log.info("Key: {}, value: {}", header.key(), new String(header.value()));
                        });
        libraryEventsService.processLibraryEvent(consumerRecord);
    }
}
