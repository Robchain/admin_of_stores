import { createSlice } from '@reduxjs/toolkit';

interface SaleState {
  loading: boolean;
  error: string | null;
}

const initialState: SaleState = {
  loading: false,
  error: null,
};

const saleSlice = createSlice({
  name: 'sales',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
  },
});

export const { clearError } = saleSlice.actions;
export default saleSlice.reducer;