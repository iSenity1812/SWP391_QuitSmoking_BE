// Mock API service for admin data
export interface DashboardStats {
    totalUsers: number
    activePlans: number
    successRate: number
    monthlyRevenue: number
    serverPerformance: number
    databaseHealth: number
    apiResponseTime: number
    newUsersToday: number
    plansCreated: number
    successStories: number
    premiumUpgrades: number
}

export interface User {
    id: number
    name: string
    email: string
    phone: string
    status: "active" | "inactive"
    plan: "Cơ Bản" | "Premium"
    joinDate: string
    lastActive: string
    avatar?: string
}

export interface Plan {
    id: number
    user: string
    planName: string
    startDate: string
    targetDate: string
    progress: number
    status: "active" | "completed" | "at-risk"
    daysLeft: number
}

export interface Activity {
    id: number
    user: string
    action: string
    time: string
    type: "success" | "info" | "warning" | "error"
}

export interface SystemAlert {
    id: number
    message: string
    type: "warning" | "info" | "success"
    timestamp: string
}

// Mock data generators
const generateRandomUser = (id: number): User => {
    const names = [
        "Nguyễn Văn An",
        "Trần Thị Bình",
        "Lê Văn Cường",
        "Phạm Thị Dung",
        "Hoàng Văn Em",
        "Vũ Thị Phương",
        "Đặng Văn Giang",
        "Bùi Thị Hoa",
    ]
    const statuses: ("active" | "inactive")[] = ["active", "inactive"]
    const plans: ("Cơ Bản" | "Premium")[] = ["Cơ Bản", "Premium"]

    return {
        id,
        name: names[Math.floor(Math.random() * names.length)],
        email: `user${id}@example.com`,
        phone: `+84 ${Math.floor(Math.random() * 900000000 + 100000000)}`,
        status: statuses[Math.floor(Math.random() * statuses.length)],
        plan: plans[Math.floor(Math.random() * plans.length)],
        joinDate: new Date(Date.now() - Math.random() * 365 * 24 * 60 * 60 * 1000).toLocaleDateString("vi-VN"),
        lastActive: `${Math.floor(Math.random() * 24)} giờ trước`,
        avatar: `/placeholder.svg?height=40&width=40`,
    }
}

const generateRandomPlan = (id: number): Plan => {
    const users = ["Nguyễn Văn An", "Trần Thị Bình", "Lê Văn Cường", "Phạm Thị Dung"]
    const planNames = ["Thử Thách 30 Ngày Cai Thuốc", "Kế Hoạch Giảm Dần", "Phương Pháp Cai Ngay", "Chiến Binh Cuối Tuần"]
    const statuses: ("active" | "completed" | "at-risk")[] = ["active", "completed", "at-risk"]

    const progress = Math.floor(Math.random() * 100)
    const daysLeft = Math.floor(Math.random() * 60)

    return {
        id,
        user: users[Math.floor(Math.random() * users.length)],
        planName: planNames[Math.floor(Math.random() * planNames.length)],
        startDate: new Date(Date.now() - Math.random() * 90 * 24 * 60 * 60 * 1000).toLocaleDateString("vi-VN"),
        targetDate: new Date(Date.now() + Math.random() * 90 * 24 * 60 * 60 * 1000).toLocaleDateString("vi-VN"),
        progress,
        status: progress === 100 ? "completed" : statuses[Math.floor(Math.random() * statuses.length)],
        daysLeft,
    }
}

const generateRandomActivity = (id: number): Activity => {
    const users = ["Nguyễn Văn An", "Trần Thị Bình", "Lê Văn Cường", "Phạm Thị Dung"]
    const actions = [
        "Hoàn thành mốc 30 ngày",
        "Bắt đầu kế hoạch mới",
        "Yêu cầu hỗ trợ",
        "Nâng cấp lên premium",
        "Bỏ lỡ check-in",
        "Hoàn thành bài tập",
        "Chia sẻ thành tích",
    ]
    const types: ("success" | "info" | "warning" | "error")[] = ["success", "info", "warning", "error"]
    const timeOptions = ["vài phút trước", "1 giờ trước", "2 giờ trước", "1 ngày trước", "2 ngày trước"]

    return {
        id,
        user: users[Math.floor(Math.random() * users.length)],
        action: actions[Math.floor(Math.random() * actions.length)],
        time: timeOptions[Math.floor(Math.random() * timeOptions.length)],
        type: types[Math.floor(Math.random() * types.length)],
    }
}

// API simulation functions
export const adminService = {
    async getDashboardStats(): Promise<DashboardStats> {
        // Simulate API delay
        await new Promise((resolve) => setTimeout(resolve, 1000))

        return {
            totalUsers: Math.floor(Math.random() * 1000) + 2000,
            activePlans: Math.floor(Math.random() * 500) + 1000,
            successRate: Math.floor(Math.random() * 20) + 70,
            monthlyRevenue: Math.floor(Math.random() * 5000) + 10000,
            serverPerformance: Math.floor(Math.random() * 10) + 90,
            databaseHealth: Math.floor(Math.random() * 15) + 85,
            apiResponseTime: Math.floor(Math.random() * 20) + 80,
            newUsersToday: Math.floor(Math.random() * 50) + 20,
            plansCreated: Math.floor(Math.random() * 30) + 10,
            successStories: Math.floor(Math.random() * 15) + 5,
            premiumUpgrades: Math.floor(Math.random() * 20) + 5,
        }
    },

    async getUsers(page = 1, limit = 10): Promise<User[]> {
        await new Promise((resolve) => setTimeout(resolve, 800))

        const users: User[] = []
        for (let i = (page - 1) * limit + 1; i <= page * limit; i++) {
            users.push(generateRandomUser(i))
        }
        return users
    },

    async getPlans(page = 1, limit = 10): Promise<Plan[]> {
        await new Promise((resolve) => setTimeout(resolve, 800))

        const plans: Plan[] = []
        for (let i = (page - 1) * limit + 1; i <= page * limit; i++) {
            plans.push(generateRandomPlan(i))
        }
        return plans
    },

    async getRecentActivities(limit = 10): Promise<Activity[]> {
        await new Promise((resolve) => setTimeout(resolve, 600))

        const activities: Activity[] = []
        for (let i = 1; i <= limit; i++) {
            activities.push(generateRandomActivity(i))
        }
        return activities
    },

    async getSystemAlerts(): Promise<SystemAlert[]> {
        await new Promise((resolve) => setTimeout(resolve, 400))

        const alerts = [
            {
                id: 1,
                message: "Bảo trì máy chủ được lên lịch vào tối nay",
                type: "warning" as const,
                timestamp: new Date().toISOString(),
            },
            {
                id: 2,
                message: "Đăng ký người dùng mới tăng 25%",
                type: "info" as const,
                timestamp: new Date().toISOString(),
            },
            {
                id: 3,
                message: "Sự cố cổng thanh toán đã được giải quyết",
                type: "success" as const,
                timestamp: new Date().toISOString(),
            },
        ]

        return alerts
    },

    async getAnalyticsData() {
        await new Promise((resolve) => setTimeout(resolve, 1200))

        return {
            monthlyActiveUsers: Math.floor(Math.random() * 1000) + 2000,
            successRate: Math.floor(Math.random() * 20) + 70,
            monthlyRevenue: Math.floor(Math.random() * 5000) + 10000,
            averageSessionTime: Math.floor(Math.random() * 5) + 5,
            monthlyData: Array.from({ length: 6 }, (_, i) => ({
                month: `T${i + 1}`,
                users: Math.floor(Math.random() * 300) + 200,
                plans: Math.floor(Math.random() * 200) + 150,
                revenue: Math.floor(Math.random() * 2000) + 2000,
            })),
            successMetrics: [
                {
                    metric: "Tỷ Lệ Thành Công 30 Ngày",
                    value: Math.floor(Math.random() * 20) + 70,
                    target: 75,
                    color: "bg-green-500",
                },
                { metric: "Giữ Chân Người Dùng", value: Math.floor(Math.random() * 20) + 60, target: 70, color: "bg-blue-500" },
                {
                    metric: "Hoàn Thành Kế Hoạch",
                    value: Math.floor(Math.random() * 20) + 75,
                    target: 80,
                    color: "bg-purple-500",
                },
                {
                    metric: "Chuyển Đổi Premium",
                    value: Math.floor(Math.random() * 10) + 10,
                    target: 20,
                    color: "bg-yellow-500",
                },
            ],
        }
    },
}
