package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskMapper taskMapper;

    private Task testTask;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();

        TaskStatus statusReview = new TaskStatus();
        statusReview.setName("To Review");
        statusReview.setSlug("to_review");
        taskStatusRepository.save(statusReview);

        TaskStatus statusBeFixed = new TaskStatus();
        statusBeFixed.setName("To Be Fixed");
        statusBeFixed.setSlug("to_be_fixed");
        taskStatusRepository.save(statusBeFixed);

        TaskStatus statusDraft = new TaskStatus();
        statusDraft.setName("Draft");
        statusDraft.setSlug("draft");
        taskStatusRepository.save(statusDraft);

        Task task = new Task();
        task.setIndex(100);
        task.setTitle("Initial Title");
        task.setContent("Initial Content");
        task.setTaskStatus(statusDraft);
        taskRepository.save(task);
        testTask = task;
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/tasks")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        var result = mockMvc.perform(get("/api/tasks/{id}", testTask.getId())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(testTask.getTitle()),
                v -> v.node("content").isEqualTo(testTask.getContent()),
                v -> v.node("status").isEqualTo(testTask.getTaskStatus().getSlug())
        );
    }

    @Test
    public void testCreate() throws Exception {
        TaskCreateDTO createDTO = new TaskCreateDTO();
        createDTO.setIndex(200);
        createDTO.setTitle("New Task");
        createDTO.setContent("New Task Content");
        createDTO.setStatus("to_review");

        createDTO.setAssigneeId(null);

        var request = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(createDTO))
                .with(SecurityMockMvcRequestPostProcessors.jwt());

        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        TaskDTO taskDTO = om.readValue(body, TaskDTO.class);
        assertThat(taskDTO.getTitle()).isEqualTo(createDTO.getTitle());
        assertThat(taskDTO.getContent()).isEqualTo(createDTO.getContent());
        assertThat(taskDTO.getStatus()).isEqualTo(createDTO.getStatus());
        assertThat(taskDTO.getIndex()).isEqualTo(createDTO.getIndex());

        var taskOptional = taskRepository.findById(taskDTO.getId());
        assertThat(taskOptional).isPresent();
    }

    @Test
    public void testUpdate() throws Exception {
        TaskUpdateDTO updateDTO = new TaskUpdateDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setContent("Updated Content");
        updateDTO.setStatus("to_be_fixed");

        var request = put("/api/tasks/{id}", testTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(updateDTO))
                .with(SecurityMockMvcRequestPostProcessors.jwt());

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        TaskDTO taskDTO = om.readValue(body, TaskDTO.class);
        assertThat(taskDTO.getTitle()).isEqualTo(updateDTO.getTitle());
        assertThat(taskDTO.getContent()).isEqualTo(updateDTO.getContent());
        assertThat(taskDTO.getStatus()).isEqualTo(updateDTO.getStatus());
    }

    @Test
    public void testDelete() throws Exception {
        var request = delete("/api/tasks/{id}", testTask.getId())
                .with(SecurityMockMvcRequestPostProcessors.jwt());
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        var taskOptional = taskRepository.findById(testTask.getId());
        assertThat(taskOptional).isEmpty();
    }

    @Test
    public void testIndexWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testShowWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", testTask.getId()))
                .andExpect(status().isUnauthorized());
    }
}