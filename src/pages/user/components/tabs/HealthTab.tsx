import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"

export function HealthTab() {
    const healthBenefits = [
        {
            timeframe: "20 phút",
            title: "Nhịp tim và huyết áp bình thường",
            description: "Nhịp tim và huyết áp của bạn giảm xuống mức bình thường, tuần hoàn máu được cải thiện.",
            icon: "heart",
            status: "completed",
        },
        {
            timeframe: "12 giờ",
            title: "Nồng độ CO trong máu giảm",
            description: "Nồng độ carbon monoxide trong máu giảm xuống mức bình thường, oxy được vận chuyển tốt hơn.",
            icon: "activity",
            status: "completed",
        },
        {
            timeframe: "2-12 tuần",
            title: "Tuần hoàn máu cải thiện",
            description: "Tuần hoàn máu được cải thiện và chức năng phổi tăng lên đáng kể.",
            icon: "activity",
            status: "in-progress",
        },
        {
            timeframe: "1-9 tháng",
            title: "Giảm ho và khó thở",
            description: "Ho, nghẹt mũi và khó thở giảm. Lông mao trong phổi phục hồi chức năng bình thường.",
            icon: "activity",
            status: "in-progress",
        },
        {
            timeframe: "1 năm",
            title: "Nguy cơ bệnh tim giảm 50%",
            description: "Nguy cơ mắc bệnh tim mạch vành giảm xuống còn một nửa so với người hút thuốc.",
            icon: "heart",
            status: "in-progress",
        },
        {
            timeframe: "5 năm",
            title: "Nguy cơ đột quỵ giảm",
            description: "Nguy cơ đột quỵ giảm xuống bằng với người không hút thuốc sau 5-15 năm.",
            icon: "brain",
            status: "in-progress",
        },
        {
            timeframe: "10 năm",
            title: "Nguy cơ ung thư phổi giảm 50%",
            description: "Nguy cơ chết vì ung thư phổi giảm xuống còn khoảng một nửa so với người hút thuốc.",
            icon: "activity",
            status: "in-progress",
        },
        {
            timeframe: "15 năm",
            title: "Nguy cơ bệnh tim như người không hút thuốc",
            description: "Nguy cơ mắc bệnh tim mạch vành bằng với người không bao giờ hút thuốc.",
            icon: "heart",
            status: "in-progress",
        },
    ]

    return (
        <div className="space-y-6">
            <div>
                <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-2">Sức khỏe</h2>
                <p className="text-gray-600 dark:text-gray-400">Theo dõi sự cải thiện sức khỏe của bạn kể từ khi bỏ thuốc</p>
            </div>

            {/* Health Benefits Timeline */}
            <Card>
                <CardHeader>
                    <CardTitle>Lợi ích sức khỏe theo thời gian</CardTitle>
                    <CardDescription>Các cải thiện sức khỏe bạn đã đạt được và sẽ đạt được</CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="space-y-4">
                        {healthBenefits.map((benefit, index) => (
                            <div key={index} className="flex items-start space-x-4 p-4 rounded-lg border">
                                <div className="flex-shrink-0">
                                    {/* Icon động */}
                                    {benefit.icon === "heart" && <span className="inline-block"><svg className="w-5 h-5 text-red-500" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M12 21C12 21 4 13.5 4 8.5C4 5.5 6.5 3 9.5 3C11.04 3 12.5 3.99 13 5.36C13.5 3.99 14.96 3 16.5 3C19.5 3 22 5.5 22 8.5C22 13.5 12 21 12 21Z" /></svg></span>}
                                    {benefit.icon === "activity" && <span className="inline-block"><svg className="w-5 h-5 text-blue-500" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12" /></svg></span>}
                                    {benefit.icon === "brain" && <span className="inline-block"><svg className="w-5 h-5 text-purple-500" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M15.5 7.5C15.5 5.01472 13.4853 3 11 3C8.51472 3 6.5 5.01472 6.5 7.5C6.5 8.88071 7.61929 10 9 10H13C14.3807 10 15.5 8.88071 15.5 7.5Z" /><path strokeLinecap="round" strokeLinejoin="round" d="M9 10V21M13 10V21" /></svg></span>}
                                </div>
                                <div className="flex-1 min-w-0">
                                    <div className="flex items-center justify-between mb-1">
                                        <h4 className="text-sm font-medium text-gray-900 dark:text-white">{benefit.title}</h4>
                                        <Badge
                                            variant={benefit.status === "completed" ? "default" : "secondary"}
                                            className={
                                                benefit.status === "completed"
                                                    ? "bg-green-100 text-green-800"
                                                    : benefit.status === "in-progress"
                                                        ? "bg-blue-100 text-blue-800"
                                                        : "bg-gray-100 text-gray-800"
                                            }
                                        >
                                            {benefit.timeframe}
                                        </Badge>
                                    </div>
                                    <p className="text-sm text-gray-600 dark:text-gray-400">{benefit.description}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}
