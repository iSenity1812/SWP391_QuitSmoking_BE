"use client"

import type React from "react"
import { AlertTriangle } from "lucide-react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"

interface DeleteConfirmDialogProps {
    isOpen: boolean
    onClose: () => void
    onConfirm: () => void
    postTitle: string
}

const DeleteConfirmDialog: React.FC<DeleteConfirmDialogProps> = ({ isOpen, onClose, onConfirm, postTitle }) => {
    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="sm:max-w-[400px]">
                <DialogHeader>
                    <DialogTitle className="flex items-center gap-2">
                        <AlertTriangle className="w-5 h-5 text-red-500" />
                        Xác nhận xóa bài viết
                    </DialogTitle>
                </DialogHeader>
                <div className="py-4">
                    <p className="text-slate-600 dark:text-slate-300 mb-4">Bạn có chắc chắn muốn xóa bài viết này không?</p>
                    <div className="bg-slate-50 dark:bg-slate-800 p-3 rounded-lg">
                        <p className="font-semibold text-slate-800 dark:text-white">{postTitle}</p>
                        <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">Hành động này không thể hoàn tác.</p>
                    </div>
                </div>
                <div className="flex justify-end gap-4">
                    <Button variant="outline" onClick={onClose}>
                        Hủy
                    </Button>
                    <Button onClick={onConfirm} className="bg-red-500 hover:bg-red-600">
                        Xóa bài viết
                    </Button>
                </div>
            </DialogContent>
        </Dialog>
    )
}

export default DeleteConfirmDialog
