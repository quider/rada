package pl.factorymethod.rada.contributions.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateContributionRequest {
    
    @NotNull(message = "Target ID cannot be null")
    private String targetId;
    
    @NotNull(message = "Student ID cannot be null")
    private String studentId;
    
    @NotNull(message = "Value cannot be null")
    @DecimalMin(value = "0.01", message = "Value must be greater than 0")
    private BigDecimal value;
    
    @NotNull(message = "Platform commission rate cannot be null")
    @DecimalMin(value = "0.0", message = "Platform commission rate must be non-negative")
    private BigDecimal platformCommissionRate;
    
    @NotNull(message = "Operator fee rate cannot be null")
    @DecimalMin(value = "0.0", message = "Operator fee rate must be non-negative")
    private BigDecimal operatorFeeRate;
}
