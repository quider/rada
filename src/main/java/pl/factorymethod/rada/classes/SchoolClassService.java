package pl.factorymethod.rada.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.factorymethod.rada.classes.dto.CreateClassStudentsRequest;
import pl.factorymethod.rada.classes.dto.CreateSchoolClassRequest;
import pl.factorymethod.rada.classes.dto.CreateStudentRequest;
import pl.factorymethod.rada.classes.dto.MoveStudentRequest;
import pl.factorymethod.rada.classes.dto.SchoolClassResponse;
import pl.factorymethod.rada.classes.dto.StudentNameResponse;
import pl.factorymethod.rada.classes.repository.SchoolClassRepository;
import pl.factorymethod.rada.classes.repository.SchoolRepository;
import pl.factorymethod.rada.model.School;
import pl.factorymethod.rada.model.SchoolClass;
import pl.factorymethod.rada.model.Student;
import pl.factorymethod.rada.targets.repository.StudentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchoolClassService {

    private final SchoolRepository schoolRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public SchoolClassResponse createClass(CreateSchoolClassRequest request) {
        UUID schoolPublicId = UUID.fromString(request.getSchoolId());
        School school = schoolRepository.findByPublicId(schoolPublicId)
                .orElseThrow(() -> new RuntimeException("School not found: " + request.getSchoolId()));

        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setPublicId(UUID.randomUUID());
        schoolClass.setName(request.getName());
        schoolClass.setStartYear(request.getStartYear());
        schoolClass.setDescription(request.getDescription());
        schoolClass.setSchool(school);

        schoolClass = schoolClassRepository.save(schoolClass);

        log.info("School class created: publicId={}, schoolId={}, name={}",
                schoolClass.getPublicId(), school.getPublicId(), schoolClass.getName());

        return SchoolClassResponse.builder()
                .classId(schoolClass.getPublicId().toString())
                .name(schoolClass.getName())
                .startYear(schoolClass.getStartYear())
                .description(schoolClass.getDescription())
                .schoolId(school.getPublicId().toString())
                .build();
    }

    @Transactional
    public List<StudentNameResponse> addStudentsToClass(String classId, CreateClassStudentsRequest request) {
        UUID classPublicId = UUID.fromString(classId);
        SchoolClass schoolClass = schoolClassRepository.findByPublicId(classPublicId)
                .orElseThrow(() -> new RuntimeException("Class not found: " + classId));

        long existingCount = studentRepository.countBySchoolClass(schoolClass);
        int startNumber = (int) existingCount + 1;

        List<Student> studentsToSave = new ArrayList<>(request.getStudents().size());
        int index = 0;
        for (CreateStudentRequest studentRequest : request.getStudents()) {
            Student student = new Student();
            student.setPublicId(UUID.randomUUID());
            student.setSchoolClass(schoolClass);
            student.setNumber(String.valueOf(startNumber + index));
            student.setFirstName(studentRequest.getFirstName());
            student.setLastName(studentRequest.getLastName());
            studentsToSave.add(student);
            index++;
        }

        List<Student> savedStudents = studentRepository.saveAll(studentsToSave);

        log.info("Added {} students to class {} (from number {})",
                savedStudents.size(), schoolClass.getPublicId(), startNumber);

        List<StudentNameResponse> responses = new ArrayList<>(savedStudents.size());
        for (Student student : savedStudents) {
            responses.add(StudentNameResponse.builder()
                    .firstName(student.getFirstName())
                    .lastName(student.getLastName())
                    .build());
        }
        return responses;
    }

    @Transactional
    public void moveStudent(String sourceClassId, String studentId, MoveStudentRequest request) {
        UUID sourceClassPublicId = UUID.fromString(sourceClassId);
        UUID studentPublicId = UUID.fromString(studentId);
        UUID targetClassPublicId = UUID.fromString(request.getTargetClassId());

        SchoolClass sourceClass = schoolClassRepository.findByPublicId(sourceClassPublicId)
                .orElseThrow(() -> new RuntimeException("Class not found: " + sourceClassId));
        SchoolClass targetClass = schoolClassRepository.findByPublicId(targetClassPublicId)
                .orElseThrow(() -> new RuntimeException("Class not found: " + request.getTargetClassId()));

        Student student = studentRepository.findByPublicId(studentPublicId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

        if (student.getSchoolClass() == null || student.getSchoolClass().getPublicId() == null) {
            throw new RuntimeException("Student not assigned to class: " + studentId);
        }
        if (!student.getSchoolClass().getPublicId().equals(sourceClass.getPublicId())) {
            throw new RuntimeException("Student not in class: " + sourceClassId);
        }

        student.setSchoolClass(targetClass);
        studentRepository.save(student);

        renumberClass(sourceClass);
        renumberClass(targetClass);

        log.info("Moved student {} from class {} to class {}", studentPublicId, sourceClassPublicId, targetClassPublicId);
    }

    private void renumberClass(SchoolClass schoolClass) {
        List<Student> students = studentRepository.findBySchoolClassOrderByLastNameAscFirstNameAsc(schoolClass);
        int number = 1;
        for (Student student : students) {
            student.setNumber(String.valueOf(number));
            number++;
        }
        studentRepository.saveAll(students);
    }
}
