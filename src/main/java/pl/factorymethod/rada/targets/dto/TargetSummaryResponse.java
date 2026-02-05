package pl.factorymethod.rada.targets.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TargetSummaryResponse {

    private String publicId;
    private String description;
    private String summary;
    private LocalDate dueTo;
    private BigDecimal estimatedValue;
    private LocalDateTime createdAt;

    private int studentCount;
    private BigDecimal feePerStudent;
    private LocalDateTime feeCalculatedAt;
}
