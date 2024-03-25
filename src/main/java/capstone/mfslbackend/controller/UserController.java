package capstone.mfslbackend.controller;

import capstone.mfslbackend.DTO.UserDTO;
import capstone.mfslbackend.model.User;
import capstone.mfslbackend.service.LoginService;
import capstone.mfslbackend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

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
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("signup")
    public ResponseEntity<?> register(@RequestParam String username, @RequestParam String password) {
        return ResponseEntity.ok(convertToDto(userService.createUser(username, password)));
    }

    @PostMapping("refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestParam String refreshToken) {
        try {
            return ResponseEntity.ok(loginService.refreshAccessToken(refreshToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }


    @GetMapping("{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUser(username));
    }

    private UserDTO convertToDto(User user) {
        if (user == null) {
            throw new IllegalArgumentException("No user given!");
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setAuthorities(user.getAuthorities());
        return userDTO;
    }


}
