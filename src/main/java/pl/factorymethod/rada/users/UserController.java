package pl.factorymethod.rada.users;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import pl.factorymethod.rada.users.dto.CreateUserRequest;
import pl.factorymethod.rada.users.dto.CreateClassUsersRequest;
import pl.factorymethod.rada.users.dto.UserResponse;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Create user",
            description = "Create a new user with encrypted data key (DEK) generation"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "409", description = "User already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Create user request received: email={}", request.getEmail());
        UserResponse response = userService.createUser(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Create users for class",
            description = "Create multiple users for a specific class and assign them to students"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users created successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Class or student not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "User already exists", content = @Content)
    })
    @PostMapping("/classes")
    public ResponseEntity<List<UserResponse>> createUsersForClass(
            @Valid @RequestBody CreateClassUsersRequest request) {
        log.info("Create users for class request received: classId={}, count={}",
                request.getClassId(), request.getUsers().size());
        return ResponseEntity.ok(userService.createUsersForClass(request));
    }
}
