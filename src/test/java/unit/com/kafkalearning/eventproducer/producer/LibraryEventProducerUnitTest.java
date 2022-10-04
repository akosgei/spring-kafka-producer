package com.kafkalearning.eventproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafkalearning.eventproducer.domain.Book;
import com.kafkalearning.eventproducer.domain.LibraryEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LibraryEventProducerUnitTest {

    @Mock
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    LibraryEventProducer eventProducer;

    @Test
    void sendLibraryEventFailureTest() throws JsonProcessingException {

        Book book = Book.builder()
                .bookId(null)
                .bookAuthor(null)
                .bookName("Test Book")
                .build();

        LibraryEvent libEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book)
                .build();

        SettableListenableFuture<SendResult<Integer, String>> future = new SettableListenableFuture<>();
        future.setException(new RuntimeException("Exception calling Kafka"));

        String json = objectMapper.writeValueAsString(libEvent);
        when(kafkaTemplate.sendDefault(anyInt(), anyString())).thenReturn(future);

        assertThrows(RuntimeException.class, () -> eventProducer.sendLibraryEvent(libEvent));
    }
}
