
import { useState } from 'react'

export default function ClienteForm({
  cliente,
  onGuardar,
  onCancelar,
  guardando
}) {

  const [datos, setDatos] = useState({
    documento: cliente?.documento ?? '',
    nombre: cliente?.nombre ?? '',
    correo: cliente?.correo ?? '',
    telefono: cliente?.telefono ?? '',
    direccion: cliente?.direccion ?? '',
    ciudad: cliente?.ciudad ?? ''
  })

  const campos = [
    {
      nombre: 'documento',
      etiqueta: 'Documento / NIT',
      obligatorio: true,
      max: 20
    },
    {
      nombre: 'nombre',
      etiqueta: 'Nombre o razón social',
      obligatorio: true,
      max: 150
    },
    {
      nombre: 'correo',
      etiqueta: 'Correo electrónico',
      tipo: 'email',
      max: 150
    },
    {
      nombre: 'telefono',
      etiqueta: 'Teléfono',
      tipo: 'tel',
      max: 20
    },
    {
      nombre: 'direccion',
      etiqueta: 'Dirección',
      max: 200
    },
    {
      nombre: 'ciudad',
      etiqueta: 'Ciudad',
      max: 100
    }
  ]

  const manejarCambio = (e) => {
    const { name, value } = e.target

    setDatos(anterior => ({
      ...anterior,
      [name]: value
    }))
  }

  const manejarEnvio = (e) => {
    e.preventDefault()
    onGuardar(datos)
  }

  return (
    <div className="card border-0 shadow-sm mb-4">

      <div className="card-body p-4">

        <h5 className="fw-bold mb-4">
          {cliente ? 'Editar cliente' : 'Registrar cliente'}
        </h5>

        <form onSubmit={manejarEnvio}>

          <div className="row g-3">

            {campos.map(campo => (
              <div
                className="col-12 col-md-6"
                key={campo.nombre}
              >
                <label
                  htmlFor={campo.nombre}
                  className="form-label"
                >
                  {campo.etiqueta}
                  {campo.obligatorio && ' *'}
                </label>

                <input
                  id={campo.nombre}
                  name={campo.nombre}
                  type={campo.tipo || 'text'}
                  className="form-control"
                  value={datos[campo.nombre]}
                  onChange={manejarCambio}
                  required={campo.obligatorio}
                  maxLength={campo.max}
                  disabled={guardando}
                />
              </div>
            ))}

          </div>

          <div className="d-flex gap-2 mt-4">

            <button
              type="submit"
              className="btn btn-primary"
              disabled={guardando}
            >
              <i className="bi bi-floppy me-2"></i>
              {guardando ? 'Guardando...' : 'Guardar cliente'}
            </button>

            <button
              type="button"
              className="btn btn-outline-secondary"
              onClick={onCancelar}
              disabled={guardando}
            >
              Cancelar
            </button>

          </div>

        </form>

      </div>

    </div>
  )
}
