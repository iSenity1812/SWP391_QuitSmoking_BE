import { useState, useEffect } from "react"
import { Calendar, CalendarDays, Clock, Loader2, Mail, RefreshCcw, User, Video } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Link } from "react-router-dom"
import { useAppointments } from "../AppointmentContext"

interface AppointmentCardProps {
  appointment: {
    appointmentId: number
    memberName: string
    memberEmail: string
    scheduleDate: string
    timeSlot: {
      startTime: string
      endTime: string
    }
    status: string
    note: string
  }
}

const AppointmentCard = ({ appointment }: Omit<AppointmentCardProps, 'onStatusUpdate'>) => {
  const formatTime = (time: string) => time.slice(0, 5);

  return (
    <div className="p-4 bg-white/70 dark:bg-slate-800/70 rounded-xl shadow-lg border border-emerald-100 
      dark:border-slate-700/50 hover:bg-white dark:hover:bg-slate-700 transition-all duration-300">

      {/* Header with Member Name & Status */}
      <div className="flex items-center justify-between pb-3 border-b">
        <div className="flex items-center space-x-3">
          <User className="w-6 h-6 text-emerald-600 dark:text-emerald-400" />
          <span className="font-bold text-lg">{appointment.memberName}</span>
        </div>
        <Badge variant={appointment.status === 'CONFIRMED' ? 'secondary' : 'default'}>
          {appointment.status}
        </Badge>
      </div>

      {/* Appointment Details */}
      <div className="mt-4 space-y-2">
        <div className="flex items-center space-x-2">
          <Clock className="w-5 h-5 text-emerald-500" />
          <span>{appointment.scheduleDate} | {formatTime(appointment.timeSlot.startTime)} - {formatTime(appointment.timeSlot.endTime)}</span>
        </div>
        <div className="flex items-center space-x-2">
          <Mail className="w-5 h-5 text-emerald-500" />
          <span>{appointment.memberEmail}</span>
        </div>
      </div>

      {/* Note Section */}
      {appointment.note && (
        <div className="mt-4 p-3 bg-emerald-50 dark:bg-slate-700/50 rounded-lg">
          <p className="text-sm text-slate-600 dark:text-slate-300">{appointment.note}</p>
        </div>
      )}

      {/* Actions */}
      <div className="mt-4 flex justify-end space-x-2">
        {appointment.status === 'CONFIRMED' && (
          <Link to={`/meeting/${appointment.appointmentId}`} target="_blank">
            <Button variant="outline" size="sm">
              <Video className="w-4 h-4 mr-2" />
              Tham gia cuộc gọi
            </Button>
          </Link>
        )}
      </div>
    </div>
  )
}

export function CoachUpcomingAppointments() {
  const [loading, setLoading] = useState(true)
  const [appointments, setAppointments] = useState<AppointmentCardProps["appointment"][]>([])
  const { fetchAppointments } = useAppointments();

  const loadAppointments = async () => {
    setLoading(true)
    try {
      const data = await fetchAppointments()
      // Map data to match AppointmentCardProps["appointment"] shape if needed
      const mappedData = data.map((item: any) => ({
        appointmentId: item.appointmentId,
        memberName: item.memberName,
        memberEmail: item.memberEmail,
        scheduleDate: item.scheduleDate,
        timeSlot: {
          startTime: item.timeSlot.startTime,
          endTime: item.timeSlot.endTime,
        },
        status: item.status,
        note: item.note,
      }))
      setAppointments(mappedData)
    } catch (error) {
      console.error('Error loading appointments:', error)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadAppointments()
  }, [])

  if (loading) {
    return (
      <Card>
        <CardContent className="flex items-center justify-center py-6">
          <Loader2 className="w-6 h-6 animate-spin" />
        </CardContent>
      </Card>
    )
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <Calendar className="w-5 h-5 text-emerald-600" />
            <span>Lịch hẹn sắp tới</span>
          </div>
          <Button variant="ghost" size="sm" onClick={loadAppointments}>
            <RefreshCcw className="w-4 h-4" />
          </Button>
        </CardTitle>
      </CardHeader>

      <CardContent>
        <div className="space-y-4">
          {appointments.length === 0 ? (
            <div className="text-center py-6">
              <CalendarDays className="w-12 h-12 mx-auto text-slate-300 mb-2" />
              <p>Không có cuộc hẹn nào sắp tới</p>
            </div>
          ) : (
            appointments.map((appointment) => (
              <AppointmentCard
                key={appointment.appointmentId}
                appointment={appointment}
              />
            ))
          )}
        </div>
      </CardContent>
    </Card>
  )
}