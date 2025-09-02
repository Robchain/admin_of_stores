import { configureStore } from '@reduxjs/toolkit';
import authSlice from './slices/authSlice';
import productSlice from './slices/productSlice';
import localSlice from './slices/localSlice';
import productLocalSlice from './slices/productLocalSlice';
import saleSlice from './slices/saleSlice';
import dashboardSlice from './slices/dashboardSlice';
import uiSlice from './slices/uiSlice';

export const store = configureStore({
  reducer: {
    auth: authSlice,
    products: productSlice,
    locals: localSlice,
    productLocals: productLocalSlice,
    sales: saleSlice,
    dashboard: dashboardSlice,
    ui: uiSlice,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;