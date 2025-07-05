"use client"

import type React from "react"
import { useState } from "react"
import { motion, AnimatePresence } from "framer-motion"
import { Button } from "@/components/ui/button"
import {
    Wind,
    ChevronRight,
    ChevronLeft,
    Star,
    TrendingUp,
    DollarSign,
    Target,
    Users,
    Award,
    Heart,
} from "lucide-react"

interface IntroSlideProps {
    onComplete: () => void
}

const introSlides = [
    {
        id: "welcome",
        title: "Chào mừng đến với QuitTogether",
        subtitle: "Hành trình bỏ thuốc thành công bắt đầu từ đây",
        description:
            "Tham gia cùng hàng nghìn người đã thành công bỏ hút thuốc. Câu chuyện thành công của bạn bắt đầu ngay hôm nay.",
        icon: Wind,
        color: "emerald",
        stats: "10,000+ người đã thành công",
        image: "🌟",
    },
    {
        id: "tracking",
        title: "Theo dõi tiến trình của bạn",
        subtitle: "Mỗi bước nhỏ đều quan trọng",
        description:
            "Lập kế hoạch mục tiêu hàng ngày và theo dõi mỗi lần hút thuốc. Từng bước nhỏ dẫn đến thành công lớn! Xem biểu đồ tiến trình và cảm nhận sự thay đổi tích cực.",
        icon: TrendingUp,
        color: "blue",
        stats: "95% người dùng thấy cải thiện trong tuần đầu",
        image: "📊",
    },
    {
        id: "savings",
        title: "Tiết kiệm tiền và theo dõi hành trình",
        subtitle: "Nhìn thấy số tiền bạn đã tiết kiệm",
        description:
            "Hình dung tiến trình của bạn và xem số tiền tiết kiệm tăng lên mỗi ngày. Mỗi lần hút thuốc đều được tính toán và hiển thị trực quan.",
        icon: DollarSign,
        color: "green",
        stats: "Trung bình tiết kiệm 2.5 triệu/tháng",
        image: "💰",
    },
    {
        id: "personalized",
        title: "Theo nhịp độ của bạn, theo quy tắc của bạn",
        subtitle: "Kế hoạch được cá nhân hóa hoàn toàn",
        description:
            "Dù bạn muốn giảm dần hay bỏ hẳn, ứng dụng của chúng tôi sẽ thích ứng với mục tiêu của bạn. Hoàn toàn tùy thuộc vào bạn!",
        icon: Target,
        color: "purple",
        stats: "3 phương pháp khác nhau để lựa chọn",
        image: "🎯",
    },
    {
        id: "community",
        title: "Cộng đồng hỗ trợ 24/7",
        subtitle: "Bạn không đơn độc trong hành trình này",
        description:
            "Kết nối với cộng đồng những người cùng chí hướng. Chia sẻ kinh nghiệm, nhận động viên và cùng nhau vượt qua thử thách.",
        icon: Users,
        color: "orange",
        stats: "5,000+ thành viên tích cực hỗ trợ",
        image: "👥",
    },
    {
        id: "testimonial",
        title: "Câu chuyện thành công thực tế",
        subtitle: "Từ những người đã thành công",
        description:
            "Nghe câu chuyện từ Minh - 44 tuổi, đã hút thuốc 4 năm và hiện tại đã 6 tuần không thuốc. Tiết kiệm được 4.3 triệu đồng và lấy lại năng lượng!",
        icon: Award,
        color: "pink",
        stats: "Tỷ lệ thành công 78% sau 3 tháng",
        image: "🏆",
        testimonial: {
            name: "Minh, 44 tuổi",
            duration: "Hút thuốc 4 năm — 6 tuần không thuốc 🔥",
            quote:
                "Việc theo dõi hàng ngày thực sự mở mắt tôi. Đã tiết kiệm được 4.300.000đ và năng lượng của tôi đã trở lại!",
        },
    },
]

export const IntroSlide: React.FC<IntroSlideProps> = ({ onComplete }) => {
    const [currentSlideIndex, setCurrentSlideIndex] = useState(0)
    const currentSlide = introSlides[currentSlideIndex]

    const nextSlide = () => {
        if (currentSlideIndex < introSlides.length - 1) {
            setCurrentSlideIndex(currentSlideIndex + 1)
        } else {
            onComplete()
        }
    }

    const prevSlide = () => {
        if (currentSlideIndex > 0) {
            setCurrentSlideIndex(currentSlideIndex - 1)
        }
    }

    const goToSlide = (index: number) => {
        setCurrentSlideIndex(index)
    }

    const getColorClasses = (color: string) => {
        const colors = {
            emerald: {
                bg: "from-emerald-500 to-emerald-600",
                text: "text-emerald-600",
                border: "border-emerald-200",
                accent: "bg-emerald-100 dark:bg-emerald-900/30",
            },
            blue: {
                bg: "from-blue-500 to-blue-600",
                text: "text-blue-600",
                border: "border-blue-200",
                accent: "bg-blue-100 dark:bg-blue-900/30",
            },
            green: {
                bg: "from-green-500 to-green-600",
                text: "text-green-600",
                border: "border-green-200",
                accent: "bg-green-100 dark:bg-green-900/30",
            },
            purple: {
                bg: "from-purple-500 to-purple-600",
                text: "text-purple-600",
                border: "border-purple-200",
                accent: "bg-purple-100 dark:bg-purple-900/30",
            },
            orange: {
                bg: "from-orange-500 to-orange-600",
                text: "text-orange-600",
                border: "border-orange-200",
                accent: "bg-orange-100 dark:bg-orange-900/30",
            },
            pink: {
                bg: "from-pink-500 to-pink-600",
                text: "text-pink-600",
                border: "border-pink-200",
                accent: "bg-pink-100 dark:bg-pink-900/30",
            },
        }
        return colors[color as keyof typeof colors] || colors.emerald
    }

    const colorClasses = getColorClasses(currentSlide.color)

    return (
        <div className="min-h-screen w-full flex items-center justify-center relative">
            {/* Background Elements */}
            <div className="absolute inset-0 bg-gradient-to-br from-slate-50 via-white to-slate-100 dark:from-slate-900 dark:via-slate-800 dark:to-slate-900"></div>

            {/* Animated Background Shapes */}
            <div
                className={`absolute top-20 right-20 w-64 h-64 bg-gradient-to-br ${colorClasses.bg} opacity-10 rounded-full blur-3xl animate-pulse`}
            ></div>
            <div
                className={`absolute bottom-20 left-20 w-96 h-96 bg-gradient-to-tr ${colorClasses.bg} opacity-5 rounded-full blur-3xl animate-pulse delay-1000`}
            ></div>

            {/* Main Content */}
            <div className="relative z-10 max-w-6xl mx-auto px-8 py-12 ">
                <AnimatePresence mode="wait">
                    <motion.div
                        key={currentSlide.id}
                        initial={{ opacity: 0, x: 300 }}
                        animate={{ opacity: 1, x: 0 }}
                        exit={{ opacity: 0, x: -300 }}
                        transition={{ duration: 0.6, ease: "easeInOut" }}
                        className="text-center"
                    >
                        {/* Logo and Brand - Only show on first slide */}
                        {currentSlideIndex === 0 && (
                            <motion.div
                                initial={{ opacity: 0, y: -30 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ duration: 0.8 }}
                                className="mb-16"
                            >
                                <div className="flex items-center justify-center gap-4 text-5xl font-black text-slate-800 dark:text-white mb-8">
                                    <Wind className="h-16 w-16 text-emerald-500" />
                                    <span className="text-6xl font-bold">QuitTogether</span>
                                </div>
                                <div className="flex justify-center mb-6">
                                    {[1, 2, 3, 4, 5].map((star) => (
                                        <Star key={star} className="w-10 h-10 text-yellow-400 fill-current" />
                                    ))}
                                </div>
                            </motion.div>
                        )}

                        {/* Main Content Area */}
                        <div className="grid lg:grid-cols-2 gap-16 items-center min-h-[700px]">
                            {/* Left Side - Text Content */}
                            <div className="text-left space-y-8">
                                <div>
                                    <motion.div
                                        initial={{ opacity: 0, y: 20 }}
                                        animate={{ opacity: 1, y: 0 }}
                                        transition={{ delay: 0.2 }}
                                        className={`inline-flex items-center gap-3 px-6 py-3 rounded-full ${colorClasses.accent} ${colorClasses.border} border-2 mb-6`}
                                    >
                                        <currentSlide.icon className={`w-6 h-6 ${colorClasses.text}`} />
                                        <span className={`font-semibold ${colorClasses.text}`}>{currentSlide.stats}</span>
                                    </motion.div>

                                    <motion.h1
                                        initial={{ opacity: 0, y: 20 }}
                                        animate={{ opacity: 1, y: 0 }}
                                        transition={{ delay: 0.3 }}
                                        className="text-5xl lg:text-6xl font-bold text-slate-800 dark:text-white mb-4 leading-tight"
                                    >
                                        {currentSlide.title}
                                    </motion.h1>

                                    <motion.p
                                        initial={{ opacity: 0, y: 20 }}
                                        animate={{ opacity: 1, y: 0 }}
                                        transition={{ delay: 0.4 }}
                                        className="text-2xl text-slate-600 dark:text-slate-300 mb-6 font-medium"
                                    >
                                        {currentSlide.subtitle}
                                    </motion.p>
                                </div>

                                <motion.p
                                    initial={{ opacity: 0, y: 20 }}
                                    animate={{ opacity: 1, y: 0 }}
                                    transition={{ delay: 0.5 }}
                                    className="text-xl text-slate-600 dark:text-slate-400 leading-relaxed"
                                >
                                    {currentSlide.description}
                                </motion.p>

                                {/* Testimonial for last slide */}
                                {currentSlide.testimonial && (
                                    <motion.div
                                        initial={{ opacity: 0, y: 20 }}
                                        animate={{ opacity: 1, y: 0 }}
                                        transition={{ delay: 0.6 }}
                                        className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl rounded-2xl p-8 border border-slate-200 dark:border-slate-700 shadow-xl"
                                    >
                                        <div className="flex items-center gap-4 mb-6">
                                            <div
                                                className={`w-16 h-16 bg-gradient-to-br ${colorClasses.bg} rounded-full flex items-center justify-center`}
                                            >
                                                <span className="text-white font-bold text-xl">M</span>
                                            </div>
                                            <div className="text-left">
                                                <div className="font-bold text-slate-800 dark:text-white text-lg">
                                                    {currentSlide.testimonial.name}
                                                </div>
                                                <div className="text-slate-600 dark:text-slate-300">{currentSlide.testimonial.duration}</div>
                                            </div>
                                        </div>
                                        <div className={`text-6xl ${colorClasses.text} mb-4`}>"</div>
                                        <p className="text-slate-700 dark:text-slate-300 italic text-lg leading-relaxed">
                                            {currentSlide.testimonial.quote}
                                        </p>
                                    </motion.div>
                                )}
                            </div>

                            {/* Right Side - Visual Content */}
                            <div className="flex items-center justify-center">
                                <motion.div
                                    initial={{ opacity: 0, scale: 0.8 }}
                                    animate={{ opacity: 1, scale: 1 }}
                                    transition={{ delay: 0.4, duration: 0.8 }}
                                    className="relative"
                                >
                                    <div
                                        className={`w-80 h-80 bg-gradient-to-br ${colorClasses.bg} rounded-3xl flex items-center justify-center shadow-2xl transform rotate-3 hover:rotate-0 transition-transform duration-500`}
                                    >
                                        <span className="text-9xl">{currentSlide.image}</span>
                                    </div>

                                    {/* Floating elements */}
                                    <div
                                        className={`absolute -top-4 -right-4 w-16 h-16 bg-gradient-to-br ${colorClasses.bg} rounded-full flex items-center justify-center shadow-lg animate-bounce`}
                                    >
                                        <currentSlide.icon className="w-8 h-8 text-white" />
                                    </div>

                                    <div
                                        className={`absolute -bottom-4 -left-4 w-12 h-12 bg-gradient-to-br ${colorClasses.bg} rounded-full flex items-center justify-center shadow-lg animate-pulse`}
                                    >
                                        <Heart className="w-6 h-6 text-white" />
                                    </div>
                                </motion.div>
                            </div>
                        </div>
                    </motion.div>
                </AnimatePresence>

                {/* Navigation Controls */}
                <div className="flex items-center justify-between mt-16">
                    {/* Previous Button */}
                    <Button
                        onClick={prevSlide}
                        variant="outline"
                        size="lg"
                        className={`px-8 py-4 rounded-xl font-semibold ${currentSlideIndex === 0 ? "opacity-50 cursor-not-allowed" : "hover:scale-105"} transition-all duration-300`}
                        disabled={currentSlideIndex === 0}
                    >
                        <ChevronLeft className="mr-2 h-5 w-5" />
                        Quay lại
                    </Button>

                    {/* Slide Indicators */}
                    <div className="flex items-center gap-3">
                        {introSlides.map((_, index) => (
                            <button
                                key={index}
                                onClick={() => goToSlide(index)}
                                className={`w-4 h-4 rounded-full transition-all duration-300 ${index === currentSlideIndex
                                    ? `bg-gradient-to-r ${colorClasses.bg} scale-125 shadow-lg`
                                    : "bg-slate-300 dark:bg-slate-600 hover:bg-slate-400 dark:hover:bg-slate-500"
                                    }`}
                            />
                        ))}
                    </div>

                    {/* Next/Complete Button */}
                    <Button
                        onClick={nextSlide}
                        size="lg"
                        className={`px-8 py-4 rounded-xl font-semibold text-white bg-gradient-to-r ${colorClasses.bg} hover:scale-105 transition-all duration-300 shadow-xl`}
                    >
                        {currentSlideIndex === introSlides.length - 1 ? "Bắt Đầu Ngay" : "Tiếp Theo"}
                        <ChevronRight className="ml-2 h-5 w-5" />
                    </Button>
                </div>

                {/* Progress Indicator */}
                <div className="mt-8 text-center">
                    <p className="text-sm text-slate-500 dark:text-slate-400">
                        {currentSlideIndex + 1} / {introSlides.length}
                    </p>
                </div>
            </div>
        </div>
    )
}
