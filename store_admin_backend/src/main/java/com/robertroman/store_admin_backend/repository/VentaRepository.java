package com.robertroman.store_admin_backend.repository;

import com.robertroman.store_admin_backend.entity.Local;
import com.robertroman.store_admin_backend.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    // Buscar ventas por local
    List<Venta> findByLocal(Local local);

    // Buscar ventas por ID de local
    List<Venta> findByLocalId(Long localId);

    // Buscar por número de factura
    Optional<Venta> findByNumeroFactura(String numeroFactura);

    // Ventas por estado
    List<Venta> findByEstado(Venta.EstadoVenta estado);

    // Ventas por local y estado
    List<Venta> findByLocalIdAndEstado(Long localId, Venta.EstadoVenta estado);

    // Ventas por método de pago
    List<Venta> findByMetodoPago(Venta.MetodoPago metodoPago);

    // Ventas en un rango de fechas
    List<Venta> findByFechaVentaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Ventas por local en un rango de fechas
    List<Venta> findByLocalIdAndFechaVentaBetween(Long localId, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Ventas por local ordenadas por fecha (más recientes primero)
    List<Venta> findByLocalIdOrderByFechaVentaDesc(Long localId);

    // Total de ventas por local en un período
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.local.id = :localId AND v.fechaVenta BETWEEN :fechaInicio AND :fechaFin AND v.estado = 'COMPLETADA'")
    BigDecimal findTotalVentasByLocalAndPeriodo(@Param("localId") Long localId,
                                                @Param("fechaInicio") LocalDateTime fechaInicio,
                                                @Param("fechaFin") LocalDateTime fechaFin);

    // Cantidad de ventas por local en un período
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.local.id = :localId AND v.fechaVenta BETWEEN :fechaInicio AND :fechaFin")
    Long countVentasByLocalAndPeriodo(@Param("localId") Long localId,
                                      @Param("fechaInicio") LocalDateTime fechaInicio,
                                      @Param("fechaFin") LocalDateTime fechaFin);

    // Promedio de venta por local
    @Query("SELECT AVG(v.total) FROM Venta v WHERE v.local.id = :localId AND v.estado = 'COMPLETADA'")
    Optional<BigDecimal> findPromedioVentaByLocal(@Param("localId") Long localId);

    // Top 10 ventas más altas por local
    List<Venta> findTop10ByLocalIdOrderByTotalDesc(Long localId);

    // Ventas por usuario (a través del local)
    @Query("SELECT v FROM Venta v WHERE v.local.usuario.id = :usuarioId ORDER BY v.fechaVenta DESC")
    List<Venta> findVentasByUsuario(@Param("usuarioId") Long usuarioId);

    // Verificar si existe número de factura
    boolean existsByNumeroFactura(String numeroFactura);

    // Últimas N ventas por local
    List<Venta> findTop20ByLocalIdOrderByFechaVentaDesc(Long localId);
}