package org.fencing.demo.integrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.util.Optional;

import org.fencing.demo.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserIntegrationTest extends BaseIntegrationTest {

    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @Test
    public void GetUsers_Success() throws Exception {

        URI uri = createUrl("/users");

        ResponseEntity<User[]> response = restTemplate.withBasicAuth("admin", "adminPass").getForEntity(uri,
                User[].class);
        User[] users = response.getBody();

        assertEquals(200, response.getStatusCode().value());
        // we have added 3 users in the setUp method
        assertEquals(3, users.length);
    }

    @Test
    public void GetUser_Success() throws Exception {
        Long id = playerUser.getId();
        URI uri = createUrl("/users/" + id);

        ResponseEntity<User> response = restTemplate.withBasicAuth("admin", "adminPass").getForEntity(uri, User.class);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(playerUser.getId(), response.getBody().getId());
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
    // URI uri = new URI(baseUrl + port + "/users");
    // User user = new User("user1", "password", "test@email.com", Role.USER);

    // ResponseEntity<User> response = restTemplate.withBasicAuth("admin",
    // "adminPass").postForEntity(uri, user, User.class);
    // User newUser = response.getBody();

    // assertEquals(201, response.getStatusCode().value());
    // assertEquals("user1", newUser.getUsername());
    // }

    @Test
    public void updateUser_Success() throws Exception {
        Long id = playerUser.getId();
        URI uri = createUrl("/users/" + id);
        User newUser = new User("user999", "password999", "user999@example.com", Role.USER);

        ResponseEntity<User> result = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(newUser), User.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(newUser.getUsername(), result.getBody().getUsername());
    }

    @Test
    public void updateUser_InvalidId_ThrowsNotFoundException() throws Exception {
        URI uri = createUrl("/users/999");
        User newUser = new User("user999", "password999", "user999@example.com", Role.USER);

        ResponseEntity<User> result = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(newUser), User.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void deleteUser_Success() throws Exception {
        Long id = playerUser.getId();
        URI uri = createUrl("/users/" + id);

        ResponseEntity<Void> response = restTemplate.withBasicAuth("admin", "adminPass").exchange(uri,
                HttpMethod.DELETE, null, Void.class);

        assertEquals(200, response.getStatusCode().value());
        Optional<User> empty = Optional.empty();
        assertEquals(empty, userRepository.findById(id));
    }

    @Test
    public void deleteUser_InvalidId_Failure() throws Exception {
        URI uri = createUrl("/users/999");
        ResponseEntity<Void> response = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.DELETE, null, Void.class);
        assertEquals(404, response.getStatusCode().value());
    }

}
