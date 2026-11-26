
import { useState, useEffect } from 'react'
import { useAuth } from '../hooks/useAuth'
import UsuarioForm from '../components/UsuarioForm'
import ContrasenaForm from '../components/ContrasenaForm'

import {
  listarUsuarios,
  listarRoles,
  crearUsuario,
  actualizarUsuario,
  cambiarEstadoUsuario,
  cambiarContrasenaUsuario
} from '../services/usuarioService'

export default function Usuarios() {

  const { usuario: usuarioActual } = useAuth()

  const [usuarios, setUsuarios] = useState([])
  const [roles, setRoles] = useState([])
  const [busqueda, setBusqueda] = useState('')
  const [filtroRol, setFiltroRol] = useState('TODOS')
  const [filtroEstado, setFiltroEstado] = useState('TODOS')

  const [cargando, setCargando] = useState(true)
  const [guardando, setGuardando] = useState(false)
  const [formulario, setFormulario] = useState(null)
  const [seleccionado, setSeleccionado] = useState(null)
  const [error, setError] = useState('')
  const [mensaje, setMensaje] = useState('')

  const cargarDatos = async () => {

    setCargando(true)

    try {

      const [datosUsuarios, datosRoles] = await Promise.all([
        listarUsuarios(),
        listarRoles()
      ])

      setUsuarios(datosUsuarios)
      setRoles(datosRoles)

    } catch (error) {

      setError(
        error.response?.data?.mensaje ||
        'No fue posible consultar los usuarios.'
      )

    } finally {
      setCargando(false)
    }
  }

  useEffect(() => {
    cargarDatos()
  }, [])

  const limpiarMensajes = () => {
    setError('')
    setMensaje('')
  }

  const abrirFormulario = (tipo, usuario = null) => {
    limpiarMensajes()
    setSeleccionado(usuario)
    setFormulario(tipo)
  }

  const cerrarFormulario = () => {
    setFormulario(null)
    setSeleccionado(null)
  }

  const guardarUsuario = async datos => {

    limpiarMensajes()
    setGuardando(true)

    try {

      if (seleccionado) {

        await actualizarUsuario(seleccionado.id, datos)
        setMensaje('Usuario actualizado correctamente.')

      } else {

        await crearUsuario(datos)
        setMensaje('Usuario registrado correctamente.')

      }

      cerrarFormulario()
      await cargarDatos()

    } catch (error) {

      setError(
        error.response?.data?.mensaje ||
        'No fue posible guardar el usuario.'
      )

    } finally {
      setGuardando(false)
    }
  }

  const guardarContrasena = async contrasena => {

    limpiarMensajes()
    setGuardando(true)

    try {

      await cambiarContrasenaUsuario(
        seleccionado.id,
        contrasena
      )

      setMensaje('Contraseña actualizada correctamente.')
      cerrarFormulario()

    } catch (error) {

      setError(
        error.response?.data?.mensaje ||
        'No fue posible cambiar la contraseña.'
      )

    } finally {
      setGuardando(false)
    }
  }

  const modificarEstado = async usuario => {

    const accion = usuario.activo ? 'desactivar' : 'activar'

    if (!window.confirm(
      `¿Desea ${accion} al usuario ${usuario.nombre}?`
    )) return

    limpiarMensajes()
    setGuardando(true)

    try {

      await cambiarEstadoUsuario(usuario.id, !usuario.activo)

      setMensaje('Estado actualizado correctamente.')

      await cargarDatos()

    } catch (error) {

      setError(
        error.response?.data?.mensaje ||
        'No fue posible modificar el estado.'
      )

    } finally {
      setGuardando(false)
    }
  }

  const usuariosFiltrados = usuarios.filter(usuario => {

    const texto = busqueda.trim().toLowerCase()

    const coincideBusqueda = [
      usuario.nombre,
      usuario.correo
    ].some(valor =>
      String(valor ?? '').toLowerCase().includes(texto)
    )

    const coincideRol =
      filtroRol === 'TODOS' ||
      usuario.rolNombre === filtroRol

    const coincideEstado =
      filtroEstado === 'TODOS' ||
      (filtroEstado === 'ACTIVOS' && usuario.activo) ||
      (filtroEstado === 'INACTIVOS' && !usuario.activo)

    return coincideBusqueda
      && coincideRol
      && coincideEstado
  })

  return (
    <div>

      <div className="d-flex flex-wrap
        justify-content-between align-items-center gap-3 mb-4">

        <div>
          <h2 className="fw-bold">Usuarios</h2>

          <p className="text-secondary mb-0">
            Administración de usuarios y roles del sistema.
          </p>
        </div>

        <button
          className="btn btn-primary"
          disabled={guardando || cargando}
          onClick={() => abrirFormulario('usuario')}
        >
          <i className="bi bi-person-plus me-2"></i>
          Nuevo usuario
        </button>

      </div>

      {error && (
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      )}

      {mensaje && (
        <div className="alert alert-success" role="status">
          {mensaje}
        </div>
      )}

      {formulario === 'usuario' && (

        <UsuarioForm
          key={seleccionado?.id ?? 'nuevo'}
          usuario={seleccionado}
          roles={roles}
          onGuardar={guardarUsuario}
          onCancelar={cerrarFormulario}
          guardando={guardando}
        />

      )}

      {formulario === 'contrasena' && (

        <ContrasenaForm
          key={seleccionado.id}
          usuario={seleccionado}
          onGuardar={guardarContrasena}
          onCancelar={cerrarFormulario}
          guardando={guardando}
        />

      )}

      <div className="card border-0 shadow-sm">

        <div className="card-body p-4">

          <div className="row g-3 mb-4">

            <div className="col-md-6">

              <input
                className="form-control"
                placeholder="Buscar por nombre o correo"
                aria-label="Buscar usuarios"
                value={busqueda}
                onChange={e => setBusqueda(e.target.value)}
              />

            </div>

            <div className="col-md-3">

              <select
                className="form-select"
                aria-label="Filtrar por rol"
                value={filtroRol}
                onChange={e => setFiltroRol(e.target.value)}
              >
                <option value="TODOS">Todos los roles</option>

                {roles.map(rol => (
                  <option key={rol.id} value={rol.nombre}>
                    {rol.nombre}
                  </option>
                ))}

              </select>

            </div>

            <div className="col-md-3">

              <select
                className="form-select"
                aria-label="Filtrar por estado"
                value={filtroEstado}
                onChange={e => setFiltroEstado(e.target.value)}
              >
                <option value="TODOS">Todos los estados</option>
                <option value="ACTIVOS">Activos</option>
                <option value="INACTIVOS">Inactivos</option>
              </select>

            </div>

          </div>

          {cargando ? (

            <div className="text-center py-5">
              <div className="spinner-border text-primary"
                role="status" />
              <p className="mt-3">Cargando usuarios...</p>
            </div>

          ) : (

            <div className="table-responsive">

              <table className="table table-hover align-middle">

                <thead className="table-light">

                  <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Correo</th>
                    <th>Rol</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                  </tr>

                </thead>

                <tbody>

                  {usuariosFiltrados.map(usuario => (

                    <tr key={usuario.id}>

                      <td>{usuario.id}</td>

                      <td className="fw-semibold">
                        {usuario.nombre}
                      </td>

                      <td>{usuario.correo}</td>

                      <td>
                        <span className="badge text-bg-primary">
                          {usuario.rolNombre}
                        </span>
                      </td>

                      <td>
                        <span className={`badge ${
                          usuario.activo
                            ? 'text-bg-success'
                            : 'text-bg-secondary'
                        }`}>
                          {usuario.activo ? 'Activo' : 'Inactivo'}
                        </span>
                      </td>

                      <td>

                        <div className="d-flex gap-2">

                          <button
                            className="btn btn-sm btn-outline-primary"
                            title="Editar usuario"
                            aria-label="Editar usuario"
                            disabled={
                              guardando ||
                              usuario.id === usuarioActual.id
                            }
                            onClick={() =>
                              abrirFormulario('usuario', usuario)
                            }
                          >
                            <i className="bi bi-pencil"></i>
                          </button>

                          <button
                            className="btn btn-sm btn-outline-warning"
                            title="Cambiar contraseña"
                            aria-label="Cambiar contraseña"
                            disabled={guardando}
                            onClick={() =>
                              abrirFormulario('contrasena', usuario)
                            }
                          >
                            <i className="bi bi-key"></i>
                          </button>

                          <button
                            className={`btn btn-sm ${
                              usuario.activo
                                ? 'btn-outline-danger'
                                : 'btn-outline-success'
                            }`}
                            title={usuario.activo
                              ? 'Desactivar'
                              : 'Activar'}
                            aria-label={usuario.activo
                              ? 'Desactivar usuario'
                              : 'Activar usuario'}
                            disabled={
                              guardando ||
                              usuario.id === usuarioActual.id
                            }
                            onClick={() => modificarEstado(usuario)}
                          >
                            <i className={`bi ${
                              usuario.activo
                                ? 'bi-person-x'
                                : 'bi-person-check'
                            }`}></i>
                          </button>

                        </div>

                      </td>

                    </tr>

                  ))}

                  {usuariosFiltrados.length === 0 && (

                    <tr>
                      <td
                        colSpan={6}
                        className="text-center text-secondary py-4"
                      >
                        No se encontraron usuarios.
                      </td>
                    </tr>

                  )}

                </tbody>

              </table>

              <small className="text-secondary">
                Mostrando {usuariosFiltrados.length} de
                {' '}{usuarios.length} usuarios.
              </small>

            </div>

          )}

        </div>

      </div>

    </div>
  )
}
