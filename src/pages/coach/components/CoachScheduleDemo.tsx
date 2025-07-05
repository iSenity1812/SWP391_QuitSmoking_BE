import { WeeklyScheduleTable } from './WeeklyScheduleTable'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Calendar, Clock, CheckCircle2 } from 'lucide-react'

/**
 * Demo component để test tích hợp API đăng ký slot cho coach
 * Với các tính năng mới:
 * - Chọn ngày cụ thể để đăng ký slot
 * - Drag selection để chọn nhiều slot liên tiếp
 * - Ngăn đăng ký slot trong quá khứ
 * - Toast notification khi đăng ký thành công
 * - Cập nhật lịch ngay lập tức không cần refresh
 */
export function CoachScheduleDemo() {
  return (
    <div className="space-y-6">
      {/* Header Card */}
      <Card className="bg-gradient-to-r from-emerald-50 to-blue-50 dark:from-emerald-900/20 dark:to-blue-900/20 border-0">
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <Calendar className="w-6 h-6 text-emerald-600" />
            <span>Quản lý Lịch Làm Việc - Coach Dashboard</span>
          </CardTitle>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-4">
            <div className="flex items-center space-x-3 p-4 bg-white dark:bg-slate-800 rounded-lg border">
              <Clock className="w-8 h-8 text-blue-500" />
              <div>
                <p className="font-semibold text-slate-900 dark:text-white">Drag & Drop</p>
                <p className="text-sm text-slate-600 dark:text-slate-400">Kéo chuột để chọn nhiều slot</p>
              </div>
            </div>
            <div className="flex items-center space-x-3 p-4 bg-white dark:bg-slate-800 rounded-lg border">
              <Calendar className="w-8 h-8 text-emerald-500" />
              <div>
                <p className="font-semibold text-slate-900 dark:text-white">Chọn Ngày</p>
                <p className="text-sm text-slate-600 dark:text-slate-400">Đăng ký slot cho ngày cụ thể</p>
              </div>
            </div>
            <div className="flex items-center space-x-3 p-4 bg-white dark:bg-slate-800 rounded-lg border">
              <CheckCircle2 className="w-8 h-8 text-purple-500" />
              <div>
                <p className="font-semibold text-slate-900 dark:text-white">Auto Update</p>
                <p className="text-sm text-slate-600 dark:text-slate-400">Cập nhật lịch ngay lập tức</p>
              </div>
            </div>
          </div>
        </CardHeader>
      </Card>

      {/* API Integration Info */}
      <Card>
        <CardHeader>
          <CardTitle className="text-lg">🔗 API Integration Details</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="p-4 bg-slate-50 dark:bg-slate-800 rounded-lg">
              <h4 className="font-semibold text-slate-900 dark:text-white mb-2">📡 API Endpoint</h4>
              <code className="text-sm text-blue-600 dark:text-blue-400">POST /coaches/schedules</code>
            </div>
            <div className="p-4 bg-slate-50 dark:bg-slate-800 rounded-lg">
              <h4 className="font-semibold text-slate-900 dark:text-white mb-2">📋 Request Format</h4>
              <code className="text-sm text-emerald-600 dark:text-emerald-400">
                [{`{ timeSlotId: 1, scheduleDate: "2025-06-22" }`}]
              </code>
            </div>
          </div>

          <div className="p-4 bg-gradient-to-r from-blue-50 to-purple-50 dark:from-blue-900/20 dark:to-purple-900/20 rounded-lg border">
            <h4 className="font-semibold text-slate-900 dark:text-white mb-2">✨ Tính năng mới</h4>
            <ul className="space-y-2 text-sm text-slate-600 dark:text-slate-400">
              <li className="flex items-center space-x-2">
                <div className="w-2 h-2 bg-emerald-500 rounded-full"></div>
                <span>Chọn ngày cụ thể để đăng ký slot thay vì cả tuần</span>
              </li>
              <li className="flex items-center space-x-2">
                <div className="w-2 h-2 bg-blue-500 rounded-full"></div>
                <span>Drag selection: Kéo chuột qua nhiều slot để chọn nhanh</span>
              </li>
              <li className="flex items-center space-x-2">
                <div className="w-2 h-2 bg-purple-500 rounded-full"></div>
                <span>Ngăn đăng ký slot trong quá khứ (grayed out)</span>
              </li>
              <li className="flex items-center space-x-2">
                <div className="w-2 h-2 bg-orange-500 rounded-full"></div>
                <span>Toast notification và cập nhật lịch ngay lập tức</span>
              </li>
            </ul>
          </div>
        </CardContent>
      </Card>

      {/* Weekly Schedule Table */}
      <WeeklyScheduleTable />
    </div>
  )
}

export default CoachScheduleDemo
