package capstone.mfslbackend.controller;

import capstone.mfslbackend.DTO.UserDTO;
import capstone.mfslbackend.model.User;
import capstone.mfslbackend.service.LoginService;
import capstone.mfslbackend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    private final LoginService loginService;


    @GetMapping("login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        try {
            return ResponseEntity.ok(loginService.loginUser(username, password));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("signup")
    public ResponseEntity<?> register(@RequestParam String username, @RequestParam String password) {
        return ResponseEntity.ok(convertToDto(userService.createUser(username, password)));
    }

    private UserDTO convertToDto(User user) {
        if (user == null) throw new IllegalArgumentException("No user given!");
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setAuthorities(user.getAuthorities());
        return userDTO;
    }


}
