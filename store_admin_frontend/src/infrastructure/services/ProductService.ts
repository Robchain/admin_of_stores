import { apiClient } from '../api/apiClient';
import { Producto } from '../../domain/types';

export class ProductService {
  async getProducts(): Promise<Producto[]> {
    return apiClient.get<Producto[]>('/productos');
  }

  async createProduct(product: Omit<Producto, 'id'>): Promise<Producto> {
    return apiClient.post<Producto, Omit<Producto, 'id'>>('/productos', product);
  }

  async updateProduct(id: number, product: Partial<Producto>): Promise<Producto> {
    return apiClient.put<Producto, Partial<Producto>>(`/productos/${id}`, product);
  }

  async getProductById(id: number): Promise<Producto> {
    return apiClient.get<Producto>(`/productos/${id}`);
  }

  async searchProducts(nombre: string): Promise<Producto[]> {
    return apiClient.get<Producto[]>(`/productos/buscar?nombre=${nombre}`);
  }

  async getProductsByCategory(categoria: string): Promise<Producto[]> {
    return apiClient.get<Producto[]>(`/productos/categoria/${categoria}`);
  }

  async getCategories(): Promise<string[]> {
    return apiClient.get<string[]>('/productos/categorias');
  }

  async checkSkuAvailability(sku: string): Promise<{ available: boolean; message: string }> {
    return apiClient.get<{ available: boolean; message: string }>(`/productos/check-sku/${sku}`);
  }
}

export const productService = new ProductService();