
import { useState } from 'react'

export default function UsuarioForm({
  usuario,
  roles,
  onGuardar,
  onCancelar,
  guardando
}) {

  const [datos, setDatos] = useState({
    nombre: usuario?.nombre ?? '',
    correo: usuario?.correo ?? '',
    contrasena: '',
    rolId: usuario?.rolId ?? ''
  })

  const manejarCambio = e => {
    const { name, value } = e.target

    setDatos(anterior => ({
      ...anterior,
      [name]: value
    }))
  }

  const manejarEnvio = e => {
    e.preventDefault()

    const solicitud = {
      nombre: datos.nombre,
      correo: datos.correo,
      rolId: Number(datos.rolId)
    }

    if (!usuario) {
      solicitud.contrasena = datos.contrasena
    }

    onGuardar(solicitud)
  }

  return (
    <div className="card border-0 shadow-sm mb-4">

      <div className="card-body p-4">

        <h5 className="fw-bold mb-4">
          {usuario ? 'Editar usuario' : 'Registrar usuario'}
        </h5>

        <form onSubmit={manejarEnvio}>

          <fieldset disabled={guardando}>

            <div className="row g-3">

              <div className="col-md-6">

                <label htmlFor="nombre" className="form-label">
                  Nombre completo *
                </label>

                <input
                  id="nombre"
                  name="nombre"
                  className="form-control"
                  value={datos.nombre}
                  onChange={manejarCambio}
                  maxLength={100}
                  required
                />

              </div>

              <div className="col-md-6">

                <label htmlFor="correo" className="form-label">
                  Correo electrónico *
                </label>

                <input
                  id="correo"
                  name="correo"
                  type="email"
                  className="form-control"
                  value={datos.correo}
                  onChange={manejarCambio}
                  maxLength={150}
                  required
                />

              </div>

              {!usuario && (

                <div className="col-md-6">

                  <label
                    htmlFor="contrasena"
                    className="form-label"
                  >
                    Contraseña *
                  </label>

                  <input
                    id="contrasena"
                    name="contrasena"
                    type="password"
                    className="form-control"
                    value={datos.contrasena}
                    onChange={manejarCambio}
                    minLength={8}
                    maxLength={72}
                    autoComplete="new-password"
                    required
                  />

                  <small className="text-secondary">
                    Mínimo 8 caracteres.
                  </small>

                </div>

              )}

              <div className="col-md-6">

                <label htmlFor="rolId" className="form-label">
                  Rol *
                </label>

                <select
                  id="rolId"
                  name="rolId"
                  className="form-select"
                  value={datos.rolId}
                  onChange={manejarCambio}
                  required
                >
                  <option value="">Seleccione un rol</option>

                  {roles.map(rol => (
                    <option key={rol.id} value={rol.id}>
                      {rol.nombre}
                    </option>
                  ))}

                </select>

              </div>

            </div>

            <div className="d-flex gap-2 mt-4">

              <button type="submit" className="btn btn-primary">
                <i className="bi bi-floppy me-2"></i>
                {guardando ? 'Guardando...' : 'Guardar usuario'}
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
