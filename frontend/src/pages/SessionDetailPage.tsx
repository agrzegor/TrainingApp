import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { ArrowLeft, Plus, Search, Dumbbell, Pencil, ExternalLink, Trash2, Trash } from 'lucide-react'
import { useAuth } from '@/context/AuthContext'
import api from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Skeleton } from '@/components/ui/skeleton'
import { Separator } from '@/components/ui/separator'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter, DialogDescription } from '@/components/ui/dialog'
import { toast } from '@/hooks/use-toast'
import type { TrainingSessionDto, SessionExerciseDto, ExerciseDto, CreateSessionExercisesRequest } from '@/types'
import { AxiosError } from 'axios'

function formatDate(dt: string) {
  return new Date(dt).toLocaleDateString('en-GB', { day: 'numeric', month: 'long', year: 'numeric' })
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

export default function SessionDetailPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { userType } = useAuth()

  const [session, setSession] = useState<TrainingSessionDto | null>(null)
  const [exercises, setExercises] = useState<SessionExerciseDto[]>([])
  const [loading, setLoading] = useState(true)

  // Add exercise dialog
  const [addOpen, setAddOpen] = useState(false)
  const [searchQuery, setSearchQuery] = useState('')
  const [searchResults, setSearchResults] = useState<ExerciseDto[]>([])
  const [searching, setSearching] = useState(false)
  const [selectedExercise, setSelectedExercise] = useState<ExerciseDto | null>(null)
  const [exerciseForm, setExerciseForm] = useState<Omit<CreateSessionExercisesRequest, 'exerciseId'>>({
    reps: 10,
    series: 3,
    weight: 0,
  })
  const [addingExercise, setAddingExercise] = useState(false)

  // Exercise detail dialog
  const [detailExercise, setDetailExercise] = useState<ExerciseDto | null>(null)
  const [detailOpen, setDetailOpen] = useState(false)
  const [loadingDetail, setLoadingDetail] = useState(false)

  // Delete session
  const [deleteSessionOpen, setDeleteSessionOpen] = useState(false)
  const [deletingSession, setDeletingSession] = useState(false)

  // Delete exercise
  const [deleteConfirmId, setDeleteConfirmId] = useState<number | null>(null)
  const [deletingExercise, setDeletingExercise] = useState(false)

  // Edit session dialog
  const [editOpen, setEditOpen] = useState(false)
  const [editForm, setEditForm] = useState({ startDate: '', duration: 0 })
  const [saving, setSaving] = useState(false)

  const fetchExercises = async () => {
    const { data } = await api.get<SessionExerciseDto[]>(`/sessions/${id}/exercises`)
    setExercises(data)
  }

  useEffect(() => {
    const init = async () => {
      try {
        const [sessRes] = await Promise.all([api.get<TrainingSessionDto[]>('/sessions')])
        const s = sessRes.data.find((s) => s.id === Number(id))
        if (!s) { navigate('/sessions'); return }
        setSession(s)
        setEditForm({ startDate: s.startDate.slice(0, 16), duration: s.duration })
        await fetchExercises()
      } catch (_) {
        navigate('/sessions')
      } finally {
        setLoading(false)
      }
    }
    init()
  }, [id])

  const handleSearch = async () => {
    if (!searchQuery.trim()) return
    setSearching(true)
    try {
      const { data } = await api.get<ExerciseDto[]>(`/exercises/search?search=${encodeURIComponent(searchQuery)}`)
      setSearchResults(data)
    } catch (_) {
      toast({ title: 'Error', description: 'Failed to search exercises', variant: 'destructive' })
    } finally {
      setSearching(false)
    }
  }

  const handleAddExercise = async () => {
    if (!selectedExercise) return
    setAddingExercise(true)
    try {
      await api.post(`/sessions/${id}/exercises`, { exerciseId: selectedExercise.id, ...exerciseForm })
      await fetchExercises()
      setAddOpen(false)
      setSelectedExercise(null)
      setSearchResults([])
      setSearchQuery('')
      setExerciseForm({ reps: 10, series: 3, weight: 0 })
      toast({ title: 'Exercise added', description: `${selectedExercise.name} added to session.` })
    } catch (err) {
      const e = err as AxiosError<{ message: string }>
      toast({ title: 'Error', description: e.response?.data?.message || 'Failed to add exercise', variant: 'destructive' })
    } finally {
      setAddingExercise(false)
    }
  }

  const handleViewExercise = async (exerciseId: number) => {
    setLoadingDetail(true)
    setDetailOpen(true)
    try {
      const { data } = await api.get<ExerciseDto>(`/exercises/${exerciseId}`)
      setDetailExercise(data)
    } catch (_) {
      toast({ title: 'Error', description: 'Failed to load exercise details', variant: 'destructive' })
      setDetailOpen(false)
    } finally {
      setLoadingDetail(false)
    }
  }

  const handleDeleteSession = async () => {
    setDeletingSession(true)
    try {
      await api.delete(`/sessions/${id}`)
      toast({ title: 'Session deleted', description: 'Training session has been deleted.' })
      navigate('/sessions')
    } catch (err) {
      const e = err as AxiosError<{ message: string }>
      toast({ title: 'Error', description: e.response?.data?.message || 'Failed to delete session', variant: 'destructive' })
      setDeletingSession(false)
    }
  }

  const handleDeleteExercise = async (exerciseId: number) => {
    setDeletingExercise(true)
    try {
      await api.delete(`/sessions/${id}/exercises/${exerciseId}`)
      await fetchExercises()
      setDeleteConfirmId(null)
      toast({ title: 'Exercise removed', description: 'Exercise removed from session.' })
    } catch (err) {
      const e = err as AxiosError<{ message: string }>
      toast({ title: 'Error', description: e.response?.data?.message || 'Failed to remove exercise', variant: 'destructive' })
    } finally {
      setDeletingExercise(false)
    }
  }

  const handleEditSession = async () => {
    setSaving(true)
    try {
      const body: Record<string, unknown> = {}
      if (editForm.startDate) body.startDate = editForm.startDate
      if (editForm.duration) body.duration = editForm.duration
      const { data } = await api.put<TrainingSessionDto>(`/sessions/${id}`, body)
      setSession(data)
      setEditOpen(false)
      toast({ title: 'Session updated', description: 'Training session updated successfully.' })
    } catch (err) {
      const e = err as AxiosError<{ message: string }>
      toast({ title: 'Error', description: e.response?.data?.message || 'Failed to update session', variant: 'destructive' })
    } finally {
      setSaving(false)
    }
  }

  if (loading || !session) return <SessionSkeleton />

  const upcoming = isUpcoming(session.startDate)
  const active = isActive(session.startDate, session.duration)

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center gap-4">
        <Button variant="outline" size="icon" onClick={() => navigate('/sessions')}>
          <ArrowLeft className="h-4 w-4" />
        </Button>
        <div className="flex-1">
          <div className="flex items-center gap-3">
            <h1 className="text-2xl font-bold text-slate-900">Session - {session.customerFirstName} {session.customerLastName}</h1>
            <Badge variant={upcoming ? 'success' : active ? 'default' : 'secondary'}>
              {upcoming ? 'Upcoming' : active ? 'Active' : 'Completed'}
            </Badge>
          </div>
          <p className="text-slate-500 mt-0.5">
            {formatDate(session.startDate)} at {formatTime(session.startDate)} · {session.duration} min
          </p>
        </div>
        {userType === 'TRAINER' && (
          <div className="flex gap-2">
            <Button variant="outline" onClick={() => setEditOpen(true)}>
              <Pencil className="h-4 w-4 mr-2" />
              Edit
            </Button>
            <Button
              variant="outline"
              className="text-red-600 hover:text-red-700 hover:bg-red-50 border-red-200"
              onClick={() => setDeleteSessionOpen(true)}
            >
              <Trash className="h-4 w-4 mr-2" />
              Delete
            </Button>
          </div>
        )}
      </div>

      {/* Session info */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <InfoCard label="Date" value={formatDate(session.startDate)} />
        <InfoCard label="Time" value={formatTime(session.startDate)} />
        <InfoCard label="Duration" value={`${session.duration} min`} />
        <InfoCard label="Exercises" value={String(exercises.length)} />
      </div>

      {/* Exercises */}
      <Card>
        <CardHeader className="flex flex-row items-center justify-between">
          <div>
            <CardTitle>Exercises</CardTitle>
            <CardDescription>{exercises.length} exercise{exercises.length !== 1 ? 's' : ''} in this session</CardDescription>
          </div>
          {userType === 'TRAINER' && (
            <Button size="sm" onClick={() => setAddOpen(true)}>
              <Plus className="h-4 w-4 mr-2" />
              Add Exercise
            </Button>
          )}
        </CardHeader>
        <CardContent>
          {exercises.length === 0 ? (
            <div className="text-center py-10">
              <Dumbbell className="h-10 w-10 mx-auto mb-3 text-slate-300" />
              <p className="text-slate-500">No exercises yet</p>
              {userType === 'TRAINER' && (
                <Button className="mt-4" size="sm" onClick={() => setAddOpen(true)}>
                  Add first exercise
                </Button>
              )}
            </div>
          ) : (
            <div className="space-y-3">
              {exercises.map((ex) => (
                <div
                  key={ex.id}
                  className="flex items-center justify-between p-4 rounded-lg bg-slate-50 border border-slate-100 hover:border-slate-200 transition-colors"
                >
                  <div className="flex items-center gap-3">
                    <div className="bg-slate-900 text-white p-2 rounded-lg">
                      <Dumbbell className="h-4 w-4" />
                    </div>
                    <div>
                      <p className="font-medium text-slate-900">{ex.exerciseName}</p>
                      <div className="flex gap-3 mt-1">
                        <span className="text-xs text-slate-500">{ex.series} sets</span>
                        <span className="text-xs text-slate-500">×</span>
                        <span className="text-xs text-slate-500">{ex.reps} reps</span>
                        {ex.weight > 0 && (
                          <>
                            <span className="text-xs text-slate-500">·</span>
                            <span className="text-xs text-slate-500">{ex.weight} kg</span>
                          </>
                        )}
                      </div>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <Button variant="ghost" size="sm" onClick={() => handleViewExercise(ex.exerciseId)}>
                      Details
                    </Button>
                    {userType === 'TRAINER' && (
                      deleteConfirmId === ex.id ? (
                        <div className="flex items-center gap-1">
                          <Button
                            variant="destructive"
                            size="sm"
                            disabled={deletingExercise}
                            onClick={() => handleDeleteExercise(ex.exerciseId)}
                          >
                            {deletingExercise ? 'Removing...' : 'Confirm'}
                          </Button>
                          <Button
                            variant="outline"
                            size="sm"
                            disabled={deletingExercise}
                            onClick={() => setDeleteConfirmId(null)}
                          >
                            Cancel
                          </Button>
                        </div>
                      ) : (
                        <Button
                          variant="ghost"
                          size="sm"
                          className="text-red-500 hover:text-red-600 hover:bg-red-50"
                          onClick={() => setDeleteConfirmId(ex.id)}
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      )
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>

      {/* Add Exercise Dialog */}
      <Dialog open={addOpen} onOpenChange={setAddOpen}>
        <DialogContent className="max-w-lg">
          <DialogHeader>
            <DialogTitle>Add Exercise</DialogTitle>
            <DialogDescription>Search for an exercise and configure sets, reps, and weight</DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            <div className="flex gap-2">
              <Input
                placeholder="Search exercises..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
              />
              <Button variant="outline" onClick={handleSearch} disabled={searching}>
                <Search className="h-4 w-4" />
              </Button>
            </div>
            {searchResults.length > 0 && !selectedExercise && (
              <div className="max-h-48 overflow-y-auto border rounded-md divide-y">
                {searchResults.map((ex) => (
                  <button
                    key={ex.id}
                    className="w-full px-3 py-2 text-left text-sm hover:bg-slate-50 transition-colors"
                    onClick={() => setSelectedExercise(ex)}
                  >
                    {ex.name}
                  </button>
                ))}
              </div>
            )}
            {selectedExercise && (
              <>
                <div className="flex items-center justify-between p-3 bg-slate-50 rounded-lg">
                  <span className="font-medium text-sm">{selectedExercise.name}</span>
                  <Button variant="ghost" size="sm" onClick={() => setSelectedExercise(null)}>
                    Change
                  </Button>
                </div>
                <Separator />
                <div className="grid grid-cols-3 gap-3">
                  <div className="space-y-1">
                    <Label htmlFor="series">Sets</Label>
                    <Input
                      id="series"
                      type="number"
                      min="1"
                      value={exerciseForm.series}
                      onChange={(e) => setExerciseForm((p) => ({ ...p, series: Number(e.target.value) }))}
                    />
                  </div>
                  <div className="space-y-1">
                    <Label htmlFor="reps">Reps</Label>
                    <Input
                      id="reps"
                      type="number"
                      min="1"
                      value={exerciseForm.reps}
                      onChange={(e) => setExerciseForm((p) => ({ ...p, reps: Number(e.target.value) }))}
                    />
                  </div>
                  <div className="space-y-1">
                    <Label htmlFor="weight">Weight (kg)</Label>
                    <Input
                      id="weight"
                      type="number"
                      min="0"
                      value={exerciseForm.weight}
                      onChange={(e) => setExerciseForm((p) => ({ ...p, weight: Number(e.target.value) }))}
                    />
                  </div>
                </div>
              </>
            )}
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setAddOpen(false)}>Cancel</Button>
            <Button onClick={handleAddExercise} disabled={!selectedExercise || addingExercise}>
              {addingExercise ? 'Adding...' : 'Add Exercise'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Exercise Detail Dialog */}
      <Dialog open={detailOpen} onOpenChange={setDetailOpen}>
        <DialogContent className="max-w-lg max-h-[80vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>{detailExercise?.name ?? 'Exercise Details'}</DialogTitle>
          </DialogHeader>
          {loadingDetail ? (
            <div className="space-y-3">
              <Skeleton className="h-4 w-full" />
              <Skeleton className="h-4 w-3/4" />
              <Skeleton className="h-4 w-full" />
            </div>
          ) : detailExercise ? (
            <div className="space-y-4">
              {detailExercise.overview && (
                <div>
                  <h4 className="font-semibold text-sm text-slate-700 mb-1">Overview</h4>
                  <p className="text-sm text-slate-600">{detailExercise.overview}</p>
                </div>
              )}
              {detailExercise.instruction && detailExercise.instruction.length > 0 && (
                <div>
                  <h4 className="font-semibold text-sm text-slate-700 mb-2">Instructions</h4>
                  <ol className="space-y-1.5 list-decimal list-inside">
                    {detailExercise.instruction.map((step, i) => (
                      <li key={i} className="text-sm text-slate-600">{step}</li>
                    ))}
                  </ol>
                </div>
              )}
              {detailExercise.exerciseTip && detailExercise.exerciseTip.length > 0 && (
                <div>
                  <h4 className="font-semibold text-sm text-slate-700 mb-2">Tips</h4>
                  <ul className="space-y-1.5">
                    {detailExercise.exerciseTip.map((tip, i) => (
                      <li key={i} className="text-sm text-slate-600 flex gap-2">
                        <span className="text-slate-400">•</span>
                        {tip}
                      </li>
                    ))}
                  </ul>
                </div>
              )}
              {detailExercise.videoUrl && (
                <a
                  href={detailExercise.videoUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="flex items-center gap-2 text-sm text-blue-600 hover:underline"
                >
                  <ExternalLink className="h-4 w-4" />
                  Watch video
                </a>
              )}
            </div>
          ) : null}
        </DialogContent>
      </Dialog>

      {/* Delete Session Dialog */}
      <Dialog open={deleteSessionOpen} onOpenChange={setDeleteSessionOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Delete Session</DialogTitle>
            <DialogDescription>
              Are you sure you want to delete this session with {session.customerFirstName} {session.customerLastName}? All exercises will also be removed. This action cannot be undone.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDeleteSessionOpen(false)} disabled={deletingSession}>
              Cancel
            </Button>
            <Button variant="destructive" onClick={handleDeleteSession} disabled={deletingSession}>
              {deletingSession ? 'Deleting...' : 'Delete Session'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Edit Session Dialog */}
      <Dialog open={editOpen} onOpenChange={setEditOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Edit Session</DialogTitle>
            <DialogDescription>Update the session schedule</DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-2">
            <div className="space-y-2">
              <Label htmlFor="editStartDate">Start Date & Time</Label>
              <Input
                id="editStartDate"
                type="datetime-local"
                value={editForm.startDate}
                onChange={(e) => setEditForm((p) => ({ ...p, startDate: e.target.value }))}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="editDuration">Duration (minutes)</Label>
              <Input
                id="editDuration"
                type="number"
                min="1"
                value={editForm.duration}
                onChange={(e) => setEditForm((p) => ({ ...p, duration: Number(e.target.value) }))}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setEditOpen(false)}>Cancel</Button>
            <Button onClick={handleEditSession} disabled={saving}>
              {saving ? 'Saving...' : 'Save Changes'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}

function InfoCard({ label, value }: { label: string; value: string }) {
  return (
    <Card>
      <CardContent className="p-4">
        <p className="text-xs text-slate-500 uppercase tracking-wide">{label}</p>
        <p className="text-lg font-semibold text-slate-900 mt-1">{value}</p>
      </CardContent>
    </Card>
  )
}

function SessionSkeleton() {
  return (
    <div className="space-y-6">
      <Skeleton className="h-10 w-64" />
      <div className="grid grid-cols-4 gap-4">
        {[1, 2, 3, 4].map((i) => <Skeleton key={i} className="h-20" />)}
      </div>
      <Skeleton className="h-64" />
    </div>
  )
}
