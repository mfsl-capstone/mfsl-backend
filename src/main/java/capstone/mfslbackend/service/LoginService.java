package capstone.mfslbackend.service;

import capstone.mfslbackend.DTO.UserDTO;
import capstone.mfslbackend.model.User;
import capstone.mfslbackend.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class LoginService {

    private final AuthenticationManager authenticationManager;

    private final AccessTokenService accessTokenService;

    private final RefreshTokenService refreshTokenService;

    private final UserRepository userRepository;

    @Transactional
    public UserDTO loginUser(String username, String password) {
        try {
            User user = userRepository.findUserByUsername(username);

            if (user == null) {
                throw new UsernameNotFoundException("No user found for username: " + username);
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), password)
            );

            String accessToken = accessTokenService.generateAccessToken(authentication);

            String refreshToken = refreshTokenService.generateRefreshToken(user);

            return new UserDTO(user.getUsername(), user.getAuthorities(), accessToken, refreshToken);

        } catch (Exception e) {
            throw new IllegalArgumentException("Incorrect username or password!");
        }
    }

    @Transactional
    public UserDTO refreshAccessToken(String refreshToken) {
        return refreshTokenService.validateRefreshToken(refreshToken)
                .map(user -> {
                    String newAccessToken = accessTokenService.generateAccessToken(new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            null,
                            user.getAuthorities().stream()
                                    .map(authority -> new SimpleGrantedAuthority(authority.name()))
                                    .collect(Collectors.toList())
                    ));
                    return new UserDTO(user.getUsername(), user.getAuthorities(), newAccessToken, refreshToken);
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
    }
}
