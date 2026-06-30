import axios from 'axios'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  timeout: 15000,
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('finance_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && !error.config?.url?.includes('/auth/login')) {
      localStorage.removeItem('finance_token')
      localStorage.removeItem('finance_user')
      window.dispatchEvent(new Event('finance:unauthorized'))
    }
    return Promise.reject(error)
  },
)

export function errorMessage(error) {
  return error.response?.data?.message || error.message || 'Something went wrong.'
}
