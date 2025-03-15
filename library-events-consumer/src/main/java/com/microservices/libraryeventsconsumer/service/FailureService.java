package com.microservices.libraryeventsconsumer.service;

import com.microservices.libraryeventsconsumer.entity.FailureRecord;
import com.microservices.libraryeventsconsumer.repository.FailureRecordRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FailureService {
    @Autowired
    private FailureRecordRepository failureRecordRepository;

    public void saveFailedRecord(ConsumerRecord<Integer, String> consumerRecord, Exception e, String status) {
        var failureRecord = new FailureRecord(null, consumerRecord.topic(),
                consumerRecord.key(), consumerRecord.value(), consumerRecord.partition(),
                consumerRecord.offset(), e.getCause().getMessage(), status);
        failureRecordRepository.save(failureRecord);
    }
}
