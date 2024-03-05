package capstone.mfslbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Player {
    @Id
    private Long playerId;
    private String name;
    private String position;
    private String url;
    private Integer number;

    @ManyToOne()
    @JoinColumn(name = "team_id")
    private Team team;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "player")
    private List<PlayerGameStats> playerGameStats = new ArrayList<>();

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
        Player player = (Player) o;
        return getPlayerId() != null && Objects.equals(getPlayerId(), player.getPlayerId());
    }

    @Override
    public final int hashCode() {
        if (this instanceof HibernateProxy) {
            return ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode();
        }
        return getClass().hashCode();
    }
}
