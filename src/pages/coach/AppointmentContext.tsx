"use client"

import { createContext, useContext, useState, type ReactNode } from "react"

export interface Appointment {
    id: number
    clientName: string
    clientAvatar: string
    date: string
    time: string
    duration: number
    type: "individual" | "group" | "emergency"
    method: "in-person" | "phone"
    status: "scheduled" | "confirmed" | "completed" | "cancelled" | "no-show"
    notes: string
    location?: string
    customerId: number
}

interface AppointmentContextType {
    appointments: Appointment[]
    addAppointment: (appointment: Omit<Appointment, "id">) => void
    updateAppointment: (id: number, appointment: Partial<Appointment>) => void
    deleteAppointment: (id: number) => void
    getTodayAppointments: () => Appointment[]
    getUpcomingAppointments: () => Appointment[]
    fetchAppointments: () => Promise<Appointment[]>;
}

const AppointmentContext = createContext<AppointmentContextType | undefined>(undefined)

export function AppointmentProvider({ children }: { children: ReactNode }) {
    const [appointments, setAppointments] = useState<Appointment[]>([
        {
            id: 1,
            clientName: "Nguyễn Văn An",
            clientAvatar: "/placeholder.svg?height=40&width=40",
            date: "2024-06-14",
            time: "09:00",
            duration: 60,
            type: "individual",
            method: "phone",
            status: "confirmed",
            notes: "Tư vấn về kỹ thuật thở sâu và quản lý stress",
            location: "",
            customerId: 1,
        },
        {
            id: 2,
            clientName: "Trần Thị Bình",
            clientAvatar: "/placeholder.svg?height=40&width=40",
            date: "2024-06-14",
            time: "10:30",
            duration: 45,
            type: "individual",
            method: "phone",
            status: "scheduled",
            notes: "Theo dõi tiến độ tuần thứ 2",
            location: "",
            customerId: 2,
        },
        {
            id: 3,
            clientName: "Nhóm Hỗ Trợ Tuần 1",
            clientAvatar: "/placeholder.svg?height=40&width=40",
            date: "2024-06-14",
            time: "14:00",
            duration: 90,
            type: "group",
            method: "phone",
            status: "confirmed",
            notes: "Buổi chia sẻ kinh nghiệm và động viên lẫn nhau",
            location: "",
            customerId: 0,
        },
        {
            id: 4,
            clientName: "Lê Văn Cường",
            clientAvatar: "/placeholder.svg?height=40&width=40",
            date: "2024-06-15",
            time: "09:30",
            duration: 30,
            type: "individual",
            method: "in-person",
            status: "scheduled",
            notes: "Tư vấn duy trì sau khi hoàn thành chương trình",
            location: "Phòng tư vấn A - Tầng 2",
            customerId: 3,
        },
    ])

    const addAppointment = (appointment: Omit<Appointment, "id">) => {
        const newAppointment = {
            ...appointment,
            id: Math.max(...appointments.map((a) => a.id), 0) + 1,
        }
        setAppointments((prev) => [...prev, newAppointment])
    }

    const updateAppointment = (id: number, updatedAppointment: Partial<Appointment>) => {
        setAppointments((prev) =>
            prev.map((appointment) => (appointment.id === id ? { ...appointment, ...updatedAppointment } : appointment)),
        )
    }

    const deleteAppointment = (id: number) => {
        setAppointments((prev) => prev.filter((appointment) => appointment.id !== id))
    }

    const getTodayAppointments = () => {
        const today = new Date()
        const year = today.getFullYear()
        const month = String(today.getMonth() + 1).padStart(2, "0")
        const day = String(today.getDate()).padStart(2, "0")
        const todayString = `${year}-${month}-${day}`

        return appointments.filter((appointment) => appointment.date === todayString)
    }

    const getUpcomingAppointments = () => {
        const today = new Date()
        const year = today.getFullYear()
        const month = String(today.getMonth() + 1).padStart(2, "0")
        const day = String(today.getDate()).padStart(2, "0")
        const todayString = `${year}-${month}-${day}`

        return appointments.filter((appointment) => appointment.date > todayString)
    }

    return (
        <AppointmentContext.Provider
            value={{
                appointments,
                addAppointment,
                updateAppointment,
                deleteAppointment,
                getTodayAppointments,
                getUpcomingAppointments,
                fetchAppointments: async () => {
                    // Simulate fetching appointments from an API
                    return new Promise((resolve) => {
                        setTimeout(() => {
                            resolve(appointments)
                        }, 1000)
                    })
                },
            }}
        >
            {children}
        </AppointmentContext.Provider>
    )
}

export function useAppointments() {
    const context = useContext(AppointmentContext)
    if (context === undefined) {
        throw new Error("useAppointments must be used within an AppointmentProvider")
    }
    return context
}
