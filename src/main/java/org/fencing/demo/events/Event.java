package org.fencing.demo.events;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.TreeSet;
import java.util.Set;

import org.fencing.demo.stages.GroupStage;
import org.fencing.demo.stages.KnockoutStage;
import org.fencing.demo.tournaments.Tournament;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    public LocalDate date;

    public Gender gender;

    public WeaponType weapon;

    public TreeSet<PlayerRank> rankings;

    @OneToMany
    public Tournament tournament;

    public Set<GroupStage> GroupStages;

    public KnockoutStage knockoutStage;

}
