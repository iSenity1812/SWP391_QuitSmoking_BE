"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { ThemeProvider } from "@/context/ThemeContext"
import { motion } from "framer-motion"
import { FlowerIcon as Butterfly, Cloud, Heart, Leaf } from "lucide-react"

export default function ClientLayout({
  children,
}: {
  children: React.ReactNode
}) {
  const [mounted, setMounted] = useState(false)

  useEffect(() => {
    setMounted(true)
  }, [])

  return (
    <ThemeProvider>
      {mounted && (
        <div className="min-h-screen bg-gradient-to-b from-pink-50 to-blue-50 dark:from-slate-900 dark:to-slate-800 overflow-hidden relative">
          {/* Background animations */}
          <BackgroundAnimations />
          {children}
        </div>
      )}
    </ThemeProvider>
  )
}

function BackgroundAnimations() {
  return (
    <>
      {/* Floating elements */}
      <div className="fixed inset-0 pointer-events-none z-0">
        {Array.from({ length: 10 }).map((_, i) => (
          <motion.div
            key={`butterfly-${i}`}
            className="absolute text-pink-400 dark:text-pink-600"
            initial={{
              x: Math.random() * 100 - 50,
              y: Math.random() * 100 + window.innerHeight,
              opacity: 0.7,
              scale: 0.6 + Math.random() * 0.8,
            }}
            animate={{
              x: Math.random() * window.innerWidth,
              y: -100,
              opacity: [0.4, 0.8, 0.4],
              scale: [0.6, 0.8, 0.6],
            }}
            transition={{
              repeat: Number.POSITIVE_INFINITY,
              duration: 15 + Math.random() * 20,
              ease: "easeInOut",
              delay: i * 2,
            }}
          >
            <Butterfly size={24} />
          </motion.div>
        ))}

        {Array.from({ length: 6 }).map((_, i) => (
          <motion.div
            key={`cloud-${i}`}
            className="absolute text-blue-200 dark:text-blue-900"
            initial={{
              x: -100,
              y: Math.random() * 300,
              opacity: 0.4,
              scale: 1 + Math.random() * 2,
            }}
            animate={{
              x: window.innerWidth + 100,
              opacity: [0.4, 0.7, 0.4],
            }}
            transition={{
              repeat: Number.POSITIVE_INFINITY,
              duration: 30 + Math.random() * 40,
              ease: "linear",
              delay: i * 5,
            }}
          >
            <Cloud size={48} />
          </motion.div>
        ))}

        {Array.from({ length: 10 }).map((_, i) => (
          <motion.div
            key={`leaf-${i}`}
            className="absolute text-green-400 dark:text-green-600"
            initial={{
              x: Math.random() * window.innerWidth,
              y: -50,
              opacity: 0.7,
              rotate: 0,
              scale: 0.6 + Math.random() * 0.5,
            }}
            animate={{
              y: window.innerHeight + 100,
              x: Math.random() * 200 - 100 + i * 100,
              rotate: 360,
              opacity: [0.7, 0.9, 0.7],
            }}
            transition={{
              repeat: Number.POSITIVE_INFINITY,
              duration: 20 + Math.random() * 10,
              ease: "easeInOut",
              delay: i * 3,
            }}
          >
            <Leaf size={20} />
          </motion.div>
        ))}

        {Array.from({ length: 6 }).map((_, i) => (
          <motion.div
            key={`heart-${i}`}
            className="absolute text-rose-400 dark:text-rose-600"
            initial={{
              x: Math.random() * window.innerWidth,
              y: -50,
              opacity: 0.7,
              rotate: 0,
              scale: 0.6 + Math.random() * 0.5,
            }}
            animate={{
              y: window.innerHeight + 100,
              x: Math.random() * 200 - 100 + i * 100,
              rotate: 360,
              opacity: [0.7, 0.9, 0.7],
            }}
            transition={{
              repeat: Number.POSITIVE_INFINITY,
              duration: 20 + Math.random() * 10,
              ease: "easeInOut",
              delay: i * 3,
            }}
          >
            <Heart size={20} />
          </motion.div>
        ))}
      </div>
    </>
  )
}
