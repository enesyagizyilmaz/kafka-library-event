package com.microservices.springkafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.springkafka.controller.LibraryEventsController;
import com.microservices.springkafka.domain.LibraryEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class LibraryEventsProducer {
    @Value("${spring.kafka.topic}")
    public String topic;

    private static final Logger log = LoggerFactory.getLogger(LibraryEventsController.class);

    private final KafkaTemplate<Integer, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public LibraryEventsProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Sends a LibraryEvent asynchronously to the Kafka topic.
     *
     * @param libraryEvent The event to be sent.
     * @return CompletableFuture representing the result of the send operation.
     * @throws JsonProcessingException if there is an error during serialization.
     */
    public CompletableFuture<SendResult<Integer, String>> sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        var key = libraryEvent.libraryEventId();
        var value = objectMapper.writeValueAsString(libraryEvent);
        var completableFuture = kafkaTemplate.send(topic, key, value);

        //1. blocking call - get metadata about the kafka cluster
        //2. send message happens - returns a CompletableFuture
        return completableFuture
                .whenComplete((sendResult, throwable) -> {
                    if (throwable != null) {
                        handleFailure(key, value, throwable);
                    }else {
                        handleSuccess(key, value, sendResult);
                    }
                });
    }

    /**
     * Sends a LibraryEvent synchronously to the Kafka topic.
     * This method blocks until the message is sent or a timeout occurs.
     *
     * @param libraryEvent The event to be sent.
     * @return SendResult containing metadata about the sent message.
     * @throws JsonProcessingException if there is an error during serialization.
     * @throws ExecutionException if an error occurs while executing the send operation.
     * @throws InterruptedException if the thread is interrupted while waiting.
     * @throws TimeoutException if the operation times out.
     */
    public SendResult<Integer, String> sendLibraryEvent_approach2(LibraryEvent libraryEvent) throws JsonProcessingException,
            ExecutionException, InterruptedException, TimeoutException {
        var key = libraryEvent.libraryEventId();
        var value = objectMapper.writeValueAsString(libraryEvent);

        //1. blocking call - get metadata about the kafka cluster
        //2. block and wait until the message is sent to the kafka

        var sendResult = kafkaTemplate.send(topic, key, value)
                .get(3, TimeUnit.SECONDS);
        handleSuccess(key, value, sendResult);
        return sendResult;
    }

    /**
     * Sends a LibraryEvent asynchronously using a ProducerRecord with additional headers.
     *
     * @param libraryEvent The event to be sent.
     * @return CompletableFuture representing the result of the send operation.
     * @throws JsonProcessingException if there is an error during serialization.
     */
    public CompletableFuture<SendResult<Integer, String>> sendLibraryEvent_approach3(LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        var key = libraryEvent.libraryEventId();
        var value = objectMapper.writeValueAsString(libraryEvent);
        var producerRecord = buildProducerRecord(key, value);

        // 1. blocking call - get metadata about the kafka cluster
        // 2. send message happens - returns a CompletableFuture

        var completableFuture = kafkaTemplate.send(producerRecord);

        return completableFuture
                .whenComplete((sendResult, throwable) -> {
                    if (throwable != null) {
                        handleFailure(key, value, throwable);
                    }else {
                        handleSuccess(key, value, sendResult);
                    }
                });
    }

    /**
     * Handles a successful Kafka message send operation.
     *
     * @param key The key of the sent message.
     * @param value The value of the sent message.
     * @param sendResult The result containing metadata about the sent message.
     */
    private void handleSuccess(Integer key, String value, SendResult<Integer, String> sendResult) {
        log.info("Message sent successfully for the key {} and the value {}, partition is {}",
                key, value, sendResult.getRecordMetadata().partition());
    }

    /**
     * Handles a failed Kafka message send operation.
     *
     * @param key The key of the message that failed to send.
     * @param value The value of the message that failed to send.
     * @param throwable The exception that occurred.
     */
    private void handleFailure(Integer key, String value, Throwable throwable) {
        log.error("Error sending the message and the exception is {}", throwable.getMessage(), throwable);
    }

    /**
     * Builds a ProducerRecord with custom headers for sending to Kafka.
     *
     * @param key The key of the message.
     * @param value The value of the message.
     * @return ProducerRecord with the specified key, value, and headers.
     */
    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value) {
        List<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));
        return new ProducerRecord<>(topic, null, key, value, recordHeaders);
    }
}
