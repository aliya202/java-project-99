package hexlet.code.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@Table(name = "tasks")
@ToString(includeFieldNames = true, onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class Task implements BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @ToString.Include
    @EqualsAndHashCode.Include
    private long id;

    private long index;

    @NotBlank(message = "Title must not be blank")
    private String title;

    private String content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "task_status_id")
    private TaskStatus taskStatus;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @CreatedDate
    private LocalDate createdAt;

    @ManyToMany
    @JoinTable(
            name = "task_labels",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private Set<Label> labels = new HashSet<>();
}
