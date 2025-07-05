import type { Coach } from "../types/booking.types"
import { CoachCard } from "../components/CoachCard"

interface CoachesTabProps {
    coaches: Coach[]
    userSubscription: "free" | "premium"
    onViewDetails: (coach: Coach) => void
    onBooking: (coach: Coach) => void
}

export function CoachesTab({ coaches, userSubscription, onViewDetails, onBooking }: CoachesTabProps) {
    return (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {coaches.map((coach) => (
                <CoachCard
                    key={coach.id}
                    coach={coach}
                    userSubscription={userSubscription}
                    onViewDetails={onViewDetails}
                    onBooking={onBooking}
                />
            ))}
        </div>
    )
}
