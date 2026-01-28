package pl.factorymethod.rada.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.factorymethod.rada.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);

    List<User> findByEmailIn(List<String> emails);

    List<User> findByPhoneIn(List<String> phones);
}
