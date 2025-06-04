package com.example.moviesbymood.repositories;

import com.example.moviesbymood.models.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("""
        SELECT DISTINCT m
        FROM Movie m
            LEFT JOIN m.movieMoods md
            LEFT JOIN m.movieGenres mg
            LEFT JOIN m.movieActors ma
            LEFT JOIN m.movieDirectors md2
        WHERE (:moodId     IS NULL OR md.moodId     = :moodId)
          AND (:genreId    IS NULL OR mg.genreId     = :genreId)
          AND (:actorId    IS NULL OR ma.actorId     = :actorId)
          AND (:directorId IS NULL OR md2.directorId = :directorId)
    """)
    Page<Movie> smartSearch(
            @Param("moodId")     Long moodId,
            @Param("genreId")    Long genreId,
            @Param("actorId")    Long actorId,
            @Param("directorId") Long directorId,
            Pageable pageable
    );

    @Query("""
        SELECT m
        FROM Movie m
        WHERE LOWER(m.movieTitle) LIKE CONCAT('%', LOWER(:q), '%')
    """)
    List<Movie> searchByTitle(@Param("q") String q);

    List<Movie> findByMovieMoods_MoodId(Long moodId);

    List<Movie> findByMovieGenres_GenreId(Long genreId);

    List<Movie> findByMovieActors_ActorId(Long actorId);

    List<Movie> findByMovieDirectors_DirectorId(Long directorId);

    List<Movie> findByMovieTitleContainingIgnoreCase(String titlePart);
}
