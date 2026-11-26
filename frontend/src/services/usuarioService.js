
import api from './api'

export const listarUsuarios = async () => {
  const respuesta = await api.get('/usuarios')
  return respuesta.data
}

export const listarRoles = async () => {
  const respuesta = await api.get('/roles')
  return respuesta.data
}

export const crearUsuario = async (usuario) => {
  const respuesta = await api.post('/usuarios', usuario)
  return respuesta.data
}

export const actualizarUsuario = async (id, usuario) => {
  const respuesta = await api.put(`/usuarios/${id}`, usuario)
  return respuesta.data
}

export const cambiarEstadoUsuario = async (id, activo) => {
  const respuesta = await api.patch(
    `/usuarios/${id}/estado`,
    { activo }
  )
  return respuesta.data
}

export const cambiarContrasenaUsuario = async (
  id, nuevaContrasena
) => {
  await api.patch(
    `/usuarios/${id}/contrasena`,
    { nuevaContrasena }
  )
}
