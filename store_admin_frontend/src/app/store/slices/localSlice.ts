import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { Local } from '../../../domain/types';
import { localService } from '../../../infrastructure/services/LocalService';

interface LocalState {
  locals: Local[];
  selectedLocal: Local | null;
  loading: boolean;
  error: string | null;
}

const initialState: LocalState = {
  locals: [],
  selectedLocal: null,
  loading: false,
  error: null,
};

export const fetchMyLocals = createAsyncThunk(
  'locals/fetchMyLocals',
  async (_, { rejectWithValue }) => {
    try {
      return await localService.getMyLocales();
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Error al cargar locales');
    }
  }
);

export const createLocal = createAsyncThunk(
  'locals/createLocal',
  async (local: Omit<Local, 'id'>, { rejectWithValue }) => {
    try {
      return await localService.createLocal(local);
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Error al crear local');
    }
  }
);

const localSlice = createSlice({
  name: 'locals',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    selectLocal: (state, action: PayloadAction<Local>) => {
      state.selectedLocal = action.payload;
    },
    clearSelectedLocal: (state) => {
      state.selectedLocal = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch My Locals
      .addCase(fetchMyLocals.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchMyLocals.fulfilled, (state, action) => {
        state.loading = false;
        state.locals = action.payload;
        // Set first local as selected if none is selected
        if (!state.selectedLocal && action.payload.length > 0) {
          state.selectedLocal = action.payload[0];
        }
      })
      .addCase(fetchMyLocals.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      // Create Local
      .addCase(createLocal.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createLocal.fulfilled, (state, action) => {
        state.loading = false;
        state.locals.push(action.payload);
      })
      .addCase(createLocal.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      });
  },
});

export const { clearError, selectLocal, clearSelectedLocal } = localSlice.actions;
export default localSlice.reducer;