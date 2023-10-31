package capstone.mfslbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Team {
    @Id
    private Long teamId;
    private String name;
    private String url;

//    @ToString.Exclude
//    @JsonIgnore
//    @OneToMany(mappedBy = "team")
//    private List<Player> players = new ArrayList<>();
//
//    @ManyToMany
//    @JsonIgnore
//    @ToString.Exclude
//    private List<Game> games;

//    IntelliJ suggests this stuff instead of @Data
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Team team = (Team) o;
        return getTeamId() != null && Objects.equals(getTeamId(), team.getTeamId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}