package org.fencing.demo.tournaments;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.lang.Long;

// ! might want to use @response status
@RestController
public class TournamentController {

    private TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping(path = "/tournaments")
    public List<Tournament> getTournaments() {
        return tournamentService.listTournaments().stream().collect(Collectors.toList());
    }

    @GetMapping(path = "/tournaments/{id}")
    public ResponseEntity<Tournament> getTournament(@PathVariable("id") Long id) {
        return tournamentService.getTournament(id).map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(path = "/tournaments")
    public Tournament createTournament(@RequestBody Tournament tournament) {
        return tournamentService.updateTournament(tournament);
    }

    // @PutMapping(path = "/tournaments/{id}")
    // public ResponseEntity<Tournament> updateTournament(@PathVariable("id") Long id,
    //         @RequestBody Tournament newTournament) {
    //     if (!tournamentService.doesTournamentExist(id)) {
    //         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    //     }
    //     newTournament.setId(id);
    //     return new ResponseEntity<>(tournamentService.saveTournament(newTournament), HttpStatus.OK);
    // }

    @PutMapping(path = "/tournaments/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Tournament updateTournament(@PathVariable("id") Long id, @RequestBody Tournament tournament) {
        return tournamentService.updateTournament(id, tournament);
    }

    @DeleteMapping(path = "/tournaments/{id}")
    public ResponseEntity<Tournament> deleteTournament(@PathVariable("id") Long id) {
        tournamentService.deleteTournament(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}