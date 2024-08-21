package space.zeinab.demo.kafka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.streams.RecoveringDeserializationExceptionHandler;
import space.zeinab.demo.kafka.exceptionHandler.CustomStreamsProcessorErrorHandler;

import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfig {
    public static final String INPUT_TOPIC = "input-topic";
    public static final String OUTPUT_TOPIC = "output-topic";

    @Autowired
    KafkaProperties kafkaProperties;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Bean
    public NewTopic inputTopic() {
        return TopicBuilder.name(INPUT_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic outputTopic() {
        return TopicBuilder.name(OUTPUT_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Bean
    public StreamsBuilderFactoryBeanConfigurer beanConfigurers() {
        return factoryBeanConfigurer -> {
            factoryBeanConfigurer.setStreamsUncaughtExceptionHandler(new CustomStreamsProcessorErrorHandler());
        };
    }

   @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamConfig() {
        Map<String, Object> props  = kafkaProperties.buildStreamsProperties();
        props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, RecoveringDeserializationExceptionHandler.class);
        props.put(RecoveringDeserializationExceptionHandler.KSTREAM_DESERIALIZATION_RECOVERER, recoverer());

        return new KafkaStreamsConfiguration(props);
    }

    private ConsumerRecordRecoverer recoverer() {
        return (consumerRecord, e) -> {
            log.error("Exception is Deserialization: {} , Failed Kafka Record : {} ", e.getMessage(), consumerRecord, e);
        };
    }

    /*@Bean
    public DeadLetterPublishingRecoverer recoverer() {
        return new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (consumerRecord, e) -> {
                    log.error("Exception is Deserialization: {} , Failed Kafka Record : {} ", e.getMessage(), consumerRecord, e);
                    return new TopicPartition("DLQName", consumerRecord.partition());
                });
    }*/
}
