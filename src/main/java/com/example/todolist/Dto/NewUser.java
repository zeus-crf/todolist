package com.example.todolist.Dto;

import java.time.LocalDateTime;

public record NewUser(String username, String email, String password, LocalDateTime createdAt) {
}
