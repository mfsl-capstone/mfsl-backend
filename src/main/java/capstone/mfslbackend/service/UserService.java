package capstone.mfslbackend.service;

import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.model.Authority;
import capstone.mfslbackend.model.SecurityUser;
import capstone.mfslbackend.model.User;
import capstone.mfslbackend.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = getUser(username);
        return new SecurityUser(user);
    }

    public User createUser(String username, String password) {
        // todo check valid password, could do it in fe maybe

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.getAuthorities().add(Authority.LEAGUE_MEMBER);

        return userRepository.save(user);
    }

    public User getUser(String username) {
        return userRepository.findById(username)
                .orElseThrow(() -> new Error404("No user found for username: " + username));
    }

}
