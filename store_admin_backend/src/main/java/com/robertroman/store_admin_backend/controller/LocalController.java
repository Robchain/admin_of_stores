package com.robertroman.store_admin_backend.controller;

import com.robertroman.store_admin_backend.entity.Local;
import com.robertroman.store_admin_backend.service.JwtService;
import com.robertroman.store_admin_backend.service.LocalService;
import com.robertroman.store_admin_backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/locales")
@CrossOrigin(origins = "*")
public class LocalController {

    @Autowired
    private LocalService localService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    // Crear local
    @PostMapping
    public ResponseEntity<?> crearLocal(@Valid @RequestBody CrearLocalRequest request,
                                        @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            Local nuevoLocal = localService.crearLocal(request.toLocal(), usuarioId);
            return ResponseEntity.ok(nuevoLocal);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    // Obtener locales del usuario autenticado
    @GetMapping("/mis-locales")
    public ResponseEntity<?> obtenerMisLocales(@RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            List<Local> locales = localService.obtenerLocalesPorUsuario(usuarioId);
            return ResponseEntity.ok(locales);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Obtener local por ID (solo si el usuario es propietario)
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerLocal(@PathVariable Long id,
                                          @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            localService.validarAccesoLocal(id, usuarioId);

            Optional<Local> local = localService.obtenerPorId(id);
            if (local.isPresent()) {
                return ResponseEntity.ok(local.get());
            }
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Actualizar local
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarLocal(@PathVariable Long id,
                                             @Valid @RequestBody Local local,
                                             @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            localService.validarAccesoLocal(id, usuarioId);

            Local localActualizado = localService.actualizarLocal(id, local);
            return ResponseEntity.ok(localActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Buscar locales por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<Local>> buscarLocales(@RequestParam String nombre) {
        List<Local> locales = localService.buscarPorNombre(nombre);
        return ResponseEntity.ok(locales);
    }

    // Obtener locales por ciudad
    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<Local>> obtenerPorCiudad(@PathVariable String ciudad) {
        List<Local> locales = localService.obtenerPorCiudad(ciudad);
        return ResponseEntity.ok(locales);
    }

    // Obtener todas las ciudades
    @GetMapping("/ciudades")
    public ResponseEntity<List<String>> obtenerCiudades() {
        List<String> ciudades = localService.obtenerCiudades();
        return ResponseEntity.ok(ciudades);
    }

    // Desactivar local
    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivarLocal(@PathVariable Long id,
                                             @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            localService.desactivarLocal(id, usuarioId);
            return ResponseEntity.ok(new MessageResponse("Local desactivado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Activar local
    @PatchMapping("/{id}/activar")
    public ResponseEntity<?> activarLocal(@PathVariable Long id,
                                          @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            localService.activarLocal(id, usuarioId);
            return ResponseEntity.ok(new MessageResponse("Local activado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Obtener estadísticas del local
    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<?> obtenerEstadisticasLocal(@PathVariable Long id,
                                                      @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            localService.validarAccesoLocal(id, usuarioId);

            LocalService.LocalEstadisticas estadisticas = localService.obtenerEstadisticasLocal(id);
            return ResponseEntity.ok(estadisticas);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Método auxiliar para extraer usuario del token
    private Long obtenerUsuarioIdDelToken(String token) {
        String tokenLimpio = token.replace("Bearer ", "");
        String username = jwtService.extractUsername(tokenLimpio);

        return usuarioService.buscarPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
    }

    // DTOs
    public static class CrearLocalRequest {
        private String nombre;
        private String direccion;
        private String telefono;
        private String ciudad;

        public Local toLocal() {
            Local local = new Local();
            local.setNombre(this.nombre);
            local.setDireccion(this.direccion);
            local.setTelefono(this.telefono);
            local.setCiudad(this.ciudad);
            return local;
        }

        // Getters y Setters
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getDireccion() { return direccion; }
        public void setDireccion(String direccion) { this.direccion = direccion; }

        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }

        public String getCiudad() { return ciudad; }
        public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    }

    // Clases de respuesta reutilizables
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

    public static class MessageResponse {
        private String message;
        private long timestamp;

        public MessageResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}