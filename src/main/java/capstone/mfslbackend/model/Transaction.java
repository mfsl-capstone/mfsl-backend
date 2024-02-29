package capstone.mfslbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "proposing_fantasy_team_id", nullable = false)
    private FantasyTeam proposingFantasyTeam;

    @OneToOne(optional = false, orphanRemoval = true)
    @JoinColumn(name = "player_in_player_id", nullable = false)
    private Player playerIn;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "player_out_player_id")
    private Player playerOut;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "receiving_fantasy_team_id")
    private FantasyTeam receivingFantasyTeam;

}
