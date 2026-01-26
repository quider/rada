package pl.factorymethod.rada.targets.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddStudentsToTargetRequest {
    
    @NotNull(message = "Target ID cannot be null")
    private String targetId;
    
    @NotEmpty(message = "Student IDs list cannot be empty")
    private List<String> studentIds;
}
