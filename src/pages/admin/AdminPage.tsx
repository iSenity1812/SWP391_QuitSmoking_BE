"use client"

import { useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Button } from "@/components/ui/button"
import {
    Users,
    Calendar,
    TrendingUp,
    MessageSquare,
    BarChart3,
    Target,
    Heart,
    DollarSign,
    Activity,
    Menu,
    X,
    ChevronLeft,
    ChevronRight,
    Sun,
    Moon,
    UserCheck,
} from "lucide-react"
import { AdminDashboard } from "./component/AdminDashBoard"
import { UserManagement } from "./staff/UserManagement"
import { PlanManagement } from "./staff/PlanManagement"
import { ContentManagement } from "./component/ContentManagement"
import { AnalyticsSection } from "./staff/AnalyticsSection"
import { useTheme } from "@/context/ThemeContext"
import { CoachManagement } from "./staff/CoachManagement"
import { motion, AnimatePresence } from "framer-motion"

export default function AdminPage() {
    const [activeTab, setActiveTab] = useState("dashboard")
    const [sidebarCollapsed, setSidebarCollapsed] = useState(false)
    const [mobileMenuOpen, setMobileMenuOpen] = useState(false)
    const { theme, toggleTheme } = useTheme()

    const navItems = [
        { id: "dashboard", label: "Tổng Quan", icon: BarChart3 },
        { id: "users", label: "Người Dùng", icon: Users },
        { id: "coaches", label: "Coach", icon: UserCheck },
        { id: "plans", label: "Kế Hoạch", icon: Calendar },
        { id: "content", label: "Quản Lý Blog", icon: MessageSquare },
        { id: "analytics", label: "Phân Tích", icon: TrendingUp },
    ]

    const renderContent = () => {
        switch (activeTab) {
            case "dashboard":
                return <AdminDashboard />
            case "users":
                return <UserManagement />
            case "coaches":
                return <CoachManagement />
            case "plans":
                return <PlanManagement />
            case "content":
                return <ContentManagement />
            case "analytics":
                return <AnalyticsSection />
            default:
                return <AdminDashboard />
        }
    }

    // Animation variants
    const sidebarVariants = {
        expanded: { width: 256 },
        collapsed: { width: 64 },
    }

    const contentVariants = {
        expanded: { marginLeft: 256 },
        collapsed: { marginLeft: 64 },
    }

    const mobileMenuVariants = {
        open: { x: 0 },
        closed: { x: "-100%" },
    }

    const cardVariants = {
        hidden: { opacity: 0, y: 20 },
        visible: (i: number) => ({
            opacity: 1,
            y: 0,
            transition: {
                delay: i * 0.1,
                duration: 0.5,
                ease: "easeOut",
            },
        }),
    }

    const navItemVariants = {
        inactive: {
            scale: 1,
            backgroundColor: "transparent",
        },
        active: {
            scale: 1.02,
            backgroundColor: "rgba(34, 197, 94, 0.1)",
            transition: { duration: 0.2 },
        },
        hover: {
            scale: 1.05,
            transition: { duration: 0.2 },
        },
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-slate-100 to-slate-200 dark:from-slate-900 dark:via-slate-800 dark:to-slate-900 transition-colors duration-300">
            {/* Mobile Menu Overlay */}
            <AnimatePresence>
                {mobileMenuOpen && (
                    <motion.div
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        transition={{ duration: 0.2 }}
                        className="fixed inset-0 bg-black/50 z-40 lg:hidden"
                        onClick={() => setMobileMenuOpen(false)}
                    />
                )}
            </AnimatePresence>

            {/* Sidebar */}
            <motion.div
                variants={sidebarVariants}
                animate={sidebarCollapsed ? "collapsed" : "expanded"}
                transition={{ duration: 0.3, ease: "easeInOut" }}
                className={`fixed left-0 top-0 h-full bg-white/95 dark:bg-slate-800/95 backdrop-blur-md border-r border-slate-200 dark:border-slate-700/50 z-50 lg:translate-x-0 ${mobileMenuOpen ? "translate-x-0" : "-translate-x-full"
                    }`}
            >
                <motion.div
                    variants={mobileMenuVariants}
                    animate={mobileMenuOpen ? "open" : "closed"}
                    transition={{ duration: 0.3, ease: "easeInOut" }}
                    className="lg:hidden"
                />

                {/* Sidebar Header */}
                <motion.div
                    className="flex items-center justify-between p-4 border-b border-slate-200 dark:border-slate-700/50"
                    initial={{ opacity: 0, y: -20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.5 }}
                >
                    <AnimatePresence>
                        {!sidebarCollapsed && (
                            <motion.div
                                initial={{ opacity: 0, x: -20 }}
                                animate={{ opacity: 1, x: 0 }}
                                exit={{ opacity: 0, x: -20 }}
                                transition={{ duration: 0.3 }}
                                className="flex items-center space-x-3"
                            >
                                <motion.div
                                    className="w-8 h-8 bg-gradient-to-r from-green-500 to-blue-500 rounded-lg flex items-center justify-center"
                                    whileHover={{ scale: 1.1, rotate: 5 }}
                                    transition={{ duration: 0.2 }}
                                >
                                    <span className="text-white font-bold text-sm">QS</span>
                                </motion.div>
                                <div>
                                    <h2 className="text-slate-900 dark:text-white font-semibold">Admin Panel</h2>
                                    <p className="text-slate-600 dark:text-slate-400 text-xs">Quản lý hệ thống</p>
                                </div>
                            </motion.div>
                        )}
                    </AnimatePresence>

                    {/* Collapse Button - Desktop */}
                    <motion.div whileHover={{ scale: 1.1 }} whileTap={{ scale: 0.95 }}>
                        <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => setSidebarCollapsed(!sidebarCollapsed)}
                            className="hidden lg:flex text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white hover:bg-slate-100 dark:hover:bg-slate-700/50"
                        >
                            <motion.div animate={{ rotate: sidebarCollapsed ? 180 : 0 }} transition={{ duration: 0.3 }}>
                                {sidebarCollapsed ? <ChevronRight size={16} /> : <ChevronLeft size={16} />}
                            </motion.div>
                        </Button>
                    </motion.div>

                    {/* Close Button - Mobile */}
                    <motion.div whileHover={{ scale: 1.1 }} whileTap={{ scale: 0.95 }}>
                        <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => setMobileMenuOpen(false)}
                            className="lg:hidden text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white hover:bg-slate-100 dark:hover:bg-slate-700/50"
                        >
                            <X size={16} />
                        </Button>
                    </motion.div>
                </motion.div>

                {/* Navigation Items */}
                <nav className="p-4 space-y-2">
                    {navItems.map((item) => {
                        const Icon = item.icon
                        const isActive = activeTab === item.id

                        return (
                            <motion.button
                                key={item.id}
                                variants={navItemVariants}
                                initial="inactive"
                                animate={isActive ? "active" : "inactive"}
                                whileHover="hover"
                                whileTap={{ scale: 0.98 }}
                                onClick={() => {
                                    setActiveTab(item.id)
                                    setMobileMenuOpen(false)
                                }}
                                className={`w-full flex items-center space-x-3 px-3 py-2.5 rounded-lg transition-all duration-200 ${isActive
                                    ? "bg-gradient-to-r from-green-500/20 to-blue-500/20 text-slate-900 dark:text-white border border-green-500/30"
                                    : "text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white hover:bg-slate-100 dark:hover:bg-slate-700/50"
                                    }`}
                            >
                                <motion.div
                                    animate={{
                                        color: isActive ? "#22c55e" : "currentColor",
                                        scale: isActive ? 1.1 : 1,
                                    }}
                                    transition={{ duration: 0.2 }}
                                >
                                    <Icon size={20} />
                                </motion.div>
                                <AnimatePresence>
                                    {!sidebarCollapsed && (
                                        <motion.span
                                            initial={{ opacity: 0, x: -10 }}
                                            animate={{ opacity: 1, x: 0 }}
                                            exit={{ opacity: 0, x: -10 }}
                                            transition={{ duration: 0.2 }}
                                            className="font-medium"
                                        >
                                            {item.label}
                                        </motion.span>
                                    )}
                                </AnimatePresence>
                            </motion.button>
                        )
                    })}
                </nav>

                {/* Theme Toggle */}
                <motion.div
                    className="px-4 mb-4"
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.5, delay: 0.3 }}
                >
                    <motion.div whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }}>
                        <Button
                            onClick={toggleTheme}
                            variant="outline"
                            size={sidebarCollapsed ? "sm" : "default"}
                            className={`${sidebarCollapsed ? "w-8 h-8 p-0" : "w-full"} bg-slate-100 dark:bg-slate-700 border-slate-300 dark:border-slate-600 hover:bg-slate-200 dark:hover:bg-slate-600 text-slate-700 dark:text-slate-300`}
                        >
                            <motion.div animate={{ rotate: theme === "light" ? 0 : 180 }} transition={{ duration: 0.3 }}>
                                {theme === "light" ? <Moon size={16} /> : <Sun size={16} />}
                            </motion.div>
                            <AnimatePresence>
                                {!sidebarCollapsed && (
                                    <motion.span
                                        initial={{ opacity: 0, x: -10 }}
                                        animate={{ opacity: 1, x: 0 }}
                                        exit={{ opacity: 0, x: -10 }}
                                        transition={{ duration: 0.2 }}
                                        className="ml-2"
                                    >
                                        {theme === "light" ? "Chế độ tối" : "Chế độ sáng"}
                                    </motion.span>
                                )}
                            </AnimatePresence>
                        </Button>
                    </motion.div>
                </motion.div>

                {/* User Profile - Bottom */}
                <motion.div
                    className="absolute bottom-4 left-4 right-4"
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.5, delay: 0.4 }}
                >
                    <motion.div
                        whileHover={{ scale: 1.02 }}
                        className={`flex items-center space-x-3 p-3 bg-slate-100 dark:bg-slate-700/50 rounded-lg ${sidebarCollapsed ? "justify-center" : ""}`}
                    >
                        <motion.div whileHover={{ scale: 1.1 }}>
                            <Avatar className="h-8 w-8">
                                <AvatarImage src="/placeholder.svg?height=32&width=32" />
                                <AvatarFallback className="bg-green-500 text-white">QT</AvatarFallback>
                            </Avatar>
                        </motion.div>
                        <AnimatePresence>
                            {!sidebarCollapsed && (
                                <motion.div
                                    initial={{ opacity: 0, x: -10 }}
                                    animate={{ opacity: 1, x: 0 }}
                                    exit={{ opacity: 0, x: -10 }}
                                    transition={{ duration: 0.2 }}
                                    className="flex-1 min-w-0"
                                >
                                    <p className="text-slate-900 dark:text-white text-sm font-medium truncate">Admin User</p>
                                    <p className="text-slate-600 dark:text-slate-400 text-xs truncate">admin@quitsmoking.com</p>
                                </motion.div>
                            )}
                        </AnimatePresence>
                    </motion.div>
                </motion.div>
            </motion.div>

            {/* Main Content */}
            <motion.div
                variants={contentVariants}
                animate={sidebarCollapsed ? "collapsed" : "expanded"}
                transition={{ duration: 0.3, ease: "easeInOut" }}
                className="transition-all duration-300 lg:ml-64"
            >
                {/* Top Header */}
                <motion.div
                    className="bg-white/95 dark:bg-slate-800/95 backdrop-blur-md border-b border-slate-200 dark:border-slate-700/50 p-4"
                    initial={{ opacity: 0, y: -20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.5 }}
                >
                    <div className="flex items-center justify-between">
                        <div className="flex items-center space-x-4">
                            {/* Mobile Menu Button */}
                            <motion.div whileHover={{ scale: 1.1 }} whileTap={{ scale: 0.95 }}>
                                <Button
                                    variant="ghost"
                                    size="sm"
                                    onClick={() => setMobileMenuOpen(true)}
                                    className="lg:hidden text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white hover:bg-slate-100 dark:hover:bg-slate-700/50"
                                >
                                    <Menu size={20} />
                                </Button>
                            </motion.div>

                            <motion.div
                                initial={{ opacity: 0, x: -20 }}
                                animate={{ opacity: 1, x: 0 }}
                                transition={{ duration: 0.5, delay: 0.1 }}
                            >
                                <h1 className="text-2xl font-bold text-slate-900 dark:text-white">Quản lý nền tảng cai thuốc lá</h1>
                                <p className="text-slate-600 dark:text-slate-400 text-sm">
                                    Bảng điều khiển quản trị toàn diện - Người dùng, Nội dung & Hệ thống
                                </p>
                            </motion.div>
                        </div>

                        <motion.div
                            className="flex items-center space-x-4"
                            initial={{ opacity: 0, x: 20 }}
                            animate={{ opacity: 1, x: 0 }}
                            transition={{ duration: 0.5, delay: 0.2 }}
                        >
                            {/* Desktop Theme Toggle */}
                            <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                                <Button
                                    onClick={toggleTheme}
                                    variant="outline"
                                    size="sm"
                                    className="hidden lg:flex bg-slate-100 dark:bg-slate-700 border-slate-300 dark:border-slate-600 hover:bg-slate-200 dark:hover:bg-slate-600 text-slate-700 dark:text-slate-300"
                                >
                                    <motion.div animate={{ rotate: theme === "light" ? 0 : 180 }} transition={{ duration: 0.3 }}>
                                        {theme === "light" ? <Moon size={16} /> : <Sun size={16} />}
                                    </motion.div>
                                    <span className="ml-2">{theme === "light" ? "Tối" : "Sáng"}</span>
                                </Button>
                            </motion.div>

                            <motion.div
                                whileHover={{ scale: 1.05 }}
                                animate={{
                                    boxShadow: "0 0 20px rgba(34, 197, 94, 0.3)",
                                }}
                                transition={{ duration: 2, repeat: Number.POSITIVE_INFINITY, repeatType: "reverse" }}
                            >
                                <Badge
                                    variant="outline"
                                    className="px-3 py-1 border-green-500/30 text-green-600 dark:text-green-400 bg-green-50 dark:bg-green-500/10"
                                >
                                    <motion.div
                                        animate={{ scale: [1, 1.2, 1] }}
                                        transition={{ duration: 2, repeat: Number.POSITIVE_INFINITY }}
                                    >
                                        <Activity className="w-4 h-4 mr-2" />
                                    </motion.div>
                                    Hệ Thống Hoạt Động
                                </Badge>
                            </motion.div>
                        </motion.div>
                    </div>
                </motion.div>

                {/* Main Content Area */}
                <motion.div
                    className="p-6"
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    transition={{ duration: 0.5, delay: 0.3 }}
                >
                    {activeTab === "dashboard" && (
                        <motion.div
                            className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8"
                            initial="hidden"
                            animate="visible"
                        >
                            {[
                                { title: "Tổng Người Dùng", value: "2,847", change: "+12%", icon: Users, color: "blue" },
                                { title: "Kế Hoạch Đang Hoạt Động", value: "1,234", change: "+8%", icon: Target, color: "green" },
                                { title: "Tỷ Lệ Thành Công", value: "73%", change: "+5%", icon: Heart, color: "red" },
                                { title: "Doanh Thu", value: "$12,847", change: "+15%", icon: DollarSign, color: "yellow" },
                            ].map((stat, index) => {
                                const Icon = stat.icon
                                return (
                                    <motion.div
                                        key={stat.title}
                                        custom={index}
                                        variants={cardVariants}
                                        whileHover={{
                                            scale: 1.05,
                                            boxShadow: "0 10px 25px rgba(0,0,0,0.1)",
                                            transition: { duration: 0.2 },
                                        }}
                                    >
                                        <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm border-slate-200 dark:border-slate-700/50 overflow-hidden">
                                            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                                                <CardTitle className="text-sm font-medium text-slate-700 dark:text-slate-300">
                                                    {stat.title}
                                                </CardTitle>
                                                <motion.div
                                                    animate={{
                                                        rotate: [0, 10, -10, 0],
                                                        scale: [1, 1.1, 1],
                                                    }}
                                                    transition={{
                                                        duration: 2,
                                                        repeat: Number.POSITIVE_INFINITY,
                                                        repeatDelay: 3,
                                                        delay: index * 0.5,
                                                    }}
                                                >
                                                    <Icon className={`h-4 w-4 text-${stat.color}-500 dark:text-${stat.color}-400`} />
                                                </motion.div>
                                            </CardHeader>
                                            <CardContent>
                                                <motion.div
                                                    className="text-2xl font-bold text-slate-900 dark:text-white"
                                                    initial={{ scale: 0 }}
                                                    animate={{ scale: 1 }}
                                                    transition={{ duration: 0.5, delay: index * 0.1 + 0.5 }}
                                                >
                                                    {stat.value}
                                                </motion.div>
                                                <motion.p
                                                    className="text-xs text-green-600 dark:text-green-400"
                                                    initial={{ opacity: 0, y: 10 }}
                                                    animate={{ opacity: 1, y: 0 }}
                                                    transition={{ duration: 0.5, delay: index * 0.1 + 0.7 }}
                                                >
                                                    {stat.change} so với tháng trước
                                                </motion.p>
                                            </CardContent>
                                        </Card>
                                    </motion.div>
                                )
                            })}
                        </motion.div>
                    )}

                    {/* Dynamic Content with Page Transitions */}
                    <AnimatePresence mode="wait">
                        <motion.div
                            key={activeTab}
                            initial={{ opacity: 0, x: 20 }}
                            animate={{ opacity: 1, x: 0 }}
                            exit={{ opacity: 0, x: -20 }}
                            transition={{ duration: 0.3 }}
                            className="space-y-6"
                        >
                            {renderContent()}
                        </motion.div>
                    </AnimatePresence>
                </motion.div>
            </motion.div>
        </div>
    )
}
