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
@Table(name = "payments")
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "date", nullable = false)
  private LocalDateTime date;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "contribution_id", nullable = false)
  private Contribution contribution;

  @Column(nullable = false)
  private BigDecimal value;

  @Column(name = "is_success", nullable = false)
  private boolean success;

  @Column(nullable = false)
  private boolean rejected;

  @Column(nullable = false)
  private boolean returned;

  @Column(name = "vendor_id", nullable = false)
  private String vendorId;

  @Column(name = "payment_type", nullable = false, length = 64)
  private String paymentType;
}
