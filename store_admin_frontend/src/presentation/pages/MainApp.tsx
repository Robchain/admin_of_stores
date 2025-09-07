import React from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../../app/store/store';
import MainLayout from '../components/Layout/MainLayout';
import Dashboard from './Dashboard';
import Products from './Products';
import Inventory from './Inventory';
import Sales from './Sales';
import Locals from './Locals';

const MainApp: React.FC = () => {
  const { currentView } = useSelector((state: RootState) => state.ui);

  const renderCurrentView = () => {
    switch (currentView) {
      case 'dashboard':
        return <Dashboard />;
      case 'products':
        return <Products />;
      case 'inventory':
        return <Inventory />;
      case 'sales':
        return <Sales />;
      case 'locals':
        return <Locals />;
      default:
        return <Dashboard />;
    }
  };

  return (
    <MainLayout>
      {renderCurrentView()}
    </MainLayout>
  );
};

export default MainApp;