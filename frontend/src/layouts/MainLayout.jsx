
import { useState } from 'react'
import { Outlet } from 'react-router-dom'
import Sidebar from '../components/Sidebar'
import { useAuth } from '../hooks/useAuth'
import '../styles/layout.css'

export default function MainLayout() {

  const { usuario } = useAuth()
  const [menuAbierto, setMenuAbierto] = useState(false)

  return (
    <div className="app-layout">

      {menuAbierto && (
        <div
          className="sidebar-overlay"
          onClick={() => setMenuAbierto(false)}
        />
      )}

      <div className={`sidebar-wrapper ${
        menuAbierto ? 'show' : ''
      }`}>
        <Sidebar onNavigate={() => setMenuAbierto(false)} />
      </div>

      <div className="main-area">

        <header className="topbar">

          <div className="d-flex align-items-center gap-3">

            <button
              className="btn btn-outline-secondary d-lg-none"
              aria-label="Abrir menú"
              aria-expanded={menuAbierto}
              onClick={() => setMenuAbierto(!menuAbierto)}
            >
              <i className="bi bi-list"></i>
            </button>

            <span className="fw-semibold">
              Panel administrativo
            </span>

          </div>

          <div className="d-flex align-items-center gap-2">

            <div className="user-avatar">
              <i className="bi bi-person-fill"></i>
            </div>

            <div className="d-none d-sm-block">
              <div className="fw-semibold small">
                {usuario.nombre}
              </div>
              <div className="text-secondary small">
                {usuario.rol}
              </div>
            </div>

          </div>

        </header>

        <main className="main-content">
          <Outlet />
        </main>

        <footer className="main-footer">
          Sistema Web de Gestión de Cartera
        </footer>

      </div>

    </div>
  )
}
