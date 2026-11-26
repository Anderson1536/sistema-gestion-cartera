
import { useState, useEffect } from 'react'
import { obtenerIndicadores } from '../services/dashboardService'
import { useAuth } from '../hooks/useAuth'
import StatCard from '../components/StatCard'

export default function Dashboard() {

  const { usuario } = useAuth()

  const [datos, setDatos] = useState(null)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {

    let activo = true

    obtenerIndicadores()
      .then(respuesta => {
        if (activo) setDatos(respuesta)
      })
      .catch(() => {
        if (activo) {
          setError('No fue posible cargar los indicadores.')
        }
      })
      .finally(() => {
        if (activo) setCargando(false)
      })

    return () => {
      activo = false
    }

  }, [])

  const moneda = valor =>
    new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      maximumFractionDigits: 0
    }).format(valor)

  if (cargando) {
    return (
      <div className="text-center py-5">
        <div className="spinner-border text-primary"
          role="status" />
        <p className="mt-3">Cargando indicadores...</p>
      </div>
    )
  }

  if (error) {
    return <div className="alert alert-danger">{error}</div>
  }

  const indicadores = [
    {
      titulo: 'Total de cobros',
      valor: datos.totalCobros,
      icono: 'bi-receipt',
      color: 'primary'
    },
    {
      titulo: 'Cobros pendientes',
      valor: datos.cobrosPendientes,
      icono: 'bi-clock-history',
      color: 'warning'
    },
    {
      titulo: 'Cobros validados',
      valor: datos.cobrosValidados,
      icono: 'bi-check-circle',
      color: 'success'
    },
    {
      titulo: 'Cobros rechazados',
      valor: datos.cobrosRechazados,
      icono: 'bi-x-circle',
      color: 'danger'
    }
  ]

  const porcentaje = cantidad =>
    datos.totalCobros > 0
      ? (cantidad / datos.totalCobros) * 100
      : 0

  return (
    <div>

      <h2 className="fw-bold">Dashboard</h2>

      <p className="text-secondary mb-4">
        Bienvenido, {usuario.nombre}.
        Consulte el estado general de la cartera.
      </p>

      <div className="row g-3 mb-4">

        {indicadores.map(indicador => (
          <div
            className="col-12 col-md-6 col-xl-3"
            key={indicador.titulo}
          >
            <StatCard {...indicador} />
          </div>
        ))}

      </div>

      <div className="row g-4">

        <div className="col-lg-5">

          <div className="card border-0 shadow-sm h-100">
            <div className="card-body p-4">

              <div className="text-success mb-3">
                <i className="bi bi-cash-coin fs-1"></i>
              </div>

              <p className="text-secondary">
                Valor total de cobros validados
              </p>

              <h2 className="fw-bold text-success">
                {moneda(datos.valorTotalValidado)}
              </h2>

              <small className="text-secondary">
                Valores expresados en pesos colombianos.
              </small>

            </div>
          </div>

        </div>

        <div className="col-lg-7">

          <div className="card border-0 shadow-sm h-100">

            <div className="card-body p-4">

              <h5 className="fw-bold mb-4">
                Distribución de cobros por estado
              </h5>

              {indicadores.slice(1).map(item => (

                <div className="mb-3" key={item.titulo}>

                  <div className="d-flex
                    justify-content-between mb-2">

                    <span>{item.titulo}</span>
                    <strong>{item.valor}</strong>

                  </div>

                  <div
                    className="progress"
                    style={{ height: '10px' }}
                  >
                    <div
                      className={`progress-bar bg-${item.color}`}
                      role="progressbar"
                      aria-label={item.titulo}
                      aria-valuenow={porcentaje(item.valor)}
                      aria-valuemin={0}
                      aria-valuemax={100}
                      style={{
                        width: `${porcentaje(item.valor)}%`
                      }}
                    />
                  </div>

                </div>

              ))}

            </div>

          </div>

        </div>

      </div>

    </div>
  )
}
