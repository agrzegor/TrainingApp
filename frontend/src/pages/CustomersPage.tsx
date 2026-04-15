import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Users, UserMinus, Phone, Info } from 'lucide-react'
import api from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Skeleton } from '@/components/ui/skeleton'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from '@/components/ui/dialog'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { toast } from '@/hooks/use-toast'
import type { CustomerDto } from '@/types'
import { AxiosError } from 'axios'

export default function CustomersPage() {
  const [customers, setCustomers] = useState<CustomerDto[]>([])
  const [loading, setLoading] = useState(true)
  const [unlinkTarget, setUnlinkTarget] = useState<CustomerDto | null>(null)
  const [unlinking, setUnlinking] = useState(false)

  const fetchCustomers = async () => {
    try {
      const { data } = await api.get<CustomerDto[]>('/trainers/me/customers')
      setCustomers(data)
    } catch (_) {}
  }

  useEffect(() => {
    fetchCustomers().finally(() => setLoading(false))
  }, [])

  const handleUnlink = async () => {
    if (!unlinkTarget) return
    setUnlinking(true)
    try {
      await api.delete(`/trainers/customers/${unlinkTarget.id}`)
      await fetchCustomers()
      setUnlinkTarget(null)
      toast({ title: 'Customer unlinked', description: `${unlinkTarget.firstName} ${unlinkTarget.lastName} has been removed and upcoming sessions cancelled.` })
    } catch (err) {
      const e = err as AxiosError<{ message: string }>
      toast({ title: 'Error', description: e.response?.data?.message || 'Failed to unlink customer', variant: 'destructive' })
    } finally {
      setUnlinking(false)
    }
  }

  if (loading) {
    return (
      <div className="space-y-4">
        {[1, 2, 3].map((i) => <Skeleton key={i} className="h-20" />)}
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Customers</h1>
          <p className="text-slate-500 mt-1">
            {customers.length} customer{customers.length !== 1 ? 's' : ''} assigned to you
          </p>
        </div>
        <Badge variant="secondary" className="text-sm px-3 py-1">
          <Users className="h-3.5 w-3.5 mr-1.5" />
          {customers.length} total
        </Badge>
      </div>

      {customers.length === 0 ? (
        <Card>
          <CardContent className="py-16 text-center">
            <Users className="h-12 w-12 mx-auto mb-4 text-slate-300" />
            <h3 className="text-lg font-semibold text-slate-900 mb-2">No customers yet</h3>
            <p className="text-slate-500 max-w-sm mx-auto">
              Customers can link themselves to you using your trainer identifier. Share it so they can connect!
            </p>
          </CardContent>
        </Card>
      ) : (
        <div className="grid gap-4 md:grid-cols-2">
          {customers.map((customer) => (
            <Card key={customer.id} className="hover:shadow-md transition-shadow">
              <CardHeader className="pb-2">
                <div className="flex items-center gap-3">
                  <Avatar className="h-12 w-12">
                    <AvatarFallback className="text-sm font-semibold">
                      {customer.firstName[0]}{customer.lastName[0]}
                    </AvatarFallback>
                  </Avatar>
                  <div className="flex-1 min-w-0">
                    <CardTitle className="text-base">
                      {customer.firstName} {customer.lastName}
                    </CardTitle>
                    <CardDescription className="flex items-center gap-1 mt-0.5">
                      <Phone className="h-3 w-3" />
                      {customer.phone}
                    </CardDescription>
                  </div>
                </div>
              </CardHeader>
              <CardContent className="pt-2 pb-3">
                <div className="flex gap-2">
                  <Button variant="outline" size="sm" asChild className="flex-1">
                    <Link to={`/customers/${customer.id}`}>
                      <Info className="h-3.5 w-3.5 mr-1.5" />
                      Details
                    </Link>
                  </Button>
                  <Button
                    variant="outline"
                    size="sm"
                    className="text-red-600 hover:text-red-700 hover:bg-red-50 border-red-200"
                    onClick={() => setUnlinkTarget(customer)}
                  >
                    <UserMinus className="h-3.5 w-3.5 mr-1.5" />
                    Unlink
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}

      {/* Unlink confirmation dialog */}
      <Dialog open={!!unlinkTarget} onOpenChange={(open) => !open && setUnlinkTarget(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Unlink Customer</DialogTitle>
            <DialogDescription>
              Are you sure you want to remove{' '}
              <span className="font-semibold">
                {unlinkTarget?.firstName} {unlinkTarget?.lastName}
              </span>{' '}
              from your client list?
            </DialogDescription>
          </DialogHeader>
          <Alert variant="destructive">
            <AlertDescription>
              This will also <span className="font-semibold">cancel all upcoming training sessions</span> with this customer. Past sessions will be kept. This action cannot be undone.
            </AlertDescription>
          </Alert>
          <DialogFooter>
            <Button variant="outline" onClick={() => setUnlinkTarget(null)}>
              Cancel
            </Button>
            <Button variant="destructive" onClick={handleUnlink} disabled={unlinking}>
              {unlinking ? 'Unlinking...' : 'Unlink Customer'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}
