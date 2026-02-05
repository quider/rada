package pl.factorymethod.rada.classes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSchoolClassRequest {

    @NotBlank(message = "School ID is required")
    private String schoolId;

    @NotBlank(message = "Class name is required")
    private String name;

    @NotNull(message = "Start year is required")
    private Integer startYear;

    private String description;
}
