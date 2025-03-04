package hexlet.code.controller;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UsersController {

    @Autowired
    private UserRepository repository;


    @GetMapping("/users")
    List<User> index() {
        return repository.findAll();
    }

    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    User show(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not Found"));
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    User create(@Valid @RequestBody User userData) {
        userData.setPassword(hashPassword(userData.getPassword()));
        repository.save(userData);
        return userData;
    }

    @PutMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    User update(@RequestBody Map<String, Object> updates, @PathVariable Long id) {
        var user = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found"));
        if (updates.containsKey("email")) {
            user.setEmail((String) updates.get("email"));
        }
        if (updates.containsKey("firstName")) {
            user.setFirstName((String) updates.get("firstName"));
        }
        if (updates.containsKey("lastName")) {
            user.setLastName((String) updates.get("lastName"));
        }
        if (updates.containsKey("password")) {
            user.setPassword(hashPassword((String) updates.get("password")));
        }

        repository.save(user);
        return user;
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found"));
        repository.delete(user);
    }

    private String hashPassword(String password) {
        return "hashed_" + password;
    }


}
