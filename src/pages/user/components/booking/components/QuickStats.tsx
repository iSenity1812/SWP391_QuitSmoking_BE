import { Card, CardContent } from "@/components/ui/card"
import { Calendar, CheckCircle, Users } from "lucide-react"
import type { Appointment } from "../types/booking.types"

interface QuickStatsProps {
    appointments: Appointment[]
    coachCount: number
}

export function QuickStats({ appointments, coachCount }: QuickStatsProps) {
    const upcomingAppointments = appointments.filter((apt) => apt.status === "scheduled" || apt.status === "confirmed")
    const completedAppointments = appointments.filter((apt) => apt.status === "completed")

    return (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                <CardContent className="p-4">
                    <div className="flex items-center space-x-3">
                        <div className="w-10 h-10 bg-blue-100 dark:bg-blue-900/20 rounded-lg flex items-center justify-center">
                            <Calendar className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                        </div>
                        <div>
                            <p className="text-2xl font-bold text-slate-900 dark:text-white">{upcomingAppointments.length}</p>
                            <p className="text-sm text-slate-600 dark:text-slate-400">Lịch sắp tới</p>
                        </div>
                    </div>
                </CardContent>
            </Card>

            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                <CardContent className="p-4">
                    <div className="flex items-center space-x-3">
                        <div className="w-10 h-10 bg-green-100 dark:bg-green-900/20 rounded-lg flex items-center justify-center">
                            <CheckCircle className="w-5 h-5 text-green-600 dark:text-green-400" />
                        </div>
                        <div>
                            <p className="text-2xl font-bold text-slate-900 dark:text-white">{completedAppointments.length}</p>
                            <p className="text-sm text-slate-600 dark:text-slate-400">Đã hoàn thành</p>
                        </div>
                    </div>
                </CardContent>
            </Card>

            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                <CardContent className="p-4">
                    <div className="flex items-center space-x-3">
                        <div className="w-10 h-10 bg-purple-100 dark:bg-purple-900/20 rounded-lg flex items-center justify-center">
                            <Users className="w-5 h-5 text-purple-600 dark:text-purple-400" />
                        </div>
                        <div>
                            <p className="text-2xl font-bold text-slate-900 dark:text-white">{coachCount}</p>
                            <p className="text-sm text-slate-600 dark:text-slate-400">Chuyên gia</p>
                        </div>
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}
