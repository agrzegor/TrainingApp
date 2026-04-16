package pl.coderslab.trainingapp.service;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.coderslab.trainingapp.dto.UserDto;
import pl.coderslab.trainingapp.entity.User;
import pl.coderslab.trainingapp.mappers.Mapper;
import pl.coderslab.trainingapp.repository.UserRepository;

import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
@Transactional
public class UserService {

    private final TrainerService trainerService;
    private final UserRepository userRepository;
    private final CustomerService customerService;
    private final Mapper mapper;
    private final AuthenticationManager authenticationManager;

    public UserDto createUser(final UserDto userDto) {
        try {
            User createdUser = switch (userDto.userType()) {
                case TRAINER -> trainerService.createTrainer(userDto);
                case CUSTOMER -> customerService.createCustomer(userDto);
            };
            return mapper.toDto(createdUser);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Email already in use");
        }
    }

    public UserDetails authenticate(final UserDto userDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.email(), userDto.password())
        );
        return (UserDetails) authentication.getPrincipal();
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("User not found"));
    }

}
