package capstone.mfslbackend.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

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

    @ManyToOne(cascade = jakarta.persistence.CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

//    @OneToMany
//    private List<PlayerGameStats> playerGameStats;

//    public void addMatchday(PlayerGameStats matchDay) {
//        this.playerGameStats.add(matchDay);
//    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Player player = (Player) o;
        return getPlayerId() != null && Objects.equals(getPlayerId(), player.getPlayerId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}