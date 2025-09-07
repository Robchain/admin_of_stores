import { apiClient } from '../api/apiClient';
import { DashboardData, ProductoVendido, VentaCategoria } from '../../domain/types';

export class DashboardService {
  async getDashboardData(localId: number, fechaInicio: string, fechaFin: string): Promise<DashboardData> {
    return apiClient.get<DashboardData>(`/dashboard/local/${localId}?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`);
  }

  async getTodayDashboard(localId: number): Promise<DashboardData> {
    return apiClient.get<DashboardData>(`/dashboard/local/${localId}/hoy`);
  }

  async getMonthDashboard(localId: number): Promise<DashboardData> {
    return apiClient.get<DashboardData>(`/dashboard/local/${localId}/mes`);
  }

  async getTopProducts(localId: number): Promise<ProductoVendido[]> {
    return apiClient.get<ProductoVendido[]>(`/dashboard/local/${localId}/productos-mas-vendidos`);
  }

  async getSalesByCategory(localId: number, fechaInicio: string, fechaFin: string): Promise<VentaCategoria[]> {
    return apiClient.get<VentaCategoria[]>(`/dashboard/local/${localId}/ventas-por-categoria?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`);
  }

  async getStockAlerts(localId: number): Promise<Array<{
    productoLocal: any;
    tipoAlerta: string;
    mensaje: string;
  }>> {
    return apiClient.get(`/dashboard/local/${localId}/alertas-stock`);
  }

  async getQuickSummary(localId: number): Promise<{
    ventasHoy: number;
    cantidadVentasHoy: number;
    promedioVenta: number;
    valorInventario: number;
    productosStockBajo: number;
    productosSinStock: number;
  }> {
    return apiClient.get(`/dashboard/local/${localId}/resumen`);
  }

  async getMonthlyComparison(localId: number): Promise<{
    periodo1Total: number;
    periodo1Cantidad: number;
    periodo2Total: number;
    periodo2Cantidad: number;
    porcentajeCambioTotal: number;
  }> {
    return apiClient.get(`/dashboard/local/${localId}/comparacion-mensual`);
  }
}

export const dashboardService = new DashboardService();