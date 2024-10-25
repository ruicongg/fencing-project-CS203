package org.fencing.demo.user;
import java.util.List;

import org.apache.hc.client5.http.auth.InvalidCredentialsException;

public interface UserService {
    List<User> listUsers();
    
    User getUser(Long id);

    // Add a new user  - not needed for now
    //User addUser(User user);

    // Update user info 
    User updateUser(Long id, User user);

    // Delete a user 
    void deleteUser(Long id);

    // Login method 
    User login(String username, String password) throws InvalidCredentialsException;
}
