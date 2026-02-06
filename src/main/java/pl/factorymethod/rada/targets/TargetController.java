package pl.factorymethod.rada.targets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
import pl.factorymethod.rada.targets.dto.AddStudentsToTargetRequest;
import pl.factorymethod.rada.targets.dto.CreateTargetRequest;
import pl.factorymethod.rada.targets.dto.TargetResponse;
import pl.factorymethod.rada.targets.dto.TargetSummaryResponse;
import pl.factorymethod.rada.targets.dto.TargetStudentResponse;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/targets")
@RequiredArgsConstructor
@Tag(name = "Targets", description = "Target management APIs")
public class TargetController {

        private final TargetService targetService;

        @Value("${rada.admin-token:dev-admin-token}")
        private String adminToken;

        @Operation(summary = "Create target", description = "Create a new target")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Target created successfully", content = @Content(schema = @Schema(implementation = TargetResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
        })
        @PostMapping
        // @PreAuthorize("hasAuthority('create_target')")
        public ResponseEntity<TargetResponse> createTarget(@Valid @RequestBody CreateTargetRequest request) {
                log.info("Create target request received: dueTo={}, estimatedValue={}",
                                request.getDueTo(), request.getEstimatedValue());
                TargetResponse response = targetService.createTarget(request);
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Add students to target", description = "Assign multiple students to a specific target")
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

        @Operation(summary = "Open contribution collection", description = "Freeze student fees for a target and emit a domain event")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Contribution collection opened"),
                        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Target not found", content = @Content)
        })
        @PostMapping("/{targetId}/contributions/open")
        public ResponseEntity<Void> openContributionCollection(
                        @PathVariable String targetId) {
                log.info("Opening contribution collection for target: {}", targetId);
                targetService.openContributionCollection(targetId);
                return ResponseEntity.ok().build();
        }

        @Operation(summary = "Get targets", description = "List all targets")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Targets retrieved successfully", content = @Content(schema = @Schema(implementation = TargetSummaryResponse.class)))
        })
        @GetMapping
        public ResponseEntity<java.util.List<TargetSummaryResponse>> getTargets() {
                return ResponseEntity.ok(targetService.getTargets());
        }

        @Operation(summary = "Get target", description = "Get target details by public ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Target retrieved successfully", content = @Content(schema = @Schema(implementation = TargetSummaryResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Target not found", content = @Content)
        })
        @GetMapping("/{targetId}")
        public ResponseEntity<TargetSummaryResponse> getTarget(@PathVariable String targetId) {
                return ResponseEntity.ok(targetService.getTarget(targetId));
        }

        @Operation(summary = "Get target students", description = "List students assigned to a target")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Target students retrieved successfully", content = @Content(schema = @Schema(implementation = TargetStudentResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Target not found", content = @Content)
        })
        @GetMapping("/{targetId}/students")
        public ResponseEntity<java.util.List<TargetStudentResponse>> getTargetStudents(@PathVariable String targetId) {
                return ResponseEntity.ok(targetService.getTargetStudents(targetId));
        }

        @Operation(summary = "Get targets by class", description = "List targets for a specific class")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Targets retrieved successfully", content = @Content(schema = @Schema(implementation = TargetSummaryResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Class not found", content = @Content)
        })
        @GetMapping("/by-class/{classId}")
        public ResponseEntity<java.util.List<TargetSummaryResponse>> getTargetsByClass(@PathVariable String classId) {
                return ResponseEntity.ok(targetService.getTargetsByClass(classId));
        }
}
