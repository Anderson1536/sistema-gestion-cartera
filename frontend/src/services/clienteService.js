
import api from './api'

export const listarClientes = async () => {
  const respuesta = await api.get('/clientes')
  return respuesta.data
}

export const crearCliente = async (cliente) => {
  const respuesta = await api.post('/clientes', cliente)
  return respuesta.data
}

export const actualizarCliente = async (id, cliente) => {
  const respuesta = await api.put(`/clientes/${id}`, cliente)
  return respuesta.data
}

export const cambiarEstadoCliente = async (id, activo) => {
  const respuesta = await api.patch(
    `/clientes/${id}/estado`,
    { activo }
  )
  return respuesta.data
}
