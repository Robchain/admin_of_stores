// src/shared/hooks/use-auth.ts
import { useAppSelector, useAppDispatch } from './redux-hooks';
import { loginAsync, logoutAsync, clearError } from '../../infrastructure/store/slices/auth-slice';
import { LoginCredentials } from '../types';


export const useAuth = () => {
  const dispatch = useAppDispatch();
  const { user, token, isAuthenticated, isLoading, error } = useAppSelector(state => state.auth);

  const login = async (credentials: LoginCredentials) => {
    const result = await dispatch(loginAsync(credentials));
    return result.meta.requestStatus === 'fulfilled';
  };

  const logout = async () => {
    await dispatch(logoutAsync());
  };

  const clearAuthError = () => {
    dispatch(clearError());
  };

  const isAdmin = () => {
    return user?.rol === 'Administrador';
  };

  const isBuyer = () => {
    return user?.rol === 'Comprador';
  };

  return {
    user,
    token,
    isAuthenticated,
    isLoading,
    error,
    login,
    logout,
    clearAuthError,
    isAdmin,
    isBuyer,
  };
};