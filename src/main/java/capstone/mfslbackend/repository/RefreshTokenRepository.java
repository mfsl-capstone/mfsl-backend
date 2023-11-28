package capstone.mfslbackend.repository;

import capstone.mfslbackend.model.RefreshToken;
import capstone.mfslbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    void deleteByUser(User user);
}
