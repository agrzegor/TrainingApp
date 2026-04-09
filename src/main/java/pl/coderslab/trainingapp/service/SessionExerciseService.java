package pl.coderslab.trainingapp.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import pl.coderslab.trainingapp.repository.SessionExerciseRepository;

@Service
@Setter
@Getter
@Builder
@AllArgsConstructor
public class SessionExerciseService {

    private final SessionExerciseRepository sessionExerciseRepository;



}
