"use client"

import { useAuth } from "@/hooks/useAuth"
import { Navigate } from "react-router-dom"
import { getDefaultRouteForRole } from "@/hooks/useRoleAuth"
import type { ReactNode } from "react"

interface AuthRedirectProps {
  children: ReactNode
}

// Component to prevent authenticated users from accessing auth pages
export function AuthRedirect({ children }: AuthRedirectProps) {
  const { isAuthenticated, user, isLoading } = useAuth()

  // Show loading while checking authentication
  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-500"></div>
      </div>
    )
  }

  // If user is already authenticated, redirect to their default page
  if (isAuthenticated && user) {
    const defaultRoute = getDefaultRouteForRole(user.role)
    return <Navigate to={defaultRoute} replace />
  }

  // If not authenticated, show the auth page
  return <>{children}</>
}
