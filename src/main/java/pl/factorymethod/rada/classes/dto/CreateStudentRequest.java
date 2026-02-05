package pl.factorymethod.rada.classes.dto;

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
public class CreateStudentRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 64, message = "First name must not exceed 64 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 64, message = "Last name must not exceed 64 characters")
    private String lastName;
}
