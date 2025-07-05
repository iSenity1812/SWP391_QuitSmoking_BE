import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Progress } from "@/components/ui/progress"
import { Calendar, Trophy } from "lucide-react"
import type { User } from "../../types/user-types"

interface ProgressTabProps {
    user: User
}

export function ProgressTab({ user }: ProgressTabProps) {
    return (
        <div className="space-y-6">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                {/* Total Plan Days */}
                <Card>
                    <CardHeader>
                        <CardTitle>Tổng Số Ngày Theo Kế Hoạch</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-center mb-6">
                            <div className="bg-gradient-to-br from-blue-100 to-blue-200 dark:from-blue-900 dark:to-blue-800 rounded-full p-8 inline-block mb-4">
                                <Calendar className="h-16 w-16 text-blue-600 dark:text-blue-400" />
                            </div>
                            <h3 className="text-4xl font-bold text-blue-700 dark:text-blue-400 mb-2">
                                {user.daysSmokeFreee * 3} ngày
                            </h3>
                            <p className="text-slate-600 dark:text-slate-400">Tổng số ngày bạn đã thực hiện theo kế hoạch</p>
                        </div>

                        <div className="space-y-4">
                            <div className="flex items-center justify-between p-3 bg-blue-50 dark:bg-blue-900/20 rounded-lg">
                                <span className="font-medium text-slate-900 dark:text-white">Kế hoạch đã tạo</span>
                                <span className="text-blue-600 dark:text-blue-400 font-semibold">3 kế hoạch</span>
                            </div>
                            <div className="flex items-center justify-between p-3 bg-green-50 dark:bg-green-900/20 rounded-lg">
                                <span className="font-medium text-slate-900 dark:text-white">Kế hoạch hoàn thành</span>
                                <span className="text-green-600 dark:text-green-400 font-semibold">2 kế hoạch</span>
                            </div>
                            <div className="flex items-center justify-between p-3 bg-orange-50 dark:bg-orange-900/20 rounded-lg">
                                <span className="font-medium text-slate-900 dark:text-white">Kế hoạch đang thực hiện</span>
                                <span className="text-orange-600 dark:text-orange-400 font-semibold">1 kế hoạch</span>
                            </div>
                        </div>
                    </CardContent>
                </Card>

                {/* Highest Streak Achieved */}
                <Card>
                    <CardHeader>
                        <CardTitle>Streak Cao Nhất Đạt Được</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-center mb-6">
                            <div className="bg-gradient-to-br from-emerald-100 to-emerald-200 dark:from-emerald-900 dark:to-emerald-800 rounded-full p-8 inline-block mb-4">
                                <Trophy className="h-16 w-16 text-emerald-600 dark:text-emerald-400" />
                            </div>
                            <h3 className="text-4xl font-bold text-emerald-700 dark:text-emerald-400 mb-2">
                                {Math.max(user.daysSmokeFreee, 67)} ngày
                            </h3>
                            <p className="text-slate-600 dark:text-slate-400">Kỷ lục streak dài nhất của bạn</p>
                        </div>

                        <div className="space-y-4">
                            <div className="flex items-center justify-between p-3 bg-emerald-50 dark:bg-emerald-900/20 rounded-lg">
                                <span className="font-medium text-slate-900 dark:text-white">Streak hiện tại</span>
                                <span className="text-emerald-600 dark:text-emerald-400 font-semibold">{user.daysSmokeFreee} ngày</span>
                            </div>
                            <div className="flex items-center justify-between p-3 bg-yellow-50 dark:bg-yellow-900/20 rounded-lg">
                                <span className="font-medium text-slate-900 dark:text-white">Lần thử lại</span>
                                <span className="text-yellow-600 dark:text-yellow-400 font-semibold">2 lần</span>
                            </div>
                            <div className="flex items-center justify-between p-3 bg-purple-50 dark:bg-purple-900/20 rounded-lg">
                                <span className="font-medium text-slate-900 dark:text-white">Tỷ lệ thành công</span>
                                <span className="text-purple-600 dark:text-purple-400 font-semibold">85%</span>
                            </div>
                        </div>

                        <div className="mt-6">
                            <div className="flex justify-between items-center mb-2">
                                <span className="text-sm font-medium text-slate-700 dark:text-slate-300">Tiến độ đến kỷ lục mới</span>
                                <span className="text-sm text-slate-500 dark:text-slate-400">
                                    {user.daysSmokeFreee}/{Math.max(user.daysSmokeFreee, 67) + 10}
                                </span>
                            </div>
                            <Progress
                                value={(user.daysSmokeFreee / (Math.max(user.daysSmokeFreee, 67) + 10)) * 100}
                                className="h-3"
                            />
                        </div>
                    </CardContent>
                </Card>
            </div>
        </div>
    )
}
