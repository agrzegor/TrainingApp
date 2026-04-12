package pl.coderslab.trainingapp.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity(name = "exercise")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(unique = true)
    private String externalExerciseId;

}
