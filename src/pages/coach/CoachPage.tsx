"use client"

import CoachDashboard from "./CoachDashBoard"
import { AppointmentProvider } from "./AppointmentContext"
import { useContext } from "react";
import { AuthContext } from "@/context/AuthContext";

export default function CoachPage() {
    const auth = useContext(AuthContext);

    return (
        <AppointmentProvider>
            <div className="coach-page-root">
                <CoachDashboard logout={auth?.logout} />
            </div>
        </AppointmentProvider>
    )
}
