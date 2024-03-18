package capstone.mfslbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
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

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "fantasy_team_a_id", nullable = false)
    private FantasyTeam fantasyTeamA;

    @ToString.Exclude
    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "fantasy_week_id")
    private FantasyWeek fantasyWeek;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "fantasy_team_b_id", nullable = false)
    private FantasyTeam fantasyTeamB;

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
