package com.robertroman.store_admin_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "detalles_venta")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Venta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    // Relación con ProductoLocal (el producto específico del local)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_local_id", nullable = false)
    private ProductLocal productoLocal;

    @NotNull(message = "La cantidad es obligatoria")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @NotNull(message = "El subtotal es obligatorio")
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "descuento_item", precision = 10, scale = 2)
    private BigDecimal descuentoItem = BigDecimal.ZERO;

    // Constructores
    public DetalleVenta() {}

    public DetalleVenta(Venta venta, ProductLocal productoLocal, Integer cantidad, BigDecimal precioUnitario) {
        this.venta = venta;
        this.productoLocal = productoLocal;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.calcularSubtotal();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }

    public ProductLocal getProductoLocal() { return productoLocal; }
    public void setProductoLocal(ProductLocal productoLocal) { this.productoLocal = productoLocal; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        this.calcularSubtotal();
    }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        this.calcularSubtotal();
    }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDescuentoItem() { return descuentoItem; }
    public void setDescuentoItem(BigDecimal descuentoItem) {
        this.descuentoItem = descuentoItem;
        this.calcularSubtotal();
    }

    // Métodos de utilidad
    public void calcularSubtotal() {
        if (this.cantidad != null && this.precioUnitario != null) {
            BigDecimal subtotalBruto = this.precioUnitario.multiply(new BigDecimal(this.cantidad));
            this.subtotal = subtotalBruto.subtract(this.descuentoItem != null ? this.descuentoItem : BigDecimal.ZERO);
        }
    }

    // Información del producto para evitar lazy loading en reportes
    public String getNombreProducto() {
        return this.productoLocal != null && this.productoLocal.getProducto() != null
                ? this.productoLocal.getProducto().getNombre()
                : null;
    }

    public String getSkuProducto() {
        return this.productoLocal != null && this.productoLocal.getProducto() != null
                ? this.productoLocal.getProducto().getSku()
                : null;
    }

    public String getCategoriaProducto() {
        return this.productoLocal != null && this.productoLocal.getProducto() != null
                ? this.productoLocal.getProducto().getCategoria()
                : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetalleVenta that = (DetalleVenta) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DetalleVenta{" +
                "id=" + id +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                '}';
    }
}