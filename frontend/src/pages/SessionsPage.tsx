import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { CalendarDays, Plus, Clock, ArrowRight } from 'lucide-react'
import { useAuth } from '@/context/AuthContext'
import api from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Skeleton } from '@/components/ui/skeleton'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter, DialogDescription } from '@/components/ui/dialog'
import { Label } from '@/components/ui/label'
import { Input } from '@/components/ui/input'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { toast } from '@/hooks/use-toast'
import type { TrainingSessionDto, CustomerDto, CreateTrainingSessionRequest } from '@/types'
import { AxiosError } from 'axios'

function formatDate(dt: string) {
  return new Date(dt).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' })
}
function formatTime(dt: string) {
  return new Date(dt).toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' })
}
function isUpcoming(dt: string) {
  return new Date(dt) > new Date()
}
function isActive(dt: string, duration: number) {
  const start = new Date(dt)
  const end = new Date(start.getTime() + duration * 60 * 1000)
  const now = new Date()
  return start <= now && now < end
}
function isPast(dt: string, duration: number) {
  const end = new Date(new Date(dt).getTime() + duration * 60 * 1000)
  return end <= new Date()
}

export default function SessionsPage() {
  const { userType } = useAuth()
  const [sessions, setSessions] = useState<TrainingSessionDto[]>([])
  const [customers, setCustomers] = useState<CustomerDto[]>([])
  const [loading, setLoading] = useState(true)
  const [createOpen, setCreateOpen] = useState(false)
  const [creating, setCreating] = useState(false)
  const [form, setForm] = useState<CreateTrainingSessionRequest>({
    customerId: 0,
    startDate: '',
    duration: 60,
  })

  const fetchSessions = async () => {
    try {
      const { data } = await api.get<TrainingSessionDto[]>('/sessions')
      setSessions(data)
    } catch (_) {}
  }

  useEffect(() => {
    const init = async () => {
      await fetchSessions()
      if (userType === 'TRAINER') {
        try {
          const { data } = await api.get<CustomerDto[]>('/trainers/me/customers')
          setCustomers(data)
        } catch (_) {}
      }
      setLoading(false)
    }
    init()
  }, [userType])

  const handleCreate = async () => {
    if (!form.customerId || !form.startDate || !form.duration) return
    setCreating(true)
    try {
      await api.post('/sessions', form)
      await fetchSessions()
      setCreateOpen(false)
      setForm({ customerId: 0, startDate: '', duration: 60 })
      toast({ title: 'Session created', description: 'Training session scheduled successfully.' })
    } catch (err) {
      const e = err as AxiosError<{ message: string }>
      toast({ title: 'Error', description: e.response?.data?.message || 'Failed to create session', variant: 'destructive' })
    } finally {
      setCreating(false)
    }
  }

  const upcoming = sessions.filter((s) => isUpcoming(s.startDate))
  const active = sessions.filter((s) => isActive(s.startDate, s.duration))
  const past = sessions.filter((s) => isPast(s.startDate, s.duration))

  if (loading) return <div className="space-y-4">{[1, 2, 3].map((i) => <Skeleton key={i} className="h-20" />)}</div>

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Training Sessions</h1>
          <p className="text-slate-500 mt-1">
            {sessions.length} total session{sessions.length !== 1 ? 's' : ''}
          </p>
        </div>
        {userType === 'TRAINER' && (
          <Button onClick={() => setCreateOpen(true)}>
            <Plus className="h-4 w-4 mr-2" />
            New Session
          </Button>
        )}
      </div>

      <Tabs defaultValue="upcoming">
        <TabsList>
          <TabsTrigger value="upcoming">Upcoming ({upcoming.length})</TabsTrigger>
          <TabsTrigger value="active">Active ({active.length})</TabsTrigger>
          <TabsTrigger value="past">Past ({past.length})</TabsTrigger>
          <TabsTrigger value="all">All ({sessions.length})</TabsTrigger>
        </TabsList>

        {(['upcoming', 'active', 'past', 'all'] as const).map((tab) => (
          <TabsContent key={tab} value={tab}>
            <SessionList
              sessions={tab === 'upcoming' ? upcoming : tab === 'active' ? active : tab === 'past' ? past : sessions}
              userType={userType}
              customers={customers}
            />
          </TabsContent>
        ))}
      </Tabs>

      {/* Create session dialog */}
      <Dialog open={createOpen} onOpenChange={setCreateOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Create Training Session</DialogTitle>
            <DialogDescription>Schedule a new session for one of your customers</DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-2">
            <div className="space-y-2">
              <Label>Customer</Label>
              <Select
                value={form.customerId ? String(form.customerId) : ''}
                onValueChange={(v) => setForm((p) => ({ ...p, customerId: Number(v) }))}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select a customer" />
                </SelectTrigger>
                <SelectContent>
                  {customers.map((c) => (
                    <SelectItem key={c.id} value={String(c.id)}>
                      {c.firstName} {c.lastName}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="startDate">Start Date & Time</Label>
              <Input
                id="startDate"
                type="datetime-local"
                value={form.startDate}
                onChange={(e) => setForm((p) => ({ ...p, startDate: e.target.value }))}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="duration">Duration (minutes)</Label>
              <Input
                id="duration"
                type="number"
                min="1"
                value={form.duration}
                onChange={(e) => setForm((p) => ({ ...p, duration: Number(e.target.value) }))}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setCreateOpen(false)}>Cancel</Button>
            <Button onClick={handleCreate} disabled={creating || !form.customerId || !form.startDate}>
              {creating ? 'Creating...' : 'Create Session'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}

function SessionList({ sessions, userType }: { sessions: TrainingSessionDto[]; userType: string | null }) {
  if (sessions.length === 0) {
    return (
      <Card>
        <CardContent className="py-12 text-center">
          <CalendarDays className="h-10 w-10 mx-auto mb-3 text-slate-300" />
          <p className="text-slate-500">No sessions here</p>
        </CardContent>
      </Card>
    )
  }
  return (
    <div className="space-y-3">
      {sessions.map((session) => {
        const upcoming = isUpcoming(session.startDate)
        const active = isActive(session.startDate, session.duration)
        return (
          <Card key={session.id} className="hover:shadow-md transition-shadow">
            <CardHeader className="pb-3">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="bg-slate-100 p-2.5 rounded-lg">
                    <CalendarDays className="h-5 w-5 text-slate-600" />
                  </div>
                  <div>
                    <CardTitle className="text-base">Session - {session.customerFirstName}</CardTitle>
                    <CardDescription className="flex items-center gap-1 mt-0.5">
                      <Clock className="h-3 w-3" />
                      {formatDate(session.startDate)} at {formatTime(session.startDate)} · {session.duration} min
                    </CardDescription>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  <Badge variant={upcoming ? 'success' : active ? 'default' : 'secondary'}>
                    {upcoming ? 'Upcoming' : active ? 'Active' : 'Completed'}
                  </Badge>
                  <Button variant="ghost" size="icon" asChild>
                    <Link to={`/sessions/${session.id}`}>
                      <ArrowRight className="h-4 w-4" />
                    </Link>
                  </Button>
                </div>
              </div>
            </CardHeader>
            {userType === 'TRAINER' && (
              <CardContent className="pt-0 pb-3">
                <p className="text-xs text-slate-400">{session.customerFirstName} {session.customerLastName}</p>
              </CardContent>
            )}
          </Card>
        )
      })}
    </div>
  )
}
