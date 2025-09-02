import { apiClient } from '../api/apiClient';
import { Local } from '../../domain/types';

export class LocalService {
  async getMyLocales(): Promise<Local[]> {
    return apiClient.get<Local[]>('/locales/mis-locales');
  }

  async createLocal(local: Omit<Local, 'id'>): Promise<Local> {
    return apiClient.post<Local, Omit<Local, 'id'>>('/locales', local);
  }

  async updateLocal(id: number, local: Partial<Local>): Promise<Local> {
    return apiClient.put<Local, Partial<Local>>(`/locales/${id}`, local);
  }

  async getLocalById(id: number): Promise<Local> {
    return apiClient.get<Local>(`/locales/${id}`);
  }

  async searchLocales(nombre: string): Promise<Local[]> {
    return apiClient.get<Local[]>(`/locales/buscar?nombre=${nombre}`);
  }

  async getCities(): Promise<string[]> {
    return apiClient.get<string[]>('/locales/ciudades');
  }

  async deactivateLocal(id: number): Promise<{ message: string }> {
    return apiClient.delete<{ message: string }>(`/locales/${id}`);
  }

  async activateLocal(id: number): Promise<{ message: string }> {
    return apiClient.patch<{ message: string }>(`/locales/${id}/activar`);
  }
}

export const localService = new LocalService();