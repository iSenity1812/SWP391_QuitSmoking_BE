"use client"

import { useState } from "react"
// import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Button } from "@/components/ui/button"
import { Bell, Settings, Wind } from "lucide-react"
import { Link } from "react-router-dom"
import { SettingsModal } from "./SettingsModal"
import type { User } from "../types/user-types"

interface UserProfileHeaderProps {
    user: User
}

const UserProfileHeader = ({ user }: UserProfileHeaderProps) => {
    const [isSettingsOpen, setIsSettingsOpen] = useState(false)
    return (
        <div className="bg-white dark:bg-slate-900 shadow-sm border-b border-slate-200 dark:border-slate-700">
            <div className="max-w-1xl mx-auto px-4 sm:px-6 py-4">
                <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3 text-2xl font-black text-slate-800 dark:text-white">
                        <Wind className="h-8 w-8 text-emerald-500" />
                        <Link to="/" className="text-xl font-bold">
                            QuitTogether
                        </Link>
                    </div>
                    <div className="flex items-center gap-4">
                        <div className="flex flex-col items-end mr-2">
                            <span className="font-semibold text-slate-900 dark:text-white">{user.name}</span>
                            <span className="text-xs text-slate-500 dark:text-slate-400">{user.email}</span>
                        </div>
                        <img
                            src={user.avatar}
                            alt={user.name}
                            className="w-10 h-10 rounded-full border border-slate-300 dark:border-slate-700 object-cover"
                        />
                        <Button variant="outline" size="sm">
                            <Bell className="h-4 w-4 mr-2" />
                            Thông báo
                        </Button>
                        <Button 
                            variant="outline" 
                            size="sm"
                            onClick={() => setIsSettingsOpen(true)}
                        >
                            <Settings className="h-4 w-4 mr-2" />
                            Cài đặt
                        </Button>
                    </div>
                </div>
            </div>
            
            <SettingsModal 
                isOpen={isSettingsOpen} 
                onClose={() => setIsSettingsOpen(false)} 
            />
        </div>
    )
}

export default UserProfileHeader;

export type { UserProfileHeaderProps };
