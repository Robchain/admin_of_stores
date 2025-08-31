import { BrowserRouter ,Routes, Route, Navigate } from 'react-router-dom';
import { LoginPage } from '../../features';


export default function AppRoutes() {

  return (
       <BrowserRouter>
         <Routes>
      <Route path='/login' element={<LoginPage/>} />

      <Route path='/admin/products' element={<>Dashboard</>} />

      <Route path='/admin/store' element={<>LOGIN</>} />

      <Route path='/admin/dashboard_product' element={<>LOGIN</>} />
    </Routes>
       </BrowserRouter>
  )

}