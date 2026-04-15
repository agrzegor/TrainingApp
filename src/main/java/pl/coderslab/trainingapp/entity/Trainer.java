package pl.coderslab.trainingapp.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import java.util.List;


@Entity
@Setter
@Getter
public class Trainer extends User {

    @Column(unique = true)
    private String identifier;

    @OneToMany(mappedBy = "trainer",cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Customer> customers;

}
