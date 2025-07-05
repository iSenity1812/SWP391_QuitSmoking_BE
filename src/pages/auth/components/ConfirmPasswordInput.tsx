// src/components/auth/ConfirmPasswordInput.tsx
import React, { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Lock, Check, X, Eye, EyeOff } from "lucide-react";

interface ConfirmPasswordInputProps {
  confirmPassword: string;
  setConfirmPassword: (value: string) => void;
  isConfirmPasswordValid: boolean | null;
  validateConfirmPassword: (value: string) => void;
}

export const ConfirmPasswordInput: React.FC<ConfirmPasswordInputProps> = ({
  confirmPassword,
  setConfirmPassword,
  isConfirmPasswordValid,
  validateConfirmPassword,
}) => {
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  return (
    <div className="space-y-2">
      <Label htmlFor="confirmPassword" className="flex items-center gap-2">
        <Lock size={16} />
        Confirm Password
      </Label>
      <div className="relative">
        <Input
          id="confirmPassword"
          type={showConfirmPassword ? "text" : "password"}
          value={confirmPassword}
          onChange={(e) => {
            setConfirmPassword(e.target.value);
            validateConfirmPassword(e.target.value);
          }}
          className={`pr-10 ${isConfirmPasswordValid === true
              ? "border-primary focus:ring-primary"
              : isConfirmPasswordValid === false
                ? "border-destructive focus:ring-destructive"
                : ""
            }`}
        />
        <motion.span
          className="absolute right-3 top-1/2 -translate-y-1/2 cursor-pointer text-muted-foreground hover:text-foreground"
          onClick={() => setShowConfirmPassword((prev) => !prev)}
          initial={{ scale: 1 }}
          whileTap={{ scale: 0.9 }}
        >
          <AnimatePresence mode="wait">
            {showConfirmPassword ? (
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
          {isConfirmPasswordValid !== null && (
            <motion.span
              initial={{ scale: 0, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0, opacity: 0 }}
              className="absolute right-10 top-1/2 -translate-y-1/2 input-validation-icon"
            >
              {isConfirmPasswordValid ? (
                <Check className="text-primary" size={18} />
              ) : (
                <X className="text-destructive" size={18} />
              )}
            </motion.span>
          )}
        </AnimatePresence>
      </div>
      <AnimatePresence>
        {isConfirmPasswordValid === false && (
          <motion.p
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: "auto", opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            className="text-xs text-destructive mt-1"
          >
            Passwords do not match
          </motion.p>
        )}
      </AnimatePresence>
    </div>
  );
};