package org.fencing.demo.tournaments;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.hibernate.annotations.DialectOverride.OverridesAnnotation;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;

    public TournamentServiceImpl(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public Tournament updateTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    @Override
    public List<Tournament> listTournaments() {
        return StreamSupport.stream(tournamentRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Tournament> getTournament(Long id) {
        return tournamentRepository.findById(id);
    }

    @Override
    public boolean doesTournamentExist(Long id) {
        return tournamentRepository.existsById(id);
    }

    @Override
    public List<Tournament> findByStartDateTournament(LocalDate date){

        return tournamentRepository.findByStartDate(date);
    }

    @Override
    public List<Tournament> findByEndDateTournament(LocalDate date){

        return tournamentRepository.findByEndDate(date);
    }


    // ! check before deleting
    // @Override
    // public Tournament updateTournament(Long id, Tournament tournament) {
    //     tournament.setId(id);

    //     return tournamentRepository.findById(id).map(existingTournament -> {
    //         Optional.ofNullable(tournament.getName()).ifPresent(existingTournament::setName);
    //         return tournamentRepository.save(existingTournament);
    //     }).orElseThrow(() -> new RuntimeException("Tournament does not exist"));
    // }

    // ! still need to implement exception handling
    // this method assumes that you are not changing reviews
    @Override
    public Tournament updateTournament(Long id, Tournament newTournament) {
        return tournamentRepository.findById(id).map(existingTournament -> {
            existingTournament.setName(newTournament.getName());
            existingTournament.setStartDate(newTournament.getStartDate());
            existingTournament.setEndDate(newTournament.getEndDate());
            return tournamentRepository.save(existingTournament);
        }).orElseThrow(() -> new IllegalArgumentException("Tournament does not exist"));
    }

    @Override
    public void deleteTournament(Long id) {
        tournamentRepository.deleteById(id);
    }

    @Override
    public List<Tournament> findByAvailability(LocalDate startDate, LocalDate endDate){
        LocalDate currentDate = LocalDate.now();
        if (!startDate.isAfter(currentDate) || !endDate.isAfter(currentDate)){
            throw new IllegalArgumentException("Error: Date entered must be after today!");
        }

        if(startDate.isAfter(endDate)){
            throw new IllegalArgumentException("Error: start date is after end date");
        }

        return tournamentRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(endDate, startDate);
    }

    

}
