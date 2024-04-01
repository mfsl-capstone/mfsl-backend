package capstone.mfslbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class FantasyTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String teamName;
    private String playerIdsInOrder;
    private int orderNumber;
    private String colour;
    private int wins;
    private int losses;
    private int ties;
    private int points;
    private int fantasyPoints;


    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_username", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fantasy_league_id", nullable = false)
    private FantasyLeague fantasyLeague;
    @OneToMany(mappedBy = "proposingFantasyTeam", orphanRemoval = true)
    private Set<Transaction> transactions = new LinkedHashSet<>();
    @OneToMany(mappedBy = "receivingFantasyTeam", orphanRemoval = true)
    private Set<Transaction> incomingTrades = new LinkedHashSet<>();

    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "fantasy_team_players",
            joinColumns = @JoinColumn(name = "fantasy_team_id"),
            inverseJoinColumns = @JoinColumn(name = "players_player_id"))
    private Set<Player> players = new LinkedHashSet<>();

    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "fantasy_team_fantasy_weeks",
            joinColumns = @JoinColumn(name = "fantasyTeam_id"),
            inverseJoinColumns = @JoinColumn(name = "fantasyWeeks_id"))
    private List<FantasyWeek> fantasyWeeks = new ArrayList<>();

    public FantasyTeam(FantasyTeam fantasyTeam) {
        this.id = fantasyTeam.id;
        this.teamName = fantasyTeam.teamName;
        this.playerIdsInOrder = fantasyTeam.playerIdsInOrder;
        this.orderNumber = fantasyTeam.orderNumber;
        this.colour = fantasyTeam.colour;
        this.wins = fantasyTeam.wins;
        this.losses = fantasyTeam.losses;
        this.ties = fantasyTeam.ties;
        this.points = fantasyTeam.points;
        this.fantasyPoints = fantasyTeam.fantasyPoints;
        this.fantasyWeeks = fantasyTeam.fantasyWeeks;
        this.user = fantasyTeam.user;
        this.fantasyLeague = fantasyTeam.fantasyLeague;
        this.transactions = fantasyTeam.transactions;
        this.incomingTrades = fantasyTeam.incomingTrades;
        this.players = fantasyTeam.players;
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass;
        if (o instanceof HibernateProxy) {
            oEffectiveClass = ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass();
        } else {
            oEffectiveClass = o.getClass();
        }
        Class<?> thisEffectiveClass;
        if (this instanceof HibernateProxy) {
            thisEffectiveClass = ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass();
        } else {
            thisEffectiveClass = this.getClass();
        }
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        FantasyTeam fantasyTeam = (FantasyTeam) o;
        return id != null && Objects.equals(getId(), fantasyTeam.getId());
    }

    @Override
    public final int hashCode() {
        if (this instanceof HibernateProxy) {
            return ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode();
        }
        return getClass().hashCode();
    }
}
