package pl.factorymethod.rada.classes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentNameResponse {

    private String firstName;
    private String lastName;
}
