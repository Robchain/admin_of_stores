import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { ProductoLocal, AsignarProductoRequest, ActualizarStockRequest } from '../../../domain/types';
import { productLocalService } from '../../../infrastructure/services/ProductLocalService';

interface ProductLocalState {
  productLocals: ProductoLocal[];
  loading: boolean;
  error: string | null;
}

const initialState: ProductLocalState = {
  productLocals: [],
  loading: false,
  error: null,
};

export const assignProductToLocal = createAsyncThunk(
  'productLocals/assignProductToLocal',
  async (request: AsignarProductoRequest, { rejectWithValue }) => {
    try {
      return await productLocalService.assignProductToLocal(request);
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Error al asignar producto');
    }
  }
);

export const updateStock = createAsyncThunk(
  'productLocals/updateStock',
  async (request: ActualizarStockRequest, { rejectWithValue }) => {
    try {
      return await productLocalService.updateStock(request);
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Error al actualizar stock');
    }
  }
);

export const fetchProductsByLocal = createAsyncThunk(
  'productLocals/fetchProductsByLocal',
  async (localId: number, { rejectWithValue }) => {
    try {
      return await productLocalService.getProductsByLocal(localId);
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Error al cargar productos del local');
    }
  }
);

export const increaseStock = createAsyncThunk(
  'productLocals/increaseStock',
  async ({ productoId, localId, cantidad }: { productoId: number; localId: number; cantidad: number }, { rejectWithValue, dispatch }) => {
    try {
      await productLocalService.increaseStock(productoId, localId, cantidad);
      // Refresh the products list after increasing stock
      dispatch(fetchProductsByLocal(localId));
      return { productoId, localId, cantidad };
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Error al aumentar stock');
    }
  }
);

const productLocalSlice = createSlice({
  name: 'productLocals',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearProductLocals: (state) => {
      state.productLocals = [];
    },
  },
  extraReducers: (builder) => {
    builder
      // Assign Product to Local
      .addCase(assignProductToLocal.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(assignProductToLocal.fulfilled, (state, action) => {
        state.loading = false;
        state.productLocals.push(action.payload);
      })
      .addCase(assignProductToLocal.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      // Update Stock
      .addCase(updateStock.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateStock.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.productLocals.findIndex(pl => pl.id === action.payload.id);
        if (index !== -1) {
          state.productLocals[index] = action.payload;
        }
      })
      .addCase(updateStock.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      // Fetch Products by Local
      .addCase(fetchProductsByLocal.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProductsByLocal.fulfilled, (state, action) => {
        state.loading = false;
        state.productLocals = action.payload;
      })
      .addCase(fetchProductsByLocal.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      // Increase Stock
      .addCase(increaseStock.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(increaseStock.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(increaseStock.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      });
  },
});

export const { clearError, clearProductLocals } = productLocalSlice.actions;
export default productLocalSlice.reducer;