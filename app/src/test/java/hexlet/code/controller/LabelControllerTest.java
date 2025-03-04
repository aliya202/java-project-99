package hexlet.code.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
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
public class LabelControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    private Label testLabel;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        taskRepository.deleteAll();
        labelRepository.deleteAll();
        taskStatusRepository.deleteAll();

        TaskStatus status = new TaskStatus();
        status.setName("Draft");
        status.setSlug("draft");
        status = taskStatusRepository.save(status);

        Label label = new Label();
        label.setName("Bug");
        label = labelRepository.save(label);
        testLabel = label;
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/labels")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        var result = mockMvc.perform(get("/api/labels/{id}", testLabel.getId())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body).node("name").isEqualTo(testLabel.getName());
    }

    @Test
    public void testCreate() throws Exception {
        LabelCreateDTO createDTO = new LabelCreateDTO();
        createDTO.setName("Feature");

        var request = post("/api/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(createDTO))
                .with(SecurityMockMvcRequestPostProcessors.jwt());

        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        LabelDTO labelDTO = om.readValue(body, LabelDTO.class);
        assertThat(labelDTO.getName()).isEqualTo(createDTO.getName());

        var labelOptional = labelRepository.findById(labelDTO.getId());
        assertThat(labelOptional).isPresent();
    }

    @Test
    public void testUpdate() throws Exception {
        LabelUpdateDTO updateDTO = new LabelUpdateDTO();
        updateDTO.setName("Bug Updated");

        var request = put("/api/labels/{id}", testLabel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(updateDTO))
                .with(SecurityMockMvcRequestPostProcessors.jwt());

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        LabelDTO labelDTO = om.readValue(body, LabelDTO.class);
        assertThat(labelDTO.getName()).isEqualTo(updateDTO.getName());
    }

    @Test
    public void testDelete() throws Exception {
        Label labelToDelete = new Label();
        labelToDelete.setName("ToDelete");
        labelToDelete = labelRepository.save(labelToDelete);

        var request = delete("/api/labels/{id}", labelToDelete.getId())
                .with(SecurityMockMvcRequestPostProcessors.jwt());
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        var labelOptional = labelRepository.findById(labelToDelete.getId());
        assertThat(labelOptional).isEmpty();
    }

    @Test
    public void testDeleteLabelAssociatedWithTask() throws Exception {
        Label labelLinked = new Label();
        labelLinked.setName("LinkedLabel");
        labelLinked = labelRepository.save(labelLinked);

        TaskStatus status = new TaskStatus();
        status.setName("Draft");
        status.setSlug("draft");
        status = taskStatusRepository.save(status);

        Task task = new Task();
        task.setIndex(123);
        task.setTitle("Task with Label");
        task.setContent("Some content");
        task.setTaskStatus(status);
        task.getLabels().add(labelLinked);
        taskRepository.save(task);

        var request = delete("/api/labels/{id}", labelLinked.getId())
                .with(SecurityMockMvcRequestPostProcessors.jwt());
        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    public void testAccessWithoutAuth() throws Exception {
        LabelCreateDTO createDTO = new LabelCreateDTO();
        createDTO.setName("NoAuthLabel");

        mockMvc.perform(post("/api/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(createDTO)))
                .andExpect(status().isUnauthorized());
    }
}