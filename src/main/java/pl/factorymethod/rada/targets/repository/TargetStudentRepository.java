package pl.factorymethod.rada.targets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.factorymethod.rada.model.TargetStudent;
import pl.factorymethod.rada.model.TargetStudentId;

@Repository
public interface TargetStudentRepository extends JpaRepository<TargetStudent, TargetStudentId> {
}
