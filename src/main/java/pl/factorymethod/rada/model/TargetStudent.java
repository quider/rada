package pl.factorymethod.rada.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "target_students")
public class TargetStudent {

  @EmbeddedId
  private TargetStudentId id = new TargetStudentId();

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("targetId")
  @JoinColumn(name = "target_id", nullable = false)
  private Target target;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("studentId")
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
}
