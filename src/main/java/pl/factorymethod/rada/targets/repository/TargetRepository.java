package pl.factorymethod.rada.targets.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pl.factorymethod.rada.model.Target;

@Repository
public interface TargetRepository extends JpaRepository<Target, Long> {
    
    Optional<Target> findByPublicId(UUID publicId);

    @Query("""
            select distinct t
            from Target t
            join t.targetStudents ts
            join ts.student s
            where s.schoolClass.publicId = :classId
            """)
    List<Target> findByClassPublicId(@Param("classId") UUID classPublicId);
}
