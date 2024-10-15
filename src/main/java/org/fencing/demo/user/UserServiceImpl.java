package org.fencing.demo.user;

import org.apache.hc.client5.http.auth.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @Transactional
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
    @Transactional
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
