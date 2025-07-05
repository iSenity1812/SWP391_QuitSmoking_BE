// src/components/auth/AuthHeader.tsx
import type React from "react"
import { motion } from "framer-motion"

export const AuthHeader: React.FC = () => {
  return (
    <motion.div
      initial={{ y: -20, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ delay: 0.2, type: "spring", stiffness: 100 }}
      className="mb-8 text-center"
    >
      <h1 className="text-4xl font-bold text-primary mb-2">Fresh Start</h1>
      <p className="text-muted-foreground">Your journey to a smoke-free life begins here</p>
    </motion.div>
  )
}