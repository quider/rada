package pl.factorymethod.rada.targets;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.factorymethod.rada.targets.dto.AddStudentsToTargetRequest;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/targets")
@RequiredArgsConstructor
@Tag(name = "Targets", description = "Target management APIs")
public class TargetController {

    private final TargetService targetService;

    @Operation(
            summary = "Add students to target",
            description = "Assign multiple students to a specific target"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Students successfully added to target"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Target or student not found", content = @Content)
    })
    @PostMapping("/students")
    public ResponseEntity<Void> addStudentsToTarget(@Valid @RequestBody AddStudentsToTargetRequest request) {
        log.info("Adding {} students to target: {}", request.getStudentIds().size(), request.getTargetId());
        targetService.addStudentsToTarget(request);
        return ResponseEntity.ok().build();
    }
}
