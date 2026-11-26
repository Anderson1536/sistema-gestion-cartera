
import { useState, useEffect } from 'react'
import { useAuth } from '../hooks/useAuth'
import ClienteForm from '../components/ClienteForm'

import {
  listarClientes,
  crearCliente,
  actualizarCliente,
  cambiarEstadoCliente
} from '../services/clienteService'

export default function Clientes() {

  const { usuario } = useAuth()

  const puedeGestionar = [
    'ADMINISTRADOR',
    'ANALISTA'
  ].includes(usuario.rol)

  const [clientes, setClientes] = useState([])
  const [busqueda, setBusqueda] = useState('')
  const [filtroEstado, setFiltroEstado] = useState('TODOS')
  const [cargando, setCargando] = useState(true)
  const [guardando, setGuardando] = useState(false)
  const [mostrarFormulario, setMostrarFormulario] = useState(false)
  const [clienteEditado, setClienteEditado] = useState(null)
  const [error, setError] = useState('')
  const [mensaje, setMensaje] = useState('')

  const cargarClientes = async () => {

    setCargando(true)

    try {
      const datos = await listarClientes()
      setClientes(datos)

    } catch (error) {
      setError(
        error.response?.data?.mensaje ||
        'No fue posible consultar los clientes.'
      )

    } finally {
      setCargando(false)
    }
  }

  useEffect(() => {
    cargarClientes()
  }, [])

  const limpiarMensajes = () => {
    setError('')
    setMensaje('')
  }

  const nuevoCliente = () => {
    limpiarMensajes()
    setClienteEditado(null)
    setMostrarFormulario(true)
  }

  const editarCliente = (cliente) => {
    limpiarMensajes()
    setClienteEditado(cliente)
    setMostrarFormulario(true)
  }

  const cancelarFormulario = () => {
    setMostrarFormulario(false)
    setClienteEditado(null)
    limpiarMensajes()
  }

  const guardarCliente = async (datos) => {

    limpiarMensajes()
    setGuardando(true)

    try {

      if (clienteEditado) {
        await actualizarCliente(clienteEditado.id, datos)
        setMensaje('Cliente actualizado correctamente.')

      } else {
        await crearCliente(datos)
        setMensaje('Cliente registrado correctamente.')
      }

      setMostrarFormulario(false)
      setClienteEditado(null)

      await cargarClientes()

    } catch (error) {

      setError(
        error.response?.data?.mensaje ||
        'No fue posible guardar el cliente.'
      )

    } finally {
      setGuardando(false)
    }
  }

  const modificarEstado = async (cliente) => {

    const accion = cliente.activo ? 'desactivar' : 'activar'

    const confirmar = window.confirm(
      `¿Desea ${accion} al cliente ${cliente.nombre}?`
    )

    if (!confirmar) return

    limpiarMensajes()
    setGuardando(true)

    try {

      await cambiarEstadoCliente(
        cliente.id,
        !cliente.activo
      )

      setMensaje('Estado actualizado correctamente.')

      await cargarClientes()

    } catch (error) {

      setError(
        error.response?.data?.mensaje ||
        'No fue posible actualizar el estado.'
      )

    } finally {
      setGuardando(false)
    }
  }

  const clientesFiltrados = clientes.filter(cliente => {

    const texto = busqueda.trim().toLowerCase()

    const coincideBusqueda = [
      cliente.nombre,
      cliente.documento,
      cliente.correo,
      cliente.ciudad
    ].some(valor =>
      String(valor ?? '').toLowerCase().includes(texto)
    )

    const coincideEstado =
      filtroEstado === 'TODOS' ||
      (filtroEstado === 'ACTIVOS' && cliente.activo) ||
      (filtroEstado === 'INACTIVOS' && !cliente.activo)

    return coincideBusqueda && coincideEstado
  })

  return (
    <div>

      <div className="d-flex flex-wrap
        justify-content-between align-items-center
        gap-3 mb-4">

        <div>
          <h2 className="fw-bold">Clientes</h2>
          <p className="text-secondary mb-0">
            Consulta y gestión de clientes registrados.
          </p>
        </div>

        {puedeGestionar && (
          <button
            className="btn btn-primary"
            onClick={nuevoCliente}
            disabled={guardando}
          >
            <i className="bi bi-person-plus me-2"></i>
            Nuevo cliente
          </button>
        )}

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

      {mostrarFormulario && puedeGestionar && (
        <ClienteForm
          key={clienteEditado?.id ?? 'nuevo'}
          cliente={clienteEditado}
          onGuardar={guardarCliente}
          onCancelar={cancelarFormulario}
          guardando={guardando}
        />
      )}

      <div className="card border-0 shadow-sm">

        <div className="card-body p-4">

          <div className="row g-3 mb-4">

            <div className="col-md-8">

              <div className="input-group">

                <span className="input-group-text">
                  <i className="bi bi-search"></i>
                </span>

                <input
                  className="form-control"
                  placeholder="Buscar por nombre, documento o ciudad"
                  aria-label="Buscar clientes"
                  value={busqueda}
                  onChange={e => setBusqueda(e.target.value)}
                />

              </div>

            </div>

            <div className="col-md-4">

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
              <div
                className="spinner-border text-primary"
                role="status"
              />
              <p className="mt-3">Cargando clientes...</p>
            </div>

          ) : (

            <div className="table-responsive">

              <table className="table table-hover align-middle">

                <thead className="table-light">
                  <tr>
                    <th>Documento</th>
                    <th>Nombre</th>
                    <th>Correo</th>
                    <th>Teléfono</th>
                    <th>Ciudad</th>
                    <th>Estado</th>
                    {puedeGestionar && <th>Acciones</th>}
                  </tr>
                </thead>

                <tbody>

                  {clientesFiltrados.map(cliente => (

                    <tr key={cliente.id}>

                      <td>{cliente.documento}</td>
                      <td className="fw-semibold">
                        {cliente.nombre}
                      </td>
                      <td>{cliente.correo || '—'}</td>
                      <td>{cliente.telefono || '—'}</td>
                      <td>{cliente.ciudad || '—'}</td>

                      <td>
                        <span className={`badge ${
                          cliente.activo
                            ? 'text-bg-success'
                            : 'text-bg-secondary'
                        }`}>
                          {cliente.activo ? 'Activo' : 'Inactivo'}
                        </span>
                      </td>

                      {puedeGestionar && (

                        <td>

                          <div className="d-flex gap-2">

                            <button
                              className="btn btn-sm btn-outline-primary"
                              title="Editar cliente"
                              aria-label="Editar cliente"
                              disabled={guardando}
                              onClick={() => editarCliente(cliente)}
                            >
                              <i className="bi bi-pencil"></i>
                            </button>

                            <button
                              className={`btn btn-sm ${
                                cliente.activo
                                  ? 'btn-outline-danger'
                                  : 'btn-outline-success'
                              }`}
                              title={cliente.activo
                                ? 'Desactivar'
                                : 'Activar'}
                              aria-label={cliente.activo
                                ? 'Desactivar cliente'
                                : 'Activar cliente'}
                              disabled={guardando}
                              onClick={() => modificarEstado(cliente)}
                            >
                              <i className={`bi ${
                                cliente.activo
                                  ? 'bi-person-x'
                                  : 'bi-person-check'
                              }`}></i>
                            </button>

                          </div>

                        </td>

                      )}

                    </tr>

                  ))}

                  {clientesFiltrados.length === 0 && (
                    <tr>
                      <td
                        colSpan={puedeGestionar ? 7 : 6}
                        className="text-center text-secondary py-4"
                      >
                        No se encontraron clientes.
                      </td>
                    </tr>
                  )}

                </tbody>

              </table>

              <small className="text-secondary">
                Mostrando {clientesFiltrados.length} de
                {' '}{clientes.length} clientes.
              </small>

            </div>

          )}

        </div>

      </div>

    </div>
  )
}
