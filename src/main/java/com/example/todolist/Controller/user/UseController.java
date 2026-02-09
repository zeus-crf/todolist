package com.example.todolist.Controller.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.todolist.Dto.EditUser;
import com.example.todolist.Dto.NewUser;
import com.example.todolist.Model.User;
import com.example.todolist.repository.UserRepository;
import com.example.todolist.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UseController {

    private final UserRepository userRepository;

    public UseController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody NewUser newUser){

      if (newUser.username() == null || newUser.username().isBlank()){
          return ResponseEntity.badRequest().body("Username é obrigatorio");
      }

      if (userRepository.existsByUsername(newUser.username()) && userRepository.existsByEmail(newUser.email())){
          return ResponseEntity.status(HttpStatus.CONFLICT).body("Esse usuário já existe");
      }

      if (userRepository.existsByUsername(newUser.username())){
          return ResponseEntity.status(HttpStatus.CONFLICT).body("Username em uso, escolha outro");
      }

      User user = new User();
      user.setUsername(newUser.username());
      if (newUser.email() != null) user.setEmail(newUser.email());
      user.setCreatedAt(LocalDateTime.now());

      var passwordHash = BCrypt.withDefaults().hashToString(12, newUser.password().toCharArray());
      if (newUser.password() != null) user.setPassword(passwordHash);
      userRepository.save(user);

      return ResponseEntity.status(HttpStatus.CREATED).body(user);

    }

    @GetMapping("/")
    public List<User> listAll(){
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<User> findOne(@PathVariable UUID id){
        return userRepository.findById(id).map(user -> {
            user.getUsername();
            user.getEmail();
            user.getCreatedAt();
            return user;
        });
    }


    @PutMapping("/{id}")
    public ResponseEntity<User> editUser( @PathVariable UUID id, @RequestBody EditUser editUser) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado "));


        Utils.copyNonNullProperties(editUser, user);

        userRepository.save(user);
        return ResponseEntity.ok(user);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletedUser(@PathVariable UUID id){
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);

            return ResponseEntity.ok(Map.of("Messagem", "Usuário excluído com sucesso"));
        })
                .orElse(ResponseEntity.notFound().build());
    }




}
