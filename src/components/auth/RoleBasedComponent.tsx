"use client"

import { useAuth } from "@/hooks/useAuth"
import type { Role } from "@/types/auth"
import type { ReactNode } from "react"

interface RoleBasedComponentProps {
  children: ReactNode
  allowedRoles: Role[]
  fallback?: ReactNode
}

export function RoleBasedComponent({
  children,
  allowedRoles,
  fallback = null
}: RoleBasedComponentProps) {
  const { user, isAuthenticated } = useAuth()

  if (!isAuthenticated || !user) {
    return <>{fallback}</>
  }

  if (!allowedRoles.includes(user.role)) {
    return <>{fallback}</>
  }

  return <>{children}</>
}
