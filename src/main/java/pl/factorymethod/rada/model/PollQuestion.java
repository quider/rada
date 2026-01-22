package pl.factorymethod.rada.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "polls_questions")
public class PollQuestion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "public_id", unique = true)
  private UUID publicId;

  @Column(nullable = false, columnDefinition = "text")
  private String question;

  @Column(name = "poll_id", nullable = false)
  private Long pollId;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
}
