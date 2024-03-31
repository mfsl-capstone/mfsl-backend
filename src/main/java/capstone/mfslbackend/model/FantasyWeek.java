package capstone.mfslbackend.model;

import capstone.mfslbackend.model.enums.FantasyWeekStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class FantasyWeek {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int weekNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private FantasyWeekStatus status;
    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "fantasy_team_a_id", nullable = false)
    private FantasyTeam fantasyTeamA;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "fantasy_team_b_id", nullable = false)
    private FantasyTeam fantasyTeamB;

    private String teamAInOrder;
    private String teamBInOrder;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "fantasy_week_a_player_game_stats",
            joinColumns = @JoinColumn(name = "fantasy_week_id"),
            inverseJoinColumns = @JoinColumn(name = "player_game_stats_id"))
    private Set<PlayerGameStats> statsTeamA = new LinkedHashSet<>();

    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "fantasy_week_b_player_game_stats",
            joinColumns = @JoinColumn(name = "fantasy_week_id"),
            inverseJoinColumns = @JoinColumn(name = "player_game_stats_id"))
    private Set<PlayerGameStats> statsTeamB = new LinkedHashSet<>();

    private int teamAScore;
    private int teamBScore;
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
        FantasyWeek fantasyWeek = (FantasyWeek) o;
        return id != null && Objects.equals(getId(), fantasyWeek.getId());
    }

    @Override
    public final int hashCode() {
        if (this instanceof HibernateProxy) {
            return ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode();
        }
        return getClass().hashCode();
    }
}
