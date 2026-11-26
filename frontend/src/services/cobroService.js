
import api from './api'

export const listarCobros = async () => {
  const respuesta = await api.get('/cobros')
  return respuesta.data
}

export const listarCobrosPorUsuario = async (usuarioId) => {
  const respuesta = await api.get(`/cobros/usuario/${usuarioId}`)
  return respuesta.data
}

export const crearCobro = async (cobro) => {
  const respuesta = await api.post('/cobros', cobro)
  return respuesta.data
}

export const actualizarCobro = async (id, cobro) => {
  const respuesta = await api.put(`/cobros/${id}`, cobro)
  return respuesta.data
}

export const cambiarEstadoCobro = async (id, estado) => {
  const respuesta = await api.patch(
    `/cobros/${id}/estado`,
    { estado }
  )

  return respuesta.data
}
