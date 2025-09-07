import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { Producto } from '../../../domain/types';
import { productService } from '../../../infrastructure/services/ProductService';

interface ProductState {
  products: Producto[];
  categories: string[];
  selectedProduct: Producto | null;
  loading: boolean;
  error: string | null;
}

const initialState: ProductState = {
  products: [],
  categories: [],
  selectedProduct: null,
  loading: false,
  error: null,
};

export const fetchProducts = createAsyncThunk(
  'products/fetchProducts',
  async (_, { rejectWithValue }) => {
    try {
      return await productService.getProducts();
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Error al cargar productos');
    }
  }
);

export const createProduct = createAsyncThunk(
  'products/createProduct',
  async (product: Omit<Producto, 'id'>, { rejectWithValue }) => {
    try {
      return await productService.createProduct(product);
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Error al crear producto');
    }
  }
);

export const fetchCategories = createAsyncThunk(
  'products/fetchCategories',
  async (_, { rejectWithValue }) => {
    try {
      return await productService.getCategories();
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Error al cargar categorías');
    }
  }
);

const productSlice = createSlice({
  name: 'products',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    selectProduct: (state, action: PayloadAction<Producto>) => {
      state.selectedProduct = action.payload;
    },
    clearSelectedProduct: (state) => {
      state.selectedProduct = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch Products
      .addCase(fetchProducts.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProducts.fulfilled, (state, action) => {
        state.loading = false;
        state.products = action.payload;
      })
      .addCase(fetchProducts.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      // Create Product
      .addCase(createProduct.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createProduct.fulfilled, (state, action) => {
        state.loading = false;
        state.products.push(action.payload);
      })
      .addCase(createProduct.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      // Fetch Categories
      .addCase(fetchCategories.fulfilled, (state, action) => {
        state.categories = action.payload;
      });
  },
});

export const { clearError, selectProduct, clearSelectedProduct } = productSlice.actions;
export default productSlice.reducer;