package com.robertroman.store_admin_backend.repository;

import com.robertroman.store_admin_backend.entity.DetalleVenta;
import com.robertroman.store_admin_backend.entity.ProductLocal;
import com.robertroman.store_admin_backend.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    // Buscar detalles por venta
    List<DetalleVenta> findByVenta(Venta venta);

    // Buscar detalles por ID de venta
    List<DetalleVenta> findByVentaId(Long ventaId);

    // Buscar detalles por producto local
    List<DetalleVenta> findByProductoLocal(ProductLocal productoLocal);

    // Detalles de ventas por producto en un local
    List<DetalleVenta> findByProductoLocalId(Long productoLocalId);

    // Productos más vendidos en un local (por cantidad)
    @Query("SELECT dv.productoLocal, SUM(dv.cantidad) as totalVendido " +
            "FROM DetalleVenta dv " +
            "WHERE dv.productoLocal.local.id = :localId " +
            "GROUP BY dv.productoLocal " +
            "ORDER BY totalVendido DESC")
    List<Object[]> findProductosMasVendidosByLocal(@Param("localId") Long localId);

    // Productos más vendidos en general (por cantidad)
    @Query("SELECT dv.productoLocal.producto, SUM(dv.cantidad) as totalVendido " +
            "FROM DetalleVenta dv " +
            "GROUP BY dv.productoLocal.producto " +
            "ORDER BY totalVendido DESC")
    List<Object[]> findProductosMasVendidos();

    // Ventas por producto en un período
    @Query("SELECT dv FROM DetalleVenta dv " +
            "WHERE dv.productoLocal.producto.id = :productoId " +
            "AND dv.venta.fechaVenta BETWEEN :fechaInicio AND :fechaFin")
    List<DetalleVenta> findVentasByProductoAndPeriodo(@Param("productoId") Long productoId,
                                                      @Param("fechaInicio") LocalDateTime fechaInicio,
                                                      @Param("fechaFin") LocalDateTime fechaFin);

    // Total de cantidad vendida por producto en un período
    @Query("SELECT COALESCE(SUM(dv.cantidad), 0) FROM DetalleVenta dv " +
            "WHERE dv.productoLocal.producto.id = :productoId " +
            "AND dv.venta.fechaVenta BETWEEN :fechaInicio AND :fechaFin")
    Long findTotalCantidadVendidaByProductoAndPeriodo(@Param("productoId") Long productoId,
                                                      @Param("fechaInicio") LocalDateTime fechaInicio,
                                                      @Param("fechaFin") LocalDateTime fechaFin);

    // Total de ingresos por producto en un período
    @Query("SELECT COALESCE(SUM(dv.subtotal), 0) FROM DetalleVenta dv " +
            "WHERE dv.productoLocal.producto.id = :productoId " +
            "AND dv.venta.fechaVenta BETWEEN :fechaInicio AND :fechaFin")
    java.math.BigDecimal findTotalIngresosByProductoAndPeriodo(@Param("productoId") Long productoId,
                                                               @Param("fechaInicio") LocalDateTime fechaInicio,
                                                               @Param("fechaFin") LocalDateTime fechaFin);

    // Reporte de ventas por categoría en un local
    @Query("SELECT dv.productoLocal.producto.categoria, SUM(dv.cantidad), SUM(dv.subtotal) " +
            "FROM DetalleVenta dv " +
            "WHERE dv.productoLocal.local.id = :localId " +
            "AND dv.venta.fechaVenta BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY dv.productoLocal.producto.categoria " +
            "ORDER BY SUM(dv.subtotal) DESC")
    List<Object[]> findReporteVentasByCategoria(@Param("localId") Long localId,
                                                @Param("fechaInicio") LocalDateTime fechaInicio,
                                                @Param("fechaFin") LocalDateTime fechaFin);

    // Detalles de ventas por local en un período
    @Query("SELECT dv FROM DetalleVenta dv " +
            "WHERE dv.productoLocal.local.id = :localId " +
            "AND dv.venta.fechaVenta BETWEEN :fechaInicio AND :fechaFin " +
            "ORDER BY dv.venta.fechaVenta DESC")
    List<DetalleVenta> findDetallesByLocalAndPeriodo(@Param("localId") Long localId,
                                                     @Param("fechaInicio") LocalDateTime fechaInicio,
                                                     @Param("fechaFin") LocalDateTime fechaFin);
}