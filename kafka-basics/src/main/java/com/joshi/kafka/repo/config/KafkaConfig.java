package com.joshi.kafka.repo.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.record.CompressionType;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.bootstrap.server}")
    private String kafkaServers;

    @Value("${app.kafka.request.timeout.ms}")
    private String requestTimeout;

//    ########## PRODUCER ############

    @Bean
    public DefaultKafkaProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeout);
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, CompressionType.GZIP.name);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


    //    ############ CONSUMER #############
    @Value("${app.kafka.consumer.listener.concurrency}")
    private Integer listenerConcurrency;

    @Bean
    @Qualifier("defaultKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> defaultKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaListenerStringConsumerFactory());
        factory.setConcurrency(listenerConcurrency);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setBatchListener(true);
        factory.setCommonErrorHandler(getCommonErrorHandler());
        return factory;
    }
    /*
    consumer:
      heartbeat-interval: 7000
      fetch-max-wait-ms: 5000
      fetch-min-bytes: 40000
      listener:
        poll-timeout: 600000
     */

    @Value("${app.kafka.consumer.errorHandler.intervalMs}")
    private Long errorHandlerInterval;
    @Value("${app.kafka.consumer.errorHandler.maxRetries}")
    private Long errorHandlerMaxRetries;

    private CommonErrorHandler getCommonErrorHandler() {
        return new DefaultErrorHandler(
                new FixedBackOff(errorHandlerInterval, errorHandlerMaxRetries));
    }

    @Value("${app.kafka.consumer.enable-auto-commit}")
    private boolean enableAutoCommit;
    @Value("${app.kafka.consumer.session-timeout-ms}")
    private Integer kafkaConsumerSessionTimeout;
    @Value("${app.kafka.consumer.max-poll-interval-ms}")
    private Integer kafkaConsumerMaxPollout;
    @Value("${app.kafka.consumer.auto-offset-reset}")
    private String autoOffsetResetConfig;
    @Value("${app.kafka.consumer.max-poll-records}")
    private Integer kafkaConsumerMaxPollRecords;

    @Bean
    public ConsumerFactory<String, Object> kafkaListenerStringConsumerFactory() {
        Map<String, Object> consumerProperties = new HashMap<>();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProperties.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        consumerProperties.put(JsonDeserializer.REMOVE_TYPE_INFO_HEADERS, false);

        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        consumerProperties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaConsumerSessionTimeout);
        consumerProperties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaConsumerMaxPollout);
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetResetConfig);
        consumerProperties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaConsumerMaxPollRecords);

        ErrorHandlingDeserializer<Object> errorHandlingValueDeserializer
                = new ErrorHandlingDeserializer<>(new JsonDeserializer<>());
        return new DefaultKafkaConsumerFactory<>(
                consumerProperties, new StringDeserializer(), errorHandlingValueDeserializer
        );
    }
}
