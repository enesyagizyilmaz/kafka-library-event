package com.microservices.libraryeventsconsumer.repository;

import com.microservices.libraryeventsconsumer.entity.LibraryEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryEventsRepository extends JpaRepository<LibraryEvent,Integer> {
}
