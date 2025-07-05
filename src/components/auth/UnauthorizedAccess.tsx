"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Shield, Home } from "lucide-react"
import { Link } from "react-router-dom"
import { useAuth } from "@/hooks/useAuth"
import { getDefaultRouteForRole } from "@/hooks/useRoleAuth"

export function UnauthorizedAccess() {
  const { user, isAuthenticated } = useAuth()

  const getHomeRoute = () => {
    if (isAuthenticated && user) {
      return getDefaultRouteForRole(user.role)
    }
    return "/"
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-50 to-slate-100 dark:from-slate-900 dark:to-slate-800">
      <Card className="w-full max-w-md mx-4">
        <CardHeader className="text-center">
          <div className="mx-auto w-16 h-16 bg-red-100 dark:bg-red-900/20 rounded-full flex items-center justify-center mb-4">
            <Shield className="w-8 h-8 text-red-600 dark:text-red-400" />
          </div>
          <CardTitle className="text-2xl font-bold text-slate-900 dark:text-white">
            Access Denied
          </CardTitle>
          <CardDescription className="text-slate-600 dark:text-slate-400">
            You don't have permission to access this page
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">          <p className="text-sm text-center text-slate-500 dark:text-slate-400">
          This page is restricted to certain user roles. Please check with your administrator if you believe this is an error.
        </p>

          <div className="flex flex-col gap-2">
            <Button asChild className="w-full">
              <Link to={getHomeRoute()}>
                <Home className="w-4 h-4 mr-2" />
                {isAuthenticated && user ? 'Go to My Dashboard' : 'Go to Home'}
              </Link>
            </Button>
          </div>

          {!isAuthenticated && (
            <div className="pt-4 border-t">
              <p className="text-xs text-center text-slate-500 dark:text-slate-400 mb-3">
                Not logged in?
              </p>
              <Button asChild variant="secondary" className="w-full">
                <Link to="/login">
                  Sign In
                </Link>
              </Button>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
