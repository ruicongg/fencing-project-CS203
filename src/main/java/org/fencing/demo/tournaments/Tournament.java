package org.fencing.demo.tournaments;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tournaments")

public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tournament_id_seq")
    @SequenceGenerator(name = "tournament_id_seq", sequenceName = "tournament_id_seq", allocationSize = 1)
    private long id;

    private String name;
}
