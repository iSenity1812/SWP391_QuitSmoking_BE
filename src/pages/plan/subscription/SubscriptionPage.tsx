"use client"
import { useState } from "react"
import { motion, AnimatePresence } from "framer-motion"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Check, Crown, Star, Shield, Smartphone, Building, ChevronLeft, Sparkles, ArrowLeft } from "lucide-react"
import { Link } from "react-router-dom"

interface PlanOption {
    id: string
    name: string
    price: string
    originalPrice?: string
    duration: string
    description: string
    features: string[]
    popular?: boolean
    savings?: string
}

const freePlan: PlanOption = {
    id: "free",
    name: "Miễn Phí",
    price: "0 VNĐ",
    duration: "Vĩnh viễn",
    description: "Bắt đầu hành trình cai thuốc với các tính năng cơ bản",
    features: [
        "Theo dõi tiến trình cơ bản",
        "Tính toán tiền tiết kiệm",
        "Lịch cai thuốc",
        "Thành tựu cơ bản",
        "Cộng đồng hỗ trợ",
    ],
}

const premiumPlans: PlanOption[] = [
    {
        id: "premium-2weeks",
        name: "Premium 2 Tuần",
        price: "49,000 VNĐ",
        duration: "14 ngày",
        description: "Trải nghiệm đầy đủ tính năng Premium",
        features: [
            "Tất cả tính năng miễn phí",
            "Tư vấn với chuyên gia",
            "Kế hoạch cá nhân hóa",
            "Theo dõi sức khỏe chi tiết",
            "Bài tập thở hướng dẫn",
            "Nhắc nhở thông minh",
            "Báo cáo tiến trình chi tiết",
            "Hỗ trợ 24/7",
        ],
    },
    {
        id: "premium-1month",
        name: "Premium 1 Tháng",
        price: "89,000 VNĐ",
        originalPrice: "98,000 VNĐ",
        duration: "30 ngày",
        description: "Lựa chọn phổ biến nhất",
        popular: true,
        savings: "Tiết kiệm 9,000 VNĐ",
        features: [
            "Tất cả tính năng 2 tuần",
            "Phân tích hành vi chi tiết",
            "Kế hoạch dinh dưỡng",
            "Theo dõi cảm xúc",
            "Cộng đồng Premium",
            "Webinar độc quyền",
        ],
    },
    {
        id: "premium-3months",
        name: "Premium 3 Tháng",
        price: "199,000 VNĐ",
        originalPrice: "267,000 VNĐ",
        duration: "90 ngày",
        description: "Giá trị tốt nhất cho hành trình dài hạn",
        savings: "Tiết kiệm 68,000 VNĐ",
        features: [
            "Tất cả tính năng 1 tháng",
            "Mentor cá nhân",
            "Kế hoạch tập luyện",
            "Theo dõi y tế chuyên sâu",
            "Ưu tiên hỗ trợ",
            "Chứng chỉ hoàn thành",
        ],
    },
]

const paymentMethods = [
    {
        id: "momo",
        name: "MoMo",
        icon: Smartphone,
        description: "Thanh toán qua ví điện tử MoMo",
    },
    {
        id: "banking",
        name: "Chuyển khoản ngân hàng",
        icon: Building,
        description: "Chuyển khoản qua ngân hàng",
    },
]

export default function SubscriptionPage() {
    const [step, setStep] = useState<"plan-selection" | "payment-method" | "payment-details">("plan-selection")
    const [selectedPlan, setSelectedPlan] = useState<PlanOption | null>(null)
    const [selectedPaymentMethod, setSelectedPaymentMethod] = useState<string>("")
    const [paymentDetails, setPaymentDetails] = useState({
        cardNumber: "",
        expiryDate: "",
        cvv: "",
        holderName: "",
        phoneNumber: "",
    })

    const handlePlanSelect = (plan: PlanOption) => {
        if (plan.id === "free") {
            // Handle free plan selection - could redirect back or show confirmation
            alert("Bạn đã chọn gói miễn phí!")
            window.location.href = "/profile"
        } else {
            setSelectedPlan(plan)
            setStep("payment-method")
        }
    }

    const handlePaymentMethodSelect = (methodId: string) => {
        setSelectedPaymentMethod(methodId)
        setStep("payment-details")
    }

    const handlePaymentSubmit = () => {
        // Simulate payment processing
        setTimeout(() => {
            alert(`Thanh toán thành công! Bạn đã nâng cấp lên gói ${selectedPlan?.name}`)
            // Store subscription in localStorage
            const expiryDate = new Date()
            if (selectedPlan?.duration === "14 ngày") expiryDate.setDate(expiryDate.getDate() + 14)
            else if (selectedPlan?.duration === "30 ngày") expiryDate.setDate(expiryDate.getDate() + 30)
            else if (selectedPlan?.duration === "90 ngày") expiryDate.setDate(expiryDate.getDate() + 90)

            localStorage.setItem(
                "userSubscription",
                JSON.stringify({
                    type: "premium",
                    duration: selectedPlan?.duration,
                    expiryDate: expiryDate,
                }),
            )

            // Redirect to profile with booking tab active
            window.location.href = "/profile?tab=booking"
        }, 2000)
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-emerald-50 via-white to-emerald-50 dark:from-slate-900 dark:via-slate-800 dark:to-slate-900 pt-20">
            <div className="max-w-6xl mx-auto p-6">
                {/* Header */}
                <div className="mb-8">
                    <Link to="/profile" className="inline-flex items-center gap-2 text-emerald-600 hover:text-emerald-700 mb-4">
                        <ArrowLeft className="w-4 h-4" />
                        Quay lại Profile
                    </Link>
                    <div className="text-center">
                        <h1 className="text-4xl font-bold text-slate-800 dark:text-white mb-2">Nâng Cấp Premium</h1>
                        <p className="text-xl text-slate-600 dark:text-slate-300">
                            Mở khóa toàn bộ tính năng để tối ưu hóa hành trình cai thuốc của bạn
                        </p>
                    </div>
                </div>

                <AnimatePresence mode="wait">
                    {step === "plan-selection" && (
                        <motion.div
                            key="plan-selection"
                            initial={{ opacity: 0, x: 50 }}
                            animate={{ opacity: 1, x: 0 }}
                            exit={{ opacity: 0, x: -50 }}
                            className="space-y-6"
                        >
                            <div className="grid gap-6">
                                {/* Free Plan */}
                                <Card className="border-2 border-gray-200 dark:border-slate-700 hover:border-emerald-300 dark:hover:border-emerald-600 transition-all duration-300">
                                    <CardHeader>
                                        <div className="flex justify-between items-start">
                                            <div>
                                                <CardTitle className="text-xl">{freePlan.name}</CardTitle>
                                                <p className="text-slate-600 dark:text-slate-300 text-sm mt-1">{freePlan.description}</p>
                                            </div>
                                            <div className="text-right">
                                                <div className="text-2xl font-bold text-emerald-600">{freePlan.price}</div>
                                                <div className="text-sm text-slate-500">{freePlan.duration}</div>
                                            </div>
                                        </div>
                                    </CardHeader>
                                    <CardContent>
                                        <div className="space-y-4">
                                            <div className="grid grid-cols-1 md:grid-cols-2 gap-2">
                                                {freePlan.features.map((feature, index) => (
                                                    <div key={index} className="flex items-center gap-2">
                                                        <Check className="w-4 h-4 text-emerald-500" />
                                                        <span className="text-sm">{feature}</span>
                                                    </div>
                                                ))}
                                            </div>
                                            <Button
                                                onClick={() => handlePlanSelect(freePlan)}
                                                className="w-full bg-emerald-500 hover:bg-emerald-600"
                                            >
                                                Tiếp Tục Miễn Phí
                                            </Button>
                                        </div>
                                    </CardContent>
                                </Card>

                                {/* Premium Plans */}
                                <div className="grid md:grid-cols-3 gap-6">
                                    {premiumPlans.map((plan) => (
                                        <Card
                                            key={plan.id}
                                            className={`relative border-2 transition-all duration-300 hover:scale-105 hover:-translate-y-1 ${plan.popular
                                                    ? "border-emerald-500 bg-emerald-50 dark:bg-emerald-900/20 shadow-xl"
                                                    : "border-gray-200 dark:border-slate-700 hover:border-emerald-300 dark:hover:border-emerald-600"
                                                }`}
                                        >
                                            {plan.popular && (
                                                <div className="absolute -top-3 left-1/2 transform -translate-x-1/2">
                                                    <Badge className="bg-emerald-500 text-white px-3 py-1">
                                                        <Star className="w-3 h-3 mr-1" />
                                                        Phổ biến nhất
                                                    </Badge>
                                                </div>
                                            )}

                                            <CardHeader>
                                                <div className="text-center">
                                                    <div className="w-12 h-12 bg-gradient-to-br from-emerald-400 to-emerald-600 rounded-full flex items-center justify-center mx-auto mb-3">
                                                        <Crown className="w-6 h-6 text-white" />
                                                    </div>
                                                    <CardTitle className="text-lg">{plan.name}</CardTitle>
                                                    <p className="text-slate-600 dark:text-slate-300 text-sm mt-1">{plan.description}</p>

                                                    <div className="mt-4">
                                                        <div className="flex items-center justify-center gap-2">
                                                            <span className="text-2xl font-bold text-emerald-600">{plan.price}</span>
                                                            {plan.originalPrice && (
                                                                <span className="text-sm text-slate-500 line-through">{plan.originalPrice}</span>
                                                            )}
                                                        </div>
                                                        <div className="text-sm text-slate-500">{plan.duration}</div>
                                                        {plan.savings && (
                                                            <Badge
                                                                variant="secondary"
                                                                className="mt-2 bg-orange-100 text-orange-700 dark:bg-orange-900 dark:text-orange-300"
                                                            >
                                                                {plan.savings}
                                                            </Badge>
                                                        )}
                                                    </div>
                                                </div>
                                            </CardHeader>

                                            <CardContent>
                                                <div className="space-y-4">
                                                    <div className="space-y-2">
                                                        {plan.features.map((feature, index) => (
                                                            <div key={index} className="flex items-center gap-2">
                                                                <Check className="w-4 h-4 text-emerald-500 flex-shrink-0" />
                                                                <span className="text-sm">{feature}</span>
                                                            </div>
                                                        ))}
                                                    </div>

                                                    <Button
                                                        onClick={() => handlePlanSelect(plan)}
                                                        className={`w-full ${plan.popular
                                                                ? "bg-gradient-to-r from-emerald-500 to-emerald-600 hover:from-emerald-600 hover:to-emerald-700"
                                                                : "bg-emerald-500 hover:bg-emerald-600"
                                                            }`}
                                                    >
                                                        Chọn Gói Này
                                                    </Button>
                                                </div>
                                            </CardContent>
                                        </Card>
                                    ))}
                                </div>
                            </div>
                        </motion.div>
                    )}

                    {step === "payment-method" && selectedPlan && (
                        <motion.div
                            key="payment-method"
                            initial={{ opacity: 0, x: 50 }}
                            animate={{ opacity: 1, x: 0 }}
                            exit={{ opacity: 0, x: -50 }}
                            className="space-y-6"
                        >
                            <div className="text-center space-y-2">
                                <h2 className="text-3xl font-bold text-slate-800 dark:text-white">Chọn Phương Thức Thanh Toán</h2>
                                <p className="text-slate-600 dark:text-slate-300">Thanh toán an toàn cho gói {selectedPlan.name}</p>
                            </div>

                            {/* Order Summary */}
                            <Card className="bg-emerald-50 dark:bg-emerald-900/20 border-emerald-200 dark:border-emerald-700">
                                <CardHeader>
                                    <CardTitle className="flex items-center gap-2">
                                        <Sparkles className="w-5 h-5 text-emerald-500" />
                                        Tóm Tắt Đơn Hàng
                                    </CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <div className="flex justify-between items-center">
                                        <div>
                                            <p className="font-medium">{selectedPlan.name}</p>
                                            <p className="text-sm text-slate-600 dark:text-slate-300">{selectedPlan.duration}</p>
                                        </div>
                                        <div className="text-right">
                                            <p className="text-xl font-bold text-emerald-600">{selectedPlan.price}</p>
                                            {selectedPlan.originalPrice && (
                                                <p className="text-sm text-slate-500 line-through">{selectedPlan.originalPrice}</p>
                                            )}
                                        </div>
                                    </div>
                                </CardContent>
                            </Card>

                            {/* Payment Methods */}
                            <div className="grid gap-4">
                                {paymentMethods.map((method) => (
                                    <Card
                                        key={method.id}
                                        className={`cursor-pointer border-2 transition-all duration-300 hover:shadow-lg ${selectedPaymentMethod === method.id
                                                ? "border-emerald-500 bg-emerald-50 dark:bg-emerald-900/20"
                                                : "border-gray-200 dark:border-slate-700 hover:border-emerald-300 dark:hover:border-emerald-600"
                                            }`}
                                        onClick={() => handlePaymentMethodSelect(method.id)}
                                    >
                                        <CardContent className="p-4">
                                            <div className="flex items-center gap-4">
                                                <div className="w-12 h-12 bg-gradient-to-br from-blue-400 to-blue-600 rounded-xl flex items-center justify-center">
                                                    <method.icon className="w-6 h-6 text-white" />
                                                </div>
                                                <div className="flex-1">
                                                    <h3 className="font-medium">{method.name}</h3>
                                                    <p className="text-sm text-slate-600 dark:text-slate-300">{method.description}</p>
                                                </div>
                                                {selectedPaymentMethod === method.id && <Check className="w-6 h-6 text-emerald-500" />}
                                            </div>
                                        </CardContent>
                                    </Card>
                                ))}
                            </div>

                            <div className="flex gap-4">
                                <Button variant="outline" onClick={() => setStep("plan-selection")} className="flex-1">
                                    <ChevronLeft className="w-4 h-4 mr-2" />
                                    Quay Lại
                                </Button>
                            </div>
                        </motion.div>
                    )}

                    {step === "payment-details" && selectedPlan && (
                        <motion.div
                            key="payment-details"
                            initial={{ opacity: 0, x: 50 }}
                            animate={{ opacity: 1, x: 0 }}
                            exit={{ opacity: 0, x: -50 }}
                            className="space-y-6"
                        >
                            <div className="text-center space-y-2">
                                <h2 className="text-3xl font-bold text-slate-800 dark:text-white">Thông Tin Thanh Toán</h2>
                                <p className="text-slate-600 dark:text-slate-300">Nhập thông tin để hoàn tất thanh toán</p>
                            </div>

                            <div className="grid md:grid-cols-2 gap-6">
                                {/* Payment Form */}
                                <Card>
                                    <CardHeader>
                                        <CardTitle className="flex items-center gap-2">
                                            <Shield className="w-5 h-5 text-green-500" />
                                            Thông Tin Thanh Toán
                                        </CardTitle>
                                    </CardHeader>
                                    <CardContent className="space-y-4">
                                        {selectedPaymentMethod === "momo" && (
                                            <div>
                                                <Label htmlFor="phoneNumber">Số điện thoại MoMo</Label>
                                                <Input
                                                    id="phoneNumber"
                                                    placeholder="0123456789"
                                                    value={paymentDetails.phoneNumber}
                                                    onChange={(e) => setPaymentDetails((prev) => ({ ...prev, phoneNumber: e.target.value }))}
                                                />
                                            </div>
                                        )}

                                        {selectedPaymentMethod === "banking" && (
                                            <div className="text-center p-4 bg-blue-50 dark:bg-blue-900/20 rounded-lg">
                                                <p className="font-medium mb-2">Thông tin chuyển khoản:</p>
                                                <p className="text-sm">Ngân hàng: Vietcombank</p>
                                                <p className="text-sm">STK: 1234567890</p>
                                                <p className="text-sm">Chủ TK: CONG TY QUIT TOGETHER</p>
                                                <p className="text-sm font-medium text-blue-600">
                                                    Nội dung: PREMIUM {selectedPlan.id.toUpperCase()}
                                                </p>
                                            </div>
                                        )}

                                        <Button
                                            onClick={handlePaymentSubmit}
                                            className="w-full bg-gradient-to-r from-emerald-500 to-emerald-600"
                                        >
                                            Thanh Toán {selectedPlan.price}
                                        </Button>
                                    </CardContent>
                                </Card>

                                {/* Order Summary */}
                                <Card>
                                    <CardHeader>
                                        <CardTitle>Tóm Tắt Đơn Hàng</CardTitle>
                                    </CardHeader>
                                    <CardContent className="space-y-4">
                                        <div className="flex justify-between">
                                            <span>Gói dịch vụ:</span>
                                            <span className="font-medium">{selectedPlan.name}</span>
                                        </div>
                                        <div className="flex justify-between">
                                            <span>Thời hạn:</span>
                                            <span>{selectedPlan.duration}</span>
                                        </div>
                                        {selectedPlan.originalPrice && (
                                            <div className="flex justify-between text-green-600">
                                                <span>Giảm giá:</span>
                                                <span>
                                                    -
                                                    {(
                                                        Number.parseInt(selectedPlan.originalPrice.replace(/[^\d]/g, "")) -
                                                        Number.parseInt(selectedPlan.price.replace(/[^\d]/g, ""))
                                                    ).toLocaleString("vi-VN")}{" "}
                                                    VNĐ
                                                </span>
                                            </div>
                                        )}
                                        <hr />
                                        <div className="flex justify-between text-lg font-bold">
                                            <span>Tổng cộng:</span>
                                            <span className="text-emerald-600">{selectedPlan.price}</span>
                                        </div>

                                        <div className="mt-6 p-4 bg-emerald-50 dark:bg-emerald-900/20 rounded-lg">
                                            <h4 className="font-medium text-emerald-700 dark:text-emerald-300 mb-2">Bạn sẽ nhận được:</h4>
                                            <ul className="space-y-1">
                                                {selectedPlan.features.slice(0, 4).map((feature, index) => (
                                                    <li
                                                        key={index}
                                                        className="text-sm text-emerald-600 dark:text-emerald-400 flex items-center gap-2"
                                                    >
                                                        <Check className="w-3 h-3" />
                                                        {feature}
                                                    </li>
                                                ))}
                                                {selectedPlan.features.length > 4 && (
                                                    <li className="text-sm text-emerald-600 dark:text-emerald-400">
                                                        + {selectedPlan.features.length - 4} tính năng khác
                                                    </li>
                                                )}
                                            </ul>
                                        </div>
                                    </CardContent>
                                </Card>
                            </div>

                            <div className="flex gap-4">
                                <Button variant="outline" onClick={() => setStep("payment-method")} className="flex-1">
                                    <ChevronLeft className="w-4 h-4 mr-2" />
                                    Quay Lại
                                </Button>
                            </div>
                        </motion.div>
                    )}
                </AnimatePresence>
            </div>
        </div>
    )
}
