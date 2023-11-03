package capstone.mfslbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class User {

    @Id
    private String username;

    private String password;

    private Set<Authority> authorities = new HashSet<>();

}
