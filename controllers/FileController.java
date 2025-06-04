package com.example.moviesbymood.controllers;

import com.example.moviesbymood.services.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping("/files")
    public String filesPage() {
        return "files/upload";
    }

    @PostMapping("/files")
    @ResponseBody
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        String storedName = fileStorageService.saveFile(file);
        return ResponseEntity.ok(storedName);
    }

    @GetMapping("/files/{filename:.+}")
    public void serve(@PathVariable String filename,
                      HttpServletResponse response) {
        fileStorageService.writeFileToResponse(filename, response);
    }
}
