package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


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
