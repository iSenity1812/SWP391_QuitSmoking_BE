"use client"

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Button } from "@/components/ui/button"
import {
    Share2,
    LogOut,
    X,
    LayoutDashboard,
    BarChartIcon as ChartBar,
    Award,
    Heart,
    Users,
    Calendar,
    Crown,
    Activity,
    TrendingUp,
} from "lucide-react"
import { Link } from "react-router-dom"
import type { User } from "../types/user-types"
import type React from "react"

interface UserProfileSidebarProps {
    user: User
    activeTab: string
    sidebarOpen: boolean
    onTabChange: (tab: string) => void
    onSidebarClose: () => void
}

const sidebarItems: Array<{
    id: string
    label: string
    icon: React.ComponentType<{ className?: string }>
    premium?: boolean
}> = [
        { id: "overview", label: "Tổng quan", icon: LayoutDashboard },
        { id: "progress", label: "Tiến trình", icon: ChartBar },
        { id: "achievements", label: "Thành tựu", icon: Award },
        { id: "health", label: "Sức khỏe", icon: Heart },
        { id: "social", label: "Cộng đồng", icon: Users },
        { id: "booking", label: "Đặt Lịch Chuyên Gia", icon: Calendar, premium: true },
    ]

export function UserProfileSidebar({
    user,
    activeTab,
    sidebarOpen,
    onTabChange,
    onSidebarClose,
}: UserProfileSidebarProps) {
    return (
        <div
            className={`
        fixed md:sticky top-0 left-0 z-50 md:z-0 h-screen md:h-auto w-64 md:w-64 
        bg-white dark:bg-slate-900 shadow-lg md:shadow-none border-r border-slate-200 dark:border-slate-700
        transform transition-transform duration-200 ease-in-out md:transform-none
        ${sidebarOpen ? "translate-x-0" : "-translate-x-full md:translate-x-0"}
      `}
        >
            <div className="flex items-center justify-between p-4 md:hidden">
                <h2 className="font-bold text-lg text-slate-900 dark:text-white">Menu</h2>
                <button
                    className="p-2 rounded-md text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800"
                    onClick={onSidebarClose}
                >
                    <X className="h-5 w-5" />
                </button>
            </div>

            <div className="p-4">
                <div className="mb-6">
                    <div className="flex items-center gap-3 mb-4">
                        <Avatar className="h-10 w-10">
                            <AvatarImage src={user.avatar || "/placeholder.svg"} alt={user.name} />
                            <AvatarFallback className="bg-emerald-500 text-white">{user.name.charAt(0)}</AvatarFallback>
                        </Avatar>
                        <div>
                            <p className="font-semibold text-slate-900 dark:text-white">{user.name}</p>
                            <p className="text-xs text-slate-500 dark:text-slate-400">{user.email}</p>
                        </div>
                    </div>
                </div>

                <div className="space-y-1">
                    {sidebarItems.map((item) => (
                        <button
                            key={item.id}
                            className={`
            w-full flex items-center gap-3 px-3 py-2 rounded-lg text-left
            ${activeTab === item.id
                                    ? "bg-emerald-100 dark:bg-emerald-900/30 text-emerald-700 dark:text-emerald-400 font-medium"
                                    : "text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800"
                                }
        `}
                            onClick={() => {
                                onTabChange(item.id)
                                onSidebarClose()
                            }}
                        >
                            <item.icon
                                className={`h-5 w-5 ${activeTab === item.id ? "text-emerald-600 dark:text-emerald-400" : ""}`}
                            />
                            <span className="flex-1">{item.label}</span>
                            {item.premium && <Crown className="h-4 w-4 text-amber-500" />}
                        </button>
                    ))}
                </div>

                <div className="mt-8 pt-6 border-t border-slate-200 dark:border-slate-700">
                    <Button variant="outline" className="w-full justify-start" size="sm">
                        <Share2 className="h-4 w-4 mr-2" />
                        Chia sẻ tiến trình
                    </Button>
                    <Button
                        variant="outline"
                        className="w-full justify-start mt-2 text-rose-600 dark:text-rose-400"
                        size="sm"
                        onClick={() => {
                            localStorage.removeItem("user_session")
                            localStorage.removeItem("auth_token")
                            window.location.href = "/"
                        }}
                    >
                        <LogOut className="h-4 w-4 mr-2" />
                        Đăng xuất
                    </Button>
                </div>
            </div>
        </div>
    )
}
