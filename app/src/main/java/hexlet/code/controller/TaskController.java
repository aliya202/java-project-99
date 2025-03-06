package hexlet.code.controller;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecifications;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    @GetMapping("/tasks")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskDTO> index(
            @RequestParam(name = "titleCont", required = false) String titleCont,
            @RequestParam(name = "assigneeId", required = false) Long assigneeId,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "labelId", required = false) Long labelId
    ) {
        Specification<Task> spec = Specification.where(null);
        if (titleCont != null && !titleCont.isEmpty()) {
            spec = spec.and(TaskSpecifications.titleContains(titleCont));
        }
        if (assigneeId != null) {
            spec = spec.and(TaskSpecifications.assigneeEquals(assigneeId));
        }
        if (status != null && !status.isEmpty()) {
            spec = spec.and(TaskSpecifications.statusEquals(status));
        }

        List<Task> tasks = taskRepository.findAll(spec);
        return tasks.stream().map(taskMapper::map).toList();
    }

    @GetMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO show(@PathVariable Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        return taskMapper.map(task);
    }

    @PostMapping("/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO create(@Valid @RequestBody TaskCreateDTO taskCreateDTO) {
        Task task = taskMapper.map(taskCreateDTO);
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    @PutMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO update(@RequestBody TaskUpdateDTO taskUpdateDTO, @PathVariable Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        taskMapper.update(taskUpdateDTO, task);
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        taskRepository.delete(task);
    }
}
