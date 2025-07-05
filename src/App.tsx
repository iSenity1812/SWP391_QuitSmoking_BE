"use client"

import { useLocation } from "react-router-dom"
import { Navbar } from "./components/ui/Navbar"
import { AnimatePresence } from "framer-motion"
import { useEffect, useState } from "react"
import { OnboardingPage } from "./pages/onboarding/onBoardingPage"
import { AppRoutes } from "./routes/route"
import { useTokenRefresh } from "./hooks/useTokenRefresh"
import { ToastContainer } from "react-toastify"
import "react-toastify/dist/ReactToastify.css"

function shouldHideNavbar(pathname: string) {
  const hiddenPaths = [
    "/login",
    "/register",
    "/admin",
    "/coach",
    "/contentadmin",
    "/reset-password",
    "/forgot-password",
    "/onboarding",
    "/plan-selection",
    "/profile",
    "/subscription",
    '/meeting',
  ]

  // Dùng startsWith để hỗ trợ cả các route như /admin/dashboard
  return hiddenPaths.some((prefix) => pathname.startsWith(prefix))
}

export default function App() {
  const location = useLocation()
  // Auto-refresh token check
  useTokenRefresh()

  const shouldShowNavbar = !shouldHideNavbar(location.pathname)

  // Use state to track onboarding status with a default of true (completed)
  const [isOnboardingCompleted, setIsOnboardingCompleted] = useState(true)
  useEffect(() => {
    // Check localStorage only once on initial load
    const storedOnboardingStatus = localStorage.getItem("onboarding_completed")

    // Only update state if localStorage has a value and it's "false"
    if (storedOnboardingStatus === "false") {
      setIsOnboardingCompleted(false)
    }

    // For debugging - log the current status
    console.log("Onboarding completed status:", storedOnboardingStatus !== "false")
  }, [])

  // Force complete onboarding (for development/testing)
  const forceCompleteOnboarding = () => {
    localStorage.setItem("onboarding_completed", "true")
    setIsOnboardingCompleted(true)
    console.log("Onboarding manually completed")
  }

  const isOnOnboardingPage = location.pathname === "/onboarding"
  const isPlanSelectionPage = location.pathname === "/plan-selection"
  const isHomePage = location.pathname === "/"
  const isAuthPage = location.pathname === "/login" || location.pathname === "/register"

  // Only redirect to onboarding if:
  // 1. Onboarding is not completed
  // 2. User is not already on onboarding page
  // 3. User is not on plan selection page
  // 4. User is not on homepage (allow access to homepage)
  // 5. User is not on auth pages (allow login/register)
  const shouldRedirectToOnboarding =
    !isOnboardingCompleted && !isOnOnboardingPage && !isPlanSelectionPage && !isHomePage && !isAuthPage

  if (shouldRedirectToOnboarding) {
    return (
      <div className="min-h-screen flex flex-col">
        <main className="flex-1">
          <OnboardingPage />
        </main>
      </div>
    )
  }
  return (
    <div className="min-h-screen flex flex-col">
      {shouldShowNavbar && <Navbar />}
      <main className="flex-1">
        {/* Hidden button for development to force complete onboarding */}
        {!isOnboardingCompleted && (
          <button
            onClick={forceCompleteOnboarding}
            className="fixed bottom-4 right-4 bg-red-500 text-white px-3 py-1 rounded-md text-xs z-50"
          >
            Skip Onboarding (Dev)
          </button>
        )}

        <AnimatePresence mode="wait" initial={false}>
          <AppRoutes />
        </AnimatePresence>
      </main>

      {/* Toast Container */}
      <ToastContainer
        position="top-right"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
        className="mt-16"
      />
    </div>
  )
}
