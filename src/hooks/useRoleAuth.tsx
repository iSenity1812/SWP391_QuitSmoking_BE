import { useAuth } from "@/hooks/useAuth"
import type { Role } from "@/types/auth"

// Hook to check if user has required role
export function useRoleCheck(requiredRoles: Role[]): boolean {
  const { user, isAuthenticated } = useAuth()

  if (!isAuthenticated || !user) {
    return false
  }

  return requiredRoles.includes(user.role)
}

// Hook to get user's accessible routes
export function useUserRoutes() {
  const { user, isAuthenticated } = useAuth()

  if (!isAuthenticated || !user) {
    return {
      canAccessProfile: false,
      canAccessPlan: false,
      canAccessCoach: false,
      canAccessAdmin: false,
      canAccessContentAdmin: false,
      canAccessHome: true,
      canAccessAbout: true,
      canAccessBlog: true,
      defaultRoute: '/'
    }
  }

  const role = user.role

  return {
    canAccessProfile: true,
    canAccessPlan: ['NORMAL_MEMBER', 'PREMIUM_MEMBER'].includes(role),
    canAccessCoach: role === 'COACH',
    canAccessAdmin: role === 'SUPER_ADMIN',
    canAccessContentAdmin: role === 'CONTENT_ADMIN',
    // Public pages access control
    canAccessHome: ['NORMAL_MEMBER', 'PREMIUM_MEMBER'].includes(role),
    canAccessAbout: ['NORMAL_MEMBER', 'PREMIUM_MEMBER'].includes(role),
    canAccessBlog: ['NORMAL_MEMBER', 'PREMIUM_MEMBER', 'COACH'].includes(role),
    defaultRoute: getDefaultRouteForRole(role)
  }
}

export function getDefaultRouteForRole(role: Role): string {
  const roleRouteMap: Record<Role, string> = {
    'NORMAL_MEMBER': '/',
    'PREMIUM_MEMBER': '/',
    'COACH': '/coach',
    'SUPER_ADMIN': '/admin',
    'CONTENT_ADMIN': '/contentadmin'
  }

  return roleRouteMap[role] || '/'
}
