import { apiClient } from '../api/apiClient';
import { ProductoLocal, AsignarProductoRequest, ActualizarStockRequest } from '../../domain/types';

export class ProductLocalService {
  async assignProductToLocal(request: AsignarProductoRequest): Promise<ProductoLocal> {
    return apiClient.post<ProductoLocal, AsignarProductoRequest>('/productos-local/asignar', request);
  }

  async updateStock(request: ActualizarStockRequest): Promise<ProductoLocal> {
    return apiClient.put<ProductoLocal, ActualizarStockRequest>('/productos-local/stock', request);
  }

  async getProductsByLocal(localId: number): Promise<ProductoLocal[]> {
    return apiClient.get<ProductoLocal[]>(`/productos-local/local/${localId}`);
  }

  async getStock(productoId: number, localId: number): Promise<{ stock: number }> {
    return apiClient.get<{ stock: number }>(`/productos-local/stock/${productoId}/${localId}`);
  }

  async getProductsWithLowStock(localId: number): Promise<ProductoLocal[]> {
    return apiClient.get<ProductoLocal[]>(`/productos-local/local/${localId}/stock-bajo`);
  }

  async getProductsWithoutStock(localId: number): Promise<ProductoLocal[]> {
    return apiClient.get<ProductoLocal[]>(`/productos-local/local/${localId}/sin-stock`);
  }

  async getInventoryValue(localId: number): Promise<{ valor: number }> {
    return apiClient.get<{ valor: number }>(`/productos-local/local/${localId}/valor-inventario`);
  }

  async getInventorySummary(localId: number): Promise<{
    totalProductos: number;
    productosStockBajo: number;
    productosSinStock: number;
    valorTotalInventario: number;
  }> {
    return apiClient.get(`/productos-local/local/${localId}/resumen`);
  }

  async updatePrice(productoId: number, localId: number, nuevoPrecio: number): Promise<ProductoLocal> {
    return apiClient.patch<ProductoLocal>('/productos-local/precio-venta', {
      productoId,
      localId,
      nuevoPrecio
    });
  }

  async increaseStock(productoId: number, localId: number, cantidad: number): Promise<{ message: string }> {
    return apiClient.patch<{ message: string }>('/productos-local/aumentar-stock', {
      productoId,
      localId,
      cantidad
    });
  }
}

export const productLocalService = new ProductLocalService();