package pl.factorymethod.rada.contributions.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContributionResponse {

    private String publicId;
    private BigDecimal value;
    private BigDecimal platformCommissionReserved;
    private BigDecimal operatorFee;
    private String operatorFeeStatus;
    private LocalDateTime operatorFeeSettledAt;
    private BigDecimal netToTarget;
    private BigDecimal platformProfit;
    private String studentId;
    private String targetId;
    private LocalDateTime createdAt;
}
