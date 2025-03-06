package hexlet.code.component;


import hexlet.code.dto.UserCreateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {


    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserMapper userMapper;

    @Autowired
    private final TaskStatusRepository taskStatusRepository;
    @Autowired
    private final LabelRepository labelRepository;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        var userData = new UserCreateDTO();
        userData.setFirstName("hexlet");
        userData.setEmail("hexlet@example.com");
        userData.setPassword("123");
        var user = userMapper.map(userData);
        userRepository.save(user);
        String[][] defaultStatuses = {
                {"Draft", "draft"},
                {"To Review", "to_review"},
                {"To Be Fixed", "to_be_fixed"},
                {"To Publish", "to_publish"},
                {"Published", "published"}
        };


        for (String[] statusData : defaultStatuses) {
            String name = statusData[0];
            String slug = statusData[1];
            if (taskStatusRepository.findBySlug(slug).isEmpty()) {
                TaskStatus status = new TaskStatus();
                status.setName(name);
                status.setSlug(slug);
                taskStatusRepository.save(status);
            }
        }

        String[] defaultLabels = {"feature", "bug"};

        for (String defaultLabel : defaultLabels) {
            if (labelRepository.findByName(defaultLabel).isEmpty()) {
                Label label = new Label();
                label.setName(defaultLabel);
                labelRepository.save(label);
            }
        }

    }
}
