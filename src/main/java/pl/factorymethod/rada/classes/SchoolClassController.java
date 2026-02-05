package pl.factorymethod.rada.classes;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.factorymethod.rada.classes.dto.CreateClassStudentsRequest;
import pl.factorymethod.rada.classes.dto.CreateSchoolClassRequest;
import pl.factorymethod.rada.classes.dto.MoveStudentRequest;
import pl.factorymethod.rada.classes.dto.SchoolClassResponse;
import pl.factorymethod.rada.classes.dto.StudentNameResponse;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/classes")
@RequiredArgsConstructor
@Tag(name = "Classes", description = "School class management APIs")
public class SchoolClassController {

    private final SchoolClassService schoolClassService;

    @Operation(
            summary = "Create school class",
            description = "Create a new class within a school"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class created successfully",
                    content = @Content(schema = @Schema(implementation = SchoolClassResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "School not found", content = @Content)
    })
    @PostMapping
    public ResponseEntity<SchoolClassResponse> createClass(@Valid @RequestBody CreateSchoolClassRequest request) {
        log.info("Create class request received: schoolId={}, name={}", request.getSchoolId(), request.getName());
        return ResponseEntity.ok(schoolClassService.createClass(request));
    }

    @Operation(
            summary = "Add students to class",
            description = "Add students in bulk to a class and assign sequential numbers"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Students added successfully",
                    content = @Content(schema = @Schema(implementation = StudentNameResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Class not found", content = @Content)
    })
    @PostMapping("/{classId}/students")
    public ResponseEntity<List<StudentNameResponse>> addStudentsToClass(
            @PathVariable String classId,
            @Valid @RequestBody CreateClassStudentsRequest request) {
        log.info("Add students request received: classId={}, count={}", classId, request.getStudents().size());
        return ResponseEntity.ok(schoolClassService.addStudentsToClass(classId, request));
    }

    @Operation(
            summary = "Move student to another class",
            description = "Move a student to another class and renumber both classes alphabetically"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student moved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Class or student not found", content = @Content)
    })
    @PostMapping("/{classId}/students/{studentId}/move")
    public ResponseEntity<Void> moveStudent(
            @PathVariable String classId,
            @PathVariable String studentId,
            @Valid @RequestBody MoveStudentRequest request) {
        log.info("Move student request received: studentId={}, fromClassId={}, toClassId={}",
                studentId, classId, request.getTargetClassId());
        schoolClassService.moveStudent(classId, studentId, request);
        return ResponseEntity.ok().build();
    }
}
