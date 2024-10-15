package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.events.WeaponType;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.events.Gender;
import org.fencing.demo.match.Match;
import org.fencing.demo.match.MatchRepository;
import org.fencing.demo.stages.GroupStage;
import org.fencing.demo.stages.GroupStageRepository;
import org.fencing.demo.stages.KnockoutStage;
import org.fencing.demo.tournament.Tournament;
import org.fencing.demo.tournament.TournamentRepository;
import org.fencing.demo.user.UserRepository;
import org.fencing.demo.user.Role;
import org.fencing.demo.user.User;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GroupStageIntegrationTest {
    @LocalServerPort
	private int port;

	private final String baseUrl = "http://localhost:";

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private GroupStageRepository grpStages;

	@Autowired
	private EventRepository events;

    @Autowired
	private TournamentRepository tournaments;

	@Autowired
	private UserRepository users;

	@Autowired
	private BCryptPasswordEncoder encoder;

    @AfterEach
	void tearDown(){
		// clear the database after each test
		grpStages.deleteAll();
		events.deleteAll();
		tournaments.deleteAll();
	}

    // @Test
	// public void getGroup_Success() throws Exception {
		
	// 	Tournament tournament = new Tournament();
	// 	tournament.setRegistrationStartDate(LocalDate.of(2024, 12, 4));
	// 	tournament.setRegistrationEndDate(LocalDate.of(2024, 12, 25));
	// 	tournament.setTournamentEndDate(LocalDate.of(2025, 1, 25));
	// 	tournament.setTournamentStartDate(LocalDate.of(2025, 1, 20));
	// 	tournament.setName("testFencing");
	// 	tournament.setVenue("court 1");
	// 	Set<Event> newEvents = new HashSet<>();
		
	// 	Event event = new Event();
	// 	event.setStartDate(LocalDateTime.of(2024, 12, 4, 10, 30));
	// 	event.setEndDate(LocalDateTime.of(2024, 12, 25, 11,00));
	// 	event.setGender(Gender.FEMALE);
	// 	event.setWeapon(WeaponType.EPEE);
	// 	List<GroupStage> grpstages = new ArrayList<>();
	// 	event.setGroupStages(grpstages);
		
	// 	newEvents.add(event);
	// 	tournament.setEvents(newEvents);
	// 	Long tournamentId = tournaments.save(tournament).getId();

	// 	event.setTournament(tournament);

	// 	GroupStage groupStage = new GroupStage();
    //     groupStage.setEvent(event);
    //     Long grpStageId = grpStages.save(groupStage).getId();

    //     event.getGroupStages().add(groupStage);
    //     Long eventId = events.save(event).getId();


	// 	users.save(new User("newAdmin", encoder.encode("goodpassword"), 
	// 	"newAdmin@gmail.com",Role.ADMIN));
        
	// 	URI uri = new URI(baseUrl + port + "/tournaments/" + tournamentId + "/events/" + eventId +"/groupStage/" + grpStageId);
	// 	HttpEntity<GroupStage> requestEntity = new HttpEntity<>(groupStage);
		
	// 	ResponseEntity<GroupStage> result = restTemplate.withBasicAuth("newAdmin", "goodpassword")
	// 	.exchange(uri, HttpMethod.GET, requestEntity, GroupStage.class);

	// 	assertEquals(200, result.getStatusCode().value());
	// 	assertEquals(groupStage.getId(), result.getBody().getId());
	// }

	@Test
	public void getGroup_Success() throws Exception {
		// Step 1: Setup and save Tournament first
		Tournament tournament = new Tournament();
		tournament.setRegistrationStartDate(LocalDate.of(2024, 12, 4));
		tournament.setRegistrationEndDate(LocalDate.of(2024, 12, 25));
		tournament.setTournamentEndDate(LocalDate.of(2025, 1, 25));
		tournament.setTournamentStartDate(LocalDate.of(2025, 1, 20));
		tournament.setName("testFencing");
		tournament.setVenue("court 1");
		tournament = tournaments.save(tournament); // Save and ensure it's managed

		// Step 2: Setup Event and associate with Tournament
		Event event = new Event();
		event.setStartDate(LocalDateTime.of(2024, 12, 4, 10, 30));
		event.setEndDate(LocalDateTime.of(2024, 12, 25, 11, 00));
		event.setGender(Gender.FEMALE);
		event.setWeapon(WeaponType.EPEE);
		event.setTournament(tournament);
		event.setGroupStages(new ArrayList<>());
		event = events.save(event);

		// Step 3: Setup GroupStage and associate with Event
		GroupStage groupStage = new GroupStage();
		groupStage.setEvent(event);
		groupStage = grpStages.save(groupStage);

		// Step 4: Update Event with GroupStage reference
		event.getGroupStages().add(groupStage);
		events.save(event); // Re-save to update the relationship

		// Step 5: Create User for Authentication
		users.save(new User("newAdmin", encoder.encode("goodpassword"), "newAdmin@gmail.com", Role.ADMIN));

		// Step 6: Perform the GET request
		URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/groupStage/" + groupStage.getId());
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth("newAdmin", "goodpassword");
		HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

		// Execute the GET request
		ResponseEntity<GroupStage> result = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, GroupStage.class);

		// Verify the response
		assertEquals(200, result.getStatusCode().value());
		assertEquals(groupStage.getId(), result.getBody().getId());
	}



	@Test
	public void getBook_InvalidGroupStageId_Failure() throws Exception {
		Tournament tournament = new Tournament(1, "test fencing", LocalDate.of(2024, 12, 4), LocalDate.of(2024, 12, 25),
        LocalDate.of(2025, 1, 20), LocalDate.of(2025, 1, 25), "court 4", new HashSet<Event>());
        Long tournamentId = tournaments.save(tournament).getId();
		
		Event event = new Event(1, LocalDateTime.of(2024, 12, 4, 10, 30), 
        LocalDateTime.of(2024, 12, 25, 11,00),
        Gender.FEMALE, WeaponType.EPEE, new HashSet<PlayerRank>(), tournament,
        new ArrayList<GroupStage>(), new ArrayList<KnockoutStage>());
		Long eventId = events.save(event).getId();

		URI uri = new URI(baseUrl + port + "/tournaments/" + tournamentId + "/events/" + eventId +"/groupStage/1");
		
		ResponseEntity<GroupStage> result = restTemplate.getForEntity(uri, GroupStage.class);
			
		assertEquals(404, result.getStatusCode().value());
	}



}
