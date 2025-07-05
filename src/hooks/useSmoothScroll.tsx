
import { useEffect, useRef } from "react"

export const useSmoothScroll = () => {
  // Ref dùng để giữ ID của requestAnimationFrame để có thể hủy
  const rafRef = useRef<number>(0);

  // Scroll mục tiêu mà ta muốn mượt tới
  const targetScrollRef = useRef<number>(0);

  // Vị trí cuộn hiện tại (được mượt dần về targetScroll)
  const currentScrollRef = useRef<number>(0);

  // Tốc độ hiện tại của cuộn, dùng để tạo hiệu ứng inertia
  const velocityRef = useRef<number>(0);

  // Cờ để đánh dấu đang cuộn hay không
  const isScrollingRef = useRef<boolean>(false);

  useEffect(() => {
    // let lastWheelTime = 0;
    let wheelTimeout: NodeJS.Timeout;

    // Hàm nội suy tuyến tính (linear interpolation) giữa start và end
    const lerp = (start: number, end: number, factor: number) => {
      return start + (end - start) * factor
    }

    // Hàm chạy hiệu ứng cuộn mượt bằng animation frame
    const smoothScroll = () => {
      const difference = targetScrollRef.current - currentScrollRef.current;
      const distance = Math.abs(difference);

      if (distance > 0.5) {
        // Di chuyển dần về targetScroll theo tốc độ easing
        currentScrollRef.current = lerp(currentScrollRef.current, targetScrollRef.current, 0.08);

        // Thêm hiệu ứng inertia nếu còn velocity
        if (Math.abs(velocityRef.current) > 0.1) {
          velocityRef.current *= 0.95; // giảm dần tốc độ (friction)
          targetScrollRef.current += velocityRef.current;

          // Giới hạn không cho cuộn quá giới hạn trang
          const maxScroll = Math.max(0, document.documentElement.scrollHeight - window.innerHeight);
          targetScrollRef.current = Math.max(0, Math.min(targetScrollRef.current, maxScroll));
        }

        window.scrollTo(0, currentScrollRef.current);
        rafRef.current = requestAnimationFrame(smoothScroll);
      } else {
        // Nếu gần target rồi thì dừng lại
        currentScrollRef.current = targetScrollRef.current;
        window.scrollTo(0, currentScrollRef.current);
        isScrollingRef.current = false;
        velocityRef.current = 0;
      }
    }

    // Xử lý khi người dùng cuộn chuột
    const handleWheel = (e: WheelEvent) => {
      e.preventDefault();

      // const now = Date.now();
      // const deltaTime = now - lastWheelTime;
      // lastWheelTime = now;

      const wheelDelta = e.deltaY;
      const speed = Math.min(Math.abs(wheelDelta) / 3, 50); // Giới hạn tốc độ
      const direction = wheelDelta > 0 ? 1 : -1;

      // Cộng thêm vào velocity để tạo momentum
      velocityRef.current += direction * speed * 0.3;

      targetScrollRef.current += direction * speed;

      const maxScroll = Math.max(0, document.documentElement.scrollHeight - window.innerHeight);
      targetScrollRef.current = Math.max(0, Math.min(targetScrollRef.current, maxScroll));

      // Nếu chưa cuộn thì bắt đầu cuộn
      if (!isScrollingRef.current) {
        isScrollingRef.current = true;
        currentScrollRef.current = window.scrollY;
        smoothScroll();
      }

      // Dừng inertia sau 50ms nếu không cuộn nữa
      clearTimeout(wheelTimeout)
      wheelTimeout = setTimeout(() => {
        if (Math.abs(velocityRef.current) > 1) {
          velocityRef.current *= 0.8;
        }
      }, 50);
    }

    // Xử lý cuộn bằng phím
    const handleKeyDown = (e: KeyboardEvent) => {
      const scrollAmount = 100;
      let direction = 0;

      switch (e.key) {
        case "ArrowDown":
        case "PageDown":
        case " ":
          direction = 1
          break
        case "ArrowUp":
        case "PageUp":
          direction = -1
          break
        case "Home":
          targetScrollRef.current = 0
          break
        case "End":
          targetScrollRef.current = document.documentElement.scrollHeight - window.innerHeight;
          break
        default:
          return
      }

      if (direction !== 0) {
        e.preventDefault()
        targetScrollRef.current += direction * scrollAmount;
        const maxScroll = Math.max(0, document.documentElement.scrollHeight - window.innerHeight);
        targetScrollRef.current = Math.max(0, Math.min(targetScrollRef.current, maxScroll));

        if (!isScrollingRef.current) {
          isScrollingRef.current = true;
          currentScrollRef.current = window.scrollY;
          smoothScroll();
        }
      } else {
        // Home/End key
        e.preventDefault();
        if (!isScrollingRef.current) {
          isScrollingRef.current = true;
          currentScrollRef.current = window.scrollY;
          smoothScroll();
        }
      }
    }

    // Khởi tạo scroll position ban đầu
    currentScrollRef.current = window.scrollY;
    targetScrollRef.current = window.scrollY;

    // Bỏ qua cuộn mượt nếu là thiết bị di động
    const isMobile = /Android|iPhone|iPad|iPod/i.test(navigator.userAgent);

    if (!isMobile) {
      window.addEventListener("wheel", handleWheel, { passive: false });
      window.addEventListener("keydown", handleKeyDown, { passive: false });
    }

    // Cleanup
    return () => {
      if (rafRef.current) cancelAnimationFrame(rafRef.current);
      clearTimeout(wheelTimeout);
      window.removeEventListener("wheel", handleWheel);
      window.removeEventListener("keydown", handleKeyDown);
    }
  }, [])

  /**
   * Cuộn đến vị trí cụ thể hoặc selector như "#section1"
   */
  const scrollTo = (target: string | number) => {
    let targetPosition: number;

    if (typeof target === "string") {
      const element = document.querySelector(target);
      if (element) {
        targetPosition = element.getBoundingClientRect().top + window.scrollY - 80; // Trừ chiều cao header
      } else return
    } else {
      targetPosition = target;
    }

    targetScrollRef.current = targetPosition;
    velocityRef.current = 0;

    if (!isScrollingRef.current) {
      isScrollingRef.current = true;
      currentScrollRef.current = window.scrollY;

      const smoothScroll = () => {
        const difference = targetScrollRef.current - currentScrollRef.current;

        if (Math.abs(difference) > 0.5) {
          currentScrollRef.current += difference * 0.1;
          window.scrollTo(0, currentScrollRef.current);
          rafRef.current = requestAnimationFrame(smoothScroll);
        } else {
          currentScrollRef.current = targetScrollRef.current;
          window.scrollTo(0, currentScrollRef.current);
          isScrollingRef.current = false;
        }
      }

      smoothScroll();
    }
  }

  return { scrollTo }
}
