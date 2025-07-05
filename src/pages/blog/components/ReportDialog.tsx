"use client"

import type React from "react"
import { useState } from "react"
import { AlertCircle } from "lucide-react"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import type { ReportFormData } from "../types/blog-types"

interface ReportDialogProps {
    isOpen: boolean
    onClose: () => void
    onSubmit: (data: ReportFormData) => void
    contentTitle: string
}

const ReportDialog: React.FC<ReportDialogProps> = ({ isOpen, onClose, onSubmit, contentTitle }) => {
    const [reason, setReason] = useState("")
    const [error, setError] = useState("")

    const handleSubmit = () => {
        if (!reason.trim() || reason.length < 10) {
            setError("Vui lòng nhập lý do báo cáo (ít nhất 10 ký tự)")
            return
        }

        onSubmit({
            reason,
            reportType: "Content Report",
            reportedContentType: "Blog",
        })

        // Reset form
        setReason("")
        setError("")
    }

    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="sm:max-w-md">
                <DialogHeader>
                    <DialogTitle>Báo cáo nội dung</DialogTitle>
                    <DialogDescription>Báo cáo nội dung không phù hợp hoặc vi phạm quy định cộng đồng</DialogDescription>
                </DialogHeader>
                <div className="space-y-4 py-4">
                    <div>
                        <p className="text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">Nội dung báo cáo:</p>
                        <p className="text-sm text-slate-600 dark:text-slate-400 italic">"{contentTitle}"</p>
                    </div>
                    <div className="space-y-2">
                        <Label htmlFor="reason">
                            Lý do báo cáo <span className="text-red-500">*</span>
                        </Label>
                        <Textarea
                            id="reason"
                            value={reason}
                            onChange={(e) => {
                                setReason(e.target.value)
                                if (error) setError("")
                            }}
                            placeholder="Vui lòng mô tả chi tiết lý do báo cáo nội dung này..."
                            className={`resize-none h-32 ${error ? "border-red-500" : ""}`}
                        />
                        {error && (
                            <p className="text-sm text-red-500 flex items-center gap-1">
                                <AlertCircle className="w-4 h-4" />
                                {error}
                            </p>
                        )}
                    </div>
                </div>
                <DialogFooter>
                    <Button variant="outline" onClick={onClose}>
                        Hủy
                    </Button>
                    <Button className="bg-orange-500 hover:bg-orange-600" onClick={handleSubmit}>
                        Gửi báo cáo
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    )
}

export default ReportDialog
