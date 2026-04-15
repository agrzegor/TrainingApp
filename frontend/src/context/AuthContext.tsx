import { createContext, useContext, useState, useEffect, type ReactNode } from 'react'
import type { UserType } from '@/types'

interface AuthContextValue {
  token: string | null
  userType: UserType | null
  login: (token: string, userType: UserType) => void
  logout: () => void
  isAuthenticated: boolean
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('token'))
  const [userType, setUserType] = useState<UserType | null>(
    () => localStorage.getItem('userType') as UserType | null
  )

  useEffect(() => {
    if (token) localStorage.setItem('token', token)
    else localStorage.removeItem('token')
  }, [token])

  useEffect(() => {
    if (userType) localStorage.setItem('userType', userType)
    else localStorage.removeItem('userType')
  }, [userType])

  const login = (newToken: string, newUserType: UserType) => {
    setToken(newToken)
    setUserType(newUserType)
  }

  const logout = () => {
    setToken(null)
    setUserType(null)
  }

  return (
    <AuthContext.Provider value={{ token, userType, login, logout, isAuthenticated: !!token }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider')
  return ctx
}
