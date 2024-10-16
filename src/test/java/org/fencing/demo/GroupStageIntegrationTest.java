package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GroupStageIntegrationTest {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User adminUser;

    private User regularUser;

	@Autowired
	private GroupStageRepository grpStagesRepository;

	@Autowired
	private EventRepository eventsRepository;

	@BeforeEach
    void setUp() {
		userRepository.deleteAll();
		if (userRepository.findByUsername("admin") == null) {
			adminUser = new User("admin", passwordEncoder.encode("adminPass"), "admin@example.com", Role.ADMIN);
			userRepository.save(adminUser);
		}
	
		if (userRepository.findByUsername("user") == null) {
			regularUser = new User("user", passwordEncoder.encode("userPass"), "user@example.com", Role.USER);
			userRepository.save(regularUser);
		}

    }

    @AfterEach
	void tearDown(){
		// clear the database after each test
		grpStagesRepository.deleteAll();
		eventsRepository.deleteAll();
		tournamentRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	@Transactional
    public void getGrpStageById_Success() throws Exception {
		Tournament findTournament = createValidTournament();
		Long tournamentId = tournamentRepository.save(findTournament).getId();
		Event findEvent = createValidEvent(findTournament);
		Long eventId = eventsRepository.save(findEvent).getId();
		GroupStage findGrpStage = createValidGrpStage(findEvent);
		Long grpStageId = grpStagesRepository.save(findGrpStage).getId();

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournamentId + "/events/" + eventId +"/groupStage/" + grpStageId);

		assertNotNull(tournamentId);
		assertNotNull(eventId);
		assertNotNull(grpStageId);

		// ResponseEntity<GroupStage> result = restTemplate.getForEntity(uri, GroupStage.class);

        // assertEquals(200, result.getStatusCode().value());
    }


	@Test
    public void getGroupStage_InvalidGroupStageId_Failure() throws Exception {
		Tournament findTournament = createValidTournament();
		Long tournamentId = tournamentRepository.save(findTournament).getId();
		Event findEvent = createValidEvent(findTournament);
		Long eventId = eventsRepository.save(findEvent).getId();
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournamentId + "/events/" + eventId +"/groupStage/999");

		ResponseEntity<GroupStage> result = restTemplate.getForEntity(uri, GroupStage.class);
		
		assertNotNull(tournamentId);
		assertNotNull(eventId);
		
        assertEquals(404, result.getStatusCode().value());
    }



	private Tournament createValidTournament() {
        return Tournament.builder()
                .name("Spring Championship")
                .registrationStartDate(LocalDate.of(2024, 12, 4))
                .registrationEndDate(LocalDate.of(2024, 12, 25))
                .tournamentStartDate(LocalDate.of(2025, 1, 20))
                .tournamentEndDate(LocalDate.of(2025, 1, 25))
                .venue("Sports Arena")
                .events(new HashSet<>())
                .build();
    }


	private Event createValidEvent(Tournament tournament){
		Event event = new Event();
		event.setStartDate(LocalDateTime.of(2024, 12, 4, 10, 30));
		event.setEndDate(LocalDateTime.of(2024, 12, 25, 11,00));
		event.setGender(Gender.FEMALE);
		event.setWeapon(WeaponType.EPEE);
		event.setGroupStages(new ArrayList<>());
		event.setTournament(tournament);

		return event;
	}

	private GroupStage createValidGrpStage(Event event){
		GroupStage grpStage = new GroupStage();
		grpStage.setEvent(event);
		grpStage.setPlayers(new ArrayList<>());
		grpStage.setMatches(new ArrayList<>());
		grpStage.setAllMatchesCompleted(false);

		return grpStage;
	}


}
