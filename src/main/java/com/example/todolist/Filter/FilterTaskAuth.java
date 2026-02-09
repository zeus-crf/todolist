package com.example.todolist.Filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.todolist.Model.User;
import com.example.todolist.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public FilterTaskAuth(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String path = request.getServletPath();


        if (!path.startsWith("/tasks")) {
            chain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Basic ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token ausente");
            return;
        }

        try {
            String base64 = authorization.substring("Basic ".length()).trim();
            String decoded = new String(Base64.getDecoder().decode(base64));

            String[] credentials = decoded.split(":");
            String username = credentials[0];
            String password = credentials[1];

            User user = userRepository.findByUsername(username);

            if (user == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuário inválido");
                return;
            }

            boolean valid = BCrypt.verifyer()
                    .verify(password.toCharArray(), user.getPassword())
                    .verified;

            if (!valid) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Senha inválida");
                return;
            }

            request.setAttribute("userId", user.getId());
            chain.doFilter(request, response);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Erro de autenticação");
        }
    }
}

