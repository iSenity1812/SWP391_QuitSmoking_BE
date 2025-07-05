import type React from "react";
import { createContext, useContext, useEffect, useState } from "react";

type Theme = "light" | "dark";

interface ThemeContextType {
  theme: Theme;
  toggleTheme: () => void;
  isTransitioning: boolean;
}

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

export const useTheme = () => {
  const context = useContext(ThemeContext);
  if (context === undefined) {
    throw new Error("useTheme must be used within a ThemeProvider");
  }
  return context;
};

export const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [theme, setTheme] = useState<Theme>(() => {
    if (typeof window !== "undefined") {
      const savedTheme = localStorage.getItem("theme") as Theme;
      if (savedTheme) {
        return savedTheme;
      }
      return window.matchMedia("(prefers-color-scheme: dark)").matches
        ? "dark"
        : "light";
    }
    return "light";
  });

  const [isTransitioning, setIsTransitioning] = useState(false);

  useEffect(() => {
    if (theme === "dark") document.documentElement.classList.add("dark");
    else document.documentElement.classList.remove("dark");
    localStorage.setItem("theme", theme);
  }, [theme]);

  const toggleTheme = () => {
    // Ngăn người dùng spam click trong khi animation đang chạy
    if (isTransitioning) return;

    setIsTransitioning(true);

    // Tạo hiệu ứng gợn sóng (ripple)
    const ripple = document.createElement("div");
    // Khởi tạo ripple với scale rất nhỏ (không phải 0 hoàn toàn để tránh một số vấn đề rendering)
    // Và không có opacity để nó hiện ra ngay lập tức
    ripple.className =
      "fixed bg-emerald-500/30 rounded-full z-[9999] pointer-events-none";


    const button = document.querySelector("#theme-toggle-button") || document.querySelector(".theme-toggle-button-class");
    if (button) {
      const rect = button.getBoundingClientRect();
      ripple.style.left = `${rect.left + rect.width / 2}px`;
      ripple.style.top = `${rect.top + rect.height / 2}px`;
      ripple.style.width = `8px`; // Kích thước ban đầu nhỏ
      ripple.style.height = `8px`; // Kích thước ban đầu nhỏ
      ripple.style.transform = "translate(-50%, -50%) scale(0)"; // Đặt gốc transform vào giữa element
    } else {
      ripple.style.right = "24px";
      ripple.style.top = "50%";
      ripple.style.width = "8px";
      ripple.style.height = "8px";
      ripple.style.transform = "translate(50%, -50%) scale(0)";
    }


    document.body.appendChild(ripple);

    // Đảm bảo trình duyệt đã render element ở trạng thái ban đầu
    // Bằng cách gọi requestAnimationFrame hoặc setTimeout(..., 0)
    requestAnimationFrame(() => {
      // Kích hoạt animation sau khi trình duyệt đã nhận biết trạng thái ban đầu của ripple
      ripple.style.transition =
        "transform 0.6s cubic-bezier(0.4, 0, 0.2, 1), opacity 0.6s ease-out";

      // Kích thước cuối cùng của ripple để che toàn màn hình
      const largestDimension = Math.max(window.innerWidth, window.innerHeight);
      const scaleFactor = (largestDimension * 2) / 8; // Nhân 2 để đảm bảo che phủ hoàn toàn
      ripple.style.transform = `${ripple.style.transform.split('scale')[0]} scale(${scaleFactor})`;
      ripple.style.opacity = "0"; // Mờ dần khi phóng to

      // Thay đổi theme sau khi animation đã bắt đầu và đủ thời gian để hiển thị
      // Nên khớp với một phần lớn thời gian của animation ripple
      setTimeout(() => {
        setTheme((prevTheme) => (prevTheme === "light" ? "dark" : "light"));
      }, 300);

      // Dọn dẹp ripple sau khi animation hoàn tất
      // Thời gian này phải bằng hoặc lớn hơn thời lượng animation của ripple
      setTimeout(() => {
        setIsTransitioning(false);
        if (ripple.parentNode) {
          ripple.parentNode.removeChild(ripple);
        }
      }, 600); // Đảm bảo thời gian này bằng hoặc lớn hơn thời gian transition của ripple
    });
  };

  return (
    <ThemeContext.Provider value={{ theme, toggleTheme, isTransitioning }}>
      {children}
    </ThemeContext.Provider>
  );
};