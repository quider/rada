package pl.factorymethod.rada.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class PollAnswerId implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "poll_question_id", nullable = false)
  private UUID pollQuestionId;

  @Column(name = "student_id", nullable = false)
  private UUID studentId;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PollAnswerId that = (PollAnswerId) o;
    return Objects.equals(pollQuestionId, that.pollQuestionId)
        && Objects.equals(studentId, that.studentId)
        && Objects.equals(userId, that.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pollQuestionId, studentId, userId);
  }
}
