package capstone.mfslbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private LocalDateTime date;
    @Enumerated
    private TransactionStatus status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "proposing_fantasy_team_id", nullable = false)
    private FantasyTeam proposingFantasyTeam;

    @OneToOne(optional = false, orphanRemoval = true)
    @JoinColumn(name = "player_in_player_id", nullable = false)
    private Player playerIn;

    @OneToOne(optional = false, orphanRemoval = true)
    @JoinColumn(name = "player_out_player_id", nullable = false)
    private Player playerOut;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "receiving_fantasy_team_id")
    private FantasyTeam receivingFantasyTeam;

}
