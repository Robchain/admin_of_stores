package com.robertroman.store_admin_backend.service;

import com.robertroman.store_admin_backend.entity.Producto;
import com.robertroman.store_admin_backend.entity.ProductoLocal;
import com.robertroman.store_admin_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

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

    // Dashboard principal con todas las estadísticas
    public DashboardData obtenerDashboardCompleto(Long localId, Long usuarioId,
                                                  LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        // Validar acceso al local
        localService.validarAccesoLocal(localId, usuarioId);

        DashboardData dashboard = new DashboardData();

        // Estadísticas de ventas
        dashboard.setTotalVentas(ventaRepository.findTotalVentasByLocalAndPeriodo(localId, fechaInicio, fechaFin));
        dashboard.setCantidadVentas(ventaRepository.countVentasByLocalAndPeriodo(localId, fechaInicio, fechaFin));
        dashboard.setPromedioVenta(ventaRepository.findPromedioVentaByLocal(localId).orElse(BigDecimal.ZERO));

        // Estadísticas de inventario
        dashboard.setValorInventario(BigDecimal.ZERO); // Simplificar por ahora

        // Obtener productos del local y calcular estadísticas en memoria
        List<ProductoLocal> productos = productoLocalRepository.findByLocalIdAndActivoTrue(localId);
        dashboard.setProductosStockBajo((int) productos.stream()
                .filter(ProductoLocal::estaEnStockMinimo).count());
        dashboard.setProductosSinStock((int) productos.stream()
                .filter(p -> p.getStock() == 0).count());

        // Productos más vendidos
        dashboard.setProductosMasVendidos(obtenerProductosMasVendidos(localId));

        // Ventas por categoría
        dashboard.setVentasPorCategoria(obtenerVentasPorCategoria(localId, fechaInicio, fechaFin));

        return dashboard;
    }

    // Productos más vendidos en el local
    public List<ProductoVendido> obtenerProductosMasVendidos(Long localId) {
        List<Object[]> resultados = detalleVentaRepository.findProductosMasVendidosByLocal(localId);

        return resultados.stream()
                .limit(10) // Top 10
                .map(row -> {
                    ProductoVendido pv = new ProductoVendido();
                    pv.setProductoLocal((com.robertroman.store_admin_backend.entity.ProductoLocal) row[0]);
                    pv.setCantidadVendida(((Number) row[1]).longValue());
                    return pv;
                })
                .collect(Collectors.toList());
    }

    // Ventas por categoría
    public List<VentaCategoria> obtenerVentasPorCategoria(Long localId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Object[]> resultados = detalleVentaRepository.findReporteVentasByCategoria(localId, fechaInicio, fechaFin);

        return resultados.stream()
                .map(row -> {
                    VentaCategoria vc = new VentaCategoria();
                    vc.setCategoria((String) row[0]);
                    vc.setCantidadVendida(((Number) row[1]).longValue());
                    vc.setTotalVentas((BigDecimal) row[2]);
                    return vc;
                })
                .collect(Collectors.toList());
    }

    // Reporte de stock bajo (para alertas)
    public List<AlertaStock> obtenerAlertasStock(Long localId, Long usuarioId) {
        // Validar acceso al local
        localService.validarAccesoLocal(localId, usuarioId);

        // Obtener productos del local y filtrar en memoria
        List<ProductoLocal> productos = productoLocalRepository.findByLocalIdAndActivoTrue(localId);

        return productos.stream()
                .filter(pl -> pl.getStock() <= pl.getStockMinimo())
                .map(pl -> {
                    AlertaStock alerta = new AlertaStock();
                    alerta.setProductoLocal(pl);
                    alerta.setTipoAlerta(pl.getStock() == 0 ? "SIN_STOCK" : "STOCK_BAJO");
                    alerta.setMensaje(pl.getStock() == 0 ?
                            "Producto sin stock" :
                            "Stock por debajo del mínimo (" + pl.getStockMinimo() + ")");
                    return alerta;
                })
                .collect(Collectors.toList());
    }

    // Comparación de ventas por período
    public ComparacionVentas compararVentas(Long localId, Long usuarioId,
                                            LocalDateTime periodo1Inicio, LocalDateTime periodo1Fin,
                                            LocalDateTime periodo2Inicio, LocalDateTime periodo2Fin) {
        // Validar acceso al local
        localService.validarAccesoLocal(localId, usuarioId);

        ComparacionVentas comparacion = new ComparacionVentas();

        // Período 1
        comparacion.setPeriodo1Total(ventaRepository.findTotalVentasByLocalAndPeriodo(localId, periodo1Inicio, periodo1Fin));
        comparacion.setPeriodo1Cantidad(ventaRepository.countVentasByLocalAndPeriodo(localId, periodo1Inicio, periodo1Fin));

        // Período 2
        comparacion.setPeriodo2Total(ventaRepository.findTotalVentasByLocalAndPeriodo(localId, periodo2Inicio, periodo2Fin));
        comparacion.setPeriodo2Cantidad(ventaRepository.countVentasByLocalAndPeriodo(localId, periodo2Inicio, periodo2Fin));

        // Calcular diferencias
        if (comparacion.getPeriodo2Total().compareTo(BigDecimal.ZERO) > 0) {
            comparacion.setPorcentajeCambioTotal(
                    comparacion.getPeriodo1Total()
                            .subtract(comparacion.getPeriodo2Total())
                            .divide(comparacion.getPeriodo2Total(), 4, java.math.RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"))
            );
        }

        return comparacion;
    }

    // Análisis de rentabilidad por producto
    public List<RentabilidadProducto> obtenerRentabilidadProductos(Long localId, Long usuarioId,
                                                                   LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        // Validar acceso al local
        localService.validarAccesoLocal(localId, usuarioId);

        List<com.robertroman.store_admin_backend.entity.ProductoLocal> productos =
                productoLocalRepository.findByLocalIdAndActivoTrue(localId);

        return productos.stream()
                .map(pl -> {
                    RentabilidadProducto rentabilidad = new RentabilidadProducto();
                    rentabilidad.setProductoLocal(pl);

                    // Calcular cantidad vendida y ingresos en el período
                    Long cantidadVendida = detalleVentaRepository
                            .findTotalCantidadVendidaByProductoAndPeriodo(pl.getProducto().getId(), fechaInicio, fechaFin);
                    BigDecimal ingresos = detalleVentaRepository
                            .findTotalIngresosByProductoAndPeriodo(pl.getProducto().getId(), fechaInicio, fechaFin);

                    rentabilidad.setCantidadVendida(cantidadVendida);
                    rentabilidad.setIngresosGenerados(ingresos);

                    // Calcular margen (precio venta - precio base) * cantidad vendida
                    BigDecimal margenUnitario = pl.getPrecioVenta().subtract(pl.getProducto().getPrecioBase());
                    rentabilidad.setMargenTotal(margenUnitario.multiply(new BigDecimal(cantidadVendida)));

                    return rentabilidad;
                })
                .sorted((r1, r2) -> r2.getMargenTotal().compareTo(r1.getMargenTotal()))
                .collect(Collectors.toList());
    }

    // Clases internas para DTOs

    public static class DashboardData {
        // Estadísticas de ventas
        private BigDecimal totalVentas;
        private Long cantidadVentas;
        private BigDecimal promedioVenta;

        // Estadísticas de inventario
        private BigDecimal valorInventario;
        private Integer productosStockBajo;
        private Integer productosSinStock;

        // Listas de análisis
        private List<ProductoVendido> productosMasVendidos;
        private List<VentaCategoria> ventasPorCategoria;

        // Getters y Setters
        public BigDecimal getTotalVentas() { return totalVentas; }
        public void setTotalVentas(BigDecimal totalVentas) { this.totalVentas = totalVentas; }

        public Long getCantidadVentas() { return cantidadVentas; }
        public void setCantidadVentas(Long cantidadVentas) { this.cantidadVentas = cantidadVentas; }

        public BigDecimal getPromedioVenta() { return promedioVenta; }
        public void setPromedioVenta(BigDecimal promedioVenta) { this.promedioVenta = promedioVenta; }

        public BigDecimal getValorInventario() { return valorInventario; }
        public void setValorInventario(BigDecimal valorInventario) { this.valorInventario = valorInventario; }

        public Integer getProductosStockBajo() { return productosStockBajo; }
        public void setProductosStockBajo(Integer productosStockBajo) { this.productosStockBajo = productosStockBajo; }

        public Integer getProductosSinStock() { return productosSinStock; }
        public void setProductosSinStock(Integer productosSinStock) { this.productosSinStock = productosSinStock; }

        public List<ProductoVendido> getProductosMasVendidos() { return productosMasVendidos; }
        public void setProductosMasVendidos(List<ProductoVendido> productosMasVendidos) { this.productosMasVendidos = productosMasVendidos; }

        public List<VentaCategoria> getVentasPorCategoria() { return ventasPorCategoria; }
        public void setVentasPorCategoria(List<VentaCategoria> ventasPorCategoria) { this.ventasPorCategoria = ventasPorCategoria; }
    }

    public static class ProductoVendido {
        private com.robertroman.store_admin_backend.entity.ProductoLocal productoLocal;
        private Long cantidadVendida;

        public com.robertroman.store_admin_backend.entity.ProductoLocal getProductoLocal() { return productoLocal; }
        public void setProductoLocal(com.robertroman.store_admin_backend.entity.ProductoLocal productoLocal) { this.productoLocal = productoLocal; }

        public Long getCantidadVendida() { return cantidadVendida; }
        public void setCantidadVendida(Long cantidadVendida) { this.cantidadVendida = cantidadVendida; }
    }

    public static class VentaCategoria {
        private String categoria;
        private Long cantidadVendida;
        private BigDecimal totalVentas;

        public String getCategoria() { return categoria; }
        public void setCategoria(String categoria) { this.categoria = categoria; }

        public Long getCantidadVendida() { return cantidadVendida; }
        public void setCantidadVendida(Long cantidadVendida) { this.cantidadVendida = cantidadVendida; }

        public BigDecimal getTotalVentas() { return totalVentas; }
        public void setTotalVentas(BigDecimal totalVentas) { this.totalVentas = totalVentas; }
    }

    public static class AlertaStock {
        private com.robertroman.store_admin_backend.entity.ProductoLocal productoLocal;
        private String tipoAlerta;
        private String mensaje;

        public com.robertroman.store_admin_backend.entity.ProductoLocal getProductoLocal() { return productoLocal; }
        public void setProductoLocal(com.robertroman.store_admin_backend.entity.ProductoLocal productoLocal) { this.productoLocal = productoLocal; }

        public String getTipoAlerta() { return tipoAlerta; }
        public void setTipoAlerta(String tipoAlerta) { this.tipoAlerta = tipoAlerta; }

        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    }

    public static class ComparacionVentas {
        private BigDecimal periodo1Total;
        private Long periodo1Cantidad;
        private BigDecimal periodo2Total;
        private Long periodo2Cantidad;
        private BigDecimal porcentajeCambioTotal;

        // Getters y Setters
        public BigDecimal getPeriodo1Total() { return periodo1Total; }
        public void setPeriodo1Total(BigDecimal periodo1Total) { this.periodo1Total = periodo1Total; }

        public Long getPeriodo1Cantidad() { return periodo1Cantidad; }
        public void setPeriodo1Cantidad(Long periodo1Cantidad) { this.periodo1Cantidad = periodo1Cantidad; }

        public BigDecimal getPeriodo2Total() { return periodo2Total; }
        public void setPeriodo2Total(BigDecimal periodo2Total) { this.periodo2Total = periodo2Total; }

        public Long getPeriodo2Cantidad() { return periodo2Cantidad; }
        public void setPeriodo2Cantidad(Long periodo2Cantidad) { this.periodo2Cantidad = periodo2Cantidad; }

        public BigDecimal getPorcentajeCambioTotal() { return porcentajeCambioTotal; }
        public void setPorcentajeCambioTotal(BigDecimal porcentajeCambioTotal) { this.porcentajeCambioTotal = porcentajeCambioTotal; }
    }

    public static class RentabilidadProducto {
        private com.robertroman.store_admin_backend.entity.ProductoLocal productoLocal;
        private Long cantidadVendida;
        private BigDecimal ingresosGenerados;
        private BigDecimal margenTotal;

        public com.robertroman.store_admin_backend.entity.ProductoLocal getProductoLocal() { return productoLocal; }
        public void setProductoLocal(com.robertroman.store_admin_backend.entity.ProductoLocal productoLocal) { this.productoLocal = productoLocal; }

        public Long getCantidadVendida() { return cantidadVendida; }
        public void setCantidadVendida(Long cantidadVendida) { this.cantidadVendida = cantidadVendida; }

        public BigDecimal getIngresosGenerados() { return ingresosGenerados; }
        public void setIngresosGenerados(BigDecimal ingresosGenerados) { this.ingresosGenerados = ingresosGenerados; }

        public BigDecimal getMargenTotal() { return margenTotal; }
        public void setMargenTotal(BigDecimal margenTotal) { this.margenTotal = margenTotal; }
    }
}