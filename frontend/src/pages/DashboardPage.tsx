import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { CalendarDays, Users, Dumbbell, TrendingUp, ArrowRight } from 'lucide-react'
import { useAuth } from '@/context/AuthContext'
import api from '@/lib/api'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Skeleton } from '@/components/ui/skeleton'
import type { TrainingSessionDto, CustomerDto } from '@/types'

function formatDate(dt: string) {
  return new Date(dt).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' })
}

function formatTime(dt: string) {
  return new Date(dt).toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' })
}

function isUpcoming(dt: string) {
  return new Date(dt) > new Date()
}

export default function DashboardPage() {
  const { userType } = useAuth()
  const [sessions, setSessions] = useState<TrainingSessionDto[]>([])
  const [customers, setCustomers] = useState<CustomerDto[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [sessionsRes] = await Promise.all([api.get<TrainingSessionDto[]>('/sessions')])
        setSessions(sessionsRes.data)
        if (userType === 'TRAINER') {
          const custRes = await api.get<CustomerDto[]>('/trainers/me/customers')
          setCustomers(custRes.data)
        }
      } catch (_) {
        // silently fail
      } finally {
        setLoading(false)
      }
    }
    fetchData()
  }, [userType])

  const upcomingSessions = sessions.filter((s) => isUpcoming(s.startDate)).slice(0, 3)
  const pastSessions = sessions.filter((s) => !isUpcoming(s.startDate))

  if (loading) return <DashboardSkeleton />

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Dashboard</h1>
        <p className="text-slate-500 mt-1">
          {userType === 'TRAINER' ? 'Manage your clients and training sessions' : 'Track your training progress'}
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <StatCard
          title="Total Sessions"
          value={sessions.length}
          icon={<CalendarDays className="h-5 w-5 text-blue-600" />}
          bg="bg-blue-50"
        />
        {userType === 'TRAINER' && (
          <StatCard
            title="Customers"
            value={customers.length}
            icon={<Users className="h-5 w-5 text-green-600" />}
            bg="bg-green-50"
          />
        )}
        <StatCard
          title="Upcoming Sessions"
          value={upcomingSessions.length}
          icon={<TrendingUp className="h-5 w-5 text-purple-600" />}
          bg="bg-purple-50"
        />
        <StatCard
          title="Completed Sessions"
          value={pastSessions.length}
          icon={<Dumbbell className="h-5 w-5 text-orange-600" />}
          bg="bg-orange-50"
        />
      </div>

      {/* Upcoming Sessions */}
      <Card>
        <CardHeader className="flex flex-row items-center justify-between">
          <div>
            <CardTitle>Upcoming Sessions</CardTitle>
            <CardDescription>Your next scheduled training sessions</CardDescription>
          </div>
          <Button variant="ghost" size="sm" asChild>
            <Link to="/sessions" className="flex items-center gap-1">
              View all <ArrowRight className="h-4 w-4" />
            </Link>
          </Button>
        </CardHeader>
        <CardContent>
          {upcomingSessions.length === 0 ? (
            <div className="text-center py-8 text-slate-500">
              <CalendarDays className="h-10 w-10 mx-auto mb-3 text-slate-300" />
              <p>No upcoming sessions</p>
              {userType === 'TRAINER' && (
                <Button className="mt-4" size="sm" asChild>
                  <Link to="/sessions">Schedule a session</Link>
                </Button>
              )}
            </div>
          ) : (
            <div className="space-y-3">
              {upcomingSessions.map((session) => (
                <Link key={session.id} to={`/sessions/${session.id}`}>
                  <div className="flex items-center justify-between p-3 rounded-lg border border-slate-100 hover:border-slate-300 hover:bg-slate-50 transition-colors">
                    <div className="flex items-center gap-3">
                      <div className="bg-slate-100 p-2 rounded-lg">
                        <CalendarDays className="h-4 w-4 text-slate-600" />
                      </div>
                      <div>
                        <p className="text-sm font-medium text-slate-900">Session - {session.customerFirstName} {session.customerLastName}</p>
                        <p className="text-xs text-slate-500">
                          {formatDate(session.startDate)} at {formatTime(session.startDate)} · {session.duration} min
                        </p>
                      </div>
                    </div>
                    <Badge variant="success">Upcoming</Badge>
                  </div>
                </Link>
              ))}
            </div>
          )}
        </CardContent>
      </Card>

      {/* Trainer-only: customer list preview */}
      {userType === 'TRAINER' && (
        <Card>
          <CardHeader className="flex flex-row items-center justify-between">
            <div>
              <CardTitle>Your Customers</CardTitle>
              <CardDescription>Clients assigned to you</CardDescription>
            </div>
            <Button variant="ghost" size="sm" asChild>
              <Link to="/customers" className="flex items-center gap-1">
                View all <ArrowRight className="h-4 w-4" />
              </Link>
            </Button>
          </CardHeader>
          <CardContent>
            {customers.length === 0 ? (
              <div className="text-center py-8 text-slate-500">
                <Users className="h-10 w-10 mx-auto mb-3 text-slate-300" />
                <p>No customers yet</p>
              </div>
            ) : (
              <div className="space-y-2">
                {customers.slice(0, 4).map((customer) => (
                  <Link key={customer.id} to={`/customers/${customer.id}`}>
                    <div className="flex items-center justify-between p-3 rounded-lg border border-slate-100 hover:border-slate-300 hover:bg-slate-50 transition-colors">
                      <div className="flex items-center gap-3">
                        <div className="bg-slate-900 text-white rounded-full h-8 w-8 flex items-center justify-center text-xs font-semibold">
                          {customer.firstName[0]}{customer.lastName[0]}
                        </div>
                        <div>
                          <p className="text-sm font-medium text-slate-900">
                            {customer.firstName} {customer.lastName}
                          </p>
                          <p className="text-xs text-slate-500">{customer.phone}</p>
                        </div>
                      </div>
                      <ArrowRight className="h-4 w-4 text-slate-400" />
                    </div>
                  </Link>
                ))}
              </div>
            )}
          </CardContent>
        </Card>
      )}
    </div>
  )
}

function StatCard({ title, value, icon, bg }: { title: string; value: number; icon: React.ReactNode; bg: string }) {
  return (
    <Card>
      <CardContent className="p-6">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm text-slate-500">{title}</p>
            <p className="text-3xl font-bold text-slate-900 mt-1">{value}</p>
          </div>
          <div className={`${bg} p-3 rounded-xl`}>{icon}</div>
        </div>
      </CardContent>
    </Card>
  )
}

function DashboardSkeleton() {
  return (
    <div className="space-y-8">
      <Skeleton className="h-8 w-48" />
      <div className="grid grid-cols-3 gap-4">
        {[1, 2, 3].map((i) => <Skeleton key={i} className="h-28" />)}
      </div>
      <Skeleton className="h-64" />
    </div>
  )
}
