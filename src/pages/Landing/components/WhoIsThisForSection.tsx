"use client"

import type React from "react"
import { AnimatedSection } from "@/components/ui/AnimatedSection"

export const WhoIsThisFor: React.FC = () => {
  const userTypes = [
    {
      title: "Heavy Smokers (Pack-a-Day+)",
      emoji: "🚬",
      description:
        "You've been smoking for years and tried to quit multiple times. Let's turn that pack-a-day habit into a victory-a-day celebration. Every cigarette not smoked is a win worth tracking!",
      gradient: "from-red-400 to-red-600",
      bgGradient: "from-red-50 to-red-100",
      darkBgGradient: "dark:from-red-900/20 dark:to-red-800/20",
    },
    {
      title: "Social Smokers (Weekend Warriors)",
      emoji: "🎉",
      description:
        "You only smoke when you're out with friends or having drinks. Perfect! We'll help you find new ways to socialize and celebrate without reaching for that cigarette.",
      gradient: "from-purple-400 to-purple-600",
      bgGradient: "from-purple-50 to-purple-100",
      darkBgGradient: "dark:from-purple-900/20 dark:to-purple-800/20",
    },
    {
      title: "Stress Smokers (Pressure Relievers)",
      emoji: "😤",
      description:
        "Work stress, life pressure, anxiety - cigarettes became your go-to relief. We'll teach you healthier coping mechanisms and track your stress-free victories instead.",
      gradient: "from-orange-400 to-orange-600",
      bgGradient: "from-orange-50 to-orange-100",
      darkBgGradient: "dark:from-orange-900/20 dark:to-orange-800/20",
    },
    {
      title: "Budget-Conscious Quitters (Money Savers)",
      emoji: "💰",
      description:
        "You've calculated how much you spend on cigarettes and it's shocking! Our money-tracking feature will show you exactly how much you're saving - and what you can do with it instead.",
      gradient: "from-green-400 to-green-600",
      bgGradient: "from-green-50 to-green-100",
      darkBgGradient: "dark:from-green-900/20 dark:to-green-800/20",
    },
  ]

  return (
    <section className="py-20 bg-gradient-to-br from-slate-50 to-white dark:from-slate-900 dark:to-slate-800">
      <div className="max-w-7xl mx-auto px-6">
        <AnimatedSection animation="fadeUp">
          <div className="text-center mb-16">
            <h2 className="text-4xl lg:text-5xl font-black mb-6 text-slate-800 dark:text-white">
              Who is QuitSpark For? 🤔
            </h2>
            <p className="text-xl text-slate-600 dark:text-slate-300 max-w-3xl mx-auto font-medium leading-relaxed">
              QuitSpark believes in empowering people who are ready to take control of their health and finances.
            </p>
            <div className="mt-4 px-6 py-3 rounded-full bg-gradient-to-r from-amber-100 to-amber-200 dark:from-amber-900/30 dark:to-amber-800/30 border-2 border-amber-300 dark:border-amber-700 inline-block">
              <p className="text-amber-800 dark:text-amber-300 font-bold text-lg">
                ⚠️ WARNING: Highly addictive to success! Side effects may include: improved health, extra money, and
                uncontrollable confidence! 😄
              </p>
            </div>
          </div>
        </AnimatedSection>

        <div className="grid lg:grid-cols-2 gap-12 items-center">
          {/* Left side - User types */}
          <div className="space-y-8">
            {userTypes.map((type, index) => (
              <AnimatedSection key={index} animation="fadeUp" delay={index * 150}>
                <div
                  className={`relative p-6 rounded-3xl bg-gradient-to-br ${type.bgGradient} ${type.darkBgGradient} border-2 border-emerald-100 dark:border-slate-700 hover:border-emerald-300 dark:hover:border-emerald-500 transition-all duration-500 hover:scale-105 hover:-translate-y-2 shadow-xl hover:shadow-2xl group overflow-hidden`}
                >
                  {/* Decorative border */}
                  <div
                    className={`absolute left-0 top-0 bottom-0 w-1 bg-gradient-to-b ${type.gradient} rounded-l-3xl`}
                  ></div>

                  <div className="flex items-start gap-4">
                    <div
                      className={`w-14 h-14 rounded-2xl bg-gradient-to-br ${type.gradient} flex items-center justify-center text-2xl shadow-lg group-hover:scale-110 group-hover:rotate-6 transition-all duration-300 flex-shrink-0`}
                    >
                      {type.emoji}
                    </div>
                    <div className="flex-1">
                      <h3 className="text-xl font-black mb-3 text-slate-800 dark:text-white leading-tight">
                        {type.title}
                      </h3>
                      <p className="text-slate-600 dark:text-slate-300 leading-relaxed font-medium">
                        {type.description}
                      </p>
                    </div>
                  </div>
                </div>
              </AnimatedSection>
            ))}
          </div>

          {/* Right side - Illustration */}
          <AnimatedSection animation="slideLeft" delay={300}>
            <div className="relative">
              <div className="relative rounded-3xl overflow-hidden shadow-2xl border-4 border-emerald-200 dark:border-slate-600 hover:scale-105 hover:-translate-y-2 transition-all duration-500">
                <img
                  src="/images/who-is-this-for.png"
                  alt="Person relaxing and celebrating their smoke-free journey"
                  className="w-full h-auto object-cover"
                />
                {/* Overlay with stats */}
                <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent">
                  <div className="absolute bottom-6 left-6 right-6">
                    <div className="bg-white/95 dark:bg-slate-800/95 rounded-2xl p-4 backdrop-blur-sm border border-white/20">
                      <div className="grid grid-cols-3 gap-4 text-center">
                        <div>
                          <div className="text-2xl font-black text-emerald-600 dark:text-emerald-400">50K+</div>
                          <div className="text-xs font-semibold text-slate-600 dark:text-slate-300">
                            Success Stories
                          </div>
                        </div>
                        <div>
                          <div className="text-2xl font-black text-emerald-600 dark:text-emerald-400">$2.5M</div>
                          <div className="text-xs font-semibold text-slate-600 dark:text-slate-300">Money Saved</div>
                        </div>
                        <div>
                          <div className="text-2xl font-black text-emerald-600 dark:text-emerald-400">98%</div>
                          <div className="text-xs font-semibold text-slate-600 dark:text-slate-300">Satisfaction</div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              {/* Floating elements */}
              <div className="absolute -top-4 -right-4 w-16 h-16 bg-gradient-to-br from-emerald-400 to-emerald-600 rounded-full flex items-center justify-center text-white text-2xl font-bold shadow-xl animate-bounce">
                ✨
              </div>
              <div className="absolute -bottom-4 -left-4 w-12 h-12 bg-gradient-to-br from-amber-400 to-amber-600 rounded-full flex items-center justify-center text-white text-xl font-bold shadow-xl animate-pulse">
                💪
              </div>
            </div>
          </AnimatedSection>
        </div>

        {/* Bottom CTA */}
        <AnimatedSection animation="fadeUp" delay={600}>
          <div className="text-center mt-16">
            <div className="bg-gradient-to-r from-emerald-500 to-emerald-600 rounded-3xl p-8 shadow-2xl border-2 border-emerald-400">
              <h3 className="text-2xl lg:text-3xl font-black text-white mb-4">
                Ready to Join the Smoke-Free Revolution? 🚀
              </h3>
              <p className="text-emerald-100 text-lg mb-6 max-w-2xl mx-auto">
                No matter which category you fit into, QuitSpark has the tools, community, and motivation you need to
                succeed!
              </p>
              <button className="px-8 py-4 rounded-xl font-bold text-lg text-emerald-600 bg-white hover:bg-emerald-50 hover:scale-105 transition-all duration-300 hover:-translate-y-1 shadow-xl">
                🎯 Start Your Journey Today
              </button>
            </div>
          </div>
        </AnimatedSection>
      </div>
    </section>
  )
}
