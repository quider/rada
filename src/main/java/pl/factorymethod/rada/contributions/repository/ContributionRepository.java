package pl.factorymethod.rada.contributions.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.factorymethod.rada.model.Contribution;
import pl.factorymethod.rada.model.Student;
import pl.factorymethod.rada.model.Target;

@Repository
public interface ContributionRepository extends JpaRepository<Contribution, Long> {
    
    Optional<Contribution> findByPublicId(UUID publicId);

    List<Contribution> findByTargetOrderByCreatedAtDesc(Target target);

    List<Contribution> findByStudentOrderByCreatedAtDesc(Student student);
}
