package com.kafkalearning.eventproducer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafkalearning.eventproducer.domain.Book;
import com.kafkalearning.eventproducer.domain.LibraryEvent;
import com.kafkalearning.eventproducer.domain.LibraryEventType;
import com.kafkalearning.eventproducer.producer.LibraryEventProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * To write unit tests in spring-boot for the web layer, use webmvctest annotation
 * Using this annotation will disable full auto-configuration and instead apply only configuration relevant to MVC tests
 * (i.e. @Controller, @ControllerAdvice, @JsonComponent, Converter/GenericConverter, Filter, WebMvcConfigurer and HandlerMethodArgumentResolver beans but not @Component, @Service or @Repository beans).
 * Typically @WebMvcTest is used in combination with @MockBean or @Import to create any collaborators required by your @Controller beans.
 * NB:
 * If you are looking to load your full application configuration and use MockMVC, you should consider @SpringBootTest combined with @AutoConfigureMockMvc rather than this annotation.
 */
@WebMvcTest(LibraryEventsController.class)
//Includes both the @AutoConfigureWebMvc and the @AutoConfigureMockMvc, among other functionality. such as Junit 5 @ExtendWith(SpringExtension.class)
public class LibraryEventControllerUnitTest {

    @Autowired
    MockMvc mockmvc;

    @MockBean
    LibraryEventProducer libraryEventProducer;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void postLibraryEvent() throws Exception {

        //given stuff
        Book book = Book.builder()
                .bookId(222)
                .bookAuthor("Test Book Author")
                .bookName("Test Book")
                .build();

        LibraryEvent libEvent = LibraryEvent.builder()
                .libraryEventType(LibraryEventType.NEW)
                .libraryEventId(123)
                .book(book)
                .build();

        doNothing().when(libraryEventProducer).sendLibraryEvent(isA(LibraryEvent.class));

        //when doing some stuff
        String json = mapper.writeValueAsString(libEvent);
        ResultActions result = mockmvc.perform(post("/v1/libraryevent")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
        /**
         *.
         *                 andExpect(jsonPath("$.book.bookName").value("Test Book")).
         *                 andExpect(jsonPath("$.libraryEventId").value("123"));
         */

        //expect results

    }

    @Test
    public void postLibraryEventErrorMessageIfValidationFails() throws Exception {
        Book book = Book.builder()
                .bookId(null)
                .bookAuthor(null)
                .bookName("Test Book")
                .build();

        LibraryEvent libEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .libraryEventType(null)
                .book(book)
                .build();

        doNothing().when(libraryEventProducer).sendLibraryEvent(isA(LibraryEvent.class));

        //when doing some stuff
        String json = mapper.writeValueAsString(libEvent);
        ResultActions result = mockmvc.perform(post("/v1/libraryevent")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().string("book.bookAuthor-must not be blank,book.bookId-must not be null,libraryEventId-must not be null,libraryEventType-must not be null"));
    }
}
