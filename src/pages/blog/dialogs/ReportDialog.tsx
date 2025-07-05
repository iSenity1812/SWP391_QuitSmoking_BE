"use client"

import type React from "react"
import { useState } from "react"
import { Flag } from "lucide-react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import type { ReportFormData } from "../types/blog-types"

interface ReportDialogProps {
    isOpen: boolean
    onClose: () => void
    onSubmit: (reportData: ReportFormData) => void
    postTitle: string
    postAuthor: string
}

const ReportDialog: React.FC<ReportDialogProps> = ({ isOpen, onClose, onSubmit, postTitle, postAuthor }) => {
    const [reportData, setReportData] = useState<ReportFormData>({
        reason: "",
        reportType: "Content Report",
        reportedContentType: "Blog",
    })

    const handleChange = (field: keyof ReportFormData, value: string) => {
        setReportData((prev) => ({
            ...prev,
            [field]: value,
        }))
    }

    const handleSubmit = () => {
        if (reportData.reason.trim()) {
            onSubmit(reportData)
        }
    }

    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="sm:max-w-[500px]">
                <DialogHeader>
                    <DialogTitle className="flex items-center gap-2">
                        <Flag className="w-5 h-5 text-red-500" />
                        Báo cáo bài viết
                    </DialogTitle>
                </DialogHeader>
                <div className="py-4">
                    <div className="mb-4">
                        <p className="text-sm text-slate-600 dark:text-slate-300 mb-2">
                            Bài viết: <strong>{postTitle}</strong>
                        </p>
                        <p className="text-sm text-slate-500 dark:text-slate-400">Tác giả: {postAuthor}</p>
                    </div>
                    <div className="grid gap-2">
                        <Label htmlFor="report-reason">Lý do báo cáo</Label>
                        <Textarea
                            id="report-reason"
                            value={reportData.reason}
                            onChange={(e) => handleChange("reason", e.target.value)}
                            placeholder="Vui lòng mô tả lý do báo cáo bài viết này..."
                            className="min-h-[100px]"
                        />
                    </div>
                    <div className="text-xs text-slate-500 dark:text-slate-400 mt-2">
                        Báo cáo sẽ được gửi đến đội ngũ quản trị để xem xét.
                    </div>
                </div>
                <div className="flex justify-end gap-4">
                    <Button variant="outline" onClick={onClose}>
                        Hủy
                    </Button>
                    <Button onClick={handleSubmit} disabled={!reportData.reason.trim()} className="bg-red-500 hover:bg-red-600">
                        Gửi báo cáo
                    </Button>
                </div>
            </DialogContent>
        </Dialog>
    )
}

export default ReportDialog
