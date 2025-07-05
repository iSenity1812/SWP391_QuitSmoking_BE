import type React from "react"
import { createContext, useContext, useEffect, useRef } from "react"

interface SmoothScrollContextType {
  scrollTo: (target: string | number) => void
}

const SmoothScrollContext = createContext<SmoothScrollContextType | undefined>(undefined)

export const useSmoothScroll = () => {
  const context = useContext(SmoothScrollContext)
  if (context === undefined) {
    throw new Error("useSmoothScroll must be used within a SmoothScrollProvider")
  }
  return context
}

export const SmoothScrollProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const rafRef = useRef<number>(0);
  const targetScrollRef = useRef<number>(0);
  const currentScrollRef = useRef<number>(0);
  const isScrollingRef = useRef<boolean>(false);

  useEffect(() => {
    let scrollTimeout: NodeJS.Timeout;

    const smoothScroll = () => {
      const difference = targetScrollRef.current - currentScrollRef.current
      const speed = difference * 0.1 // Adjust this for different scroll speeds

      if (Math.abs(difference) > 0.5) {
        currentScrollRef.current += speed;
        window.scrollTo(0, currentScrollRef.current);
        rafRef.current = requestAnimationFrame(smoothScroll);
      } else {
        currentScrollRef.current = targetScrollRef.current;
        window.scrollTo(0, currentScrollRef.current);
        isScrollingRef.current = false;
      }
    }

    const handleWheel = (e: WheelEvent) => {
      e.preventDefault();

      // Clear any existing timeout
      clearTimeout(scrollTimeout);

      // Add momentum to scroll
      const momentum = e.deltaY * 2;// Adjust multiplier for sensitivity
      targetScrollRef.current += momentum;

      // Clamp to valid scroll range
      const maxScroll = document.documentElement.scrollHeight - window.innerHeight;
      targetScrollRef.current = Math.max(0, Math.min(targetScrollRef.current, maxScroll));

      if (!isScrollingRef.current) {
        isScrollingRef.current = true;
        currentScrollRef.current = window.scrollY;
        smoothScroll();
      }

      // Add inertia effect - continue scrolling briefly after wheel stops
      scrollTimeout = setTimeout(() => {
        if (isScrollingRef.current) {
          const inertiaScroll = momentum * 0.3; // Reduced momentum for inertia
          targetScrollRef.current += inertiaScroll;
          const maxScroll = document.documentElement.scrollHeight - window.innerHeight;
          targetScrollRef.current = Math.max(0, Math.min(targetScrollRef.current, maxScroll));
        }
      }, 50);
    }

    const handleTouchStart = () => {
      // Touch start logic
    };

    const handleTouchMove = () => {
      // Touch move logic
    };

    // Only apply smooth scrolling on desktop
    const isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)

    if (!isMobile) {
      window.addEventListener("wheel", handleWheel, { passive: false })
    }

    window.addEventListener("touchstart", handleTouchStart, { passive: true })
    window.addEventListener("touchmove", handleTouchMove, { passive: true })

    return () => {
      if (rafRef.current) {
        cancelAnimationFrame(rafRef.current)
      }
      clearTimeout(scrollTimeout)
      window.removeEventListener("wheel", handleWheel)
      window.removeEventListener("touchstart", handleTouchStart)
      window.removeEventListener("touchmove", handleTouchMove)
    }
  }, [])

  const scrollTo = (target: string | number) => {
    let targetPosition: number

    if (typeof target === "string") {
      const element = document.querySelector(target)
      if (element) {
        targetPosition = element.getBoundingClientRect().top + window.scrollY
      } else {
        return
      }
    } else {
      targetPosition = target
    }

    targetScrollRef.current = targetPosition
    currentScrollRef.current = window.scrollY

    if (!isScrollingRef.current) {
      isScrollingRef.current = true
      const smoothScroll = () => {
        const difference = targetScrollRef.current - currentScrollRef.current
        const speed = difference * 0.1

        if (Math.abs(difference) > 0.5) {
          currentScrollRef.current += speed
          window.scrollTo(0, currentScrollRef.current)
          rafRef.current = requestAnimationFrame(smoothScroll)
        } else {
          currentScrollRef.current = targetScrollRef.current
          window.scrollTo(0, currentScrollRef.current)
          isScrollingRef.current = false
        }
      }
      smoothScroll()
    }
  }

  return <SmoothScrollContext.Provider value={{ scrollTo }}>{children}</SmoothScrollContext.Provider>
}
