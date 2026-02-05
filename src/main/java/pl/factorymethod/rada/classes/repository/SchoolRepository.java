package pl.factorymethod.rada.classes.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.factorymethod.rada.model.School;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {

    Optional<School> findByPublicId(UUID publicId);
}
