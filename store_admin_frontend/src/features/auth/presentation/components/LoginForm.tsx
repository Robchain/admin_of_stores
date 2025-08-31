import React, { useState, useEffect } from "react";
// import { useAuth } from "../../../shared/hooks/use-auth";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../../../shared";

const LoginForm: React.FC = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const { login, isLoading, error, isAuthenticated, clearAuthError, isAdmin } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (isAuthenticated && isAdmin()) {
      navigate("/admin/dashboard");
    }
  }, [isAuthenticated, isAdmin, navigate]);

  useEffect(() => {
    return () => {
      if (error) {
        clearAuthError();
      }
    };
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!email || !password) {
      return;
    }

    const success = await login({ email, password });

    if (success) {
      console.log("Login exitoso");
    }
  };

  const handleInputChange = () => {
    if (error) {
      clearAuthError();
    }
  };

  return (
    <div className="flex justify-center items-center min-h-screen bg-gray-100">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-lg">
        {/* Header */}
        <div className="bg-blue-600 text-white text-center py-4 rounded-t-2xl">
          <h4 className="text-lg font-semibold">Iniciar Sesión - Admin</h4>
        </div>

        {/* Body */}
        <div className="p-6">
          {/* Error Alert */}
          {error && (
            <div className="mb-3 rounded-md bg-red-100 text-red-700 px-4 py-2 text-sm">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Email */}
            <div>
              <label
                htmlFor="email"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Email
              </label>
              <input
                type="email"
                id="email"
                placeholder="Ingresa tu email"
                value={email}
                onChange={(e) => {
                  setEmail(e.target.value);
                  handleInputChange();
                }}
                required
                disabled={isLoading}
                className="w-full px-3 py-2 border rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100"
              />
            </div>

            {/* Password */}
            <div>
              <label
                htmlFor="password"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Contraseña
              </label>
              <input
                type="password"
                id="password"
                placeholder="Ingresa tu contraseña"
                value={password}
                onChange={(e) => {
                  setPassword(e.target.value);
                  handleInputChange();
                }}
                required
                disabled={isLoading}
                className="w-full px-3 py-2 border rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100"
              />
            </div>

            {/* Submit Button */}
            <button
              type="submit"
              disabled={isLoading || !email || !password}
              className="w-full flex justify-center items-center py-2 px-4 bg-blue-600 text-white font-medium rounded-lg shadow hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isLoading ? (
                <>
                  <svg
                    className="animate-spin h-5 w-5 mr-2 text-white"
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                  >
                    <circle
                      className="opacity-25"
                      cx="12"
                      cy="12"
                      r="10"
                      stroke="currentColor"
                      strokeWidth="4"
                    ></circle>
                    <path
                      className="opacity-75"
                      fill="currentColor"
                      d="M4 12a8 8 0 018-8v8H4z"
                    ></path>
                  </svg>
                  Iniciando sesión...
                </>
              ) : (
                "Iniciar Sesión"
              )}
            </button>
          </form>

          {/* Footer note */}
          <div className="mt-4 text-center">
            <small className="text-gray-500">
              Demo - Usa credenciales de administrador
            </small>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginForm;
