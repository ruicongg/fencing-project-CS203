// package org.fencing.demo.AfterEvent;

// import org.fencing.demo.events.Event;
// import org.fencing.demo.tournament.Tournament;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// public class AfterEventsController {
//     @Autowired
//     private AfterEventsService afterEventsService;

//     @PostMapping("/{tournamentId}/events/{eventId}/end")
//     public ResponseEntity<String> endEvent(@PathVariable Long tournamentId, @PathVariable Long eventId) {
//         // Retrieve the tournament (optional, based on your implementation)
//         Tournament tournament = afterEventsService.getTournamentById(tournamentId);

//         // Retrieve AfterEvent by event ID and access the event field
//         AfterEvent afterEvent = afterEventsService.getAfterEventByEventId(eventId);
//         Event event = afterEvent.getEvent();

//         // Call the after-tournament service to update player stats
//         afterEventsService.updatePlayerEloAfterEvent(event);

//         return ResponseEntity.ok("Event ended, and player stats updated.");
//     }
// }
