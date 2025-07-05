"use client"

import type React from "react"
import { DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Badge } from "@/components/ui/badge"
import { Target, Sparkles, TrendingDown, Zap } from "lucide-react"

interface PlanFormData {
    title: string
    description: string
    startDate: Date
    targetDate: Date
    dailyCigarettes: number
    motivation: string
    cigaretteType: string
}

interface PlanFormDialogProps {
    newPlan: PlanFormData
    selectedPlanType: "gradual" | "cold-turkey" | null
    setSelectedPlanType: (type: "gradual" | "cold-turkey" | null) => void
    onInputChange: (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void
    onSelectChange: (e: React.ChangeEvent<HTMLSelectElement>) => void
    onDateChange: (e: React.ChangeEvent<HTMLInputElement>) => void
    onNumberChange: (e: React.ChangeEvent<HTMLInputElement>) => void
    onSubmit: () => void
    onCancel: () => void
    cigarettePrices: { [key: string]: number }
    isEditing?: boolean
    isPremium?: boolean
}

export const PlanFormDialog: React.FC<PlanFormDialogProps> = ({
    newPlan,
    selectedPlanType,
    setSelectedPlanType,
    onInputChange,
    onSelectChange,
    onDateChange,
    onNumberChange,
    onSubmit,
    onCancel,
    cigarettePrices,
    isEditing = false,
    isPremium = false,
}) => {
    return (
        <DialogContent className="sm:max-w-[700px] bg-white/95 dark:bg-slate-900/95 backdrop-blur-xl border-2 border-emerald-100 dark:border-slate-700 max-h-[90vh] overflow-hidden flex flex-col">
            <DialogHeader className="pb-2">
                <DialogTitle className="flex items-center gap-2 text-slate-900 dark:text-white">
                    {selectedPlanType === "gradual" ? (
                        <>
                            <TrendingDown className="w-5 h-5 text-blue-500" />
                            {isEditing ? "Chỉnh Sửa Kế Hoạch Giảm Dần" : "Tạo Kế Hoạch Giảm Dần"}
                        </>
                    ) : selectedPlanType === "cold-turkey" ? (
                        <>
                            <Zap className="w-5 h-5 text-red-500" />
                            {isEditing ? "Chỉnh Sửa Kế Hoạch Dứt Khoát" : "Tạo Kế Hoạch Dứt Khoát"}
                        </>
                    ) : (
                        <>
                            <Target className="w-5 h-5 text-emerald-500" />
                            {isEditing ? "Chỉnh Sửa Kế Hoạch" : "Chọn Phương Pháp Cai Thuốc"}
                        </>
                    )}
                    {isPremium && <Badge className="bg-emerald-500 text-white text-xs">PREMIUM</Badge>}
                </DialogTitle>
            </DialogHeader>

            {/* Make the content area scrollable */}
            <div className="overflow-y-auto flex-1 pr-1">
                <div className="grid gap-4 py-2">
                    {isPremium && (
                        <div className="bg-emerald-50 dark:bg-emerald-900/20 p-4 rounded-lg border border-emerald-200 dark:border-emerald-700">
                            <p className="text-emerald-700 dark:text-emerald-300 text-sm flex items-center gap-2">
                                <Sparkles className="w-4 h-4" />
                                Bạn đang sử dụng gói Premium! Tận hưởng các tính năng cao cấp và tư vấn cá nhân hóa.
                            </p>
                        </div>
                    )}

                    {/* Plan Type Selection */}
                    {!selectedPlanType && !isEditing && (
                        <div className="space-y-4">
                            <h3 className="text-lg font-medium text-slate-900 dark:text-white">
                                Chọn phương pháp cai thuốc phù hợp với bạn:
                            </h3>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                {/* Gradual Plan Option */}
                                <button
                                    onClick={() => setSelectedPlanType("gradual")}
                                    className="flex flex-col h-full bg-white dark:bg-slate-800 border-2 border-blue-200 dark:border-blue-800 hover:border-blue-500 dark:hover:border-blue-600 rounded-xl p-6 transition-all duration-300 hover:shadow-lg group"
                                >
                                    <div className="flex items-center gap-3 mb-4">
                                        <div className="w-14 h-14 rounded-lg bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                                            <TrendingDown className="w-7 h-7 text-white" />
                                        </div>
                                        <div className="text-left">
                                            <h3 className="font-bold text-lg text-slate-800 dark:text-white">Giảm Dần</h3>
                                            <Badge className="bg-blue-100 text-blue-800 text-xs">Khuyến nghị</Badge>
                                        </div>
                                    </div>
                                    <p className="text-sm text-slate-600 dark:text-slate-300 text-left mb-4">
                                        Giảm số lượng điếu thuốc dần dần theo thời gian, giúp cơ thể thích nghi và giảm triệu chứng cai
                                        thuốc.
                                    </p>
                                    <div className="mt-auto">
                                        <ul className="text-sm text-slate-700 dark:text-slate-300 text-left space-y-1">
                                            <li className="flex items-center gap-2">
                                                <svg
                                                    className="w-4 h-4 text-blue-500"
                                                    fill="none"
                                                    stroke="currentColor"
                                                    viewBox="0 0 24 24"
                                                    xmlns="http://www.w3.org/2000/svg"
                                                >
                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 13l4 4L19 7"></path>
                                                </svg>
                                                Giảm 1 điếu mỗi ngày
                                            </li>
                                            <li className="flex items-center gap-2">
                                                <svg
                                                    className="w-4 h-4 text-blue-500"
                                                    fill="none"
                                                    stroke="currentColor"
                                                    viewBox="0 0 24 24"
                                                    xmlns="http://www.w3.org/2000/svg"
                                                >
                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 13l4 4L19 7"></path>
                                                </svg>
                                                Triệu chứng cai thuốc nhẹ hơn
                                            </li>
                                            <li className="flex items-center gap-2">
                                                <svg
                                                    className="w-4 h-4 text-blue-500"
                                                    fill="none"
                                                    stroke="currentColor"
                                                    viewBox="0 0 24 24"
                                                    xmlns="http://www.w3.org/2000/svg"
                                                >
                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 13l4 4L19 7"></path>
                                                </svg>
                                                Lịch trình giảm dần tự động
                                            </li>
                                        </ul>
                                    </div>
                                </button>

                                {/* Cold Turkey Plan Option */}
                                <button
                                    onClick={() => setSelectedPlanType("cold-turkey")}
                                    className="flex flex-col h-full bg-white dark:bg-slate-800 border-2 border-red-200 dark:border-red-800 hover:border-red-500 dark:hover:border-red-600 rounded-xl p-6 transition-all duration-300 hover:shadow-lg group"
                                >
                                    <div className="flex items-center gap-3 mb-4">
                                        <div className="w-14 h-14 rounded-lg bg-gradient-to-br from-red-400 to-red-600 flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                                            <Zap className="w-7 h-7 text-white" />
                                        </div>
                                        <div className="text-left">
                                            <h3 className="font-bold text-lg text-slate-800 dark:text-white">Dứt Khoát</h3>
                                            <Badge className="bg-red-100 text-red-800 text-xs">Thử thách</Badge>
                                        </div>
                                    </div>
                                    <p className="text-sm text-slate-600 dark:text-slate-300 text-left mb-4">
                                        Ngừng hút thuốc hoàn toàn ngay lập tức, phương pháp hiệu quả nhanh nhưng đòi hỏi ý chí mạnh mẽ.
                                    </p>
                                    <div className="mt-auto">
                                        <ul className="text-sm text-slate-700 dark:text-slate-300 text-left space-y-1">
                                            <li className="flex items-center gap-2">
                                                <svg
                                                    className="w-4 h-4 text-red-500"
                                                    fill="none"
                                                    stroke="currentColor"
                                                    viewBox="0 0 24 24"
                                                    xmlns="http://www.w3.org/2000/svg"
                                                >
                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 13l4 4L19 7"></path>
                                                </svg>
                                                Ngừng hút thuốc ngay lập tức
                                            </li>
                                            <li className="flex items-center gap-2">
                                                <svg
                                                    className="w-4 h-4 text-red-500"
                                                    fill="none"
                                                    stroke="currentColor"
                                                    viewBox="0 0 24 24"
                                                    xmlns="http://www.w3.org/2000/svg"
                                                >
                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 13l4 4L19 7"></path>
                                                </svg>
                                                Kết quả nhanh chóng
                                            </li>
                                            <li className="flex items-center gap-2">
                                                <svg
                                                    className="w-4 h-4 text-red-500"
                                                    fill="none"
                                                    stroke="currentColor"
                                                    viewBox="0 0 24 24"
                                                    xmlns="http://www.w3.org/2000/svg"
                                                >
                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 13l4 4L19 7"></path>
                                                </svg>
                                                Theo dõi streak tự động
                                            </li>
                                        </ul>
                                    </div>
                                </button>
                            </div>
                        </div>
                    )}

                    {/* Plan Type Display */}
                    {selectedPlanType && (
                        <div
                            className={`p-4 rounded-lg border-2 ${selectedPlanType === "gradual"
                                    ? "bg-blue-50 dark:bg-blue-900/20 border-blue-200 dark:border-blue-700"
                                    : "bg-red-50 dark:bg-red-900/20 border-red-200 dark:border-red-700"
                                }`}
                        >
                            <div className="flex items-center gap-3">
                                <div
                                    className={`w-10 h-10 rounded-lg flex items-center justify-center ${selectedPlanType === "gradual" ? "bg-blue-500" : "bg-red-500"
                                        }`}
                                >
                                    {selectedPlanType === "gradual" ? (
                                        <TrendingDown className="w-5 h-5 text-white" />
                                    ) : (
                                        <Zap className="w-5 h-5 text-white" />
                                    )}
                                </div>
                                <div>
                                    <h3
                                        className={`font-semibold ${selectedPlanType === "gradual"
                                                ? "text-blue-800 dark:text-blue-300"
                                                : "text-red-800 dark:text-red-300"
                                            }`}
                                    >
                                        {selectedPlanType === "gradual" ? "Phương Pháp Giảm Dần" : "Phương Pháp Dứt Khoát"}
                                    </h3>
                                    <p
                                        className={`text-sm ${selectedPlanType === "gradual"
                                                ? "text-blue-600 dark:text-blue-400"
                                                : "text-red-600 dark:text-red-400"
                                            }`}
                                    >
                                        {selectedPlanType === "gradual"
                                            ? "Giảm 1 điếu mỗi ngày một cách có kế hoạch"
                                            : "Ngừng hút thuốc hoàn toàn ngay từ hôm nay"}
                                    </p>
                                </div>
                                {!isEditing && (
                                    <button
                                        onClick={() => setSelectedPlanType(null)}
                                        className="ml-auto text-sm text-slate-500 hover:text-slate-700 dark:text-slate-400 dark:hover:text-slate-200"
                                    >
                                        Đổi phương pháp
                                    </button>
                                )}
                            </div>
                        </div>
                    )}

                    {/* Different UI based on plan type */}
                    {selectedPlanType === "cold-turkey" && (
                        <div className="bg-red-50 dark:bg-red-900/20 p-4 rounded-lg border border-red-200 dark:border-red-700">
                            <h4 className="font-medium text-red-800 dark:text-red-300 mb-2 flex items-center gap-2">
                                <Zap className="w-4 h-4" />
                                Lưu ý quan trọng cho phương pháp dứt khoát
                            </h4>
                            <ul className="text-sm text-red-700 dark:text-red-300 space-y-1">
                                <li>• Bạn sẽ ngừng hút thuốc hoàn toàn từ ngày bắt đầu</li>
                                <li>• Có thể gặp triệu chứng cai thuốc trong vài ngày đầu</li>
                                <li>• Hãy chuẩn bị tinh thần và động lực mạnh mẽ</li>
                                <li>• Tìm kiếm sự hỗ trợ từ gia đình và bạn bè</li>
                            </ul>
                        </div>
                    )}

                    {selectedPlanType === "gradual" && (
                        <div className="bg-blue-50 dark:bg-blue-900/20 p-4 rounded-lg border border-blue-200 dark:border-blue-700">
                            <h4 className="font-medium text-blue-800 dark:text-blue-300 mb-2 flex items-center gap-2">
                                <TrendingDown className="w-4 h-4" />
                                Lịch trình giảm dần của bạn
                            </h4>
                            <p className="text-sm text-blue-700 dark:text-blue-300 mb-2">
                                Dựa trên số điếu thuốc hiện tại, bạn sẽ có lịch trình giảm dần như sau:
                            </p>
                            {newPlan.dailyCigarettes > 0 && (
                                <div className="bg-white/60 dark:bg-slate-800/60 p-3 rounded text-sm">
                                    <p>
                                        <strong>Ngày 1:</strong> {newPlan.dailyCigarettes} điếu
                                    </p>
                                    <p>
                                        <strong>Ngày {Math.floor(newPlan.dailyCigarettes / 2)}:</strong>{" "}
                                        {Math.ceil(newPlan.dailyCigarettes / 2)} điếu
                                    </p>
                                    <p>
                                        <strong>Ngày {newPlan.dailyCigarettes + 1}:</strong> 0 điếu (Hoàn thành!)
                                    </p>
                                    <p className="mt-2 font-medium text-blue-800 dark:text-blue-300">
                                        Tổng thời gian: {newPlan.dailyCigarettes + 1} ngày
                                    </p>
                                </div>
                            )}
                        </div>
                    )}

                    {/* Only show form fields if a plan type is selected */}
                    {selectedPlanType && (
                        <>
                            <div className="grid gap-2">
                                <Label htmlFor="title" className="text-sm font-medium text-slate-900 dark:text-white">
                                    Tên Kế Hoạch <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="title"
                                    name="title"
                                    value={newPlan.title}
                                    onChange={onInputChange}
                                    placeholder={
                                        selectedPlanType === "gradual"
                                            ? "VD: Hành trình giảm dần 30 ngày"
                                            : selectedPlanType === "cold-turkey"
                                                ? "VD: Dứt khoát cai thuốc từ hôm nay"
                                                : "VD: Hành trình cai thuốc 30 ngày"
                                    }
                                    className="border-2 border-emerald-100 dark:border-slate-600 focus:border-emerald-300 dark:focus:border-emerald-500 text-slate-900 dark:text-white bg-white dark:bg-slate-800 placeholder:text-slate-500 dark:placeholder:text-slate-400"
                                    required
                                />
                            </div>

                            <div className="grid gap-2">
                                <Label htmlFor="description" className="text-sm font-medium text-slate-900 dark:text-white">
                                    Mô Tả <span className="text-red-500">*</span>
                                </Label>
                                <Textarea
                                    id="description"
                                    name="description"
                                    value={newPlan.description}
                                    onChange={onInputChange}
                                    placeholder="Mô tả chi tiết kế hoạch cai thuốc lá của bạn"
                                    className="border-2 border-emerald-100 dark:border-slate-600 focus:border-emerald-300 dark:focus:border-emerald-500 text-slate-900 dark:text-white bg-white dark:bg-slate-800 placeholder:text-slate-500 dark:placeholder:text-slate-400"
                                    rows={3}
                                    required
                                />
                            </div>

                            <div className="grid gap-2">
                                <Label htmlFor="cigaretteType" className="text-sm font-medium text-slate-900 dark:text-white">
                                    Loại Thuốc Lá
                                </Label>
                                <select
                                    id="cigaretteType"
                                    name="cigaretteType"
                                    value={newPlan.cigaretteType}
                                    onChange={onSelectChange}
                                    className="flex h-10 w-full rounded-md border-2 border-emerald-100 dark:border-slate-600 bg-white dark:bg-slate-800 px-3 py-2 text-sm ring-offset-background focus:border-emerald-300 dark:focus:border-emerald-500 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-emerald-500 focus-visible:ring-offset-2 text-slate-900 dark:text-white"
                                >
                                    {Object.keys(cigarettePrices).map((type) => (
                                        <option
                                            key={type}
                                            value={type}
                                            className="text-slate-900 dark:text-white bg-white dark:bg-slate-800"
                                        >
                                            {type} - {cigarettePrices[type].toLocaleString("vi-VN")} VNĐ/gói
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div className="grid gap-2">
                                    <Label className="text-sm font-medium text-slate-900 dark:text-white">Ngày Bắt Đầu</Label>
                                    <Input
                                        type="date"
                                        name="startDate"
                                        value={newPlan.startDate?.toISOString().split("T")[0]}
                                        onChange={onDateChange}
                                        className="border-2 border-emerald-100 dark:border-slate-600 focus:border-emerald-300 dark:focus:border-emerald-500 text-slate-900 dark:text-white bg-white dark:bg-slate-800"
                                    />
                                </div>
                                <div className="grid gap-2">
                                    <Label className="text-sm font-medium text-slate-900 dark:text-white">Ngày Mục Tiêu</Label>
                                    <Input
                                        type="date"
                                        name="targetDate"
                                        value={newPlan.targetDate?.toISOString().split("T")[0]}
                                        onChange={onDateChange}
                                        className="border-2 border-emerald-100 dark:border-slate-600 focus:border-emerald-300 dark:focus:border-emerald-500 text-slate-900 dark:text-white bg-white dark:bg-slate-800"
                                    />
                                </div>
                            </div>

                            <div className="grid gap-2">
                                <Label htmlFor="dailyCigarettes" className="text-sm font-medium text-slate-900 dark:text-white">
                                    Số Điếu Thuốc Hàng Ngày (trước khi cai)
                                </Label>
                                <Input
                                    id="dailyCigarettes"
                                    name="dailyCigarettes"
                                    type="number"
                                    value={newPlan.dailyCigarettes}
                                    onChange={onNumberChange}
                                    placeholder="VD: 20"
                                    min="0"
                                    className="border-2 border-emerald-100 dark:border-slate-600 focus:border-emerald-300 dark:focus:border-emerald-500 text-slate-900 dark:text-white bg-white dark:bg-slate-800 placeholder:text-slate-500 dark:placeholder:text-slate-400"
                                />
                            </div>

                            <div className="grid gap-2">
                                <Label htmlFor="motivation" className="text-sm font-medium text-slate-900 dark:text-white">
                                    Động Lực Của Bạn <span className="text-red-500">*</span>
                                </Label>
                                <Textarea
                                    id="motivation"
                                    name="motivation"
                                    value={newPlan.motivation}
                                    onChange={onInputChange}
                                    placeholder="Viết lý do tại sao bạn muốn cai thuốc lá..."
                                    className="border-2 border-emerald-100 dark:border-slate-600 focus:border-emerald-300 dark:focus:border-emerald-500 text-slate-900 dark:text-white bg-white dark:bg-slate-800 placeholder:text-slate-500 dark:placeholder:text-slate-400"
                                    rows={3}
                                    required
                                />
                            </div>
                        </>
                    )}
                </div>
            </div>

            {/* Fixed footer with buttons */}
            <div className="flex justify-end gap-4 pt-4 border-t border-slate-200 dark:border-slate-700 mt-2">
                <Button variant="outline" onClick={onCancel}>
                    Hủy
                </Button>
                {selectedPlanType && (
                    <Button
                        onClick={onSubmit}
                        className={`text-white ${selectedPlanType === "gradual"
                                ? "bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700"
                                : "bg-gradient-to-r from-red-500 to-red-600 hover:from-red-600 hover:to-red-700"
                            }`}
                    >
                        {isEditing ? "Cập Nhật Kế Hoạch" : "Tạo Kế Hoạch"}
                    </Button>
                )}
            </div>
        </DialogContent>
    )
}
