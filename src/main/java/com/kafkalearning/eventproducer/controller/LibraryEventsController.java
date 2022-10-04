package com.kafkalearning.eventproducer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kafkalearning.eventproducer.domain.LibraryEvent;
import com.kafkalearning.eventproducer.producer.LibraryEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class LibraryEventsController {


    @Autowired
    LibraryEventProducer libraryEventProducer;

    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> storeLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException {

        libraryEventProducer.SendLibraryEventProducerRecordVersion(libraryEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

}
