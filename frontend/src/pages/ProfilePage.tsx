import { useEffect, useState } from 'react'
import { User, Phone, Mail, Pencil, Copy, Check, Link2, Unlink } from 'lucide-react'
import { useAuth } from '@/context/AuthContext'
import api from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card, CardContent, CardFooter, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Badge } from '@/components/ui/badge'
import { Skeleton } from '@/components/ui/skeleton'
import { Separator } from '@/components/ui/separator'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from '@/components/ui/dialog'
import { toast } from '@/hooks/use-toast'
import type { TrainerDto, CustomerDto } from '@/types'
import { AxiosError } from 'axios'

type ProfileData = (TrainerDto & { email: string }) | (CustomerDto & { email: string })

function isTrainer(data: ProfileData): data is TrainerDto & { email: string } {
  return 'identifier' in data
}

function getEmailFromToken(): string {
  const token = localStorage.getItem('token')
  if (!token) return ''
  try {
    return JSON.parse(atob(token.split('.')[1])).sub ?? ''
  } catch {
    return ''
  }
}

export default function ProfilePage() {
  const { userType } = useAuth()
  const [profile, setProfile] = useState<ProfileData | null>(null)
  const [assignedTrainer, setAssignedTrainer] = useState<TrainerDto | null>(null)
  const [loading, setLoading] = useState(true)
  const [editOpen, setEditOpen] = useState(false)
  const [form, setForm] = useState({ firstName: '', lastName: '', phone: '' })
  const [saving, setSaving] = useState(false)
  const [copied, setCopied] = useState(false)

  // Reset form to current profile values each time the edit dialog opens
  useEffect(() => {
    if (editOpen && profile) {
      setForm({
        firstName: profile.firstName ?? '',
        lastName: profile.lastName ?? '',
        phone: profile.phone ?? '',
      })
    }
  }, [editOpen])

  const [linkOpen, setLinkOpen] = useState(false)
  const [trainerIdentifier, setTrainerIdentifier] = useState('')
  const [linking, setLinking] = useState(false)
  const [linkError, setLinkError] = useState('')
  const [unlinkOpen, setUnlinkOpen] = useState(false)
  const [unlinking, setUnlinking] = useState(false)

  const email = getEmailFromToken()

  const fetchProfile = async () => {
    try {
      if (userType === 'TRAINER') {
        const { data } = await api.get<TrainerDto>('/trainers/me')
        setProfile({ ...data, email })
        setForm({ firstName: data.firstName, lastName: data.lastName, phone: data.phone })
      } else {
        const { data } = await api.get<CustomerDto>('/customers/me')
        setProfile({ ...data, email })
        setForm({ firstName: data.firstName, lastName: data.lastName, phone: data.phone })
        if (data.trainerId) {
          const trainerRes = await api.get<TrainerDto>(`/trainers/${data.trainerId}`)
          setAssignedTrainer(trainerRes.data)
        }
      }
    } catch (_) {}
  }

  useEffect(() => {
    fetchProfile().finally(() => setLoading(false))
  }, [userType])

  const handleSave = async () => {
    setSaving(true)
    try {
      if (userType === 'TRAINER') {
        const { data } = await api.put<TrainerDto>('/trainers', form)
        setProfile({ ...data, email })
      } else {
        const { data } = await api.put<CustomerDto>('/customers', form)
        setProfile({ ...data, email })
      }
      setEditOpen(false)
      toast({ title: 'Profile updated', description: 'Your profile has been saved.' })
    } catch (err) {
      const e = err as AxiosError<{ message: string }>
      toast({ title: 'Error', description: e.response?.data?.message || 'Failed to update profile', variant: 'destructive' })
    } finally {
      setSaving(false)
    }
  }

  const handleLink = async () => {
    if (!trainerIdentifier.trim()) return
    setLinkError('')
    setLinking(true)
    try {
      await api.post(`/customers/${encodeURIComponent(trainerIdentifier)}`)
      setLinkOpen(false)
      setTrainerIdentifier('')
      toast({ title: 'Linked!', description: 'You have been linked to your trainer.' })
      await fetchProfile()
    } catch (err) {
      const e = err as AxiosError<{ message: string }>
      setLinkError(e.response?.data?.message || 'Invalid trainer identifier')
    } finally {
      setLinking(false)
    }
  }

  const handleUnlink = async () => {
    setUnlinking(true)
    try {
      await api.delete('/customers/me/trainer')
      setUnlinkOpen(false)
      setAssignedTrainer(null)
      if (profile && !isTrainer(profile)) {
        setProfile({ ...profile, trainerId: null })
      }
      toast({ title: 'Unlinked', description: 'You have been unlinked from your trainer and future sessions have been cancelled.' })
    } catch (err) {
      const e = err as AxiosError<{ message: string }>
      toast({ title: 'Error', description: e.response?.data?.message || 'Failed to unlink from trainer', variant: 'destructive' })
    } finally {
      setUnlinking(false)
    }
  }

  const copyIdentifier = () => {
    if (profile && isTrainer(profile)) {
      navigator.clipboard.writeText(profile.identifier)
      setCopied(true)
      setTimeout(() => setCopied(false), 2000)
    }
  }

  if (loading) {
    return (
      <div className="space-y-4 max-w-2xl">
        <Skeleton className="h-8 w-32" />
        <Skeleton className="h-48" />
        <Skeleton className="h-32" />
      </div>
    )
  }

  if (!profile) {
    return (
      <div className="max-w-2xl">
        <h1 className="text-2xl font-bold text-slate-900 mb-4">Profile</h1>
        <Card>
          <CardContent className="py-12 text-center text-slate-500">
            Could not load profile. Please try again.
          </CardContent>
        </Card>
      </div>
    )
  }

  const initials = `${profile.firstName?.[0] ?? ''}${profile.lastName?.[0] ?? ''}`

  return (
    <div className="space-y-6 max-w-2xl">
      <h1 className="text-2xl font-bold text-slate-900">Profile</h1>

      <Card>
        <CardHeader>
          <div className="flex items-center gap-4">
            <Avatar className="h-16 w-16">
              <AvatarFallback className="text-xl font-bold">{initials}</AvatarFallback>
            </Avatar>
            <div className="flex-1">
              <div className="flex items-center gap-2">
                <CardTitle>
                  {profile.firstName} {profile.lastName}
                </CardTitle>
                <Badge variant={userType === 'TRAINER' ? 'default' : 'secondary'} className="capitalize">
                  {userType?.toLowerCase()}
                </Badge>
              </div>
              <CardDescription className="mt-1">Manage your account details</CardDescription>
            </div>
            <Button variant="outline" size="sm" onClick={() => setEditOpen(true)}>
              <Pencil className="h-3.5 w-3.5 mr-1.5" />
              Edit
            </Button>
          </div>
        </CardHeader>
        <Separator />
        <CardContent className="pt-6 space-y-4">
          <div className="flex items-center gap-3">
            <User className="h-4 w-4 text-slate-400" />
            <div>
              <p className="text-xs text-slate-500">Full Name</p>
              <p className="text-sm font-medium">{profile.firstName} {profile.lastName}</p>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <Mail className="h-4 w-4 text-slate-400" />
            <div>
              <p className="text-xs text-slate-500">Email</p>
              <p className="text-sm font-medium">{email}</p>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <Phone className="h-4 w-4 text-slate-400" />
            <div>
              <p className="text-xs text-slate-500">Phone</p>
              <p className="text-sm font-medium">{profile.phone}</p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Trainer identifier */}
      {userType === 'TRAINER' && isTrainer(profile) && (
        <Card>
          <CardHeader>
            <CardTitle className="text-base">Trainer Identifier</CardTitle>
            <CardDescription>Share this code with customers so they can link to you</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="flex items-center gap-2">
              <code className="flex-1 bg-slate-100 rounded-md px-4 py-2.5 text-sm font-mono font-semibold tracking-wider">
                {profile.identifier}
              </code>
              <Button variant="outline" size="icon" onClick={copyIdentifier}>
                {copied ? <Check className="h-4 w-4 text-green-600" /> : <Copy className="h-4 w-4" />}
              </Button>
            </div>
          </CardContent>
        </Card>
      )}

      {/* Customer: assigned trainer */}
      {userType === 'CUSTOMER' && (
        <Card>
          <CardHeader>
            <CardTitle className="text-base">My Trainer</CardTitle>
            <CardDescription>The trainer currently assigned to your account</CardDescription>
          </CardHeader>
          <CardContent>
            {assignedTrainer ? (
              <div className="flex items-center gap-3 p-3 bg-slate-50 rounded-lg border border-slate-100">
                <div className="bg-slate-900 text-white rounded-full h-10 w-10 flex items-center justify-center text-sm font-bold shrink-0">
                  {assignedTrainer.firstName[0]}{assignedTrainer.lastName[0]}
                </div>
                <div>
                  <p className="font-medium text-slate-900">
                    {assignedTrainer.firstName} {assignedTrainer.lastName}
                  </p>
                  <p className="text-xs text-slate-500">{assignedTrainer.phone}</p>
                </div>
              </div>
            ) : (
              <p className="text-sm text-slate-500">No trainer linked yet.</p>
            )}
          </CardContent>
          <CardFooter className="gap-2">
            <Button variant="outline" onClick={() => setLinkOpen(true)}>
              <Link2 className="h-4 w-4 mr-2" />
              {assignedTrainer ? 'Change Trainer' : 'Link to Trainer'}
            </Button>
            {assignedTrainer && (
              <Button
                variant="outline"
                className="text-red-600 hover:text-red-700 hover:bg-red-50 border-red-200"
                onClick={() => setUnlinkOpen(true)}
              >
                <Unlink className="h-4 w-4 mr-2" />
                Unlink Trainer
              </Button>
            )}
          </CardFooter>
        </Card>
      )}

      {/* Edit dialog */}
      <Dialog open={editOpen} onOpenChange={setEditOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Edit Profile</DialogTitle>
            <DialogDescription>Update your personal information</DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-2">
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-2">
                <Label>First Name</Label>
                <Input value={form.firstName} onChange={(e) => setForm((p) => ({ ...p, firstName: e.target.value }))} />
              </div>
              <div className="space-y-2">
                <Label>Last Name</Label>
                <Input value={form.lastName} onChange={(e) => setForm((p) => ({ ...p, lastName: e.target.value }))} />
              </div>
            </div>
            <div className="space-y-2">
              <Label>Phone</Label>
              <Input value={form.phone} onChange={(e) => setForm((p) => ({ ...p, phone: e.target.value }))} />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setEditOpen(false)}>Cancel</Button>
            <Button onClick={handleSave} disabled={saving}>{saving ? 'Saving...' : 'Save Changes'}</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Unlink from trainer dialog */}
      <Dialog open={unlinkOpen} onOpenChange={setUnlinkOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Unlink from Trainer</DialogTitle>
            <DialogDescription>
              Are you sure you want to unlink from{' '}
              <span className="font-semibold">
                {assignedTrainer?.firstName} {assignedTrainer?.lastName}
              </span>?
            </DialogDescription>
          </DialogHeader>
          <Alert variant="destructive">
            <AlertDescription>
              This will also <span className="font-semibold">cancel all upcoming training sessions</span> with this trainer. Past sessions will be kept.
            </AlertDescription>
          </Alert>
          <DialogFooter>
            <Button variant="outline" onClick={() => setUnlinkOpen(false)}>Cancel</Button>
            <Button variant="destructive" onClick={handleUnlink} disabled={unlinking}>
              {unlinking ? 'Unlinking...' : 'Unlink Trainer'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Link to trainer dialog */}
      <Dialog open={linkOpen} onOpenChange={(open) => { setLinkOpen(open); setLinkError('') }}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Link to Trainer</DialogTitle>
            <DialogDescription>Enter the identifier provided by your trainer</DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-2">
            {linkError && (
              <Alert variant="destructive">
                <AlertDescription>{linkError}</AlertDescription>
              </Alert>
            )}
            <div className="space-y-2">
              <Label>Trainer Identifier</Label>
              <Input
                placeholder="e.g. #Ab3Xy"
                value={trainerIdentifier}
                onChange={(e) => setTrainerIdentifier(e.target.value)}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setLinkOpen(false)}>Cancel</Button>
            <Button onClick={handleLink} disabled={linking || !trainerIdentifier.trim()}>
              {linking ? 'Linking...' : 'Link Account'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}
