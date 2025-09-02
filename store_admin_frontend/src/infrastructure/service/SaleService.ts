import { apiClient } from '../api/apiClient';
import { Venta, CrearVentaRequest } from '../../domain/types';

export class SaleService {
  async createSale(request: CrearVentaRequest): Promise<Venta> {
    return apiClient.post<Venta, CrearVentaRequest>('/ventas', request);
  }

  async getSalesByLocal(localId: number): Promise<Venta[]> {
    return apiClient.get<Venta[]>(`/ventas/local/${localId}`);
  }

  async getSaleById(id: number): Promise<Venta> {
    return apiClient.get<Venta>(`/ventas/${id}`);
  }

  async getSalesByPeriod(localId: number, fechaInicio: string, fechaFin: string): Promise<Venta[]> {
    return apiClient.get<Venta[]>(`/ventas/local/${localId}/periodo?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`);
  }

  async getTodaySales(localId: number): Promise<Venta[]> {
    return apiClient.get<Venta[]>(`/ventas/local/${localId}/hoy`);
  }

  async cancelSale(id: number, motivo: string): Promise<Venta> {
    return apiClient.patch<Venta>(`/ventas/${id}/cancelar`, { motivo });
  }

  async getSalesStatistics(localId: number, fechaInicio: string, fechaFin: string): Promise<{
    totalVentas: number;
    cantidadVentas: number;
    promedioVenta: number;
  }> {
    return apiClient.get(`/ventas/local/${localId}/estadisticas?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`);
  }

  async getTodayStatistics(localId: number): Promise<{
    totalVentas: number;
    cantidadVentas: number;
    promedioVenta: number;
  }> {
    return apiClient.get(`/ventas/local/${localId}/estadisticas/hoy`);
  }

  async getMonthStatistics(localId: number): Promise<{
    totalVentas: number;
    cantidadVentas: number;
    promedioVenta: number;
  }> {
    return apiClient.get(`/ventas/local/${localId}/estadisticas/mes`);
  }
}

export const saleService = new SaleService();