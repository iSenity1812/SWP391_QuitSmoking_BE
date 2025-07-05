"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Heart, Brain, Activity, Clock, CheckCircle, ArrowLeft, Home } from "lucide-react"
import { Link } from "react-router-dom"

interface HealthBenefit {
    timeframe: string
    title: string
    description: string
    icon: React.ReactNode
    category: "immediate" | "short-term" | "medium-term" | "long-term"
    achieved?: boolean
}

const healthBenefits: HealthBenefit[] = [
    {
        timeframe: "20 phút",
        title: "Nhịp tim và huyết áp bình thường",
        description: "Nhịp tim và huyết áp của bạn giảm xuống mức bình thường, tuần hoàn máu được cải thiện.",
        icon: <Heart className="w-6 h-6" />,
        category: "immediate",
        achieved: true
    },
    {
        timeframe: "12 giờ",
        title: "Nồng độ CO trong máu giảm",
        description: "Nồng độ carbon monoxide trong máu giảm xuống mức bình thường, oxy được vận chuyển tốt hơn.",
        icon: <Activity className="w-6 h-6" />,
        category: "immediate",
        achieved: true
    },
    {
        timeframe: "2-12 tuần",
        title: "Tuần hoàn máu cải thiện",
        description: "Tuần hoàn máu được cải thiện và chức năng phổi tăng lên đáng kể.",
        icon: <Activity className="w-6 h-6" />,
        category: "short-term",
        achieved: false
    },
    {
        timeframe: "1-9 tháng",
        title: "Giảm ho và khó thở",
        description: "Ho, nghẹt mũi và khó thở giảm. Lông mao trong phổi phục hồi chức năng bình thường.",
        icon: <Activity className="w-6 h-6" />,
        category: "short-term",
        achieved: false
    },
    {
        timeframe: "1 năm",
        title: "Nguy cơ bệnh tim giảm 50%",
        description: "Nguy cơ mắc bệnh tim mạch vành giảm xuống còn một nửa so với người hút thuốc.",
        icon: <Heart className="w-6 h-6" />,
        category: "medium-term",
        achieved: false
    },
    {
        timeframe: "5 năm",
        title: "Nguy cơ đột quỵ giảm",
        description: "Nguy cơ đột quỵ giảm xuống bằng với người không hút thuốc sau 5-15 năm.",
        icon: <Brain className="w-6 h-6" />,
        category: "medium-term",
        achieved: false
    },
    {
        timeframe: "10 năm",
        title: "Nguy cơ ung thư phổi giảm 50%",
        description: "Nguy cơ chết vì ung thư phổi giảm xuống còn khoảng một nửa so với người hút thuốc.",
        icon: <Activity className="w-6 h-6" />,
        category: "long-term",
        achieved: false
    },
    {
        timeframe: "15 năm",
        title: "Nguy cơ bệnh tim như người không hút thuốc",
        description: "Nguy cơ mắc bệnh tim mạch vành bằng với người không bao giờ hút thuốc.",
        icon: <Heart className="w-6 h-6" />,
        category: "long-term",
        achieved: false
    }
]

const categoryColors = {
    immediate: "bg-green-100 text-green-800 border-green-200",
    "short-term": "bg-blue-100 text-blue-800 border-blue-200",
    "medium-term": "bg-yellow-100 text-yellow-800 border-yellow-200",
    "long-term": "bg-purple-100 text-purple-800 border-purple-200"
}

const categoryLabels = {
    immediate: "Tức thì",
    "short-term": "Ngắn hạn",
    "medium-term": "Trung hạn",
    "long-term": "Dài hạn"
}

export default function HealthBenefitsPage() {
    const [selectedCategory, setSelectedCategory] = useState<string | null>(null)

    const filteredBenefits = selectedCategory 
        ? healthBenefits.filter(benefit => benefit.category === selectedCategory)
        : healthBenefits

    return (
        <div className="min-h-screen bg-gradient-to-br from-green-50 to-blue-50 dark:from-slate-900 dark:to-slate-800">
            {/* Navigation Header */}
            <div className="bg-white dark:bg-slate-900 shadow-sm border-b border-slate-200 dark:border-slate-700">
                <div className="container mx-auto px-4 py-4">
                    <div className="flex items-center gap-4">
                        <Link to="/profile" className="flex items-center gap-2 text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white transition-colors">
                            <ArrowLeft className="w-5 h-5" />
                            <span>Quay về Profile</span>
                        </Link>
                        <div className="h-4 w-px bg-slate-300 dark:bg-slate-600"></div>
                        <Link to="/" className="flex items-center gap-2 text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white transition-colors">
                            <Home className="w-5 h-5" />
                            <span>Trang chủ</span>
                        </Link>
                    </div>
                </div>
            </div>
            
            <div className="container mx-auto px-4 py-8">
                {/* Header */}
                <div className="text-center mb-8">
                    <h1 className="text-4xl font-bold text-slate-900 dark:text-white mb-4">
                        Lợi Ích Sức Khỏe Khi Bỏ Thuốc Lá
                    </h1>
                    <p className="text-lg text-slate-600 dark:text-slate-300 max-w-3xl mx-auto">
                        Khám phá những thay đổi tích cực trong cơ thể bạn từ 20 phút đầu tiên đến 15 năm sau khi bỏ thuốc lá.
                        Mỗi khoảnh khắc đều là một bước tiến quan trọng cho sức khỏe của bạn.
                    </p>
                </div>

                {/* Category Filters */}
                <div className="flex flex-wrap justify-center gap-3 mb-8">
                    <Button
                        variant={selectedCategory === null ? "default" : "outline"}
                        onClick={() => setSelectedCategory(null)}
                        className="rounded-full"
                    >
                        Tất cả
                    </Button>
                    {Object.entries(categoryLabels).map(([category, label]) => (
                        <Button
                            key={category}
                            variant={selectedCategory === category ? "default" : "outline"}
                            onClick={() => setSelectedCategory(category)}
                            className="rounded-full"
                        >
                            {label}
                        </Button>
                    ))}
                </div>

                {/* Timeline */}
                <div className="relative">
                    {/* Timeline line */}
                    <div className="absolute left-8 top-0 bottom-0 w-1 bg-gradient-to-b from-green-400 via-blue-400 via-yellow-400 to-purple-400 rounded-full hidden md:block"></div>
                    
                    {/* Benefits */}
                    <div className="space-y-8">
                        {filteredBenefits.map((benefit, index) => (
                            <div
                                key={index}
                                className="relative flex items-start gap-6 group"
                            >
                                {/* Timeline dot */}
                                <div className="hidden md:flex relative z-10 flex-shrink-0 w-16 h-16 bg-white dark:bg-slate-800 rounded-full border-4 border-white dark:border-slate-700 shadow-lg items-center justify-center">
                                    <div className="flex items-center justify-center w-8 h-8 bg-gradient-to-br from-green-400 to-blue-500 rounded-full text-white">
                                        {benefit.achieved ? <CheckCircle className="w-5 h-5" /> : <Clock className="w-5 h-5" />}
                                    </div>
                                </div>

                                {/* Content Card */}
                                <Card className={`flex-1 transition-all duration-300 hover:shadow-xl hover:scale-[1.02] ${
                                    benefit.achieved ? "ring-2 ring-green-200 bg-green-50/50" : ""
                                }`}>
                                    <CardHeader className="pb-3">
                                        <div className="flex items-start justify-between gap-4">
                                            <div className="flex items-center gap-3">
                                                <div className={`p-2 rounded-lg ${
                                                    benefit.category === "immediate" ? "bg-green-100 text-green-600" :
                                                    benefit.category === "short-term" ? "bg-blue-100 text-blue-600" :
                                                    benefit.category === "medium-term" ? "bg-yellow-100 text-yellow-600" :
                                                    "bg-purple-100 text-purple-600"
                                                }`}>
                                                    {benefit.icon}
                                                </div>
                                                <div>
                                                    <CardTitle className="text-xl font-bold text-slate-900 dark:text-white">
                                                        {benefit.title}
                                                    </CardTitle>
                                                    <div className="flex items-center gap-2 mt-1">
                                                        <Badge 
                                                            variant="outline" 
                                                            className={`text-xs font-medium ${categoryColors[benefit.category]}`}
                                                        >
                                                            {categoryLabels[benefit.category]}
                                                        </Badge>
                                                        <Badge variant="secondary" className="text-xs">
                                                            {benefit.timeframe}
                                                        </Badge>
                                                        {benefit.achieved && (
                                                            <Badge variant="default" className="text-xs bg-green-500 hover:bg-green-600">
                                                                Đã đạt được
                                                            </Badge>
                                                        )}
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </CardHeader>
                                    <CardContent>
                                        <CardDescription className="text-slate-600 dark:text-slate-300 leading-relaxed">
                                            {benefit.description}
                                        </CardDescription>
                                    </CardContent>
                                </Card>
                            </div>
                        ))}
                    </div>
                    {/* Link bài báo khoa học ở cuối trang */}
                    <div className="mt-12 text-center">
                        <span className="text-sm text-slate-500 dark:text-slate-400 mr-2">Tham khảo:</span>
                        <a
                            href="https://www.cdc.gov/tobacco/quit_smoking/how_to_quit/benefits/index.htm"
                            target="_blank"
                            rel="noopener noreferrer"
                            className="text-blue-600 dark:text-blue-400 underline hover:text-blue-800"
                        >
                            Lợi ích sức khỏe khi bỏ thuốc lá (CDC.gov)
                        </a>
                    </div>
                </div>

                {/* Motivation Section */}
                <div className="mt-16 text-center">
                    <Card className="bg-gradient-to-r from-green-500 to-blue-600 text-white border-0">
                        <CardContent className="p-8">
                            <h2 className="text-2xl font-bold mb-4">Mỗi Ngày Đều Quan Trọng!</h2>
                            <p className="text-lg mb-6">
                                Cơ thể bạn đang hồi phục mỗi ngày mà bạn không hút thuốc. 
                                Hãy tiếp tục hành trình để đạt được những cột mốc sức khỏe quan trọng này.
                            </p>
                            <div className="flex justify-center gap-4">
                                <Link to="/profile">
                                    <Button variant="outline" className="bg-white text-blue-700 border border-blue-300 hover:bg-blue-50 hover:text-blue-900 font-semibold shadow-sm">
                                        Theo dõi tiến trình
                                    </Button>
                                </Link>
                                <Link to="/plan">
                                    <Button variant="outline" className="border-white text-white hover:bg-white hover:text-slate-900">
                                        Xem kế hoạch của bạn
                                    </Button>
                                </Link>
                            </div>
                            <div className="mt-8 text-sm text-white/80">
                                <span>Nguồn: </span>
                                <a href="https://www.cdc.gov/tobacco/quit_smoking/how_to_quit/benefits/index.htm" target="_blank" rel="noopener noreferrer" className="underline hover:text-blue-200">CDC - Benefits of Quitting Smoking</a>,
                                <a href="https://www.who.int/news-room/fact-sheets/detail/tobacco" target="_blank" rel="noopener noreferrer" className="underline hover:text-blue-200 ml-2">WHO - Tobacco Fact Sheet</a>
                            </div>
                        </CardContent>
                    </Card>
                </div>
            </div>
        </div>
    )
}