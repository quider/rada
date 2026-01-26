package pl.factorymethod.rada.contributions.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.factorymethod.rada.model.Contribution;

@Repository
public interface ContributionRepository extends JpaRepository<Contribution, Long> {
    
    Optional<Contribution> findByPublicId(UUID publicId);
}
