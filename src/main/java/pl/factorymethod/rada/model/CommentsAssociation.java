package pl.factorymethod.rada.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
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
@Table(name = "comments_associations")
public class CommentsAssociation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "public_id", unique = true)
  private UUID publicId;

  @Column(name = "entity_id", nullable = false)
  private Long entityId;

  @Column(name = "comment_id", nullable = false)
  private Long commentId;

  @Column(name = "comment_associate", nullable = false, length = 64)
  @Enumerated(jakarta.persistence.EnumType.STRING)
  private Associate commentAssociate;
}
