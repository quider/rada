package pl.factorymethod.rada.classes.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudentAddedToClassEvent(
    UUID studentPublicId,
    UUID classPublicId,
    String number,
    String firstName,
    String lastName,
    LocalDateTime addedAt
) {
}
