package pl.factorymethod.rada.targets.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.factorymethod.rada.model.Target;

@Repository
public interface TargetRepository extends JpaRepository<Target, Long> {
    
    Optional<Target> findByPublicId(UUID publicId);
}
