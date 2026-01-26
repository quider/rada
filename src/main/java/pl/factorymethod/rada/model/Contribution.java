package pl.factorymethod.rada.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "contributions")
public class Contribution {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "public_id", unique = true)
  private UUID publicId;

  @Column(nullable = false)
  private BigDecimal value;

  @Column(name = "platform_commission_reserved", nullable = false, precision = 38, scale = 2)
  private BigDecimal platformCommissionReserved;

  @Column(name = "operator_fee", precision = 38, scale = 2)
  private BigDecimal operatorFee;

  @Column(name = "operator_fee_status", nullable = false, length = 20)
  private String operatorFeeStatus;

  @Column(name = "operator_fee_settled_at")
  private LocalDateTime operatorFeeSettledAt;

  @Column(name = "net_to_target", precision = 38, scale = 2, insertable = false, updatable = false)
  private BigDecimal netToTarget;

  @Column(name = "platform_profit", precision = 38, scale = 2, insertable = false, updatable = false)
  private BigDecimal platformProfit;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "target_id", nullable = false)
  private Target target;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
}
