package pl.factorymethod.rada.classes.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record SchoolClassCreatedEvent(
    UUID classPublicId,
    UUID schoolPublicId,
    String name,
    Integer startYear,
    String description,
    LocalDateTime createdAt
) {
}
