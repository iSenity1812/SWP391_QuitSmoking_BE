import type React from "react";
import { Sun, Moon } from "lucide-react";
import { useTheme } from "@/context/ThemeContext";

export const ThemeToggle: React.FC = () => {
  const { theme, toggleTheme } = useTheme();

  return (
    <button
      onClick={toggleTheme}
      className="flex items-center justify-center w-11 h-11 bg-gradient-to-br from-emerald-50 to-emerald-100 dark:from-slate-700 dark:to-slate-600 border-2 border-emerald-200 dark:border-slate-500 rounded-xl cursor-pointer transition-all duration-300 ease-out hover:scale-105 hover:shadow-lg hover:shadow-emerald-200/50 dark:hover:shadow-slate-500/50 hover:border-emerald-300 dark:hover:border-emerald-400 text-emerald-700 dark:text-emerald-300 hover:-translate-y-0.5"
      aria-label={`Switch to ${theme === "light" ? "dark" : "light"} mode`}
      title={`Switch to ${theme === "light" ? "dark" : "light"} mode`}
    >
      {theme === "light" ? (
        <Moon className="w-5 h-5 transition-transform duration-300 hover:rotate-12" />
      ) : (
        <Sun className="w-5 h-5 transition-transform duration-300 hover:rotate-12" />
      )}
    </button>
  );
};
