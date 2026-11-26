import api from './api'

export const iniciarSesion = async (correo, contrasena) => {
  const respuesta = await api.post('/auth/login', {
    correo,
    contrasena
  })

  return respuesta.data
}