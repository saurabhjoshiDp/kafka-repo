package com.joshi.kafka.repo.client.producer;

import com.joshi.kafka.repo.Utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
public class KafkaProducer {
    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    CommonUtils commonUtils;

    public void produceMsgToKafka(String topicName, String key, Object payload) {
        log.info("producing.. msg to kafka. topicName:{}, key:{}, payload:{}", topicName, key, payload);
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicName, key, payload);
        future.thenAccept(result -> {
            RecordMetadata recordMetadata = result.getRecordMetadata();
            log.trace("Successfully produced the data to topic:{}, key:{} partition:{}, offset:{}",
                    recordMetadata.topic(), key, recordMetadata.partition(), recordMetadata.offset());
        }).exceptionally(ex -> {
            log.error("Exception while producing msg to kafka topic: {}, exMsg:{} ", topicName, ex.getMessage(), ex);
            return null;
        });
    }
}
