
import { createContext, useState, useEffect } from 'react'
import { iniciarSesion } from '../services/authService'

export const AuthContext = createContext(null)

export function AuthProvider({ children }) {

    const [usuario, setUsuario] = useState(() => {
        try {
            const datos = sessionStorage.getItem('usuario')
            const token = sessionStorage.getItem('token')
            const expiracion = Number(sessionStorage.getItem('expiracion'))

            return datos && token && expiracion > Date.now()
                ? JSON.parse(datos)
                : null
        } catch {
            return null
        }
    })

    const [cargando, setCargando] = useState(false)

    const cerrarSesion = () => {
        sessionStorage.removeItem('token')
        sessionStorage.removeItem('usuario')
        sessionStorage.removeItem('expiracion')
        setUsuario(null)
    }

    useEffect(() => {

        const manejarSesionExpirada = () => {
            setUsuario(null)
        }

        window.addEventListener(
            'sesion-expirada',
            manejarSesionExpirada
        )

        return () => {
            window.removeEventListener(
                'sesion-expirada',
                manejarSesionExpirada
            )
        }

    }, [])

    useEffect(() => {
        if (!usuario) {
            sessionStorage.removeItem('token')
            sessionStorage.removeItem('usuario')
            sessionStorage.removeItem('expiracion')
            return
        }

        const tiempoRestante =
            Number(sessionStorage.getItem('expiracion')) - Date.now()

        const temporizador = setTimeout(
            cerrarSesion,
            Math.max(0, tiempoRestante)
        )

        return () => clearTimeout(temporizador)
    }, [usuario])

    const login = async (correo, contrasena) => {
        setCargando(true)

        try {
            const respuesta = await iniciarSesion(correo, contrasena)

            const datosUsuario = {
                id: respuesta.usuarioId,
                nombre: respuesta.nombre,
                correo: respuesta.correo,
                rol: respuesta.rol
            }

            sessionStorage.setItem('token', respuesta.token)

            sessionStorage.setItem(
                'usuario',
                JSON.stringify(datosUsuario)
            )

            sessionStorage.setItem(
                'expiracion',
                String(Date.now() + respuesta.expiraEn * 1000)
            )

            setUsuario(datosUsuario)

            return datosUsuario

        } finally {
            setCargando(false)
        }
    }

    return (
        <AuthContext.Provider value={{
            usuario,
            cargando,
            autenticado: !!usuario,
            login,
            cerrarSesion
        }}>
            {children}
        </AuthContext.Provider>
    )
}
