
import { useState, useEffect } from 'react'
import { useAuth } from '../hooks/useAuth'
import CobroForm from '../components/CobroForm'
import { listarClientes } from '../services/clienteService'

import {
  listarCobros,
  listarCobrosPorUsuario,
  crearCobro,
  actualizarCobro,
  cambiarEstadoCobro
} from '../services/cobroService'

const coloresEstado = {
  PENDIENTE: 'warning',
  VALIDADO: 'success',
  RECHAZADO: 'danger'
}

const moneda = valor =>
  new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    minimumFractionDigits: 0,
    maximumFractionDigits: 2
  }).format(valor)

export default function Cobros() {

  const { usuario } = useAuth()

  const esRepresentante = usuario.rol === 'REPRESENTANTE'

  const puedeRegistrar = [
    'ADMINISTRADOR',
    'REPRESENTANTE'
  ].includes(usuario.rol)

  const puedeValidar = [
    'ADMINISTRADOR',
    'ANALISTA'
  ].includes(usuario.rol)

  const [cobros, setCobros] = useState([])
  const [clientes, setClientes] = useState([])
  const [busqueda, setBusqueda] = useState('')
  const [filtroEstado, setFiltroEstado] = useState('TODOS')
  const [filtroMetodo, setFiltroMetodo] = useState('TODOS')
  const [fechaInicio, setFechaInicio] = useState('')
  const [fechaFin, setFechaFin] = useState('')

  const [cargando, setCargando] = useState(true)
  const [guardando, setGuardando] = useState(false)
  const [mostrarFormulario, setMostrarFormulario] = useState(false)
  const [cobroEditado, setCobroEditado] = useState(null)
  const [error, setError] = useState('')
  const [mensaje, setMensaje] = useState('')

  const cargarDatos = async () => {

    setCargando(true)

    try {

      const [datosCobros, datosClientes] = await Promise.all([
        esRepresentante
          ? listarCobrosPorUsuario(usuario.id)
          : listarCobros(),
        listarClientes()
      ])

      setCobros(datosCobros)
      setClientes(datosClientes)

    } catch (error) {

      setError(
        error.response?.data?.mensaje ||
        'No fue posible consultar los cobros.'
      )

    } finally {
      setCargando(false)
    }
  }

  useEffect(() => {
    cargarDatos()
  }, [usuario.id, usuario.rol])

  const limpiarMensajes = () => {
    setError('')
    setMensaje('')
  }

  const nuevoCobro = () => {
    limpiarMensajes()
    setCobroEditado(null)
    setMostrarFormulario(true)
  }

  const editarCobro = cobro => {
    limpiarMensajes()
    setCobroEditado(cobro)
    setMostrarFormulario(true)
  }

  const cancelarFormulario = () => {
    setMostrarFormulario(false)
    setCobroEditado(null)
    limpiarMensajes()
  }

  const guardarCobro = async datos => {

    limpiarMensajes()
    setGuardando(true)

    try {

      if (cobroEditado) {
        await actualizarCobro(cobroEditado.id, datos)
        setMensaje('Cobro corregido correctamente.')
      } else {
        await crearCobro(datos)
        setMensaje('Cobro registrado correctamente.')
      }

      setMostrarFormulario(false)
      setCobroEditado(null)

      await cargarDatos()

    } catch (error) {

      setError(
        error.response?.data?.mensaje ||
        'No fue posible guardar el cobro.'
      )

    } finally {
      setGuardando(false)
    }
  }

  const modificarEstado = async (cobro, estado) => {

    const accion = estado === 'VALIDADO'
      ? 'validar'
      : 'rechazar'

    if (!window.confirm(
      `¿Desea ${accion} el cobro #${cobro.id}?`
    )) return

    limpiarMensajes()
    setGuardando(true)

    try {

      await cambiarEstadoCobro(cobro.id, estado)

      setMensaje(
        estado === 'VALIDADO'
          ? 'Cobro validado correctamente.'
          : 'Cobro rechazado correctamente.'
      )

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

  const limpiarFiltros = () => {
    setBusqueda('')
    setFiltroEstado('TODOS')
    setFiltroMetodo('TODOS')
    setFechaInicio('')
    setFechaFin('')
  }

  const cobrosFiltrados = cobros.filter(cobro => {

    const texto = busqueda.trim().toLowerCase()

    const coincideBusqueda = [
      cobro.clienteNombre,
      cobro.clienteDocumento,
      cobro.referencia,
      cobro.usuarioNombre
    ].some(valor =>
      String(valor ?? '').toLowerCase().includes(texto)
    )

    return coincideBusqueda
      && (filtroEstado === 'TODOS'
        || cobro.estado === filtroEstado)
      && (filtroMetodo === 'TODOS'
        || cobro.metodoPago === filtroMetodo)
      && (!fechaInicio || cobro.fechaPago >= fechaInicio)
      && (!fechaFin || cobro.fechaPago <= fechaFin)
  })

  const totalFiltrado = cobrosFiltrados.reduce(
    (total, cobro) => total + Number(cobro.valor), 0
  )

  return (
    <div>

      <div className="d-flex flex-wrap
        justify-content-between align-items-center gap-3 mb-4">

        <div>
          <h2 className="fw-bold">
            {esRepresentante ? 'Mis cobros' : 'Cobros'}
          </h2>

          <p className="text-secondary mb-0">
            Registro, consulta y seguimiento de cobros.
          </p>
        </div>

        {puedeRegistrar && (
          <button
            className="btn btn-primary"
            onClick={nuevoCobro}
            disabled={guardando || cargando}
          >
            <i className="bi bi-plus-circle me-2"></i>
            Nuevo cobro
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

      {mostrarFormulario && (
        <CobroForm
          key={cobroEditado?.id ?? 'nuevo'}
          cobro={cobroEditado}
          clientes={clientes}
          onGuardar={guardarCobro}
          onCancelar={cancelarFormulario}
          guardando={guardando}
        />
      )}

      <div className="card border-0 shadow-sm mb-4">
        <div className="card-body p-4">

          <h5 className="fw-bold mb-3">
            <i className="bi bi-funnel me-2"></i>
            Filtros de búsqueda
          </h5>

          <div className="row g-3">

            <div className="col-md-6">
              <label className="form-label" htmlFor="busqueda">
                Buscar cobro
              </label>

              <input
                id="busqueda"
                className="form-control"
                placeholder="Cliente, documento o referencia"
                value={busqueda}
                onChange={e => setBusqueda(e.target.value)}
              />
            </div>

            <div className="col-md-3">

              <label className="form-label" htmlFor="estado">
                Estado
              </label>

              <select
                id="estado"
                className="form-select"
                value={filtroEstado}
                onChange={e => setFiltroEstado(e.target.value)}
              >
                <option value="TODOS">Todos</option>
                <option value="PENDIENTE">Pendientes</option>
                <option value="VALIDADO">Validados</option>
                <option value="RECHAZADO">Rechazados</option>
              </select>

            </div>

            <div className="col-md-3">

              <label className="form-label" htmlFor="metodo">
                Método de pago
              </label>

              <select
                id="metodo"
                className="form-select"
                value={filtroMetodo}
                onChange={e => setFiltroMetodo(e.target.value)}
              >
                <option value="TODOS">Todos</option>
                <option value="EFECTIVO">Efectivo</option>
                <option value="TRANSFERENCIA">Transferencia</option>
                <option value="CONSIGNACION">Consignación</option>
                <option value="CHEQUE">Cheque</option>
              </select>

            </div>

            <div className="col-md-4">

              <label className="form-label" htmlFor="fechaInicio">
                Fecha desde
              </label>

              <input
                id="fechaInicio"
                type="date"
                className="form-control"
                value={fechaInicio}
                onChange={e => setFechaInicio(e.target.value)}
              />

            </div>

            <div className="col-md-4">

              <label className="form-label" htmlFor="fechaFin">
                Fecha hasta
              </label>

              <input
                id="fechaFin"
                type="date"
                className="form-control"
                min={fechaInicio || undefined}
                value={fechaFin}
                onChange={e => setFechaFin(e.target.value)}
              />

            </div>

            <div className="col-md-4 d-flex align-items-end">

              <button
                className="btn btn-outline-secondary w-100"
                onClick={limpiarFiltros}
              >
                Limpiar filtros
              </button>

            </div>

          </div>

          {fechaInicio && fechaFin && fechaInicio > fechaFin && (
            <div className="text-danger small mt-2">
              La fecha inicial no puede superar la fecha final.
            </div>
          )}

        </div>
      </div>

      <div className="card border-0 shadow-sm">

        <div className="card-body p-4">

          {cargando ? (

            <div className="text-center py-5">
              <div className="spinner-border text-primary"
                role="status" />
              <p className="mt-3">Cargando cobros...</p>
            </div>

          ) : (

            <>

              <div className="table-responsive">

                <table className="table table-hover align-middle">

                  <thead className="table-light">
                    <tr>
                      <th>ID</th>
                      <th>Cliente</th>
                      <th>Valor</th>
                      <th>Fecha de pago</th>
                      <th>Método</th>
                      <th>Referencia</th>
                      {!esRepresentante && <th>Registrado por</th>}
                      <th>Estado</th>
                      <th>Acciones</th>
                    </tr>
                  </thead>

                  <tbody>

                    {cobrosFiltrados.map(cobro => (

                      <tr key={cobro.id}>

                        <td>{cobro.id}</td>

                        <td className="fw-semibold">
                          {cobro.clienteNombre}
                        </td>

                        <td className="text-nowrap">
                          {moneda(cobro.valor)}
                        </td>

                        <td>{cobro.fechaPago}</td>
                        <td>{cobro.metodoPago}</td>
                        <td>{cobro.referencia || '—'}</td>

                        {!esRepresentante && (
                          <td>{cobro.usuarioNombre}</td>
                        )}

                        <td>
                          <span className={`badge text-bg-${
                            coloresEstado[cobro.estado] || 'secondary'
                          }`}>
                            {cobro.estado}
                          </span>
                        </td>

                        <td>

                          <div className="d-flex gap-2">

                            {cobro.estado !== 'VALIDADO' && (
                              <button
                                className="btn btn-sm btn-outline-primary"
                                title="Corregir cobro"
                                aria-label="Corregir cobro"
                                disabled={guardando}
                                onClick={() => editarCobro(cobro)}
                              >
                                <i className="bi bi-pencil"></i>
                              </button>
                            )}

                            {puedeValidar &&
                              cobro.estado === 'PENDIENTE' && (
                              <>

                                <button
                                  className="btn btn-sm btn-outline-success"
                                  title="Validar cobro"
                                  aria-label="Validar cobro"
                                  disabled={guardando}
                                  onClick={() =>
                                    modificarEstado(cobro, 'VALIDADO')
                                  }
                                >
                                  <i className="bi bi-check-lg"></i>
                                </button>

                                <button
                                  className="btn btn-sm btn-outline-danger"
                                  title="Rechazar cobro"
                                  aria-label="Rechazar cobro"
                                  disabled={guardando}
                                  onClick={() =>
                                    modificarEstado(cobro, 'RECHAZADO')
                                  }
                                >
                                  <i className="bi bi-x-lg"></i>
                                </button>

                              </>
                            )}

                            {cobro.estado === 'VALIDADO' && (
                              <i className="bi bi-lock text-secondary"
                                title="Cobro validado" />
                            )}

                          </div>

                        </td>

                      </tr>

                    ))}

                    {cobrosFiltrados.length === 0 && (
                      <tr>
                        <td
                          colSpan={esRepresentante ? 8 : 9}
                          className="text-center text-secondary py-4"
                        >
                          No se encontraron cobros.
                        </td>
                      </tr>
                    )}

                  </tbody>

                </table>

              </div>

              <div className="d-flex flex-wrap
                justify-content-between align-items-center gap-2">

                <small className="text-secondary">
                  Mostrando {cobrosFiltrados.length} de
                  {' '}{cobros.length} cobros.
                </small>

                <strong>
                  Total de registros filtrados:
                  {' '}{moneda(totalFiltrado)}
                </strong>

              </div>

            </>

          )}

        </div>

      </div>

    </div>
  )
}
