package com.example.moviesbymood.dto;

import com.example.moviesbymood.models.Actor;
import com.example.moviesbymood.models.FileInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActorDto {
    private Long   actorId;
    private String actorFullName;
    private LocalDate actorBirthDate;
    private String actorBiography;
    private String actorPhoto;

    public static ActorDto fromEntity(Actor actor) {
        if (actor == null) return null;
        String url = null;
        FileInfo fi = actor.getActorPhoto();
        if (fi != null) {
            url = fi.getFileInfoUrl();
        }
        return ActorDto.builder()
                .actorId(actor.getActorId())
                .actorFullName(actor.getActorFullName())
                .actorBirthDate(actor.getActorBirthDate())
                .actorBiography(actor.getActorBiography())
                .actorPhoto(url)
                .build();
    }

    public Actor toEntity() {
        Actor actor = new Actor();
        actor.setActorId(this.actorId);
        actor.setActorFullName(this.actorFullName);
        actor.setActorBirthDate(this.actorBirthDate);
        actor.setActorBiography(this.actorBiography);
        return actor;
    }
}
