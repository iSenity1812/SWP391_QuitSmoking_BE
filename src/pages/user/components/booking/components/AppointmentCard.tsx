import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Calendar, MessageSquare } from "lucide-react"
import type { Appointment } from "../types/booking.types"
import { getStatusColor, getStatusLabel, getMethodIcon } from "../booking.utils"

interface AppointmentCardProps {
    appointment: Appointment
    showActions?: boolean
}

export function AppointmentCard({ appointment, showActions = true }: AppointmentCardProps) {
    const MethodIcon = getMethodIcon(appointment.method)

    return (
        <div className="flex items-center justify-between p-4 border border-slate-200 dark:border-slate-700 rounded-lg">
            <div className="flex items-center space-x-4">
                <Avatar className="h-12 w-12">
                    <AvatarImage src={appointment.coachAvatar || "/placeholder.svg"} />
                    <AvatarFallback>{appointment.coachName.charAt(0)}</AvatarFallback>
                </Avatar>
                <div>
                    <h4 className="font-medium text-slate-900 dark:text-white">{appointment.coachName}</h4>
                    <div className="flex items-center space-x-2 text-sm text-slate-600 dark:text-slate-400">
                        <Calendar className="w-4 h-4" />
                        <span>
                            {appointment.date} - {appointment.time}
                        </span>
                        <MethodIcon className="w-4 h-4" />
                        {appointment.status !== "completed" && <span>{appointment.duration} phút</span>}
                    </div>
                    {appointment.notes && <p className="text-sm text-slate-600 dark:text-slate-400 mt-1">{appointment.notes}</p>}
                    <Badge className={getStatusColor(appointment.status)} variant="outline">
                        {getStatusLabel(appointment.status)}
                    </Badge>
                </div>
            </div>
            <div className="text-right">
                <p className="font-medium text-slate-900 dark:text-white">{appointment.price.toLocaleString("vi-VN")} VNĐ</p>
                {showActions && (
                    <div className="flex space-x-2 mt-2">
                        {appointment.status === "completed" ? (
                            <Button size="sm" variant="outline">
                                Đánh Giá
                            </Button>
                        ) : (
                            <>
                                <Button size="sm" variant="outline">
                                    <MessageSquare className="w-4 h-4" />
                                </Button>
                                <Button size="sm" variant="outline">
                                    Hủy
                                </Button>
                            </>
                        )}
                    </div>
                )}
            </div>
        </div>
    )
}
