package pl.coderslab.trainingapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity (name = "training_session")
@Setter
@Getter
public class TrainingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @ManyToOne
    @JoinColumn (name = "customer_id")
    private Customer customer;

    private LocalDateTime createdAt;

    private LocalDateTime startDate;

    private int duration;


}
