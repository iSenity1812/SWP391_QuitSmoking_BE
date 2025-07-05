"use client"

import type React from "react"
import { LogIn, X } from "lucide-react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Link } from "react-router-dom"
import type { UserRole } from "../types/blog-types"

interface LoginPromptDialogProps {
    isOpen: boolean
    onClose: () => void
    handleDemoLogin: (role: UserRole) => void
}

const LoginPromptDialog: React.FC<LoginPromptDialogProps> = ({ isOpen, onClose, handleDemoLogin }) => {
    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="sm:max-w-[400px]">
                <DialogHeader>
                    <div className="flex justify-between items-start">
                        <DialogTitle className="flex items-center gap-2">
                            <LogIn className="w-5 h-5" />
                            Đăng nhập để tiếp tục
                        </DialogTitle>
                        <Button variant="ghost" size="icon" className="rounded-full h-6 w-6 p-0" onClick={onClose}>
                            <X className="h-4 w-4" />
                            <span className="sr-only">Đóng</span>
                        </Button>
                    </div>
                </DialogHeader>
                <div className="py-4">
                    <p className="text-slate-600 dark:text-slate-300 mb-6">
                        Bạn cần đăng nhập để có thể tạo bài viết và tương tác với cộng đồng.
                    </p>
                    <div className="flex flex-col gap-3">
                        <Link to="/login">
                            <Button className="w-full bg-emerald-500 hover:bg-emerald-600">Đăng nhập</Button>
                        </Link>
                        <Link to="/register">
                            <Button variant="outline" className="w-full">
                                Đăng ký tài khoản mới
                            </Button>
                        </Link>
                        <div className="text-center">
                            <span className="text-sm text-slate-500 dark:text-slate-400">hoặc đăng nhập demo:</span>
                        </div>
                        <div className="grid grid-cols-2 gap-2">
                            <Button
                                variant="ghost"
                                className="text-blue-600 hover:text-blue-700"
                                onClick={() => handleDemoLogin("Premium member")}
                            >
                                Premium Member
                            </Button>
                            <Button
                                variant="ghost"
                                className="text-purple-600 hover:text-purple-700"
                                onClick={() => handleDemoLogin("Coach")}
                            >
                                Coach
                            </Button>
                        </div>
                        <Button
                            variant="ghost"
                            className="text-red-600 hover:text-red-700"
                            onClick={() => handleDemoLogin("Content Admin")}
                        >
                            Content Admin (để test)
                        </Button>
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    )
}

export default LoginPromptDialog
