package pl.coderslab.trainingapp.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pl.coderslab.trainingapp.config.RapidApi;
import pl.coderslab.trainingapp.dto.ExerciseDto;
import pl.coderslab.trainingapp.dto.api.GetExerciseDetailsResponse;
import pl.coderslab.trainingapp.dto.api.SearchExerciseResponse;
import pl.coderslab.trainingapp.entity.Exercise;
import pl.coderslab.trainingapp.mappers.Mapper;
import pl.coderslab.trainingapp.repository.ExerciseRepository;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class ExerciseService {


    private final RapidApi rapidApi;

    private final ExerciseRepository exerciseRepository;
    private final Mapper mapper;
    private final RestTemplate restTemplate;

    public GetExerciseDetailsResponse getDetails(String externalId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-rapidapi-host", rapidApi.getHost());
            headers.set("x-rapidapi-key", rapidApi.getKey());
            headers.set("Content-Type", "application/json");

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromUri(URI.create(rapidApi.getUrl() + "/v1/exercises/" + externalId));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            GetExerciseDetailsResponse response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<GetExerciseDetailsResponse>() {
                    }
            ).getBody();
            return response;

        } catch (RestClientException e) {
            log.error("Failed to fetch exercise details for externalId={}", externalId, e);
            throw new RuntimeException("Failed to fetch exercise details for id: " + externalId, e);
        }

    }

    public List<ExerciseDto> getExercises(String search) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-rapidapi-host", rapidApi.getHost());
            headers.set("x-rapidapi-key", rapidApi.getKey());
            headers.set("Content-Type", "application/json");

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromUri(URI.create(rapidApi.getUrl() + "/v1/exercises/search"))
                            .queryParamIfPresent("search", Optional.ofNullable(search));
            HttpEntity<String> entity = new HttpEntity<>(headers);
            SearchExerciseResponse response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<SearchExerciseResponse>() {
                    }
            ).getBody();

            return response.data().stream()
                    .map(el -> exerciseRepository.findExerciseByExternalExerciseId(el.exerciseId())
                            .orElseGet(() -> exerciseRepository.save(Exercise.builder()
                                    .externalExerciseId(el.exerciseId())
                                    .name(el.name())
                                    .build())))
                    .map(mapper::toDto)
                    .toList();


        } catch (RestClientException e) {
            log.error("Failed to search exercises for query='{}'", search, e);
            throw new RuntimeException("Failed to search exercises", e);
        }
    }


    public ExerciseDto getDetailsByExerciseId(Long exerciseId) {
        Exercise exercise = exerciseRepository.findExerciseById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Not found"));

        String externalId = exercise.getExternalExerciseId();
        GetExerciseDetailsResponse response = getDetails(externalId);

        return mapper.toDto(exercise, response);
    }

    public Exercise getExerciseById(Long id) {
        return exerciseRepository.findExerciseById(id)
                .orElseThrow(() -> new NoSuchElementException("Exercise does not exist."));
    }
}
