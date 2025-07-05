"use client"

import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Star, Award, CheckCircle, Crown, Lock } from "lucide-react"
import type { Coach } from "../types/booking.types"

interface CoachCardProps {
    coach: Coach
    userSubscription: "free" | "premium"
    onViewDetails: (coach: Coach) => void
    onBooking: (coach: Coach) => void
}

export function CoachCard({ coach, userSubscription, onViewDetails, onBooking }: CoachCardProps) {
    if (userSubscription === "free") {
        return (
            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm hover:shadow-lg transition-shadow relative">
                {/* Premium Overlay */}
                <div className="absolute inset-0 bg-black/20 backdrop-blur-[1px] rounded-lg flex items-center justify-center z-10">
                    <div className="bg-white dark:bg-slate-800 p-6 rounded-lg shadow-xl text-center max-w-sm mx-4">
                        <div className="w-16 h-16 bg-gradient-to-r from-amber-500 to-orange-500 rounded-full flex items-center justify-center mx-auto mb-4">
                            <Lock className="w-8 h-8 text-white" />
                        </div>
                        <h3 className="text-xl font-bold text-slate-900 dark:text-white mb-2">Tính năng Premium</h3>
                        <p className="text-slate-600 dark:text-slate-400 mb-4">
                            Bạn cần nâng cấp lên gói Premium để sử dụng tính năng đặt lịch với chuyên gia
                        </p>
                        <Button
                            onClick={() => onBooking(coach)}
                            className="w-full bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600"
                        >
                            <Crown className="w-4 h-4 mr-2" />
                            Nâng cấp Premium
                        </Button>
                    </div>
                </div>

                <CardContent className="p-6">
                    <div className="flex items-start space-x-4">
                        <Avatar className="h-16 w-16">
                            <AvatarImage src={coach.avatar || "/placeholder.svg"} />
                            <AvatarFallback>{coach.name.charAt(0)}</AvatarFallback>
                        </Avatar>
                        <div className="flex-1">
                            <h3 className="font-semibold text-lg text-slate-900 dark:text-white">{coach.name}</h3>
                            <p className="text-sm text-blue-600 dark:text-blue-400 mb-2">{coach.title}</p>

                            <div className="flex items-center space-x-4 text-sm text-slate-600 dark:text-slate-400 mb-3">
                                <div className="flex items-center space-x-1">
                                    <Star className="w-4 h-4 text-yellow-500" />
                                    <span>
                                        {coach.rating} ({coach.reviewCount})
                                    </span>
                                </div>
                                <div className="flex items-center space-x-1">
                                    <Award className="w-4 h-4" />
                                    <span>{coach.experience} năm</span>
                                </div>
                                <div className="flex items-center space-x-1">
                                    <CheckCircle className="w-4 h-4 text-green-500" />
                                    <span>{coach.successRate}%</span>
                                </div>
                            </div>

                            <div className="flex flex-wrap gap-1 mb-4">
                                {coach.specializations.slice(0, 2).map((spec, index) => (
                                    <Badge key={index} variant="outline" className="text-xs">
                                        {spec}
                                    </Badge>
                                ))}
                                {coach.specializations.length > 2 && (
                                    <Badge variant="outline" className="text-xs">
                                        +{coach.specializations.length - 2}
                                    </Badge>
                                )}
                            </div>

                            <div className="flex space-x-2">
                                <Button size="sm" variant="outline" disabled>
                                    Xem Chi Tiết
                                </Button>
                                <Button size="sm" disabled className="opacity-50">
                                    Đặt Lịch
                                </Button>
                            </div>
                        </div>
                    </div>
                </CardContent>
            </Card>
        )
    }

    return (
        <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm hover:shadow-lg transition-shadow">
            <CardContent className="p-6">
                <div className="flex items-start space-x-4">
                    <Avatar className="h-16 w-16">
                        <AvatarImage src={coach.avatar || "/placeholder.svg"} />
                        <AvatarFallback>{coach.name.charAt(0)}</AvatarFallback>
                    </Avatar>
                    <div className="flex-1">
                        <h3 className="font-semibold text-lg text-slate-900 dark:text-white">{coach.name}</h3>
                        <p className="text-sm text-blue-600 dark:text-blue-400 mb-2">{coach.title}</p>

                        <div className="flex items-center space-x-4 text-sm text-slate-600 dark:text-slate-400 mb-3">
                            <div className="flex items-center space-x-1">
                                <Star className="w-4 h-4 text-yellow-500" />
                                <span>
                                    {coach.rating} ({coach.reviewCount})
                                </span>
                            </div>
                            <div className="flex items-center space-x-1">
                                <Award className="w-4 h-4" />
                                <span>{coach.experience} năm</span>
                            </div>
                            <div className="flex items-center space-x-1">
                                <CheckCircle className="w-4 h-4 text-green-500" />
                                <span>{coach.successRate}%</span>
                            </div>
                        </div>

                        <div className="flex flex-wrap gap-1 mb-4">
                            {coach.specializations.slice(0, 2).map((spec, index) => (
                                <Badge key={index} variant="outline" className="text-xs">
                                    {spec}
                                </Badge>
                            ))}
                            {coach.specializations.length > 2 && (
                                <Badge variant="outline" className="text-xs">
                                    +{coach.specializations.length - 2}
                                </Badge>
                            )}
                        </div>

                        <div className="flex space-x-2">
                            <Button size="sm" variant="outline" onClick={() => onViewDetails(coach)}>
                                Xem Chi Tiết
                            </Button>
                            <Button
                                size="sm"
                                onClick={() => onBooking(coach)}
                                className="bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600"
                            >
                                Đặt Lịch
                            </Button>
                        </div>
                    </div>
                </div>
            </CardContent>
        </Card>
    )
}
