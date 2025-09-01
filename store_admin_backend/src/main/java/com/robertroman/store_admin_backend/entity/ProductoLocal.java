package com.robertroman.store_admin_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "productos_locales",
        uniqueConstraints = @UniqueConstraint(columnNames = {"producto_id", "local_id"}))
public class ProductoLocal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    // Relación con Local
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id", nullable = false)
    private Local local;

    @NotNull(message = "El stock es obligatorio")
    @Column(name = "stock", nullable = false)
    private Integer stock = 0;

    @Column(name = "stock_minimo")
    private Integer stockMinimo = 0;

    // Precio específico del producto en este local (puede diferir del precio base)
    @Column(name = "precio_venta", precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructores
    public ProductoLocal() {}

    public ProductoLocal(Producto product, Local local, Integer stock, BigDecimal precioVenta) {
        this.producto = producto;
        this.local = local;
        this.stock = stock;
        this.precioVenta = precioVenta;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Local getLocal() { return local; }
    public void setLocal(Local local) { this.local = local; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Métodos de utilidad
    public void reducirStock(Integer cantidad) {
        if (this.stock >= cantidad) {
            this.stock -= cantidad;
        } else {
            throw new RuntimeException("Stock insuficiente. Stock actual: " + this.stock + ", solicitado: " + cantidad);
        }
    }

    public void aumentarStock(Integer cantidad) {
        this.stock += cantidad;
    }

    public boolean tieneStockSuficiente(Integer cantidad) {
        return this.stock >= cantidad;
    }

    public boolean estaEnStockMinimo() {
        return this.stock <= this.stockMinimo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductoLocal that = (ProductoLocal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ProductoLocal{" +
                "id=" + id +
                ", stock=" + stock +
                ", precioVenta=" + precioVenta +
                ", activo=" + activo +
                '}';
    }
}