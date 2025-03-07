package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
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

@SpringBootTest(properties = {"data.initializer.enabled=false"})

@AutoConfigureMockMvc
public class TaskStatusControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    private TaskStatus taskStatus;

    @BeforeEach
    public void setUp() {
        // Initialize MockMvc with Spring Security support
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
        // Clear dependent repositories (but do not clear taskStatusRepository if your DataInitializer might already populate it)
        userRepository.deleteAll();
        labelRepository.deleteAll();
        // Instead of clearing taskStatusRepository, check if a TaskStatus with slug "to_review" exists
        taskStatus = taskStatusRepository.findBySlug("to_review")
                .orElseGet(() -> {
                    TaskStatus ts = new TaskStatus();
                    ts.setName("ToReview");
                    ts.setSlug("to_review");
                    return taskStatusRepository.save(ts);
                });
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/task_statuses")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        var result = mockMvc.perform(get("/api/task_statuses/{id}", taskStatus.getId())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(taskStatus.getName()),
                v -> v.node("slug").isEqualTo(taskStatus.getSlug())
        );
    }

    @Test
    public void testCreate() throws Exception {
        TaskStatusCreateDTO createDTO = new TaskStatusCreateDTO();
        createDTO.setName("Draft");
        createDTO.setSlug("draft");

        var request = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(createDTO))
                .with(SecurityMockMvcRequestPostProcessors.jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        TaskStatusDTO taskStatusDTO = om.readValue(body, TaskStatusDTO.class);
        assertThat(taskStatusDTO.getName()).isEqualTo(createDTO.getName());
        assertThat(taskStatusDTO.getSlug()).isEqualTo(createDTO.getSlug());

        var taskStatusOptional = taskStatusRepository.findBySlug(createDTO.getSlug());
        assertThat(taskStatusOptional).isPresent();
    }

    @Test
    public void testUpdate() throws Exception {
        TaskStatusUpdateDTO updateDTO = new TaskStatusUpdateDTO();
        updateDTO.setName("ToReview");
        updateDTO.setSlug("to_review");

        var request = put("/api/task_statuses/{id}", taskStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(updateDTO))
                .with(SecurityMockMvcRequestPostProcessors.jwt());

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        TaskStatusDTO updatedStatus = om.readValue(body, TaskStatusDTO.class);
        assertThat(updatedStatus.getName()).isEqualTo(updateDTO.getName());
        assertThat(updatedStatus.getSlug()).isEqualTo(updateDTO.getSlug());
    }

    @Test
    public void testDelete() throws Exception {
        var request = delete("/api/task_statuses/{id}", taskStatus.getId())
                .with(SecurityMockMvcRequestPostProcessors.jwt());
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        var taskStatusOptional = taskStatusRepository.findBySlug(taskStatus.getSlug());
        assertThat(taskStatusOptional).isEmpty();
    }

    @Test
    public void testIndexWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/task_statuses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testShowWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/task_statuses/{id}", taskStatus.getId()))
                .andExpect(status().isUnauthorized());
    }
}