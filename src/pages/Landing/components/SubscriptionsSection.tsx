import { AnimatedSection } from "@/components/ui/AnimatedSection"
export const SubscriptionsSection = () => {
    return (
        <section
            id="plans"
            className="py-20 bg-gradient-to-br from-emerald-50 to-white dark:from-slate-800 dark:to-slate-900"
        >
            <AnimatedSection animation="fadeUp" delay={400} className="relative z-10">
                <div className="absolute top-0 right-0 w-96 h-96 bg-gradient-to-br from-emerald-200/30 to-emerald-300/20 dark:from-emerald-500/10 dark:to-emerald-600/30 rounded-full blur-3xl animate-pulse"></div>
                <div className="absolute bottom-0 left-0 w-96 h-96 bg-gradient-to-tr from-emerald-100/40 to-emerald-200/30 dark:from-emerald-600/10 dark:to-emerald-500/5 rounded-full blur-3xl animate-pulse delay-1000"></div>
                <div className="max-w-6xl mx-auto px-6">
                    <div className="text-center mb-16">
                        <h2 className="text-4xl lg:text-5xl font-black mb-6 text-slate-800 dark:text-white">Chọn Gói Của Bạn</h2>
                        <p className="text-xl text-slate-600 dark:text-slate-300 max-w-3xl mx-auto font-medium leading-relaxed">
                            Chọn gói phù hợp nhất cho hành trình bỏ thuốc của bạn!
                        </p>
                    </div>

                    <div className="grid md:grid-cols-6 gap-8 max-w-5xl mx-auto">
                        {[
                            {
                                name: "Miễn Phí",
                                price: "0đ",
                                period: "mỗi tháng",
                                description: "Công cụ cơ bản để bắt đầu hành trình",
                                features: [
                                    "✨ Theo dõi tiến độ cơ bản",
                                    "✨ Truy cập cộng đồng",
                                    "✨ Câu nói động viên hàng ngày",
                                    "✨ Thông tin sức khỏe hạn chế",
                                ],
                                cta: "🚀 Bắt Đầu",
                                popular: false,
                            },
                            {
                                name: "Cao Cấp",
                                price: "119.000đ",
                                period: "mỗi tháng",
                                description: "Mọi thứ bạn cần để bỏ thuốc vĩnh viễn!",
                                features: [
                                    "✨ Theo dõi tiến độ nâng cao",
                                    "✨ Trò chuyện với huấn luyện viên/AI",
                                    "✨ Tạo thử thách tùy chỉnh",
                                    "✨ Bảng điều khiển sức khỏe đầy đủ",
                                    "✨ Máy tính tiết kiệm tiền",
                                    "✨ Truy cập nội dung Cao cấp",
                                    "✨ Tính năng cộng đồng Cao cấp",
                                ],
                                cta: "🚀 Bắt Đầu Hành Trình",
                                popular: true,
                                variants: [
                                    {
                                        id: "prem_14d",
                                        label: "14 Ngày",
                                        price: "119.000đ",
                                        subLabel: "thanh toán mỗi 14 ngày",
                                        cta: "Choose 14 Days",
                                    },
                                    {
                                        id: "prem_1m",
                                        label: "1 Tháng",
                                        price: "209.000đ",
                                        subLabel: "thanh toán mỗi tháng, tiết kiệm 10%",
                                        cta: "Choose 1 Month",
                                        highlight: "Phổ Biến",
                                    },
                                    {
                                        id: "prem_3m",
                                        label: "3 Tháng",
                                        price: "499.000đ",
                                        subLabel: "thanh toán mỗi 3 tháng, tiết kiệm 20%",
                                        cta: "Choose 3 Months",
                                        highlight: "Giá Trị Tốt Nhất",
                                    },
                                ],
                            },
                        ].map((plan, i) => (
                            <AnimatedSection
                                key={i}
                                animation="fadeUp"
                                delay={i * 150}
                                className={`${plan.popular ? "md:col-span-3" : "md:col-span-3"}`}
                            >
                                <div
                                    className={`relative bg-white dark:bg-slate-800 rounded-3xl p-8 shadow-xl border-2 transition-all duration-500 hover:scale-105 hover:-translate-y-2 flex flex-col ${plan.popular
                                        ? " border-emerald-300 dark:border-emerald-500 shadow-2xl shadow-emerald-200/50 dark:shadow-emerald-500/25"
                                        : " border-emerald-100 dark:border-slate-700 hover:border-emerald-300 dark:hover:border-emerald-500"
                                        }`}
                                >
                                    {plan.popular && (
                                        <div className="absolute -top-4 left-1/2 transform -translate-x-1/2 px-6 py-2 bg-gradient-to-r from-emerald-500 to-emerald-600 text-white font-bold text-sm rounded-full shadow-lg">
                                            🌟 Phổ Biến Nhất
                                        </div>
                                    )}
                                    <div className="text-center mb-8">
                                        {/* <div className="text-5xl mb-4 drop-shadow-lg">{plan.emoji}</div> */}
                                        <h3 className="text-2xl font-black mb-4 text-slate-800 dark:text-white">{plan.name}</h3>
                                        <div className="flex items-baseline justify-center gap-2 mb-4">
                                            <span className="text-4xl font-black bg-gradient-to-r from-emerald-500 to-emerald-600 bg-clip-text text-transparent">
                                                {plan.price}
                                            </span>
                                            {plan.period && (
                                                <span className="text-slate-600 dark:text-slate-300 font-semibold">{plan.period}</span>
                                            )}
                                        </div>
                                        <p className="text-slate-600 dark:text-slate-300 font-medium">{plan.description}</p>
                                    </div>
                                    <div className="flex-1 mb-2">
                                        <ul className="space-y-3">
                                            {plan.features.map((feature, j) => (
                                                <li key={j} className="text-slate-600 dark:text-slate-300 font-medium">
                                                    {feature}
                                                </li>
                                            ))}
                                        </ul>

                                        {/* Variants Section for Premium Plan */}
                                        {plan.name === "Cao Cấp" && plan.variants && (
                                            <div className="mb-2 mt-4">
                                                {/* <h4 className="text-lg font-semibold text-slate-700 dark:text-slate-200 mb-4 text-center">Choose your duration:</h4> */}
                                                <div className="space-y-3">
                                                    {plan.variants.map((variant) => (
                                                        <div
                                                            key={variant.id}
                                                            className={`relative p-4 border-2 rounded-xl cursor-pointer transition-all hover:shadow-lg dark:hover:border-emerald-400 ${variant.highlight === "Giá Trị Tốt Nhất" || variant.highlight === "Phổ Biến" ? "border-emerald-400 dark:border-emerald-500 bg-emerald-50/50 dark:bg-emerald-500/10" : "border-slate-200 dark:border-slate-600 hover:border-emerald-300 bg-slate-50 dark:bg-slate-700/50"}`}
                                                        >
                                                            {variant.highlight && (
                                                                <div
                                                                    className={`absolute -top-3 right-3 text-xs font-bold px-2 py-1 rounded-full text-white ${variant.highlight === "Giá Trị Tốt Nhất" ? "bg-yellow-500" : "bg-emerald-500"}`}
                                                                >
                                                                    {variant.highlight}
                                                                </div>
                                                            )}
                                                            <div className="flex justify-between items-center">
                                                                <div>
                                                                    <h5 className="font-bold text-slate-800 dark:text-white">{variant.label}</h5>
                                                                    <p className="text-sm text-slate-500 dark:text-slate-400">{variant.subLabel}</p>
                                                                </div>
                                                                <div className="text-right">
                                                                    <span className="text-xl font-bold text-emerald-600 dark:text-emerald-400">
                                                                        {variant.price}
                                                                    </span>
                                                                    {variant.label !== "1 Tháng" && (
                                                                        <span className="text-xs text-slate-500 dark:text-slate-400 block">
                                                                            /{variant.label}
                                                                        </span>
                                                                    )}
                                                                </div>
                                                            </div>
                                                            {/* Optionally, add a button for each variant if the main CTA changes
                                                        <button className="mt-2 w-full py-2 text-sm rounded-lg font-semibold bg-emerald-100 text-emerald-700 hover:bg-emerald-200 dark:bg-emerald-700 dark:text-emerald-100 dark:hover:bg-emerald-600">
                                                            {variant.cta}
                                                        </button>
                                                        */}
                                                        </div>
                                                    ))}
                                                </div>
                                            </div>
                                        )}
                                    </div>
                                    <div className="mt-auto">
                                        <p className="text-sm text-slate-500 dark:text-slate-400 mb-4">Không cần thẻ tín dụng.</p>
                                    </div>

                                    <button
                                        className={`w-full py-4 rounded-xl font-bold text-lg transition-all duration-300 hover:scale-105 hover:-translate-y-1 shadow-lg ${plan.popular
                                            ? "text-white bg-gradient-to-r from-emerald-500 to-emerald-600 hover:from-emerald-600 hover:to-emerald-700 shadow-emerald-200/50 dark:shadow-emerald-500/25"
                                            : "text-slate-700 dark:text-slate-300 bg-gradient-to-r from-emerald-50 to-emerald-100 dark:from-slate-700 dark:to-slate-600 border-2 border-emerald-200 dark:border-slate-600 hover:border-emerald-300 dark:hover:border-emerald-500"
                                            }`}
                                    >
                                        {plan.cta}
                                    </button>
                                </div>
                            </AnimatedSection>
                        ))}
                    </div>
                </div>
            </AnimatedSection>
        </section>
    )
}
