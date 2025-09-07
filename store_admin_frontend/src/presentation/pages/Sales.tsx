import React, { useEffect, useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { RootState, AppDispatch } from '../../app/store/store';
import { fetchProductsByLocal } from '../../app/store/slices/productLocalSlice';
import { openCreateSaleModal, closeCreateSaleModal } from '../../app/store/slices/uiSlice';
import { saleService } from '../../infrastructure/services/SaleService';
import { CrearVentaRequest, ItemVentaRequest, MetodoPago, Venta } from '../../domain/types';

const Sales: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { selectedLocal } = useSelector((state: RootState) => state.locals);
  const { productLocals } = useSelector((state: RootState) => state.productLocals);
  const { showCreateSaleModal } = useSelector((state: RootState) => state.ui);
  
  const [sales, setSales] = useState<Venta[]>([]);
  const [loading, setLoading] = useState(false);
  const [saleItems, setSaleItems] = useState<ItemVentaRequest[]>([]);
  const [saleData, setSaleData] = useState({
    metodoPago: MetodoPago.EFECTIVO,
    descuento: 0,
    impuestos: 0,
    observaciones: '',
  });

  useEffect(() => {
    if (selectedLocal?.id) {
      dispatch(fetchProductsByLocal(selectedLocal.id));
      fetchSales();
    }
  }, [selectedLocal, dispatch]);

  const fetchSales = async () => {
    if (!selectedLocal?.id) return;
    
    setLoading(true);
    try {
      const salesData = await saleService.getSalesByLocal(selectedLocal.id);
      setSales(salesData);
    } catch (error) {
      console.error('Error fetching sales:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateSale = () => {
    setSaleItems([{ productoId: 0, cantidad: 1, precioUnitario: 0, descuentoItem: 0 }]);
    dispatch(openCreateSaleModal());
  };

  const handleCloseSaleModal = () => {
    dispatch(closeCreateSaleModal());
    setSaleItems([]);
    setSaleData({
      metodoPago: MetodoPago.EFECTIVO,
      descuento: 0,
      impuestos: 0,
      observaciones: '',
    });
  };

  const addSaleItem = () => {
    setSaleItems([...saleItems, { productoId: 0, cantidad: 1, precioUnitario: 0, descuentoItem: 0 }]);
  };

  const removeSaleItem = (index: number) => {
    setSaleItems(saleItems.filter((_, i) => i !== index));
  };

  const updateSaleItem = (index: number, field: keyof ItemVentaRequest, value: any) => {
    const newItems = [...saleItems];
    newItems[index] = { ...newItems[index], [field]: value };
    
    // Auto-fill price when product is selected
    if (field === 'productoId' && value) {
      const productLocal = productLocals.find(pl => pl.producto.id === parseInt(value));
      if (productLocal) {
        newItems[index].precioUnitario = productLocal.precioVenta;
      }
    }
    
    setSaleItems(newItems);
  };

  const calculateTotal = () => {
    const subtotal = saleItems.reduce((sum, item) => {
      if (item.productoId && item.cantidad && item.precioUnitario) {
        return sum + (item.cantidad * item.precioUnitario - (item.descuentoItem || 0));
      }
      return sum;
    }, 0);
    
    return subtotal + saleData.impuestos - saleData.descuento;
  };

  const handleSubmitSale = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedLocal?.id) return;

    const validItems = saleItems.filter(item => 
      item.productoId && item.cantidad > 0 && item.precioUnitario! > 0
    );

    if (validItems.length === 0) {
      alert('Agrega al menos un producto válido');
      return;
    }

    const saleRequest: CrearVentaRequest = {
      localId: selectedLocal.id,
      items: validItems,
      metodoPago: saleData.metodoPago,
      descuento: saleData.descuento,
      impuestos: saleData.impuestos,
      observaciones: saleData.observaciones,
    };

    setLoading(true);
    try {
      await saleService.createSale(saleRequest);
      handleCloseSaleModal();
      fetchSales();
      dispatch(fetchProductsByLocal(selectedLocal.id)); // Refresh inventory
    } catch (error) {
      console.error('Error creating sale:', error);
      alert('Error al crear la venta');
    } finally {
      setLoading(false);
    }
  };

  if (!selectedLocal) {
    return (
      <div className="text-center py-12">
        <h2 className="text-2xl font-bold text-gray-900 mb-4">Selecciona un Local</h2>
        <p className="text-gray-600">Para gestionar las ventas, primero selecciona un local.</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Ventas</h1>
          <p className="text-gray-600 mt-2">
            Gestiona las ventas del local: <span className="font-medium">{selectedLocal.nombre}</span>
          </p>
        </div>
        <button
          onClick={handleCreateSale}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg font-medium hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 transition-colors"
        >
          Nueva Venta
        </button>
      </div>

      {loading && !showCreateSaleModal ? (
        <div className="flex justify-center items-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
        </div>
      ) : (
        <div className="bg-white shadow-sm rounded-lg overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Factura
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Fecha
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Total
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Método Pago
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Estado
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {sales.map((sale) => (
                <tr key={sale.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {sale.numeroFactura}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {new Date(sale.fechaVenta).toLocaleDateString()}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    ${sale.total.toFixed(2)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {sale.metodoPago?.replace('_', ' ') || '-'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                      sale.estado === 'COMPLETADA'
                        ? 'bg-green-100 text-green-800'
                        : sale.estado === 'CANCELADA'
                        ? 'bg-red-100 text-red-800'
                        : 'bg-yellow-100 text-yellow-800'
                    }`}>
                      {sale.estado}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {sales.length === 0 && (
            <div className="text-center py-12">
              <svg className="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <h3 className="mt-2 text-sm font-medium text-gray-900">No hay ventas</h3>
              <p className="mt-1 text-sm text-gray-500">Comienza realizando tu primera venta.</p>
              <div className="mt-6">
                <button
                  onClick={handleCreateSale}
                  className="bg-primary-600 text-white px-4 py-2 rounded-lg font-medium hover:bg-primary-700"
                >
                  Nueva Venta
                </button>
              </div>
            </div>
          )}
        </div>
      )}

      {/* Create Sale Modal */}
      {showCreateSaleModal && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div className="relative top-10 mx-auto p-5 border w-4/5 max-w-4xl shadow-lg rounded-md bg-white">
            <div className="mt-3">
              <h3 className="text-lg font-medium text-gray-900 mb-4">Nueva Venta</h3>
              <form onSubmit={handleSubmitSale}>
                <div className="space-y-6">
                  {/* Sale Items */}
                  <div>
                    <div className="flex justify-between items-center mb-4">
                      <h4 className="text-md font-medium text-gray-900">Productos</h4>
                      <button
                        type="button"
                        onClick={addSaleItem}
                        className="bg-green-600 text-white px-3 py-1 rounded-md text-sm hover:bg-green-700"
                      >
                        Agregar Producto
                      </button>
                    </div>
                    
                    <div className="space-y-3">
                      {saleItems.map((item, index) => (
                        <div key={index} className="grid grid-cols-5 gap-3 items-center border p-3 rounded-md">
                          <div>
                            <select
                              value={item.productoId}
                              onChange={(e) => updateSaleItem(index, 'productoId', parseInt(e.target.value))}
                              required
                              className="block w-full border border-gray-300 rounded-md px-3 py-1 text-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                            >
                              <option value="">Seleccionar</option>
                              {productLocals.filter(pl => pl.stock > 0).map(pl => (
                                <option key={pl.producto.id} value={pl.producto.id}>
                                  {pl.producto.nombre} (Stock: {pl.stock})
                                </option>
                              ))}
                            </select>
                          </div>
                          
                          <div>
                            <input
                              type="number"
                              placeholder="Cantidad"
                              value={item.cantidad}
                              onChange={(e) => updateSaleItem(index, 'cantidad', parseInt(e.target.value) || 0)}
                              min="1"
                              max={productLocals.find(pl => pl.producto.id === item.productoId)?.stock || 999}
                              required
                              className="block w-full border border-gray-300 rounded-md px-3 py-1 text-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                            />
                          </div>
                          
                          <div>
                            <input
                              type="number"
                              placeholder="Precio Unit."
                              value={item.precioUnitario}
                              onChange={(e) => updateSaleItem(index, 'precioUnitario', parseFloat(e.target.value) || 0)}
                              step="0.01"
                              min="0"
                              required
                              className="block w-full border border-gray-300 rounded-md px-3 py-1 text-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                            />
                          </div>
                          
                          <div>
                            <input
                              type="number"
                              placeholder="Descuento"
                              value={item.descuentoItem}
                              onChange={(e) => updateSaleItem(index, 'descuentoItem', parseFloat(e.target.value) || 0)}
                              step="0.01"
                              min="0"
                              className="block w-full border border-gray-300 rounded-md px-3 py-1 text-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                            />
                          </div>
                          
                          <div className="flex justify-center">
                            <button
                              type="button"
                              onClick={() => removeSaleItem(index)}
                              className="text-red-600 hover:text-red-900"
                            >
                              <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                              </svg>
                            </button>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>

                  {/* Sale Details */}
                  <div className="grid grid-cols-2 gap-6">
                    <div>
                      <label className="block text-sm font-medium text-gray-700">Método de Pago</label>
                      <select
                        value={saleData.metodoPago}
                        onChange={(e) => setSaleData(prev => ({ ...prev, metodoPago: e.target.value as MetodoPago }))}
                        className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                      >
                        <option value={MetodoPago.EFECTIVO}>Efectivo</option>
                        <option value={MetodoPago.TARJETA_CREDITO}>Tarjeta de Crédito</option>
                        <option value={MetodoPago.TARJETA_DEBITO}>Tarjeta de Débito</option>
                        <option value={MetodoPago.TRANSFERENCIA}>Transferencia</option>
                        <option value={MetodoPago.OTRO}>Otro</option>
                      </select>
                    </div>
                    
                    <div>
                      <label className="block text-sm font-medium text-gray-700">Descuento General</label>
                      <input
                        type="number"
                        value={saleData.descuento}
                        onChange={(e) => setSaleData(prev => ({ ...prev, descuento: parseFloat(e.target.value) || 0 }))}
                        step="0.01"
                        min="0"
                        className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                      />
                    </div>
                    
                    <div>
                      <label className="block text-sm font-medium text-gray-700">Impuestos</label>
                      <input
                        type="number"
                        value={saleData.impuestos}
                        onChange={(e) => setSaleData(prev => ({ ...prev, impuestos: parseFloat(e.target.value) || 0 }))}
                        step="0.01"
                        min="0"
                        className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                      />
                    </div>
                    
                    <div>
                      <label className="block text-sm font-medium text-gray-700">Total</label>
                      <div className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 bg-gray-50 text-lg font-bold">
                        ${calculateTotal().toFixed(2)}
                      </div>
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700">Observaciones</label>
                    <textarea
                      value={saleData.observaciones}
                      onChange={(e) => setSaleData(prev => ({ ...prev, observaciones: e.target.value }))}
                      rows={3}
                      className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                    />
                  </div>
                </div>
                
                <div className="flex justify-end space-x-3 mt-6">
                  <button
                    type="button"
                    onClick={handleCloseSaleModal}
                    className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    disabled={loading}
                    className="px-4 py-2 text-sm font-medium text-white bg-primary-600 rounded-md hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 disabled:opacity-50"
                  >
                    {loading ? 'Procesando...' : 'Procesar Venta'}
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

export default Sales;