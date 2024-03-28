package capstone.mfslbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Draft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "draft", orphanRemoval = true)
    private FantasyLeague fantasyLeague;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "draft_id")
    private Set<Transaction> transactions = new LinkedHashSet<>();


}
