package pl.factorymethod.rada.classes.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoveStudentRequest {

    @NotBlank(message = "Target class ID is required")
    private String targetClassId;
}
