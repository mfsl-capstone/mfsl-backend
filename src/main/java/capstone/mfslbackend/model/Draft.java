package capstone.mfslbackend.model;

import capstone.mfslbackend.model.enums.DraftStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Draft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime draftDate;
    private LocalDateTime timePlayerStarted;
    private int round;
    private String direction;
    private DraftStatus status;

    @OneToOne(mappedBy = "draft", orphanRemoval = true)
    private FantasyLeague fantasyLeague;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "draft_id")
    private Set<Transaction> transactions = new LinkedHashSet<>();

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "fantasy_team_id")
    private FantasyTeam fantasyTeam;
}
