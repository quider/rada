package pl.factorymethod.rada.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "students")
public class Student {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "public_id", unique = true)
  private UUID publicId;

  @Column(nullable = false, length = 64)
  private String number;

  @Column(name = "first_name", length = 64)
  private String firstName;

  @Column(name = "last_name", length = 64)
  private String lastName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "class_id")
  private SchoolClass schoolClass;

  @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, orphanRemoval = true)
  private Set<TargetStudent> targetStudents = new HashSet<>();

  @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, orphanRemoval = true)
  private Set<StudentJoinCode> studentJoinCodes = new HashSet<>();
}
