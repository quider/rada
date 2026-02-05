package pl.factorymethod.rada.classes.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudentMovedToClassEvent(
    UUID studentPublicId,
    UUID sourceClassPublicId,
    UUID targetClassPublicId,
    String oldNumber,
    String newNumber,
    LocalDateTime movedAt
) {
}
