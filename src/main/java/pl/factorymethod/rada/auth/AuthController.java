package pl.factorymethod.rada.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import pl.factorymethod.rada.auth.dto.LoginRequest;
import pl.factorymethod.rada.auth.dto.LoginResponse;
import pl.factorymethod.rada.auth.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Get user info from token",
            description = "Get authenticated user information from JWT token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - no valid JWT token",
                    content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<LoginResponse> getCurrentUser() {
        log.info("Get current user info");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt");
            return ResponseEntity.status(401).build();
        }
        
        LoginResponse response = authService.provisionUser(authentication);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Provision/sync user",
            description = "Sync user from Keycloak with Rada database"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User provisioned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - no valid JWT token",
                    content = @Content)
    })
    @PostMapping("/provision")
    public ResponseEntity<Void> provisionUser() {
        log.info("Provision user endpoint called");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to provision endpoint");
            return ResponseEntity.status(401).build();
        }
        
        authService.provisionUser(authentication);
        return ResponseEntity.ok().build();
    }
}
 