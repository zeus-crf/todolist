package com.example.todolist.repository;

import com.example.todolist.Model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface TaskRespository extends JpaRepository<Task, UUID> {
    List<Task> findByUserId(UUID userId);
}
