package com.robertroman.store_admin_backend.controller;

import com.robertroman.store_admin_backend.service.DashboardService;
import com.robertroman.store_admin_backend.service.JwtService;
import com.robertroman.store_admin_backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    // REQUERIMIENTO PRINCIPAL: Dashboard completo con stocks y ventas
    @GetMapping("/local/{localId}")
    public ResponseEntity<?> obtenerDashboardCompleto(
            @PathVariable Long localId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            DashboardService.DashboardData dashboard = dashboardService.obtenerDashboardCompleto(
                    localId, usuarioId, fechaInicio, fechaFin);
            return ResponseEntity.ok(dashboard);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Dashboard del día actual
    @GetMapping("/local/{localId}/hoy")
    public ResponseEntity<?> obtenerDashboardHoy(@PathVariable Long localId,
                                                 @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);

            LocalDateTime inicioDia = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime finDia = inicioDia.plusDays(1).minusNanos(1);

            DashboardService.DashboardData dashboard = dashboardService.obtenerDashboardCompleto(
                    localId, usuarioId, inicioDia, finDia);
            return ResponseEntity.ok(dashboard);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Dashboard del mes actual
    @GetMapping("/local/{localId}/mes")
    public ResponseEntity<?> obtenerDashboardMes(@PathVariable Long localId,
                                                 @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);

            LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1)
                    .withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime finMes = inicioMes.plusMonths(1).minusNanos(1);

            DashboardService.DashboardData dashboard = dashboardService.obtenerDashboardCompleto(
                    localId, usuarioId, inicioMes, finMes);
            return ResponseEntity.ok(dashboard);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Productos más vendidos
    @GetMapping("/local/{localId}/productos-mas-vendidos")
    public ResponseEntity<?> obtenerProductosMasVendidos(@PathVariable Long localId,
                                                         @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            // Validar acceso (se hace internamente en el servicio)
            List<DashboardService.ProductoVendido> productos = dashboardService.obtenerProductosMasVendidos(localId);
            return ResponseEntity.ok(productos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Ventas por categoría
    @GetMapping("/local/{localId}/ventas-por-categoria")
    public ResponseEntity<?> obtenerVentasPorCategoria(
            @PathVariable Long localId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            List<DashboardService.VentaCategoria> ventasPorCategoria = dashboardService.obtenerVentasPorCategoria(
                    localId, fechaInicio, fechaFin);
            return ResponseEntity.ok(ventasPorCategoria);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Alertas de stock bajo
    @GetMapping("/local/{localId}/alertas-stock")
    public ResponseEntity<?> obtenerAlertasStock(@PathVariable Long localId,
                                                 @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            List<DashboardService.AlertaStock> alertas = dashboardService.obtenerAlertasStock(localId, usuarioId);
            return ResponseEntity.ok(alertas);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Comparación de ventas entre dos períodos
    @GetMapping("/local/{localId}/comparar-ventas")
    public ResponseEntity<?> compararVentas(
            @PathVariable Long localId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodo1Inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodo1Fin,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodo2Inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodo2Fin,
            @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            DashboardService.ComparacionVentas comparacion = dashboardService.compararVentas(
                    localId, usuarioId, periodo1Inicio, periodo1Fin, periodo2Inicio, periodo2Fin);
            return ResponseEntity.ok(comparacion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Análisis de rentabilidad por producto
    @GetMapping("/local/{localId}/rentabilidad-productos")
    public ResponseEntity<?> obtenerRentabilidadProductos(
            @PathVariable Long localId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            List<DashboardService.RentabilidadProducto> rentabilidad = dashboardService.obtenerRentabilidadProductos(
                    localId, usuarioId, fechaInicio, fechaFin);
            return ResponseEntity.ok(rentabilidad);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Dashboard simplificado para vista rápida
    @GetMapping("/local/{localId}/resumen")
    public ResponseEntity<?> obtenerResumenRapido(@PathVariable Long localId,
                                                  @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);

            // Dashboard del día actual simplificado
            LocalDateTime inicioDia = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime finDia = inicioDia.plusDays(1).minusNanos(1);

            DashboardService.DashboardData dashboard = dashboardService.obtenerDashboardCompleto(
                    localId, usuarioId, inicioDia, finDia);

            // Crear resumen simplificado
            ResumenRapido resumen = new ResumenRapido();
            resumen.setVentasHoy(dashboard.getTotalVentas());
            resumen.setCantidadVentasHoy(dashboard.getCantidadVentas());
            resumen.setPromedioVenta(dashboard.getPromedioVenta());
            resumen.setValorInventario(dashboard.getValorInventario());
            resumen.setProductosStockBajo(dashboard.getProductosStockBajo());
            resumen.setProductosSinStock(dashboard.getProductosSinStock());

            return ResponseEntity.ok(resumen);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Comparación mes actual vs mes anterior
    @GetMapping("/local/{localId}/comparacion-mensual")
    public ResponseEntity<?> obtenerComparacionMensual(@PathVariable Long localId,
                                                       @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);

            // Mes actual
            LocalDateTime inicioMesActual = LocalDateTime.now().withDayOfMonth(1)
                    .withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime finMesActual = inicioMesActual.plusMonths(1).minusNanos(1);

            // Mes anterior
            LocalDateTime inicioMesAnterior = inicioMesActual.minusMonths(1);
            LocalDateTime finMesAnterior = inicioMesActual.minusNanos(1);

            DashboardService.ComparacionVentas comparacion = dashboardService.compararVentas(
                    localId, usuarioId, inicioMesActual, finMesActual, inicioMesAnterior, finMesAnterior);

            return ResponseEntity.ok(comparacion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
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
    public static class ResumenRapido {
        private java.math.BigDecimal ventasHoy;
        private Long cantidadVentasHoy;
        private java.math.BigDecimal promedioVenta;
        private java.math.BigDecimal valorInventario;
        private Integer productosStockBajo;
        private Integer productosSinStock;

        // Getters y Setters
        public java.math.BigDecimal getVentasHoy() { return ventasHoy; }
        public void setVentasHoy(java.math.BigDecimal ventasHoy) { this.ventasHoy = ventasHoy; }

        public Long getCantidadVentasHoy() { return cantidadVentasHoy; }
        public void setCantidadVentasHoy(Long cantidadVentasHoy) { this.cantidadVentasHoy = cantidadVentasHoy; }

        public java.math.BigDecimal getPromedioVenta() { return promedioVenta; }
        public void setPromedioVenta(java.math.BigDecimal promedioVenta) { this.promedioVenta = promedioVenta; }

        public java.math.BigDecimal getValorInventario() { return valorInventario; }
        public void setValorInventario(java.math.BigDecimal valorInventario) { this.valorInventario = valorInventario; }

        public Integer getProductosStockBajo() { return productosStockBajo; }
        public void setProductosStockBajo(Integer productosStockBajo) { this.productosStockBajo = productosStockBajo; }

        public Integer getProductosSinStock() { return productosSinStock; }
        public void setProductosSinStock(Integer productosSinStock) { this.productosSinStock = productosSinStock; }
    }

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