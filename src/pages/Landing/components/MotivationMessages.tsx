import { RefreshCw } from "lucide-react";
import { AnimatedSection } from "@/components/ui/AnimatedSection";

export function MotivationMessages() {
  const messages = [
    "Every day smoke-free is a victory. Keep going!",
    "You are stronger than your cravings. Believe in yourself!",
    "Each moment without smoking is a step towards a healthier you.",
    "Remember why you started. Your future self will thank you!",
    "Stay focused on your goals. You've got this!",
    "Celebrate your progress, no matter how small.",
    "You are not alone. We're here to support you every step of the way.",
  ];

  const randomQuote = messages[Math.floor(Math.random() * messages.length)];

  return (

    <section className="relative w-full bg-emerald-500 py-6" >
      <AnimatedSection animation="fadeUp" delay={400}>
        <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/10 to-transparent animate-pulse">
        </div>
        <div className="max-w-4xl mx-auto px-6 relative z-10">
          <div className="flex items-center justify-center gap-4 text-center text-white">
            <RefreshCw className="text-3xl animate-spin-slow" />
            <p className="text-xl lg:text-2xl font-bold italic text-white drop-shadow-lg">{randomQuote}</p>
          </div>
        </div>
      </AnimatedSection>
    </section>
  );
}
