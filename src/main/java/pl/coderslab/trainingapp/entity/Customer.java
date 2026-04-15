package pl.coderslab.trainingapp.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Customer extends User{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="trainer_id")
    private Trainer trainer;


}
