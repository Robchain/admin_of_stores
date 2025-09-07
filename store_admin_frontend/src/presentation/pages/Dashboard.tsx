import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { RootState, AppDispatch } from '../../app/store/store';
import { dashboardService } from '../../infrastructure/services/DashboardService';

const Dashboard: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { selectedLocal } = useSelector((state: RootState) => state.locals);
  const [dashboardData, setDashboardData] = React.useState<any>(null);
  const [loading, setLoading] = React.useState(false);

  useEffect(() => {
    if (selectedLocal?.id) {
      fetchDashboardData();
    }
  }, [selectedLocal]);

  const fetchDashboardData = async () => {
    if (!selectedLocal?.id) return;
    
    setLoading(true);
    try {
      const data = await dashboardService.getTodayDashboard(selectedLocal.id);
      setDashboardData(data);
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (!selectedLocal) {
    return (
      <div className="text-center py-12">
        <h2 className="text-2xl font-bold text-gray-900 mb-4">Selecciona un Local</h2>
        <p className="text-gray-600">Para ver el dashboard, primero selecciona un local en el header.</p>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="flex justify-center items-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-gray-600 mt-2">
          Resumen del local: <span className="font-medium">{selectedLocal.nombre}</span>
        </p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <div className="w-8 h-8 bg-green-500 rounded-md flex items-center justify-center">
                <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
            </div>
            <div className="ml-5 w-0 flex-1">
              <dl>
                <dt className="text-sm font-medium text-gray-500 truncate">Ventas Hoy</dt>
                <dd className="text-lg font-medium text-gray-900">
                  ${dashboardData?.totalVentas?.toFixed(2) || '0.00'}
                </dd>
              </dl>
            </div>
          </div>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <div className="w-8 h-8 bg-blue-500 rounded-md flex items-center justify-center">
                <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 8l2 2 4-4" />
                </svg>
              </div>
            </div>
            <div className="ml-5 w-0 flex-1">
              <dl>
                <dt className="text-sm font-medium text-gray-500 truncate">Transacciones</dt>
                <dd className="text-lg font-medium text-gray-900">
                  {dashboardData?.cantidadVentas || 0}
                </dd>
              </dl>
            </div>
          </div>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <div className="w-8 h-8 bg-yellow-500 rounded-md flex items-center justify-center">
                <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v3m0 0v3m0-3h3m-3 0H9m12 0a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
            </div>
            <div className="ml-5 w-0 flex-1">
              <dl>
                <dt className="text-sm font-medium text-gray-500 truncate">Stock Bajo</dt>
                <dd className="text-lg font-medium text-gray-900">
                  {dashboardData?.productosStockBajo || 0}
                </dd>
              </dl>
            </div>
          </div>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <div className="w-8 h-8 bg-red-500 rounded-md flex items-center justify-center">
                <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                </svg>
              </div>
            </div>
            <div className="ml-5 w-0 flex-1">
              <dl>
                <dt className="text-sm font-medium text-gray-500 truncate">Sin Stock</dt>
                <dd className="text-lg font-medium text-gray-900">
                  {dashboardData?.productosSinStock || 0}
                </dd>
              </dl>
            </div>
          </div>
        </div>
      </div>

      {/* Recent Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Productos Más Vendidos</h3>
          <div className="space-y-3">
            {dashboardData?.productosMasVendidos?.slice(0, 5).map((item: any, index: number) => (
              <div key={index} className="flex items-center justify-between py-2 border-b border-gray-100 last:border-b-0">
                <div>
                  <p className="text-sm font-medium text-gray-900">{item.productoLocal?.producto?.nombre}</p>
                  <p className="text-xs text-gray-500">{item.productoLocal?.producto?.categoria}</p>
                </div>
                <div className="text-sm text-gray-600">
                  {item.cantidadVendida} vendidos
                </div>
              </div>
            )) || (
              <p className="text-gray-500 text-sm">No hay datos disponibles</p>
            )}
          </div>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Ventas por Categoría</h3>
          <div className="space-y-3">
            {dashboardData?.ventasPorCategoria?.slice(0, 5).map((item: any, index: number) => (
              <div key={index} className="flex items-center justify-between py-2 border-b border-gray-100 last:border-b-0">
                <div>
                  <p className="text-sm font-medium text-gray-900">{item.categoria}</p>
                  <p className="text-xs text-gray-500">{item.cantidadVendida} productos</p>
                </div>
                <div className="text-sm text-gray-600">
                  ${item.totalVentas?.toFixed(2)}
                </div>
              </div>
            )) || (
              <p className="text-gray-500 text-sm">No hay datos disponibles</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;