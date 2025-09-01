package com.robertroman.store_admin_backend.controller;

import com.robertroman.store_admin_backend.entity.Producto;
import com.robertroman.store_admin_backend.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // Crear producto
    @PostMapping
    public ResponseEntity<?> crearProducto(@Valid @RequestBody Producto producto) {
        try {
            Producto nuevoProducto = productoService.crearProducto(producto);
            return ResponseEntity.ok(nuevoProducto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    // Obtener todos los productos activos
    @GetMapping
    public ResponseEntity<List<Producto>> obtenerProductos() {
        List<Producto> productos = productoService.obtenerProductosActivos();
        return ResponseEntity.ok(productos);
    }

    // Obtener producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProducto(@PathVariable Long id) {
        Optional<Producto> producto = productoService.obtenerPorId(id);
        if (producto.isPresent()) {
            return ResponseEntity.ok(producto.get());
        }
        return ResponseEntity.notFound().build();
    }

    // Actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id,
                                                @Valid @RequestBody Producto producto) {
        try {
            Producto productoActualizado = productoService.actualizarProducto(id, producto);
            return ResponseEntity.ok(productoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    // Buscar productos por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarProductos(@RequestParam String nombre) {
        List<Producto> productos = productoService.buscarPorNombre(nombre);
        return ResponseEntity.ok(productos);
    }

    // Obtener productos por categoría
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Producto>> obtenerPorCategoria(@PathVariable String categoria) {
        List<Producto> productos = productoService.obtenerPorCategoria(categoria);
        return ResponseEntity.ok(productos);
    }

    // Obtener todas las categorías
    @GetMapping("/categorias")
    public ResponseEntity<List<String>> obtenerCategorias() {
        List<String> categorias = productoService.obtenerCategorias();
        return ResponseEntity.ok(categorias);
    }

    // Verificar disponibilidad de SKU
    @GetMapping("/check-sku/{sku}")
    public ResponseEntity<AvailabilityResponse> checkSku(@PathVariable String sku) {
        boolean disponible = productoService.esSKUDisponible(sku);
        return ResponseEntity.ok(new AvailabilityResponse(disponible,
                disponible ? "SKU disponible" : "SKU ya existe"));
    }

    // Obtener productos sin asignar a locales
    @GetMapping("/sin-asignar")
    public ResponseEntity<List<Producto>> obtenerProductosSinAsignar() {
        List<Producto> productos = productoService.obtenerProductosSinAsignar();
        return ResponseEntity.ok(productos);
    }

    // Desactivar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivarProducto(@PathVariable Long id) {
        try {
            productoService.desactivarProducto(id);
            return ResponseEntity.ok(new MessageResponse("Producto desactivado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Activar producto
    @PatchMapping("/{id}/activar")
    public ResponseEntity<?> activarProducto(@PathVariable Long id) {
        try {
            productoService.activarProducto(id);
            return ResponseEntity.ok(new MessageResponse("Producto activado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Clases internas para respuestas
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