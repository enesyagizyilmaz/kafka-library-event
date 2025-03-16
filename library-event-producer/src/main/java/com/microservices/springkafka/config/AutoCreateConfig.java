package com.microservices.springkafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class AutoCreateConfig {
    @Value("${spring.kafka.topic}")
    public String topic;

    /**
     * Creates a Kafka topic with the specified name, number of partitions, and replicas.
     * @return NewTopic
     */
    @Bean
    public NewTopic libraryEvents() {
        return TopicBuilder
                .name(topic)
                .partitions(3)
                .replicas(3)
                .build();
    }
}
