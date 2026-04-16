package pl.coderslab.trainingapp.controller;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.coderslab.trainingapp.dto.UserDto;
import pl.coderslab.trainingapp.entity.User;
import pl.coderslab.trainingapp.security.JwtService;
import pl.coderslab.trainingapp.dto.api.LoginResponse;
import pl.coderslab.trainingapp.service.UserService;

import java.util.Map;

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
    public ResponseEntity<UserDto> register(@RequestBody @Valid UserDto registerUserDto) {
        UserDto registeredUser = userService.createUser(registerUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody UserDto loginUserDto) {
        UserDetails authenticatedUser = userService.authenticate(loginUserDto);
        Map<String, Object> extraClaims = Map.of(
                "userType", ((User) authenticatedUser).getUserType().toString()
        );
        String jwtToken = jwtService.generateToken(extraClaims, authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

}
