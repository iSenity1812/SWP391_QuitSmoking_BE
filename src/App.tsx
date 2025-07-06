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
import { messaging } from "./firebase"
import { getToken, onMessage } from "firebase/messaging"
import { toast } from "react-toastify"

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

    // Đăng ký nhận notification FCM
    Notification.requestPermission().then((permission) => {
      if (permission === "granted") {
        getToken(messaging, { vapidKey: "BHzIniQM8sFQ9SyhSBFswrNroTVnTQHSae_dY4W7FP782YZn0A-9GKzl3gTV5mBSsq_h5WDFDRCHDVnYVKLy52U" }).then((currentToken) => {
          if (currentToken) {
            // Gửi token này lên server để lưu lại
            console.log('FCM Token:', currentToken);
            // Lấy userId từ localStorage (giả sử đã lưu khi login)
            const userId = localStorage.getItem("userId");
            if (userId) {
              fetch("http://localhost:8080/api/fcm-token", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ userId, token: currentToken })
              })
                .then(res => res.json())
                .then(data => console.log("Token saved:", data))
                .catch(err => console.error("Error saving token:", err));
            } else {
              console.warn("No userId found in localStorage, cannot send FCM token to backend.");
            }
          }
        });
      }
    });

    onMessage(messaging, (payload) => {
      if (payload.notification) {
        toast.info(
          <div>
            <strong>{payload.notification.title}</strong>
            <div>{payload.notification.body}</div>
          </div>,
          {
            position: "top-right",
            autoClose: 5000,
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
          }
        );
      }
    });
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
