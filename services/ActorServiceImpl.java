package com.example.moviesbymood.services;

import com.example.moviesbymood.converters.ActorConverter;
import com.example.moviesbymood.dto.ActorDto;
import com.example.moviesbymood.models.Actor;
import com.example.moviesbymood.repositories.ActorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActorServiceImpl implements ActorService {

    private final ActorRepository actorRepo;
    private final ActorConverter actorConverter;


    @Override
    public List<Actor> findAll() {
        return actorRepo.findAll();
    }

    @Override
    public Actor create(Actor actor) {
        return actorRepo.save(actor);
    }

    @Override
    public Actor update(Long id, Actor actor) {
        Actor existing = actorRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Актёр не найден: " + id));
        existing.setActorFullName(actor.getActorFullName());
        existing.setActorBirthDate(actor.getActorBirthDate());
        existing.setActorBiography(actor.getActorBiography());
        return actorRepo.save(existing);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        actorRepo.deleteMovieActorsByActorId(id);

        actorRepo.deleteById(id);
    }

    @Override
    public Actor findById(Long id) {
        return actorRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Актёр не найден: " + id));
    }

    @Override
    public ActorDto findDtoById(Long id) {
        Actor a = actorRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Актёр не найден: " + id));
        return ActorDto.builder()
                .actorId(a.getActorId())
                .actorFullName(a.getActorFullName())
                .actorBirthDate(a.getActorBirthDate())
                .actorBiography(a.getActorBiography())
                .actorPhoto(a.getActorPhoto() != null
                        ? "/files/" + a.getActorPhoto().getFileInfoFilename()
                        : null)
                .build();
    }

    @Override
    public List<ActorDto> findAllDto() {
        return actorRepo.findAll().stream()
                .map(ActorDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!actorRepo.existsById(id)) {
            throw new EntityNotFoundException("Актёр не найден: " + id);
        }
        actorRepo.deleteMovieActorsByActorId(id);
        actorRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActorDto> searchByName(String q) {
        return actorRepo.findByActorFullNameContainingIgnoreCase(q).stream()
                .map(actorConverter::convert)
                .collect(Collectors.toList());
    }
}
