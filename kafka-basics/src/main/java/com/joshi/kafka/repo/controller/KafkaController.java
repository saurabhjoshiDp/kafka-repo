package com.joshi.kafka.repo.controller;

import com.joshi.kafka.repo.client.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/kafka")
public class KafkaController {
    @Autowired
    KafkaProducer kafkaProducer;

    @PostMapping("/publishMsgToKafka")
    void publishMsgToKafka(@RequestParam String topicName, @RequestParam String key, @RequestBody Object payload) {
        kafkaProducer.produceMsgToKafka(topicName, key, payload);
    }

    @PostMapping("/testStickyPartitioner")
    void testStickyPartitioner(@RequestParam String topicName, @RequestParam String key, @RequestBody Object payload) {
        // KafkaProducer will make batch to send data to partitions
        // its performance improvement
        for (int i = 0; i < 50; i++) {
            ((Map<String, Object>) payload).put("Iteration", i);
            kafkaProducer.produceMsgToKafka(topicName, key + " " + i, payload);
        }
    }

}
