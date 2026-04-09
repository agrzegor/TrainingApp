package pl.coderslab.trainingapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "session_exercise")
@Getter
@Setter
public class SessionExercise {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "training_session_id")
    private TrainingSession trainingSession;

        private Long reps;

        private Long series;

        private Long weight;

        @ManyToOne
        @JoinColumn(name ="exercise_id")
        private Exercise exercise;
}
