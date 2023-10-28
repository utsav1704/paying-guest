package com.pg.owner.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic topicOwner() {
        return TopicBuilder.name("pg_owner").build();
    }

    @Bean
    public NewTopic topicProperty() {
        return TopicBuilder.name("pg_property").build();
    }
}
