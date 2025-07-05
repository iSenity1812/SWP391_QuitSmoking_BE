import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Link } from "react-router-dom"
import "../styles/style.css"
import { AnimatedSection } from "@/components/ui/AnimatedSection"

export function HeroSection() {
  return (
    <section
      id="hero"
      className="py-40 mt-16 bg-gradient-to-br from-emerald-50 via-white to-emerald-50 dark:from-slate-900 dark:via-slate-800 dark:to-slate-900 relative overflow-hidden"
    >
      <AnimatedSection animation="fadeUp" delay={400}>
        <div className="absolute top-0 right-0 w-96 h-96 bg-gradient-to-br from-emerald-200/30 to-emerald-300/20 dark:from-emerald-500/10 dark:to-emerald-600/5 rounded-full blur-3xl animate-pulse"></div>
        <div className="absolute bottom-0 left-0 w-96 h-96 bg-gradient-to-tr from-emerald-100/40 to-emerald-200/30 dark:from-emerald-600/10 dark:to-emerald-500/5 rounded-full blur-3xl animate-pulse delay-1000"></div>

        <div className="container mx-auto relative z-10 grid gap-8 md:grid-cols-2 md:gap-12">
          <div className="flex flex-col justify-center space-y-4">
            <Badge className="inline-block px-3 py-2 rounded-full text-sm font-bold text-white bg-gradient-to-r from-emerald-500 to-emerald-600 shadow-lg shadow-emerald-200/50 dark:shadow-emerald-500/25 mb-2">
              ✨ Bắt Đầu Hành Trình
            </Badge>
            <h1 className="text-4xl font-bold tracking-tight sm:text-5xl md:text-6xl">
              <span className="text-slate-800 dark:text-white">Bỏ Thuốc Lá,</span>
              <br />
              <span className="bg-gradient-to-r from-emerald-500 to-emerald-600 bg-clip-text text-transparent">
                Bắt Đầu Sống
              </span>
            </h1>
            <p className="text-xl text-slate-600 dark:text-slate-300 mb-10 leading-relaxed font-medium">
              Tham gia cùng hàng nghìn người đã thành công bỏ thuốc lá với phương pháp thú vị và hiệu quả của chúng tôi.
              Tiết kiệm tiền, cải thiện sức khỏe và nhận phần thưởng trong suốt hành trình.
            </p>
            <div className="flex flex-col gap-3 sm:flex-row">
              <Link to="/plan">
                <Button
                  size="lg"
                  className="px-8 py-4 rounded-xl font-bold text-lg text-white bg-gradient-to-r from-emerald-500 to-emerald-600 hover:from-emerald-600 hover:to-emerald-700 hover:scale-105 transition-all duration-300 hover:-translate-y-1 shadow-xl shadow-emerald-200/50 dark:shadow-emerald-500/25"
                >
                  Bắt Đầu Miễn Phí
                </Button>
              </Link>
            </div>

            <div className="flex items-center gap-4 pt-4">
              <div className="flex -space-x-2">
                {[1, 2, 3, 4].map((i) => (
                  <Avatar key={i} className="border-2 border-emerald-600 dark:border-white">
                    <AvatarImage
                      // src={`/placeholder.svg?height=40&width=40&text=User${i}`}
                      src="/public/cham1.jpg"
                    />
                    <AvatarFallback>U{i}</AvatarFallback>
                  </Avatar>
                ))}
              </div>
              <div className="text-sm text-muted-foreground">
                <span className="font-medium text-foreground">250+</span> người đã tham gia tuần này
              </div>
            </div>
          </div>

          <div className="flex items-center justify-center">
            <img
              src="/landing_tree_43-light.jpg"
              alt="Soft edge"
              className="soft-edge w-[650px] h-auto object-cover block dark:hidden"
            />
            <img
              src="/landing_tree_43_dark.jpg"
              alt="soft edge dark"
              className="soft-edge w-[650px] h-auto object-cover hidden dark:block"
            />
          </div>
        </div>

        {/* Decorative elements */}
        <div className="absolute -right-20 -top-20 h-[300px] w-[300px] rounded-full bg-emerald-100 opacity-50 blur-3xl"></div>
        <div className="absolute -bottom-32 -left-20 h-[300px] w-[300px] rounded-full bg-teal-100 opacity-50 blur-3xl"></div>
      </AnimatedSection>
    </section>
  )
}
