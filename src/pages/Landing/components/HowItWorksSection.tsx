export function HowItWorksSection() {
  return (
    <section className="py-16 px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto">
        <div className="text-center mb-12">
          <h2 className="text-3xl sm:text-4xl font-bold text-slate-800 mb-4">Cách Thức Hoạt Động</h2>
          <p className="text-lg text-slate-600 max-w-2xl mx-auto">
            Bắt đầu rất đơn giản. Thực hiện theo ba bước dễ dàng này để bắt đầu hành trình không khói thuốc của bạn
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div className="text-center">
            <div className="w-24 h-24 bg-gradient-to-br from-purple-200 to-violet-200 rounded-full flex items-center justify-center mb-6 mx-auto">
              <span className="text-3xl font-bold text-coral-600">1</span>
            </div>
            <h3 className="text-2xl font-semibold text-slate-800 mb-4 dark:text-white">Đặt Ngày Bỏ Thuốc</h3>
            <p className="text-slate-600 leading-relaxed dark:text-slate-300">
              Chọn thời điểm bạn muốn bỏ thuốc lá và để chúng tôi giúp bạn chuẩn bị cho thành công với kế hoạch cá nhân
              hóa
            </p>
          </div>

          <div className="text-center">
            <div className="w-24 h-24 bg-gradient-to-br from-emerald-200 to-green-200 rounded-full flex items-center justify-center mb-6 mx-auto">
              <span className="text-3xl font-bold text-emerald-600">2</span>
            </div>
            <h3 className="text-2xl font-semibold text-slate-800 mb-4 dark:text-white">Theo Dõi Tiến Độ</h3>
            <p className="text-slate-600 leading-relaxed">
              Theo dõi số ngày không khói thuốc, cải thiện sức khỏe và số tiền tiết kiệm được với bảng điều khiển trực
              quan
            </p>
          </div>

          <div className="text-center">
            <div className="w-24 h-24 bg-gradient-to-br from-sky-200 to-blue-200 rounded-full flex items-center justify-center mb-6 mx-auto">
              <span className="text-3xl font-bold text-sky-600">3</span>
            </div>
            <h3 className="text-2xl font-semibold text-slate-800 mb-4">Nhận Hỗ Trợ & Duy Trì Động Lực</h3>
            <p className="text-slate-600 leading-relaxed">
              Kết nối với cộng đồng, truy cập bài tập thở và nhận động viên hàng ngày
            </p>
          </div>
        </div>
      </div>
    </section>
  )
}
