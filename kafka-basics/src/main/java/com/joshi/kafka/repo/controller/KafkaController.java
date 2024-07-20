package com.joshi.kafka.repo.controller;

import com.joshi.kafka.repo.client.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/kafka")
public class KafkaController {
    @Autowired
    KafkaProducer kafkaProducer;

    @PostMapping("/publishMsgToKafka")
    void publishMsgToKafka(@RequestParam String topicName, @RequestParam String key, @RequestBody Object payload) {
        kafkaProducer.produceMsgToKafka(topicName, key, payload);
    }

}
