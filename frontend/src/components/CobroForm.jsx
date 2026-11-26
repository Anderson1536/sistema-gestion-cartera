
import { useState } from 'react'

export default function CobroForm({
  cobro,
  clientes,
  onGuardar,
  onCancelar,
  guardando
}) {

  const fechaActual = new Intl.DateTimeFormat('en-CA', {
    timeZone: 'America/Bogota',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  }).format(new Date())

  const [datos, setDatos] = useState({
    clienteId: cobro?.clienteId ?? '',
    valor: cobro?.valor ?? '',
    fechaPago: cobro?.fechaPago ?? fechaActual,
    metodoPago: cobro?.metodoPago ?? '',
    referencia: cobro?.referencia ?? '',
    observaciones: cobro?.observaciones ?? ''
  })

  const manejarCambio = (e) => {
    const { name, value } = e.target

    setDatos(anterior => ({
      ...anterior,
      [name]: value
    }))
  }

  const manejarEnvio = (e) => {
    e.preventDefault()

    onGuardar({
      ...datos,
      clienteId: Number(datos.clienteId)
    })
  }

  return (
    <div className="card border-0 shadow-sm mb-4">
      <div className="card-body p-4">

        <h5 className="fw-bold mb-4">
          {cobro ? 'Corregir cobro' : 'Registrar cobro'}
        </h5>

        <form onSubmit={manejarEnvio}>

          <fieldset disabled={guardando}>

            <div className="row g-3">

              <div className="col-md-6">

                <label htmlFor="clienteId" className="form-label">
                  Cliente *
                </label>

                <select
                  id="clienteId"
                  name="clienteId"
                  className="form-select"
                  value={datos.clienteId}
                  onChange={manejarCambio}
                  required
                >
                  <option value="">Seleccione un cliente</option>

                  {clientes.filter(cliente => cliente.activo)
                    .map(cliente => (
                      <option key={cliente.id} value={cliente.id}>
                        {cliente.documento} - {cliente.nombre}
                      </option>
                    ))}
                </select>

              </div>

              <div className="col-md-6">

                <label htmlFor="valor" className="form-label">
                  Valor del cobro (COP) *
                </label>

                <input
                  id="valor"
                  name="valor"
                  type="number"
                  className="form-control"
                  min="0.01"
                  max="9999999999999.99"
                  step="0.01"
                  value={datos.valor}
                  onChange={manejarCambio}
                  required
                />

              </div>

              <div className="col-md-6">

                <label htmlFor="fechaPago" className="form-label">
                  Fecha de pago *
                </label>

                <input
                  id="fechaPago"
                  name="fechaPago"
                  type="date"
                  className="form-control"
                  max={fechaActual}
                  value={datos.fechaPago}
                  onChange={manejarCambio}
                  required
                />

              </div>

              <div className="col-md-6">

                <label htmlFor="metodoPago" className="form-label">
                  Método de pago *
                </label>

                <select
                  id="metodoPago"
                  name="metodoPago"
                  className="form-select"
                  value={datos.metodoPago}
                  onChange={manejarCambio}
                  required
                >
                  <option value="">Seleccione un método</option>
                  <option value="EFECTIVO">Efectivo</option>
                  <option value="TRANSFERENCIA">Transferencia</option>
                  <option value="CONSIGNACION">Consignación</option>
                  <option value="CHEQUE">Cheque</option>
                </select>

              </div>

              <div className="col-12">

                <label htmlFor="referencia" className="form-label">
                  Referencia de pago
                </label>

                <input
                  id="referencia"
                  name="referencia"
                  className="form-control"
                  maxLength={100}
                  value={datos.referencia}
                  onChange={manejarCambio}
                  placeholder="Número de comprobante o referencia"
                />

              </div>

              <div className="col-12">

                <label htmlFor="observaciones" className="form-label">
                  Observaciones
                </label>

                <textarea
                  id="observaciones"
                  name="observaciones"
                  className="form-control"
                  rows={3}
                  maxLength={500}
                  value={datos.observaciones}
                  onChange={manejarCambio}
                />

              </div>

            </div>

            <div className="alert alert-info mt-4">
              <i className="bi bi-info-circle me-2"></i>
              El cobro quedará pendiente de validación.
            </div>

            <div className="d-flex gap-2">

              <button type="submit" className="btn btn-primary">
                <i className="bi bi-floppy me-2"></i>
                {guardando ? 'Guardando...' : 'Guardar cobro'}
              </button>

              <button
                type="button"
                className="btn btn-outline-secondary"
                onClick={onCancelar}
              >
                Cancelar
              </button>

            </div>

          </fieldset>

        </form>

      </div>
    </div>
  )
}
