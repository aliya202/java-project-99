package hexlet.code.dto;

import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.IDENTITY;


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