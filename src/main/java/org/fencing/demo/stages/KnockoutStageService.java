package org.fencing.demo.stages;

public interface KnockoutStageService {

    KnockoutStage addKnockoutStage(Long eventId);

    KnockoutStage getKnockoutStage(Long knockoutStageId);

    KnockoutStage updateKnockoutStage(Long eventId, Long knockoutStageId, KnockoutStage newKnockoutStage);

    void deleteKnockoutStage(Long eventId, Long knockoutStageId);
}
