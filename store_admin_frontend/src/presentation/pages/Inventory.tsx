import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { AppDispatch, RootState } from '../../app/store/store';
import { fetchProducts } from '../../app/store/slices/productSlice';
import { fetchProductsByLocal, assignProductToLocal, updateStock, increaseStock } from '../../app/store/slices/productLocalSlice';
import { openAssignProductModal, closeAssignProductModal, openUpdateStockModal, closeUpdateStockModal } from '../../app/store/slices/uiSlice';
import { AsignarProductoRequest, ActualizarStockRequest } from '../../domain/types';

const Inventory: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { products } = useSelector((state: RootState) => state.products);
  const { productLocals, loading } = useSelector((state: RootState) => state.productLocals);
  const { selectedLocal } = useSelector((state: RootState) => state.locals);
  const { showAssignProductModal, showUpdateStockModal } = useSelector((state: RootState) => state.ui);
  
  const [selectedProductLocal, setSelectedProductLocal] = useState<any>(null);
  const [assignData, setAssignData] = useState<AsignarProductoRequest>({
    productoId: 0,
    localId: 0,
    stock: 0,
    precioVenta: 0,
    stockMinimo: 0,
  });
  
  const [stockData, setStockData] = useState({
    nuevoStock: 0,
    cantidad: 0,
  });

  useEffect(() => {
    dispatch(fetchProducts());
  }, [dispatch]);

  useEffect(() => {
    if (selectedLocal?.id) {
      dispatch(fetchProductsByLocal(selectedLocal.id));
    }
  }, [selectedLocal, dispatch]);

  const handleAssignProduct = () => {
    if (selectedLocal?.id) {
      setAssignData(prev => ({ ...prev, localId: selectedLocal.id! }));
      dispatch(openAssignProductModal());
    }
  };

  const handleCloseAssignModal = () => {
    dispatch(closeAssignProductModal());
    setAssignData({
      productoId: 0,
      localId: 0,
      stock: 0,
      precioVenta: 0,
      stockMinimo: 0,
    });
  };

  const handleSubmitAssign = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await dispatch(assignProductToLocal(assignData)).unwrap();
      handleCloseAssignModal();
    } catch (error) {
      console.error('Error assigning product:', error);
    }
  };

  const handleUpdateStock = (productLocal: any) => {
    setSelectedProductLocal(productLocal);
    setStockData({
      nuevoStock: productLocal.stock,
      cantidad: 0,
    });
    dispatch(openUpdateStockModal());
  };

  const handleCloseUpdateModal = () => {
    dispatch(closeUpdateStockModal());
    setSelectedProductLocal(null);
    setStockData({ nuevoStock: 0, cantidad: 0 });
  };

  const handleSubmitUpdateStock = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedProductLocal || !selectedLocal?.id) return;

    try {
      const request: ActualizarStockRequest = {
        productoId: selectedProductLocal.producto.id,
        localId: selectedLocal.id,
        nuevoStock: stockData.nuevoStock,
      };
      await dispatch(updateStock(request)).unwrap();
      handleCloseUpdateModal();
    } catch (error) {
      console.error('Error updating stock:', error);
    }
  };

  const handleIncreaseStock = async (productLocal: any, cantidad: number) => {
    if (!selectedLocal?.id) return;
    
    try {
      await dispatch(increaseStock({
        productoId: productLocal.producto.id,
        localId: selectedLocal.id,
        cantidad: cantidad
      })).unwrap();
    } catch (error) {
      console.error('Error increasing stock:', error);
    }
  };

  const getAvailableProducts = () => {
    const assignedProductIds = productLocals.map(pl => pl.producto.id);
    return products.filter(p => !assignedProductIds.includes(p.id));
  };

  if (!selectedLocal) {
    return (
      <div className="text-center py-12">
        <h2 className="text-2xl font-bold text-gray-900 mb-4">Selecciona un Local</h2>
        <p className="text-gray-600">Para gestionar el inventario, primero selecciona un local.</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Inventario</h1>
          <p className="text-gray-600 mt-2">
            Gestiona el inventario del local: <span className="font-medium">{selectedLocal.nombre}</span>
          </p>
        </div>
        <button
          onClick={handleAssignProduct}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg font-medium hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 transition-colors"
        >
          Asignar Producto
        </button>
      </div>

      {loading ? (
        <div className="flex justify-center items-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
        </div>
      ) : (
        <div className="bg-white shadow-sm rounded-lg overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Producto
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Stock Actual
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Stock Mínimo
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Precio Venta
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Estado
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Acciones
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {productLocals.map((productLocal) => (
                <tr key={productLocal.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div>
                      <div className="text-sm font-medium text-gray-900">
                        {productLocal.producto.nombre}
                      </div>
                      <div className="text-sm text-gray-500">
                        {productLocal.producto.categoria}
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <span className={`text-sm font-medium ${
                        productLocal.stock === 0 
                          ? 'text-red-600' 
                          : productLocal.stock <= productLocal.stockMinimo 
                          ? 'text-yellow-600' 
                          : 'text-gray-900'
                      }`}>
                        {productLocal.stock}
                      </span>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {productLocal.stockMinimo}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    ${productLocal.precioVenta.toFixed(2)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                      productLocal.stock === 0
                        ? 'bg-red-100 text-red-800'
                        : productLocal.stock <= productLocal.stockMinimo
                        ? 'bg-yellow-100 text-yellow-800'
                        : 'bg-green-100 text-green-800'
                    }`}>
                      {productLocal.stock === 0 
                        ? 'Sin Stock' 
                        : productLocal.stock <= productLocal.stockMinimo 
                        ? 'Stock Bajo' 
                        : 'En Stock'
                      }
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                    <button
                      onClick={() => handleUpdateStock(productLocal)}
                      className="text-primary-600 hover:text-primary-900"
                    >
                      Actualizar Stock
                    </button>
                    <button
                      onClick={() => handleIncreaseStock(productLocal, 10)}
                      className="text-green-600 hover:text-green-900"
                    >
                      +10
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {productLocals.length === 0 && (
            <div className="text-center py-12">
              <svg className="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" />
              </svg>
              <h3 className="mt-2 text-sm font-medium text-gray-900">No hay productos en inventario</h3>
              <p className="mt-1 text-sm text-gray-500">Comienza asignando productos a este local.</p>
              <div className="mt-6">
                <button
                  onClick={handleAssignProduct}
                  className="bg-primary-600 text-white px-4 py-2 rounded-lg font-medium hover:bg-primary-700"
                >
                  Asignar Producto
                </button>
              </div>
            </div>
          )}
        </div>
      )}

      {/* Assign Product Modal */}
      {showAssignProductModal && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
            <div className="mt-3">
              <h3 className="text-lg font-medium text-gray-900 mb-4">Asignar Producto al Local</h3>
              <form onSubmit={handleSubmitAssign}>
                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Producto</label>
                    <select
                      value={assignData.productoId}
                      onChange={(e) => setAssignData(prev => ({ ...prev, productoId: parseInt(e.target.value) }))}
                      required
                      className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                    >
                      <option value="">Seleccionar producto</option>
                      {getAvailableProducts().map(product => (
                        <option key={product.id} value={product.id}>
                          {product.nombre}
                        </option>
                      ))}
                    </select>
                  </div>
                  
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Stock Initial</label>
                    <input
                      type="number"
                      value={assignData.stock}
                      onChange={(e) => setAssignData(prev => ({ ...prev, stock: parseInt(e.target.value) || 0 }))}
                      min="0"
                      required
                      className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                    />
                  </div>
                  
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Precio de Venta</label>
                    <input
                      type="number"
                      value={assignData.precioVenta}
                      onChange={(e) => setAssignData(prev => ({ ...prev, precioVenta: parseFloat(e.target.value) || 0 }))}
                      min="0"
                      step="0.01"
                      required
                      className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                    />
                  </div>
                  
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Stock Mínimo</label>
                    <input
                      type="number"
                      value={assignData.stockMinimo}
                      onChange={(e) => setAssignData(prev => ({ ...prev, stockMinimo: parseInt(e.target.value) || 0 }))}
                      min="0"
                      className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                    />
                  </div>
                </div>
                
                <div className="flex justify-end space-x-3 mt-6">
                  <button
                    type="button"
                    onClick={handleCloseAssignModal}
                    className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    disabled={loading}
                    className="px-4 py-2 text-sm font-medium text-white bg-primary-600 rounded-md hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 disabled:opacity-50"
                  >
                    {loading ? 'Asignando...' : 'Asignar Producto'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}

      {/* Update Stock Modal */}
      {showUpdateStockModal && selectedProductLocal && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
            <div className="mt-3">
              <h3 className="text-lg font-medium text-gray-900 mb-4">
                Actualizar Stock: {selectedProductLocal.producto.nombre}
              </h3>
              <form onSubmit={handleSubmitUpdateStock}>
                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700">
                      Stock Actual: {selectedProductLocal.stock}
                    </label>
                  </div>
                  
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Nuevo Stock</label>
                    <input
                      type="number"
                      value={stockData.nuevoStock}
                      onChange={(e) => setStockData(prev => ({ ...prev, nuevoStock: parseInt(e.target.value) || 0 }))}
                      min="0"
                      required
                      className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                    />
                  </div>
                </div>
                
                <div className="flex justify-end space-x-3 mt-6">
                  <button
                    type="button"
                    onClick={handleCloseUpdateModal}
                    className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    disabled={loading}
                    className="px-4 py-2 text-sm font-medium text-white bg-primary-600 rounded-md hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 disabled:opacity-50"
                  >
                    {loading ? 'Actualizando...' : 'Actualizar Stock'}
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

export default Inventory;