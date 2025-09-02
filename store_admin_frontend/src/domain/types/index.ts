// Auth Types
export interface LoginRequest {
  usernameOrEmail: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  username: string;
  email: string;
}

// User Types
export interface User {
  id: number;
  username: string;
  email: string;
}

// Local Types
export interface Local {
  id?: number;
  nombre: string;
  direccion?: string;
  telefono?: string;
  ciudad?: string;
  activo?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

// Product Types
export interface Producto {
  id?: number;
  nombre: string;
  descripcion?: string;
  precioBase: number;
  categoria?: string;
  sku?: string;
  activo?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

// ProductoLocal Types
export interface ProductoLocal {
  id?: number;
  producto: Producto;
  local: Local;
  stock: number;
  stockMinimo: number;
  precioVenta: number;
  activo?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

// Venta Types
export interface Venta {
  id?: number;
  local: Local;
  total: number;
  subtotal?: number;
  impuestos?: number;
  descuento?: number;
  estado: EstadoVenta;
  metodoPago?: MetodoPago;
  numeroFactura?: string;
  observaciones?: string;
  fechaVenta: string;
  detalles?: DetalleVenta[];
}

export interface DetalleVenta {
  id?: number;
  productoLocal: ProductoLocal;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
  descuentoItem?: number;
}

export interface ItemVentaRequest {
  productoId: number;
  cantidad: number;
  precioUnitario?: number;
  descuentoItem?: number;
}

export interface CrearVentaRequest {
  localId: number;
  items: ItemVentaRequest[];
  metodoPago: MetodoPago;
  descuento?: number;
  impuestos?: number;
  observaciones?: string;
}

// Dashboard Types
export interface DashboardData {
  totalVentas: number;
  cantidadVentas: number;
  promedioVenta: number;
  valorInventario: number;
  productosStockBajo: number;
  productosSinStock: number;
  productosMasVendidos: ProductoVendido[];
  ventasPorCategoria: VentaCategoria[];
}

export interface ProductoVendido {
  productoLocal: ProductoLocal;
  cantidadVendida: number;
}

export interface VentaCategoria {
  categoria: string;
  cantidadVendida: number;
  totalVentas: number;
}

// Request Types
export interface AsignarProductoRequest {
  productoId: number;
  localId: number;
  stock: number;
  precioVenta: number;
  stockMinimo: number;
}

export interface ActualizarStockRequest {
  productoId: number;
  localId: number;
  nuevoStock: number;
}

// Enums
export enum EstadoVenta {
  PENDIENTE = 'PENDIENTE',
  COMPLETADA = 'COMPLETADA',
  CANCELADA = 'CANCELADA',
  DEVUELTA = 'DEVUELTA'
}

export enum MetodoPago {
  EFECTIVO = 'EFECTIVO',
  TARJETA_CREDITO = 'TARJETA_CREDITO',
  TARJETA_DEBITO = 'TARJETA_DEBITO',
  TRANSFERENCIA = 'TRANSFERENCIA',
  OTRO = 'OTRO'
}

// API Response Types
export interface ApiResponse<T> {
  data?: T;
  message?: string;
  timestamp?: number;
}

export interface ErrorResponse {
  message: string;
  timestamp: number;
}