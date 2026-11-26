export default function StatCard({
  titulo, valor, icono, color
}) {

  return (
    <div className="card border-0 shadow-sm h-100">

      <div className="card-body p-4">

        <div className="d-flex align-items-center gap-3">

          <div className={`rounded-3 p-3 bg-${color}
            bg-opacity-10 text-${color}`}>
            <i className={`bi ${icono} fs-4`}></i>
          </div>

          <div>
            <div className="text-secondary small mb-1">
              {titulo}
            </div>

            <h4 className="fw-bold mb-0">
              {valor}
            </h4>
          </div>

        </div>

      </div>

    </div>
  )
}