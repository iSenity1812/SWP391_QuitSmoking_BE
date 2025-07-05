// src/components/auth/AuthFooter.tsx
import type React from "react"
import { Link } from "react-router-dom"
import { motion } from "framer-motion"
import { Sparkles } from "lucide-react"

export const AuthFooter: React.FC = () => {
  return (
    <div className="text-center text-sm">
      Don&apos;t have an account?{" "}
      <Link to="/register" className="text-emerald-500 hover:underline inline-flex items-center group">
        Register now
        <motion.span
          initial={{ x: -5, opacity: 0 }}
          whileHover={{ x: 0, opacity: 1 }}
          className="ml-1 opacity-0 group-hover:opacity-100 transition-opacity"
        >
          <Sparkles size={12} />
        </motion.span>
      </Link>
    </div>
  )
}