package capstone.mfslbackend.service;

import capstone.mfslbackend.model.RefreshToken;
import capstone.mfslbackend.model.User;
import capstone.mfslbackend.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RefreshTokenService {

    private RefreshTokenRepository refreshTokenRepository;

    private static final int VALID_TIME = 1440; // in minutes, so 24 hours

    public String generateRefreshToken(User user) {
        // delete existing refresh tokens
        deleteRefreshTokensForUser(user);

        // create new token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plus(VALID_TIME, ChronoUnit.MINUTES));
        refreshToken.setRefreshToken(UUID.randomUUID().toString());

        refreshTokenRepository.save(refreshToken);

        return refreshToken.getRefreshToken();
    }

    public Optional<User> validateRefreshToken(String token) {
        return refreshTokenRepository.findByRefreshToken(token)
                .filter(refreshToken -> refreshToken.getExpiryDate().isAfter(Instant.now()))
                .map(RefreshToken::getUser);
    }

    public void deleteRefreshTokensForUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
