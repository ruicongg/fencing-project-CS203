package org.fencing.demo.integrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;

import java.net.URI;
import java.time.LocalDateTime;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.Gender;
import org.fencing.demo.events.WeaponType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventIntegrationTest extends BaseIntegrationTest{

    @BeforeEach
    void setUp() {
        super.setUp();
    }


    // Add Event - Success
    @Test
    public void addEvent_Success() throws Exception {
                
        URI uri = createUrl("/tournaments/" + tournament.getId() + "/events");

        ResponseEntity<Event> result = restTemplate.withBasicAuth("admin", "adminPass")
                                        .postForEntity(uri, event, Event.class);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(event.getGender(), result.getBody().getGender());
        assertEquals(event.getWeapon(), result.getBody().getWeapon());
    }

    @Test
    public void addEvent_ForbiddenForRegularUser_Failure() throws Exception {
        
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        ResponseEntity<Event> result = restTemplate.withBasicAuth("user", "userPass")
                                            .postForEntity(uri, event, Event.class);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());  
    }

    @Test
    public void addEvent_NullTournamentId_Failure() throws Exception {

        
        URI uri = new URI(baseUrl + port + "/tournaments/null/events");

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .postForEntity(uri, event, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void addEvent_NullEvent_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .postForEntity(uri, null, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void addEvent_MissingRequiredFields_Failure() throws Exception {
        Event event = new Event();
        event.setGender(Gender.MALE);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .postForEntity(uri, event, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void addEvent_NonExistentTournamentId_Failure() throws Exception {
        
        Long nonExistentTournamentId = 999L;  
        URI uri = new URI(baseUrl + port + "/tournaments/" + nonExistentTournamentId + "/events");
        
        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .postForEntity(uri, event, String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    // Get All Events by Tournament ID - Success
    @Test
    public void getAllEventsByTournamentId_Success() throws Exception {

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        ResponseEntity<Event[]> result = restTemplate.withBasicAuth("admin", "adminPass")
                                            .getForEntity(uri, Event[].class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        // 1 event created in BaseIntegrationTest setUp
        assertEquals(1, result.getBody().length);
    }

    // Get Event by ID - Success
    @Test
    public void getEventById_Success() throws Exception {

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId());

        ResponseEntity<Event> result = restTemplate.withBasicAuth("admin", "adminPass")
                                            .getForEntity(uri, Event.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(event.getId(), result.getBody().getId());
    }

    @Test
    public void getAllEventsByTournamentId_RegularUser_Success() throws Exception {
        
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        ResponseEntity<Event[]> result = restTemplate.withBasicAuth("user", "userPass")
                                                .getForEntity(uri, Event[].class);
        // 1 event created in BaseIntegrationTest setUp
        assertEquals(HttpStatus.OK, result.getStatusCode());  
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().length);
    }

    @Test
    public void addEvent_StartDateBeforeTournamentStartDate_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

                event.setStartDate(LocalDateTime.now().plusDays(24));

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .postForEntity(uri, event, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().contains("Event start date cannt be earlier than Tournament start date"));
    }

    @Test
    public void addEvent_EndDateBeforeStartDate_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        event.setEndDate(LocalDateTime.now().minusDays(24));

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .postForEntity(uri, event, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().contains("Event start date must be in the future"));
    }

    // Update Event - Success
    @Test // event successfully created and persisted, but when updating, everything becomes null? eg. id=0, startDate=null
    public void updateEvent_Success() throws Exception {

        long id = eventRepository.save(event).getId();
        
        
        
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + id);

        event.setGender(Gender.FEMALE);
        event.setWeapon(WeaponType.EPEE);

        ResponseEntity<Event> result = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(event), Event.class);

        // assertEquals(HttpStatus.OK, result.getStatusCode());
        // assertEquals("smth", result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(Gender.FEMALE, result.getBody().getGender());
        assertEquals(WeaponType.EPEE, result.getBody().getWeapon());
    }

    @Test
    public void updateEvent_ForbiddenForRegularUser_Failure() throws Exception {
                long id = eventRepository.save(event).getId();

        

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + id);
        
        event.setGender(Gender.FEMALE);
        event.setWeapon(WeaponType.EPEE);

        ResponseEntity<Event> result = restTemplate.withBasicAuth("user", "userPass")
                                            .exchange(uri, HttpMethod.PUT, new HttpEntity<>(event), Event.class);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());  
    }

    @Test
    public void updateEvent_NullIdsOrEvent_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/null");

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(null), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }


    @Test 
    public void updateEvent_EndDateBeforeStartDate_Failure() throws Exception {

        long id = event.getId();
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + id);
        
        event.setEndDate(LocalDateTime.now().plusDays(24));  // End date before start date

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(event), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());

        assertTrue(result.getBody().contains("Event end date must be after start date"));
    }

    @Test
    public void updateEvent_NonExistentEvent_Failure() throws Exception {
        
        

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/9999");

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(event), String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }


    // Delete Event - Success
    @Test
    public void deleteEvent_Success() throws Exception {
                long id = eventRepository.save(event).getId();

        

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + id);

        ResponseEntity<Void> result = restTemplate.withBasicAuth("admin", "adminPass")
                                        .exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertFalse(eventRepository.findById(event.getId()).isPresent());
    }

    @Test
    public void deleteEvent_ForbiddenForRegularUser_Failure() throws Exception {
                long id = eventRepository.save(event).getId();

        

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + id);

        ResponseEntity<Void> result = restTemplate.withBasicAuth("user", "userPass")
                                            .exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());  // Expecting 403 Forbidden
    }


}

