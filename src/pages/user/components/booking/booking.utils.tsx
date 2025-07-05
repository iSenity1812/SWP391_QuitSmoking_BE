import { Phone, Video, MapPin, Calendar } from "lucide-react"

export const getStatusColor = (status: string) => {
    switch (status) {
        case "confirmed":
            return "bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400"
        case "scheduled":
            return "bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400"
        case "completed":
            return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
        case "cancelled":
            return "bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400"
        default:
            return "bg-gray-100 text-gray-800"
    }
}

export const getStatusLabel = (status: string) => {
    switch (status) {
        case "confirmed":
            return "Đã xác nhận"
        case "scheduled":
            return "Đã lên lịch"
        case "completed":
            return "Hoàn thành"
        case "cancelled":
            return "Đã hủy"
        default:
            return status
    }
}

export const getMethodIcon = (method: string) => {
    switch (method) {
        case "phone":
            return Phone
        case "video":
            return Video
        case "in-person":
            return MapPin
        default:
            return Calendar
    }
}
