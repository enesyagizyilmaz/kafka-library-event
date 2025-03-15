package com.microservices.libraryeventsconsumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservices.libraryeventsconsumer.config.LibraryEventsConsumerConfig;
import com.microservices.libraryeventsconsumer.entity.FailureRecord;
import com.microservices.libraryeventsconsumer.repository.FailureRecordRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchedulingService {
    @Autowired
    private FailureRecordRepository failureRecordRepository;

    @Autowired
    private LibraryEventsService libraryEventsService;

    private static final Logger log = LoggerFactory.getLogger(SchedulingService.class);

    @Scheduled(fixedRate = 10000)
    public void retryFailedRecords() {
        log.info("Retrieving failure records started");
        List<FailureRecord> failureRecordList = failureRecordRepository.findAllByStatus(LibraryEventsConsumerConfig.RETRY);
        failureRecordList.forEach(failureRecord -> {
            log.info("Retrieved failure record: {}", failureRecord);
            var consumerRecord = buildConsumerRecord(failureRecord);
            try {
                libraryEventsService.processLibraryEvent(consumerRecord);
                failureRecord.setStatus(LibraryEventsConsumerConfig.SUCCESS);
                failureRecordRepository.save(failureRecord);
            } catch (Exception e) {
                log.error("Exception in retryFailedRecords: {}", e.getMessage(), e);
            }
        });
        log.info("Retrieving failure records ended");
    }

    private ConsumerRecord<Integer, String> buildConsumerRecord(FailureRecord failureRecord) {
        return new ConsumerRecord<>(
                failureRecord.getTopic(),
                failureRecord.getPartition(),
                failureRecord.getOffset_value(),
                failureRecord.getKey_value(),
                failureRecord.getErrorRecord()
        );
    }
}
