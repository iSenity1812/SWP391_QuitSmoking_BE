"use client"

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { useAuth } from "@/hooks/useAuth"
import { useUserRoutes } from "@/hooks/useRoleAuth"
import { Link } from "react-router-dom"
import { Shield, User, Crown, BookOpen } from "lucide-react"

export function RouteTestDashboard() {
  const { user, isAuthenticated } = useAuth()
  const {
    canAccessProfile,
    canAccessPlan,
    canAccessCoach,
    canAccessAdmin,
    canAccessContentAdmin,
    canAccessHome,
    canAccessAbout,
    canAccessBlog,
    defaultRoute
  } = useUserRoutes()

  if (!isAuthenticated || !user) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Card className="w-full max-w-md">
          <CardHeader>
            <CardTitle>Authentication Required</CardTitle>
            <CardDescription>Please log in to view this page</CardDescription>
          </CardHeader>
          <CardContent>
            <Button asChild className="w-full">
              <Link to="/login">Login</Link>
            </Button>
          </CardContent>
        </Card>
      </div>
    )
  }

  const getRoleIcon = (role: string) => {
    switch (role) {
      case 'SUPER_ADMIN': return <Shield className="w-5 h-5" />
      case 'CONTENT_ADMIN': return <BookOpen className="w-5 h-5" />
      case 'COACH': return <User className="w-5 h-5" />
      case 'PREMIUM_MEMBER': return <Crown className="w-5 h-5" />
      case 'NORMAL_MEMBER': return <User className="w-5 h-5" />
      default: return <User className="w-5 h-5" />
    }
  }

  const getRoleColor = (role: string) => {
    switch (role) {
      case 'SUPER_ADMIN': return 'bg-red-500'
      case 'CONTENT_ADMIN': return 'bg-blue-500'
      case 'COACH': return 'bg-green-500'
      case 'PREMIUM_MEMBER': return 'bg-purple-500'
      case 'NORMAL_MEMBER': return 'bg-gray-500'
      default: return 'bg-gray-500'
    }
  }

  return (
    <div className="container mx-auto p-6 space-y-6">
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            {getRoleIcon(user.role)}
            Route Access Dashboard
          </CardTitle>
          <CardDescription>
            Testing role-based route access for your account
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          {/* User Info */}
          <div className="flex items-center gap-4 p-4 bg-slate-50 dark:bg-slate-800 rounded-lg">
            <div className="flex-1">
              <h3 className="font-semibold">{user.username}</h3>
              <p className="text-sm text-slate-600 dark:text-slate-400">{user.email}</p>
            </div>
            <Badge className={`${getRoleColor(user.role)} text-white`}>
              {user.role.replace('_', ' ')}
            </Badge>
          </div>

          {/* Access Matrix */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="space-y-2">              <h4 className="font-medium">Route Access Permissions:</h4>
              <div className="space-y-1">
                <div className="flex items-center justify-between p-2 bg-slate-50 dark:bg-slate-800 rounded">
                  <span>Home</span>
                  <Badge variant={canAccessHome ? "default" : "secondary"}>
                    {canAccessHome ? "✓ Allowed" : "✗ Denied"}
                  </Badge>
                </div>
                <div className="flex items-center justify-between p-2 bg-slate-50 dark:bg-slate-800 rounded">
                  <span>About</span>
                  <Badge variant={canAccessAbout ? "default" : "secondary"}>
                    {canAccessAbout ? "✓ Allowed" : "✗ Denied"}
                  </Badge>
                </div>
                <div className="flex items-center justify-between p-2 bg-slate-50 dark:bg-slate-800 rounded">
                  <span>Blog</span>
                  <Badge variant={canAccessBlog ? "default" : "secondary"}>
                    {canAccessBlog ? "✓ Allowed" : "✗ Denied"}
                  </Badge>
                </div>
                <div className="flex items-center justify-between p-2 bg-slate-50 dark:bg-slate-800 rounded">
                  <span>Profile</span>
                  <Badge variant={canAccessProfile ? "default" : "secondary"}>
                    {canAccessProfile ? "✓ Allowed" : "✗ Denied"}
                  </Badge>
                </div>
                <div className="flex items-center justify-between p-2 bg-slate-50 dark:bg-slate-800 rounded">
                  <span>Plan</span>
                  <Badge variant={canAccessPlan ? "default" : "secondary"}>
                    {canAccessPlan ? "✓ Allowed" : "✗ Denied"}
                  </Badge>
                </div>
                <div className="flex items-center justify-between p-2 bg-slate-50 dark:bg-slate-800 rounded">
                  <span>Coach Dashboard</span>
                  <Badge variant={canAccessCoach ? "default" : "secondary"}>
                    {canAccessCoach ? "✓ Allowed" : "✗ Denied"}
                  </Badge>
                </div>
                <div className="flex items-center justify-between p-2 bg-slate-50 dark:bg-slate-800 rounded">
                  <span>Admin Dashboard</span>
                  <Badge variant={canAccessAdmin ? "default" : "secondary"}>
                    {canAccessAdmin ? "✓ Allowed" : "✗ Denied"}
                  </Badge>
                </div>
                <div className="flex items-center justify-between p-2 bg-slate-50 dark:bg-slate-800 rounded">
                  <span>Content Admin</span>
                  <Badge variant={canAccessContentAdmin ? "default" : "secondary"}>
                    {canAccessContentAdmin ? "✓ Allowed" : "✗ Denied"}
                  </Badge>
                </div>
              </div>
            </div>            <div className="space-y-2">
              <h4 className="font-medium">Test Routes:</h4>
              <div className="space-y-2">
                <Button asChild variant="outline" size="sm" className="w-full justify-start">
                  <Link to={defaultRoute}>Default Route</Link>
                </Button>

                {/* Public pages test */}
                <Button asChild variant={canAccessHome ? "outline" : "destructive"} size="sm" className="w-full justify-start">
                  <Link to="/">Home</Link>
                </Button>
                <Button asChild variant={canAccessAbout ? "outline" : "destructive"} size="sm" className="w-full justify-start">
                  <Link to="/about">About</Link>
                </Button>
                <Button asChild variant={canAccessBlog ? "outline" : "destructive"} size="sm" className="w-full justify-start">
                  <Link to="/blog">Blog</Link>
                </Button>

                {canAccessProfile && (
                  <Button asChild variant="outline" size="sm" className="w-full justify-start">
                    <Link to="/profile">Profile</Link>
                  </Button>
                )}
                {canAccessPlan && (
                  <Button asChild variant="outline" size="sm" className="w-full justify-start">
                    <Link to="/plan">Plan</Link>
                  </Button>
                )}
                {canAccessCoach && (
                  <Button asChild variant="outline" size="sm" className="w-full justify-start">
                    <Link to="/coach">Coach Dashboard</Link>
                  </Button>
                )}
                {canAccessAdmin && (
                  <Button asChild variant="outline" size="sm" className="w-full justify-start">
                    <Link to="/admin">Admin Dashboard</Link>
                  </Button>
                )}
                {canAccessContentAdmin && (
                  <Button asChild variant="outline" size="sm" className="w-full justify-start">
                    <Link to="/contentadmin">Content Admin</Link>
                  </Button>
                )}
              </div>
            </div>
          </div>          {/* Unauthorized Test Links */}
          <div className="pt-4 border-t">
            <h4 className="font-medium mb-2 text-orange-600">Test Unauthorized Access:</h4>
            <div className="grid grid-cols-2 md:grid-cols-3 gap-2">
              {!canAccessHome && (
                <Button asChild variant="destructive" size="sm">
                  <Link to="/">Home (Force)</Link>
                </Button>
              )}
              {!canAccessAbout && (
                <Button asChild variant="destructive" size="sm">
                  <Link to="/about">About (Force)</Link>
                </Button>
              )}
              {!canAccessBlog && (
                <Button asChild variant="destructive" size="sm">
                  <Link to="/blog">Blog (Force)</Link>
                </Button>
              )}
              <Button asChild variant="destructive" size="sm">
                <Link to="/admin">Admin (Force)</Link>
              </Button>
              <Button asChild variant="destructive" size="sm">
                <Link to="/coach">Coach (Force)</Link>
              </Button>
              <Button asChild variant="destructive" size="sm">
                <Link to="/contentadmin">Content Admin (Force)</Link>
              </Button>
            </div>
            <p className="text-xs text-slate-500 mt-2">
              These links will show unauthorized access page if you don't have permission
            </p>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
