package pl.factorymethod.rada.targets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import org.springframework.security.access.prepost.PreAuthorize;
import pl.factorymethod.rada.targets.dto.AddStudentsToTargetRequest;
import pl.factorymethod.rada.targets.dto.CreateTargetRequest;
import pl.factorymethod.rada.targets.dto.TargetResponse;

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
                        @ApiResponse(responseCode = "200", description = "Target created successfully",
                                        content = @Content(schema = @Schema(implementation = TargetResponse.class))),
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
                        @PathVariable String targetId,
                        @RequestHeader(value = "X-Rada-Admin-Token", required = false) String providedToken) {
                if (providedToken == null || !providedToken.equals(adminToken)) {
                        log.warn("Unauthorized attempt to open contribution collection for target {}", targetId);
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }

                log.info("Opening contribution collection for target: {}", targetId);
                targetService.openContributionCollection(targetId);
                return ResponseEntity.ok().build();
        }
}
