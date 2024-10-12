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
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import org.springframework.format.annotation.DateTimeFormat;    

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
    @NotNull(message = "Tournament name cannot be null")
    private String name;

    @NotNull(message = "Registration start date cannot be null")
    @FutureOrPresent(message = "Registration start date must be in the present or future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate registrationStartDate;

    @NotNull(message = "Registration end date cannot be null")
    @Future(message = "Registration end date must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate registrationEndDate;

    @NotNull(message = "Tournament start date cannot be null")
    @Future(message = "Tournament start date must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate tournamentStartDate;

    @NotNull(message = "Tournament end date cannot be null")
    @Future(message = "Tournament end date must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate tournamentEndDate;
    @NotNull(message = "Venue is required")
    private String venue;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Event> events;
}
