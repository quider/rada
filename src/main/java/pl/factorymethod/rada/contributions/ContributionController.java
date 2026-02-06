package pl.factorymethod.rada.contributions;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
import pl.factorymethod.rada.contributions.dto.ContributionResponse;
import pl.factorymethod.rada.contributions.dto.CreateContributionRequest;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/contributions")
@RequiredArgsConstructor
@Tag(name = "Contributions", description = "Contribution management APIs")
public class ContributionController {

    private final ContributionService contributionService;

    @Operation(summary = "Create contribution", description = "Create a new contribution payment for a target. " +
            "Fees must be frozen (calculated) before contributions can be collected. " +
            "Platform commission and operator fee are calculated based on provided rates.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contribution created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or fees not frozen", content = @Content),
            @ApiResponse(responseCode = "404", description = "Target or student not found", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Void> createContribution(@Valid @RequestBody CreateContributionRequest request) {
        log.info("Create contribution request received: targetId={}, studentId={}, value={}",
                request.getTargetId(), request.getStudentId(), request.getValue());
        contributionService.createContribution(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get contributions by target", description = "List all contributions for a specific target")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contributions retrieved successfully", content = @Content(schema = @Schema(implementation = ContributionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Target not found", content = @Content)
    })
    @GetMapping("/by-target/{targetId}")
    public ResponseEntity<List<ContributionResponse>> getContributionsByTarget(@PathVariable String targetId) {
        log.info("Get contributions by target request received: targetId={}", targetId);
        return ResponseEntity.ok(contributionService.getContributionsByTarget(targetId));
    }

    @Operation(summary = "Get contributions by student", description = "List all contributions for a specific student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contributions retrieved successfully", content = @Content(schema = @Schema(implementation = ContributionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Student not found", content = @Content)
    })
    @GetMapping("/by-student/{studentId}")
    public ResponseEntity<List<ContributionResponse>> getContributionsByStudent(@PathVariable String studentId) {
        log.info("Get contributions by student request received: studentId={}", studentId);
        return ResponseEntity.ok(contributionService.getContributionsByStudent(studentId));
    }
}
