package space.zeinab.demo.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Slf4j
public class ProducerUtil {
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String INPUT_TOPIC = "input-topic";

    static KafkaProducer<String, String> producer = new KafkaProducer<String, String>(getProducerProperties());

    public static RecordMetadata produceRecord(String key, String message) {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(INPUT_TOPIC, key, message);
        RecordMetadata recordMetadata = null;

        try {
            log.info("producerRecord : " + producerRecord);
            recordMetadata = producer.send(producerRecord).get();
        } catch (InterruptedException e) {
            log.error("InterruptedException in  publishMessageSync : {}  ", e.getMessage(), e);
        } catch (ExecutionException e) {
            log.error("ExecutionException in  publishMessageSync : {}  ", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Exception in  publishMessageSync : {}  ", e.getMessage(), e);
        }
        return recordMetadata;
    }

    private static Properties getProducerProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return props;
    }

}
