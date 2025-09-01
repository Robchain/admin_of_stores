package com.robertroman.store_admin_backend.controller;

import com.robertroman.store_admin_backend.entity.ProductoLocal;
import com.robertroman.store_admin_backend.service.JwtService;
import com.robertroman.store_admin_backend.service.ProductoLocalService;
import com.robertroman.store_admin_backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos-local")
@CrossOrigin(origins = "*")
public class ProductoLocalController {

    @Autowired
    private ProductoLocalService productoLocalService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    // REQUERIMIENTO PRINCIPAL: Asignar producto a local
    @PostMapping("/asignar")
    public ResponseEntity<?> asignarProductoALocal(@Valid @RequestBody AsignarProductoRequest request,
                                                   @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);

            ProductoLocal productoLocal = productoLocalService.asignarProductoALocal(
                    request.getProductoId(),
                    request.getLocalId(),
                    request.getStock(),
                    request.getPrecioVenta(),
                    request.getStockMinimo(),
                    usuarioId
            );

            return ResponseEntity.ok(productoLocal);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    // REQUERIMIENTO PRINCIPAL: Actualizar stock
    @PutMapping("/stock")
    public ResponseEntity<?> actualizarStock(@Valid @RequestBody ActualizarStockRequest request,
                                             @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);

            ProductoLocal productoLocal = productoLocalService.actualizarStock(
                    request.getProductoId(),
                    request.getLocalId(),
                    request.getNuevoStock(),
                    usuarioId
            );

            return ResponseEntity.ok(productoLocal);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Aumentar stock (reposiciones)
    @PatchMapping("/aumentar-stock")
    public ResponseEntity<?> aumentarStock(@Valid @RequestBody CambiarStockRequest request,
                                           @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);

            ProductoLocal productoLocal = productoLocalService.aumentarStock(
                    request.getProductoId(),
                    request.getLocalId(),
                    request.getCantidad(),
                    usuarioId
            );

            return ResponseEntity.ok(new MessageResponse("Stock aumentado exitosamente. Nuevo stock: " + productoLocal.getStock()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Obtener productos de un local (DASHBOARD)
    @GetMapping("/local/{localId}")
    public ResponseEntity<?> obtenerProductosDeLocal(@PathVariable Long localId,
                                                     @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            List<ProductoLocal> productos = productoLocalService.obtenerProductosDeLocal(localId, usuarioId);
            return ResponseEntity.ok(productos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Obtener stock de un producto específico en un local
    @GetMapping("/stock/{productoId}/{localId}")
    public ResponseEntity<?> obtenerStock(@PathVariable Long productoId,
                                          @PathVariable Long localId) {
        Optional<Integer> stock = productoLocalService.obtenerStock(productoId, localId);
        if (stock.isPresent()) {
            return ResponseEntity.ok(new StockResponse(stock.get()));
        }
        return ResponseEntity.notFound().build();
    }

    // Obtener productos con stock bajo (ALERTAS)
    @GetMapping("/local/{localId}/stock-bajo")
    public ResponseEntity<?> obtenerProductosStockBajo(@PathVariable Long localId,
                                                       @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            List<ProductoLocal> productos = productoLocalService.obtenerProductosStockBajo(localId, usuarioId);
            return ResponseEntity.ok(productos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Obtener productos sin stock
    @GetMapping("/local/{localId}/sin-stock")
    public ResponseEntity<?> obtenerProductosSinStock(@PathVariable Long localId,
                                                      @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            List<ProductoLocal> productos = productoLocalService.obtenerProductosSinStock(localId, usuarioId);
            return ResponseEntity.ok(productos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Obtener valor total del inventario
    @GetMapping("/local/{localId}/valor-inventario")
    public ResponseEntity<?> obtenerValorInventario(@PathVariable Long localId,
                                                    @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            Optional<BigDecimal> valor = productoLocalService.obtenerValorInventario(localId, usuarioId);
            if (valor.isPresent()) {
                return ResponseEntity.ok(new ValorInventarioResponse(valor.get()));
            }
            return ResponseEntity.ok(new ValorInventarioResponse(BigDecimal.ZERO));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Obtener resumen completo del inventario (DASHBOARD)
    @GetMapping("/local/{localId}/resumen")
    public ResponseEntity<?> obtenerResumenInventario(@PathVariable Long localId,
                                                      @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            ProductoLocalService.ResumenInventario resumen = productoLocalService.obtenerResumenInventario(localId, usuarioId);
            return ResponseEntity.ok(resumen);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Actualizar precio de venta
    @PatchMapping("/precio-venta")
    public ResponseEntity<?> actualizarPrecioVenta(@Valid @RequestBody ActualizarPrecioRequest request,
                                                   @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);

            ProductoLocal productoLocal = productoLocalService.actualizarPrecioVenta(
                    request.getProductoId(),
                    request.getLocalId(),
                    request.getNuevoPrecio(),
                    usuarioId
            );

            return ResponseEntity.ok(productoLocal);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Desasignar producto de local
    @DeleteMapping("/{productoId}/{localId}")
    public ResponseEntity<?> desasignarProducto(@PathVariable Long productoId,
                                                @PathVariable Long localId,
                                                @RequestHeader("Authorization") String token) {
        try {
            Long usuarioId = obtenerUsuarioIdDelToken(token);
            productoLocalService.desasignarProductoDeLocal(productoId, localId, usuarioId);
            return ResponseEntity.ok(new MessageResponse("Producto desasignado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Verificar disponibilidad para venta
    @GetMapping("/disponibilidad/{productoId}/{localId}/{cantidad}")
    public ResponseEntity<DisponibilidadResponse> verificarDisponibilidad(
            @PathVariable Long productoId,
            @PathVariable Long localId,
            @PathVariable Integer cantidad) {

        boolean disponible = productoLocalService.verificarDisponibilidadParaVenta(productoId, localId, cantidad);
        return ResponseEntity.ok(new DisponibilidadResponse(disponible,
                disponible ? "Stock suficiente" : "Stock insuficiente"));
    }

    // Método auxiliar
    private Long obtenerUsuarioIdDelToken(String token) {
        String tokenLimpio = token.replace("Bearer ", "");
        String username = jwtService.extractUsername(tokenLimpio);

        return usuarioService.buscarPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
    }

    // DTOs para requests
    public static class AsignarProductoRequest {
        private Long productoId;
        private Long localId;
        private Integer stock;
        private BigDecimal precioVenta;
        private Integer stockMinimo;

        // Getters y Setters
        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }

        public Long getLocalId() { return localId; }
        public void setLocalId(Long localId) { this.localId = localId; }

        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }

        public BigDecimal getPrecioVenta() { return precioVenta; }
        public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }

        public Integer getStockMinimo() { return stockMinimo; }
        public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    }

    public static class ActualizarStockRequest {
        private Long productoId;
        private Long localId;
        private Integer nuevoStock;

        // Getters y Setters
        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }

        public Long getLocalId() { return localId; }
        public void setLocalId(Long localId) { this.localId = localId; }

        public Integer getNuevoStock() { return nuevoStock; }
        public void setNuevoStock(Integer nuevoStock) { this.nuevoStock = nuevoStock; }
    }

    public static class CambiarStockRequest {
        private Long productoId;
        private Long localId;
        private Integer cantidad;

        // Getters y Setters
        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }

        public Long getLocalId() { return localId; }
        public void setLocalId(Long localId) { this.localId = localId; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }

    public static class ActualizarPrecioRequest {
        private Long productoId;
        private Long localId;
        private BigDecimal nuevoPrecio;

        // Getters y Setters
        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }

        public Long getLocalId() { return localId; }
        public void setLocalId(Long localId) { this.localId = localId; }

        public BigDecimal getNuevoPrecio() { return nuevoPrecio; }
        public void setNuevoPrecio(BigDecimal nuevoPrecio) { this.nuevoPrecio = nuevoPrecio; }
    }

    // DTOs para responses
    public static class StockResponse {
        private Integer stock;

        public StockResponse(Integer stock) { this.stock = stock; }

        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
    }

    public static class ValorInventarioResponse {
        private BigDecimal valor;

        public ValorInventarioResponse(BigDecimal valor) { this.valor = valor; }

        public BigDecimal getValor() { return valor; }
        public void setValor(BigDecimal valor) { this.valor = valor; }
    }

    public static class DisponibilidadResponse {
        private boolean disponible;
        private String mensaje;

        public DisponibilidadResponse(boolean disponible, String mensaje) {
            this.disponible = disponible;
            this.mensaje = mensaje;
        }

        public boolean isDisponible() { return disponible; }
        public void setDisponible(boolean disponible) { this.disponible = disponible; }

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



public static class MessageResponse {
    private String message;
    private long timestamp;

    public MessageResponse(String message) {
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    }
}