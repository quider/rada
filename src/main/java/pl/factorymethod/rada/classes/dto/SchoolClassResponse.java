package pl.factorymethod.rada.classes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolClassResponse {

    private String classId;
    private String name;
    private Integer startYear;
    private String description;
    private String schoolId;
}
