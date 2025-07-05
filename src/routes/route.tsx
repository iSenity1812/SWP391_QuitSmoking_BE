import { Routes, Route } from "react-router-dom"
import { ProtectedRoute } from "@/components/auth/ProtectedRoute"
import { AuthRedirect } from "@/components/auth/AuthRedirect"

// Public pages
import { LandingPage } from "@/pages/Landing/LandingPage"
import BlogPage from "@/pages/blog/BlogPage"
import AboutPage from "@/pages/about/AboutPage"
import LoginPage from "@/pages/auth/LoginPage"
import RegisterPage from "@/pages/auth/RegisterPage"
import { OnboardingPage } from "@/pages/onboarding/onBoardingPage"
// import { PlanSelectionDirectPage } from "@/pages/plan-selection/PlanDirectPage"

// Protected pages
// import PlanPage from "@/pages/plan/PlanPage"
import UserProfilePage from "@/pages/user/userProfilePage"
import SubscriptionPage from "@/pages/plan/subscription/SubscriptionPage"
import PublicUserProfilePage from "@/pages/user/PublicUserProfilePage"

// Role-specific pages
import AdminPage from "@/pages/admin/AdminPage"
import CoachPage from "@/pages/coach/CoachPage"
import ContentAdminPage from "@/pages/admin/content/ContentAdminPage"

// Test components (remove in production)
import { RouteTestDashboard } from "@/components/auth/RouteTestDashboard"
import BookingPage from "@/pages/booking/BookingPage"
import PlanPagePref from "@/pages/plan/PlanPagePref"
import { MeetingPage } from "@/pages/meeting/MeetingPage"
import HealthBenefitsPage from "@/pages/health-benefits/HealthBenefitsPage"
import QuitStatsPage from "@/pages/user/QuitStatsPage"

export function AppRoutes() {
  return (
    <Routes>
      {/* Public Routes - accessible by guests and members only (NOT admin/coach except blog for coach) */}      <Route
        path="/"
        element={
          <ProtectedRoute
            allowedRoles={['NORMAL_MEMBER', 'PREMIUM_MEMBER']}
            requireAuth={false}
          >
            <LandingPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/about"
        element={
          <ProtectedRoute
            allowedRoles={['NORMAL_MEMBER', 'PREMIUM_MEMBER']}
            requireAuth={false}
          >
            <AboutPage />
          </ProtectedRoute>
        }
      />

      {/* Blog - accessible by guests, members, and coaches */}
      <Route
        path="/blog"
        element={
          <ProtectedRoute
            allowedRoles={['NORMAL_MEMBER', 'PREMIUM_MEMBER', 'COACH']}
            requireAuth={false}
          >
            <BlogPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/booking"
        element={
          <ProtectedRoute
            allowedRoles={['PREMIUM_MEMBER']}
            requireAuth={true}
          >
            <BookingPage />
          </ProtectedRoute>
        }
      />

      {/* Auth routes - prevent authenticated users from accessing */}
      <Route path="/login" element={<AuthRedirect><LoginPage /></AuthRedirect>} />
      <Route path="/register" element={<AuthRedirect><RegisterPage /></AuthRedirect>} />
      <Route path="/onboarding" element={<OnboardingPage />} />
      {/* <Route path="/plan-selection" element={<PlanSelectionDirectPage />} /> */}

      {/* Protected Routes - Require Authentication */}
      <Route
        path="/profile"
        element={
          <ProtectedRoute requireAuth={true}>
            <UserProfilePage />
          </ProtectedRoute>
        }
      />

      {/* Member Routes - NORMAL_MEMBER & PREMIUM_MEMBER */}
      <Route
        path="/plan"
        element={
          <ProtectedRoute
            allowedRoles={['NORMAL_MEMBER', 'PREMIUM_MEMBER']}
            requireAuth={true}
          >
            {/* <PlanPage /> */}
            <PlanPagePref />
          </ProtectedRoute>
        }
      />

      {/* Route cho meeting page */}
      <Route
        path="/meeting/:appointmentId"
        element={
          <ProtectedRoute
            allowedRoles={['PREMIUM_MEMBER', 'COACH']}
            requireAuth={true}
          >
            <MeetingPage />
          </ProtectedRoute>
        }
      >
      </Route>

      <Route
        path="/subscription"
        element={
          <ProtectedRoute
            allowedRoles={['NORMAL_MEMBER', 'PREMIUM_MEMBER']}
            requireAuth={true}
          >
            <SubscriptionPage />
          </ProtectedRoute>
        }
      />

      {/* Health Benefits - accessible by all members */}
      <Route
        path="/health-benefits"
        element={
          <ProtectedRoute
            allowedRoles={['NORMAL_MEMBER', 'PREMIUM_MEMBER']}
            requireAuth={false}
          >
            <HealthBenefitsPage />
          </ProtectedRoute>
        }
      />

      {/* Quit Stats - accessible by all members */}
      <Route
        path="/quit-stats"
        element={
          <ProtectedRoute
            allowedRoles={['NORMAL_MEMBER', 'PREMIUM_MEMBER']}
            requireAuth={true}
          >
            <QuitStatsPage />
          </ProtectedRoute>
        }
      />

      {/* Admin Routes - SUPER_ADMIN only */}
      <Route
        path="/admin/*"
        element={
          <ProtectedRoute
            allowedRoles={['SUPER_ADMIN']}
            requireAuth={true}
            redirectTo="/login"
          >
            <AdminPage />
          </ProtectedRoute>
        }
      />

      {/* Coach Routes - COACH only */}
      <Route
        path="/coach/*"
        element={
          <ProtectedRoute
            allowedRoles={['COACH']}
            requireAuth={true}
            redirectTo="/login"
          >
            <CoachPage />
          </ProtectedRoute>
        }
      />

      {/* Content Admin Routes - CONTENT_ADMIN only */}
      <Route
        path="/contentadmin/*"
        element={
          <ProtectedRoute
            allowedRoles={['CONTENT_ADMIN']}
            requireAuth={true}
            redirectTo="/login"
          >
            <ContentAdminPage />
          </ProtectedRoute>
        }
      />

      <Route path="/user/:userId" element={<PublicUserProfilePage />} />

      {/* Catch all - 404 or redirect to home */}
      <Route path="*" element={<LandingPage />} />

      {/* Development/Testing Route - Remove in production */}
      <Route
        path="/route-test"
        element={
          <ProtectedRoute requireAuth={true}>
            <RouteTestDashboard />
          </ProtectedRoute>
        }
      />
    </Routes>
  )
}
