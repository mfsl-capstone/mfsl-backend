package capstone.mfslbackend.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
public class FantasyLeague {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String leagueName;
    @JsonIgnore
    @OneToMany(mappedBy = "fantasyLeague", orphanRemoval = true)
    private Set<FantasyTeam> fantasyTeams = new LinkedHashSet<>();

    @JsonIgnore
    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "draft_id")
    private Draft draft;

}
