"use client"

import { useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { CalendarIcon, Clock, Plus, Edit, Trash2, Users, Phone, MapPin, CheckCircle } from "lucide-react"
import { useAppointments, type Appointment } from "../AppointmentContext"


// Format mẫu Response JSON cho danh sách lịch hẹn
// API: GET http://localhost:8080/api/appointments/coach-appointments
// {
//   "status": 1073741824,
//   "message": "string",
//   "data": [
//     {
//       "appointmentId": 9007199254740991,
//       "member": {
//         "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
//         "username": "string",
//         "email": "string"
//       },
//       "coachSchedule": {
//         "scheduleId": 9007199254740991,
//         "coach": {
//           "coachId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
//           "username": "string",
//           "email": "string",
//           "fullName": "string"
//         },
//         "timeSlot": {
//           "timeSlotId": 1073741824,
//           "label": "string",
//           "startTime": {
//             "hour": 1073741824,
//             "minute": 1073741824,
//             "second": 1073741824,
//             "nano": 1073741824
//           },
//           "endTime": {
//             "hour": 1073741824,
//             "minute": 1073741824,
//             "second": 1073741824,
//             "nano": 1073741824
//           },
//           "deleted": true
//         },
//         "scheduleDate": "2025-06-20",
//         "booked": true
//       },
//       "status": "CONFIRMED",
//       "note": "string",
//       "bookingTime": "2025-06-20T12:04:35.928Z"
//     }
//   ],
//   "error": {},
//   "errorCode": "string",
//   "timestamp": "2025-06-20T12:04:35.928Z"
// }

export function AppointmentScheduler() {
    const [selectedDate, setSelectedDate] = useState<string>(() => {
        const today = new Date()
        const year = today.getFullYear()
        const month = String(today.getMonth() + 1).padStart(2, "0")
        const day = String(today.getDate()).padStart(2, "0")
        return `${year}-${month}-${day}`
    });
    const [isDialogOpen, setIsDialogOpen] = useState(false)
    const [newAppointment, setNewAppointment] = useState<{
        clientName: string
        date: string
        time: string
        duration: number
        type: "individual" | "group" | "emergency"
        method: "in-person" | "phone"
        notes: string
        location: string
    }>({
        clientName: "",
        date: "",
        time: "",
        duration: 60,
        type: "individual",
        method: "phone",
        notes: "",
        location: "",
    })

    const [editingAppointment, setEditingAppointment] = useState<Appointment | null>(null)
    const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
    const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)
    const [appointmentToDelete, setAppointmentToDelete] = useState<Appointment | null>(null)

    const {
        appointments,
        addAppointment,
        updateAppointment,
        deleteAppointment,
        getTodayAppointments,
        getUpcomingAppointments,
    } = useAppointments()
    const todayAppointments = getTodayAppointments()
    const upcomingAppointments = getUpcomingAppointments()

    const getStatusColor = (status: string) => {
        switch (status) {
            case "confirmed":
                return "bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400"
            case "scheduled":
                return "bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400"
            case "completed":
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
            case "cancelled":
                return "bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400"
            case "no-show":
                return "bg-orange-100 text-orange-800 dark:bg-orange-900/20 dark:text-orange-400"
            default:
                return "bg-gray-100 text-gray-800"
        }
    }

    const getStatusLabel = (status: string) => {
        switch (status) {
            case "confirmed":
                return "Đã xác nhận"
            case "scheduled":
                return "Đã lên lịch"
            case "completed":
                return "Hoàn thành"
            case "cancelled":
                return "Đã hủy"
            case "no-show":
                return "Không đến"
            default:
                return status
        }
    }

    const getMethodIcon = (method: string) => {
        switch (method) {
            case "phone":
                return <Phone className="w-4 h-4" />
            case "in-person":
                return <MapPin className="w-4 h-4" />
            default:
                return <CalendarIcon className="w-4 h-4" />
        }
    }

    const handleCreateAppointment = () => {
        if (!newAppointment.clientName || !newAppointment.date || !newAppointment.time) {
            alert("Vui lòng điền đầy đủ thông tin bắt buộc")
            return
        }

        addAppointment({
            clientName: newAppointment.clientName,
            clientAvatar: "/placeholder.svg?height=40&width=40",
            date: newAppointment.date,
            time: newAppointment.time,
            duration: newAppointment.duration,
            type: newAppointment.type,
            method: newAppointment.method,
            status: "scheduled",
            notes: newAppointment.notes,
            location: newAppointment.location,
            customerId: 0, // Default for manually created appointments
        })

        // Reset form
        setNewAppointment({
            clientName: "",
            date: "",
            time: "",
            duration: 60,
            type: "individual",
            method: "phone",
            notes: "",
            location: "",
        })
        setIsDialogOpen(false)
        alert("Đã tạo lịch hẹn thành công!")
    }

    const handleEditAppointment = (appointment: Appointment) => {
        setEditingAppointment(appointment)
        setNewAppointment({
            clientName: appointment.clientName,
            date: appointment.date,
            time: appointment.time,
            duration: appointment.duration,
            type: appointment.type,
            method: appointment.method,
            notes: appointment.notes,
            location: appointment.location || "",
        })
        setIsEditDialogOpen(true)
    }

    const handleUpdateAppointment = () => {
        if (!editingAppointment || !newAppointment.clientName || !newAppointment.date || !newAppointment.time) {
            alert("Vui lòng điền đầy đủ thông tin bắt buộc")
            return
        }

        updateAppointment(editingAppointment.id, {
            clientName: newAppointment.clientName,
            date: newAppointment.date,
            time: newAppointment.time,
            duration: newAppointment.duration,
            type: newAppointment.type,
            method: newAppointment.method,
            notes: newAppointment.notes,
            location: newAppointment.location,
        })

        // Reset form
        setNewAppointment({
            clientName: "",
            date: "",
            time: "",
            duration: 60,
            type: "individual",
            method: "phone",
            notes: "",
            location: "",
        })
        setEditingAppointment(null)
        setIsEditDialogOpen(false)
        alert("Đã cập nhật lịch hẹn thành công!")
    }

    const handleCancelAppointment = (appointmentId: number) => {
        updateAppointment(appointmentId, { status: "cancelled" })
        alert("Đã hủy lịch hẹn!")
    }

    const handleDeleteAppointment = () => {
        if (appointmentToDelete) {
            deleteAppointment(appointmentToDelete.id)
            setAppointmentToDelete(null)
            setIsDeleteDialogOpen(false)
            alert("Đã xóa lịch hẹn!")
        }
    }

    const openDeleteDialog = (appointment: Appointment) => {
        setAppointmentToDelete(appointment)
        setIsDeleteDialogOpen(true)
    }

    // Generate calendar days for current month
    const generateCalendarDays = () => {
        const today = new Date()
        const currentMonth = today.getMonth()
        const currentYear = today.getFullYear()
        const lastDay = new Date(currentYear, currentMonth + 1, 0)
        const daysInMonth = lastDay.getDate()

        const days = []
        for (let i = 1; i <= daysInMonth; i++) {
            const date = new Date(currentYear, currentMonth, i)
            // Use local date format instead of ISO to avoid timezone issues
            const year = date.getFullYear()
            const month = String(date.getMonth() + 1).padStart(2, "0")
            const day = String(date.getDate()).padStart(2, "0")
            const dateString = `${year}-${month}-${day}`

            const hasAppointments = appointments.some((apt: Appointment) => apt.date === dateString)
            const isSelected = selectedDate === dateString

            // Fix today comparison using local date
            const todayString = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, "0")}-${String(today.getDate()).padStart(2, "0")}`
            const isToday = dateString === todayString

            days.push({
                day: i,
                date: dateString,
                hasAppointments,
                isSelected,
                isToday,
            })
        }
        return days
    }

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

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                {/* Calendar */}
                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                    <CardHeader>
                        <CardTitle className="flex items-center justify-between">
                            <span>Lịch</span>
                            <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
                                <DialogTrigger asChild>
                                    <Button size="sm">
                                        <Plus className="w-4 h-4 mr-2" />
                                        Tạo Lịch Hẹn
                                    </Button>
                                </DialogTrigger>
                                <DialogContent className="max-w-md">
                                    <DialogHeader>
                                        <DialogTitle>Đặt Lịch Cho Thành Viên</DialogTitle>
                                    </DialogHeader>
                                    <div className="space-y-4">
                                        <Input
                                            placeholder="Tên thành viên *"
                                            value={newAppointment.clientName}
                                            onChange={(e) => setNewAppointment({ ...newAppointment, clientName: e.target.value })}
                                        />
                                        <div className="grid grid-cols-2 gap-2">
                                            <Input
                                                type="date"
                                                value={newAppointment.date}
                                                onChange={(e) => setNewAppointment({ ...newAppointment, date: e.target.value })}
                                            />
                                            <Input
                                                type="time"
                                                value={newAppointment.time}
                                                onChange={(e) => setNewAppointment({ ...newAppointment, time: e.target.value })}
                                            />
                                        </div>
                                        <div className="grid grid-cols-2 gap-2">
                                            <select
                                                className="px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-md bg-white dark:bg-slate-800 text-slate-900 dark:text-white"
                                                value={newAppointment.type}
                                                onChange={(e) =>
                                                    setNewAppointment({
                                                        ...newAppointment,
                                                        type: e.target.value as "individual" | "group" | "emergency",
                                                    })
                                                }
                                            >
                                                <option value="individual">Cá nhân</option>
                                                <option value="group">Nhóm</option>
                                                <option value="emergency">Khẩn cấp</option>
                                            </select>
                                            <select
                                                className="px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-md bg-white dark:bg-slate-800 text-slate-900 dark:text-white"
                                                value={newAppointment.method}
                                                onChange={(e) =>
                                                    setNewAppointment({
                                                        ...newAppointment,
                                                        method: e.target.value as "in-person" | "phone",
                                                    })
                                                }
                                            >
                                                <option value="phone">Điện thoại</option>
                                                <option value="in-person">Trực tiếp</option>
                                            </select>
                                        </div>
                                        {newAppointment.method === ("in-person" as const) && (
                                            <Input
                                                placeholder="Địa điểm"
                                                value={newAppointment.location}
                                                onChange={(e) => setNewAppointment({ ...newAppointment, location: e.target.value })}
                                            />
                                        )}
                                        <Textarea
                                            placeholder="Ghi chú"
                                            value={newAppointment.notes}
                                            onChange={(e) => setNewAppointment({ ...newAppointment, notes: e.target.value })}
                                        />
                                        <Button onClick={handleCreateAppointment} className="w-full">
                                            Đặt Lịch
                                        </Button>
                                    </div>
                                </DialogContent>
                            </Dialog>
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        {/* Simple Calendar Grid */}
                        <div className="space-y-4">
                            <div className="text-center font-semibold text-slate-900 dark:text-white">
                                Tháng {new Date().getMonth() + 1}, {new Date().getFullYear()}
                            </div>
                            <div className="grid grid-cols-7 gap-1 text-center text-sm">
                                {["CN", "T2", "T3", "T4", "T5", "T6", "T7"].map((day) => (
                                    <div key={day} className="p-2 font-medium text-slate-600 dark:text-slate-400">
                                        {day}
                                    </div>
                                ))}
                                {generateCalendarDays().map(({ day, date, hasAppointments, isSelected, isToday }) => (
                                    <button
                                        key={day}
                                        onClick={() => setSelectedDate(date)}
                                        className={`p-2 rounded-md text-sm transition-colors ${isSelected
                                            ? "bg-blue-500 text-white"
                                            : isToday
                                                ? "bg-blue-100 dark:bg-blue-900/20 text-blue-600 dark:text-blue-400"
                                                : hasAppointments
                                                    ? "bg-green-100 dark:bg-green-900/20 text-green-600 dark:text-green-400"
                                                    : "hover:bg-slate-100 dark:hover:bg-slate-700 text-slate-900 dark:text-white"
                                            }`}
                                    >
                                        {day}
                                    </button>
                                ))}
                            </div>
                        </div>
                    </CardContent>
                </Card>

                {/* Today's Appointments */}
                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                    <CardHeader>
                        <CardTitle>Lịch Thành Viên Hôm Nay</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-4">
                            {todayAppointments.map((appointment) => (
                                <div
                                    key={appointment.id}
                                    className="p-3 border border-slate-200 dark:border-slate-700 rounded-lg hover:bg-slate-50 dark:hover:bg-slate-700/50 transition-colors"
                                >
                                    <div className="flex items-start justify-between">
                                        <div className="flex items-center space-x-3">
                                            <Avatar className="h-10 w-10">
                                                <AvatarImage src={appointment.clientAvatar || "/placeholder.svg"} />
                                                <AvatarFallback>{appointment.clientName.charAt(0)}</AvatarFallback>
                                            </Avatar>
                                            <div>
                                                <p className="font-medium text-slate-900 dark:text-white">{appointment.clientName}</p>
                                                <div className="flex items-center space-x-2 text-sm text-slate-600 dark:text-slate-400">
                                                    <Clock className="w-3 h-3" />
                                                    <span>
                                                        {appointment.date} - {appointment.time}
                                                    </span>
                                                    {getMethodIcon(appointment.method)}
                                                </div>
                                            </div>
                                        </div>
                                        <Badge className={getStatusColor(appointment.status)}>{getStatusLabel(appointment.status)}</Badge>
                                    </div>
                                    {appointment.notes && (
                                        <p className="text-sm text-slate-600 dark:text-slate-400 mt-2">{appointment.notes}</p>
                                    )}
                                    <div className="flex space-x-2 mt-3">
                                        <Button size="sm" variant="outline" onClick={() => handleEditAppointment(appointment)}>
                                            <Edit className="w-3 h-3" />
                                        </Button>
                                        <Button size="sm" variant="outline" onClick={() => openDeleteDialog(appointment)}>
                                            <Trash2 className="w-3 h-3" />
                                        </Button>
                                        {appointment.status !== "cancelled" && appointment.status !== "completed" && (
                                            <Button size="sm" variant="outline" onClick={() => handleCancelAppointment(appointment.id)}>
                                                Hủy
                                            </Button>
                                        )}
                                    </div>
                                </div>
                            ))}
                            {todayAppointments.length === 0 && (
                                <p className="text-slate-500 dark:text-slate-400 text-center py-4">Không có lịch hẹn nào hôm nay</p>
                            )}
                        </div>
                    </CardContent>
                </Card>

                {/* Upcoming Appointments */}
                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                    <CardHeader>
                        <CardTitle>Lịch Thành Viên Sắp Tới</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-4">
                            {upcomingAppointments.map((appointment) => (
                                <div
                                    key={appointment.id}
                                    className="p-3 border border-slate-200 dark:border-slate-700 rounded-lg hover:bg-slate-50 dark:hover:bg-slate-700/50 transition-colors"
                                >
                                    <div className="flex items-start justify-between">
                                        <div className="flex items-center space-x-3">
                                            <Avatar className="h-8 w-8">
                                                <AvatarImage src={appointment.clientAvatar || "/placeholder.svg"} />
                                                <AvatarFallback>{appointment.clientName.charAt(0)}</AvatarFallback>
                                            </Avatar>
                                            <div>
                                                <p className="font-medium text-sm text-slate-900 dark:text-white">{appointment.clientName}</p>
                                                <div className="flex items-center space-x-2 text-xs text-slate-600 dark:text-slate-400">
                                                    <span>{appointment.date}</span>
                                                    <span>{appointment.time}</span>
                                                    {getMethodIcon(appointment.method)}
                                                </div>
                                            </div>
                                        </div>
                                        <Badge className={getStatusColor(appointment.status)} variant="outline">
                                            {getStatusLabel(appointment.status)}
                                        </Badge>
                                    </div>
                                </div>
                            ))}
                            {upcomingAppointments.length === 0 && (
                                <p className="text-slate-500 dark:text-slate-400 text-center py-4">Không có lịch hẹn sắp tới</p>
                            )}
                        </div>
                    </CardContent>
                </Card>
            </div>
            {/* Edit Appointment Dialog */}
            <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
                <DialogContent className="max-w-md">
                    <DialogHeader>
                        <DialogTitle>Chỉnh Sửa Lịch Hẹn</DialogTitle>
                    </DialogHeader>
                    <div className="space-y-4">
                        <Input
                            placeholder="Tên thành viên *"
                            value={newAppointment.clientName}
                            onChange={(e) => setNewAppointment({ ...newAppointment, clientName: e.target.value })}
                        />
                        <div className="grid grid-cols-2 gap-2">
                            <Input
                                type="date"
                                value={newAppointment.date}
                                onChange={(e) => setNewAppointment({ ...newAppointment, date: e.target.value })}
                            />
                            <Input
                                type="time"
                                value={newAppointment.time}
                                onChange={(e) => setNewAppointment({ ...newAppointment, time: e.target.value })}
                            />
                        </div>
                        <div className="grid grid-cols-2 gap-2">
                            <select
                                className="px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-md bg-white dark:bg-slate-800 text-slate-900 dark:text-white"
                                value={newAppointment.type}
                                onChange={(e) =>
                                    setNewAppointment({
                                        ...newAppointment,
                                        type: e.target.value as "individual" | "group" | "emergency",
                                    })
                                }
                            >
                                <option value="individual">Cá nhân</option>
                                <option value="group">Nhóm</option>
                                <option value="emergency">Khẩn cấp</option>
                            </select>
                            <select
                                className="px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-md bg-white dark:bg-slate-800 text-slate-900 dark:text-white"
                                value={newAppointment.method}
                                onChange={(e) =>
                                    setNewAppointment({
                                        ...newAppointment,
                                        method: e.target.value as "in-person" | "phone",
                                    })
                                }
                            >
                                <option value="phone">Điện thoại</option>
                                <option value="in-person">Trực tiếp</option>
                            </select>
                        </div>
                        {newAppointment.method === ("in-person" as const) && (
                            <Input
                                placeholder="Địa điểm"
                                value={newAppointment.location}
                                onChange={(e) => setNewAppointment({ ...newAppointment, location: e.target.value })}
                            />
                        )}
                        <Textarea
                            placeholder="Ghi chú"
                            value={newAppointment.notes}
                            onChange={(e) => setNewAppointment({ ...newAppointment, notes: e.target.value })}
                        />
                        <div className="flex space-x-2">
                            <Button onClick={handleUpdateAppointment} className="flex-1">
                                Cập Nhật
                            </Button>
                            <Button variant="outline" onClick={() => setIsEditDialogOpen(false)} className="flex-1">
                                Hủy
                            </Button>
                        </div>
                    </div>
                </DialogContent>
            </Dialog>

            {/* Delete Confirmation Dialog */}
            <Dialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
                <DialogContent className="max-w-md">
                    <DialogHeader>
                        <DialogTitle>Xác Nhận Xóa</DialogTitle>
                    </DialogHeader>
                    <div className="space-y-4">
                        <p className="text-slate-600 dark:text-slate-400">
                            Bạn có chắc chắn muốn xóa lịch hẹn với {appointmentToDelete?.clientName}?
                        </p>
                        <div className="flex space-x-2">
                            <Button variant="destructive" onClick={handleDeleteAppointment} className="flex-1">
                                Xóa
                            </Button>
                            <Button variant="outline" onClick={() => setIsDeleteDialogOpen(false)} className="flex-1">
                                Hủy
                            </Button>
                        </div>
                    </div>
                </DialogContent>
            </Dialog>
        </div>
    )
}
