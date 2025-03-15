package com.microservices.libraryeventsconsumer.repository;

import com.microservices.libraryeventsconsumer.entity.FailureRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FailureRecordRepository extends JpaRepository<FailureRecord,Integer> {
    List<FailureRecord> findAllByStatus(String status);
}
