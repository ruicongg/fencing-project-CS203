package org.fencing.demo.integrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.Gender;
import org.fencing.demo.events.WeaponType;
import org.fencing.demo.player.Player;
import org.fencing.demo.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EventIntegrationTest extends BaseIntegrationTest{

    @BeforeEach
    void setUp() {
        super.setUp();
    }


    // Add Event - Success
    @Test
    public void addEvent_Success() throws Exception {
                
        URI uri = createUrl("/tournaments/" + tournament.getId() + "/events");

        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(adminToken));
        ResponseEntity<Event> response = restTemplate.exchange(uri, HttpMethod.POST, request, Event.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(event.getGender(), response.getBody().getGender());
        assertEquals(event.getWeapon(), response.getBody().getWeapon());
    }

    @Test
    public void addEvent_ForbiddenForRegularUser_Failure() throws Exception {
        
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(userToken));
        ResponseEntity<Event> response = restTemplate.exchange(uri, HttpMethod.POST, request, Event.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void addEvent_NullTournamentId_Failure() throws Exception {

        
        URI uri = new URI(baseUrl + port + "/tournaments/null/events");

        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(adminToken));
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void addEvent_NullEvent_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        HttpEntity<Event> request = new HttpEntity<>(null, createHeaders(adminToken));
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void addEvent_MissingRequiredFields_Failure() throws Exception {
        Event event = new Event();
        event.setGender(Gender.MALE);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(adminToken));
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void addEvent_NonExistentTournamentId_Failure() throws Exception {
        
        Long nonExistentTournamentId = 999L;  
        URI uri = new URI(baseUrl + port + "/tournaments/" + nonExistentTournamentId + "/events");

        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(adminToken));
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Get All Events by Tournament ID - Success
    @Test
    public void getAllEventsByTournamentId_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(adminToken));
        ResponseEntity<Event[]> response = restTemplate.exchange(uri, HttpMethod.GET, request, Event[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // 1 event created in BaseIntegrationTest setUp
        assertEquals(1, response.getBody().length);
    }

    // Get Event by ID - Success
    @Test
    public void getEventById_Success() throws Exception {

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId());

        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(adminToken));
        ResponseEntity<Event> response = restTemplate.exchange(uri, HttpMethod.GET, request, Event.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(event.getId(), response.getBody().getId());
    }

    @Test
    public void getAllEventsByTournamentId_RegularUser_Success() throws Exception {

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        HttpEntity<Void> request = new HttpEntity<>(createHeaders(userToken));
        ResponseEntity<Event[]> response = restTemplate.exchange(uri, HttpMethod.GET, request, Event[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
    }

    @Test
    public void addEvent_StartDateBeforeTournamentStartDate_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

                event.setStartDate(LocalDateTime.now().plusDays(24));

        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(adminToken));
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Event start date cannt be earlier than Tournament start date"));
    }

    @Test
    public void addEvent_EndDateBeforeStartDate_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        event.setEndDate(LocalDateTime.now().plusDays(2));

        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(adminToken));
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Event end date must be after start date"));
    }

    @Test
    public void addEvent_NonExistentTournament_ThrowsTournamentNotFoundException() throws Exception {
        Event event = createValidEvent(tournament);
        Long nonExistentId = 999L;

        URI uri = new URI(baseUrl + port + "/tournaments/" + nonExistentId + "/events");
        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(adminToken));

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Update Event - Success
    @Test // event successfully created and persisted, but when updating, everything
          // becomes null? eg. id=0, startDate=null
    public void updateEvent_Success() throws Exception {

        long id = event.getId();
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + id);

        event.setGender(Gender.FEMALE);
        event.setWeapon(WeaponType.EPEE);

        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(adminToken));
        ResponseEntity<Event> response = restTemplate.exchange(uri, HttpMethod.PUT, request, Event.class);

        // assertEquals(HttpStatus.OK, response.getStatusCode());
        // assertEquals("smth", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Gender.FEMALE, response.getBody().getGender());
        assertEquals(WeaponType.EPEE, response.getBody().getWeapon());
    }

    @Test
    public void updateEvent_ForbiddenForRegularUser_Failure() throws Exception {
                long id = eventRepository.save(event).getId();

        

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + id);

        event.setGender(Gender.FEMALE);
        event.setWeapon(WeaponType.EPEE);

        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(userToken));
        ResponseEntity<Event> response = restTemplate.exchange(uri, HttpMethod.PUT, request, Event.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void updateEvent_NullIdsOrEvent_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/null");

        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(adminToken));
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    @Test 
    public void updateEvent_EndDateBeforeStartDate_Failure() throws Exception {

        long id = event.getId();
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + id);
        
        event.setEndDate(LocalDateTime.now().plusDays(24));  // End date before start date

        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(adminToken));
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertTrue(response.getBody().contains("Event end date must be after start date"));
    }

    @Test
    public void updateEvent_NonExistentEvent_Failure() throws Exception {
        
        Long nonExistentId = 9999L;
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + nonExistentId);

        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(adminToken));
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void updateEvent_StartDateBeforeTournamentStart_ThrowsIllegalArgumentException() throws Exception {
        Event event = createValidEvent(tournament);
        event = eventRepository.save(event);

        Event updatedEvent = createValidEvent(tournament);
        updatedEvent.setStartDate(tournament.getTournamentStartDate().atStartOfDay().minusDays(1));

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId());
        HttpEntity<Event> request = new HttpEntity<>(updatedEvent, createHeaders(adminToken));

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Event start date cannot be earlier than Tournament start date"));
    }

    // Delete Event - Success
    @Test
    public void deleteEvent_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId());

        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(adminToken));
        ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.DELETE, request, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(eventRepository.findById(event.getId()).isPresent());
    }

    @Test
    public void deleteEvent_ForbiddenForRegularUser_Failure() throws Exception {

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId());

        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(userToken));
        ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.DELETE, request, Void.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode()); // Expecting 403 Forbidden
    }

    @Test
    public void addPlayerToEvent_UserAddingSelf_Success() throws Exception {

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() +
                "/events/" + event.getId() + "/players/" + playerUser.getUsername());
        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(playerToken));

        ResponseEntity<Event> response = restTemplate.exchange(uri, HttpMethod.POST, request, Event.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void addPlayerToEvent_UserAddingOther_Forbidden() throws Exception {

        // Create and save test user with proper role
        Player newPlayer = new Player(
                "testPlayer",
                passwordEncoder.encode("password"),
                "test@example.com",
                Role.USER,
                Gender.MALE);
        playerRepository.save(newPlayer);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() +
                "/events/" + event.getId() + "/players/" + newPlayer.getUsername());
        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(playerToken));

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void addPlayerToEvent_AdminAddingOther_Success() throws Exception {

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() +
                "/events/" + event.getId() + "/players/" + playerUser.getUsername());
        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(adminToken));

        ResponseEntity<Event> response = restTemplate.exchange(uri, HttpMethod.POST, request, Event.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void addPlayerToEvent_NullEventId_ThrowsIllegalArgumentException() throws Exception {

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/null/players/"
                + playerUser.getUsername());
        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(playerToken));

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void addPlayerToEvent_PlayerGenderMismatch_ThrowsIllegalArgumentException() throws Exception {

        playerUser.setGender(Gender.FEMALE);
        playerRepository.save(playerUser);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId()
                + "/players/" + playerUser.getUsername());
        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(playerToken));

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST,
                request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Player's gender does not match"));
    }

    @Test
    public void addPlayerToEvent_OutsideRegistrationPeriod_ThrowsIllegalStateException() throws Exception {

        tournament.setRegistrationStartDate(LocalDate.now().plusDays(1));
        tournamentRepository.save(tournament);

        URI uri = new URI(
                baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/players/" +
                        playerUser.getUsername());
        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(playerToken));

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST,
                request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Registration is not open"));
    }

    @Test
    public void removePlayerFromEvent_PlayerNotInEvent_ThrowsPlayerNotFoundException() throws Exception {

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() +
                "/events/" + event.getId() + "/players");
        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(playerToken));

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.DELETE, request, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void adminRemovesPlayerFromEvent_NonAdminUser_ThrowsAccessDeniedException() throws Exception {

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId()
                + "/players/" + playerUser.getUsername());
        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(playerToken));

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.DELETE, request, String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

}
