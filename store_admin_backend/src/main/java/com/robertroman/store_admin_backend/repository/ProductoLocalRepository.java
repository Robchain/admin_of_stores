package com.robertroman.store_admin_backend.repository;

import com.robertroman.store_admin_backend.entity.Local;
import com.robertroman.store_admin_backend.entity.Product;
import com.robertroman.store_admin_backend.entity.ProductLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoLocalRepository extends JpaRepository<ProductLocal, Long> {

    // Buscar por producto y local (relación única)
    Optional<ProductLocal> findByProductoAndLocal(Product producto, Local local);

    // Buscar por IDs de producto y local
    Optional<ProductLocal> findByProductoIdAndLocalId(Long productoId, Long localId);

    // Buscar todos los productos de un local
    List<ProductLocal> findByLocalAndActivoTrue(Local local);

    // Buscar por ID de local
    List<ProductLocal> findByLocalIdAndActivoTrue(Long localId);

    // Buscar todos los locales donde está disponible un producto
    List<ProductLocal> findByProductoAndActivoTrue(Product producto);

    // Buscar por ID de producto
    List<ProductLocal> findByProductoIdAndActivoTrue(Long productoId);

    // Verificar si un producto está asignado a un local
    boolean existsByProductoIdAndLocalIdAndActivoTrue(Long productoId, Long localId);

    // Consultas más simples usando métodos básicos de Spring Data
    // Los servicios harán el filtrado de stock bajo en memoria
    List<ProductLocal> findByLocalIdAndStockLessThanEqualAndActivoTrue(Long localId, Integer stock);

    List<ProductLocal> findByLocalIdAndStockAndActivoTrue(Long localId, Integer stock);
}