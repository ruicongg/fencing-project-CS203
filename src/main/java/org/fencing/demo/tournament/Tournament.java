package org.fencing.demo.tournament;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import jakarta.persistence.Column;

import org.fencing.demo.events.Event;
import java.util.Set;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tournaments")
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String name;

    @NotNull(message = "Registration start date cannot be null")
    private LocalDate registrationStartDate;

    @NotNull(message = "Registration end date cannot be null")
    private LocalDate registrationEndDate;

    @NotNull(message = "Tournament start date cannot be null")
    private LocalDate tournamentStartDate;

    @NotNull(message = "Tournament end date cannot be null")
    private LocalDate tournamentEndDate;
    
    @NotNull(message = "Venue is required")
    private String venue;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Event> events;
}
