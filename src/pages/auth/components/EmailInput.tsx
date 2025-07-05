// src/components/auth/EmailInput.tsx
import type React from "react"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { AnimatePresence, motion } from "framer-motion"
import { Check, X, User } from "lucide-react"

interface EmailInputProps {
  email: string
  setEmail: (email: string) => void
  validateEmail: (email: string) => void
  isEmailValid: boolean | null
}

export const EmailInput: React.FC<EmailInputProps> = ({
  email,
  setEmail,
  validateEmail,
  isEmailValid,
}) => {
  return (
    <div className="space-y-2">
      <Label htmlFor="email" className="flex items-center gap-2">
        <User size={16} />
        Email
      </Label>
      <div className="relative">
        <Input
          id="email"
          type="email"
          placeholder="you@example.com"
          value={email}
          onChange={(e) => {
            setEmail(e.target.value)
            validateEmail(e.target.value)
          }}
          className={`pr-10 ${isEmailValid === true
            ? "border-primary focus:ring-primary"
            : isEmailValid === false
              ? "border-destructive focus:ring-destructive"
              : ""
            }`}
        />
        <AnimatePresence>
          {isEmailValid !== null && (
            <motion.span
              initial={{ scale: 0, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0, opacity: 0 }}
              className="absolute right-3 top-1/2 -translate-y-1/2 input-validation-icon"
            >
              {isEmailValid ? (
                <Check className="text-emerald-500" size={18} />
              ) : (
                <X className="text-destructive" size={18} />
              )}
            </motion.span>
          )}
        </AnimatePresence>
      </div>
      <AnimatePresence>
        {isEmailValid === false && (
          <motion.p
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: "auto", opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            className="text-xs text-destructive mt-1"
          >
            Please enter a valid email address
          </motion.p>
        )}
      </AnimatePresence>
    </div>
  )
}