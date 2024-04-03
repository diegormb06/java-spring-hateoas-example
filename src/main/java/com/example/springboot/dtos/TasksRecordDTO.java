package com.example.springboot.dtos;

import jakarta.validation.constraints.NotBlank;

public record TasksRecordDTO(@NotBlank String title, @NotBlank String description) {
}
