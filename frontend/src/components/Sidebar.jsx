
import { NavLink } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'

const opciones = [
  {
    nombre: 'Dashboard',
    ruta: '/',
    icono: 'bi-speedometer2',
    roles: ['ADMINISTRADOR', 'ANALISTA']
  },
  {
    nombre: 'Clientes',
    ruta: '/clientes',
    icono: 'bi-people',
    roles: ['ADMINISTRADOR', 'ANALISTA', 'REPRESENTANTE']
  },
  {
    nombre: 'Cobros',
    ruta: '/cobros',
    icono: 'bi-cash-stack',
    roles: ['ADMINISTRADOR', 'ANALISTA', 'REPRESENTANTE']
  },
  {
    nombre: 'Usuarios',
    ruta: '/usuarios',
    icono: 'bi-person-gear',
    roles: ['ADMINISTRADOR']
  }
]

export default function Sidebar({ onNavigate }) {

  const { usuario, cerrarSesion } = useAuth()

  return (
    <aside className="sidebar">

      <div className="sidebar-brand">
        <i className="bi bi-wallet2 fs-3"></i>
        <div>
          <strong>Gestión de Cartera</strong>
          <small>Sistema administrativo</small>
        </div>
      </div>

      <div className="sidebar-label">
        MENÚ PRINCIPAL
      </div>

      <nav className="sidebar-nav">

        {opciones
          .filter(opcion => opcion.roles.includes(usuario.rol))
          .map(opcion => (
            <NavLink
              key={opcion.ruta}
              to={opcion.ruta}
              end={opcion.ruta === '/'}
              onClick={onNavigate}
              className={({ isActive }) =>
                `sidebar-link ${isActive ? 'active' : ''}`
              }
            >
              <i className={`bi ${opcion.icono}`}></i>
              {opcion.nombre}
            </NavLink>
          ))}

      </nav>

      <div className="sidebar-footer">

        <button
          className="sidebar-link w-100 border-0"
          onClick={cerrarSesion}
        >
          <i className="bi bi-box-arrow-left"></i>
          Cerrar sesión
        </button>

      </div>

    </aside>
  )
}
