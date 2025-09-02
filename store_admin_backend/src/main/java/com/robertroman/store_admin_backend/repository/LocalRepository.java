package com.robertroman.store_admin_backend.repository;

import com.robertroman.store_admin_backend.entity.Local;
import com.robertroman.store_admin_backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalRepository extends JpaRepository<Local, Long> {

    // Buscar locales por usuario
    List<Local> findByUsuarioAndActivoTrue(Usuario usuario);

    // Buscar locales por ID de usuario
    List<Local> findByUsuarioIdAndActivoTrue(Long usuarioId);

    // Buscar locales activos
    List<Local> findByActivoTrue();

    // Buscar por nombre (case insensitive)
    List<Local> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre); 

    // Buscar por ciudad
    List<Local> findByCiudadAndActivoTrue(String ciudad);

    // Obtener todas las ciudades donde hay locales
    @Query("SELECT DISTINCT l.ciudad FROM Local l WHERE l.ciudad IS NOT NULL AND l.activo = true ORDER BY l.ciudad")
    List<String> findAllCiudades();

    // Contar locales por usuario
    @Query("SELECT COUNT(l) FROM Local l WHERE l.usuario.id = :usuarioId AND l.activo = true")
    Long countByUsuarioId(@Param("usuarioId") Long usuarioId);

    // Buscar locales con ventas en un período (útil para reportes)
    @Query("SELECT DISTINCT l FROM Local l JOIN l.ventas v WHERE v.fechaVenta BETWEEN :fechaInicio AND :fechaFin AND l.activo = true")
    List<Local> findLocalesConVentasEnPeriodo(@Param("fechaInicio") java.time.LocalDateTime fechaInicio,
                                              @Param("fechaFin") java.time.LocalDateTime fechaFin);
}