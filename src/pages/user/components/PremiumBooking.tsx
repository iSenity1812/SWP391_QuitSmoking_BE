"use client"

import { useState } from "react"
import type { Coach, Appointment, BookingForm, PremiumBookingProps } from "./booking/types/booking.types"
import { coaches } from "./booking/data/coaches.data"
import { initialAppointments } from "./booking/data/appointments.data"

// Components
import { PremiumHeader } from "./booking/components/PremiumHeader"
import { QuickStats } from "./booking/components/QuickStats"
import { NavigationTabs } from "./booking/components/NavigationTabs"

// Tabs
import { CoachesTab } from "./booking/tabs/CoachesTab"
import { AppointmentsTab } from "./booking/tabs/AppointmentsTab"
import { HistoryTab } from "./booking/tabs/HistoryTab"

// Dialogs
import { SubscriptionModal } from "./booking/dialogs/SubscriptionModal"
import { CoachDetailDialog } from "./booking/dialogs/CoachDetailDialog"
import { BookingDialog } from "./booking/dialogs/BookingDialog"

export function PremiumBooking({ userSubscription = "free" }: PremiumBookingProps) {
    const [selectedCoach, setSelectedCoach] = useState<Coach | null>(null)
    const [isBookingDialogOpen, setIsBookingDialogOpen] = useState(false)
    const [isCoachDetailOpen, setIsCoachDetailOpen] = useState(false)
    const [isSubscriptionModalOpen, setIsSubscriptionModalOpen] = useState(false)
    const [activeTab, setActiveTab] = useState<"coaches" | "appointments" | "history">("coaches")

    const [bookingForm, setBookingForm] = useState<BookingForm>({
        date: "",
        time: "",
        duration: 60,
        type: "individual",
        method: "video",
        notes: "",
        location: "",
    })

    const [appointments, setAppointments] = useState<Appointment[]>(initialAppointments)

    const handleBookingClick = (coach: Coach) => {
        setSelectedCoach(coach)
        if (userSubscription === "free") {
            setIsSubscriptionModalOpen(true)
        } else {
            setIsBookingDialogOpen(true)
        }
    }

    const handleBookAppointment = () => {
        if (!selectedCoach || !bookingForm.date || !bookingForm.time) {
            alert("Vui lòng điền đầy đủ thông tin bắt buộc")
            return
        }

        const newAppointment: Appointment = {
            id: Date.now().toString(),
            coachId: selectedCoach.id,
            coachName: selectedCoach.name,
            coachAvatar: selectedCoach.avatar,
            date: bookingForm.date,
            time: bookingForm.time,
            duration: bookingForm.duration,
            type: bookingForm.type,
            method: bookingForm.method,
            status: "scheduled",
            notes: bookingForm.notes,
            location: bookingForm.location,
            price: selectedCoach.pricing[bookingForm.type],
        }

        setAppointments((prev) => [...prev, newAppointment])

        // Reset form
        setBookingForm({
            date: "",
            time: "",
            duration: 60,
            type: "individual",
            method: "video",
            notes: "",
            location: "",
        })

        setIsBookingDialogOpen(false)
        alert("Đã đặt lịch thành công! Chúng tôi sẽ liên hệ xác nhận trong vòng 24h.")
    }

    return (
        <div className="space-y-6">
            {/* Premium Header */}
            <PremiumHeader userSubscription={userSubscription} />

            {/* Quick Stats - Only show for premium users */}
            {userSubscription === "premium" && <QuickStats appointments={appointments} coachCount={coaches.length} />}

            {/* Navigation Tabs - Only show for premium users */}
            {userSubscription === "premium" && <NavigationTabs activeTab={activeTab} setActiveTab={setActiveTab} />}

            {/* Tab Content */}
            {activeTab === "coaches" && (
                <CoachesTab
                    coaches={coaches}
                    userSubscription={userSubscription}
                    onViewDetails={(coach) => {
                        setSelectedCoach(coach)
                        setIsCoachDetailOpen(true)
                    }}
                    onBooking={handleBookingClick}
                />
            )}

            {activeTab === "appointments" && userSubscription === "premium" && (
                <AppointmentsTab appointments={appointments} />
            )}

            {activeTab === "history" && userSubscription === "premium" && <HistoryTab appointments={appointments} />}

            {/* For free users, always show coaches */}
            {userSubscription === "free" && (
                <CoachesTab
                    coaches={coaches}
                    userSubscription={userSubscription}
                    onViewDetails={(coach) => {
                        setSelectedCoach(coach)
                        setIsCoachDetailOpen(true)
                    }}
                    onBooking={handleBookingClick}
                />
            )}

            {/* Dialogs */}
            <SubscriptionModal
                isOpen={isSubscriptionModalOpen}
                onClose={() => setIsSubscriptionModalOpen(false)}
                selectedCoach={selectedCoach}
            />

            <CoachDetailDialog
                isOpen={isCoachDetailOpen}
                onClose={() => setIsCoachDetailOpen(false)}
                selectedCoach={selectedCoach}
                onBooking={handleBookingClick}
            />

            <BookingDialog
                isOpen={isBookingDialogOpen}
                onClose={() => setIsBookingDialogOpen(false)}
                selectedCoach={selectedCoach}
                bookingForm={bookingForm}
                setBookingForm={setBookingForm}
                onBookAppointment={handleBookAppointment}
            />
        </div>
    )
}
