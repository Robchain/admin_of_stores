export interface User {
  idUser: number;
  name: string;
  email: string;
  rol: string;
  dateCreated?: string;
}

export interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
}

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface RegisterData {
  name: string;
  email: string;
  password: string;
  rol: 'Administrador' | 'Comprador';
}