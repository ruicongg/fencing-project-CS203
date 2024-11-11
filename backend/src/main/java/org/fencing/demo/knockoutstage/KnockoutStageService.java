package org.fencing.demo.knockoutstage;

import java.util.List;

public interface KnockoutStageService {

    KnockoutStage getKnockoutStage(Long knockoutStageId);

    List<KnockoutStage> getAllKnockoutStagesByEventId(Long eventId);
    
    void deleteKnockoutStage(Long eventId, Long knockoutStageId);
}
