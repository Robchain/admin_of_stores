package com.robertroman.store_admin_backend.service;

import com.robertroman.store_admin_backend.entity.Producto;
import com.robertroman.store_admin_backend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // Crear producto
    public Producto crearProducto(Producto producto) {
        // Validar que no exista el SKU si está presente
        if (producto.getSku() != null && productoRepository.existsBySku(producto.getSku())) {
            throw new RuntimeException("Ya existe un producto con el SKU: " + producto.getSku());
        }

        return productoRepository.save(producto);
    }

    // Actualizar producto
    public Producto actualizarProducto(Long id, Producto productoActualizado) {
        Optional<Producto> productoExistente = productoRepository.findById(id);

        if (productoExistente.isEmpty()) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }

        Producto producto = productoExistente.get();

        // Validar SKU solo si cambió
        if (productoActualizado.getSku() != null &&
                !productoActualizado.getSku().equals(producto.getSku()) &&
                productoRepository.existsBySku(productoActualizado.getSku())) {
            throw new RuntimeException("Ya existe un producto con el SKU: " + productoActualizado.getSku());
        }

        // Actualizar campos
        producto.setNombre(productoActualizado.getNombre());
        producto.setDescripcion(productoActualizado.getDescripcion());
        producto.setPrecioBase(productoActualizado.getPrecioBase());
        producto.setCategoria(productoActualizado.getCategoria());
        producto.setSku(productoActualizado.getSku());
        producto.setActivo(productoActualizado.getActivo());

        return productoRepository.save(producto);
    }

    // Obtener producto por ID
    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    // Obtener todos los productos activos
    public List<Producto> obtenerProductosActivos() {
        return productoRepository.findByActivoTrue();
    }

    // Buscar productos por nombre
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // Obtener productos por categoría
    public List<Producto> obtenerPorCategoria(String categoria) {
        return productoRepository.findByCategoriaAndActivoTrue(categoria);
    }

    // Obtener producto por SKU
    public Optional<Producto> obtenerPorSku(String sku) {
        return productoRepository.findBySku(sku);
    }

    // Obtener todas las categorías
    public List<String> obtenerCategorias() {
        return productoRepository.findAllCategorias();
    }

    // Obtener productos sin asignar a locales
    public List<Producto> obtenerProductosSinAsignar() {
        return productoRepository.findProductosSinAsignar();
    }

    // Desactivar producto (soft delete)
    public void desactivarProducto(Long id) {
        Optional<Producto> productoOpt = productoRepository.findById(id);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            producto.setActivo(false);
            productoRepository.save(producto);
        } else {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
    }

    // Activar producto
    public void activarProducto(Long id) {
        Optional<Producto> productoOpt = productoRepository.findById(id);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            producto.setActivo(true);
            productoRepository.save(producto);
        } else {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
    }

    // Verificar disponibilidad de SKU
    public boolean esSKUDisponible(String sku) {
        return !productoRepository.existsBySku(sku);
    }

    // Obtener todos los productos (incluye inactivos)
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    // Eliminar producto completamente (usar con precaución)
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }
}