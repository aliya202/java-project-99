package hexlet.code.mapper;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {

    @Autowired
    protected TaskStatusRepository taskStatusRepository;

    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "mapStatus")
    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "mapUser")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "assigneeId", source = "assignee.id")
    public abstract TaskDTO map(Task task);

    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "mapStatus")
    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "mapUser")
    public abstract void update(TaskUpdateDTO update, @MappingTarget Task task);

    @Named("mapStatus")
    protected TaskStatus mapStatus(String slug) {
        if (slug == null) {
            return null;
        }
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("TaskStatus with slug " + slug + " not found"));
    }

    @Named("mapUser")
    protected User mapUser(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}