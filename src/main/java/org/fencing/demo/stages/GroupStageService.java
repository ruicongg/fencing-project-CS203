package org.fencing.demo.stages;

public interface GroupStageService {

    //GroupStage addGroupStages(Long eventId);

    GroupStage getGroupStage(Long GroupStageId);

    GroupStage updateGroupStage(Long eventId, Long GroupStageId, GroupStage newGroupStage);

    void deleteGroupStage(Long eventId, Long GroupStageId);

}
