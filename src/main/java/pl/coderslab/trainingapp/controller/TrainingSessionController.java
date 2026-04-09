package pl.coderslab.trainingapp.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.trainingapp.dto.CreateTrainingSessionRequest;
import pl.coderslab.trainingapp.dto.UpdateTrainingSessionRequest;
import pl.coderslab.trainingapp.dto.TrainingSessionDto;
import pl.coderslab.trainingapp.service.TrainingSessionService;

@RestController
@RequestMapping("/api")
public class TrainingSessionController {

    private final TrainingSessionService trainingSessionService;


    public TrainingSessionController(TrainingSessionService trainingSessionService) {
        this.trainingSessionService = trainingSessionService;
    }


    @PostMapping("/sessions")
    public ResponseEntity<TrainingSessionDto> createSession(@AuthenticationPrincipal String emailTrainer,
                                                            @RequestBody CreateTrainingSessionRequest request) {
        TrainingSessionDto session = trainingSessionService.createSession(emailTrainer, request);
        return ResponseEntity.ok(session);
    }

    @PutMapping("/sessions/{sessionId}")
    public TrainingSessionDto updateTrainingSession (@AuthenticationPrincipal String emailTrainer,
                                                     @RequestBody UpdateTrainingSessionRequest request,
                                                     @PathVariable("sessionId") Long sessionId){
      return trainingSessionService.updateSession(emailTrainer, request, sessionId);
    }
}
