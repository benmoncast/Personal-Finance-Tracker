import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { api } from '../lib/api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('finance_token'))
  const [user, setUser] = useState(() => {
    try { return JSON.parse(localStorage.getItem('finance_user')) } catch { return null }
  })
  const [loading, setLoading] = useState(Boolean(token) && !user)

  useEffect(() => {
    const unauthorized = () => { setToken(null); setUser(null) }
    window.addEventListener('finance:unauthorized', unauthorized)
    return () => window.removeEventListener('finance:unauthorized', unauthorized)
  }, [])

  useEffect(() => {
    if (!token || user) return
    api.get('/auth/me').then(({ data }) => {
      setUser(data); localStorage.setItem('finance_user', JSON.stringify(data))
    }).finally(() => setLoading(false))
  }, [token, user])

  const authenticate = async (path, values) => {
    const { data } = await api.post(path, values)
    localStorage.setItem('finance_token', data.token)
    localStorage.setItem('finance_user', JSON.stringify(data.user))
    setToken(data.token); setUser(data.user)
  }

  const logout = () => {
    localStorage.removeItem('finance_token'); localStorage.removeItem('finance_user')
    setToken(null); setUser(null)
  }

  const updateUser = (next) => { setUser(next); localStorage.setItem('finance_user', JSON.stringify(next)) }
  const value = useMemo(() => ({ user, token, loading, login: (v) => authenticate('/auth/login', v), register: (v) => authenticate('/auth/register', v), logout, updateUser }), [user, token, loading])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => useContext(AuthContext)
