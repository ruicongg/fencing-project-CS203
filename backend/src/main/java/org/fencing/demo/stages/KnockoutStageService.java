package org.fencing.demo.stages;

import java.util.List;

public interface KnockoutStageService {

    KnockoutStage addKnockoutStage(Long eventId);

    KnockoutStage getKnockoutStage(Long knockoutStageId);
    
    List<KnockoutStage> getAllKnockoutStagesByEventId(Long eventId);

    KnockoutStage updateKnockoutStage(Long eventId, Long knockoutStageId, KnockoutStage newKnockoutStage);

    void deleteKnockoutStage(Long eventId, Long knockoutStageId);
}
