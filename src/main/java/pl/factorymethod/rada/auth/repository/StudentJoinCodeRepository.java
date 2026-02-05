package pl.factorymethod.rada.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pl.factorymethod.rada.model.StudentJoinCode;

@Repository
public interface StudentJoinCodeRepository extends JpaRepository<StudentJoinCode, Long> {

    boolean existsByJoinCode(String joinCode);

    Optional<StudentJoinCode> findByJoinCode(String joinCode);

    @Modifying
    @Query(nativeQuery = true,
            value = "UPDATE student_join_codes SET user_id = :userId, updated_at = now() " +
                    "WHERE join_code = :joinCode AND user_id IS NULL")
    int assignUserToJoinCode(@Param("userId") Long userId, @Param("joinCode") String joinCode);
}
