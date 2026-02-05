package pl.factorymethod.rada.classes.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClassStudentsRequest {

    @NotEmpty(message = "Students list cannot be empty")
    @Valid
    private List<CreateStudentRequest> students;
}
