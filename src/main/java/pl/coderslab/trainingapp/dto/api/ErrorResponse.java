package pl.coderslab.trainingapp.dto.api;

public record ErrorResponse(int status, String message, long timestamp) {}
