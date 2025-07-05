import { Card, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import {
  Filter,
  Search,
  Trophy,
  Target,
  Award,
  CalendarDays,
  Coins,
  PiggyBank,
  Banknote,
  Gem,
  Plane,
  CheckCircle,
  TrendingDown,
  Shield,
  RefreshCw,
  Sunrise,
  Heart,
  Activity,
  Users,
  Sparkles,
  Gift,
  Flame,
} from "lucide-react"
import type { User } from "../../types/user-types"
import { useEffect, useState } from "react"
import { achievementService } from "@/services/achievementService"
import { useAuth } from "@/hooks/useAuth"
import { toast } from "react-toastify"

const getAchievementIcon = (iconName: string) => {
  switch (iconName) {
    case "calendar":
      return <CalendarDays className="h-8 w-8" />
    case "trophy":
      return <Trophy className="h-8 w-8" />
    case "award":
      return <Award className="h-8 w-8" />
    case "medal":
      return <Award className="h-8 w-8" /> // Using Award for medal as it's similar
    case "crown":
      return <Award className="h-8 w-8" /> // Using Award for crown
    case "flame":
      return <Flame className="h-8 w-8" />
    case "coins":
      return <Coins className="h-8 w-8" />
    case "piggy-bank":
      return <PiggyBank className="h-8 w-8" />
    case "banknote":
      return <Banknote className="h-8 w-8" />
    case "gem":
      return <Gem className="h-8 w-8" />
    case "plane":
      return <Plane className="h-8 w-8" />
    case "check-circle":
      return <CheckCircle className="h-8 w-8" />
    case "trending-down":
      return <TrendingDown className="h-8 w-8" />
    case "target":
      return <Target className="h-8 w-8" />
    case "shield":
      return <Shield className="h-8 w-8" />
    case "refresh-cw":
      return <RefreshCw className="h-8 w-8" />
    case "sunrise":
      return <Sunrise className="h-8 w-8" />
    case "heart":
      return <Heart className="h-8 w-8" />
    case "activity":
      return <Activity className="h-8 w-8" />
    case "users":
      return <Users className="h-8 w-8" />
    case "sparkles":
      return <Sparkles className="h-8 w-8" />
    case "gift":
      return <Gift className="h-8 w-8" />
    case "calendar-days":
      return <CalendarDays className="h-8 w-8" />
    default:
      return <Award className="h-8 w-8" /> // Default icon
  }
}

const getTypeLabel = (type: string) => {
  switch (type) {
    case "DAYS_QUIT": return "Chuỗi ngày";
    case "MONEY_SAVED": return "Tiết kiệm tiền";
    case "CIGARETTES_NOT_SMOKED": return "Điếu thuốc tránh được";
    case "RESILIENCE": return "Kiên trì";
    case "HEALTH": return "Sức khỏe";
    case "SOCIAL": return "Xã hội";
    case "SPECIAL": return "Đặc biệt";
    case "DAILY": return "Hàng ngày";
    default: return "Khác";
  }
}

export function AchievementsTab() {
  const { user } = useAuth();
  const [achievements, setAchievements] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [syncing, setSyncing] = useState(false);

  useEffect(() => {
    if (!user?.userId) return;
    setLoading(true);
    achievementService.getAllAchievementsForUser(user.userId)
      .then(data => setAchievements(data))
      .finally(() => setLoading(false));
  }, [user?.userId]);

  const handleSyncAchievements = async () => {
    if (!user?.userId) return;
    setSyncing(true);
    try {
      await achievementService.cleanInvalidAchievements(user.userId);
      toast.success("Đồng bộ thành tựu thành công!");
      achievementService.getAllAchievementsForUser(user.userId)
        .then(data => setAchievements(data));
    } catch (e) {
      toast.error("Lỗi khi đồng bộ thành tựu!");
    } finally {
      setSyncing(false);
    }
  };

  if (loading) return <div>Đang tải thành tựu...</div>;
  if (!achievements.length) return <div>Không có thành tựu nào.</div>;

  // Tính toán số lượng đã hoàn thành/chưa hoàn thành
  const completedCount = achievements.filter(a => a.completed).length;
  const notCompletedCount = achievements.length - completedCount;
  const percentCompleted = Math.round((completedCount / achievements.length) * 100);

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-slate-900 dark:text-white">Thành tựu của bạn</h2>
        <div className="flex gap-2">
          <Button variant="outline" size="sm">
            <Filter className="h-4 w-4 mr-2" />
            Lọc
          </Button>
          <Button variant="outline" size="sm">
            <Search className="h-4 w-4 mr-2" />
            Tìm kiếm
          </Button>
        </div>
      </div>

      {/* Achievement Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card className="bg-gradient-to-br from-emerald-500 to-emerald-600 text-white border-0 shadow-lg relative overflow-hidden">
          <CardContent className="p-6 text-center relative z-10">
            <div className="bg-white/20 rounded-full p-4 w-16 h-16 mx-auto mb-4 flex items-center justify-center">
              <Trophy className="h-8 w-8" />
            </div>
            <p className="text-4xl font-bold mb-2">{completedCount}</p>
            <p className="text-emerald-100 text-sm">Đã hoàn thành</p>
          </CardContent>
          <div className="absolute top-4 right-4 w-12 h-8 bg-white/10 rounded-full"></div>
          <div className="absolute bottom-4 left-4 w-16 h-10 bg-white/10 rounded-full"></div>
          <div className="absolute top-1/2 left-8 w-8 h-6 bg-white/10 rounded-full"></div>
        </Card>
        <Card className="bg-gradient-to-br from-blue-500 to-blue-600 text-white border-0 shadow-lg relative overflow-hidden">
          <CardContent className="p-6 text-center relative z-10">
            <div className="bg-white/20 rounded-full p-4 w-16 h-16 mx-auto mb-4 flex items-center justify-center">
              <Target className="h-8 w-8" />
            </div>
            <p className="text-4xl font-bold mb-2">{notCompletedCount}</p>
            <p className="text-blue-100 text-sm">Chưa hoàn thành</p>
          </CardContent>
          <div className="absolute top-6 left-6 w-10 h-6 bg-white/10 rounded-full"></div>
          <div className="absolute bottom-6 right-6 w-14 h-8 bg-white/10 rounded-full"></div>
          <div className="absolute top-1/3 right-4 w-6 h-4 bg-white/10 rounded-full"></div>
        </Card>
        <Card className="bg-gradient-to-br from-purple-500 to-purple-600 text-white border-0 shadow-lg relative overflow-hidden">
          <CardContent className="p-6 text-center relative z-10">
            <div className="bg-white/20 rounded-full p-4 w-16 h-16 mx-auto mb-4 flex items-center justify-center"></div>
            <p className="text-4xl font-bold mb-2">{percentCompleted}%</p>
            <p className="text-purple-100 text-sm">Tỷ lệ hoàn thành</p>
          </CardContent>
          <div className="absolute top-4 left-4 w-8 h-6 bg-white/10 rounded-full"></div>
          <div className="absolute bottom-8 right-8 w-12 h-8 bg-white/10 rounded-full"></div>
          <div className="absolute bottom-4 left-1/3 w-6 h-4 bg-white/10 rounded-full"></div>
        </Card>
      </div>

      <div className="flex items-center mb-4">
        <button
          className="ml-auto p-2 bg-blue-600 text-white rounded-full hover:bg-blue-700 disabled:opacity-50 flex items-center justify-center"
          onClick={handleSyncAchievements}
          disabled={syncing}
          aria-label="Đồng bộ thành tựu"
          title="Đồng bộ thành tựu"
        >
          <RefreshCw className={`h-5 w-5 ${syncing ? "animate-spin" : ""}`} />
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {achievements.map((achievement) => {
          const isCompleted = achievement.completed;
          return (
            <Card
              key={achievement.id || achievement.achievementId}
              className={`relative ${isCompleted
                  ? "bg-gradient-to-br from-emerald-50 to-emerald-100 dark:from-emerald-900/30 dark:to-emerald-800/30 border-emerald-200 dark:border-emerald-800"
                  : "bg-slate-50 dark:bg-slate-800 border-slate-200 dark:border-slate-700 opacity-80"
                }`}
            >
              <CardContent className="p-6">
                <div className="text-center">
                  {/* Category Badge */}
                  <div className="absolute top-2 left-2">
                    <Badge>{getTypeLabel(achievement.achievementType)}</Badge>
                  </div>
                  <div
                    className={`rounded-full p-4 mb-4 inline-block ${isCompleted ? "bg-emerald-100 dark:bg-emerald-900" : "bg-slate-100 dark:bg-slate-700"}`}
                  >
                    {getAchievementIcon(achievement.icon || achievement.icon_url)}
                  </div>
                  <h3
                    className={`font-semibold mb-2 ${isCompleted
                        ? "text-emerald-700 dark:text-emerald-400"
                        : "text-slate-500 dark:text-slate-400"
                      }`}
                  >
                    {achievement.name || achievement.achievement_name}
                  </h3>
                  <p className="text-sm text-slate-600 dark:text-slate-400 mb-3">{achievement.description}</p>
                  {isCompleted ? (
                    <Badge
                      variant="secondary"
                      className="bg-emerald-100 text-emerald-700 dark:bg-emerald-900/50 dark:text-emerald-400"
                    >
                      Hoàn thành {achievement.completedDate || achievement.completed_date}
                    </Badge>
                  ) : (
                    <Badge variant="outline" className="dark:border-slate-600 dark:text-slate-400">
                      Chưa hoàn thành
                    </Badge>
                  )}
                </div>
              </CardContent>
            </Card>
          )
        })}
      </div>
    </div>
  )
}
