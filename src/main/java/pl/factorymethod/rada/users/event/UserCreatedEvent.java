package pl.factorymethod.rada.users.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserCreatedEvent(
    UUID userPublicId,
    String email,
    LocalDateTime createdAt
) {
}
