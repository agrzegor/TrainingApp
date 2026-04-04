package pl.coderslab.trainingapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;


@Entity
@Setter
@Getter
public class Trainer extends User {

    @Column(unique = true)
    private String identifier;

    @OneToMany(mappedBy = "trainer")
    private List<Customer> customers;

    /**
     *
     * Mozemy czytac ale nie dodawac
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

}
