package com.robertroman.store_admin_backend.repository;

import com.robertroman.store_admin_backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Product, Long> {

    // Buscar productos activos
    List<Product> findByActivoTrue();

    // Buscar por nombre (case insensitive)
    List<Product> findByNombreContainingIgnoreCase(String nombre);

    // Buscar por categoría
    List<Product> findByCategoriaAndActivoTrue(String categoria);

    // Buscar por SKU
    Optional<Product> findBySku(String sku);

    // Verificar si existe SKU
    boolean existsBySku(String sku);

    // Buscar productos por categoría activos
    @Query("SELECT p FROM Producto p WHERE p.categoria = :categoria AND p.activo = true ORDER BY p.nombre")
    List<Product> findActivosByCategoria(@Param("categoria") String categoria);

    // Obtener todas las categorías disponibles
    @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.categoria IS NOT NULL AND p.activo = true ORDER BY p.categoria")
    List<String> findAllCategorias();

    // Buscar productos sin asignar a ningún local
    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.id NOT IN " +
            "(SELECT DISTINCT pl.producto.id FROM ProductoLocal pl WHERE pl.activo = true)")
    List<Product> findProductosSinAsignar();
}