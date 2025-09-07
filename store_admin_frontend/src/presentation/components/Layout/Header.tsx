import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { AppDispatch, RootState } from '../../../app/store/store';
import { logout } from '../../../app/store/slices/authSlice';
import { toggleSidebar } from '../../../app/store/slices/uiSlice';
import { selectLocal } from '../../../app/store/slices/localSlice';

const Header: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { user } = useSelector((state: RootState) => state.auth);
  const { locals, selectedLocal } = useSelector((state: RootState) => state.locals);
  const { sidebarOpen } = useSelector((state: RootState) => state.ui);

  const handleLogout = () => {
    dispatch(logout());
  };

  const handleToggleSidebar = () => {
    dispatch(toggleSidebar());
  };

  const handleLocalChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const localId = parseInt(e.target.value);
    const local = locals.find(l => l.id === localId);
    if (local) {
      dispatch(selectLocal(local));
    }
  };

  return (
    <header className="bg-white shadow-sm border-b border-gray-200">
      <div className="flex items-center justify-between px-6 py-4">
        <div className="flex items-center space-x-4">
          <button
            onClick={handleToggleSidebar}
            className="text-gray-500 hover:text-gray-700 focus:outline-none focus:ring-2 focus:ring-primary-500 rounded-lg p-2"
          >
            <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>
          
          {locals.length > 0 && (
            <div className="flex items-center space-x-2">
              <span className="text-sm font-medium text-gray-700">Local:</span>
              <select
                value={selectedLocal?.id || ''}
                onChange={handleLocalChange}
                className="border border-gray-300 rounded-md px-3 py-1 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
              >
                {locals.map((local) => (
                  <option key={local.id} value={local.id}>
                    {local.nombre}
                  </option>
                ))}
              </select>
            </div>
          )}
        </div>

        <div className="flex items-center space-x-4">
          <div className="text-sm text-gray-700">
            Bienvenido, <span className="font-medium">{user?.username}</span>
          </div>
          
          <button
            onClick={handleLogout}
            className="bg-red-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2 transition-colors"
          >
            Cerrar Sesión
          </button>
        </div>
      </div>
    </header>
  );
};

export default Header;