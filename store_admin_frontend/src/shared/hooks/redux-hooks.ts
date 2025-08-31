import { useDispatch, useSelector, TypedUseSelectorHook } from 'react-redux';
import type { RootStateT, AppDispatchT } from '../../infrastructure/store/types';

export const useAppDispatch = () => useDispatch<AppDispatchT>();
export const useAppSelector: TypedUseSelectorHook<RootStateT> = useSelector;