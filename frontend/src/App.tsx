import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from '@/context/AuthContext'
import ProtectedRoute from '@/components/ProtectedRoute'
import { Toaster } from '@/components/ui/toaster'
import LoginPage from '@/pages/LoginPage'
import SignupPage from '@/pages/SignupPage'
import DashboardPage from '@/pages/DashboardPage'
import SessionsPage from '@/pages/SessionsPage'
import SessionDetailPage from '@/pages/SessionDetailPage'
import CustomersPage from '@/pages/CustomersPage'
import CustomerDetailPage from '@/pages/CustomerDetailPage'
import ProfilePage from '@/pages/ProfilePage'

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
          <Route
            path="/dashboard"
            element={<ProtectedRoute><DashboardPage /></ProtectedRoute>}
          />
          <Route
            path="/sessions"
            element={<ProtectedRoute><SessionsPage /></ProtectedRoute>}
          />
          <Route
            path="/sessions/:id"
            element={<ProtectedRoute><SessionDetailPage /></ProtectedRoute>}
          />
          <Route
            path="/customers"
            element={<ProtectedRoute><CustomersPage /></ProtectedRoute>}
          />
          <Route
            path="/customers/:id"
            element={<ProtectedRoute><CustomerDetailPage /></ProtectedRoute>}
          />
          <Route
            path="/profile"
            element={<ProtectedRoute><ProfilePage /></ProtectedRoute>}
          />
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
        <Toaster />
      </BrowserRouter>
    </AuthProvider>
  )
}
