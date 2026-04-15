import { useEffect, useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { ArrowLeft, Phone, CalendarDays, ArrowRight } from 'lucide-react'
import api from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Skeleton } from '@/components/ui/skeleton'
import type { CustomerDto, TrainingSessionDto } from '@/types'

function formatDate(dt: string) {
  return new Date(dt).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' })
}
function formatTime(dt: string) {
  return new Date(dt).toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' })
}
function isUpcoming(dt: string) {
  return new Date(dt) > new Date()
}

export default function CustomerDetailPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [customer, setCustomer] = useState<CustomerDto | null>(null)
  const [sessions, setSessions] = useState<TrainingSessionDto[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const init = async () => {
      try {
        const [custRes, sessRes] = await Promise.all([
          api.get<CustomerDto>(`/trainers/customers/${id}`),
          api.get<TrainingSessionDto[]>('/sessions'),
        ])
        setCustomer(custRes.data)
        setSessions(sessRes.data.filter((s) => s.customerId === Number(id)))
      } catch (_) {
        navigate('/customers')
      } finally {
        setLoading(false)
      }
    }
    init()
  }, [id])

  if (loading || !customer) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-10 w-48" />
        <Skeleton className="h-32" />
        <Skeleton className="h-64" />
      </div>
    )
  }

  const upcomingSessions = sessions.filter((s) => isUpcoming(s.startDate))
  const pastSessions = sessions.filter((s) => !isUpcoming(s.startDate))

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <Button variant="outline" size="icon" onClick={() => navigate('/customers')}>
          <ArrowLeft className="h-4 w-4" />
        </Button>
        <h1 className="text-2xl font-bold text-slate-900">Customer Profile</h1>
      </div>

      {/* Profile card */}
      <Card>
        <CardContent className="p-6">
          <div className="flex items-center gap-5">
            <Avatar className="h-16 w-16">
              <AvatarFallback className="text-xl font-bold">
                {customer.firstName[0]}{customer.lastName[0]}
              </AvatarFallback>
            </Avatar>
            <div>
              <h2 className="text-xl font-bold text-slate-900">
                {customer.firstName} {customer.lastName}
              </h2>
              <p className="text-slate-500 flex items-center gap-1.5 mt-1">
                <Phone className="h-4 w-4" />
                {customer.phone}
              </p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Stats */}
      <div className="grid grid-cols-3 gap-4">
        <Card>
          <CardContent className="p-4 text-center">
            <p className="text-2xl font-bold text-slate-900">{sessions.length}</p>
            <p className="text-sm text-slate-500">Total Sessions</p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 text-center">
            <p className="text-2xl font-bold text-slate-900">{upcomingSessions.length}</p>
            <p className="text-sm text-slate-500">Upcoming</p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 text-center">
            <p className="text-2xl font-bold text-slate-900">{pastSessions.length}</p>
            <p className="text-sm text-slate-500">Completed</p>
          </CardContent>
        </Card>
      </div>

      {/* Sessions */}
      <Card>
        <CardHeader>
          <CardTitle>Training Sessions</CardTitle>
          <CardDescription>All sessions for this customer</CardDescription>
        </CardHeader>
        <CardContent>
          {sessions.length === 0 ? (
            <div className="text-center py-8">
              <CalendarDays className="h-10 w-10 mx-auto mb-3 text-slate-300" />
              <p className="text-slate-500">No sessions yet</p>
            </div>
          ) : (
            <div className="space-y-3">
              {sessions.map((session) => {
                const upcoming = isUpcoming(session.startDate)
                return (
                  <div key={session.id} className="flex items-center justify-between p-3 rounded-lg bg-slate-50 border border-slate-100">
                    <div className="flex items-center gap-3">
                      <CalendarDays className="h-4 w-4 text-slate-500" />
                      <div>
                        <p className="text-sm font-medium">Session #{session.id}</p>
                        <p className="text-xs text-slate-500">
                          {formatDate(session.startDate)} at {formatTime(session.startDate)} · {session.duration} min
                        </p>
                      </div>
                    </div>
                    <div className="flex items-center gap-2">
                      <Badge variant={upcoming ? 'success' : 'secondary'}>
                        {upcoming ? 'Upcoming' : 'Done'}
                      </Badge>
                      <Button variant="ghost" size="icon" asChild>
                        <Link to={`/sessions/${session.id}`}>
                          <ArrowRight className="h-4 w-4" />
                        </Link>
                      </Button>
                    </div>
                  </div>
                )
              })}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
