package space.zeinab.demo.kafka.model;

import java.time.LocalDateTime;

public record User(String id, String name, LocalDateTime registerDateTime) {
}
