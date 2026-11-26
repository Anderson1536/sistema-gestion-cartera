
import {
  BrowserRouter,
  Routes,
  Route,
  Navigate
} from 'react-router-dom'

import { AuthProvider } from './context/AuthContext'
import { useAuth } from './hooks/useAuth'
import ProtectedRoute from './routes/ProtectedRoute'
import MainLayout from './layouts/MainLayout'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import Clientes from './pages/Clientes'
import Cobros from './pages/Cobros'
import Usuarios from './pages/Usuarios'



function Inicio() {

  const { usuario } = useAuth()

  return usuario.rol === 'REPRESENTANTE'
    ? <Navigate to="/cobros" replace />
    : <Dashboard />
}

function AppRoutes() {

  const { autenticado } = useAuth()

  return (
    <Routes>

      <Route
        path="/login"
        element={
          autenticado
            ? <Navigate to="/" replace />
            : <Login />
        }
      />

      <Route element={<ProtectedRoute />}>

        <Route element={<MainLayout />}>

          <Route index element={<Inicio />} />

          <Route
            path="clientes"
            element={<Clientes />}
          />

          <Route
            path="cobros"
            element={<Cobros />}
          />

          <Route element={
            <ProtectedRoute roles={['ADMINISTRADOR']} />
          }>

            <Route
              path="usuarios"
              element={<Usuarios />}
            />

          </Route>

        </Route>

      </Route>

      <Route
        path="*"
        element={<Navigate to="/" replace />}
      />

    </Routes>
  )
}

export default function App() {

  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  )
}
