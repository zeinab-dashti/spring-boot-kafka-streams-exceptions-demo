package space.zeinab.demo.kafka.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;
import space.zeinab.demo.kafka.config.KafkaConfig;
import space.zeinab.demo.kafka.model.User;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserProcessTopology {
    private final ObjectMapper objectMapper;

    @Autowired
    public void process(StreamsBuilder streamsBuilder) {

        KStream<String, User> inputStream = streamsBuilder.stream(KafkaConfig.INPUT_TOPIC, Consumed.with(Serdes.String(), getUserSerde()));
        inputStream.print(Printed.<String, User>toSysOut().withLabel("Input Stream"));

        KStream<String, User> processedStream = inputStream.peek((key, value) -> {
            if (value.name().contains("userDataWithError")) {
                throw new IllegalStateException(value.name());
            }
        });

        processedStream.to(KafkaConfig.OUTPUT_TOPIC, Produced.with(Serdes.String(), getUserSerde()));
    }

    private JsonSerde<User> getUserSerde() {
        return new JsonSerde<>(User.class, objectMapper);
    }

}
