package com.robertroman.store_admin_backend.controller;

import com.robertroman.store_admin_backend.dto.AuthResponse;
import com.robertroman.store_admin_backend.dto.LoginRequest;
import com.robertroman.store_admin_backend.dto.RegisterRequest;
import com.robertroman.store_admin_backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Para desarrollo - cambiar en producción
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    // Endpoint para registrar usuario
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = usuarioService.registrarUsuario(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    // Endpoint para login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = usuarioService.loginUsuario(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    // Endpoint para verificar si username existe
    @GetMapping("/check-username/{username}")
    public ResponseEntity<AvailabilityResponse> checkUsername(@PathVariable String username) {
        boolean exists = usuarioService.existeUsername(username);
        return ResponseEntity.ok(new AvailabilityResponse(!exists,
                exists ? "Username no disponible" : "Username disponible"));
    }

    // Endpoint para verificar si email existe
    @GetMapping("/check-email/{email}")
    public ResponseEntity<AvailabilityResponse> checkEmail(@PathVariable String email) {
        boolean exists = usuarioService.existeEmail(email);
        return ResponseEntity.ok(new AvailabilityResponse(!exists,
                exists ? "Email no disponible" : "Email disponible"));
    }

    // Clase interna para respuestas de error
    public static class ErrorResponse {
        private String message;
        private long timestamp;

        public ErrorResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    // Clase interna para verificar disponibilidad
    public static class AvailabilityResponse {
        private boolean available;
        private String message;

        public AvailabilityResponse(boolean available, String message) {
            this.available = available;
            this.message = message;
        }

        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}