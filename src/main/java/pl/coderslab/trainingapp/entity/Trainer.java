package pl.coderslab.trainingapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Setter
@Getter
public class Trainer extends User {

    private boolean certified;
    private String identifier;

    @OneToMany
    private List<Customer> customerList;
}
