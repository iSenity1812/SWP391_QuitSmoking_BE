// src/components/auth/PasswordInput.tsx
import React, { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Lock, Check, X, Eye, EyeOff } from "lucide-react";

interface PasswordInputProps {
  password: string;
  setPassword: (value: string) => void;
  isPasswordValid: boolean | null;
  validatePassword: (value: string) => void;
}

export const PasswordInput: React.FC<PasswordInputProps> = ({
  password,
  setPassword,
  isPasswordValid,
  validatePassword,
}) => {
  const [showPassword, setShowPassword] = useState(false);

  return (
    <div className="space-y-2">
      <Label htmlFor="password" className="flex items-center gap-2">
        <Lock size={16} />
        Password
      </Label>
      <div className="relative">
        <Input
          id="password"
          type={showPassword ? "text" : "password"} // Thay đổi type dựa trên showPassword
          value={password}
          onChange={(e) => {
            setPassword(e.target.value);
            validatePassword(e.target.value);
          }}
          className={`pr-10 ${isPasswordValid === true
            ? "border-primary focus:ring-primary"
            : isPasswordValid === false
              ? "border-destructive focus:ring-destructive"
              : ""
            }`}
        />
        <motion.span
          className="absolute right-3 top-1/2 -translate-y-1/2 cursor-pointer text-muted-foreground hover:text-foreground"
          onClick={() => setShowPassword((prev) => !prev)}
          initial={{ scale: 1 }}
          whileTap={{ scale: 0.9 }}
        >
          <AnimatePresence mode="wait">
            {showPassword ? (
              <motion.div
                key="eye-off"
                initial={{ opacity: 0, scale: 0.8 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.8 }}
                transition={{ duration: 0.1 }}
              >
                <EyeOff size={18} />
              </motion.div>
            ) : (
              <motion.div
                key="eye"
                initial={{ opacity: 0, scale: 0.8 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.8 }}
                transition={{ duration: 0.1 }}
              >
                <Eye size={18} />
              </motion.div>
            )}
          </AnimatePresence>
        </motion.span>

        <AnimatePresence>
          {isPasswordValid !== null && (
            <motion.span
              initial={{ scale: 0, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0, opacity: 0 }}
              className="absolute right-10 top-1/2 -translate-y-1/2 input-validation-icon" // Điều chỉnh vị trí
            >
              {isPasswordValid ? (
                <Check className="text-emerald-500" size={18} />
              ) : (
                <X className="text-destructive" size={18} />
              )}
            </motion.span>
          )}
        </AnimatePresence>
      </div>
    </div>
  );
};