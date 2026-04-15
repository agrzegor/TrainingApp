import { Link, useLocation, useNavigate } from 'react-router-dom'
import { Dumbbell, LayoutDashboard, Users, CalendarDays, LogOut, User } from 'lucide-react'
import { useAuth } from '@/context/AuthContext'
import { Button } from '@/components/ui/button'
import { Separator } from '@/components/ui/separator'
import { cn } from '@/lib/utils'
import type { ReactNode } from 'react'

const trainerNav = [
  { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/sessions', label: 'Sessions', icon: CalendarDays },
  { to: '/customers', label: 'Customers', icon: Users },
  { to: '/profile', label: 'Profile', icon: User },
]

const customerNav = [
  { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/sessions', label: 'My Sessions', icon: CalendarDays },
  { to: '/profile', label: 'Profile', icon: User },
]

export default function AppLayout({ children }: { children: ReactNode }) {
  const { userType, logout } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()
  const navItems = userType === 'TRAINER' ? trainerNav : customerNav

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className="flex min-h-screen bg-slate-50">
      {/* Sidebar */}
      <aside className="w-64 bg-white border-r border-slate-200 flex flex-col">
        <div className="p-6 flex items-center gap-3">
          <div className="bg-slate-900 text-white p-2 rounded-lg">
            <Dumbbell className="h-5 w-5" />
          </div>
          <div>
            <p className="font-bold text-slate-900 text-sm">TrainingApp</p>
            <p className="text-xs text-slate-500 capitalize">{userType?.toLowerCase()} portal</p>
          </div>
        </div>
        <Separator />
        <nav className="flex-1 p-4 space-y-1">
          {navItems.map(({ to, label, icon: Icon }) => (
            <Link
              key={to}
              to={to}
              className={cn(
                'flex items-center gap-3 px-3 py-2 rounded-md text-sm font-medium transition-colors',
                location.pathname === to
                  ? 'bg-slate-900 text-white'
                  : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
              )}
            >
              <Icon className="h-4 w-4" />
              {label}
            </Link>
          ))}
          <Separator className="my-2" />
          <Button variant="ghost" className="w-full justify-start gap-3 text-slate-600 px-3 py-2 h-auto font-medium text-sm" onClick={handleLogout}>
            <LogOut className="h-4 w-4" />
            Logout
          </Button>
        </nav>
      </aside>

      {/* Main content */}
      <main className="flex-1 overflow-auto">
        <div className="p-8">{children}</div>
      </main>
    </div>
  )
}
