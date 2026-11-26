
import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 15000
})

api.interceptors.request.use(
  (config) => {

    const token = sessionStorage.getItem('token')

    if (token && config.url !== '/auth/login') {
      config.headers.Authorization = `Bearer ${token}`
    }

    return config
  },
  (error) => Promise.reject(error)
)

api.interceptors.response.use(
  (response) => response,

  (error) => {

    const tokenActual = sessionStorage.getItem('token')

    if (
      error.response?.status === 401 &&
      error.config?.url !== '/auth/login' &&
      tokenActual &&
      error.config?.headers?.Authorization ===
        `Bearer ${tokenActual}`
    ) {

      sessionStorage.removeItem('token')
      sessionStorage.removeItem('usuario')
      sessionStorage.removeItem('expiracion')

      window.dispatchEvent(
        new Event('sesion-expirada')
      )
    }

    return Promise.reject(error)
  }
)

export default api
