package com.robertroman.store_admin_backend.service;

import com.robertroman.store_admin_backend.entity.Local;
import com.robertroman.store_admin_backend.entity.Producto;
import com.robertroman.store_admin_backend.entity.ProductoLocal;
import com.robertroman.store_admin_backend.repository.LocalRepository;
import com.robertroman.store_admin_backend.repository.ProductoLocalRepository;
import com.robertroman.store_admin_backend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductoLocalService {

    @Autowired
    private ProductoLocalRepository productoLocalRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private LocalRepository localRepository;

    @Autowired
    private LocalService localService;

    // Asignar producto a local (requerimiento principal)
    public ProductoLocal asignarProductoALocal(Long productoId, Long localId, Integer stock,
                                               BigDecimal precioVenta, Integer stockMinimo, Long usuarioId) {

        // Validar acceso al local
        localService.validarAccesoLocal(localId, usuarioId);

        // Verificar que existe el producto
        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (productoOpt.isEmpty()) {
            throw new RuntimeException("Producto no encontrado con ID: " + productoId);
        }

        // Verificar que existe el local
        Optional<Local> localOpt = localRepository.findById(localId);
        if (localOpt.isEmpty()) {
            throw new RuntimeException("Local no encontrado con ID: " + localId);
        }

        // Verificar que no esté ya asignado
        if (productoLocalRepository.existsByProductoIdAndLocalIdAndActivoTrue(productoId, localId)) {
            throw new RuntimeException("El producto ya está asignado a este local");
        }

        // Crear asignación
        ProductoLocal productoLocal = new ProductoLocal();
        productoLocal.setProducto(productoOpt.get());
        productoLocal.setLocal(localOpt.get());
        productoLocal.setStock(stock != null ? stock : 0);
        productoLocal.setStockMinimo(stockMinimo != null ? stockMinimo : 0);
        productoLocal.setPrecioVenta(precioVenta != null ? precioVenta : productoOpt.get().getPrecioBase());
        productoLocal.setActivo(true);


        return productoLocalRepository.save(productoLocal);
    }

    // Actualizar stock (requerimiento principal)
    public ProductoLocal actualizarStock(Long productoId, Long localId, Integer nuevoStock, Long usuarioId) {
        // Validar acceso al local
        localService.validarAccesoLocal(localId, usuarioId);

        Optional<ProductoLocal> productoLocalOpt = productoLocalRepository.findByProductoIdAndLocalId(productoId, localId);
        if (productoLocalOpt.isEmpty()) {
            throw new RuntimeException("Producto no asignado a este local");
        }

        ProductoLocal productoLocal = productoLocalOpt.get();
        productoLocal.setStock(nuevoStock);

        return productoLocalRepository.save(productoLocal);
    }

    // Actualizar precio de venta en local
    public ProductoLocal actualizarPrecioVenta(Long productoId, Long localId, BigDecimal nuevoPrecio, Long usuarioId) {
        // Validar acceso al local
        localService.validarAccesoLocal(localId, usuarioId);

        Optional<ProductoLocal> productoLocalOpt = productoLocalRepository.findByProductoIdAndLocalId(productoId, localId);
        if (productoLocalOpt.isEmpty()) {
            throw new RuntimeException("Producto no asignado a este local");
        }

        ProductoLocal productoLocal = productoLocalOpt.get();
        productoLocal.setPrecioVenta(nuevoPrecio);

        return productoLocalRepository.save(productoLocal);
    }

    // Reducir stock (para ventas)
    public ProductoLocal reducirStock(Long productoId, Long localId, Integer cantidad) {
        Optional<ProductoLocal> productoLocalOpt = productoLocalRepository.findByProductoIdAndLocalId(productoId, localId);
        if (productoLocalOpt.isEmpty()) {
            throw new RuntimeException("Producto no asignado a este local");
        }

        ProductoLocal productoLocal = productoLocalOpt.get();

        if (!productoLocal.tieneStockSuficiente(cantidad)) {
            throw new RuntimeException("Stock insuficiente. Stock actual: " + productoLocal.getStock() +
                    ", cantidad solicitada: " + cantidad);
        }

        productoLocal.reducirStock(cantidad);
        return productoLocalRepository.save(productoLocal);
    }

    // Aumentar stock (para reposiciones)
    public ProductoLocal aumentarStock(Long productoId, Long localId, Integer cantidad, Long usuarioId) {
        // Validar acceso al local
        localService.validarAccesoLocal(localId, usuarioId);

        Optional<ProductoLocal> productoLocalOpt = productoLocalRepository.findByProductoIdAndLocalId(productoId, localId);
        if (productoLocalOpt.isEmpty()) {
            throw new RuntimeException("Producto no asignado a este local");
        }

        ProductoLocal productoLocal = productoLocalOpt.get();
        productoLocal.aumentarStock(cantidad);

        return productoLocalRepository.save(productoLocal);
    }

    // Obtener productos de un local (para dashboard)
    public List<ProductoLocal> obtenerProductosDeLocal(Long localId, Long usuarioId) {
        // Validar acceso al local
        localService.validarAccesoLocal(localId, usuarioId);

        return productoLocalRepository.findByLocalIdAndActivoTrue(localId);
    }

    // Obtener stock de un producto en un local específico
    // Obtener stock de un producto en un local específico
    public Optional<Integer> obtenerStock(Long productoId, Long localId) {
        Optional<ProductoLocal> productoLocalOpt = productoLocalRepository.findByProductoIdAndLocalId(productoId, localId);
        if (productoLocalOpt.isPresent()) {
            return Optional.of(productoLocalOpt.get().getStock());
        }
        return Optional.empty();
    }

    // Obtener productos con stock bajo (para alertas)
    public List<ProductoLocal> obtenerProductosStockBajo(Long localId, Long usuarioId) {
        // Validar acceso al local
        localService.validarAccesoLocal(localId, usuarioId);

        // Obtener todos los productos del local y filtrar por stock bajo en memoria
        List<ProductoLocal> productosLocal = productoLocalRepository.findByLocalIdAndActivoTrue(localId);
        return productosLocal.stream()
                .filter(ProductoLocal::estaEnStockMinimo)
                .toList();
    }

    // Obtener productos sin stock
    public List<ProductoLocal> obtenerProductosSinStock(Long localId, Long usuarioId) {
        // Validar acceso al local
        localService.validarAccesoLocal(localId, usuarioId);

        return productoLocalRepository.findByLocalIdAndStockAndActivoTrue(localId, 0);
    }

    // Obtener valor total del inventario de un local
    public Optional<BigDecimal> obtenerValorInventario(Long localId, Long usuarioId) {
        // Validar acceso al local
        localService.validarAccesoLocal(localId, usuarioId);

        // Calcular valor en memoria
        List<ProductoLocal> productos = productoLocalRepository.findByLocalIdAndActivoTrue(localId);
        BigDecimal valorTotal = productos.stream()
                .map(pl -> pl.getPrecioVenta().multiply(new BigDecimal(pl.getStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Optional.of(valorTotal);
    }

    // Obtener locales donde está disponible un producto
    public List<ProductoLocal> obtenerLocalesConProducto(Long productoId) {
        return productoLocalRepository.findByProductoIdAndActivoTrue(productoId);
    }

    // Desasignar producto de local
    public void desasignarProductoDeLocal(Long productoId, Long localId, Long usuarioId) {
        // Validar acceso al local
        localService.validarAccesoLocal(localId, usuarioId);

        Optional<ProductoLocal> productoLocalOpt = productoLocalRepository.findByProductoIdAndLocalId(productoId, localId);
        if (productoLocalOpt.isPresent()) {
            ProductoLocal productoLocal = productoLocalOpt.get();
            productoLocal.setActivo(false);
            productoLocalRepository.save(productoLocal);
        } else {
            throw new RuntimeException("Producto no asignado a este local");
        }
    }

    // Verificar disponibilidad para venta
    public boolean verificarDisponibilidadParaVenta(Long productoId, Long localId, Integer cantidad) {
        Optional<ProductoLocal> productoLocalOpt = productoLocalRepository.findByProductoIdAndLocalId(productoId, localId);
        if (productoLocalOpt.isPresent()) {
            return productoLocalOpt.get().tieneStockSuficiente(cantidad);
        }
        return false;
    }

    // Obtener ProductoLocal por ID
    public Optional<ProductoLocal> obtenerPorId(Long id) {
        return productoLocalRepository.findById(id);
    }

    // Dashboard: Resumen de inventario por local
    public ResumenInventario obtenerResumenInventario(Long localId, Long usuarioId) {
        // Validar acceso al local
        localService.validarAccesoLocal(localId, usuarioId);

        List<ProductoLocal> productos = productoLocalRepository.findByLocalIdAndActivoTrue(localId);

        ResumenInventario resumen = new ResumenInventario();
        resumen.setTotalProductos(productos.size());
        resumen.setProductosStockBajo((int) productos.stream().filter(ProductoLocal::estaEnStockMinimo).count());
        resumen.setProductosSinStock((int) productos.stream().filter(p -> p.getStock() == 0).count());

        // Calcular valor total del inventario en memoria
        BigDecimal valorTotal = productos.stream()
                .map(pl -> pl.getPrecioVenta().multiply(new BigDecimal(pl.getStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        resumen.setValorTotalInventario(valorTotal);

        return resumen;
    }


    // Clase interna para resumen de inventario
    public static class ResumenInventario {
        private Integer totalProductos;
        private Integer productosStockBajo;
        private Integer productosSinStock;
        private BigDecimal valorTotalInventario;

        // Getters y Setters
        public Integer getTotalProductos() { return totalProductos; }
        public void setTotalProductos(Integer totalProductos) { this.totalProductos = totalProductos; }

        public Integer getProductosStockBajo() { return productosStockBajo; }
        public void setProductosStockBajo(Integer productosStockBajo) { this.productosStockBajo = productosStockBajo; }

        public Integer getProductosSinStock() { return productosSinStock; }
        public void setProductosSinStock(Integer productosSinStock) { this.productosSinStock = productosSinStock; }

        public BigDecimal getValorTotalInventario() { return valorTotalInventario; }
        public void setValorTotalInventario(BigDecimal valorTotalInventario) { this.valorTotalInventario = valorTotalInventario; }
    }
}