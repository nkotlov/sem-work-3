package com.example.moviesbymood.services;

import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;

public interface FileStorageService {
    String saveFile(MultipartFile uploadFile);
    void writeFileToResponse(String filename, HttpServletResponse response);
}
