"use client"

import { useAuth } from "@/hooks/useAuth"
import type { Role } from "@/types/auth"
import { Navigate, useLocation } from "react-router-dom"
import type { ReactNode } from "react"
import { UnauthorizedAccess } from "./UnauthorizedAccess"

interface ProtectedRouteProps {
  children: ReactNode
  allowedRoles?: Role[]
  requireAuth?: boolean
  redirectTo?: string
}

export function ProtectedRoute({
  children,
  allowedRoles = [],
  requireAuth = true,
  redirectTo = "/login"
}: ProtectedRouteProps) {
  const { isAuthenticated, user, isLoading } = useAuth()
  const location = useLocation()
  // Show loading while checking authentication
  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <div className="flex flex-col items-center space-y-4">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-500"></div>
          <p className="text-sm text-muted-foreground">Checking authentication...</p>
        </div>
      </div>
    )
  }

  // If authentication is required but user is not authenticated
  if (requireAuth && !isAuthenticated) {
    return <Navigate to={redirectTo} state={{ from: location }} replace />
  }  // If specific roles are required, check user role
  if (allowedRoles.length > 0) {
    // If user is not authenticated but roles are specified
    if (!isAuthenticated || !user) {
      // For public pages (requireAuth = false), allow access
      if (!requireAuth) {
        return <>{children}</>
      }
      // For protected pages, redirect to login
      return <Navigate to={redirectTo} state={{ from: location }} replace />
    }

    // If user is authenticated, check if their role is allowed
    if (!allowedRoles.includes(user.role)) {
      // For authenticated users with wrong role, always show unauthorized page
      // This prevents redirect loops and gives clear feedback
      return <UnauthorizedAccess />
    }
  }

  return <>{children}</>
}
