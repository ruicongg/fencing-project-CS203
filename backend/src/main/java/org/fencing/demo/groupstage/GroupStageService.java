package org.fencing.demo.groupstage;

public interface GroupStageService {

    GroupStage getGroupStage(Long GroupStageId);

    List<GroupStage> getAllGroupStagesByEventId(Long eventId);

    GroupStage updateGroupStage(Long eventId, Long GroupStageId, GroupStage newGroupStage);

    void deleteGroupStage(Long eventId, Long GroupStageId);

}
