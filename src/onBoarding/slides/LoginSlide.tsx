"use client"

import type React from "react"
import { useState } from "react"
import { motion } from "framer-motion"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { ChevronLeft, User, Lock, Eye, EyeOff, ArrowRight } from "lucide-react"
import type { User as UserType } from "../onBoardingFlow"

interface LoginSlideProps {
    onSuccess: (user: UserType) => void
    onBack: () => void
    onSwitchToRegister: () => void
}

export const LoginSlide: React.FC<LoginSlideProps> = ({ onSuccess, onBack, onSwitchToRegister }) => {
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [showPassword, setShowPassword] = useState(false)
    const [isLoading, setIsLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setError(null)
        setIsLoading(true)

        try {
            // Mô phỏng API call
            await new Promise((resolve) => setTimeout(resolve, 1500))

            // Kiểm tra đơn giản
            if (email.trim() === "" || !email.includes("@")) {
                throw new Error("Email không hợp lệ")
            }

            if (password.length < 6) {
                throw new Error("Mật khẩu phải có ít nhất 6 ký tự")
            }

            const user: UserType = {
                name: "Người dùng",
                email: email,
            }

            onSuccess(user)
        } catch (err) {
            setError(err instanceof Error ? err.message : "Đăng nhập thất bại. Vui lòng thử lại.")
        } finally {
            setIsLoading(false)
        }
    }

    return (
        <motion.div
            initial={{ opacity: 0, x: 50 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -50 }}
            transition={{ duration: 0.5 }}
            className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 relative overflow-hidden"
        >
            {/* Decorative Background Elements */}
            <div className="absolute top-8 left-8 w-16 h-16 bg-blue-200/40 rounded-full blur-sm"></div>
            <div className="absolute top-16 right-16 w-24 h-24 bg-blue-300/30 rounded-full blur-md"></div>
            <div className="absolute top-32 right-32 w-12 h-12 bg-green-200/40 rounded-full blur-sm"></div>
            <div className="absolute bottom-16 left-16 w-20 h-20 bg-pink-200/40 rounded-full blur-sm"></div>
            <div className="absolute bottom-32 right-8 w-16 h-16 bg-green-300/30 rounded-full blur-md"></div>
            <div className="absolute top-1/2 left-4 w-8 h-8 bg-red-200/40 rounded-full blur-sm"></div>

            {/* Cloud shapes */}
            <div className="absolute top-12 right-1/4 w-32 h-20 bg-blue-200/20 rounded-full blur-lg"></div>
            <div className="absolute top-20 right-1/3 w-24 h-16 bg-blue-300/15 rounded-full blur-lg"></div>
            <div className="absolute bottom-1/4 right-12 w-40 h-24 bg-blue-200/20 rounded-full blur-lg"></div>
            <div className="absolute bottom-1/3 right-20 w-28 h-18 bg-blue-300/15 rounded-full blur-lg"></div>

            <div className="relative z-10 flex flex-col items-center justify-center min-h-screen p-8">
                {/* Header */}
                <div className="text-center mb-12">
                    <h1 className="text-4xl font-bold text-gray-900 mb-4">Welcome back</h1>
                    <p className="text-gray-600 text-lg">Your journey to a smoke-free life begins here</p>
                </div>

                {/* Login Card */}
                <div className="w-full max-w-md bg-white rounded-3xl border-2 border-emerald-400 p-8 shadow-sm">
                    <div className="text-center mb-8">
                        <h2 className="text-2xl font-semibold text-gray-900 mb-2">Login to Your Account</h2>
                        <p className="text-gray-600">Welcome back! Please enter your details.</p>
                    </div>

                    {error && (
                        <div className="bg-red-50 text-red-600 p-3 rounded-xl mb-6 text-sm border border-red-200">{error}</div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div className="space-y-2">
                            <Label htmlFor="email" className="flex items-center gap-2 text-gray-700 font-medium">
                                <User className="w-4 h-4" />
                                Username or Email
                            </Label>
                            <Input
                                id="email"
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                placeholder="johndoe or you@example.com"
                                className="h-12 rounded-xl border-gray-200 focus:border-emerald-400 focus:ring-emerald-400 text-gray-700 placeholder:text-gray-400"
                                required
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="password" className="flex items-center gap-2 text-gray-700 font-medium">
                                <Lock className="w-4 h-4" />
                                Password
                            </Label>
                            <div className="relative">
                                <Input
                                    id="password"
                                    type={showPassword ? "text" : "password"}
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    placeholder="Enter your password"
                                    className="h-12 rounded-xl border-gray-200 focus:border-emerald-400 focus:ring-emerald-400 text-gray-700 pr-12"
                                    required
                                />
                                <button
                                    type="button"
                                    onClick={() => setShowPassword(!showPassword)}
                                    className="absolute right-4 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                                >
                                    {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                </button>
                            </div>
                        </div>

                        <Button
                            type="submit"
                            className="w-full h-12 bg-emerald-500 hover:bg-emerald-600 text-white font-medium rounded-xl transition-colors duration-200"
                            disabled={isLoading}
                        >
                            {isLoading ? "Logging in..." : "Login"}
                        </Button>
                    </form>

                    <div className="mt-8 text-center">
                        <p className="text-gray-600">
                            Don't have an account?{" "}
                            <button
                                onClick={onSwitchToRegister}
                                className="text-emerald-500 hover:text-emerald-600 font-medium inline-flex items-center gap-1"
                            >
                                Register here
                                <ArrowRight className="w-4 h-4" />
                            </button>
                        </p>
                    </div>
                </div>

                {/* Back Button */}
                <div className="mt-8">
                    <Button
                        variant="ghost"
                        onClick={onBack}
                        className="flex items-center gap-2 text-gray-600 hover:text-gray-800"
                    >
                        <ChevronLeft className="w-4 h-4" />
                        Back
                    </Button>
                </div>
            </div>
        </motion.div>
    )
}
