import type React from "react"
import { Badge } from "@/components/ui/badge"
import { User, Crown, GraduationCap, Shield } from "lucide-react"
import type { UserRole } from "../types/blog-types"

interface UserBadgesProps {
    role: UserRole
}

export const getRoleIcon = (role: UserRole) => {
    switch (role) {
        case "Coach":
            return <GraduationCap className="w-4 h-4 text-purple-600" />
        case "Premium member":
            return <Crown className="w-4 h-4 text-yellow-600" />
        case "Normal member":
            return <User className="w-4 h-4 text-blue-600" />
        case "Content Admin":
            return <Shield className="w-4 h-4 text-red-600" />
        default:
            return <User className="w-4 h-4 text-gray-600" />
    }
}

export const getRoleBadge = (role: UserRole) => {
    switch (role) {
        case "Coach":
            return <Badge className="bg-purple-100 text-purple-700">Coach</Badge>
        case "Premium member":
            return <Badge className="bg-yellow-100 text-yellow-700">Premium</Badge>
        case "Normal member":
            return <Badge className="bg-blue-100 text-blue-700">Member</Badge>
        case "Content Admin":
            return <Badge className="bg-red-100 text-red-700">Admin</Badge>
        default:
            return <Badge variant="secondary">{role}</Badge>
    }
}

export const getStatusBadge = (status: string) => {
    switch (status) {
        case "Published":
            return <Badge className="bg-green-100 text-green-700">Đã xuất bản</Badge>
        case "Pending Approval":
            return <Badge className="bg-yellow-100 text-yellow-700">Chờ phê duyệt</Badge>
        case "Rejected":
            return <Badge className="bg-red-100 text-red-700">Bị từ chối</Badge>
        default:
            return <Badge variant="secondary">{status}</Badge>
    }
}

const UserBadges: React.FC<UserBadgesProps> = ({ role }) => {
    return (
        <div className="flex items-center gap-2">
            {getRoleIcon(role)}
            {getRoleBadge(role)}
        </div>
    )
}

export default UserBadges
