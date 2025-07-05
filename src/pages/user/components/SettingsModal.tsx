"use client"

import { useState, useContext } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { User, Mail, Lock, Eye, EyeOff } from "lucide-react"
import { AuthContext } from "@/context/AuthContext"
import { toast } from "react-toastify"
import { userService } from "@/services/userService"

interface SettingsModalProps {
    isOpen: boolean
    onClose: () => void
}

export function SettingsModal({ isOpen, onClose }: SettingsModalProps) {
    const auth = useContext(AuthContext)
    const [showPassword, setShowPassword] = useState(false)
    const [showNewPassword, setShowNewPassword] = useState(false)
    const [showConfirmPassword, setShowConfirmPassword] = useState(false)
    const [loading, setLoading] = useState(false)

    // Form states
    const [profileForm, setProfileForm] = useState({
        name: auth?.user?.username || "",
        email: auth?.user?.email || "",
    })

    const [passwordForm, setPasswordForm] = useState({
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
    })

    // Validate email format
    const isValidEmail = (email: string) => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        return emailRegex.test(email)
    }

    // Validate username format (matching backend validation)
    const isValidUsername = (username: string) => {
        const usernameRegex = /^[a-zA-Z0-9_]+$/
        return username.length >= 3 && username.length <= 50 && usernameRegex.test(username)
    }

    // Validate password strength
    const isValidPassword = (password: string) => {
        return password.length >= 6
    }

    // Handle profile update
    const handleProfileUpdate = async () => {
        if (!profileForm.name.trim()) {
            toast.error("Tên không được để trống")
            return
        }
        if (!isValidUsername(profileForm.name)) {
            toast.error("Tên người dùng chỉ được chứa chữ cái, số và dấu gạch dưới (3-50 ký tự)")
            return
        }
        if (!isValidEmail(profileForm.email)) {
            toast.error("Email không hợp lệ")
            return
        }

        // Check if no changes made
        if (profileForm.name === auth?.user?.username && profileForm.email === auth?.user?.email) {
            toast.info("Không có thay đổi nào để cập nhật")
            return
        }

        setLoading(true)
        try {
            const result = await userService.updateProfile(profileForm)
            
            // Update localStorage is already handled in userService
            // Refresh the page to update the UI with new user info
            window.location.reload()
            
            toast.success("Cập nhật thông tin thành công!")
        } catch (error: any) {
            console.error("Update profile error:", error)
            
            // Handle specific error cases
            if (error?.response?.status === 400) {
                if (error?.response?.data?.message?.includes("Email already exists")) {
                    toast.error("Email này đã được sử dụng bởi tài khoản khác")
                } else if (error?.response?.data?.message?.includes("Username already exists")) {
                    toast.error("Tên người dùng này đã tồn tại")
                } else {
                    toast.error("Dữ liệu không hợp lệ. Vui lòng kiểm tra lại thông tin")
                }
            } else if (error?.response?.status === 500) {
                toast.error("Lỗi server. Vui lòng thử lại sau")
            } else {
                const errorMessage = error?.response?.data?.message || "Cập nhật thất bại. Vui lòng thử lại!"
                toast.error(errorMessage)
            }
        } finally {
            setLoading(false)
        }
    }

    // Handle password change
    const handlePasswordChange = async () => {
        if (!passwordForm.currentPassword) {
            toast.error("Vui lòng nhập mật khẩu hiện tại")
            return
        }
        if (!isValidPassword(passwordForm.newPassword)) {
            toast.error("Mật khẩu mới phải có ít nhất 6 ký tự")
            return
        }
        if (passwordForm.newPassword !== passwordForm.confirmPassword) {
            toast.error("Xác nhận mật khẩu không khớp")
            return
        }
        if (passwordForm.currentPassword === passwordForm.newPassword) {
            toast.error("Mật khẩu mới phải khác mật khẩu hiện tại")
            return
        }

        setLoading(true)
        try {
            await userService.changePassword(passwordForm)
            toast.success("Đổi mật khẩu thành công!")
            setPasswordForm({ currentPassword: "", newPassword: "", confirmPassword: "" })
        } catch (error: any) {
            console.error("Change password error:", error)
            const errorMessage = error?.response?.data?.message || "Đổi mật khẩu thất bại. Vui lòng kiểm tra mật khẩu hiện tại!"
            toast.error(errorMessage)
        } finally {
            setLoading(false)
        }
    }

    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="sm:max-w-[600px] max-h-[80vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle className="text-2xl font-bold text-slate-900 dark:text-white">
                        Cài Đặt Tài Khoản
                    </DialogTitle>
                </DialogHeader>

                <Tabs defaultValue="profile" className="w-full">
                    <TabsList className="grid w-full grid-cols-2">
                        <TabsTrigger value="profile" className="flex items-center gap-2">
                            <User className="w-4 h-4" />
                            Thông Tin Cá Nhân
                        </TabsTrigger>
                        <TabsTrigger value="password" className="flex items-center gap-2">
                            <Lock className="w-4 h-4" />
                            Đổi Mật Khẩu
                        </TabsTrigger>
                    </TabsList>

                    {/* Profile Tab */}
                    <TabsContent value="profile" className="space-y-4">
                        <Card>
                            <CardHeader>
                                <CardTitle className="flex items-center gap-2">
                                    <User className="w-5 h-5" />
                                    Cập Nhật Thông Tin
                                </CardTitle>
                                <CardDescription>
                                    Thay đổi tên hiển thị và email của bạn
                                </CardDescription>
                            </CardHeader>
                            <CardContent className="space-y-4">
                                <div className="space-y-2">
                                    <Label htmlFor="name">Tên hiển thị</Label>
                                    <Input
                                        id="name"
                                        type="text"
                                        value={profileForm.name}
                                        onChange={(e) => setProfileForm(prev => ({ ...prev, name: e.target.value }))}
                                        placeholder="Chỉ chữ cái, số và dấu _ (3-50 ký tự)"
                                        className="w-full"
                                        maxLength={50}
                                    />
                                    <p className="text-xs text-slate-500">
                                        Tên người dùng chỉ được chứa chữ cái, số và dấu gạch dưới
                                    </p>
                                </div>
                                <div className="space-y-2">
                                    <Label htmlFor="email">Email</Label>
                                    <div className="relative">
                                        <Mail className="absolute left-3 top-3 h-4 w-4 text-slate-400" />
                                        <Input
                                            id="email"
                                            type="email"
                                            value={profileForm.email}
                                            onChange={(e) => setProfileForm(prev => ({ ...prev, email: e.target.value }))}
                                            placeholder="Nhập email của bạn"
                                            className="pl-10 w-full"
                                            maxLength={100}
                                        />
                                    </div>
                                </div>
                                <Button
                                    onClick={handleProfileUpdate}
                                    disabled={loading}
                                    className="w-full"
                                >
                                    {loading ? "Đang cập nhật..." : "Cập Nhật Thông Tin"}
                                </Button>
                            </CardContent>
                        </Card>
                    </TabsContent>

                    {/* Password Tab */}
                    <TabsContent value="password" className="space-y-4">
                        <Card>
                            <CardHeader>
                                <CardTitle className="flex items-center gap-2">
                                    <Lock className="w-5 h-5" />
                                    Đổi Mật Khẩu
                                </CardTitle>
                                <CardDescription>
                                    Thay đổi mật khẩu để bảo mật tài khoản của bạn
                                </CardDescription>
                            </CardHeader>
                            <CardContent className="space-y-4">
                                <div className="space-y-2">
                                    <Label htmlFor="currentPassword">Mật khẩu hiện tại</Label>
                                    <div className="relative">
                                        <Lock className="absolute left-3 top-3 h-4 w-4 text-slate-400" />
                                        <Input
                                            id="currentPassword"
                                            type={showPassword ? "text" : "password"}
                                            value={passwordForm.currentPassword}
                                            onChange={(e) => setPasswordForm(prev => ({ ...prev, currentPassword: e.target.value }))}
                                            placeholder="Nhập mật khẩu hiện tại"
                                            className="pl-10 pr-10 w-full"
                                        />
                                        <Button
                                            type="button"
                                            variant="ghost"
                                            size="sm"
                                            className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                                            onClick={() => setShowPassword(!showPassword)}
                                        >
                                            {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                                        </Button>
                                    </div>
                                </div>
                                <div className="space-y-2">
                                    <Label htmlFor="newPassword">Mật khẩu mới</Label>
                                    <div className="relative">
                                        <Lock className="absolute left-3 top-3 h-4 w-4 text-slate-400" />
                                        <Input
                                            id="newPassword"
                                            type={showNewPassword ? "text" : "password"}
                                            value={passwordForm.newPassword}
                                            onChange={(e) => setPasswordForm(prev => ({ ...prev, newPassword: e.target.value }))}
                                            placeholder="Nhập mật khẩu mới (ít nhất 6 ký tự)"
                                            className="pl-10 pr-10 w-full"
                                        />
                                        <Button
                                            type="button"
                                            variant="ghost"
                                            size="sm"
                                            className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                                            onClick={() => setShowNewPassword(!showNewPassword)}
                                        >
                                            {showNewPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                                        </Button>
                                    </div>
                                </div>
                                <div className="space-y-2">
                                    <Label htmlFor="confirmPassword">Xác nhận mật khẩu mới</Label>
                                    <div className="relative">
                                        <Lock className="absolute left-3 top-3 h-4 w-4 text-slate-400" />
                                        <Input
                                            id="confirmPassword"
                                            type={showConfirmPassword ? "text" : "password"}
                                            value={passwordForm.confirmPassword}
                                            onChange={(e) => setPasswordForm(prev => ({ ...prev, confirmPassword: e.target.value }))}
                                            placeholder="Nhập lại mật khẩu mới"
                                            className="pl-10 pr-10 w-full"
                                        />
                                        <Button
                                            type="button"
                                            variant="ghost"
                                            size="sm"
                                            className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                                            onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                        >
                                            {showConfirmPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                                        </Button>
                                    </div>
                                </div>
                                <Button
                                    onClick={handlePasswordChange}
                                    disabled={loading}
                                    className="w-full"
                                >
                                    {loading ? "Đang đổi mật khẩu..." : "Đổi Mật Khẩu"}
                                </Button>
                            </CardContent>
                        </Card>
                    </TabsContent>
                </Tabs>

                <div className="flex justify-end pt-4">
                    <Button variant="outline" onClick={onClose}>
                        Đóng
                    </Button>
                </div>
            </DialogContent>
        </Dialog>
    )
}