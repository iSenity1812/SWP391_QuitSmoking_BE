"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Search, Filter, Edit, UserCheck, UserX, Calendar } from "lucide-react"

interface User {
    id: number
    name: string
    email: string
    avatar?: string
    status: "active" | "inactive" | "suspended"
    joinDate: string
    lastActive: string
    planStatus: "none" | "active" | "completed" | "failed"
    smokingDays: number
    quitStreak: number
    role: "user" | "premium"
}

export function UserManagement() {
    const [users, setUsers] = useState<User[]>([
        {
            id: 1,
            name: "Nguyễn Văn A",
            email: "nguyenvana@email.com",
            avatar: "/placeholder.svg?height=40&width=40",
            status: "active",
            joinDate: "2024-01-15T00:00:00Z",
            lastActive: "2024-01-20T10:30:00Z",
            planStatus: "active",
            smokingDays: 15,
            quitStreak: 25,
            role: "premium",
        },
        {
            id: 2,
            name: "Trần Thị B",
            email: "tranthib@email.com",
            status: "active",
            joinDate: "2024-01-10T00:00:00Z",
            lastActive: "2024-01-19T15:20:00Z",
            planStatus: "completed",
            smokingDays: 20,
            quitStreak: 45,
            role: "user",
        },
    ])

    const handleSuspendUser = (userId: number) => {
        setUsers((prev) => prev.map((user) => (user.id === userId ? { ...user, status: "suspended" as const } : user)))
    }

    const handleActivateUser = (userId: number) => {
        setUsers((prev) => prev.map((user) => (user.id === userId ? { ...user, status: "active" as const } : user)))
    }

    const getStatusColor = (status: string) => {
        switch (status) {
            case "active":
                return "bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400"
            case "inactive":
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
            case "suspended":
                return "bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400"
            default:
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
        }
    }

    const getPlanStatusColor = (status: string) => {
        switch (status) {
            case "active":
                return "bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400"
            case "completed":
                return "bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400"
            case "failed":
                return "bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400"
            default:
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
        }
    }

    return (
        <div className="space-y-6">
            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                <CardHeader>
                    <div className="flex justify-between items-center">
                        <div>
                            <CardTitle className="text-slate-900 dark:text-white">Quản Lý Người Dùng</CardTitle>
                            <CardDescription className="text-slate-600 dark:text-slate-400">
                                Quản lý tài khoản và hoạt động của người dùng web
                            </CardDescription>
                        </div>
                        <div className="flex space-x-2">
                            <Button variant="outline" size="sm">
                                <Filter className="w-4 h-4 mr-2" />
                                Lọc
                            </Button>
                            <Button variant="outline" size="sm">
                                <Search className="w-4 h-4 mr-2" />
                                Tìm kiếm
                            </Button>
                        </div>
                    </div>
                </CardHeader>
                <CardContent>
                    <div className="space-y-4">
                        {users.map((user) => (
                            <div
                                key={user.id}
                                className="p-4 rounded-lg bg-slate-50 dark:bg-slate-700/50 border border-slate-200 dark:border-slate-600"
                            >
                                <div className="flex items-start justify-between">
                                    <div className="flex items-start space-x-4 flex-1">
                                        <Avatar className="h-12 w-12">
                                            <AvatarImage src={user.avatar || "/placeholder.svg"} />
                                            <AvatarFallback>{user.name.charAt(0)}</AvatarFallback>
                                        </Avatar>
                                        <div className="flex-1">
                                            <div className="flex items-center space-x-2 mb-2">
                                                <h4 className="font-medium text-slate-900 dark:text-white">{user.name}</h4>
                                                <Badge className={getStatusColor(user.status)}>
                                                    {user.status === "active"
                                                        ? "Hoạt động"
                                                        : user.status === "inactive"
                                                            ? "Không hoạt động"
                                                            : "Tạm khóa"}
                                                </Badge>
                                                <Badge variant={user.role === "premium" ? "default" : "secondary"}>
                                                    {user.role === "premium" ? "Premium" : "Thường"}
                                                </Badge>
                                            </div>
                                            <p className="text-slate-600 dark:text-slate-400 mb-2">{user.email}</p>

                                            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-3">
                                                <div className="text-center p-2 bg-white dark:bg-slate-600/50 rounded">
                                                    <div className="text-lg font-semibold text-slate-900 dark:text-white">{user.quitStreak}</div>
                                                    <div className="text-xs text-slate-500 dark:text-slate-400">Ngày cai thuốc</div>
                                                </div>
                                                <div className="text-center p-2 bg-white dark:bg-slate-600/50 rounded">
                                                    <div className="text-lg font-semibold text-slate-900 dark:text-white">{user.smokingDays}</div>
                                                    <div className="text-xs text-slate-500 dark:text-slate-400">Năm hút thuốc</div>
                                                </div>
                                                <div className="text-center p-2 bg-white dark:bg-slate-600/50 rounded">
                                                    <Badge className={getPlanStatusColor(user.planStatus)}>
                                                        {user.planStatus === "active"
                                                            ? "Đang thực hiện"
                                                            : user.planStatus === "completed"
                                                                ? "Hoàn thành"
                                                                : user.planStatus === "failed"
                                                                    ? "Thất bại"
                                                                    : "Chưa có kế hoạch"}
                                                    </Badge>
                                                </div>
                                                <div className="text-center p-2 bg-white dark:bg-slate-600/50 rounded">
                                                    <div className="text-xs text-slate-500 dark:text-slate-400">Tham gia</div>
                                                    <div className="text-sm font-medium text-slate-900 dark:text-white">
                                                        {new Date(user.joinDate).toLocaleDateString("vi-VN")}
                                                    </div>
                                                </div>
                                            </div>

                                            <div className="flex items-center space-x-4 text-xs text-slate-500 dark:text-slate-400">
                                                <span className="flex items-center">
                                                    <Calendar className="w-3 h-3 mr-1" />
                                                    Hoạt động cuối: {new Date(user.lastActive).toLocaleDateString("vi-VN")}
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="flex space-x-2">
                                        <Button size="sm" variant="ghost">
                                            <Edit className="w-4 h-4" />
                                        </Button>
                                        {user.status === "active" ? (
                                            <Button
                                                size="sm"
                                                variant="ghost"
                                                onClick={() => handleSuspendUser(user.id)}
                                                className="text-red-600 hover:text-red-700 hover:bg-red-50 dark:hover:bg-red-900/20"
                                            >
                                                <UserX className="w-4 h-4" />
                                            </Button>
                                        ) : (
                                            <Button
                                                size="sm"
                                                variant="ghost"
                                                onClick={() => handleActivateUser(user.id)}
                                                className="text-green-600 hover:text-green-700 hover:bg-green-50 dark:hover:bg-green-900/20"
                                            >
                                                <UserCheck className="w-4 h-4" />
                                            </Button>
                                        )}
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}
