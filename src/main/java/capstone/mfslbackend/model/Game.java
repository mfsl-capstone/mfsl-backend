package capstone.mfslbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
public class Game {
    @Id
    @Column(nullable = false)
    private Long id;
//    2020-02-06T14:00:00+00:00 is the date format
    private String date;
    private String round;

    @ToString.Exclude
    @ManyToOne(optional = false)
    @JoinColumn(name = "home_team_team_id", nullable = false)
    private Team homeTeam;

    @ToString.Exclude
    @ManyToOne(optional = false)
    @JoinColumn(name = "away_team_team_id", nullable = false)
    private Team awayTeam;

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
        Game game = (Game) o;
        return getId() != null && Objects.equals(getId(), game.getId());
    }

    @Override
    public final int hashCode() {
        if (this instanceof HibernateProxy) {
            return ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode();
        }
        return getClass().hashCode();
    }

}

