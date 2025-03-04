package hexlet.code.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LabelUpdateDTO {
    @Size(min = 3, max = 1000)
    private String name;
}
