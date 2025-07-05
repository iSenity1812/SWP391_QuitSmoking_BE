import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import type { Appointment } from "../types/booking.types"
import { AppointmentCard   } from "../components/AppointmentCard"

interface AppointmentsTabProps {
  appointments: Appointment[]
}

export function AppointmentsTab({ appointments }: AppointmentsTabProps) {
  const upcomingAppointments = appointments.filter((apt) => apt.status === "scheduled" || apt.status === "confirmed")

  return (
    <div className="space-y-6">
      <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
        <CardHeader>
          <CardTitle>Lịch Hẹn Sắp Tới</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {upcomingAppointments.map((appointment) => (
              <AppointmentCard key={appointment.id} appointment={appointment} />
            ))}
            {upcomingAppointments.length === 0 && (
              <p className="text-center text-slate-500 dark:text-slate-400 py-8">Bạn chưa có lịch hẹn nào sắp tới</p>
            )}
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
