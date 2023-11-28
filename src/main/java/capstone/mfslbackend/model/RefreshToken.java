package capstone.mfslbackend.model;

import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class RefreshToken {

    @Id
    private String refreshToken;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;

    private Instant expiryDate;
}
