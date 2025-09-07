import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { AppDispatch, RootState } from '../../app/store/store';
import { createLocal } from '../../app/store/slices/localSlice';
import { openCreateLocalModal, closeCreateLocalModal } from '../../app/store/slices/uiSlice';
import { Local } from '../../domain/types';

const Locals: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { locals, loading, error } = useSelector((state: RootState) => state.locals);
  const { showCreateLocalModal } = useSelector((state: RootState) => state.ui);
  
  const [newLocal, setNewLocal] = useState<Omit<Local, 'id'>>({
    nombre: '',
    direccion: '',
    telefono: '',
    ciudad: '',
  });

  const handleCreateLocal = () => {
    dispatch(openCreateLocalModal());
  };

  const handleCloseModal = () => {
    dispatch(closeCreateLocalModal());
    setNewLocal({
      nombre: '',
      direccion: '',
      telefono: '',
      ciudad: '',
    });
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setNewLocal(prev => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmitLocal = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await dispatch(createLocal(newLocal)).unwrap();
      handleCloseModal();
    } catch (error) {
      console.error('Error creating local:', error);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Locales</h1>
          <p className="text-gray-600 mt-2">Gestiona tus puntos de venta</p>
        </div>
        <button
          onClick={handleCreateLocal}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg font-medium hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 transition-colors"
        >
          Crear Local
        </button>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 rounded-md p-4">
          <p className="text-red-800">{error}</p>
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {locals.map((local) => (
          <div key={local.id} className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-medium text-gray-900">{local.nombre}</h3>
              <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                local.activo
                  ? 'bg-green-100 text-green-800'
                  : 'bg-red-100 text-red-800'
              }`}>
                {local.activo ? 'Activo' : 'Inactivo'}
              </span>
            </div>
            
            <div className="space-y-2 text-sm text-gray-600">
              {local.direccion && (
                <div className="flex items-center">
                  <svg className="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                  </svg>
                  <span>{local.direccion}</span>
                </div>
              )}
              
              {local.telefono && (
                <div className="flex items-center">
                  <svg className="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                  </svg>
                  <span>{local.telefono}</span>
                </div>
              )}
              
              {local.ciudad && (
                <div className="flex items-center">
                  <svg className="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-4m-5 0H9m11 0a2 2 0 01-2 2H5a2 2 0 01-2-2m0 0V5a2 2 0 012-2h2M7 7h.01M7 3h5v2H7V3zm-4 8h4m0 0h4m-4 0v4m-4-4h.01" />
                  </svg>
                  <span>{local.ciudad}</span>
                </div>
              )}
            </div>
            
            <div className="mt-4 pt-4 border-t border-gray-200">
              <div className="text-xs text-gray-500">
                Creado: {new Date(local.createdAt || '').toLocaleDateString()}
              </div>
            </div>
          </div>
        ))}
      </div>

      {locals.length === 0 && (
        <div className="text-center py-12">
          <svg className="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-4m-5 0H9m11 0a2 2 0 01-2 2H5a2 2 0 01-2-2m0 0V5a2 2 0 012-2h2M7 7h.01M7 3h5v2H7V3zm-4 8h4m0 0h4m-4 0v4m-4-4h.01" />
          </svg>
          <h3 className="mt-2 text-sm font-medium text-gray-900">No hay locales</h3>
          <p className="mt-1 text-sm text-gray-500">Comienza creando tu primer local.</p>
          <div className="mt-6">
            <button
              onClick={handleCreateLocal}
              className="bg-primary-600 text-white px-4 py-2 rounded-lg font-medium hover:bg-primary-700"
            >
              Crear Local
            </button>
          </div>
        </div>
      )}

      {/* Create Local Modal */}
      {showCreateLocalModal && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
            <div className="mt-3">
              <h3 className="text-lg font-medium text-gray-900 mb-4">Crear Nuevo Local</h3>
              <form onSubmit={handleSubmitLocal}>
                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Nombre *</label>
                    <input
                      type="text"
                      name="nombre"
                      value={newLocal.nombre}
                      onChange={handleInputChange}
                      required
                      className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                    />
                  </div>
                  
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Dirección</label>
                    <input
                      type="text"
                      name="direccion"
                      value={newLocal.direccion}
                      onChange={handleInputChange}
                      className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                    />
                  </div>
                  
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Teléfono</label>
                    <input
                      type="text"
                      name="telefono"
                      value={newLocal.telefono}
                      onChange={handleInputChange}
                      className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                    />
                  </div>
                  
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Ciudad</label>
                    <input
                      type="text"
                      name="ciudad"
                      value={newLocal.ciudad}
                      onChange={handleInputChange}
                      className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                    />
                  </div>
                </div>
                
                <div className="flex justify-end space-x-3 mt-6">
                  <button
                    type="button"
                    onClick={handleCloseModal}
                    className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    disabled={loading}
                    className="px-4 py-2 text-sm font-medium text-white bg-primary-600 rounded-md hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 disabled:opacity-50"
                  >
                    {loading ? 'Creando...' : 'Crear Local'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Locals;