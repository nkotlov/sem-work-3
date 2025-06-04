package com.example.moviesbymood.services;

import com.example.moviesbymood.converters.GenreConverter;
import com.example.moviesbymood.dto.GenreDto;
import com.example.moviesbymood.models.Genre;
import com.example.moviesbymood.repositories.GenreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepo;
    private final GenreConverter genreConverter;

    @Override
    public List<Genre> findAll() {
        return genreRepo.findAll();
    }

    @Override
    public Genre create(Genre genre) {
        return genreRepo.save(genre);
    }

    @Override
    public Genre update(Long id, Genre genre) {
        Genre existing = genreRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Жанр не найден: " + id));
        existing.setGenreName(genre.getGenreName());
        return genreRepo.save(existing);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        genreRepo.deleteMovieGenresByGenreId(id);
        genreRepo.deleteById(id);
    }

    @Override
    public Genre findById(Long id) {
        return genreRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Жанр не найден: " + id));
    }

    @Override
    public List<GenreDto> findAllDto() {
        return genreRepo.findAll().stream()
                .map(GenreDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!genreRepo.existsById(id)) {
            throw new EntityNotFoundException("Жанр не найден: " + id);
        }
        genreRepo.deleteMovieGenresByGenreId(id);
        genreRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GenreDto> searchByName(String q) {
        return genreRepo.findByGenreNameContainingIgnoreCase(q).stream()
                .map(genreConverter::convert)
                .collect(Collectors.toList());
    }
}
