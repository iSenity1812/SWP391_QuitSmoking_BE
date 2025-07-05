// src/pages/RegisterPage.tsx

import { motion } from "framer-motion";
import { ThemeToggle } from "@/components/ui/ThemeToggle";
import { AuthHeader } from "@/pages/auth/components/AuthHeader";

import { RegisterForm } from "@/pages/auth/components/RegisterForm"; // Component chính
import { Wind } from "lucide-react";

export default function RegisterPage() {
  return (
    <motion.div
      className="flex min-h-screen flex-col items-center justify-center p-4"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.5 }}
    >
      {/* Nút chuyển đổi chủ đề (light/dark mode) */}
      <div className="absolute top-4 left-4 right-4">
        <div className="flex items-center justify-between w-full">
          {/* Phần QuitTogether nằm bên trái */}
          <a href="/" className="flex items-center gap-3 text-2xl font-black text-slate-800 dark:text-white">
            <Wind className="h-8 w-8 text-emerald-500" />
            <span className="text-xl font-bold">QuitTogether</span>
          </a>

          {/* Phần ThemeToggle nằm bên phải */}
          <ThemeToggle />
        </div>
      </div>

      <div className="w-full max-w-md">
        {/* Phần tiêu đề chung cho trang Auth (đăng ký/đăng nhập) */}
        <AuthHeader />

        <div className="grid grid-cols-1 md:grid-cols-1 gap-6 items-center">
          {/* Phần bên trái (ví dụ: ảnh, mô tả ngắn) - Ẩn trên mobile, hiển thị trên desktop */}
          <motion.div
            className="hidden md:block md:col-span-2"
            initial={{ x: -50, opacity: 0 }}
            animate={{ x: 0, opacity: 1 }}
            transition={{ delay: 0.3, type: "spring" }}
          >
            {/* có thể thêm một hình ảnh hoặc đoạn văn bản marketing vào đây.
                Ví dụ:
                <img src="/path/to/your-image.png" alt="Welcome" className="w-full h-auto object-cover rounded-lg shadow-lg" />
                <p className="mt-4 text-center text-muted-foreground">Join our community and start your journey today!</p>
            */}
          </motion.div>

          {/* Form đăng ký chính - Đã được tách thành component RegisterForm */}
          <RegisterForm />
        </div>
      </div>
    </motion.div>
  );
}