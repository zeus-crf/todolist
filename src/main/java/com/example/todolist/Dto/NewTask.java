package com.example.todolist.Dto;

import com.example.todolist.Model.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record NewTask(
        String description,
        String title,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String priority,
        UUID userId
) {}

