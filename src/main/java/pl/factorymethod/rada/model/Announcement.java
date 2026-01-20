package pl.factorymethod.rada.model;

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
@Table(name = "announcements")
public class Announcement {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "is_read", nullable = false)
  private boolean read = false;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(columnDefinition = "text")
  private String description;

  @Column(columnDefinition = "text")
  private String summary;

  @Column(name = "published_at", nullable = false)
  private LocalDateTime publishedAt;
}
