package com.kafkalearning.eventproducer.controller;

import com.kafkalearning.eventproducer.domain.Book;
import com.kafkalearning.eventproducer.domain.LibraryEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"}, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers = ${spring.embedded.kafka.brokers}", "spring.kafka.admin.properties.bootstrap.servers = ${spring.embedded.kafka.brokers}"})
/**
 * Add @AutoConfigureMockMvc if you want to only test the controller invocation without starting the web server
 Remember as well to include.
 *
 *       @Autowired
 *    private MockMvc mockMvc;
 *
 * in the test class
 */
public class LibraryEventsControllerIT {


    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<Integer, String> consumer;


    @BeforeEach
    void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    void postLibraryEvent() {

        //given
        LibraryEvent libEvent = LibraryEvent.builder()
                .libraryEventId(123)
                .book(Book.builder()
                        .bookId(222)
                        .bookAuthor("Test Book Author")
                        .bookName("Test Book")
                        .build())
                .build();

        //build headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());

        //build request payload
        HttpEntity<LibraryEvent> requestPayload = new HttpEntity<>(libEvent, headers);

        //when
        ResponseEntity<LibraryEvent> response = restTemplate.exchange("/v1/libraryevent", HttpMethod.POST, requestPayload, LibraryEvent.class);

        //then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "library-events");

        String postedValue = consumerRecord.value();

        assertEquals("{\"libraryEventId\":123,\"book\":{\"bookId\":222,\"bookName\":\"Test Book\",\"bookAuthor\":\"Test Book Author\"}}", postedValue);
    }
}