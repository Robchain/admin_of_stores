package com.robertroman.store_admin_backend.repository;

import com.robertroman.store_admin_backend.entity.Local;
import com.robertroman.store_admin_backend.entity.Producto;
import com.robertroman.store_admin_backend.entity.ProductoLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoLocalRepository extends JpaRepository<ProductoLocal, Long> {

    // Buscar por producto y local (relación única)
    Optional<ProductoLocal> findByProductoAndLocal(Producto producto, Local local);

    // Buscar por IDs de producto y local
    Optional<ProductoLocal> findByProductoIdAndLocalId(Long productoId, Long localId);

    // Buscar todos los productos de un local
    List<ProductoLocal> findByLocalAndActivoTrue(Local local);

    // Buscar por ID de local
    List<ProductoLocal> findByLocalIdAndActivoTrue(Long localId);

    // Buscar todos los locales donde está disponible un producto
    List<ProductoLocal> findByProductoAndActivoTrue(Producto producto);

    // Buscar por ID de producto
    List<ProductoLocal> findByProductoIdAndActivoTrue(Long productoId);

    // Verificar si un producto está asignado a un local
    boolean existsByProductoIdAndLocalIdAndActivoTrue(Long productoId, Long localId);

    // Consultas más simples usando métodos básicos de Spring Data
    // Los servicios harán el filtrado de stock bajo en memoria
    List<ProductoLocal> findByLocalIdAndStockLessThanEqualAndActivoTrue(Long localId, Integer stock);

    List<ProductoLocal> findByLocalIdAndStockAndActivoTrue(Long localId, Integer stock);
}