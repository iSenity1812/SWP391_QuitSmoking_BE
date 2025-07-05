"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Progress } from "@/components/ui/progress"
import { Target, Trophy, Calendar, BookOpen, MessageCircle, Download, Users, Heart } from "lucide-react"
import type { User } from "../../types/user-types"
import { useEffect, useState } from "react"
import { achievementService } from "@/services/achievementService"
import { useAuth } from "@/hooks/useAuth"

interface OverviewTabProps {
    user: User
    onTestAchievement: () => void
}

export function OverviewTab({ user: profileUser, onTestAchievement }: OverviewTabProps) {
    const { user: authUser } = useAuth()
    const [nextMilestone, setNextMilestone] = useState<any>(null)
    useEffect(() => {
        if (authUser?.userId) {
            achievementService.getNextMilestone(authUser.userId).then(setNextMilestone)
        }
    }, [authUser?.userId])
    return (
        <div className="space-y-6">
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                {/* Next Milestone */}
                <Card className="lg:col-span-2">
                    <CardHeader>
                        <CardTitle className="flex items-center gap-2">
                            <Target className="h-5 w-5 text-emerald-600" />
                            Cột mốc tiếp theo
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="flex items-center gap-6">
                            <div className="bg-gradient-to-br from-emerald-100 to-emerald-200 dark:from-emerald-900 dark:to-emerald-800 rounded-full p-6">
                                <Trophy className="h-12 w-12 text-emerald-600 dark:text-emerald-400" />
                            </div>
                            <div className="flex-1">
                                <h3 className="text-xl font-semibold text-emerald-700 dark:text-emerald-400 mb-2">
                                    {nextMilestone?.name || "Đã đạt tất cả cột mốc!"}
                                </h3>
                                <p className="text-3xl font-bold text-slate-900 dark:text-white mb-1">
                                    {nextMilestone ? `${nextMilestone.left} ${nextMilestone.type === "DAYS_QUIT" ? "ngày nữa" : nextMilestone.type === "MONEY_SAVED" ? "VNĐ nữa" : nextMilestone.type === "CIGARETTES_NOT_SMOKED" ? "điếu nữa" : ""}` : ""}
                                </p>
                                <p className="text-slate-600 dark:text-slate-400 mb-4">Phần thưởng: {nextMilestone?.reward || "-"}</p>
                                {nextMilestone && (
                                    <Progress
                                        value={
                                            nextMilestone.milestoneValue && nextMilestone.left !== undefined
                                                ? ((Number(nextMilestone.milestoneValue) - Number(nextMilestone.left)) / Number(nextMilestone.milestoneValue)) * 100
                                                : 100
                                        }
                                        className="h-3"
                                    />
                                )}
                                {nextMilestone && (
                                    <p className="text-sm text-slate-500 dark:text-slate-400 mt-2">
                                        {nextMilestone.milestoneValue && nextMilestone.left !== undefined
                                            ? `${Math.round(((Number(nextMilestone.milestoneValue) - Number(nextMilestone.left)) / Number(nextMilestone.milestoneValue)) * 100)}% hoàn thành`
                                            : ""}
                                    </p>
                                )}
                            </div>
                        </div>
                    </CardContent>
                </Card>

                {/* Quick Actions */}
                <Card>
                    <CardHeader>
                        <CardTitle>Hành động nhanh</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-3">
                            <Button className="w-full justify-start" variant="outline">
                                <Calendar className="h-4 w-4 mr-3" />
                                Cập nhật kế hoạch
                            </Button>
                            <Button className="w-full justify-start" variant="outline">
                                <BookOpen className="h-4 w-4 mr-3" />
                                Viết nhật ký
                            </Button>
                            <Button className="w-full justify-start" variant="outline">
                                <MessageCircle className="h-4 w-4 mr-3" />
                                Nhận hỗ trợ
                            </Button>
                            <Button className="w-full justify-start" variant="outline">
                                <Download className="h-4 w-4 mr-3" />
                                Xuất báo cáo
                            </Button>
                            <Button className="w-full justify-start" variant="outline" onClick={onTestAchievement}>
                                <Trophy className="h-4 w-4 mr-3" />
                                Test Achievement
                            </Button>
                        </div>
                    </CardContent>
                </Card>
            </div>

            {/* Recent Activities */}
            <Card>
                <CardHeader>
                    <CardTitle className="flex items-center justify-between">
                        <span>Hoạt động gần đây</span>
                        <Button variant="outline" size="sm">
                            Xem tất cả
                        </Button>
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="space-y-4">
                        {profileUser.recentActivities.map((activity, index) => (
                            <div key={index} className="flex items-center gap-4 p-3 bg-slate-50 dark:bg-slate-800 rounded-lg">
                                <div
                                    className={`rounded-full p-2 ${activity.type === "achievement"
                                            ? "bg-yellow-100 dark:bg-yellow-900"
                                            : activity.type === "milestone"
                                                ? "bg-green-100 dark:bg-green-900"
                                                : activity.type === "social"
                                                    ? "bg-blue-100 dark:bg-blue-900"
                                                    : "bg-purple-100 dark:bg-purple-900"
                                        }`}
                                >
                                    {activity.type === "achievement" && <Trophy className="h-4 w-4 text-yellow-600" />}
                                    {activity.type === "milestone" && <Target className="h-4 w-4 text-green-600" />}
                                    {activity.type === "social" && <Users className="h-4 w-4 text-blue-600" />}
                                    {activity.type === "health" && <Heart className="h-4 w-4 text-purple-600" />}
                                </div>
                                <div className="flex-1">
                                    <p className="font-medium text-slate-900 dark:text-white">{activity.message}</p>
                                    <p className="text-sm text-slate-500 dark:text-slate-400">{activity.time}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}
