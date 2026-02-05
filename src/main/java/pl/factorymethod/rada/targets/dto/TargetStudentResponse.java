package pl.factorymethod.rada.targets.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TargetStudentResponse {

    private String studentId;
    private String number;
    private String firstName;
    private String lastName;
}
