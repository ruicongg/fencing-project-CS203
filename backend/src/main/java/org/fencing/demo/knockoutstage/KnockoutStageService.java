package org.fencing.demo.knockoutstage;

public interface KnockoutStageService {

    KnockoutStage getKnockoutStage(Long knockoutStageId);


    void deleteKnockoutStage(Long eventId, Long knockoutStageId);
}
