package hexlet.code.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskStatusUpdateDTO {
    @NotNull
    private String name;
    @NotNull
    private String slug;
}
