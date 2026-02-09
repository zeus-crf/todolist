package com.example.todolist.Controller.user.task;

import com.example.todolist.Dto.EditTask;
import com.example.todolist.Dto.NewTask;
import com.example.todolist.Model.Task;
import com.example.todolist.Model.User;
import com.example.todolist.repository.TaskRespository;
import com.example.todolist.repository.UserRepository;
import com.example.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final UserRepository userRepository;
    private final TaskRespository taskRespository;

    public TaskController(TaskRespository taskRespository, UserRepository userRepository){
        this.taskRespository = taskRespository;
        this.userRepository = userRepository;
    }

    @PostMapping("/")
    public ResponseEntity<?> createTask(@RequestBody NewTask newTask, HttpServletRequest request) throws Exception {
        System.out.println("Chegou ao controller" + request.getAttribute("userId"));
        Task task = new Task();
        if (newTask.description() != null) task.setDescription(newTask.description());

        if (newTask.title() != null) task.setTitle(newTask.title());

        if (newTask.priority() != null) task.setPriority(newTask.priority());

        var today = LocalDateTime.now();

        if (newTask.startAt() != null && !today.isAfter(newTask.startAt()) && newTask.startAt().isBefore(newTask.endAt())){
            task.setStartAt(newTask.startAt());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início não pode ser antes da data de hoje, e nem depois da data de término");
        }

        if (newTask.endAt() != null && !today.isAfter(newTask.endAt())){
            task.setEndAt(newTask.endAt());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de término não pode ser antes da data de hoje");
        }

        Object userId = request.getAttribute("userId");

        User user = userRepository.findById((UUID) userId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        task.setUser(user);

        taskRespository.save(task);

        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editTask(
            @PathVariable UUID id,
            @RequestBody EditTask editTask,
            HttpServletRequest request
    ) {

        if (id == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "ID da task é obrigatório"
            );
        }

        UUID userId = (UUID) request.getAttribute("userId");
        System.out.println("USER ID NO CONTROLLER: " + userId);


        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Esse usuário não existe"
                ));

        Task task = taskRespository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task não encontrada"
                ));



        if (!task.getUser().getId().equals(userId) ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Esse usuário não tem permisãp para isso ");
        }

                Utils.copyNonNullProperties(editTask, task);

        taskRespository.save(task);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/")
    public List<Task> listTask(HttpServletRequest request){
        var id = request.getAttribute("userId");
        return taskRespository.findByUserId((UUID) id);
    }
}
