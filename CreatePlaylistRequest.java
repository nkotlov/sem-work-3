package com.example.moviesbymood.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePlaylistRequest {

    @NotBlank(message = "Название плейлиста не может быть пустым")
    private String playlistName;
    private String playlistDescription;
    private List<Long> movieIds;
    private Long posterFileId;
    private String posterFilename;
}
