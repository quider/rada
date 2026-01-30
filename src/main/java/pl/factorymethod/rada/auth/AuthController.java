package pl.factorymethod.rada.auth;

import org.springframework.http.ResponseEntity;
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
            summary = "User login",
            description = "Authenticate a user with email and password"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)
    })
    @PostMapping("/provision")
    public ResponseEntity<Void> login() {
        //todo: implement a logic which gets userId from jwt and retrive all data from keycloak about this user
        //at the same time the same user is matched in rada database by id or its created if not existing.
        //This user is needed to keep relations correctness in rada database
        return ResponseEntity.ok().build();
    }
}
