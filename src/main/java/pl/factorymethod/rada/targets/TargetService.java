package pl.factorymethod.rada.targets;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.factorymethod.rada.model.Student;
import pl.factorymethod.rada.model.Target;
import pl.factorymethod.rada.model.TargetStudent;
import pl.factorymethod.rada.shared.events.EventPublisher;
import pl.factorymethod.rada.targets.dto.AddStudentsToTargetRequest;
import pl.factorymethod.rada.targets.dto.CreateTargetRequest;
import pl.factorymethod.rada.targets.dto.TargetResponse;
import pl.factorymethod.rada.targets.dto.TargetSummaryResponse;
import pl.factorymethod.rada.targets.dto.TargetStudentResponse;
import pl.factorymethod.rada.targets.event.TargetContributionCollectionOpenedEvent;
import pl.factorymethod.rada.targets.event.StudentsAddedToTargetEvent;
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
        private final EventPublisher eventPublisher;

        @Transactional
        public TargetResponse createTarget(CreateTargetRequest request) {
                Target target = new Target();
                target.setPublicId(UUID.randomUUID());
                target.setDescription(request.getDescription());
                target.setSummary(request.getSummary());
                target.setDueTo(request.getDueTo());
                target.setEstimatedValue(request.getEstimatedValue());
                target.setCreatedAt(LocalDateTime.now());

                Target savedTarget = targetRepository.save(target);

                log.info("Target created: publicId={}, dueTo={}, estimatedValue={}",
                                savedTarget.getPublicId(), savedTarget.getDueTo(), savedTarget.getEstimatedValue());

                return TargetResponse.builder()
                                .publicId(savedTarget.getPublicId().toString())
                                .description(savedTarget.getDescription())
                                .summary(savedTarget.getSummary())
                                .dueTo(savedTarget.getDueTo())
                                .estimatedValue(savedTarget.getEstimatedValue())
                                .createdAt(savedTarget.getCreatedAt())
                                .build();
        }

        @Transactional
        public void addStudentsToTarget(AddStudentsToTargetRequest request) {
                UUID targetPublicId = UUID.fromString(request.getTargetId());

                // Find target by public ID
                Target target = targetRepository.findByPublicId(targetPublicId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Target not found: " + request.getTargetId()));

                // Convert student IDs to UUIDs
                List<UUID> requestedStudentIds = request.getStudentIds().stream()
                                .map(UUID::fromString)
                                .distinct()
                                .toList();

                // Find all students by public IDs
                List<Student> students = studentRepository.findByPublicIdIn(requestedStudentIds);

                if (students.size() != requestedStudentIds.size()) {
                        log.warn("Not all students found. Requested: {}, Found: {}",
                                        requestedStudentIds.size(), students.size());
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Some students not found");
                }

                List<TargetStudent> existingTargetStudents = targetStudentRepository
                                .findByTargetAndStudent_PublicIdIn(target, requestedStudentIds);
                Set<UUID> existingStudentIds = new HashSet<>(existingTargetStudents.size());
                for (TargetStudent targetStudent : existingTargetStudents) {
                        existingStudentIds.add(targetStudent.getStudent().getPublicId());
                }

                List<UUID> newStudentIds = new ArrayList<>();
                for (UUID studentId : requestedStudentIds) {
                        if (!existingStudentIds.contains(studentId)) {
                                newStudentIds.add(studentId);
                        }
                }

                if (newStudentIds.isEmpty()) {
                        log.info("No new students to add to target {}", target.getPublicId());
                        return;
                }

                Map<UUID, Student> studentsById = new HashMap<>(students.size());
                for (Student student : students) {
                        studentsById.put(student.getPublicId(), student);
                }

                List<Student> newStudents = new ArrayList<>(newStudentIds.size());
                for (UUID studentId : newStudentIds) {
                        newStudents.add(studentsById.get(studentId));
                }

                // Create TargetStudent entries
                LocalDateTime now = LocalDateTime.now();
                List<TargetStudent> targetStudents = newStudents.stream()
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

                eventPublisher.publish(new StudentsAddedToTargetEvent(
                                target.getPublicId(),
                                newStudentIds,
                                newStudentIds.size(),
                                now));

                log.info("Successfully added {} students to target {}", newStudentIds.size(), target.getPublicId());
        }

        @Transactional
        public void openContributionCollection(String targetId) {
                UUID targetPublicId = UUID.fromString(targetId);

                Target target = targetRepository.findByPublicId(targetPublicId)
                                .orElseThrow(() -> new RuntimeException("Target not found: " + targetId));

                List<TargetStudent> targetStudents = targetStudentRepository.findByTarget(target);

                if (targetStudents.isEmpty()) {
                        throw new RuntimeException("No students assigned to target: " + targetId);
                }

                BigDecimal feePerStudent = target.getEstimatedValue()
                                .divide(BigDecimal.valueOf(targetStudents.size()), 2, RoundingMode.HALF_UP);
                LocalDateTime now = LocalDateTime.now();

                targetStudents.forEach(targetStudent -> {
                        targetStudent.setFeeAmount(feePerStudent);
                        targetStudent.setFeeCalculatedAt(now);
                });

                targetStudentRepository.saveAll(targetStudents);

                eventPublisher.publish(new TargetContributionCollectionOpenedEvent(
                                target.getPublicId(),
                                now,
                                feePerStudent,
                                targetStudents.size()));

                log.info("Contribution collection opened for target {}. Fee per student: {}, students: {}",
                                target.getPublicId(), feePerStudent, targetStudents.size());
        }

        @Transactional(readOnly = true)
        public List<TargetSummaryResponse> getTargets() {
                List<Target> targets = targetRepository.findAll();
                List<TargetSummaryResponse> responses = new ArrayList<>(targets.size());
                for (Target target : targets) {
                        responses.add(mapToSummary(target));
                }
                return responses;
        }

        @Transactional(readOnly = true)
        public TargetSummaryResponse getTarget(String targetId) {
                UUID targetPublicId = UUID.fromString(targetId);
                Target target = targetRepository.findByPublicId(targetPublicId)
                                .orElseThrow(() -> new RuntimeException("Target not found: " + targetId));
                return mapToSummary(target);
        }

        @Transactional(readOnly = true)
        public List<TargetStudentResponse> getTargetStudents(String targetId) {
                UUID targetPublicId = UUID.fromString(targetId);
                Target target = targetRepository.findByPublicId(targetPublicId)
                                .orElseThrow(() -> new RuntimeException("Target not found: " + targetId));

                List<TargetStudent> targetStudents = targetStudentRepository.findByTarget(target);
                List<TargetStudentResponse> responses = new ArrayList<>(targetStudents.size());
                for (TargetStudent targetStudent : targetStudents) {
                        Student student = targetStudent.getStudent();
                        responses.add(TargetStudentResponse.builder()
                                        .studentId(student.getPublicId().toString())
                                        .number(student.getNumber())
                                        .firstName(student.getFirstName())
                                        .lastName(student.getLastName())
                                        .build());
                }
                return responses;
        }

        @Transactional(readOnly = true)
        public List<TargetSummaryResponse> getTargetsByClass(String classId) {
                UUID classPublicId = UUID.fromString(classId);
                List<Target> targets = targetRepository.findByClassPublicId(classPublicId);
                List<TargetSummaryResponse> responses = new ArrayList<>(targets.size());
                for (Target target : targets) {
                        responses.add(mapToSummary(target));
                }
                return responses;
        }

        private TargetSummaryResponse mapToSummary(Target target) {
                List<TargetStudent> targetStudents = targetStudentRepository.findByTarget(target);
                int studentCount = targetStudents.size();
                BigDecimal feePerStudent = null;
                LocalDateTime feeCalculatedAt = null;

                for (TargetStudent targetStudent : targetStudents) {
                        if (targetStudent.getFeeCalculatedAt() != null) {
                                if (feeCalculatedAt == null
                                                || targetStudent.getFeeCalculatedAt().isAfter(feeCalculatedAt)) {
                                        feeCalculatedAt = targetStudent.getFeeCalculatedAt();
                                        feePerStudent = targetStudent.getFeeAmount();
                                }
                        }
                }

                return TargetSummaryResponse.builder()
                                .publicId(target.getPublicId().toString())
                                .description(target.getDescription())
                                .summary(target.getSummary())
                                .dueTo(target.getDueTo())
                                .estimatedValue(target.getEstimatedValue())
                                .createdAt(target.getCreatedAt())
                                .studentCount(studentCount)
                                .feePerStudent(feePerStudent)
                                .feeCalculatedAt(feeCalculatedAt)
                                .build();
        }
}
