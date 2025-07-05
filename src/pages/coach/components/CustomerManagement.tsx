"use client"

import { useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Textarea } from "@/components/ui/textarea"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { MessageSquare, Plus, Eye, Calendar, Crown, Sparkles } from "lucide-react"
import { useAppointments } from "../AppointmentContext"
import { Search } from "lucide-react" // Declare the Search variable

interface Customer {
    id: number
    name: string
    email: string
    phone: string
    joinDate: string
    status: "active" | "at-risk" | "completed" | "inactive"
    progress: number
    lastContact: string
    smokingHistory: {
        cigarettesPerDay: number
        yearsOfSmoking: number
        quitAttempts: number
    }
    currentPlan: string
    notes: string[]
    avatar: string
}

interface CustomerManagementProps {
    onStartChat?: (customer: Customer) => void
}

export function CustomerManagement({ onStartChat }: CustomerManagementProps) {
    const [searchTerm, setSearchTerm] = useState("")
    const [selectedCustomer, setSelectedCustomer] = useState<Customer | null>(null)
    const [newNote, setNewNote] = useState("")
    const [filterStatus, setFilterStatus] = useState<string>("all")
    const [isDetailDialogOpen, setIsDetailDialogOpen] = useState(false)

    const { addAppointment } = useAppointments()
    const [isBookingDialogOpen, setIsBookingDialogOpen] = useState(false)
    const [bookingForm, setBookingForm] = useState({
        date: "",
        time: "",
        duration: 60,
        type: "individual" as "individual" | "group" | "emergency",
        method: "phone" as "in-person" | "phone",
        notes: "",
        location: "",
    })

    const [isAddMemberDialogOpen, setIsAddMemberDialogOpen] = useState(false)
    const [newMemberForm, setNewMemberForm] = useState({
        name: "",
        email: "",
        phone: "",
        smokingHistory: {
            cigarettesPerDay: 0,
            yearsOfSmoking: 0,
            quitAttempts: 0,
        },
        currentPlan: "",
        notes: "",
    })

    const [customers, setCustomers] = useState<Customer[]>([
        {
            id: 1,
            name: "Nguyễn Văn An",
            email: "an.nguyen@email.com",
            phone: "+84 901 234 567",
            joinDate: "2024-01-15",
            status: "active",
            progress: 75,
            lastContact: "2 ngày trước",
            smokingHistory: {
                cigarettesPerDay: 20,
                yearsOfSmoking: 10,
                quitAttempts: 3,
            },
            currentPlan: "Kế hoạch giảm dần 30 ngày",
            notes: ["Khách hàng rất có động lực", "Đã giảm từ 20 xuống 5 điếu/ngày", "Cần hỗ trợ thêm về quản lý stress"],
            avatar: "/placeholder.svg?height=40&width=40",
        },
        {
            id: 2,
            name: "Trần Thị Bình",
            email: "binh.tran@email.com",
            phone: "+84 902 345 678",
            joinDate: "2024-02-01",
            status: "at-risk",
            progress: 45,
            lastContact: "5 ngày trước",
            smokingHistory: {
                cigarettesPerDay: 15,
                yearsOfSmoking: 8,
                quitAttempts: 2,
            },
            currentPlan: "Kế hoạch cai ngay lập tức",
            notes: ["Gặp khó khăn trong tuần đầu", "Cần tăng cường hỗ trợ tâm lý", "Đã bỏ lỡ 2 cuộc hẹn gần đây"],
            avatar: "/placeholder.svg?height=40&width=40",
        },
        {
            id: 3,
            name: "Lê Văn Cường",
            email: "cuong.le@email.com",
            phone: "+84 903 456 789",
            joinDate: "2023-12-10",
            status: "completed",
            progress: 100,
            lastContact: "1 tuần trước",
            smokingHistory: {
                cigarettesPerDay: 25,
                yearsOfSmoking: 15,
                quitAttempts: 5,
            },
            currentPlan: "Hoàn thành - Duy trì",
            notes: [
                "Thành công cai thuốc sau 3 tháng",
                "Trở thành mentor cho khách hàng mới",
                "Tham gia nhóm hỗ trợ cộng đồng",
            ],
            avatar: "/placeholder.svg?height=40&width=40",
        },
        {
            id: 4,
            name: "Phạm Thị Dung",
            email: "dung.pham@email.com",
            phone: "+84 904 567 890",
            joinDate: "2024-03-01",
            status: "inactive",
            progress: 20,
            lastContact: "2 tuần trước",
            smokingHistory: {
                cigarettesPerDay: 10,
                yearsOfSmoking: 5,
                quitAttempts: 1,
            },
            currentPlan: "Tạm dừng",
            notes: ["Mất liên lạc sau tuần thứ 2", "Cần liên hệ lại để hỗ trợ", "Có thể cần thay đổi phương pháp tiếp cận"],
            avatar: "/placeholder.svg?height=40&width=40",
        },
    ])

    const filteredCustomers = customers.filter((customer) => {
        const matchesSearch =
            customer.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
            customer.email.toLowerCase().includes(searchTerm.toLowerCase())
        const matchesFilter = filterStatus === "all" || customer.status === filterStatus
        return matchesSearch && matchesFilter
    })

    const getStatusColor = (status: string) => {
        switch (status) {
            case "active":
                return "bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400"
            case "at-risk":
                return "bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400"
            case "completed":
                return "bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400"
            case "inactive":
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
            default:
                return "bg-gray-100 text-gray-800"
        }
    }

    const getStatusLabel = (status: string) => {
        switch (status) {
            case "active":
                return "Đang hoạt động"
            case "at-risk":
                return "Có nguy cơ"
            case "completed":
                return "Hoàn thành"
            case "inactive":
                return "Không hoạt động"
            default:
                return status
        }
    }

    const handleBookSchedule = () => {
        if (!bookingForm.date || !bookingForm.time || !selectedCustomer) {
            alert("Vui lòng điền đầy đủ thông tin bắt buộc")
            return
        }

        addAppointment({
            clientName: selectedCustomer.name,
            clientAvatar: selectedCustomer.avatar,
            date: bookingForm.date,
            time: bookingForm.time,
            duration: bookingForm.duration,
            type: bookingForm.type,
            method: bookingForm.method,
            status: "scheduled",
            notes: bookingForm.notes,
            location: bookingForm.location,
            customerId: selectedCustomer.id,
        })

        // Reset form
        setBookingForm({
            date: "",
            time: "",
            duration: 60,
            type: "individual",
            method: "phone",
            notes: "",
            location: "",
        })
        setIsBookingDialogOpen(false)
        alert("Đã đặt lịch thành công!")
    }

    const handleAddNewMember = () => {
        if (!newMemberForm.name || !newMemberForm.email || !newMemberForm.phone) {
            alert("Vui lòng điền đầy đủ thông tin bắt buộc")
            return
        }

        // Email validation
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        if (!emailRegex.test(newMemberForm.email)) {
            alert("Vui lòng nhập email hợp lệ")
            return
        }

        // Phone validation (Vietnamese phone number format)
        const phoneRegex = /^(\+84|0)[0-9]{9,10}$/
        if (!phoneRegex.test(newMemberForm.phone)) {
            alert("Vui lòng nhập số điện thoại hợp lệ")
            return
        }

        const newCustomer: Customer = {
            id: Math.max(...customers.map((c) => c.id), 0) + 1,
            name: newMemberForm.name,
            email: newMemberForm.email,
            phone: newMemberForm.phone,
            joinDate: new Date().toLocaleDateString("vi-VN"),
            status: "active",
            progress: 0,
            lastContact: "Vừa tham gia",
            smokingHistory: newMemberForm.smokingHistory,
            currentPlan: newMemberForm.currentPlan || "Chưa chọn kế hoạch",
            notes: newMemberForm.notes ? [newMemberForm.notes] : ["Thành viên mới tham gia"],
            avatar: "/placeholder.svg?height=40&width=40",
        }

        // Add the new customer to the state
        setCustomers((prevCustomers) => [...prevCustomers, newCustomer])

        // Reset form
        setNewMemberForm({
            name: "",
            email: "",
            phone: "",
            smokingHistory: {
                cigarettesPerDay: 0,
                yearsOfSmoking: 0,
                quitAttempts: 0,
            },
            currentPlan: "",
            notes: "",
        })

        setIsAddMemberDialogOpen(false)
        alert("Đã thêm thành viên mới thành công!")
    }

    const handleAddNote = () => {
        if (newNote.trim() && selectedCustomer) {
            // In a real app, this would update the database
            console.log("Adding note:", newNote, "for customer:", selectedCustomer.id)
            setNewNote("")
        }
    }

    const handleViewCustomer = (customer: Customer) => {
        setSelectedCustomer(customer)
        setIsDetailDialogOpen(true)
    }

    return (
        <div className="space-y-6">
            {/* Premium Feature Banner */}
            <div className="mb-6 bg-gradient-to-r from-amber-500 to-orange-500 text-white p-4 rounded-lg shadow-lg">
                <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                        <Crown className="w-6 h-6" />
                        <div>
                            <h3 className="font-bold text-lg">Tính Năng Premium - Quản Lý Thành Viên</h3>
                            <p className="text-amber-100 text-sm">Chỉ dành cho thành viên Premium - Được hướng dẫn bởi chuyên gia</p>
                        </div>
                    </div>
                    <div className="flex items-center gap-2 bg-white/20 px-3 py-1 rounded-full">
                        <Sparkles className="w-4 h-4" />
                        <span className="text-sm font-medium">PREMIUM</span>
                    </div>
                </div>
            </div>
            {/* Search and Filter */}
            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm border-slate-200 dark:border-slate-700/50">
                <CardHeader>
                    <CardTitle className="text-slate-900 dark:text-white">Tìm Kiếm & Lọc Thành Viên</CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="flex flex-col md:flex-row gap-4">
                        <div className="flex-1 relative">
                            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 w-4 h-4" />
                            <Input
                                placeholder="Tìm kiếm theo tên hoặc email..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                                className="pl-10"
                            />
                        </div>
                        <div className="flex gap-2">
                            <Button
                                variant={filterStatus === "all" ? "default" : "outline"}
                                size="sm"
                                onClick={() => setFilterStatus("all")}
                            >
                                Tất cả
                            </Button>
                            <Button
                                variant={filterStatus === "active" ? "default" : "outline"}
                                size="sm"
                                onClick={() => setFilterStatus("active")}
                            >
                                Hoạt động
                            </Button>
                            <Button
                                variant={filterStatus === "completed" ? "default" : "outline"}
                                size="sm"
                                onClick={() => setFilterStatus("completed")}
                            >
                                Hoàn thành
                            </Button>
                        </div>
                    </div>
                </CardContent>
            </Card>

            {/* Customer List */}
            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm border-slate-200 dark:border-slate-700/50">
                <CardHeader>
                    <CardTitle className="flex items-center justify-between text-slate-900 dark:text-white">
                        <span>Danh Sách Thành Viên ({filteredCustomers.length})</span>
                        <Button size="sm" onClick={() => setIsAddMemberDialogOpen(true)}>
                            <Plus className="w-4 h-4 mr-2" />
                            Thêm Thành Viên
                        </Button>
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="space-y-4">
                        {filteredCustomers.map((customer) => (
                            <div
                                key={customer.id}
                                className="flex items-center justify-between p-4 rounded-lg bg-slate-50 dark:bg-slate-700/50 hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors cursor-pointer"
                                onClick={() => handleViewCustomer(customer)}
                            >
                                <div className="flex items-center space-x-4">
                                    <Avatar className="h-12 w-12">
                                        <AvatarImage src={customer.avatar || "/placeholder.svg"} />
                                        <AvatarFallback>{customer.name.charAt(0)}</AvatarFallback>
                                    </Avatar>
                                    <div>
                                        <h3 className="font-medium text-slate-900 dark:text-white">{customer.name}</h3>
                                        <p className="text-sm text-slate-600 dark:text-slate-400">{customer.email}</p>
                                        <div className="flex items-center space-x-2 mt-1">
                                            <Badge className={getStatusColor(customer.status)}>{getStatusLabel(customer.status)}</Badge>
                                            <span className="text-xs text-slate-500">Tiến độ: {customer.progress}%</span>
                                            <Badge className="bg-gradient-to-r from-amber-500 to-orange-500 text-white border-0">
                                                <Crown className="w-3 h-3 mr-1" />
                                                PREMIUM
                                            </Badge>
                                        </div>
                                    </div>
                                </div>
                                <div className="flex items-center space-x-2">
                                    <Button
                                        size="sm"
                                        variant="outline"
                                        onClick={(e) => {
                                            e.stopPropagation()
                                            // This will be handled by the parent component
                                            onStartChat?.(customer)
                                        }}
                                    >
                                        <MessageSquare className="w-4 h-4" />
                                    </Button>
                                    <Button size="sm" variant="outline">
                                        <Calendar className="w-4 h-4" />
                                    </Button>
                                    <Button size="sm" variant="outline">
                                        <Eye className="w-4 h-4" />
                                    </Button>
                                </div>
                            </div>
                        ))}
                    </div>
                </CardContent>
            </Card>

            {/* Customer Detail Dialog */}
            <Dialog open={isDetailDialogOpen} onOpenChange={setIsDetailDialogOpen}>
                <DialogContent className="max-w-4xl max-h-[80vh] overflow-y-auto">
                    <DialogHeader>
                        <DialogTitle>Thông Tin Chi Tiết - {selectedCustomer?.name}</DialogTitle>
                    </DialogHeader>
                    {selectedCustomer && (
                        <div className="space-y-6">
                            {/* Customer Avatar and Basic Info */}
                            <div className="flex items-start space-x-6">
                                <Avatar className="h-24 w-24">
                                    <AvatarImage src={selectedCustomer.avatar || "/placeholder.svg"} />
                                    <AvatarFallback className="text-2xl">{selectedCustomer.name.charAt(0)}</AvatarFallback>
                                </Avatar>
                                <div className="flex-1">
                                    <h2 className="text-2xl font-bold text-slate-900 dark:text-white mb-2">{selectedCustomer.name}</h2>
                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                        <div>
                                            <p className="text-sm text-slate-600 dark:text-slate-400">Email</p>
                                            <p className="font-medium text-slate-900 dark:text-white">{selectedCustomer.email}</p>
                                        </div>
                                        <div>
                                            <p className="text-sm text-slate-600 dark:text-slate-400">Điện thoại</p>
                                            <p className="font-medium text-slate-900 dark:text-white">{selectedCustomer.phone}</p>
                                        </div>
                                        <div>
                                            <p className="text-sm text-slate-600 dark:text-slate-400">Ngày tham gia</p>
                                            <p className="font-medium text-slate-900 dark:text-white">{selectedCustomer.joinDate}</p>
                                        </div>
                                        <div>
                                            <p className="text-sm text-slate-600 dark:text-slate-400">Liên hệ cuối</p>
                                            <p className="font-medium text-slate-900 dark:text-white">{selectedCustomer.lastContact}</p>
                                        </div>
                                    </div>
                                    <div className="mt-4">
                                        <Badge className={getStatusColor(selectedCustomer.status)} variant="outline">
                                            {getStatusLabel(selectedCustomer.status)}
                                        </Badge>
                                    </div>
                                </div>
                            </div>

                            {/* Progress Section */}
                            <div className="bg-slate-50 dark:bg-slate-800/50 p-6 rounded-lg">
                                <h4 className="font-semibold mb-4 text-slate-900 dark:text-white">Tiến Độ Cai Thuốc</h4>
                                <div className="space-y-4">
                                    <div>
                                        <div className="flex justify-between items-center mb-2">
                                            <span className="text-sm text-slate-600 dark:text-slate-400">Hoàn thành</span>
                                            <span className="text-sm font-medium text-slate-900 dark:text-white">
                                                {selectedCustomer.progress}%
                                            </span>
                                        </div>
                                        <div className="w-full bg-slate-200 dark:bg-slate-700 rounded-full h-3">
                                            <div
                                                className="bg-gradient-to-r from-blue-500 to-green-500 h-3 rounded-full transition-all duration-300"
                                                style={{ width: `${selectedCustomer.progress}%` }}
                                            />
                                        </div>
                                    </div>
                                    <div>
                                        <p className="text-sm text-slate-600 dark:text-slate-400">Kế hoạch hiện tại</p>
                                        <p className="font-medium text-slate-900 dark:text-white">{selectedCustomer.currentPlan}</p>
                                    </div>
                                </div>
                            </div>

                            {/* Smoking History */}
                            <div className="bg-slate-50 dark:bg-slate-800/50 p-6 rounded-lg">
                                <h4 className="font-semibold mb-4 text-slate-900 dark:text-white">Lịch Sử Hút Thuốc</h4>
                                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                                    <div className="text-center">
                                        <div className="text-3xl font-bold text-red-500 mb-1">
                                            {selectedCustomer.smokingHistory.cigarettesPerDay}
                                        </div>
                                        <p className="text-sm text-slate-600 dark:text-slate-400">Điếu/ngày</p>
                                    </div>
                                    <div className="text-center">
                                        <div className="text-3xl font-bold text-orange-500 mb-1">
                                            {selectedCustomer.smokingHistory.yearsOfSmoking}
                                        </div>
                                        <p className="text-sm text-slate-600 dark:text-slate-400">Năm hút thuốc</p>
                                    </div>
                                    <div className="text-center">
                                        <div className="text-3xl font-bold text-blue-500 mb-1">
                                            {selectedCustomer.smokingHistory.quitAttempts}
                                        </div>
                                        <p className="text-sm text-slate-600 dark:text-slate-400">Lần cai trước</p>
                                    </div>
                                </div>
                            </div>

                            {/* Notes Section */}
                            <div className="bg-slate-50 dark:bg-slate-800/50 p-6 rounded-lg">
                                <h4 className="font-semibold mb-4 text-slate-900 dark:text-white">Ghi Chú Theo Dõi</h4>
                                <div className="space-y-3 mb-4">
                                    {selectedCustomer.notes.map((note, index) => (
                                        <div
                                            key={index}
                                            className="p-3 bg-white dark:bg-slate-700 rounded-lg border border-slate-200 dark:border-slate-600"
                                        >
                                            <p className="text-sm text-slate-700 dark:text-slate-300">{note}</p>
                                            <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">
                                                {new Date().toLocaleDateString("vi-VN")} - Coach
                                            </p>
                                        </div>
                                    ))}
                                </div>
                                <div className="flex gap-2">
                                    <Textarea
                                        placeholder="Thêm ghi chú mới..."
                                        value={newNote}
                                        onChange={(e) => setNewNote(e.target.value)}
                                        className="flex-1"
                                        rows={3}
                                    />
                                    <Button onClick={handleAddNote} className="self-end">
                                        <Plus className="w-4 h-4" />
                                    </Button>
                                </div>
                            </div>

                            {/* Action Buttons */}
                            <div className="flex justify-end space-x-3 pt-4 border-t border-slate-200 dark:border-slate-700">
                                <Button variant="outline" onClick={() => setIsDetailDialogOpen(false)}>
                                    Đóng
                                </Button>
                                <Button onClick={() => setIsBookingDialogOpen(true)}>
                                    <Calendar className="w-4 h-4 mr-2" />
                                    Đặt Lịch Hẹn
                                </Button>
                            </div>
                        </div>
                    )}
                </DialogContent>
            </Dialog>

            {/* Booking Schedule Dialog */}
            <Dialog open={isBookingDialogOpen} onOpenChange={setIsBookingDialogOpen}>
                <DialogContent className="max-w-md">
                    <DialogHeader>
                        <DialogTitle>Đặt Lịch Cho {selectedCustomer?.name}</DialogTitle>
                    </DialogHeader>
                    <div className="space-y-4">
                        <div className="grid grid-cols-2 gap-2">
                            <Input
                                type="date"
                                value={bookingForm.date}
                                onChange={(e) => setBookingForm({ ...bookingForm, date: e.target.value })}
                            />
                            <Input
                                type="time"
                                value={bookingForm.time}
                                onChange={(e) => setBookingForm({ ...bookingForm, time: e.target.value })}
                            />
                        </div>
                        <div className="grid grid-cols-2 gap-2">
                            <select
                                className="px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-md bg-white dark:bg-slate-800 text-slate-900 dark:text-white"
                                value={bookingForm.type}
                                onChange={(e) =>
                                    setBookingForm({
                                        ...bookingForm,
                                        type: e.target.value as "individual" | "group" | "emergency",
                                    })
                                }
                            >
                                <option value="individual">Cá nhân</option>
                                <option value="group">Nhóm</option>
                                <option value="emergency">Khẩn cấp</option>
                            </select>
                            <select
                                className="px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-md bg-white dark:bg-slate-800 text-slate-900 dark:text-white"
                                value={bookingForm.method}
                                onChange={(e) =>
                                    setBookingForm({
                                        ...bookingForm,
                                        method: e.target.value as "in-person" | "phone",
                                    })
                                }
                            >
                                <option value="phone">Điện thoại</option>
                                <option value="in-person">Trực tiếp</option>
                            </select>
                        </div>
                        <Input
                            type="number"
                            placeholder="Thời gian (phút)"
                            value={bookingForm.duration}
                            onChange={(e) => setBookingForm({ ...bookingForm, duration: Number.parseInt(e.target.value) || 60 })}
                        />
                        {bookingForm.method === "in-person" && (
                            <Input
                                placeholder="Địa điểm"
                                value={bookingForm.location}
                                onChange={(e) => setBookingForm({ ...bookingForm, location: e.target.value })}
                            />
                        )}
                        <Textarea
                            placeholder="Ghi chú cho buổi hẹn"
                            value={bookingForm.notes}
                            onChange={(e) => setBookingForm({ ...bookingForm, notes: e.target.value })}
                            rows={3}
                        />
                        <div className="flex gap-2">
                            <Button variant="outline" onClick={() => setIsBookingDialogOpen(false)} className="flex-1">
                                Hủy
                            </Button>
                            <Button onClick={handleBookSchedule} className="flex-1">
                                Đặt Lịch
                            </Button>
                        </div>
                    </div>
                </DialogContent>
            </Dialog>

            {/* Add New Member Dialog */}
            <Dialog open={isAddMemberDialogOpen} onOpenChange={setIsAddMemberDialogOpen}>
                <DialogContent className="max-w-2xl max-h-[80vh] overflow-y-auto">
                    <DialogHeader>
                        <DialogTitle>Thêm Thành Viên Mới</DialogTitle>
                    </DialogHeader>
                    <div className="space-y-6">
                        {/* Basic Information */}
                        <div className="bg-slate-50 dark:bg-slate-800/50 p-4 rounded-lg">
                            <h4 className="font-semibold mb-4 text-slate-900 dark:text-white">Thông Tin Cơ Bản</h4>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-2">
                                        Họ và tên *
                                    </label>
                                    <Input
                                        placeholder="Nhập họ và tên"
                                        value={newMemberForm.name}
                                        onChange={(e) => setNewMemberForm({ ...newMemberForm, name: e.target.value })}
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-2">Email *</label>
                                    <Input
                                        type="email"
                                        placeholder="example@email.com"
                                        value={newMemberForm.email}
                                        onChange={(e) => setNewMemberForm({ ...newMemberForm, email: e.target.value })}
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-2">
                                        Số điện thoại *
                                    </label>
                                    <Input
                                        placeholder="+84 901 234 567"
                                        value={newMemberForm.phone}
                                        onChange={(e) => setNewMemberForm({ ...newMemberForm, phone: e.target.value })}
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-2">
                                        Kế hoạch ban đầu
                                    </label>
                                    <select
                                        className="w-full px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-md bg-white dark:bg-slate-800 text-slate-900 dark:text-white"
                                        value={newMemberForm.currentPlan}
                                        onChange={(e) => setNewMemberForm({ ...newMemberForm, currentPlan: e.target.value })}
                                    >
                                        <option value="">Chọn kế hoạch</option>
                                        <option value="Kế hoạch giảm dần 30 ngày">Kế hoạch giảm dần 30 ngày</option>
                                        <option value="Kế hoạch cai ngay lập tức">Kế hoạch cai ngay lập tức</option>
                                        <option value="Kế hoạch linh hoạt">Kế hoạch linh hoạt</option>
                                        <option value="Kế hoạch nhóm">Kế hoạch nhóm</option>
                                    </select>
                                </div>
                            </div>
                        </div>

                        {/* Smoking History */}
                        <div className="bg-slate-50 dark:bg-slate-800/50 p-4 rounded-lg">
                            <h4 className="font-semibold mb-4 text-slate-900 dark:text-white">Lịch Sử Hút Thuốc</h4>
                            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-2">
                                        Số điếu/ngày
                                    </label>
                                    <Input
                                        type="number"
                                        min="0"
                                        placeholder="20"
                                        value={newMemberForm.smokingHistory.cigarettesPerDay}
                                        onChange={(e) =>
                                            setNewMemberForm({
                                                ...newMemberForm,
                                                smokingHistory: {
                                                    ...newMemberForm.smokingHistory,
                                                    cigarettesPerDay: Number.parseInt(e.target.value) || 0,
                                                },
                                            })
                                        }
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-2">
                                        Số năm hút thuốc
                                    </label>
                                    <Input
                                        type="number"
                                        min="0"
                                        placeholder="10"
                                        value={newMemberForm.smokingHistory.yearsOfSmoking}
                                        onChange={(e) =>
                                            setNewMemberForm({
                                                ...newMemberForm,
                                                smokingHistory: {
                                                    ...newMemberForm.smokingHistory,
                                                    yearsOfSmoking: Number.parseInt(e.target.value) || 0,
                                                },
                                            })
                                        }
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-2">
                                        Số lần cai trước đây
                                    </label>
                                    <Input
                                        type="number"
                                        min="0"
                                        placeholder="3"
                                        value={newMemberForm.smokingHistory.quitAttempts}
                                        onChange={(e) =>
                                            setNewMemberForm({
                                                ...newMemberForm,
                                                smokingHistory: {
                                                    ...newMemberForm.smokingHistory,
                                                    quitAttempts: Number.parseInt(e.target.value) || 0,
                                                },
                                            })
                                        }
                                    />
                                </div>
                            </div>
                        </div>

                        {/* Initial Notes */}
                        <div className="bg-slate-50 dark:bg-slate-800/50 p-4 rounded-lg">
                            <h4 className="font-semibold mb-4 text-slate-900 dark:text-white">Ghi Chú Ban Đầu</h4>
                            <Textarea
                                placeholder="Thêm ghi chú về tình trạng, mục tiêu hoặc thông tin quan trọng khác..."
                                value={newMemberForm.notes}
                                onChange={(e) => setNewMemberForm({ ...newMemberForm, notes: e.target.value })}
                                rows={4}
                            />
                        </div>

                        {/* Action Buttons */}
                        <div className="flex justify-end space-x-3 pt-4 border-t border-slate-200 dark:border-slate-700">
                            <Button variant="outline" onClick={() => setIsAddMemberDialogOpen(false)}>
                                Hủy
                            </Button>
                            <Button onClick={handleAddNewMember}>
                                <Plus className="w-4 h-4 mr-2" />
                                Thêm Thành Viên
                            </Button>
                        </div>
                    </div>
                </DialogContent>
            </Dialog>
        </div>
    )
}
