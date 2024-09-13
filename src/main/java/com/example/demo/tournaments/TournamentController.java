package com.example.demo.tournaments;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import java.lang.Long;

import com.example.demo.mapper.Mapper;

@RestController
public class TournamentController {

    private TournamentService tournamentService;
    private Mapper<TournamentEntity, TournamentDto> tournamentMapper;

    public TournamentController(TournamentService tournamentService, Mapper<TournamentEntity, TournamentDto> tournamentMapper){
        this.tournamentService = tournamentService;
        this.tournamentMapper = tournamentMapper;
    }

    @PostMapping(path = "/tournaments")
    public TournamentDto createTournament(@RequestBody TournamentDto tournament){
        TournamentEntity tournamentEntity = tournamentMapper.mapFrom(tournament);
        TournamentEntity savedTournament = tournamentService.save(tournamentEntity);
        return tournamentMapper.mapTo(savedTournament);
    }

    @GetMapping(path = "/tournaments")
    public List<TournamentDto> listTournaments(@RequestBody TournamentDto tournament){
        List<TournamentEntity> tournaments = tournamentService.findAll();
        return tournaments.stream().map(tournamentMapper::mapTo).collect(Collectors.toList());
    }

    @GetMapping(path = "/tournaments/{id}")
    public ResponseEntity<TournamentDto> getTournament(@PathVariable("id") Long id){
        Optional<TournamentEntity> foundTournament = tournamentService.findOne(id);
        return foundTournament.map(TournamentEntity -> {
            TournamentDto tournamentDto = tournamentMapper.mapTo(TournamentEntity);
            return new ResponseEntity<>(tournamentDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND)); 
    }
    
    @PutMapping(path = "/tournaments/{id}")
    public ResponseEntity<TournamentDto> fullUpdateTournament(@PathVariable("id") Long id, @RequestBody TournamentDto tournamentDto){
        if (!tournamentService.isExists(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
        }

        tournamentDto.setId(id);
        TournamentEntity tournamentEntity = tournamentMapper.mapFrom(tournamentDto);
        TournamentEntity savedTournament = tournamentService.save(tournamentEntity);
        return new ResponseEntity<>(tournamentMapper.mapTo(savedTournament), HttpStatus.OK); 
    }  

    @PatchMapping(path = "/authors/{id}")
    public ResponseEntity<TournamentDto> partialUpdate(@PathVariable("id") Long id, @RequestBody TournamentDto tournamentDto){
        if(!tournamentService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        TournamentEntity tournamentEntity = tournamentMapper.mapFrom(tournamentDto);
        TournamentEntity updatedTournament = tournamentService.partialUpdate(id, tournamentEntity);
        return new ResponseEntity<>(tournamentMapper.mapTo(updatedTournament), HttpStatus.OK);
    }

    @DeleteMapping(path = "/authors/{id}")
    public ResponseEntity<TournamentDto> deleteAuthor(@PathVariable("id") Long id) {
        tournamentService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

     
} 