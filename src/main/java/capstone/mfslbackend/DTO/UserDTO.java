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

    public String username;
    public Set<Authority> authorities;
    public String token;
}
