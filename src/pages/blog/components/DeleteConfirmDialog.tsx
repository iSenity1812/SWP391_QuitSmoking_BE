"use client"

import type React from "react"

import { useEffect, useRef } from "react"
import { createPortal } from "react-dom"
import { X } from "lucide-react"

interface DeleteConfirmDialogProps {
    isOpen: boolean
    onClose: () => void
    onConfirm: () => void
    title: string
    description: string
    itemName: string
}

const DeleteConfirmDialog: React.FC<DeleteConfirmDialogProps> = ({
    isOpen,
    onClose,
    onConfirm,
    title,
    description,
    itemName,
}) => {
    const overlayRef = useRef<HTMLDivElement>(null)
    const dialogRef = useRef<HTMLDivElement>(null)

    // Handle ESC key press
    useEffect(() => {
        const handleKeyDown = (e: KeyboardEvent) => {
            if (e.key === "Escape" && isOpen) {
                onClose()
            }
        }

        window.addEventListener("keydown", handleKeyDown)
        return () => window.removeEventListener("keydown", handleKeyDown)
    }, [isOpen, onClose])

    // Handle click outside
    useEffect(() => {
        const handleClickOutside = (e: MouseEvent) => {
            if (
                isOpen &&
                overlayRef.current &&
                dialogRef.current &&
                e.target === overlayRef.current &&
                !dialogRef.current.contains(e.target as Node)
            ) {
                onClose()
            }
        }

        window.addEventListener("mousedown", handleClickOutside)
        return () => window.removeEventListener("mousedown", handleClickOutside)
    }, [isOpen, onClose])

    // Focus trap
    useEffect(() => {
        if (isOpen) {
            const dialog = dialogRef.current
            if (dialog) {
                const focusableElements = dialog.querySelectorAll(
                    'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])',
                )

                if (focusableElements.length > 0) {
                    const firstElement = focusableElements[0] as HTMLElement
                    const lastElement = focusableElements[focusableElements.length - 1] as HTMLElement

                    firstElement.focus()

                    const handleTabKey = (e: KeyboardEvent) => {
                        if (e.key === "Tab") {
                            if (e.shiftKey && document.activeElement === firstElement) {
                                e.preventDefault()
                                lastElement.focus()
                            } else if (!e.shiftKey && document.activeElement === lastElement) {
                                e.preventDefault()
                                firstElement.focus()
                            }
                        }
                    }

                    dialog.addEventListener("keydown", handleTabKey)
                    return () => dialog.removeEventListener("keydown", handleTabKey)
                }
            }
        }
    }, [isOpen])

    // Prevent body scroll when dialog is open
    useEffect(() => {
        if (isOpen) {
            document.body.style.overflow = "hidden"
        } else {
            document.body.style.overflow = ""
        }

        return () => {
            document.body.style.overflow = ""
        }
    }, [isOpen])

    if (!isOpen) return null

    return createPortal(
        <div
            ref={overlayRef}
            className="fixed inset-0 z-50 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 overflow-y-auto"
            aria-hidden="true"
            role="dialog"
            aria-modal="true"
            aria-labelledby="dialog-title"
        >
            <div
                ref={dialogRef}
                className="bg-white dark:bg-gray-800 rounded-lg shadow-lg w-full max-w-md mx-auto p-6 animate-in fade-in duration-200"
                role="alertdialog"
            >
                <div className="flex justify-between items-start mb-4">
                    <h2 id="dialog-title" className="text-xl font-semibold text-gray-900 dark:text-gray-100">
                        {title}
                    </h2>
                    <button
                        onClick={onClose}
                        className="text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200 rounded-full p-1 transition-colors"
                        aria-label="Close"
                    >
                        <X size={18} />
                    </button>
                </div>

                <div className="mb-6">
                    <p className="text-gray-600 dark:text-gray-300">{description}</p>
                    <span className="font-semibold block mt-2 text-gray-800 dark:text-gray-200">"{itemName}"</span>
                </div>

                <div className="flex justify-end gap-3">
                    <button
                        onClick={onClose}
                        className="px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-md text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
                    >
                        Hủy
                    </button>
                    <button
                        onClick={() => {
                            onConfirm()
                            onClose()
                        }}
                        className="px-4 py-2 bg-red-500 hover:bg-red-600 text-white rounded-md transition-colors"
                    >
                        Xóa
                    </button>
                </div>
            </div>
        </div>,
        document.body,
    )
}

export default DeleteConfirmDialog
