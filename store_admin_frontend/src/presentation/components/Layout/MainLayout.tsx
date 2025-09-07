import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { AppDispatch, RootState } from '../../../app/store/store';
import { fetchMyLocals } from '../../../app/store/slices/localSlice';
import Sidebar from './Sidebar';
import Header from './Header';

interface MainLayoutProps {
  children: React.ReactNode;
}

const MainLayout: React.FC<MainLayoutProps> = ({ children }) => {
  const dispatch = useDispatch<AppDispatch>();
  const { sidebarOpen } = useSelector((state: RootState) => state.ui);

  useEffect(() => {
    dispatch(fetchMyLocals());
  }, [dispatch]);

  return (
    <div className="flex h-screen bg-gray-50">
      <Sidebar />
      <div className={`flex-1 flex flex-col overflow-hidden ${sidebarOpen ? 'ml-64' : 'ml-16'} transition-all duration-300`}>
        <Header />
        <main className="flex-1 overflow-x-hidden overflow-y-auto bg-gray-50 p-6">
          {children}
        </main>
      </div>
    </div>
  );
};

export default MainLayout;