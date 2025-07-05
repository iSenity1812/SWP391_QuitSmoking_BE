"use client"

import type React from "react"
import { motion } from "framer-motion"
import { Button } from "@/components/ui/button"
import { Wind, ChevronLeft, Star } from "lucide-react"

interface AuthChoiceSlideProps {
  onLoginClick: () => void
  onRegisterClick: () => void
  onBack: () => void
}

export const AuthChoiceSlide: React.FC<AuthChoiceSlideProps> = ({ onLoginClick, onRegisterClick, onBack }) => {
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

      <div className="relative z-10 flex items-center justify-center min-h-screen p-4">
        <div className="max-w-md w-full bg-white/95 dark:bg-slate-800/95 backdrop-blur-xl rounded-2xl shadow-xl shadow-emerald-100/50 dark:shadow-slate-900/50 p-8 text-center border border-emerald-200 dark:border-slate-700">
          {/* Logo and Brand */}
          <div className="mb-8">
            <div className="flex items-center justify-center gap-3 text-2xl font-black text-slate-800 dark:text-white mb-4">
              <Wind className="h-8 w-8 text-emerald-500" />
              <span className="text-2xl font-bold">QuitTogether</span>
            </div>
            <div className="flex justify-center mb-2">
              {[1, 2, 3, 4, 5].map((star) => (
                <Star key={star} className="w-5 h-5 text-yellow-400 fill-current" />
              ))}
            </div>
            <h2 className="text-2xl font-bold text-slate-800 dark:text-white mb-2">Bỏ Hút Thuốc</h2>
            <p className="text-slate-600 dark:text-slate-300">
              Để tiếp tục hành trình bỏ hút thuốc, vui lòng chọn một trong các tùy chọn bên dưới
            </p>
          </div>

          <div className="space-y-4">
            <Button
              onClick={onLoginClick}
              className="w-full bg-gradient-to-r from-emerald-500 to-emerald-600 hover:from-emerald-600 hover:to-emerald-700 text-white font-semibold py-6 rounded-xl hover:scale-105 transition-all duration-300 hover:-translate-y-0.5 shadow-lg shadow-emerald-200/50 dark:shadow-emerald-500/25"
              size="lg"
            >
              Đăng Nhập
            </Button>

            <Button
              onClick={onRegisterClick}
              variant="outline"
              className="w-full border-2 border-emerald-500 text-emerald-600 dark:text-emerald-400 font-semibold py-6 rounded-xl hover:bg-emerald-50 dark:hover:bg-emerald-900/20 hover:scale-105 transition-all duration-300 hover:-translate-y-0.5"
              size="lg"
            >
              Đăng Ký Tài Khoản Mới
            </Button>
          </div>

          <Button
            variant="ghost"
            onClick={onBack}
            className="mt-6 flex items-center gap-2 text-slate-600 dark:text-slate-300 hover:text-emerald-600 dark:hover:text-emerald-400"
          >
            <ChevronLeft className="w-4 h-4" />
            Quay Lại Trang Chủ
          </Button>
        </div>
      </div>
    </motion.div>
  )
}
