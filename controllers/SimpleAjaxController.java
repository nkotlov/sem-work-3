package com.example.moviesbymood.controllers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class SimpleAjaxController {

    @GetMapping("/ajax")
    public String ajaxPage() {
        return "ajax";
    }

    @PostMapping(path = "/ajax", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> handleAjax(@RequestBody Map<String, String> payload) {
        String text = payload.getOrDefault("text", "ни о чём");
        return Map.of("message", "Сервер принял: " + text);
    }
}
