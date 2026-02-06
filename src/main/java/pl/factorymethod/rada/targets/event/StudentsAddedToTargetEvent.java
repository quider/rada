package pl.factorymethod.rada.targets.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record StudentsAddedToTargetEvent(
    UUID targetPublicId,
    List<UUID> studentPublicIds,
    int studentCount,
    LocalDateTime addedAt
) {
}
