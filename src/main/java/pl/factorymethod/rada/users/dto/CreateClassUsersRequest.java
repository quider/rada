package pl.factorymethod.rada.users.dto;

import java.util.List;

import jakarta.validation.Valid;
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
public class CreateClassUsersRequest {

    @NotNull(message = "Class ID is required")
    private String classId;

    @NotEmpty(message = "Users list cannot be empty")
    @Valid
    private List<CreateClassUserRequest> users;
}
