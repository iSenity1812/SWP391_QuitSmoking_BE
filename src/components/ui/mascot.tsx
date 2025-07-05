import { motion } from "framer-motion"

type MascotProps = {
  emotion?: "happy" | "excited" | "thinking" | "sad"
}

export function Mascot({ emotion = "happy" }: MascotProps) {
  // Use different SVG for each emotion
  const mascotSrc = `/placeholder.svg?height=300&width=300`

  return (
    <div className="relative">
      <motion.div
        className="mascot"
        initial={{ y: 20, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{
          type: "spring",
          stiffness: 100,
          damping: 10,
        }}
      >
        <div className="relative w-full h-full">
          <div className="bg-gradient-to-b from-pink-200 to-blue-200 dark:from-pink-900 dark:to-blue-900 rounded-full p-4 shadow-lg">
            <div className="relative w-48 h-48 mx-auto">
              <img src={mascotSrc || "/cham1.jpg"} alt="Cute mascot" className="object-contain w-full h-full" />

              {/* Eyes */}
              <motion.div
                className="absolute top-1/3 left-1/4 w-6 h-6 bg-slate-800 rounded-full"
                animate={{
                  scale: [1, 1.1, 1],
                }}
                transition={{
                  repeat: Number.POSITIVE_INFINITY,
                  repeatType: "reverse",
                  duration: 2,
                }}
              />
              <motion.div
                className="absolute top-1/3 right-1/4 w-6 h-6 bg-slate-800 rounded-full"
                animate={{
                  scale: [1, 1.1, 1],
                }}
                transition={{
                  repeat: Number.POSITIVE_INFINITY,
                  repeatType: "reverse",
                  duration: 2,
                  delay: 0.1,
                }}
              />

              {/* Mouth */}
              <motion.div
                className="absolute bottom-1/4 left-1/2 -translate-x-1/2 w-16 h-8 bg-red-400 dark:bg-red-600 rounded-b-full"
                animate={{
                  width: emotion === "excited" ? 20 : 16,
                  height: emotion === "excited" ? 10 : 8,
                }}
              />

              {/* Blush */}
              <div className="absolute bottom-1/3 left-1/5 w-6 h-3 bg-pink-400 dark:bg-pink-600 rounded-full opacity-60" />
              <div className="absolute bottom-1/3 right-1/5 w-6 h-3 bg-pink-400 dark:bg-pink-600 rounded-full opacity-60" />
            </div>
          </div>

          {/* Speech bubble */}
          <motion.div
            className="absolute -top-12 right-0 bg-white dark:bg-slate-800 p-3 rounded-xl shadow-md"
            initial={{ opacity: 0, scale: 0.8, y: 10 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            transition={{ delay: 1, duration: 0.5 }}
          >
            <p className="text-sm font-medium">
              {emotion === "happy" && "Welcome back!"}
              {emotion === "excited" && "Let's get started!"}
              {emotion === "thinking" && "Hmm..."}
              {emotion === "sad" && "Don't give up!"}
            </p>
            <div className="absolute -bottom-2 right-8 w-4 h-4 bg-white dark:bg-slate-800 transform rotate-45" />
          </motion.div>
        </div>
      </motion.div>

      {/* Floating elements around mascot */}
      <motion.div
        className="absolute -top-4 -left-4 text-green-400 dark:text-green-600"
        animate={{
          y: [0, -10, 0],
          rotate: [0, 10, 0],
          opacity: [0.7, 1, 0.7],
        }}
        transition={{
          repeat: Number.POSITIVE_INFINITY,
          duration: 3,
          ease: "easeInOut",
        }}
      >
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path
            d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
          <path
            d="M8 14C8 14 9.5 16 12 16C14.5 16 16 14 16 14"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
          <path d="M9 9H9.01" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" />
          <path d="M15 9H15.01" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
      </motion.div>

      <motion.div
        className="absolute top-1/2 -right-4 text-blue-400 dark:text-blue-600"
        animate={{
          y: [0, 10, 0],
          rotate: [0, -10, 0],
          opacity: [0.7, 1, 0.7],
        }}
        transition={{
          repeat: Number.POSITIVE_INFINITY,
          duration: 4,
          ease: "easeInOut",
          delay: 1,
        }}
      >
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path
            d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
        </svg>
      </motion.div>

      <motion.div
        className="absolute -bottom-2 left-1/4 text-pink-400 dark:text-pink-600"
        animate={{
          y: [0, 5, 0],
          rotate: [0, 15, 0],
          opacity: [0.7, 1, 0.7],
        }}
        transition={{
          repeat: Number.POSITIVE_INFINITY,
          duration: 3.5,
          ease: "easeInOut",
          delay: 0.5,
        }}
      >
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path
            d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
        </svg>
      </motion.div>
    </div>
  )
}
