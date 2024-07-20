package com.joshi.kafka.repo.client.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class KafkaProducer {
    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    public void produceMsgToKafka(String topicName, String key, Object payload) {
        log.info("producing msg to kafka. topicName:{}, key:{}, payload:{}", topicName, key, payload);
        kafkaTemplate.send(topicName, key, payload);
    }
}
