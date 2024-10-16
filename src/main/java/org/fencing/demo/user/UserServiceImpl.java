package org.fencing.demo.user;

import org.apache.hc.client5.http.auth.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }   

    @Override
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @Override  
    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Not Needed for now
    // @Override
    // public User addUser(User user) {
    //     Optional<User> sameUsers = userRepository.findByUsername(user.getUsername());
    //     if (!sameUsers.isPresent()) {
    //         return userRepository.save(user);
    //     }
    //     else {
    //         return null;
    //     }
    // }

    @Override
    public User updateUser(Long id, User user) {
        // Find the existing user by id
        Optional<User> existingUser = userRepository.findById(id);

        if (existingUser.isPresent()) {
            User updatedUser = existingUser.get();

            // Update the fields of the existing user with the new user data
            updatedUser.setUsername(user.getUsername());
            updatedUser.setPassword(passwordEncoder.encode(user.getPassword()));
            updatedUser.setEmail(user.getEmail());

            // Save the updated user
            return userRepository.save(updatedUser);
        } else {
            throw new UserNotFoundException(id);
        }
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.delete(user.get());
        } else {
            throw new UserNotFoundException(id);
        }
    }

    @Override
    public User login(String username, String password) throws InvalidCredentialsException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            if (passwordEncoder.matches(password, user.get().getPassword())) {
                return user.get();
            } else {
                throw new InvalidCredentialsException("Invalid password");
            }
        } else {
            throw new InvalidCredentialsException("Invalid username");
        }
    }

}
