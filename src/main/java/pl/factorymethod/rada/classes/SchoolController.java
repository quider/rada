package pl.factorymethod.rada.classes;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import pl.factorymethod.rada.classes.dto.CreateSchoolRequest;
import pl.factorymethod.rada.classes.dto.SchoolResponse;
import pl.factorymethod.rada.classes.dto.UpdateSchoolRequest;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/schools")
@RequiredArgsConstructor
@Tag(name = "Schools", description = "School management APIs")
public class SchoolController {

    private final SchoolService schoolService;

    @Operation(summary = "Create school", description = "Create a new school")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "School created successfully",
                    content = @Content(schema = @Schema(implementation = SchoolResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "409", description = "School already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<SchoolResponse> createSchool(@Valid @RequestBody CreateSchoolRequest request) {
        log.info("Create school request received: name={}", request.getName());
        return ResponseEntity.ok(schoolService.createSchool(request));
    }

    @Operation(summary = "Get school", description = "Get a school by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "School retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SchoolResponse.class))),
            @ApiResponse(responseCode = "404", description = "School not found", content = @Content)
    })
    @GetMapping("/{schoolId}")
    public ResponseEntity<SchoolResponse> getSchool(@PathVariable String schoolId) {
        log.info("Get school request received: schoolId={}", schoolId);
        return ResponseEntity.ok(schoolService.getSchool(schoolId));
    }

    @Operation(summary = "List schools", description = "List all schools")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Schools retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SchoolResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<SchoolResponse>> listSchools() {
        log.info("List schools request received");
        return ResponseEntity.ok(schoolService.listSchools());
    }

    @Operation(summary = "Update school", description = "Update an existing school")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "School updated successfully",
                    content = @Content(schema = @Schema(implementation = SchoolResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "School not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "School already exists", content = @Content)
    })
    @PutMapping("/{schoolId}")
    public ResponseEntity<SchoolResponse> updateSchool(
            @PathVariable String schoolId,
            @Valid @RequestBody UpdateSchoolRequest request) {
        log.info("Update school request received: schoolId={}, name={}", schoolId, request.getName());
        return ResponseEntity.ok(schoolService.updateSchool(schoolId, request));
    }

    @Operation(summary = "Delete school", description = "Delete an existing school")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "School deleted successfully"),
            @ApiResponse(responseCode = "404", description = "School not found", content = @Content)
    })
    @DeleteMapping("/{schoolId}")
    public ResponseEntity<Void> deleteSchool(@PathVariable String schoolId) {
        log.info("Delete school request received: schoolId={}", schoolId);
        schoolService.deleteSchool(schoolId);
        return ResponseEntity.noContent().build();
    }
}
