import { useEffect, useCallback } from 'react'
import { useAuth } from './useAuth'

export const useTokenRefresh = () => {
  const { token, logout } = useAuth()

  const checkTokenExpiry = useCallback(() => {
    if (!token) return

    try {
      const parts = token.split('.')
      if (parts.length !== 3) {
        console.warn('Invalid token format')
        logout()
        return
      }

      const payload = JSON.parse(atob(parts[1]))
      const currentTime = Math.floor(Date.now() / 1000)
      const timeToExpiry = payload.exp - currentTime

      // If token expires in less than 5 minutes, logout proactively
      if (timeToExpiry < 300) {
        console.warn('Token expiring soon, logging out')
        logout()
      }
    } catch (error) {
      console.error('Error checking token expiry:', error)
      logout()
    }
  }, [token, logout])

  useEffect(() => {
    if (!token) return

    // Check immediately
    checkTokenExpiry()

    // Check every minute
    const interval = setInterval(checkTokenExpiry, 60000)

    return () => clearInterval(interval)
  }, [token, checkTokenExpiry])
}
