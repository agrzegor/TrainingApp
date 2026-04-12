package pl.coderslab.trainingapp.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.trainingapp.dto.UserDto;
import pl.coderslab.trainingapp.security.JwtService;
import pl.coderslab.trainingapp.dto.api.LoginResponse;
import pl.coderslab.trainingapp.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {


    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> register(@RequestBody UserDto registerUserDto) {
        UserDto registeredUser = userService.createUser(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody UserDto loginUserDto) {
        UserDetails authenticatedUser = userService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

}
