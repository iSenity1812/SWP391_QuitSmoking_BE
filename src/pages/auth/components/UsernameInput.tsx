// src/components/auth/UsernameInput.tsx
import React from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { User, Check, X } from "lucide-react";

interface UsernameInputProps {
  username: string;
  setUsername: (value: string) => void;
  isUsernameValid: boolean | null;
  validateUsername: (value: string) => void;
}

export const UsernameInput: React.FC<UsernameInputProps> = ({
  username,
  setUsername,
  isUsernameValid,
  validateUsername,
}) => {
  return (
    <div className="space-y-2">
      <Label htmlFor="username" className="flex items-center gap-2">
        <User size={16} />
        Username
      </Label>
      <div className="relative">
        <Input
          id="username"
          placeholder="johndoe"
          value={username}
          onChange={(e) => {
            setUsername(e.target.value);
            validateUsername(e.target.value);
          }}
          className={`pr-10 ${isUsernameValid === true
            ? "border-primary focus:ring-primary"
            : isUsernameValid === false
              ? "border-destructive focus:ring-destructive"
              : ""
            }`}
        />
        <AnimatePresence>
          {isUsernameValid !== null && (
            <motion.span
              initial={{ scale: 0, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0, opacity: 0 }}
              className="absolute right-3 top-1/2 -translate-y-1/2 input-validation-icon"
            >
              {isUsernameValid ? (
                <Check className="text-emerald-500" size={18} />
              ) : (
                <X className="text-destructive" size={18} />
              )}
            </motion.span>
          )}
        </AnimatePresence>
      </div>
      <AnimatePresence>
        {isUsernameValid === false && (
          <motion.p
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: "auto", opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            className="text-xs text-destructive mt-1"
          >
            Username must be at least 3 characters
          </motion.p>
        )}
      </AnimatePresence>
    </div>
  );
};