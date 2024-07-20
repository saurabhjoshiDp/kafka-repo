package com.joshi.kafka.repo.client.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SimpleKafkaConsumer {
    @KafkaListener(id = "simpleKafkaConsumer", topics = "${app.kafka.consumer.simpleConsumer.topic.name}",
            containerFactory = "defaultKafkaListenerContainerFactory",
            groupId = "${app.kafka.consumer.simpleConsumer.group-id}")
    public void simpleConsumer(ConsumerRecords<String, Object> records, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
        try {
            log.info("SimpleKafkaConsumer : Received {} records", records.count());
            processRecords(records);
            acknowledgment.acknowledge();
            log.info("SimpleKafkaConsumer : Records processed successfully!");
        } catch (Exception e) {
            log.error("SimpleKafkaConsumer: Exception during processing the records", e);
        }
    }

    private void processRecords(ConsumerRecords<String, Object> records) {
        records.forEach(record -> {
            String key = record.key();
            Object value = record.value();
            String topicName = record.topic();
            log.info("topic:{}, key:{}, value:{}", topicName, key, value);
        });
    }
}
