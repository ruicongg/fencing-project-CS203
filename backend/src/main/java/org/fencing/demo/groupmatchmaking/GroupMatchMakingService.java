package org.fencing.demo.groupmatchmaking;

import java.util.List;

import org.fencing.demo.groupstage.GroupStage;
import org.fencing.demo.match.Match;

public interface GroupMatchMakingService {

    List<GroupStage> createGroupStages(Long eventId);

    List<Match> createMatchesInGroupStages(Long eventId);
}
