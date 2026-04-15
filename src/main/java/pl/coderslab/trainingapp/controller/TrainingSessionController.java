package pl.coderslab.trainingapp.controller;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.trainingapp.dto.*;
import pl.coderslab.trainingapp.dto.api.CreateSessionExercisesRequest;
import pl.coderslab.trainingapp.dto.api.CreateTrainingSessionRequest;
import pl.coderslab.trainingapp.dto.api.UpdateTrainingSessionRequest;
import pl.coderslab.trainingapp.service.SessionExerciseService;
import pl.coderslab.trainingapp.service.TrainingSessionService;


import java.util.List;

@RestController
@RequestMapping("/api")

@AllArgsConstructor
public class TrainingSessionController {

    private final TrainingSessionService trainingSessionService;
    private final SessionExerciseService sessionExerciseService;


    @PostMapping("/sessions")
    public ResponseEntity<TrainingSessionDto> createSession(@AuthenticationPrincipal String emailTrainer,
                                                            @Valid @RequestBody CreateTrainingSessionRequest request) {
        TrainingSessionDto session = trainingSessionService.createSession(emailTrainer, request);
        return ResponseEntity.ok(session);
    }

    @PutMapping("/sessions/{sessionId}")
    public TrainingSessionDto updateTrainingSession(@AuthenticationPrincipal String emailTrainer,
                                                    @Valid @RequestBody UpdateTrainingSessionRequest request,
                                                    @PathVariable("sessionId") Long sessionId) {
        return trainingSessionService.updateSession(emailTrainer, request, sessionId);
    }

    @GetMapping("/sessions")
    public List<TrainingSessionDto> getSessions(@AuthenticationPrincipal String email) {
        return trainingSessionService.getSessions(email);
    }

    @PostMapping("/sessions/{trainingSessionId}/exercises")
    public SessionExerciseDto addExercisesToSession(@AuthenticationPrincipal String emailTrainer,
                                                    @PathVariable Long trainingSessionId,
                                                    @Valid @RequestBody CreateSessionExercisesRequest request) {

        return sessionExerciseService.addExerciseToSession(emailTrainer, trainingSessionId, request);
    }


    @GetMapping("/sessions/{trainingSessionId}/exercises")
    public List<SessionExerciseDto> getAllSessionsExercisesById(@AuthenticationPrincipal String email,
                                                       @PathVariable Long trainingSessionId) {
        return sessionExerciseService.getSessionExercises(email, trainingSessionId);
    }


    @DeleteMapping("/sessions/{sessionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSession(@AuthenticationPrincipal String trainerEmail,
                              @PathVariable Long sessionId) {
        trainingSessionService.deleteSession(trainerEmail, sessionId);
    }

    @DeleteMapping("/sessions/{sessionId}/exercises/{exerciseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeExerciseFromSession (@AuthenticationPrincipal String trainerEmail,
                                           @PathVariable Long sessionId,
                                           @PathVariable Long exerciseId){
        sessionExerciseService.removeExerciseFromSession(trainerEmail, sessionId, exerciseId);
    }
}
