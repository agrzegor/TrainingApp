package pl.coderslab.trainingapp.controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.trainingapp.dto.UserDto;
import pl.coderslab.trainingapp.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }



    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUserDto(@RequestBody UserDto userDto) {
      return  userService.createUser(userDto);

    }

    /**
     *
     * @TODO
     */

    @GetMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public void loginUser(@RequestBody UserDto userDto) {


    }

}
