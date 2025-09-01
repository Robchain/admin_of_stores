package com.robertroman.store_admin_backend.service;

import com.robertroman.store_admin_backend.entity.*;
import com.robertroman.store_admin_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private ProductoLocalRepository productoLocalRepository;

    @Autowired
    private LocalRepository localRepository;

    @Autowired
    private LocalService localService;

    // Crear venta completa (requerimiento principal)
    public Venta crearVenta(CrearVentaRequest request, Long usuarioId) {
        // Validar acceso al local
        localService.validarAccesoLocal(request.getLocalId(), usuarioId);

        // Verificar que existe el local
        Optional<Local> localOpt = localRepository.findById(request.getLocalId());
        if (localOpt.isEmpty()) {
            throw new RuntimeException("Local no encontrado con ID: " + request.getLocalId());
        }

        Local local = localOpt.get();

        // Crear la venta
        Venta venta = new Venta();
        venta.setLocal(local);
        venta.setMetodoPago(request.getMetodoPago());
        venta.setDescuento(request.getDescuento() != null ? request.getDescuento() : BigDecimal.ZERO);
        venta.setImpuestos(request.getImpuestos() != null ? request.getImpuestos() : BigDecimal.ZERO);
        venta.setObservaciones(request.getObservaciones());
        venta.setEstado(Venta.EstadoVenta.PENDIENTE);

        // Generar número de factura único
        venta.setNumeroFactura(generarNumeroFactura(request.getLocalId()));

        // Guardar venta temporal
        venta = ventaRepository.save(venta);

        // Procesar cada item de la venta
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemVentaRequest item : request.getItems()) {
            DetalleVenta detalle = crearDetalleVenta(venta, item);
            subtotal = subtotal.add(detalle.getSubtotal());
        }

        // Calcular totales
        venta.setSubtotal(subtotal);
        venta.setTotal(subtotal.add(venta.getImpuestos()).subtract(venta.getDescuento()));
        venta.setEstado(Venta.EstadoVenta.COMPLETADA);

        return ventaRepository.save(venta);
    }

    // Crear detalle de venta y actualizar stock
    private DetalleVenta crearDetalleVenta(Venta venta, ItemVentaRequest item) {
        // Buscar el producto en el local
        Optional<ProductLocal> productoLocalOpt = productoLocalRepository
                .findByProductoIdAndLocalId(item.getProductoId(), venta.getLocal().getId());

        if (productoLocalOpt.isEmpty()) {
            throw new RuntimeException("Producto con ID " + item.getProductoId() + " no está disponible en este local");
        }

        ProductLocal productoLocal = productoLocalOpt.get();

        // Verificar stock suficiente
        if (!productoLocal.tieneStockSuficiente(item.getCantidad())) {
            throw new RuntimeException("Stock insuficiente para el producto: " + productoLocal.getProducto().getNombre() +
                    ". Stock disponible: " + productoLocal.getStock() + ", solicitado: " + item.getCantidad());
        }

        // Crear detalle de venta
        BigDecimal precioUnitario = item.getPrecioUnitario() != null ?
                item.getPrecioUnitario() : productoLocal.getPrecioVenta();

        DetalleVenta detalle = new DetalleVenta(venta, productoLocal, item.getCantidad(), precioUnitario);
        detalle.setDescuentoItem(item.getDescuentoItem() != null ? item.getDescuentoItem() : BigDecimal.ZERO);
        detalle.calcularSubtotal();

        // Reducir stock automáticamente
        productoLocal.reducirStock(item.getCantidad());
        productoLocalRepository.save(productoLocal);

        return detalleVentaRepository.save(detalle);
    }

    // Generar número de factura único
    private String generarNumeroFactura(Long localId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String numeroBase = "L" + localId + "-" + timestamp;

        // Verificar unicidad
        int contador = 1;
        String numeroFactura = numeroBase;
        while (ventaRepository.existsByNumeroFactura(numeroFactura)) {
            numeroFactura = numeroBase + "-" + contador;
            contador++;
        }

        return numeroFactura;
    }

    // Obtener ventas por local
    public List<Venta> obtenerVentasPorLocal(Long localId, Long usuarioId) {
        // Validar acceso al local
        localService.validarAccesoLocal(localId, usuarioId);

        return ventaRepository.findByLocalId(localId);
    }

    // Obtener venta por ID
    public Optional<Venta> obtenerPorId(Long id, Long usuarioId) {
        Optional<Venta> ventaOpt = ventaRepository.findById(id);
        if (ventaOpt.isPresent()) {
            // Validar acceso al local de la venta
            localService.validarAccesoLocal(ventaOpt.get().getLocal().getId(), usuarioId);
        }
        return ventaOpt;
    }

    // Obtener ventas por período
    public List<Venta> obtenerVentasPorPeriodo(Long localId, LocalDateTime fechaInicio,
                                               LocalDateTime fechaFin, Long usuarioId) {
        // Validar acceso al local
        localService.validarAccesoLocal(localId, usuarioId);

        return ventaRepository.findByLocalIdAndFechaVentaBetween(localId, fechaInicio, fechaFin);
    }

    // Cancelar venta (devolver stock)
    public Venta cancelarVenta(Long ventaId, Long usuarioId, String motivo) {
        Optional<Venta> ventaOpt = ventaRepository.findById(ventaId);
        if (ventaOpt.isEmpty()) {
            throw new RuntimeException("Venta no encontrada con ID: " + ventaId);
        }

        Venta venta = ventaOpt.get();

        // Validar acceso al local
        localService.validarAccesoLocal(venta.getLocal().getId(), usuarioId);

        if (venta.getEstado() == Venta.EstadoVenta.CANCELADA) {
            throw new RuntimeException("La venta ya está cancelada");
        }

        // Devolver stock de todos los items
        List<DetalleVenta> detalles = detalleVentaRepository.findByVentaId(ventaId);
        for (DetalleVenta detalle : detalles) {
            ProductLocal productoLocal = detalle.getProductoLocal();
            productoLocal.aumentarStock(detalle.getCantidad());
            productoLocalRepository.save(productoLocal);
        }

        // Actualizar estado de la venta
        venta.setEstado(Venta.EstadoVenta.CANCELADA);
        venta.setObservaciones((venta.getObservaciones() != null ? venta.getObservaciones() + " | " : "") +
                "CANCELADA: " + motivo);

        return ventaRepository.save(venta);
    }

    // Obtener estadísticas de ventas por local
    public EstadisticasVentas obtenerEstadisticasVentas(Long localId, LocalDateTime fechaInicio,
                                                        LocalDateTime fechaFin, Long usuarioId) {
        // Validar acceso al local
        localService.validarAccesoLocal(localId, usuarioId);

        EstadisticasVentas estadisticas = new EstadisticasVentas();
        estadisticas.setTotalVentas(ventaRepository.findTotalVentasByLocalAndPeriodo(localId, fechaInicio, fechaFin));
        estadisticas.setCantidadVentas(ventaRepository.countVentasByLocalAndPeriodo(localId, fechaInicio, fechaFin));
        estadisticas.setPromedioVenta(ventaRepository.findPromedioVentaByLocal(localId).orElse(BigDecimal.ZERO));

        return estadisticas;
    }

    // Obtener ventas del día (usando rango de fechas)
    public List<Venta> obtenerVentasDelDia(Long localId, Long usuarioId) {
        // Validar acceso al local
        localService.validarAccesoLocal(localId, usuarioId);

        // Obtener inicio y fin del día actual
        LocalDateTime inicioDia = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime finDia = inicioDia.plusDays(1).minusNanos(1);

        return ventaRepository.findByLocalIdAndFechaVentaBetween(localId, inicioDia, finDia);
    }

    // Clases internas para DTOs

    public static class CrearVentaRequest {
        private Long localId;
        private List<ItemVentaRequest> items;
        private Venta.MetodoPago metodoPago;
        private BigDecimal descuento;
        private BigDecimal impuestos;
        private String observaciones;

        // Getters y Setters
        public Long getLocalId() { return localId; }
        public void setLocalId(Long localId) { this.localId = localId; }

        public List<ItemVentaRequest> getItems() { return items; }
        public void setItems(List<ItemVentaRequest> items) { this.items = items; }

        public Venta.MetodoPago getMetodoPago() { return metodoPago; }
        public void setMetodoPago(Venta.MetodoPago metodoPago) { this.metodoPago = metodoPago; }

        public BigDecimal getDescuento() { return descuento; }
        public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }

        public BigDecimal getImpuestos() { return impuestos; }
        public void setImpuestos(BigDecimal impuestos) { this.impuestos = impuestos; }

        public String getObservaciones() { return observaciones; }
        public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    }

    public static class ItemVentaRequest {
        private Long productoId;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal descuentoItem;

        // Getters y Setters
        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

        public BigDecimal getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

        public BigDecimal getDescuentoItem() { return descuentoItem; }
        public void setDescuentoItem(BigDecimal descuentoItem) { this.descuentoItem = descuentoItem; }
    }

    public static class EstadisticasVentas {
        private BigDecimal totalVentas;
        private Long cantidadVentas;
        private BigDecimal promedioVenta;

        // Getters y Setters
        public BigDecimal getTotalVentas() { return totalVentas; }
        public void setTotalVentas(BigDecimal totalVentas) { this.totalVentas = totalVentas; }

        public Long getCantidadVentas() { return cantidadVentas; }
        public void setCantidadVentas(Long cantidadVentas) { this.cantidadVentas = cantidadVentas; }

        public BigDecimal getPromedioVenta() { return promedioVenta; }
        public void setPromedioVenta(BigDecimal promedioVenta) { this.promedioVenta = promedioVenta; }
    }
}