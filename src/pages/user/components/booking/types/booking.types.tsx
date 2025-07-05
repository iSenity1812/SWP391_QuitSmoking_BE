export interface Coach {
    id: string
    name: string
    title: string
    avatar: string
    rating: number
    reviewCount: number
    experience: number
    specializations: string[]
    languages: string[]
    successRate: number
    totalClients: number
    bio: string
    availability: {
        [key: string]: string[]
    }
    pricing: {
        individual: number
        group: number
        emergency: number
    }
}

export interface Appointment {
    id: string
    coachId: string
    coachName: string
    coachAvatar: string
    date: string
    time: string
    duration: number
    type: "individual" | "group" | "emergency"
    method: "phone" | "video" | "in-person"
    status: "scheduled" | "confirmed" | "completed" | "cancelled"
    notes: string
    location?: string
    price: number
}

export interface BookingForm {
    date: string
    time: string
    duration: number
    type: "individual" | "group" | "emergency"
    method: "phone" | "video" | "in-person"
    notes: string
    location: string
}

export interface PremiumBookingProps {
    userSubscription?: "free" | "premium"
}
