package pl.factorymethod.rada.auth.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pl.factorymethod.rada.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);

    List<User> findByEmailIn(List<String> emails);

    List<User> findByPhoneIn(List<String> phones);

    boolean existsByPublicId(UUID publicId);

    @Query(nativeQuery = true, 
        value = """
                select u.* from student_join_codes sjc 
                join users u on u.id = sjc.user_id
                where join_code = :joinCode;
                """)
    User findByJoinCode(@Param("joinCode") String joinCode);

    @Modifying
    @Query(nativeQuery = true, 
        value = "UPDATE student_join_codes SET user_id = :userId WHERE join_code = :joinCode")
    void saveJoinCode(@Param("userId") Long userId, @Param("joinCode") String joinCode);
}
