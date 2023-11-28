package capstone.mfslbackend.DTO;

import capstone.mfslbackend.model.Authority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private String username;
    private Set<Authority> authorities;
    private String accessToken;
    private String refreshToken;
}
