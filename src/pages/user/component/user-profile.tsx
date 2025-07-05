"use client"

import { useState, useEffect, useContext } from "react"
import { AuthContext } from "@/context/AuthContext"
import UserProfileHeader from "../components/UserProfileHeader"
import { UserProfileSidebar } from "../components/UserProfileSidebar"
import { StatsCards } from "../components/StatsCards"
import { QuitStatsSection } from "../components/QuitStatsSection"
import { AchievementNotificationModal } from "../components/AchievementNotificationModal"
import { OverviewTab } from "../components/tabs/OverviewTab"
import { ProgressTab } from "../components/tabs/ProgressTab"
import { AchievementsTab } from "../components/tabs/AchievementsTab"
import { HealthTab } from "../components/tabs/HealthTab"
import { SocialTab } from "../components/tabs/SocialTab"
import { BookingTab } from "../components/tabs/BookingtTab"
import CertificationTab from "../components/tabs/CertificationTab"
import type { AchievementNotification, User } from "../types/user-types"
import { userService } from "@/services/userService"

export default function UserProfile() {
    const [activeTab, setActiveTab] = useState("social")
    const [sidebarOpen, setSidebarOpen] = useState(false)
    const [achievementNotification, setAchievementNotification] = useState<AchievementNotification>({
        show: false,
        achievement: null,
    })
    const [quitStats, setQuitStats] = useState<{cigarettesAvoided: number, moneySaved: number}>({cigarettesAvoided: 0, moneySaved: 0})

    const auth = useContext(AuthContext)
    const user: User = auth?.user
        ? {
            userId: auth.user.userId,
            name: auth.user.username,
            email: auth.user.email,
            avatar: auth.user.profilePicture || '',
            joinDate: '',
            daysSmokeFreee: 0,
            cigarettesAvoided: quitStats.cigarettesAvoided,
            moneySaved: quitStats.moneySaved.toLocaleString('vi-VN'),
            healthImprovement: 0,
            level: '',
            streak: 0,
            achievements: [],
            achievementCategories: [],
            nextMilestone: { name: '', daysLeft: 0, reward: '' },
            healthBenefits: [],
            weeklyProgress: [],
            friends: [],
            recentActivities: [],
            subscription: undefined,
        }
        : {
            userId: '',
            name: '',
            email: '',
            avatar: '',
            joinDate: '',
            daysSmokeFreee: 0,
            cigarettesAvoided: 0,
            moneySaved: '0',
            healthImprovement: 0,
            level: '',
            streak: 0,
            achievements: [],
            achievementCategories: [],
            nextMilestone: { name: '', daysLeft: 0, reward: '' },
            healthBenefits: [],
            weeklyProgress: [],
            friends: [],
            recentActivities: [],
            subscription: undefined,
        }

    useEffect(() => {
        userService.getQuitStats().then(res => {
            if (res.data) {
                setQuitStats({
                    cigarettesAvoided: res.data.cigarettesAvoided,
                    moneySaved: res.data.moneySaved
                })
            }
        })
    }, [])

    const handleTestAchievement = () => {
        const randomAchievement = user.achievements[Math.floor(Math.random() * user.achievements.length)]
        setAchievementNotification({
            show: true,
            achievement: randomAchievement,
        })
    }

    const handleCloseNotification = () => {
        setAchievementNotification({ show: false, achievement: null })
    }

    const handleViewAchievements = () => {
        setActiveTab("achievements")
        handleCloseNotification()
    }

    const handleSidebarClose = () => {
        setSidebarOpen(false)
    }

    const handleTabChange = (tab: string) => {
        setActiveTab(tab)
    }

    // Close sidebar when clicking outside on mobile
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (sidebarOpen && window.innerWidth < 768) {
                const sidebar = document.getElementById("sidebar")
                if (sidebar && !sidebar.contains(event.target as Node)) {
                    setSidebarOpen(false)
                }
            }
        }

        document.addEventListener("mousedown", handleClickOutside)
        return () => document.removeEventListener("mousedown", handleClickOutside)
    }, [sidebarOpen])

    const renderTabContent = () => {
        switch (activeTab) {
            case "overview":
                return <OverviewTab user={user} onTestAchievement={handleTestAchievement} />
            case "progress":
                return <ProgressTab user={user} />
            case "achievements":
                return <AchievementsTab user={user} />
            case "health":
                return <HealthTab />
            case "social":
                return <SocialTab user={user} />
            case "booking":
                return <BookingTab user={user} />
            case "certification":
                return <CertificationTab />
            default:
                return <OverviewTab user={user} onTestAchievement={handleTestAchievement} />
        }
    }

    return (
        <div className="min-h-screen bg-slate-50 dark:bg-slate-900">
            <UserProfileHeader user={user} />

            <div className="flex">
                <div id="sidebar">
                    <UserProfileSidebar
                        user={user}
                        activeTab={activeTab}
                        sidebarOpen={sidebarOpen}
                        onTabChange={handleTabChange}
                        onSidebarClose={handleSidebarClose}
                    />
                </div>

                <div className="flex-1 p-6">
                    <div className="max-w-7xl mx-auto">
                        <StatsCards user={user} />
                        {renderTabContent()}
                    </div>
                </div>
            </div>

            {/* Overlay for mobile sidebar */}
            {sidebarOpen && <div className="fixed inset-0 bg-black/50 z-40 md:hidden" onClick={handleSidebarClose} />}

            <AchievementNotificationModal
                notification={achievementNotification}
                onClose={handleCloseNotification}
                onViewAchievements={handleViewAchievements}
            />
        </div>
    )
}
