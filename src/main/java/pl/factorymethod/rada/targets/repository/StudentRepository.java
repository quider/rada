package pl.factorymethod.rada.targets.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.factorymethod.rada.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    Optional<Student> findByPublicId(UUID publicId);
    
    List<Student> findByPublicIdIn(List<UUID> publicIds);
}
