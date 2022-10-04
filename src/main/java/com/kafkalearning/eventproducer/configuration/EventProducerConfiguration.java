package com.kafkalearning.eventproducer.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("local")
public class EventProducerConfiguration {

    @Bean
    public NewTopic libraryEvent() {
        return TopicBuilder.name("library-events")
                .partitions(2)
                .replicas(2)
                .build();
    }
}
