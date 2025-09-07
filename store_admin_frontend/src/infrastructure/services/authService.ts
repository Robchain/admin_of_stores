import { apiClient } from '../api/apiClient';
import { LoginRequest, AuthResponse } from '../../domain/types';

export class AuthService {
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    return apiClient.post<AuthResponse, LoginRequest>('/auth/login', credentials);
  }

  saveToken(token: string): void {
    localStorage.setItem('token', token);
  }

  saveUser(user: { username: string; email: string }): void {
    localStorage.setItem('user', JSON.stringify(user));
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUser(): { username: string; email: string } | null {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}

export const authService = new AuthService();