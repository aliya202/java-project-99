package hexlet.code.dto;

import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class TaskCreateDTO {
    private long index;

    private Long assigneeId;

    @NotBlank(message = "Title must not be blank")
    private String title;

    private String content;

    @NotBlank(message = "Status must not be blank")
    private String status;
}