// src/components/auth/LoginForm.tsx

import React, { useState } from "react";
import { motion } from "framer-motion";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Link } from "react-router-dom"; // Import useNavigate
import { Sparkles, ArrowRight, Wind } from "lucide-react";

import { EmailInput } from "@/pages/auth/components/login";
import { PasswordInput } from "@/pages/auth/components/login";
import { SubmitButton } from "@/pages/auth/components/login";
import { ThemeToggle } from "@/components/ui/ThemeToggle";

import type { ApiResponse, LoginRequest } from "@/types/auth"; // Import LoginRequest và các type liên quan nếu cần
import { toast } from "react-toastify";
import { useAuth } from "@/hooks/useAuth";

export const LoginForm: React.FC = () => {
  const [email, setEmail] = useState(""); // Đổi tên thành email để khớp với LoginRequest
  const [password, setPassword] = useState("");
  // const [isUsernameOrEmailValid, setIsUsernameOrEmailValid] = useState<boolean | null>(null); // Có thể bỏ qua nếu validation bên client đơn giản hơn
  const [isEmailValid, setIsEmailValid] = useState<boolean | null>(null); // Đổi tên để khớp với email
  const [isPasswordValid, setIsPasswordValid] = useState<boolean | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null); // State để lưu thông báo lỗi
  const [successMessage, setSuccessMessage] = useState<string | null>(null); // State để lưu thông báo thành công
  const { login: authContextLogin } = useAuth(); // Lấy hàm login từ AuthContext

  const validateEmail = (value: string) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    setIsEmailValid(emailRegex.test(value));
  };

  const validatePassword = (value: string) => {
    // Backend của bạn yêu cầu password từ 6 đến 20 ký tự
    setIsPasswordValid(value.length >= 6 && value.length <= 20);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    console.log("e.preventDefault() đã được gọi.")
    setErrorMessage(null); // Reset lỗi
    setSuccessMessage(null); // Reset thành công
    setIsSubmitting(true);

    // Re-validate just in case
    validateEmail(email);
    validatePassword(password);

    const currentEmailValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    const currentPasswordValid = password.length >= 6 && password.length <= 20;

    if (!currentEmailValid || !currentPasswordValid) {
      setIsSubmitting(false);
      toast.error("Vui lòng nhập đúng định dạng Email và Mật khẩu (từ 8 đến 20 ký tự).");
      return;
    }

    try {
      const loginData: LoginRequest = { email, password };
      const response = await authContextLogin(loginData); // Sử dụng hàm login từ AuthContext

      if (response.status === 200) { // Kiểm tra status từ ApiResponse
        toast.success(response.message || "Đăng nhập thành công!");
      } else {
        // Đây là trường hợp response.status không phải 200 nhưng vẫn là phản hồi hợp lệ từ backend
        toast.error(response.message || "Có lỗi xảy ra trong quá trình đăng nhập.");
      }
    } catch (err: unknown) {
      // Xử lý lỗi từ backend (được ném ra từ apiClient.ts)
      console.error("Login API Error:", err);
      // Xử lý lỗi được ném ra từ apiClient.ts interceptor
      if (
        typeof err === "object" &&
        err !== null &&
        "status" in err && // Kiểm tra một thuộc tính chắc chắn có trong ApiResponse
        "message" in err   // Kiểm tra một thuộc tính khác
      ) {
        // Ép kiểu err thành ApiResponse để TypeScript biết các thuộc tính của nó
        const apiError = err as ApiResponse<unknown>;

        if (apiError.error) {
          if (typeof apiError.error === 'string') {
            // Trường hợp lỗi là một chuỗi (ví dụ: "Tài khoản vô hiệu hóa")
            toast.error(apiError.error);
          } else if (typeof apiError.error === 'object' && apiError.error !== null) {
            // Trường hợp lỗi là một Map (lỗi validation)
            const fieldErrors = Object.values(apiError.error).join(". ");
            toast.error(`Lỗi nhập liệu: ${fieldErrors}`);
          } else {
            // Trường hợp lỗi.error không phải string hoặc object
            toast.error(apiError.message || "Đăng nhập thất bại. Vui lòng thử lại.");
          }
        } else if (apiError.message) {
          // Nếu không có apiError.error nhưng có apiError.message
          toast.error(apiError.message);
        } else {
          // Fallback cuối cùng nếu không có cả error và message
          toast.error("Đăng nhập thất bại. Vui lòng kiểm tra kết nối mạng hoặc thử lại.");
        }
      } else if (err instanceof Error) {
        // Xử lý các lỗi JavaScript Error thông thường (ví dụ: lỗi mạng)
        toast.error(err.message);
      } else {
        // Xử lý các lỗi không xác định khác
        toast.error("Đăng nhập thất bại. Vui lòng kiểm tra kết nối mạng hoặc thử lại.");
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <motion.div
      className="md:col-span-3 border-2 border-emerald-400/80 rounded-xl shadow-lg bg-gradient-to-br from-emerald-50 to-sky-50 dark:from-slate-900 dark:to-slate-800 z-1"
      initial={{ x: 50, opacity: 0 }}
      animate={{ x: 0, opacity: 1 }}
      transition={{ delay: 0.4, type: "spring" }}
    >
      <Card className="form-card bg-white/80 dark:bg-slate-900/80">
        <CardHeader>
          <CardTitle className="text-2xl text-center">Login to Your Account</CardTitle>
          <CardDescription className="text-center">Welcome back! Please enter your details.</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            {errorMessage && (
              <p className="text-red-500 text-center text-sm">{errorMessage}</p>
            )}
            {successMessage && (
              <p className="text-green-500 text-center text-sm">{successMessage}</p>
            )}
            <EmailInput // Tên component này có thể gây hiểu lầm, bạn có thể đổi tên thành EmailInput
              email={email}
              setEmail={setEmail}
              isEmailValid={isEmailValid}
              validateEmail={validateEmail}
            />

            <PasswordInput
              password={password}
              setPassword={setPassword}
              isPasswordValid={isPasswordValid}
              validatePassword={validatePassword}
            />

            <SubmitButton
              isSubmitting={isSubmitting}
              isPasswordValid={isPasswordValid}
              isEmailValid={isEmailValid} // Đổi tên prop
            />
          </form>
        </CardContent>
        <CardFooter className="flex flex-col space-y-4">
          <div className="text-center text-sm">
            Don't have an account?{" "}
            <Link to="/register" className="text-emerald-500 hover:underline inline-flex items-center group">
              <ArrowRight size={16} className="mr-1" />
              Register here
              <motion.span
                initial={{ x: -5, opacity: 0 }}
                whileHover={{ x: 0, opacity: 1 }}
                className="ml-1 opacity-0 group-hover:opacity-100 transition-opacity"
              >
                <Sparkles size={12} />
              </motion.span>
            </Link>
          </div>
        </CardFooter>
      </Card>
    </motion.div>
  );
};

// ... Phần còn lại của LoginPage không thay đổi
export default function LoginPage() {
  return (
    <motion.div
      className="flex min-h-screen flex-col items-center justify-center p-4"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.5 }}
    >
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
        <motion.div
          initial={{ y: -20, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          transition={{ delay: 0.2, type: "spring", stiffness: 100 }}
          className="mb-8 text-center"
        >
          <h1 className="text-4xl font-bold text-primary mb-2">Chào mừng trở lại</h1>
          <p className="text-muted-foreground">Your journey to a smoke-free life begins here</p>
        </motion.div>

        <div className="grid grid-cols-1 md:grid-cols-1 gap-6 items-center">
          <motion.div
            className="hidden md:block md:col-span-2 "
            initial={{ x: -50, opacity: 0 }}
            animate={{ x: 0, opacity: 1 }}
            transition={{ delay: 0.3, type: "spring" }}
          >

          </motion.div>
          <LoginForm />
        </div>
      </div>
    </motion.div>
  );
}