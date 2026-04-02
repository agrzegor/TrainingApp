package pl.coderslab.trainingapp.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.trainingapp.dto.UserDto;
import pl.coderslab.trainingapp.entity.Trainer;
import pl.coderslab.trainingapp.repository.TrainerRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;


    public Trainer createTrainer(UserDto userDto) {

        Trainer trainer = new Trainer();
        trainer.setFirstName(userDto.firstName());
        trainer.setLastName(userDto.lastName());
        trainer.setEmail(userDto.email());
        trainer.setPhone(userDto.phone());
        trainer.setUserType(userDto.userType());
        return trainerRepository.save(trainer);
    }

    public List<Trainer> readAllTrainers (){
        return trainerRepository.findAll();
    }

    public Trainer updateTrainerDetails (UserDto userDto, Long id){
        Trainer trainer = trainerRepository.findById(id)
                .map(trainerUpd-> {
           trainerUpd.setEmail(userDto.email());
           trainerUpd.setFirstName(userDto.firstName());
           trainerUpd.setLastName(userDto.lastName());
           trainerUpd.setPhone(userDto.phone());
            return trainerUpd;
        }).orElseThrow(()->new NoSuchElementException("User with provided ID not exist."));

        return trainerRepository.save(trainer);
    }

    public void deleteTrainerAccount(Long id){
        trainerRepository.deleteById(id);
    }
}
