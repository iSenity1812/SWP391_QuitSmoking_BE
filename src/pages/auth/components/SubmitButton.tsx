// src/components/auth/SubmitButton.tsx
import type React from "react"
import { Button } from "@/components/ui/button"
import { AnimatePresence, motion } from "framer-motion"

interface SubmitButtonProps {
  isEmailValid: boolean | null
  isPasswordValid: boolean | null
  isSubmitting: boolean
}

export const SubmitButton: React.FC<SubmitButtonProps> = ({
  isEmailValid,
  isPasswordValid,
  isSubmitting,
}) => {
  return (
    <motion.div whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }}>
      <Button
        type="submit"
        className="w-full font-bold bg-emerald-500 text-white disabled:opacity-100 hover:bg-emerald-600 relative overflow-hidden group"
        disabled={!isEmailValid || !isPasswordValid || isSubmitting}
      >
        <AnimatePresence mode="wait">
          {isSubmitting ? (
            <motion.div
              key="loading"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              className="absolute inset-0 flex items-center justify-center"
            >
              <svg
                className="animate-spin h-5 w-5 text-white"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
              >
                <circle
                  className="opacity-25"
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  strokeWidth="4"
                ></circle>
                <path
                  className="opacity-75"
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                ></path>
              </svg>
            </motion.div>
          ) : (
            <motion.span
              key="text"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
            >
              Login
            </motion.span>
          )}
        </AnimatePresence>

        <motion.span
          className="absolute inset-0 bg-gradient-to-r from-primary/50 to-secondary/50 opacity-0 group-hover:opacity-20 transition-opacity"
          initial={{ x: "-100%" }}
          whileHover={{ x: "100%" }}
          transition={{ duration: 1, ease: "easeInOut" }}
        />
      </Button>
    </motion.div>
  )
}