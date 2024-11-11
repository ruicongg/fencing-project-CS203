package org.fencing.demo.groupstage;

import java.util.List;

public interface GroupStageService {

    GroupStage getGroupStage(Long GroupStageId);

    List<GroupStage> getAllGroupStagesByEventId(Long eventId);

    GroupStage updateGroupStage(Long eventId, Long GroupStageId, GroupStage newGroupStage);

    void deleteGroupStage(Long eventId, Long GroupStageId);

}
