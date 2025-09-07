import { createSlice } from '@reduxjs/toolkit';

interface DashboardState {
  loading: boolean;
  error: string | null;
}

const initialState: DashboardState = {
  loading: false,
  error: null,
};

const dashboardSlice = createSlice({
  name: 'dashboard',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
  },
});

export const { clearError } = dashboardSlice.actions;
export default dashboardSlice.reducer;