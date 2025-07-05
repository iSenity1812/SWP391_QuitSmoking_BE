"use client"

import { useState, useContext } from "react"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Button } from "@/components/ui/button"
import {
    MessageSquare,
    FileText,
    Trophy,
    Crown,
    Flag,
    Activity,
    Menu,
    X,
    ChevronLeft,
    ChevronRight,
    Sun,
    Moon,
    BarChart3,
} from "lucide-react"
import { useTheme } from "@/context/ThemeContext"
import { ContentDashboard } from "./components/ContentDashboard"
import { ReviewsManagement } from "./components/ReviewsManagement"
import { ContentLibrary } from "./components/ContentLibrary"
import { AchievementsManagement } from "./components/AchievementsManagement"
import { PremiumPrograms } from "./components/PremiumPrograms"
import { ContentReports } from "./components/ContentReports"
import { BlogManagement } from "./components/BlogManagement"
import { RoleBasedComponent } from '@/components/auth/RoleBasedComponent'
import { AuthContext } from '@/context/AuthContext'

export default function ContentAdminPage() {
    const [activeTab, setActiveTab] = useState("dashboard")
    const [sidebarCollapsed, setSidebarCollapsed] = useState(false)
    const [mobileMenuOpen, setMobileMenuOpen] = useState(false)
    const { theme, toggleTheme } = useTheme()
    const auth = useContext(AuthContext)

    const navItems = [
        { id: "dashboard", label: "Tổng Quan", icon: BarChart3 },
        { id: "reviews", label: "Đánh Giá", icon: MessageSquare },
        { id: "blogs", label: "Blog", icon: FileText },
        { id: "content", label: "Thư Viện", icon: FileText },
        { id: "achievements", label: "Thành Tựu", icon: Trophy },
        { id: "premium", label: "Premium", icon: Crown },
        { id: "reports", label: "Báo Cáo", icon: Flag },
    ]

    const renderContent = () => {
        switch (activeTab) {
            case "dashboard":
                return <ContentDashboard />
            case "reviews":
                return <ReviewsManagement />
            case "blogs":
                return <BlogManagement />
            case "content":
                return <ContentLibrary />
            case "achievements":
                return <AchievementsManagement />
            case "premium":
                return <PremiumPrograms />
            case "reports":
                return <ContentReports />
            default:
                return <ContentDashboard />
        }
    }

    return (
        <RoleBasedComponent allowedRoles={['CONTENT_ADMIN']} fallback={<div>Bạn không có quyền truy cập trang này.</div>}>
            <div className="min-h-screen bg-gradient-to-br from-slate-50 via-slate-100 to-slate-200 dark:from-slate-900 dark:via-slate-800 dark:to-slate-900 transition-colors duration-300">
                {/* Mobile Menu Overlay */}
                {mobileMenuOpen && (
                    <div className="fixed inset-0 bg-black/50 z-40 lg:hidden" onClick={() => setMobileMenuOpen(false)} />
                )}

                {/* Sidebar */}
                <div
                    className={`fixed left-0 top-0 h-full bg-white/95 dark:bg-slate-800/95 backdrop-blur-md border-r border-slate-200 dark:border-slate-700/50 z-50 transition-all duration-300 ${sidebarCollapsed ? "w-16" : "w-64"
                        } ${mobileMenuOpen ? "translate-x-0" : "-translate-x-full lg:translate-x-0"}`}
                >
                    {/* Sidebar Header */}
                    <div className="flex items-center justify-between p-4 border-b border-slate-200 dark:border-slate-700/50">
                        {!sidebarCollapsed && (
                            <div className="flex items-center space-x-3">
                                <div className="w-8 h-8 bg-gradient-to-r from-purple-500 to-pink-500 rounded-lg flex items-center justify-center">
                                    <span className="text-white font-bold text-sm">CM</span>
                                </div>
                                <div>
                                    <h2 className="text-slate-900 dark:text-white font-semibold">Content Admin</h2>
                                    <p className="text-slate-600 dark:text-slate-400 text-xs">Quản lý nội dung</p>
                                </div>
                            </div>
                        )}

                        {/* Collapse Button - Desktop */}
                        <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => setSidebarCollapsed(!sidebarCollapsed)}
                            className="hidden lg:flex text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white hover:bg-slate-100 dark:hover:bg-slate-700/50"
                        >
                            {sidebarCollapsed ? <ChevronRight size={16} /> : <ChevronLeft size={16} />}
                        </Button>

                        {/* Close Button - Mobile */}
                        <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => setMobileMenuOpen(false)}
                            className="lg:hidden text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white hover:bg-slate-100 dark:hover:bg-slate-700/50"
                        >
                            <X size={16} />
                        </Button>
                    </div>

                    {/* Navigation Items */}
                    <nav className="p-4 space-y-2">
                        {navItems.map((item) => {
                            const Icon = item.icon
                            const isActive = activeTab === item.id

                            return (
                                <button
                                    key={item.id}
                                    onClick={() => {
                                        setActiveTab(item.id)
                                        setMobileMenuOpen(false)
                                    }}
                                    className={`w-full flex items-center space-x-3 px-3 py-2.5 rounded-lg transition-all duration-200 ${isActive
                                        ? "bg-gradient-to-r from-purple-500/20 to-pink-500/20 text-slate-900 dark:text-white border border-purple-500/30"
                                        : "text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white hover:bg-slate-100 dark:hover:bg-slate-700/50"
                                        }`}
                                >
                                    <Icon size={20} className={isActive ? "text-purple-500" : ""} />
                                    {!sidebarCollapsed && <span className="font-medium">{item.label}</span>}
                                </button>
                            )
                        })}
                    </nav>

                    {/* Theme Toggle */}
                    <div className="px-4 mb-4">
                        <Button
                            onClick={toggleTheme}
                            variant="outline"
                            size={sidebarCollapsed ? "sm" : "default"}
                            className={`${sidebarCollapsed ? "w-8 h-8 p-0" : "w-full"} bg-slate-100 dark:bg-slate-700 border-slate-300 dark:border-slate-600 hover:bg-slate-200 dark:hover:bg-slate-600 text-slate-700 dark:text-slate-300`}
                        >
                            {theme === "light" ? (
                                <>
                                    <Moon size={16} />
                                    {!sidebarCollapsed && <span className="ml-2">Chế độ tối</span>}
                                </>
                            ) : (
                                <>
                                    <Sun size={16} />
                                    {!sidebarCollapsed && <span className="ml-2">Chế độ sáng</span>}
                                </>
                            )}
                        </Button>
                    </div>

                    {/* User Profile - Bottom */}
                    <div className="absolute bottom-4 left-4 right-4">
                        <div
                            className={`flex items-center space-x-3 p-3 bg-slate-100 dark:bg-slate-700/50 rounded-lg ${sidebarCollapsed ? "justify-center" : ""}`}
                        >
                            <Avatar className="h-8 w-8">
                                <AvatarImage src="/placeholder.svg?height=32&width=32" />
                                <AvatarFallback className="bg-purple-500 text-white">CM</AvatarFallback>
                            </Avatar>
                            {!sidebarCollapsed && (
                                <div className="flex-1 min-w-0">
                                    <p className="text-slate-900 dark:text-white text-sm font-medium truncate">Content Manager</p>
                                    <p className="text-slate-600 dark:text-slate-400 text-xs truncate">content@quitsmoking.com</p>
                                </div>
                            )}
                            {/* Nút Logout */}
                            {!sidebarCollapsed && (
                                <Button
                                    variant="outline"
                                    size="sm"
                                    className="ml-2 text-red-600 border-red-300 hover:bg-red-50 dark:hover:bg-red-900/20"
                                    onClick={() => auth?.logout()}
                                >
                                    Đăng xuất
                                </Button>
                            )}
                        </div>
                    </div>
                </div>

                {/* Main Content */}
                <div className={`transition-all duration-300 ${sidebarCollapsed ? "lg:ml-16" : "lg:ml-64"}`}>
                    {/* Top Header */}
                    <div className="bg-white/95 dark:bg-slate-800/95 backdrop-blur-md border-b border-slate-200 dark:border-slate-700/50 p-4">
                        <div className="flex items-center justify-between">
                            <div className="flex items-center space-x-4">
                                {/* Mobile Menu Button */}
                                <Button
                                    variant="ghost"
                                    size="sm"
                                    onClick={() => setMobileMenuOpen(true)}
                                    className="lg:hidden text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white hover:bg-slate-100 dark:hover:bg-slate-700/50"
                                >
                                    <Menu size={20} />
                                </Button>

                                <div>
                                    <h1 className="text-2xl font-bold text-slate-900 dark:text-white">Quản Lý Nội Dung</h1>
                                    <p className="text-slate-600 dark:text-slate-400 text-sm">
                                        Quản lý đánh giá, blog, thành tựu và nội dung hệ thống
                                    </p>
                                </div>
                            </div>

                            <div className="flex items-center space-x-4">
                                {/* Desktop Theme Toggle */}
                                <Button
                                    onClick={toggleTheme}
                                    variant="outline"
                                    size="sm"
                                    className="hidden lg:flex bg-slate-100 dark:bg-slate-700 border-slate-300 dark:border-slate-600 hover:bg-slate-200 dark:hover:bg-slate-600 text-slate-700 dark:text-slate-300"
                                >
                                    {theme === "light" ? (
                                        <>
                                            <Moon size={16} />
                                            <span className="ml-2">Tối</span>
                                        </>
                                    ) : (
                                        <>
                                            <Sun size={16} />
                                            <span className="ml-2">Sáng</span>
                                        </>
                                    )}
                                </Button>

                                <Badge
                                    variant="outline"
                                    className="px-3 py-1 border-purple-500/30 text-purple-600 dark:text-purple-400 bg-purple-50 dark:bg-purple-500/10"
                                >
                                    <Activity className="w-4 h-4 mr-2" />
                                    Content System
                                </Badge>
                            </div>
                        </div>
                    </div>

                    {/* Main Content Area */}
                    <div className="p-6">
                        <div className="space-y-6">{renderContent()}</div>
                    </div>
                </div>
            </div>
        </RoleBasedComponent>
    )
}
