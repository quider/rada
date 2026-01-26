package pl.factorymethod.rada.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "targets")
public class Target {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "public_id", unique = true)
  private UUID publicId;

  @Column(columnDefinition = "text")
  private String description;

  @Column(columnDefinition = "text")
  private String summary;

  @Column(name = "due_to", nullable = false)
  private LocalDate dueTo;

  @Column(name = "estimated_value", nullable = false)
  private BigDecimal estimatedValue;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "target", fetch = FetchType.LAZY, orphanRemoval = true)
  private Set<TargetStudent> targetStudents = new HashSet<>();
}
