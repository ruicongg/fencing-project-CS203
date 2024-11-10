package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.util.Optional;

import org.fencing.demo.user.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserIntegrationTest {
    @LocalServerPort
	private int port;

	private final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User adminUser;

    @BeforeEach
    void setUp() {
        // Create an admin user
        userRepository.deleteAll();
        adminUser = new User("admin", passwordEncoder.encode("adminPass"), "admin@example.com", Role.ADMIN);
        userRepository.save(adminUser);
    }

    @AfterEach
    void teardown(){
        userRepository.deleteAll();
    }

    @Test
    public void GetUsers_Success() throws Exception {
        userRepository.save(new User("user2", "password", "user@example.com", Role.USER));
        URI uri = new URI(baseUrl + port + "/users");

        ResponseEntity<User[]> response = restTemplate.withBasicAuth("admin", "adminPass").getForEntity(uri, User[].class);
        User[] users = response.getBody();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, users.length); //2 because we have the admin user and the user we just added
    }


    @Test
    public void GetUser_Success() throws Exception {
        User user = userRepository.save(new User("user1", "password", "user@email.com", Role.USER));
        Long id = user.getId();
        URI uri = new URI(baseUrl + port + "/users/" + id);

        ResponseEntity<User> response = restTemplate.withBasicAuth("admin", "adminPass").getForEntity(uri, User.class);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(user.getId(), response.getBody().getId());
    }

    @Test
    public void getUser_InvalidId_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/users/1");

        ResponseEntity<User> response = restTemplate.withBasicAuth("admin", "adminPass").getForEntity(uri, User.class);

        assertEquals(404, response.getStatusCode().value());
    }

    // Test for POST user
    // @Test
    // public void addUser_Success() throws Exception {
    //     URI uri = new URI(baseUrl + port + "/users");
    //     User user = new User("user1", "password", "test@email.com", Role.USER);

    //     ResponseEntity<User> response = restTemplate.withBasicAuth("admin", "adminPass").postForEntity(uri, user, User.class);
    //     User newUser = response.getBody();

    //     assertEquals(201, response.getStatusCode().value());
    //     assertEquals("user1", newUser.getUsername());
    // }

     @Test
    public void updateUser_Success() throws Exception {
        User user =  userRepository.save(new User("user2", "password", "user@example.com", Role.USER));
        Long id = user.getId().longValue();
        URI uri = new URI(baseUrl + port + "/users/" + id);
        User newUser = new User("user3", "password3", "user3@example.com", Role.USER);

        ResponseEntity<User> result = restTemplate.withBasicAuth("admin", "adminPass")
        .exchange(uri, HttpMethod.PUT, new HttpEntity<>(newUser), User.class);
        
        assertEquals(200, result.getStatusCode().value()); 
        assertEquals(newUser.getUsername(), result.getBody().getUsername()); 
    }

    @Test
    public void updateUser_InvalidId_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/users/1");
        User newUser = new User("user2", "password", "user@example.com", Role.USER);
        
        ResponseEntity<User> result = restTemplate.withBasicAuth("admin", "adminPass")
        .exchange(uri, HttpMethod.PUT, new HttpEntity<>(newUser), User.class);
        
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void deleteUser_Success() throws Exception {
        User user = new User("user1", "password", "test@email.com", Role.USER);
        userRepository.save(user);
        URI uri = new URI(baseUrl + port + "/users/" + user.getId().longValue());
        userRepository.save(adminUser);

        ResponseEntity<Void> response = restTemplate.withBasicAuth("admin", "adminPass").exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(200, response.getStatusCode().value());
        Optional<User> empty = Optional.empty();
        assertEquals(empty, userRepository.findById(user.getId()));
    }

    @Test
    public void deleteUser_InvalidId_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/users/1");
        User newUser = userRepository.save(new User("user1", "password", "test@email.com", Role.USER));

        ResponseEntity<Void> response = restTemplate.withBasicAuth("admin", "adminPass")
        .exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(404, response.getStatusCode().value());
    }

}

