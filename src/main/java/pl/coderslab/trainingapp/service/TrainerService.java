package pl.coderslab.trainingapp.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.coderslab.trainingapp.dto.TrainerDto;
import pl.coderslab.trainingapp.dto.UserDto;
import pl.coderslab.trainingapp.entity.Trainer;
import pl.coderslab.trainingapp.mappers.Mapper;
import pl.coderslab.trainingapp.repository.TrainerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private Mapper mapper;
    private PasswordEncoder passwordEncoder;

    public Trainer createTrainer(UserDto userDto) {

        Trainer trainer = new Trainer();
        trainer.setFirstName(userDto.firstName());
        trainer.setLastName(userDto.lastName());
        trainer.setEmail(userDto.email());
        trainer.setPhone(userDto.phone());
        trainer.setUserType(userDto.userType());
        trainer.setIdentifier("#" + NanoIdUtils
                .randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR,NanoIdUtils.DEFAULT_ALPHABET,5));
        trainer.setPassword(passwordEncoder.encode(userDto.password()));
        return trainerRepository.save(trainer);
    }

    public List<TrainerDto> readAllTrainers (){
        List<TrainerDto> trainerDtos = new ArrayList<>();
        List<Trainer> trainers = trainerRepository.findAll();
        for (Trainer trainer : trainers) {
            trainerDtos.add(mapper.toDto(trainer));
        }
        return trainerDtos;
    }

    public TrainerDto updateTrainerDetails (UserDto userDto, Long id){
        Trainer trainer = trainerRepository.findById(id)
                .map(trainerUpd-> {
           trainerUpd.setEmail(userDto.email());
           trainerUpd.setFirstName(userDto.firstName());
           trainerUpd.setLastName(userDto.lastName());
           trainerUpd.setPhone(userDto.phone());
            return trainerUpd;
        }).orElseThrow(()->new NoSuchElementException("User with provided ID not exist."));

        return mapper.toDto(trainerRepository.save(trainer));
    }

    public void deleteTrainerAccount(Long id){
        trainerRepository.deleteById(id);
    }
}
