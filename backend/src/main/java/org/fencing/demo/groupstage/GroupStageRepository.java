package org.fencing.demo.groupstage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupStageRepository extends JpaRepository<GroupStage, Long>{
    List<GroupStage> findAllByEventId(Long eventId);
    void deleteByEventIdAndId(Long eventId, Long groupStageId);

}