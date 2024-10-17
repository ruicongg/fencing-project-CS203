package org.fencing.demo.stages;

import java.util.List;

public interface GroupStageService {

    List<GroupStage> addInitialGroupStages(Long eventId);

    GroupStage addGroupStage(Long eventId);

    GroupStage getGroupStage(Long GroupStageId);

    GroupStage updateGroupStage(Long eventId, Long GroupStageId, GroupStage newGroupStage);

    void deleteGroupStage(Long eventId, Long GroupStageId);

}
