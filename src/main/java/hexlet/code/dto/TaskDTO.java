package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class TaskDTO {
    private long id;
    private long index;
    private String title;
    private String content;
    private String status;
    private Long assigneeId;
    private LocalDate createdAt;
}
