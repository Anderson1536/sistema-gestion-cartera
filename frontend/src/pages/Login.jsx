
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import '../styles/login.css'

export default function Login() {

  const [correo, setCorreo] = useState('')
  const [contrasena, setContrasena] = useState('')
  const [mostrarContrasena, setMostrarContrasena] = useState(false)
  const [error, setError] = useState('')

  const { login, cargando } = useAuth()
  const navigate = useNavigate()

  const manejarEnvio = async (e) => {
    e.preventDefault()
    setError('')

    try {
      await login(correo, contrasena)
      navigate('/', { replace: true })

    } catch (error) {
      if (error.response?.status === 401) {
        setError('Correo o contraseña incorrectos.')
      } else {
        setError(
          error.response?.data?.mensaje ||
          'No fue posible iniciar sesión. Intente nuevamente.'
        )
      }
    }
  }

  return (
    <div className="login-container">

      <div className="login-card">

        <div className="text-center mb-4">

          <div className="login-logo">
            <i className="bi bi-wallet2"></i>
          </div>

          <h3 className="fw-bold mt-3">
            Gestión de Cartera
          </h3>

          <p className="text-secondary">
            Ingrese sus credenciales para continuar
          </p>

        </div>

        {error && (
          <div className="alert alert-danger" role="alert">
            {error}
          </div>
        )}

        <form onSubmit={manejarEnvio}>

          <div className="mb-3">

            <label htmlFor="correo" className="form-label">
              Correo electrónico
            </label>

            <div className="input-group">

              <span className="input-group-text">
                <i className="bi bi-envelope"></i>
              </span>

              <input
                id="correo"
                type="email"
                className="form-control"
                placeholder="usuario@empresa.com"
                value={correo}
                onChange={e => setCorreo(e.target.value)}
                autoComplete="username"
                required
              />

            </div>

          </div>

          <div className="mb-4">

            <label htmlFor="contrasena" className="form-label">
              Contraseña
            </label>

            <div className="input-group">

              <span className="input-group-text">
                <i className="bi bi-lock"></i>
              </span>

              <input
                id="contrasena"
                type={mostrarContrasena ? 'text' : 'password'}
                className="form-control"
                placeholder="Ingrese su contraseña"
                value={contrasena}
                onChange={e => setContrasena(e.target.value)}
                autoComplete="current-password"
                required
              />

              <button
                type="button"
                className="btn btn-outline-secondary"
                aria-label={mostrarContrasena
                  ? 'Ocultar contraseña'
                  : 'Mostrar contraseña'}
                onClick={() =>
                  setMostrarContrasena(!mostrarContrasena)
                }
              >
                <i className={mostrarContrasena
                  ? 'bi bi-eye-slash'
                  : 'bi bi-eye'}></i>
              </button>

            </div>

          </div>

          <button
            type="submit"
            className="btn btn-primary w-100 py-2"
            disabled={cargando}
          >
            {cargando ? 'Iniciando sesión...' : 'Iniciar sesión'}
          </button>

        </form>

        <div className="text-center text-secondary small mt-4">
          Sistema Web de Gestión de Cartera
        </div>

      </div>

    </div>
  )
}
