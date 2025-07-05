import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import type { Appointment } from "../types/booking.types"
import { AppointmentCard } from "../components/AppointmentCard"

interface HistoryTabProps {
    appointments: Appointment[]
}

export function HistoryTab({ appointments }: HistoryTabProps) {
    const completedAppointments = appointments.filter((apt) => apt.status === "completed")

    return (
        <div className="space-y-6">
            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                <CardHeader>
                    <CardTitle>Lịch Sử Tư Vấn</CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="space-y-4">
                        {completedAppointments.map((appointment) => (
                            <AppointmentCard key={appointment.id} appointment={appointment} />
                        ))}
                        {completedAppointments.length === 0 && (
                            <p className="text-center text-slate-500 dark:text-slate-400 py-8">Bạn chưa có lịch sử tư vấn nào</p>
                        )}
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}
