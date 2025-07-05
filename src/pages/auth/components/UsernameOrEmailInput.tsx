// src/components/auth/UsernameOrEmailInput.tsx
import React from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { User, Mail, Check, X } from "lucide-react";

interface UsernameOrEmailInputProps {
  value: string;
  setValue: (value: string) => void;
  isValid: boolean | null;
  validate: (value: string) => void; // Hàm validate sẽ kiểm tra cả email và username
}

export const UsernameOrEmailInput: React.FC<UsernameOrEmailInputProps> = ({
  value,
  setValue,
  isValid,
  validate,
}) => {
  // Hàm này chỉ để hiển thị icon và thông báo lỗi, logic validation chính nằm ở prop 'validate'
  const isEmail = (input: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(input);

  return (
    <div className="space-y-2">
      <Label htmlFor="username-or-email" className="flex items-center gap-2">
        {/* Icon có thể là User hoặc Mail tùy thuộc vào input hiện tại, hoặc một icon chung */}
        {isEmail(value) ? <Mail size={16} /> : <User size={16} />}
        Username or Email
      </Label>
      <div className="relative">
        <Input
          id="username-or-email"
          placeholder="johndoe or you@example.com"
          value={value}
          onChange={(e) => {
            setValue(e.target.value);
            validate(e.target.value);
          }}
          className={`pr-10 ${isValid === true
              ? "border-primary focus:ring-primary"
              : isValid === false
                ? "border-destructive focus:ring-destructive"
                : ""
            }`}
        />
        <AnimatePresence>
          {isValid !== null && (
            <motion.span
              initial={{ scale: 0, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0, opacity: 0 }}
              className="absolute right-3 top-1/2 -translate-y-1/2 input-validation-icon"
            >
              {isValid ? (
                <Check className="text-primary" size={18} />
              ) : (
                <X className="text-destructive" size={18} />
              )}
            </motion.span>
          )}
        </AnimatePresence>
      </div>
      <AnimatePresence>
        {isValid === false && (
          <motion.p
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: "auto", opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            className="text-xs text-destructive mt-1"
          >
            Please enter a valid email or a username (at least 3 characters)
          </motion.p>
        )}
      </AnimatePresence>
    </div>
  );
};