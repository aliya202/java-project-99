package hexlet.code.dto;

import hexlet.code.model.Task;
import hexlet.code.model.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TaskUpdateDTO {
    private Long assigneeId;
    private String title;
    private String content;
    private String status;
}