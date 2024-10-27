// package org.fencing.demo.AfterEvent;

// import java.util.List;

// import javax.swing.GroupLayout.Group;

// import org.fencing.demo.events.Event;
// import org.fencing.demo.events.PlayerRank;
// import org.fencing.demo.match.Match;
// import org.fencing.demo.player.Player;
// import org.fencing.demo.player.PlayerRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.fencing.demo.stages.GroupStage;
// import org.fencing.demo.stages.KnockoutStage;
// import org.fencing.demo.tournament.Tournament;
// import org.fencing.demo.tournament.TournamentRepository;

// @Service
// public class AfterEventsService {

//     @Autowired
//     private PlayerRepository playerRepository;  // To save updated Player details

//     private AfterEventRepository afterEventsRepository;

//     @Autowired
//     private TournamentRepository tournamentRepository;

//     public void updatePlayerEloAfterEvent(Event event) {
//         if(!allMatchesComplete(event)){
//             throw new RuntimeException("not all matches completed");
//         }

//         System.out.println("\n\n");
//         System.out.println("this is the event: " + event);
//         System.out.println("\n\n");

//         for (PlayerRank pr : event.getRankings()) {
            
//             Player p = pr.getPlayer();
//             p.setElo(pr.getTempElo());
//             if(pr.getTempElo() >= 2400){
//                 pr.getPlayer().setReached2400(true);
//             }
//             playerRepository.save(p);
//         }
        
//     }

//     private boolean allMatchesComplete(Event event){
//         List<GroupStage> grpStages = event.getGroupStages();
//         List<KnockoutStage> knockoutStages = event.getKnockoutStages();
//         if(grpStages.size() <= 0 || knockoutStages.size() <= 0){
//             throw new RuntimeException("there are no grp or knockout stages");
//         }
//         for(GroupStage grpStage : grpStages){
//             List<Match> matches = grpStage.getMatches();
//             for(Match match : matches){
//                 if(!match.isFinished()){
//                     return false;
//                 }
//             }
//         }

//         for(KnockoutStage knockoutStage : knockoutStages){
//             List<Match> matches = knockoutStage.getMatches();
//             for(Match match : matches){
//                 if(!match.isFinished()){
//                     return false;
//                 }
//             }
//         }

//         return true;
//     }



