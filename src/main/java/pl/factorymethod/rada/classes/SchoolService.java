package pl.factorymethod.rada.classes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.factorymethod.rada.classes.dto.CreateSchoolRequest;
import pl.factorymethod.rada.classes.dto.SchoolResponse;
import pl.factorymethod.rada.classes.dto.UpdateSchoolRequest;
import pl.factorymethod.rada.classes.event.SchoolCreatedEvent;
import pl.factorymethod.rada.classes.event.SchoolUpdatedEvent;
import pl.factorymethod.rada.classes.repository.SchoolRepository;
import pl.factorymethod.rada.model.School;
import pl.factorymethod.rada.shared.events.EventPublisher;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public SchoolResponse createSchool(CreateSchoolRequest request) {
        schoolRepository.findByName(request.getName()).ifPresent(existing -> {
            throw new RuntimeException("School with name already exists: " + request.getName());
        });

        School school = new School();
        school.setPublicId(UUID.randomUUID());
        school.setName(request.getName());
        school.setAddress(request.getAddress());

        school = schoolRepository.save(school);

        eventPublisher.publish(new SchoolCreatedEvent(
                school.getPublicId(),
                school.getName(),
                school.getAddress(),
                LocalDateTime.now()));

        log.info("School created: publicId={}, name={}", school.getPublicId(), school.getName());

        return toResponse(school);
    }

    @Transactional(readOnly = true)
    public SchoolResponse getSchool(String schoolId) {
        UUID schoolPublicId = UUID.fromString(schoolId);
        School school = schoolRepository.findByPublicId(schoolPublicId)
                .orElseThrow(() -> new RuntimeException("School not found: " + schoolId));
        return toResponse(school);
    }

    @Transactional(readOnly = true)
    public List<SchoolResponse> listSchools() {
        return schoolRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SchoolResponse updateSchool(String schoolId, UpdateSchoolRequest request) {
        UUID schoolPublicId = UUID.fromString(schoolId);
        School school = schoolRepository.findByPublicId(schoolPublicId)
                .orElseThrow(() -> new RuntimeException("School not found: " + schoolId));

        schoolRepository.findByName(request.getName()).ifPresent(existing -> {
            if (!existing.getPublicId().equals(school.getPublicId())) {
                throw new RuntimeException("School with name already exists: " + request.getName());
            }
        });

        school.setName(request.getName());
        school.setAddress(request.getAddress());

        school = schoolRepository.save(school);

        eventPublisher.publish(new SchoolUpdatedEvent(
                school.getPublicId(),
                school.getName(),
                school.getAddress(),
                LocalDateTime.now()));

        log.info("School updated: publicId={}, name={}", school.getPublicId(), school.getName());

        return toResponse(school);
    }

    @Transactional
    public void deleteSchool(String schoolId) {
        UUID schoolPublicId = UUID.fromString(schoolId);
        School school = schoolRepository.findByPublicId(schoolPublicId)
                .orElseThrow(() -> new RuntimeException("School not found: " + schoolId));

        schoolRepository.delete(school);

        log.info("School deleted: publicId={}, name={}", school.getPublicId(), school.getName());
    }

    private SchoolResponse toResponse(School school) {
        return SchoolResponse.builder()
                .schoolId(school.getPublicId().toString())
                .name(school.getName())
                .address(school.getAddress())
                .build();
    }
}
