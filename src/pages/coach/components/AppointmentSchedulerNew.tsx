"use client"

import { Card, CardContent } from "@/components/ui/card"
import { CalendarIcon, Clock, Users, CheckCircle } from "lucide-react"
import { useAppointments, type Appointment } from "../AppointmentContext"
import { WeeklyScheduleTable } from "./WeeklyScheduleTable"

export function AppointmentScheduler() {
  const {
    appointments,
    getTodayAppointments,
    getUpcomingAppointments,
  } = useAppointments()

  const todayAppointments = getTodayAppointments()
  const upcomingAppointments = getUpcomingAppointments()

  return (
    <div className="space-y-6">
      {/* Quick Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
          <CardContent className="p-4">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-blue-100 dark:bg-blue-900/20 rounded-lg flex items-center justify-center">
                <CalendarIcon className="w-5 h-5 text-blue-600 dark:text-blue-400" />
              </div>
              <div>
                <p className="text-2xl font-bold text-slate-900 dark:text-white">{todayAppointments.length}</p>
                <p className="text-sm text-slate-600 dark:text-slate-400">Lịch hôm nay</p>
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
                <p className="text-2xl font-bold text-slate-900 dark:text-white">
                  {appointments.filter((apt: Appointment) => apt.status === "confirmed").length}
                </p>
                <p className="text-sm text-slate-600 dark:text-slate-400">Đã xác nhận</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
          <CardContent className="p-4">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-orange-100 dark:bg-orange-900/20 rounded-lg flex items-center justify-center">
                <Clock className="w-5 h-5 text-orange-600 dark:text-orange-400" />
              </div>
              <div>
                <p className="text-2xl font-bold text-slate-900 dark:text-white">
                  {appointments.filter((apt: Appointment) => apt.status === "scheduled").length}
                </p>
                <p className="text-sm text-slate-600 dark:text-slate-400">Chờ xác nhận</p>
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
                <p className="text-2xl font-bold text-slate-900 dark:text-white">{upcomingAppointments.length}</p>
                <p className="text-sm text-slate-600 dark:text-slate-400">Tuần này</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* New Weekly Schedule Table */}
      <WeeklyScheduleTable />
    </div>
  )
}
