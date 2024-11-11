package org.fencing.demo.knockoutstage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KnockoutStageRepository extends JpaRepository<KnockoutStage, Long>{
    List<KnockoutStage> findByEventId(Long eventId);
    void deleteByEventIdAndId(Long eventId, Long knockoutStageId);
}
