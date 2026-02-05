package pl.factorymethod.rada.targets.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTargetRequest {

    private String description;

    private String summary;

    @NotNull(message = "Due date cannot be null")
    private LocalDate dueTo;

    @NotNull(message = "Estimated value cannot be null")
    @Positive(message = "Estimated value must be positive")
    private BigDecimal estimatedValue;
}
