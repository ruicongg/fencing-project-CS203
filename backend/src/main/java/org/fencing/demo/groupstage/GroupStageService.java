package org.fencing.demo.groupstage;

public interface GroupStageService {

    GroupStage addGroupStage(Long eventId);

    GroupStage getGroupStage(Long GroupStageId);

    GroupStage updateGroupStage(Long eventId, Long GroupStageId, GroupStage newGroupStage);

    void deleteGroupStage(Long eventId, Long GroupStageId);

}
