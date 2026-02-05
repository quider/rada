package pl.factorymethod.rada.classes.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record SchoolUpdatedEvent(
    UUID schoolPublicId,
    String name,
    String address,
    LocalDateTime updatedAt
) {
}
