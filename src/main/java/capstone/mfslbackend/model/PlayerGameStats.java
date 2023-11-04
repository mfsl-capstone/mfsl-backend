package capstone.mfslbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.lang.reflect.Field;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class PlayerGameStats {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)     private Long id;
    private Long id;
    private int points;
    private int yellowCards;
    private int redCards;
    private int successfulDribbles;
    private int duelsWon;
    private int foulsDrawn;
    private int foulsCommitted;
    private int minutes;
    private float rating;
    private int goalsScored;
    private int goalsConceded;
    private int assists;
    private int saves;
    private int passes;
    private int keyPasses;
    private String passAccuracy;
    private int penaltiesCommitted;
    private int penaltiesScored;
    private int penaltiesMissed;
    private int penaltiesSaved;
    private int shotsTaken;
    private int shotsOnTarget;
    private int tackles;
    private int shotBlocks;
    private int interceptions;
    private int result;
    private String round;

    @ToString.Exclude
    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    public PlayerGameStats noNulls() throws IllegalAccessException {
        for (Field f : PlayerGameStats.class.getFields()) {

            if (f.get(this) != null) {
                continue;
            }
            if (f.getType() == Integer.TYPE || f.getType() == Float.TYPE) {
                f.set(this, 0);
            }
            if (f.getType() == String.class) {
                f.set(this, " ");
            }

        }
        return this;
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
        PlayerGameStats that = (PlayerGameStats) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        if (this instanceof HibernateProxy) {
            return ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode();
        }
        return getClass().hashCode();
    }
}
