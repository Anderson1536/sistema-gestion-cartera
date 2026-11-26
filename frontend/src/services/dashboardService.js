import api from './api'

export const obtenerIndicadores = async () => {
  const respuesta = await api.get('/dashboard')
  return respuesta.data
}