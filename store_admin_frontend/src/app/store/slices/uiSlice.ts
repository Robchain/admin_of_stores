import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface UiState {
  sidebarOpen: boolean;
  currentView: 'dashboard' | 'products' | 'inventory' | 'sales' | 'locals';
  showCreateProductModal: boolean;
  showCreateLocalModal: boolean;
  showAssignProductModal: boolean;
  showUpdateStockModal: boolean;
  showCreateSaleModal: boolean;
}

const initialState: UiState = {
  sidebarOpen: true,
  currentView: 'dashboard',
  showCreateProductModal: false,
  showCreateLocalModal: false,
  showAssignProductModal: false,
  showUpdateStockModal: false,
  showCreateSaleModal: false,
};

const uiSlice = createSlice({
  name: 'ui',
  initialState,
  reducers: {
    toggleSidebar: (state) => {
      state.sidebarOpen = !state.sidebarOpen;
    },
    setCurrentView: (state, action: PayloadAction<UiState['currentView']>) => {
      state.currentView = action.payload;
    },
    openCreateProductModal: (state) => {
      state.showCreateProductModal = true;
    },
    closeCreateProductModal: (state) => {
      state.showCreateProductModal = false;
    },
    openCreateLocalModal: (state) => {
      state.showCreateLocalModal = true;
    },
    closeCreateLocalModal: (state) => {
      state.showCreateLocalModal = false;
    },
    openAssignProductModal: (state) => {
      state.showAssignProductModal = true;
    },
    closeAssignProductModal: (state) => {
      state.showAssignProductModal = false;
    },
    openUpdateStockModal: (state) => {
      state.showUpdateStockModal = true;
    },
    closeUpdateStockModal: (state) => {
      state.showUpdateStockModal = false;
    },
    openCreateSaleModal: (state) => {
      state.showCreateSaleModal = true;
    },
    closeCreateSaleModal: (state) => {
      state.showCreateSaleModal = false;
    },
  },
});

export const {
  toggleSidebar,
  setCurrentView,
  openCreateProductModal,
  closeCreateProductModal,
  openCreateLocalModal,
  closeCreateLocalModal,
  openAssignProductModal,
  closeAssignProductModal,
  openUpdateStockModal,
  closeUpdateStockModal,
  openCreateSaleModal,
  closeCreateSaleModal,
} = uiSlice.actions;

export default uiSlice.reducer;