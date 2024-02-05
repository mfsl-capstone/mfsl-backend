package capstone.mfslbackend.repository;

import capstone.mfslbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, String> {
    User findUserByUsername(String username);
}
