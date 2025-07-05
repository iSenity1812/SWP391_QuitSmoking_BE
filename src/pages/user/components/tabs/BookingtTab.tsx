"use client"
import { useState, useEffect } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { CalendarIcon, Clock, Star, MapPin, Phone, Video, MessageCircle, Lock, Crown, Plus } from "lucide-react"
import type { User as UserType } from "../../types/user-types"

// Mock data for coaches
const coaches = [
    {
        id: 1,
        name: "Dr. Nguyễn Văn An",
        title: "Chuyên gia Cai thuốc lá",
        rating: 4.9,
        reviews: 127,
        experience: "8 năm kinh nghiệm",
        specialization: "Tâm lý học, Cai nghiện",
        price: 200000,
        avatar: "/placeholder.svg?height=80&width=80",
        availability: ["09:00", "10:00", "14:00", "15:00", "16:00"],
        successRate: 92,
        description: "Chuyên gia hàng đầu về cai thuốc lá với phương pháp tâm lý học hiện đại",
    },
    {
        id: 2,
        name: "Dr. Trần Thị Bình",
        title: "Bác sĩ Nội khoa",
        rating: 4.8,
        reviews: 89,
        experience: "6 năm kinh nghiệm",
        specialization: "Y học cổ truyền, Detox",
        price: 180000,
        avatar: "/placeholder.svg?height=80&width=80",
        availability: ["08:00", "09:00", "13:00", "14:00", "17:00"],
        successRate: 88,
        description: "Kết hợp y học hiện đại và cổ truyền để hỗ trợ cai thuốc hiệu quả",
    },
    {
        id: 3,
        name: "Dr. Lê Minh Cường",
        title: "Chuyên gia Dinh dưỡng",
        rating: 4.7,
        reviews: 156,
        experience: "10 năm kinh nghiệm",
        specialization: "Dinh dưỡng, Thể dục",
        price: 150000,
        avatar: "/placeholder.svg?height=80&width=80",
        availability: ["10:00", "11:00", "15:00", "16:00", "18:00"],
        successRate: 85,
        description: "Hỗ trợ cai thuốc thông qua chế độ dinh dưỡng và luyện tập",
    },
]

// Mock appointments data
const mockAppointments = [
    {
        id: 1,
        coachId: 1,
        coachName: "Dr. Nguyễn Văn An",
        coachAvatar: "/placeholder.svg?height=40&width=40",
        date: "2024-01-20",
        time: "14:00",
        duration: 60,
        type: "individual",
        method: "video",
        status: "scheduled",
        notes: "Tư vấn về kế hoạch cai thuốc",
        price: 200000,
    },
    {
        id: 2,
        coachId: 2,
        coachName: "Dr. Trần Thị Bình",
        coachAvatar: "/placeholder.svg?height=40&width=40",
        date: "2024-01-15",
        time: "09:00",
        duration: 45,
        type: "individual",
        method: "phone",
        status: "completed",
        notes: "Theo dõi tiến trình cai thuốc",
        price: 180000,
    },
]

interface BookingTabProps {
    user: UserType
}

export function BookingTab({ user }: BookingTabProps) {
    const [isPremium, setIsPremium] = useState(false)
    const [subscriptionData, setSubscriptionData] = useState<any>(null)
    const [selectedCoach, setSelectedCoach] = useState<any>(null)
    const [showBookingDialog, setShowBookingDialog] = useState(false)
    const [showSubscriptionModal, setShowSubscriptionModal] = useState(false)
    const [appointments, setAppointments] = useState(mockAppointments)
    const [currentTime, setCurrentTime] = useState(new Date())
    const [activeTab, setActiveTab] = useState<"coaches" | "calendar">("coaches")
    const [selectedDate, setSelectedDate] = useState<string>(() => {
        const today = new Date()
        const year = today.getFullYear()
        const month = String(today.getMonth() + 1).padStart(2, "0")
        const day = String(today.getDate()).padStart(2, "0")
        return `${year}-${month}-${day}`
    })
    const [bookingForm, setBookingForm] = useState({
        date: "",
        time: "",
        duration: 60,
        type: "video",
        notes: "",
    })

    // Update current time every minute for more accurate countdown
    useEffect(() => {
        const timer = setInterval(() => {
            setCurrentTime(new Date())
        }, 60000) // Update every minute

        return () => clearInterval(timer)
    }, [])

    // Check subscription status on component mount and when URL changes
    useEffect(() => {
        checkSubscriptionStatus()

        // Listen for URL parameter changes (when returning from subscription page)
        const urlParams = new URLSearchParams(window.location.search)
        if (urlParams.get("premium") === "true") {
            // User just upgraded, refresh subscription status
            setTimeout(() => {
                checkSubscriptionStatus()
                // Clean up URL
                window.history.replaceState({}, "", window.location.pathname)
            }, 1000)
        }
    }, [])

    // Auto-create premium subscription for demo purposes (only once)
    useEffect(() => {
        const hasCreatedDemo = localStorage.getItem("demoSubscriptionCreated")
        if (!hasCreatedDemo) {
            // Create a demo premium subscription that expires in 90 days
            const demoSubscription = {
                type: "premium",
                duration: "3 tháng",
                startDate: new Date().toISOString(),
                expiryDate: new Date(Date.now() + 90 * 24 * 60 * 60 * 1000).toISOString(), // 90 days from now
                plan: "premium-quarterly",
            }
            localStorage.setItem("userSubscription", JSON.stringify(demoSubscription))
            localStorage.setItem("demoSubscriptionCreated", "true")
            checkSubscriptionStatus()
        }
    }, [])

    const checkSubscriptionStatus = () => {
        try {
            const storedSubscription = localStorage.getItem("userSubscription")
            if (storedSubscription) {
                const subscription = JSON.parse(storedSubscription)
                if (subscription.type === "premium") {
                    // Check if subscription is still valid
                    const expiryDate = new Date(subscription.expiryDate)
                    const now = new Date()

                    if (expiryDate > now) {
                        setIsPremium(true)
                        setSubscriptionData(subscription)
                    } else {
                        // Subscription expired
                        localStorage.removeItem("userSubscription")
                        setIsPremium(false)
                        setSubscriptionData(null)
                    }
                } else {
                    setIsPremium(false)
                    setSubscriptionData(null)
                }
            } else {
                // Check user data subscription as fallback
                if (user.subscription?.type === "premium") {
                    setIsPremium(true)
                    setSubscriptionData(user.subscription)
                } else {
                    setIsPremium(false)
                    setSubscriptionData(null)
                }
            }
        } catch (error) {
            console.error("Error checking subscription:", error)
            setIsPremium(false)
            setSubscriptionData(null)
        }
    }

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString("vi-VN")
    }

    const getDaysRemaining = () => {
        if (!subscriptionData || subscriptionData.type === "free") return null

        const expiryDate = new Date(subscriptionData.expiryDate)
        const now = currentTime
        const diffTime = expiryDate.getTime() - now.getTime()
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

        return Math.max(0, diffDays)
    }

    const getPremiumStatusColor = () => {
        const daysRemaining = getDaysRemaining()
        if (!daysRemaining) return "from-gray-500 to-gray-600"

        if (daysRemaining <= 7) return "from-red-500 to-red-600"
        if (daysRemaining <= 30) return "from-orange-500 to-orange-600"
        return "from-emerald-500 to-emerald-600"
    }

    const handleCoachSelect = (coach: any) => {
        setSelectedCoach(coach)
        if (isPremium) {
            setShowBookingDialog(true)
        } else {
            setShowSubscriptionModal(true)
        }
    }

    const handleBookingSubmit = () => {
        if (!selectedCoach || !bookingForm.date || !bookingForm.time) return

        const newAppointment = {
            id: appointments.length + 1,
            coachId: selectedCoach.id,
            coachName: selectedCoach.name,
            coachAvatar: selectedCoach.avatar,
            date: bookingForm.date,
            time: bookingForm.time,
            duration: bookingForm.duration,
            type: "individual",
            method: bookingForm.type,
            status: "scheduled",
            notes: bookingForm.notes,
            price: selectedCoach.price,
        }

        setAppointments([...appointments, newAppointment])
        setShowBookingDialog(false)
        setBookingForm({ date: "", time: "", duration: 60, type: "video", notes: "" })

        // Show success message
        alert("Đặt lịch thành công! Chúng tôi sẽ liên hệ với bạn sớm.")
    }

    const handleUpgradeClick = () => {
        // Redirect to subscription page
        window.location.href = "/subscription"
    }

    const getStatusColor = (status: string) => {
        switch (status) {
            case "confirmed":
            case "upcoming":
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

    const getStatusLabel = (status: string) => {
        switch (status) {
            case "confirmed":
            case "upcoming":
                return "Sắp tới"
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

    const getMethodIcon = (method: string) => {
        switch (method) {
            case "phone":
                return <Phone className="w-4 h-4" />
            case "video":
                return <Video className="w-4 h-4" />
            case "in-person":
                return <MapPin className="w-4 h-4" />
            default:
                return <MessageCircle className="w-4 h-4" />
        }
    }

    const getTodayAppointments = () => {
        const today = new Date().toISOString().split("T")[0]
        return appointments.filter((apt) => apt.date === today)
    }

    const getUpcomingAppointments = () => {
        const today = new Date()
        const nextWeek = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000)
        return appointments.filter((apt) => {
            const aptDate = new Date(apt.date)
            return aptDate >= today && aptDate <= nextWeek && apt.status !== "completed"
        })
    }

    // Generate calendar days for current month
    const generateCalendarDays = () => {
        const today = new Date()
        const currentMonth = today.getMonth()
        const currentYear = today.getFullYear()

        // Get the first day of the month
        const firstDay = new Date(currentYear, currentMonth, 1)
        const firstDayOfWeek = firstDay.getDay() // 0 for Sunday, 1 for Monday, etc.

        // Get the last day of the month
        const lastDay = new Date(currentYear, currentMonth + 1, 0)
        const daysInMonth = lastDay.getDate()

        // Create array for all days in the month
        const days = []

        // Add empty cells for days before the first day of the month
        for (let i = 0; i < firstDayOfWeek; i++) {
            days.push({ day: null, date: "", isEmpty: true })
        }

        // Add days of the month
        for (let i = 1; i <= daysInMonth; i++) {
            const date = new Date(currentYear, currentMonth, i)
            const year = date.getFullYear()
            const month = String(date.getMonth() + 1).padStart(2, "0")
            const day = String(date.getDate()).padStart(2, "0")
            const dateString = `${year}-${month}-${day}`

            const hasAppointments = appointments.some((apt) => apt.date === dateString)
            const isSelected = selectedDate === dateString

            const todayString = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, "0")}-${String(today.getDate()).padStart(2, "0")}`
            const isToday = dateString === todayString

            days.push({
                day: i,
                date: dateString,
                hasAppointments,
                isSelected,
                isToday,
                isEmpty: false,
            })
        }

        return days
    }

    const todayAppointments = getTodayAppointments()
    const upcomingAppointments = getUpcomingAppointments()

    return (
        <div className="space-y-6">
            {/* Premium Status Header */}
            {!isPremium ? (
                <Card className="bg-gradient-to-r from-orange-500 to-red-500 text-white">
                    <CardContent className="p-6">
                        <div className="text-center space-y-4">
                            <div className="flex items-center justify-center gap-2">
                                <Lock className="w-6 h-6" />
                                <span className="text-xl font-bold">Cần nâng cấp Premium</span>
                            </div>
                            <p className="text-lg opacity-90">Để đặt lịch với chuyên gia, bạn cần nâng cấp lên gói Premium</p>
                            <div className="flex justify-center">
                                <Button
                                    size="lg"
                                    className="bg-white text-orange-600 hover:bg-orange-50 font-semibold px-8"
                                    onClick={handleUpgradeClick}
                                >
                                    Nâng cấp ngay
                                </Button>
                            </div>
                        </div>
                    </CardContent>
                </Card>
            ) : (
                <Card className={`bg-gradient-to-r ${getPremiumStatusColor()} text-white`}>
                    <CardContent className="p-6">
                        <div className="flex items-center justify-between">
                            <div className="flex items-center gap-4">
                                <div className="w-12 h-12 bg-white/20 rounded-full flex items-center justify-center">
                                    <Crown className="w-6 h-6" />
                                </div>
                                <div>
                                    <div className="flex items-center gap-2 mb-1">
                                        <span className="text-xl font-bold">PREMIUM ACTIVE</span>
                                        <Badge className="bg-white/20 text-white border-white/30">{getDaysRemaining()} ngày</Badge>
                                    </div>
                                    <p className="text-white/90">Truy cập không giới hạn tất cả tính năng Premium</p>
                                </div>
                            </div>

                            <div className="text-right">
                                <div className="flex items-center gap-2 mb-2">
                                    <Clock className="w-4 h-4" />
                                    <span className="text-sm font-medium">Còn {getDaysRemaining()} ngày</span>
                                </div>
                                {subscriptionData?.expiryDate && (
                                    <div className="flex items-center gap-2">
                                        <CalendarIcon className="w-4 h-4" />
                                        <span className="text-sm">Hết hạn: {formatDate(subscriptionData.expiryDate)}</span>
                                    </div>
                                )}
                            </div>
                        </div>
                    </CardContent>
                </Card>
            )}

            {/* Navigation Tabs */}
            <div className="flex border-b border-gray-200 dark:border-gray-700 mb-4">
                <button
                    className={`py-2 px-4 font-medium text-sm ${activeTab === "coaches"
                            ? "text-blue-600 border-b-2 border-blue-600 dark:text-blue-400 dark:border-blue-400"
                            : "text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-300"
                        }`}
                    onClick={() => setActiveTab("coaches")}
                >
                    Chuyên gia
                </button>
                <button
                    className={`py-2 px-4 font-medium text-sm ${activeTab === "calendar"
                            ? "text-blue-600 border-b-2 border-blue-600 dark:text-blue-400 dark:border-blue-400"
                            : "text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-300"
                        }`}
                    onClick={() => setActiveTab("calendar")}
                >
                    Lịch hẹn
                </button>
            </div>

            {activeTab === "coaches" ? (
                /* Coaches View */
                <div>
                    <h3 className="text-xl font-semibold mb-4">Chuyên gia tư vấn</h3>
                    <div className="grid md:grid-cols-3 gap-6">
                        {coaches.map((coach) => (
                            <Card
                                key={coach.id}
                                className={`relative cursor-pointer transition-all duration-300 hover:shadow-lg ${!isPremium ? "opacity-75" : ""}`}
                                onClick={() => handleCoachSelect(coach)}
                            >
                                {!isPremium && (
                                    <div className="absolute inset-0 bg-black/20 rounded-lg flex items-center justify-center z-10">
                                        <div className="bg-white rounded-full p-3">
                                            <Lock className="w-6 h-6 text-gray-600" />
                                        </div>
                                    </div>
                                )}

                                <div className="text-center pt-6">
                                    <div className="w-20 h-20 rounded-full mx-auto mb-3 overflow-hidden">
                                        <img
                                            src={coach.avatar || "/placeholder.svg"}
                                            alt={coach.name}
                                            className="w-full h-full object-cover"
                                        />
                                    </div>
                                    <h3 className="text-lg font-semibold">{coach.name}</h3>
                                    <p className="text-sm text-slate-600 dark:text-slate-300">{coach.title}</p>

                                    <div className="flex items-center justify-center gap-1 mt-2">
                                        <Star className="w-4 h-4 fill-yellow-400 text-yellow-400" />
                                        <span className="font-medium">{coach.rating}</span>
                                        <span className="text-sm text-slate-500">({coach.reviews})</span>
                                    </div>
                                </div>

                                <CardContent className="space-y-3 pt-4">
                                    <div className="text-center">
                                        <p className="text-sm text-slate-600 dark:text-slate-300">{coach.experience}</p>
                                        <p className="text-sm font-medium text-emerald-600">Tỷ lệ thành công: {coach.successRate}%</p>
                                    </div>

                                    <Button className="w-full bg-black hover:bg-gray-800 text-white" disabled={!isPremium}>
                                        Đặt lịch
                                    </Button>
                                </CardContent>
                            </Card>
                        ))}
                    </div>

                    {/* For free users, show upgrade prompt */}
                    {!isPremium && (
                        <Card className="p-8 text-center mt-8">
                            <CardContent className="space-y-6">
                                <div className="w-20 h-20 bg-orange-100 rounded-full flex items-center justify-center mx-auto">
                                    <Lock className="w-10 h-10 text-orange-600" />
                                </div>
                                <div>
                                    <h3 className="text-2xl font-bold mb-2">Truy cập Premium</h3>
                                    <p className="text-slate-600 dark:text-slate-300 mb-6">
                                        Nâng cấp lên Premium để đặt lịch với các chuyên gia hàng đầu
                                    </p>
                                </div>
                                <Button size="lg" className="bg-orange-600 hover:bg-orange-700" onClick={handleUpgradeClick}>
                                    Nâng cấp Premium ngay
                                </Button>
                            </CardContent>
                        </Card>
                    )}
                </div>
            ) : (
                /* Calendar View - 3 Column Layout */
                <div className="space-y-6">
                    {/* Calendar Header with Book New Button */}
                    {isPremium && (
                        <div className="flex items-center justify-between">
                            <h3 className="text-xl font-semibold">Lịch hẹn của bạn</h3>
                            <Button className="bg-black hover:bg-gray-800 text-white" onClick={() => setShowBookingDialog(true)}>
                                <Plus className="w-4 h-4 mr-2" />
                                Đặt lịch mới
                            </Button>
                        </div>
                    )}

                    <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                        {/* Left Column - Calendar */}
                        <Card className="bg-white dark:bg-slate-800">
                            <CardHeader>
                                <CardTitle className="text-center">Lịch</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <div className="space-y-4">
                                    <div className="text-center font-medium text-slate-900 dark:text-white">
                                        Tháng {new Date().getMonth() + 1}, {new Date().getFullYear()}
                                    </div>
                                    <div className="grid grid-cols-7 gap-1 text-center text-sm">
                                        {["CN", "T2", "T3", "T4", "T5", "T6", "T7"].map((day) => (
                                            <div key={day} className="p-2 font-medium text-slate-600 dark:text-slate-400">
                                                {day}
                                            </div>
                                        ))}
                                        {generateCalendarDays().map((day, index) => (
                                            <div key={index} className="p-1">
                                                {!day.isEmpty ? (
                                                    <button
                                                        onClick={() => setSelectedDate(day.date)}
                                                        className={`w-full h-8 rounded-md text-sm transition-colors ${day.isSelected
                                                                ? "bg-blue-500 text-white"
                                                                : day.isToday
                                                                    ? "bg-blue-100 dark:bg-blue-900/20 text-blue-600 dark:text-blue-400"
                                                                    : day.hasAppointments
                                                                        ? "bg-green-100 dark:bg-green-900/20 text-green-600 dark:text-green-400"
                                                                        : "hover:bg-slate-100 dark:hover:bg-slate-700 text-slate-900 dark:text-white"
                                                            }`}
                                                    >
                                                        {day.day}
                                                    </button>
                                                ) : null}
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            </CardContent>
                        </Card>

                        {/* Middle Column - Today's Appointments */}
                        <Card className="bg-white dark:bg-slate-800">
                            <CardHeader>
                                <CardTitle className="text-center">Lịch Thành Viên Hôm Nay</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <div className="space-y-4 min-h-[300px] flex flex-col">
                                    {todayAppointments.length > 0 ? (
                                        todayAppointments.map((appointment) => (
                                            <div
                                                key={appointment.id}
                                                className="p-3 border border-slate-200 dark:border-slate-700 rounded-lg hover:bg-slate-50 dark:hover:bg-slate-700/50 transition-colors"
                                            >
                                                <div className="flex items-start justify-between">
                                                    <div className="flex items-center space-x-3">
                                                        <Avatar className="h-10 w-10">
                                                            <AvatarImage src={appointment.coachAvatar || "/placeholder.svg"} />
                                                            <AvatarFallback>{appointment.coachName.charAt(0)}</AvatarFallback>
                                                        </Avatar>
                                                        <div>
                                                            <p className="font-medium text-slate-900 dark:text-white">{appointment.coachName}</p>
                                                            <div className="flex items-center space-x-2 text-sm text-slate-600 dark:text-slate-400">
                                                                <Clock className="w-3 h-3" />
                                                                <span>{appointment.time}</span>
                                                                {getMethodIcon(appointment.method)}
                                                                <span>{appointment.duration} phút</span>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <Badge className={getStatusColor(appointment.status)}>
                                                        {getStatusLabel(appointment.status)}
                                                    </Badge>
                                                </div>
                                                {appointment.notes && (
                                                    <p className="text-sm text-slate-600 dark:text-slate-400 mt-2">{appointment.notes}</p>
                                                )}
                                            </div>
                                        ))
                                    ) : (
                                        <div className="flex-1 flex items-center justify-center">
                                            <p className="text-slate-500 dark:text-slate-400 text-center">Không có lịch hẹn nào hôm nay</p>
                                        </div>
                                    )}
                                </div>
                            </CardContent>
                        </Card>

                        {/* Right Column - Upcoming Appointments */}
                        <Card className="bg-white dark:bg-slate-800">
                            <CardHeader>
                                <CardTitle className="text-center">Lịch Thành Viên Sắp Tới</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <div className="space-y-4 min-h-[300px] flex flex-col">
                                    {upcomingAppointments.length > 0 ? (
                                        upcomingAppointments.map((appointment) => (
                                            <div
                                                key={appointment.id}
                                                className="p-3 border border-slate-200 dark:border-slate-700 rounded-lg hover:bg-slate-50 dark:hover:bg-slate-700/50 transition-colors"
                                            >
                                                <div className="flex items-start justify-between">
                                                    <div className="flex items-center space-x-3">
                                                        <Avatar className="h-8 w-8">
                                                            <AvatarImage src={appointment.coachAvatar || "/placeholder.svg"} />
                                                            <AvatarFallback>{appointment.coachName.charAt(0)}</AvatarFallback>
                                                        </Avatar>
                                                        <div>
                                                            <p className="font-medium text-sm text-slate-900 dark:text-white">
                                                                {appointment.coachName}
                                                            </p>
                                                            <div className="flex items-center space-x-2 text-xs text-slate-600 dark:text-slate-400">
                                                                <span>{formatDate(appointment.date)}</span>
                                                                <span>{appointment.time}</span>
                                                                {getMethodIcon(appointment.method)}
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <Badge className={getStatusColor(appointment.status)} variant="outline">
                                                        {getStatusLabel(appointment.status)}
                                                    </Badge>
                                                </div>
                                            </div>
                                        ))
                                    ) : (
                                        <div className="flex-1 flex items-center justify-center">
                                            <p className="text-slate-500 dark:text-slate-400 text-center">Không có lịch hẹn sắp tới</p>
                                        </div>
                                    )}
                                </div>
                            </CardContent>
                        </Card>
                    </div>
                </div>
            )}

            {/* Booking Dialog */}
            <Dialog open={showBookingDialog} onOpenChange={setShowBookingDialog}>
                <DialogContent className="max-w-2xl">
                    <DialogHeader>
                        <DialogTitle>Đặt lịch tư vấn</DialogTitle>
                    </DialogHeader>
                    <div className="space-y-6">
                        {selectedCoach && (
                            <div className="flex items-center justify-center gap-4 p-4 bg-emerald-50 dark:bg-emerald-900/20 rounded-lg">
                                <img
                                    src={selectedCoach.avatar || "/placeholder.svg"}
                                    alt={selectedCoach.name}
                                    className="w-16 h-16 rounded-full"
                                />
                                <div className="text-left">
                                    <h3 className="font-semibold">{selectedCoach.name}</h3>
                                    <p className="text-sm text-slate-600 dark:text-slate-300">{selectedCoach.title}</p>
                                    <div className="flex items-center gap-1 mt-1">
                                        <Star className="w-4 h-4 fill-yellow-400 text-yellow-400" />
                                        <span className="text-sm font-medium">{selectedCoach.rating}</span>
                                    </div>
                                </div>
                            </div>
                        )}

                        <div className="grid md:grid-cols-2 gap-6">
                            <div className="space-y-4">
                                <div>
                                    <Label htmlFor="date">Ngày hẹn</Label>
                                    <Input
                                        id="date"
                                        type="date"
                                        value={bookingForm.date}
                                        onChange={(e) => setBookingForm({ ...bookingForm, date: e.target.value })}
                                        min={new Date().toISOString().split("T")[0]}
                                    />
                                </div>

                                <div>
                                    <Label htmlFor="time">Giờ hẹn</Label>
                                    <select
                                        id="time"
                                        className="w-full p-2 border rounded-md"
                                        value={bookingForm.time}
                                        onChange={(e) => setBookingForm({ ...bookingForm, time: e.target.value })}
                                    >
                                        <option value="">Chọn giờ</option>
                                        {selectedCoach?.availability.map((time: string) => (
                                            <option key={time} value={time}>
                                                {time}
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                <div>
                                    <Label htmlFor="duration">Thời lượng (phút)</Label>
                                    <select
                                        id="duration"
                                        className="w-full p-2 border rounded-md"
                                        value={bookingForm.duration}
                                        onChange={(e) => setBookingForm({ ...bookingForm, duration: Number.parseInt(e.target.value) })}
                                    >
                                        <option value={30}>30 phút</option>
                                        <option value={60}>60 phút</option>
                                        <option value={90}>90 phút</option>
                                    </select>
                                </div>
                            </div>

                            <div className="space-y-4">
                                <div>
                                    <Label htmlFor="type">Hình thức tư vấn</Label>
                                    <select
                                        id="type"
                                        className="w-full p-2 border rounded-md"
                                        value={bookingForm.type}
                                        onChange={(e) => setBookingForm({ ...bookingForm, type: e.target.value })}
                                    >
                                        <option value="video">Video call</option>
                                        <option value="phone">Điện thoại</option>
                                        <option value="in-person">Trực tiếp</option>
                                    </select>
                                </div>

                                <div>
                                    <Label htmlFor="notes">Ghi chú</Label>
                                    <Textarea
                                        id="notes"
                                        placeholder="Mô tả vấn đề bạn muốn tư vấn..."
                                        value={bookingForm.notes}
                                        onChange={(e) => setBookingForm({ ...bookingForm, notes: e.target.value })}
                                        rows={4}
                                    />
                                </div>
                            </div>
                        </div>

                        <div className="flex gap-4">
                            <Button variant="outline" className="flex-1" onClick={() => setShowBookingDialog(false)}>
                                Hủy
                            </Button>
                            <Button
                                className="flex-1 bg-black hover:bg-gray-800 text-white"
                                onClick={handleBookingSubmit}
                                disabled={!bookingForm.date || !bookingForm.time}
                            >
                                Xác nhận đặt lịch
                            </Button>
                        </div>
                    </div>
                </DialogContent>
            </Dialog>

            {/* Subscription Modal */}
            <Dialog open={showSubscriptionModal} onOpenChange={setShowSubscriptionModal}>
                <DialogContent className="max-w-4xl">
                    <div className="space-y-6">
                        <div className="text-center">
                            <h2 className="text-3xl font-bold mb-2">Nâng cấp Premium</h2>
                            <p className="text-slate-600 dark:text-slate-300">
                                Để đặt lịch với {selectedCoach?.name}, bạn cần nâng cấp lên gói Premium
                            </p>
                        </div>

                        {/* Selected Coach Info */}
                        {selectedCoach && (
                            <Card className="bg-emerald-50 dark:bg-emerald-900/20 border-emerald-200 dark:border-emerald-700">
                                <CardContent className="p-4">
                                    <div className="flex items-center gap-4">
                                        <img
                                            src={selectedCoach.avatar || "/placeholder.svg"}
                                            alt={selectedCoach.name}
                                            className="w-16 h-16 rounded-full"
                                        />
                                        <div>
                                            <h3 className="font-semibold text-emerald-800 dark:text-emerald-300">
                                                Chuyên gia bạn muốn đặt lịch
                                            </h3>
                                            <p className="font-medium">{selectedCoach.name}</p>
                                            <p className="text-sm text-slate-600 dark:text-slate-300">{selectedCoach.title}</p>
                                            <div className="flex items-center gap-1 mt-1">
                                                <Star className="w-4 h-4 fill-yellow-400 text-yellow-400" />
                                                <span className="text-sm font-medium">{selectedCoach.rating}</span>
                                                <span className="text-sm text-slate-500">({selectedCoach.reviews} đánh giá)</span>
                                            </div>
                                        </div>
                                    </div>
                                </CardContent>
                            </Card>
                        )}

                        <div className="text-center">
                            <Button size="lg" className="bg-emerald-600 hover:bg-emerald-700" onClick={handleUpgradeClick}>
                                Đi tới trang nâng cấp Premium
                            </Button>
                        </div>

                        <div className="text-center">
                            <Button variant="outline" onClick={() => setShowSubscriptionModal(false)}>
                                Để sau
                            </Button>
                        </div>
                    </div>
                </DialogContent>
            </Dialog>
        </div>
    )
}
