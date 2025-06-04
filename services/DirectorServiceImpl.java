package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.DirectorDto;
import com.example.moviesbymood.models.Director;
import com.example.moviesbymood.repositories.DirectorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {

    private final DirectorRepository directorRepo;

    @Override
    public List<Director> findAll() {
        return directorRepo.findAll();
    }

    @Override
    public Director create(Director director) {
        return directorRepo.save(director);
    }

    @Override
    public Director update(Long id, Director director) {
        Director existing = directorRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Режиссёр не найден: " + id));
        existing.setDirectorFullName(director.getDirectorFullName());
        existing.setDirectorBirthDate(director.getDirectorBirthDate());
        existing.setDirectorBiography(director.getDirectorBiography());
        return directorRepo.save(existing);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        directorRepo.deleteMovieDirectorsByDirectorId(id);

        directorRepo.deleteById(id);
    }

    @Override
    public Director findById(Long id) {
        return directorRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Режиссёр не найден: " + id));
    }

    @Override
    public DirectorDto findDtoById(Long id) {
        Director d = directorRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Режиссёр не найден: " + id));
        return DirectorDto.builder()
                .directorId(d.getDirectorId())
                .directorFullName(d.getDirectorFullName())
                .directorBirthDate(d.getDirectorBirthDate())
                .directorBiography(d.getDirectorBiography())
                .directorPhoto(d.getDirectorPhoto() != null
                        ? "/files/" + d.getDirectorPhoto().getFileInfoFilename()
                        : null)
                .build();
    }

    @Override
    public List<DirectorDto> findAllDto() {
        return directorRepo.findAll().stream()
                .map(DirectorDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!directorRepo.existsById(id)) {
            throw new EntityNotFoundException("Режиссёр не найден: " + id);
        }
        directorRepo.deleteMovieDirectorsByDirectorId(id);
        directorRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DirectorDto> searchByName(String namePart) {
        return directorRepo
                .findByDirectorFullNameContainingIgnoreCase(namePart)
                .stream()
                .map(DirectorDto::fromEntity)
                .collect(Collectors.toList());
    }
}