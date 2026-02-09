package com.example.todolist.Dto;

import java.time.LocalDateTime;

public record EditTask(String description,
                       String title,
                       LocalDateTime startAt,
                       LocalDateTime endAt,
                       String priority) {
}
