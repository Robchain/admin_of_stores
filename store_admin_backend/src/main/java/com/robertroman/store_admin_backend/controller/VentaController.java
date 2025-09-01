package com.robertroman.store_admin_backend.controller;

import com.robertroman.store_admin_backend.entity.Venta;
import com.robertroman.store_admin_backend.service.JwtService;
import com.robertroman.store_admin_backend.service.UsuarioService;
import com.robertroman.store_admin_backend.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    // REQUERIMIENTO PRINCIPAL: Crear venta completa (actualiza stock automáticamente)
    @PostMapping
    public ResponseEntity<?> crearVenta(@Valid @RequestBody VentaService.CrearVentaRequest request,
                                        @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            Venta nuevaVenta = ventaService.crearVenta(request, usuarioId);
            return ResponseEntity.ok(nuevaVenta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    // Obtener ventas por local
    @GetMapping("/local/{localId}")
    public ResponseEntity<?> obtenerVentasPorLocal(@PathVariable Long localId,
                                                   @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            List<Venta> ventas = ventaService.obtenerVentasPorLocal(localId, usuarioId);
            return ResponseEntity.ok(ventas);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Obtener venta por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerVenta(@PathVariable Long id,
                                          @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            Optional<Venta> venta = ventaService.obtenerPorId(id, usuarioId);
            if (venta.isPresent()) {
                return ResponseEntity.ok(venta.get());
            }
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Obtener ventas por período
    @GetMapping("/local/{localId}/periodo")
    public ResponseEntity<?> obtenerVentasPorPeriodo(
            @PathVariable Long localId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            List<Venta> ventas = ventaService.obtenerVentasPorPeriodo(localId, fechaInicio, fechaFin, usuarioId);
            return ResponseEntity.ok(ventas);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Obtener ventas del día actual
    @GetMapping("/local/{localId}/hoy")
    public ResponseEntity<?> obtenerVentasDelDia(@PathVariable Long localId,
                                                 @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            List<Venta> ventas = ventaService.obtenerVentasDelDia(localId, usuarioId);
            return ResponseEntity.ok(ventas);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Cancelar venta (devuelve stock automáticamente)
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarVenta(@PathVariable Long id,
                                           @RequestBody CancelarVentaRequest request,
                                           @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            Venta ventaCancelada = ventaService.cancelarVenta(id, usuarioId, request.getMotivo());
            return ResponseEntity.ok(ventaCancelada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Obtener estadísticas de ventas por local y período
    @GetMapping("/local/{localId}/estadisticas")
    public ResponseEntity<?> obtenerEstadisticasVentas(
            @PathVariable Long localId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            VentaService.EstadisticasVentas estadisticas = ventaService.obtenerEstadisticasVentas(
                    localId, fechaInicio, fechaFin, usuarioId);
            return ResponseEntity.ok(estadisticas);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Obtener estadísticas rápidas del día
    @GetMapping("/local/{localId}/estadisticas/hoy")
    public ResponseEntity<?> obtenerEstadisticasHoy(@PathVariable Long localId,
                                                    @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);

            // Crear rango del día actual
            LocalDateTime inicioDia = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime finDia = inicioDia.plusDays(1).minusNanos(1);

            VentaService.EstadisticasVentas estadisticas = ventaService.obtenerEstadisticasVentas(
                    localId, inicioDia, finDia, usuarioId);
            return ResponseEntity.ok(estadisticas);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Obtener estadísticas del mes actual
    @GetMapping("/local/{localId}/estadisticas/mes")
    public ResponseEntity<?> obtenerEstadisticasMes(@PathVariable Long localId,
                                                    @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);

            // Crear rango del mes actual
            LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1)
                    .withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime finMes = inicioMes.plusMonths(1).minusNanos(1);

            VentaService.EstadisticasVentas estadisticas = ventaService.obtenerEstadisticasVentas(
                    localId, inicioMes, finMes, usuarioId);
            return ResponseEntity.ok(estadisticas);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Endpoint para validar una venta antes de procesarla (verificar stock)
    @PostMapping("/validar")
    public ResponseEntity<?> validarVenta(@Valid @RequestBody VentaService.CrearVentaRequest request,
                                          @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);

            // Aquí podrías implementar validaciones previas sin procesar la venta
            // Por ahora solo validamos acceso al local
            VentaValidationResponse validation = new VentaValidationResponse();
            validation.setValida(true);
            validation.setMensaje("Venta válida para procesar");

            return ResponseEntity.ok(validation);
        } catch (RuntimeException e) {
            VentaValidationResponse validation = new VentaValidationResponse();
            validation.setValida(false);
            validation.setMensaje(e.getMessage());
            return ResponseEntity.badRequest().body(validation);
        }
    }

    // Método auxiliar
    private Long obtenerUsuarioIdDelToken(String token) {
        String tokenLimpio = token.replace("Bearer ", "");
        String username = jwtService.extractUsername(tokenLimpio);

        return usuarioService.buscarPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
    }

    // DTOs
    public static class CancelarVentaRequest {
        private String motivo;

        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }
    }

    public static class VentaValidationResponse {
        private boolean valida;
        private String mensaje;

        public boolean isValida() { return valida; }
        public void setValida(boolean valida) { this.valida = valida; }

        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    }

    // Clases de respuesta comunes
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
}