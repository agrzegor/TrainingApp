package pl.coderslab.trainingapp.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Customer extends User{

    @ManyToOne
    private Trainer trainer;
}
