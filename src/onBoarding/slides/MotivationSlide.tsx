"use client"

import type React from "react"
import { motion } from "framer-motion"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import {
    ChevronLeft,
    DollarSign,
    Heart,
    Zap,
    Target,
    Brain,
    FlowerIcon as Butterfly,
    Rainbow,
    Wind,
} from "lucide-react"

interface MotivationSlideProps {
    onContinue: () => void
    onBack: () => void
}

export const MotivationSlide: React.FC<Omit<MotivationSlideProps, 'userEmail'>> = ({ onContinue, onBack }) => {
    const currentProblems = [
        { icon: "🧮", text: "Đếm số lần hút thuốc" },
        { icon: "🚨", text: "Kiểm tra mức nicotine" },
        { icon: "😔", text: "Đối mặt với vấn đề sức khỏe" },
        { icon: "⏰", text: "Lên kế hoạch cho giờ hút thuốc" },
        { icon: "😣", text: "Chống lại cơn thèm thuốc" },
        { icon: "💊", text: "Phụ thuộc vào nicotine" },
        { icon: "🔋", text: "Năng lượng hạn chế" },
    ]

    const futureBenefits = [
        { icon: DollarSign, text: "Tiết kiệm 2.400.000đ", color: "text-emerald-500" },
        { icon: Heart, text: "Hô hấp dễ dàng hơn", color: "text-red-500" },
        { icon: Zap, text: "Nhiều năng lượng hơn", color: "text-yellow-500" },
        { icon: Target, text: "Không còn thèm thuốc", color: "text-blue-500" },
        { icon: Heart, text: "Sức khỏe tốt hơn", color: "text-pink-500" },
        { icon: Brain, text: "Đầu óc minh mẫn", color: "text-purple-500" },
        { icon: Butterfly, text: "Tự do hoàn toàn", color: "text-cyan-500" },
        { icon: Rainbow, text: "Theo đuổi ước mơ", color: "text-indigo-500" },
    ]

    return (
        <motion.div
            initial={{ opacity: 0, x: 50 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -50 }}
            transition={{ duration: 0.5 }}
            className="min-h-screen bg-gradient-to-br from-emerald-50 via-white to-emerald-50 dark:from-slate-900 dark:via-slate-800 dark:to-slate-900 relative overflow-hidden"
        >
            {/* Background decorative elements */}
            <div className="absolute top-0 right-0 w-96 h-96 bg-gradient-to-br from-emerald-200/30 to-emerald-300/20 dark:from-emerald-500/10 dark:to-emerald-600/5 rounded-full blur-3xl animate-pulse"></div>
            <div className="absolute bottom-0 left-0 w-96 h-96 bg-gradient-to-tr from-emerald-100/40 to-emerald-200/30 dark:from-emerald-600/10 dark:to-emerald-500/5 rounded-full blur-3xl animate-pulse delay-1000"></div>

            <div className="relative z-10 p-4">
                <div className="max-w-md mx-auto">
                    {/* Header */}
                    <div className="text-center mb-8 pt-8">
                        <div className="flex items-center justify-center gap-3 text-2xl font-black text-slate-800 dark:text-white mb-4">
                            <Wind className="h-8 w-8 text-emerald-500" />
                            <span className="text-2xl font-bold">QuitTogether</span>
                        </div>
                        <h1 className="text-3xl font-bold text-slate-800 dark:text-white mb-2">Hãy Phân Tích</h1>
                        <h1 className="text-3xl font-bold bg-gradient-to-r from-emerald-500 to-emerald-600 bg-clip-text text-transparent mb-4">
                            Thói Quen Của Bạn
                        </h1>
                        <p className="text-slate-600 dark:text-slate-300 leading-relaxed">
                            Càng trung thực về thói quen hút thuốc của bạn, chúng tôi càng có thể giúp bạn bỏ thuốc hiệu quả hơn. Dữ
                            liệu của bạn giúp tạo ra kế hoạch cá nhân hóa thực sự hiệu quả – không phán xét, chỉ hỗ trợ. Sẵn sàng phân
                            tích chưa?
                        </p>
                    </div>

                    {/* Current Problems */}
                    <Card className="mb-6 bg-white/95 dark:bg-slate-800/95 backdrop-blur-xl border border-emerald-200 dark:border-slate-700 shadow-lg shadow-emerald-100/50 dark:shadow-slate-900/50">
                        <CardContent className="p-6">
                            <div className="text-center mb-4">
                                <div className="text-4xl mb-2">����</div>
                                <h3 className="font-bold text-slate-800 dark:text-white">BẢN THÂN HIỆN TẠI</h3>
                            </div>
                            <div className="space-y-3">
                                {currentProblems.map((problem, index) => (
                                    <motion.div
                                        key={index}
                                        initial={{ opacity: 0, x: -20 }}
                                        animate={{ opacity: 1, x: 0 }}
                                        transition={{ delay: index * 0.1 }}
                                        className="flex items-center gap-3 text-slate-700 dark:text-slate-300"
                                    >
                                        <span className="text-xl">{problem.icon}</span>
                                        <span>{problem.text}</span>
                                    </motion.div>
                                ))}
                            </div>
                        </CardContent>
                    </Card>

                    {/* VS Divider */}
                    <div className="flex justify-center mb-6">
                        <div className="bg-gradient-to-r from-emerald-500 to-emerald-600 text-white rounded-full w-12 h-12 flex items-center justify-center font-bold text-lg shadow-lg shadow-emerald-200/50 dark:shadow-emerald-500/25">
                            VS
                        </div>
                    </div>

                    {/* Future Benefits */}
                    <Card className="mb-8 bg-gradient-to-br from-emerald-50 to-emerald-100 dark:from-emerald-900/20 dark:to-emerald-800/20 border-2 border-emerald-200 dark:border-emerald-700 shadow-lg shadow-emerald-100/50 dark:shadow-emerald-900/50">
                        <CardContent className="p-6">
                            <div className="text-center mb-4">
                                <div className="w-16 h-16 bg-gradient-to-br from-emerald-400 to-emerald-600 rounded-2xl flex items-center justify-center mx-auto mb-3 shadow-lg shadow-emerald-200/50">
                                    <span className="text-2xl">🤩</span>
                                </div>
                                <h3 className="font-bold text-slate-800 dark:text-white text-lg">BẢN THÂN TƯƠNG LAI</h3>
                            </div>
                            <div className="space-y-3">
                                {futureBenefits.map((benefit, index) => (
                                    <motion.div
                                        key={index}
                                        initial={{ opacity: 0, x: 20 }}
                                        animate={{ opacity: 1, x: 0 }}
                                        transition={{ delay: 0.5 + index * 0.1 }}
                                        className="flex items-center gap-3"
                                    >
                                        <benefit.icon className={`w-5 h-5 ${benefit.color}`} />
                                        <span className="text-slate-700 dark:text-slate-300">{benefit.text}</span>
                                    </motion.div>
                                ))}
                            </div>
                        </CardContent>
                    </Card>

                    {/* Action Buttons */}
                    <div className="space-y-4">
                        <Button
                            onClick={onContinue}
                            className="w-full bg-gradient-to-r from-emerald-500 to-emerald-600 hover:from-emerald-600 hover:to-emerald-700 text-white font-bold py-4 text-lg rounded-xl hover:scale-105 transition-all duration-300 hover:-translate-y-0.5 shadow-lg shadow-emerald-200/50 dark:shadow-emerald-500/25"
                        >
                            Bắt Đầu Xây Dựng Tương Lai
                        </Button>

                        <Button
                            variant="ghost"
                            onClick={onBack}
                            className="w-full flex items-center justify-center gap-2 text-slate-600 dark:text-slate-300 hover:text-emerald-600 dark:hover:text-emerald-400"
                        >
                            <ChevronLeft className="w-4 h-4" />
                            Quay Lại
                        </Button>
                    </div>
                </div>
            </div>
        </motion.div>
    )
}
