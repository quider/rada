package pl.factorymethod.rada.targets.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TargetContributionCollectionOpenedEvent(
    UUID targetPublicId,
    LocalDateTime openedAt,
    BigDecimal feePerStudent,
    int studentCount
) {
}
