package space.zeinab.demo.kafka.exceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.errors.DeserializationExceptionHandler;
import org.apache.kafka.streams.processor.ProcessorContext;

import java.util.Map;

@Slf4j
public class CustomDeserializationExceptionHandler implements DeserializationExceptionHandler {
    private static final String invalidInput = "dummyTextForShutDown";

    @Override
    public DeserializationHandlerResponse handle(ProcessorContext processorContext, ConsumerRecord<byte[], byte[]> consumerRecord, Exception exception) {
        log.error("Exception in deserializing: {} , kafka record : {}", exception.getMessage(), consumerRecord, exception);

        if (exception.getCause().getMessage().contains(invalidInput)) {
            return DeserializationHandlerResponse.FAIL;
        } else {
            return DeserializationHandlerResponse.CONTINUE;
        }
    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
