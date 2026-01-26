package pl.factorymethod.rada.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class TargetStudentId implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "target_id")
  private Long targetId;

  @Column(name = "student_id")
  private Long studentId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TargetStudentId that = (TargetStudentId) o;
    return Objects.equals(targetId, that.targetId) && Objects.equals(studentId, that.studentId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(targetId, studentId);
  }
}
