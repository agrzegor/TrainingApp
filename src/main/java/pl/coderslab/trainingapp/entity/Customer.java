package pl.coderslab.trainingapp.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Entity
@Setter
@Getter
public class Customer extends User{

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="trainer_id")
    private Trainer trainer;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
