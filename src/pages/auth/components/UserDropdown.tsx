
import type React from "react"
import { useState, useRef, useEffect } from "react"
import { User, Settings, LogOut, Trophy, ChevronDown, Shield, Gem } from "lucide-react"
import { useAuth } from "@/hooks/useAuth"
import { useUserRoutes } from "@/hooks/useRoleAuth"
import { Link } from "react-router-dom"

export const UserDropdown: React.FC = () => {
  const [isOpen, setIsOpen] = useState(false)
  const dropdownRef = useRef<HTMLDivElement>(null)
  const { user, logout } = useAuth()
  const { canAccessPlan, canAccessCoach, canAccessAdmin, canAccessContentAdmin } = useUserRoutes()

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false)
      }
    }

    document.addEventListener("mousedown", handleClickOutside)
    return () => document.removeEventListener("mousedown", handleClickOutside)
  }, [])

  if (!user) return null

  // Get role badge text
  const getRoleBadgeText = () => {
    switch (user.role) {
      case 'SUPER_ADMIN': return 'Admin';
      case 'CONTENT_ADMIN': return 'Content Admin';
      case 'COACH': return 'Coach';
      case 'PREMIUM_MEMBER': return 'Premium';
      case 'NORMAL_MEMBER': return 'Member';
      default: return '';
    }
  }

  // Get dashboard link based on role
  const getDashboardLink = () => {
    switch (user.role) {
      case 'SUPER_ADMIN': return '/admin';
      case 'CONTENT_ADMIN': return '/contentadmin';
      case 'COACH': return '/coach';
      default: return '/profile';
    }
  }

  // Define menu items based on user role
  const getMenuItems = () => {
    const items = []

    // Profile/Dashboard - Always show
    if (canAccessAdmin || canAccessCoach || canAccessContentAdmin) {
      items.push({
        label: 'Dashboard',
        href: getDashboardLink(),
        icon: Shield,
        variant: 'default' as const
      })
    } else {
      items.push({
        label: 'Profile',
        href: '/profile',
        icon: User,
        variant: 'default' as const
      })
    }

    // Settings - Always show
    items.push({
      label: 'Settings',
      href: '#', // Placeholder for future
      icon: Settings,
      variant: 'default' as const
    })

    // My Plan - Only for members
    if (canAccessPlan) {
      items.push({
        label: 'My Plan',
        href: '/plan',
        icon: Gem,
        variant: 'default' as const
      })
    } return items
  }

  const menuItems = getMenuItems()

  return (
    <div className="relative" ref={dropdownRef}>
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="flex items-center gap-3 p-2 rounded-xl hover:bg-emerald-50 dark:hover:bg-slate-700 transition-all duration-300 hover:scale-105 group relative"
      >
        <div className="relative">
          <img
            src={'/cham1.jpg'}
            alt={user.username}
            className="w-10 h-10 rounded-full border-2 border-emerald-200 dark:border-emerald-500 shadow-lg group-hover:border-emerald-300 dark:group-hover:border-emerald-400 transition-colors duration-300"
          />
          {/* Role Badge */}
          {/* {getRoleBadgeText() && (
            <span className="absolute -top-1 -right-1 bg-emerald-500 text-white text-xs px-1.5 py-0.5 rounded-full font-medium">
              {getRoleBadgeText()}
            </span>
          )} */}
        </div>
        <div className="hidden md:block text-left">
          <p className="font-bold text-slate-800 dark:text-white text-sm">{user.username}</p>
        </div>
        <ChevronDown
          className={`w-4 h-4 text-slate-600 dark:text-slate-300 transition-transform duration-300 ${isOpen ? "rotate-180" : ""}`}
        />
      </button>

      {isOpen && (
        <div className="absolute right-0 mt-2 bg-white dark:bg-slate-800 rounded-2xl shadow-2xl border-2 border-emerald-100 dark:border-slate-700 overflow-hidden z-50 animate-in slide-in-from-top-2 duration-200 min-w-[240px]">
          {/* Header with user info */}
          <div className="p-4 bg-gradient-to-r from-emerald-50 to-emerald-100 dark:from-emerald-900/20 dark:to-emerald-800/20 border-b border-emerald-200 dark:border-slate-600">
            <div className="flex items-center gap-3">
              <img
                src={'/cham1.jpg'}
                alt={user.username}
                className="w-12 h-12 rounded-full border-2 border-emerald-300 dark:border-emerald-500 shadow-lg"
              />
              <div className="flex-1 min-w-0">
                <p className="font-black text-slate-800 dark:text-white truncate">{user.username}</p>
                <p className="text-sm text-slate-600 dark:text-slate-300 truncate max-w-full">{user.email}</p>
                <div className="flex items-center gap-4 mt-1">
                  <span className="text-xs font-bold text-emerald-600 dark:text-emerald-400 flex items-center gap-1">
                    <Trophy className="w-3 h-3" />
                    {getRoleBadgeText()}
                  </span>
                </div>
              </div>
            </div>
          </div>

          {/* Menu Items */}
          <div className="p-2">
            {menuItems.map((item, index) => (
              <Link
                key={index}
                to={item.href}
                onClick={() => setIsOpen(false)}
                className="w-full flex items-center gap-3 p-3 rounded-xl hover:bg-emerald-50 dark:hover:bg-slate-700 transition-colors duration-200 text-left"
              >
                <item.icon className="w-5 h-5 text-slate-600 dark:text-slate-300" />
                <span className="font-semibold text-slate-700 dark:text-slate-300">{item.label}</span>
              </Link>
            ))}

            <hr className="my-2 border-gray-200 dark:border-slate-600" />

            {/* Logout Button */}
            <button
              onClick={() => {
                setIsOpen(false)
                logout()
              }}
              className="w-full flex items-center gap-3 p-3 rounded-xl hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors duration-200 text-left text-red-600 dark:text-red-400"
            >
              <LogOut className="w-5 h-5" />
              <span className="font-semibold">Sign Out</span>
            </button>
          </div>
        </div>
      )}
    </div>
  )
}