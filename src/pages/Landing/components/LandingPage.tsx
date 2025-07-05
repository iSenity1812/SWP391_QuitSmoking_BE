import { BenefitsSection } from "@/pages/Landing/components/BenefitsSection";
import { HeroSection } from "@/pages/Landing/components/HeroSection";
import { Navbar } from "@/components/ui/Navbar";
import { Footer } from "@/components/ui/Footer";
import { MotivationSection } from "@/pages/Landing/components/MotivationSection";
import { MotivationMessages } from "@/pages/Landing/components/MotivationMessages";
import { CoachAndAchievement } from "@/pages/Landing/components/CoachAndAchievement";
import { SubscriptionsSection } from "@/pages/Landing/components/SubscriptionsSection";
import { motion } from "framer-motion";

const pageVariants = {
  initial: {
    opacity: 0,
  },
  in: {
    opacity: 1,
  },
  out: {
    opacity: 0,
  },
}

const pageTransition = {
  type: "tween",
  ease: "easeInOut",
  duration: 0.5,
};

export function LandingPage() {
  return (
    <motion.div initial="initial" animate="in" exit="out" variants={pageVariants} transition={pageTransition}>
      {/* Full height container to ensure the navbar is always at the top */}
      <div className="flex min-h-screen flex-col bg-[#f9f9f9]">
        <Navbar />
        <main className="flex-1">
          <HeroSection />
          <MotivationMessages />
          <BenefitsSection />
          <CoachAndAchievement />
          <SubscriptionsSection />
          <MotivationSection />
        </main>
        <Footer />
      </div>
    </motion.div>
  );
}
