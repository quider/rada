package pl.factorymethod.rada.classes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolResponse {

    private String schoolId;
    private String name;
    private String address;
}
