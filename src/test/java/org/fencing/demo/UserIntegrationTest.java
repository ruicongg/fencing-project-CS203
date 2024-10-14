package org.fencing.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.net.URL;
import java.util.Optional;

import org.fencing.demo.user.*;

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
    public void testGetUser() throws Exception {
        userRepository.save(new User("user1", "password", "user@email.com", Role.USER));
        URI uri = new URI(baseUrl + port + "/users");

        ResponseEntity<User[]> response = restTemplate.getForEntity(uri, User[].class);
        User[] users = response.getBody();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, users.length);
    }

    @Test
    public void addUser_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/users");
        User user = new User("user1", "password", "test@email.com", Role.USER);
        userRepository.save(user);

        ResponseEntity<User> response = restTemplate.withBasicAuth("admin", "adminPass").postForEntity(uri, user, User.class);
        User newUser = response.getBody();

        assertEquals(201, response.getStatusCode().value());
        assertEquals("user1", newUser.getUsername());
    }

    @Test
    public void deleteUser_Success() throws Exception {
        User user = new User("user1", "password", "test@email.com", Role.USER);
        userRepository.save(user);
        URL uri = new URL(baseUrl + port + "/users/" + user.getId().longValue());
        userRepository.save(adminUser);

        ResponseEntity<User> response = restTemplate.withBasicAuth("admin", "adminPass").exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(200, response.getStatusCode().value());
        Optional<User> empty = Optional.empty();
        assertEquals(empty, userRepository.findById(user.getId()));
    }

}

