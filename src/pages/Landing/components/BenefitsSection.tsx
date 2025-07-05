import { BarChart3, Calendar, DollarSign, Target } from "lucide-react"
import { AnimatedSection } from "@/components/ui/AnimatedSection"
import BlurText from "@/components/ui/BlurText"

export function BenefitsSection() {
  return (
    <section id="benefits" className="py-16 px-4 sm:px-6 lg:px-8 bg-white/50 dark:bg-slate-900">
      <AnimatedSection animation="fadeUp" delay={400}>
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-12">
            {/* <h2 className="text-3xl sm:text-4xl font-bold text-slate-800 mb-4 dark:text-white">
              Tại Sao Chọn QuitTogether?
            </h2> */}
            <BlurText
              text="Tại Sao Chọn QuitTogether?"
              delay={200}
              animateBy="words"
              direction="top"
              className="justify-center text-3xl sm:text-4xl font-bold text-slate-800 mb-4 dark:text-white"
            />
            <p className="text-lg text-slate-600 max-w-2xl mx-auto dark:text-slate-300">
              Ứng dụng của chúng tôi giúp việc bỏ thuốc lá trở nên dễ dàng hơn với các phương pháp đã được chứng minh và
              tính năng hỗ trợ
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            <div className="px-6 py-6 bg-gradient-to-br from-teal-50 to-teal-100 dark:from-slate-800 dark:to-slate-700 rounded-3xl overflow-hidden shadow-xl border-2 border-emerald-100 dark:border-slate-600 hover:border-emerald-300 dark:hover:border-emerald-500 transition-all duration-500 hover:scale-105 hover:-translate-y-3 group relative">
              <div className="w-16 h-16 bg-gradient-to-br from-red-200 to-pink-200 rounded-2xl flex items-center justify-center mb-4 mt-6 mx-auto">
                <Calendar className="w-8 h-8 text-red-500" />
              </div>
              <h3 className="text-xl font-semibold text-slate-800 mb-2 text-center dark:text-white">
                Theo Dõi Ngày Không Khói Thuốc
              </h3>
              <p className="text-slate-600 text-center dark:text-slate-300">
                Theo dõi chuỗi ngày thành công và ăn mừng mọi cột mốc trong hành trình bỏ thuốc của bạn
              </p>
            </div>

            <div className="px-6 py-6 bg-gradient-to-br from-teal-50 to-teal-100 dark:from-slate-800 dark:to-slate-700 rounded-3xl overflow-hidden shadow-xl border-2 border-emerald-100 dark:border-slate-600 hover:border-emerald-300 dark:hover:border-emerald-500 transition-all duration-500 hover:scale-105 hover:-translate-y-3 group relative">
              <div className="w-16 h-16 bg-gradient-to-br from-green-200 to-emerald-200 rounded-2xl flex items-center justify-center mb-4 mt-6 mx-auto">
                <DollarSign className="w-8 h-8 text-green-500" />
              </div>
              <h3 className="text-xl font-semibold text-slate-800 mb-2 text-center dark:text-white">Tiền Tiết Kiệm</h3>
              <p className="text-slate-600 text-center dark:text-slate-300">
                Xem chính xác số tiền bạn tiết kiệm được khi không mua thuốc lá
              </p>
            </div>

            <div className="px-6 py-6 bg-gradient-to-br from-teal-50 to-teal-100 dark:from-slate-800 dark:to-slate-700 rounded-3xl overflow-hidden shadow-xl border-2 border-emerald-100 dark:border-slate-600 hover:border-emerald-300 dark:hover:border-emerald-500 transition-all duration-500 hover:scale-105 hover:-translate-y-3 group relative">
              <div className="w-16 h-16 bg-gradient-to-br from-blue-200 to-sky-200 rounded-2xl flex items-center justify-center mb-4 mt-6 mx-auto">
                <BarChart3 className="w-8 h-8 text-blue-500" />
              </div>
              <h3 className="text-xl font-semibold text-slate-800 mb-2 text-center dark:text-white">
                Bảng Điều Khiển Tiến Độ
              </h3>
              <p className="text-slate-600 text-center dark:text-slate-300">
                Hình dung hành trình của bạn với biểu đồ và thống kê chi tiết
              </p>
            </div>

            <div className="px-6 py-6 bg-gradient-to-br from-teal-50 to-teal-100 dark:from-slate-800 dark:to-slate-700 rounded-3xl overflow-hidden shadow-xl border-2 border-emerald-100 dark:border-slate-600 hover:border-emerald-300 dark:hover:border-emerald-500 transition-all duration-500 hover:scale-105 hover:-translate-y-3 group relative">
              <div className="w-16 h-16 bg-gradient-to-br from-purple-200 to-violet-200 rounded-2xl flex items-center justify-center mb-4 mt-6 mx-auto">
                <Target className="w-8 h-8 text-purple-500" />
              </div>
              <h3 className="text-xl font-semibold text-slate-800 mb-2 text-center dark:text-white">
                Động Lực Hàng Ngày
              </h3>
              <p className="text-slate-600 text-center dark:text-slate-300">
                Nhận lời khuyên cá nhân hóa và sự khích lệ để duy trì đúng hướng
              </p>
            </div>
          </div>
        </div>
      </AnimatedSection>
    </section>
  )
}
