"use client"

import type React from "react"
import { Plus, User } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import type { BlogUser } from "../types/blog-types"
import { getRoleIcon } from "./UserBadges"

interface UserAuthSectionProps {
    currentUser: BlogUser | null
    handleCreateBlogClick: () => void
}

const UserAuthSection: React.FC<UserAuthSectionProps> = ({ currentUser, handleCreateBlogClick }) => {
    return (
        <div className="mb-8 flex justify-between items-center p-4 bg-white/90 dark:bg-slate-800/90 rounded-xl border-2 border-emerald-200 dark:border-emerald-500/30 shadow-md">
            <div className="flex items-center gap-3">
                {currentUser ? (
                    <>
                        <Avatar className="w-10 h-10">
                            <AvatarFallback>
                                {currentUser.name
                                    .split(" ")
                                    .map((n) => n[0])
                                    .join("")}
                            </AvatarFallback>
                        </Avatar>
                        <div>
                            <p className="font-semibold text-slate-800 dark:text-white">{currentUser.name}</p>
                            <div className="flex items-center gap-1">
                                {getRoleIcon(currentUser.role)}
                                <span className="text-sm text-slate-500 dark:text-slate-400">{currentUser.role}</span>
                            </div>
                        </div>
                    </>
                ) : (
                    <>
                        <Avatar className="w-10 h-10">
                            <AvatarFallback>
                                <User className="w-5 h-5" />
                            </AvatarFallback>
                        </Avatar>
                        <div>
                            <p className="font-semibold text-slate-800 dark:text-white">Khách</p>
                            <p className="text-sm text-slate-500 dark:text-slate-400">Chưa đăng nhập</p>
                        </div>
                    </>
                )}
            </div>

            <Button className="flex items-center gap-2 bg-emerald-500 hover:bg-emerald-600" onClick={handleCreateBlogClick}>
                <Plus className="w-4 h-4" />
                Tạo bài viết mới
            </Button>
        </div>
    )
}

export default UserAuthSection
