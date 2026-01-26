package pl.factorymethod.rada.targets;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.factorymethod.rada.model.Student;
import pl.factorymethod.rada.model.Target;
import pl.factorymethod.rada.model.TargetStudent;
import pl.factorymethod.rada.targets.dto.AddStudentsToTargetRequest;
import pl.factorymethod.rada.targets.repository.StudentRepository;
import pl.factorymethod.rada.targets.repository.TargetRepository;
import pl.factorymethod.rada.targets.repository.TargetStudentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class TargetService {

    private final TargetRepository targetRepository;
    private final StudentRepository studentRepository;
    private final TargetStudentRepository targetStudentRepository;

    @Transactional
    public void addStudentsToTarget(AddStudentsToTargetRequest request) {
        UUID targetPublicId = UUID.fromString(request.getTargetId());
        
        // Find target by public ID
        Target target = targetRepository.findByPublicId(targetPublicId)
                .orElseThrow(() -> new RuntimeException("Target not found: " + request.getTargetId()));
        
        // Convert student IDs to UUIDs
        List<UUID> studentPublicIds = request.getStudentIds().stream()
                .map(UUID::fromString)
                .toList();
        
        // Find all students by public IDs
        List<Student> students = studentRepository.findByPublicIdIn(studentPublicIds);
        
        if (students.size() != studentPublicIds.size()) {
            log.warn("Not all students found. Requested: {}, Found: {}", 
                    studentPublicIds.size(), students.size());
            throw new RuntimeException("Some students not found");
        }
        
        // Create TargetStudent entries
        LocalDateTime now = LocalDateTime.now();
        List<TargetStudent> targetStudents = students.stream()
                .map(student -> {
                    TargetStudent targetStudent = new TargetStudent();
                    targetStudent.setTarget(target);
                    targetStudent.setStudent(student);
                    targetStudent.setCreatedAt(now);
                    return targetStudent;
                })
                .toList();
        
        // Save all associations
        targetStudentRepository.saveAll(targetStudents);
        
        log.info("Successfully added {} students to target {}", students.size(), target.getPublicId());
    }
}
