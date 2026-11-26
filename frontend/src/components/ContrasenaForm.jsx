
import { useState } from 'react'

export default function ContrasenaForm({
  usuario,
  onGuardar,
  onCancelar,
  guardando
}) {

  const [contrasena, setContrasena] = useState('')
  const [confirmacion, setConfirmacion] = useState('')
  const [error, setError] = useState('')

  const manejarEnvio = e => {
    e.preventDefault()
    setError('')

    if (contrasena !== confirmacion) {
      setError('Las contraseñas no coinciden.')
      return
    }

    onGuardar(contrasena)
  }

  return (
    <div className="card border-0 shadow-sm mb-4">

      <div className="card-body p-4">

        <h5 className="fw-bold">Cambiar contraseña</h5>

        <p className="text-secondary">
          Usuario: {usuario.nombre}
        </p>

        {error && (
          <div className="alert alert-danger">{error}</div>
        )}

        <form onSubmit={manejarEnvio}>

          <fieldset disabled={guardando}>

            <div className="row g-3">

              <div className="col-md-6">

                <label htmlFor="nueva" className="form-label">
                  Nueva contraseña *
                </label>

                <input
                  id="nueva"
                  type="password"
                  className="form-control"
                  value={contrasena}
                  onChange={e => setContrasena(e.target.value)}
                  minLength={8}
                  maxLength={72}
                  autoComplete="new-password"
                  required
                />

              </div>

              <div className="col-md-6">

                <label htmlFor="confirmar" className="form-label">
                  Confirmar contraseña *
                </label>

                <input
                  id="confirmar"
                  type="password"
                  className="form-control"
                  value={confirmacion}
                  onChange={e => setConfirmacion(e.target.value)}
                  minLength={8}
                  maxLength={72}
                  autoComplete="new-password"
                  required
                />

              </div>

            </div>

            <div className="d-flex gap-2 mt-4">

              <button type="submit" className="btn btn-primary">
                {guardando ? 'Guardando...' : 'Cambiar contraseña'}
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
