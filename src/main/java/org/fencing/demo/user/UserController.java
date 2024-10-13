package org.fencing.demo.user;

import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
@RestController
public class UserController {
    private UserService userService; 

    public UserController(UserService us) {
        this.userService = us;  
    }

    // List all users 
    @GetMapping("/users")
    public List<User> listUsers() {  
        return userService.listUsers();  
    }

    // Search for user by ID, else throw UserNotFoundException
    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {  
        User user = userService.getUser(id); 

        if (user == null) {
            throw new UserNotFoundException(id);  
        }
        return user;
    }

    // Add new user 
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {  
        return userService.addUser(user);  
    }

    // Updates user info 
    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable Long id, @Valid @RequestBody User updatedUserInfo) {
        User user = userService.updateUser(id, updatedUserInfo);  
        if (user == null) {
            throw new UserNotFoundException(id);  
        }
        return user;
    }

    // Deletes user 
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);  
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(id);  
        }
    }
}
