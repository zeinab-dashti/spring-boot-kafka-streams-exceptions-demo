package space.zeinab.demo.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import space.zeinab.demo.kafka.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


@Slf4j
public class MockUserDataForErrorAndContinueProducer {
    private final static String[] usersWithErrorAndContinue = {"user1", "user2", "user3", "userDataWithErrorAndContinue"};

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        List<User> dummyDataForErrorAndContinue = generateDummyDataForErrorAndContinue();
        publishData(dummyDataForErrorAndContinue, objectMapper);
    }

    private static void publishData(List<User> users, ObjectMapper objectMapper) {
        users
                .forEach(user -> {
                    try {
                        String userJSON = objectMapper.writeValueAsString(user);
                        RecordMetadata recordMetaData = ProducerUtil.produceRecord(user.id(), userJSON);

                        log.info("Published user data : {} ", recordMetaData);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private static List<User> generateDummyDataForErrorAndContinue() {
        List<User> dummyUsers = new ArrayList<>();
        Arrays.stream(usersWithErrorAndContinue).forEach(user -> {
            User data = new User(new Random().nextInt() + "", user, LocalDateTime.now());
            dummyUsers.add(data);
        });
        return dummyUsers;
    }

}
