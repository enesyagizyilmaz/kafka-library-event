package com.microservices.springkafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservices.springkafka.domain.LibraryEvent;
import com.microservices.springkafka.domain.LibraryEventType;
import com.microservices.springkafka.producer.LibraryEventsProducer;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
public class LibraryEventsController {

    private static final Logger log = LoggerFactory.getLogger(LibraryEventsController.class);

    private final LibraryEventsProducer libraryEventsProducer;

    public LibraryEventsController(LibraryEventsProducer libraryEventsProducer) {
        this.libraryEventsProducer = libraryEventsProducer;
    }

    /**
     * Handles HTTP POST requests to create a new library event.
     *
     * @param libraryEvent The library event to be created. Must be of type NEW.
     * @return A ResponseEntity with the created event or a BAD_REQUEST if the event type is invalid.
     * @throws JsonProcessingException If there is an error processing the JSON.
     * @throws ExecutionException If an error occurs during message sending.
     * @throws InterruptedException If the thread is interrupted while sending.
     * @throws TimeoutException If the request times out while sending the message.
     */
    @PostMapping("/v1/libraryevent")
    public ResponseEntity<Object> postLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        if (LibraryEventType.NEW != libraryEvent.libraryEventType()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only NEW event type is supported");
        }
        //invoke kafka producer
        log.info("libraryEvent: {}", libraryEvent);

        //libraryEventProducer.sendLibraryEvent(libraryEvent);
        //libraryEventsProducer.sendLibraryEvent_approach2(libraryEvent);
        libraryEventsProducer.sendLibraryEvent_approach3(libraryEvent);

        log.info("After sending library event: {}", libraryEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    /**
     * Handles HTTP PUT requests to update an existing library event.
     *
     * @param libraryEvent The library event to be updated. Must have a valid ID and be of type UPDATE.
     * @return A ResponseEntity with the updated event or a BAD_REQUEST if the validation fails.
     * @throws JsonProcessingException If there is an error processing the JSON.
     * @throws ExecutionException If an error occurs during message sending.
     * @throws InterruptedException If the thread is interrupted while sending.
     * @throws TimeoutException If the request times out while sending the message.
     */
    @PutMapping("/v1/libraryevent")
    public ResponseEntity<Object> updateLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        log.info("libraryEvent: {}", libraryEvent);

        ResponseEntity<Object> BAD_REQUEST = validateLibraryEvent(libraryEvent);
        if (BAD_REQUEST != null) return BAD_REQUEST;

        libraryEventsProducer.sendLibraryEvent_approach3(libraryEvent);

        log.info("After sending library event: {}", libraryEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    /**
     * Validates the given library event for an update operation.
     *
     * @param libraryEvent The library event to be validated.
     * @return A ResponseEntity with an error message if validation fails, or null if valid.
     */
    private static ResponseEntity<Object> validateLibraryEvent(LibraryEvent libraryEvent) {
        if (libraryEvent.libraryEventId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("libraryEventId is required");
        }
        if (libraryEvent.libraryEventType() != LibraryEventType.UPDATE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only UPDATE event type is supported");
        }
        return null;
    }
}
