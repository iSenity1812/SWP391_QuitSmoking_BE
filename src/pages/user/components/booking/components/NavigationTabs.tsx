"use client"

interface NavigationTabsProps {
    activeTab: "coaches" | "appointments" | "history"
    setActiveTab: (tab: "coaches" | "appointments" | "history") => void
}

export function NavigationTabs({ activeTab, setActiveTab }: NavigationTabsProps) {
    return (
        <div className="flex space-x-1 bg-slate-100 dark:bg-slate-800 p-1 rounded-lg">
            <button
                onClick={() => setActiveTab("coaches")}
                className={`flex-1 py-2 px-4 rounded-md text-sm font-medium transition-colors ${activeTab === "coaches"
                        ? "bg-white dark:bg-slate-700 text-slate-900 dark:text-white shadow-sm"
                        : "text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white"
                    }`}
            >
                Chọn Chuyên Gia
            </button>
            <button
                onClick={() => setActiveTab("appointments")}
                className={`flex-1 py-2 px-4 rounded-md text-sm font-medium transition-colors ${activeTab === "appointments"
                        ? "bg-white dark:bg-slate-700 text-slate-900 dark:text-white shadow-sm"
                        : "text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white"
                    }`}
            >
                Lịch Hẹn Của Tôi
            </button>
            <button
                onClick={() => setActiveTab("history")}
                className={`flex-1 py-2 px-4 rounded-md text-sm font-medium transition-colors ${activeTab === "history"
                        ? "bg-white dark:bg-slate-700 text-slate-900 dark:text-white shadow-sm"
                        : "text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white"
                    }`}
            >
                Lịch Sử
            </button>
        </div>
    )
}
