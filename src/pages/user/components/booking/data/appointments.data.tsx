import type { Appointment } from "../types/booking.types"

export const initialAppointments: Appointment[] = [
    {
        id: "1",
        coachId: "coach-001",
        coachName: "Huấn Luyện Viên Minh",
        coachAvatar: "/placeholder.svg?height=40&width=40",
        date: "2024-06-20",
        time: "14:00",
        duration: 60,
        type: "individual",
        method: "video",
        status: "confirmed",
        notes: "Tư vấn về kỹ thuật thở sâu và quản lý stress",
        price: 500000,
    },
    {
        id: "2",
        coachId: "coach-002",
        coachName: "Chuyên gia Lan",
        coachAvatar: "/placeholder.svg?height=40&width=40",
        date: "2024-06-18",
        time: "10:30",
        duration: 45,
        type: "individual",
        method: "phone",
        status: "completed",
        notes: "Theo dõi tiến độ tuần thứ 3",
        price: 400000,
    },
]
