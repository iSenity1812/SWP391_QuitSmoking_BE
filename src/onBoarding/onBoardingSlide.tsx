"use client"

import { motion } from "framer-motion"
import { Button } from "@/components/ui/button"

interface OnboardingSlideProps {
    title: string
    description: string
    phoneImage: string
    currentSlide: number
    totalSlides: number
    onNext: () => void
    onSkip: () => void
    isLastSlide: boolean
}

export function OnboardingSlide({
    title,
    description,
    phoneImage,
    currentSlide,
    totalSlides,
    onNext,
    onSkip,
    isLastSlide,
}: OnboardingSlideProps) {
    return (
        <motion.div
            initial={{ opacity: 0, x: 50 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -50 }}
            transition={{ duration: 0.5 }}
            className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100 dark:from-slate-900 dark:to-slate-800 flex items-center justify-center p-8"
        >
            <div className="max-w-6xl w-full grid grid-cols-2 gap-16 items-center">
                {/* Left Content */}
                <div className="space-y-8">
                    {/* Progress Indicators */}
                    <div className="flex items-center justify-between">
                        <div className="flex space-x-3">
                            {Array.from({ length: totalSlides }).map((_, index) => (
                                <div
                                    key={index}
                                    className={`h-2 rounded-full transition-all duration-300 ${index <= currentSlide ? "bg-blue-600 w-12" : "bg-slate-300 dark:bg-slate-600 w-8"
                                        }`}
                                />
                            ))}
                        </div>
                        <Button
                            variant="ghost"
                            onClick={onSkip}
                            className="text-slate-500 hover:text-slate-700 dark:text-slate-400 dark:hover:text-slate-200"
                        >
                            Skip
                        </Button>
                    </div>

                    {/* Main Content */}
                    <div className="space-y-6">
                        <h1 className="text-5xl font-bold text-slate-900 dark:text-white leading-tight">{title}</h1>
                        <p className="text-xl text-slate-600 dark:text-slate-300 leading-relaxed">{description}</p>
                    </div>

                    {/* Continue Button */}
                    <Button
                        onClick={onNext}
                        className="bg-blue-600 hover:bg-blue-700 text-white px-12 py-4 text-lg rounded-2xl shadow-lg hover:shadow-xl transition-all duration-300"
                        size="lg"
                    >
                        {isLastSlide ? "Get Started" : "Continue"}
                    </Button>
                </div>

                {/* Right Phone Mockup */}
                <div className="flex justify-center">
                    <motion.div
                        initial={{ scale: 0.8, opacity: 0 }}
                        animate={{ scale: 1, opacity: 1 }}
                        transition={{ delay: 0.2, duration: 0.5 }}
                        className="relative"
                    >
                        <img src={phoneImage || "/placeholder.svg"} alt="App Preview" className="w-80 h-auto drop-shadow-2xl" />
                    </motion.div>
                </div>
            </div>
        </motion.div>
    )
}
