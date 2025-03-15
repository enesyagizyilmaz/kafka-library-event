package com.microservices.libraryeventsconsumer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class FailureRecord {
    @Id
    @GeneratedValue
    private Integer bookId;
    private String topic;
    private Integer key_value;
    private String errorRecord;
    private Integer partition;
    private Long offset_value;
    private String exception;
    private String status;

    public FailureRecord(Integer bookId, String topic, Integer key_value, String errorRecord,
                         Integer partition, Long offset_value, String exception, String status) {
        this.bookId = bookId;
        this.topic = topic;
        this.key_value = key_value;
        this.errorRecord = errorRecord;
        this.partition = partition;
        this.offset_value = offset_value;
        this.exception = exception;
        this.status = status;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getKey_value() {
        return key_value;
    }

    public void setKey_value(Integer key_value) {
        this.key_value = key_value;
    }

    public String getErrorRecord() {
        return errorRecord;
    }

    public void setErrorRecord(String errorRecord) {
        this.errorRecord = errorRecord;
    }

    public Integer getPartition() {
        return partition;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    public Long getOffset_value() {
        return offset_value;
    }

    public void setOffset_value(Long offset_value) {
        this.offset_value = offset_value;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
