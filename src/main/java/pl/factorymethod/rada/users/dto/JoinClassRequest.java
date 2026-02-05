package pl.factorymethod.rada.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinClassRequest {

    @NotBlank(message = "Join code is required")
    @Size(max = 64, message = "Join code must not exceed 64 characters")
    private String joinCode;
}
