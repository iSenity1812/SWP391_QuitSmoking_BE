import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { X, Calendar, Clock, User, Loader2 } from 'lucide-react'
import type { Coach, TimeSlot } from '@/services/timeSlotService'

interface BookingNoteModalProps {
  isOpen: boolean
  onClose: () => void
  onSubmit: (note: string) => void
  coach: Coach | null
  timeSlot: TimeSlot | null
  scheduleDate: string
  isLoading?: boolean
}

export function BookingNoteModal({
  isOpen,
  onClose,
  onSubmit,
  coach,
  timeSlot,
  scheduleDate,
  isLoading = false
}: BookingNoteModalProps) {
  const [note, setNote] = useState('')
  const [errors, setErrors] = useState<{ note?: string }>({})

  const validateNote = (value: string): string | undefined => {
    if (!value.trim()) {
      return 'Vui lòng nhập ghi chú cho buổi tư vấn'
    }
    if (value.trim().length < 10) {
      return 'Ghi chú phải có ít nhất 10 ký tự'
    }
    if (value.trim().length > 500) {
      return 'Ghi chú không được vượt quá 500 ký tự'
    }
    return undefined
  }

  const handleNoteChange = (value: string) => {
    setNote(value)
    // Clear error when user starts typing
    if (errors.note) {
      setErrors({ ...errors, note: undefined })
    }
  }

  const handleSubmit = () => {
    const noteError = validateNote(note)

    if (noteError) {
      setErrors({ note: noteError })
      return
    }

    onSubmit(note.trim())
  }

  const handleClose = () => {
    if (!isLoading) {
      setNote('')
      setErrors({})
      onClose()
    }
  }

  const formatDate = (dateString: string) => {
    const date = new Date(dateString)
    return date.toLocaleDateString('vi-VN', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    })
  }

  if (!coach || !timeSlot) return null

  return (
    <Dialog open={isOpen} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-[500px] max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <div className="flex items-center justify-between">
            <DialogTitle className="text-xl font-semibold text-slate-900 dark:text-white flex items-center space-x-2">
              Xác nhận đặt lịch tư vấn
            </DialogTitle>
            <Button
              variant="ghost"
              size="sm"
              onClick={handleClose}
              disabled={isLoading}
              className="h-6 w-6 p-0"
            >
              <X className="h-4 w-4" />
            </Button>
          </div>
        </DialogHeader>

        <div className="space-y-6 ">
          {/* Booking Summary */}
          <div className="bg-emerald-50 border border-emerald-200 rounded-lg p-4 space-y-3 dark:bg-emerald-900 dark:border-emerald-700 ">
            <h3 className="font-medium text-emerald-800 dark:text-white">Thông tin đặt lịch</h3>

            <div className="space-y-2 text-sm">
              <div className="flex items-center space-x-2 text-slate-700 dark:text-white">
                <User className="w-4 h-4 text-emerald-600" />
                <span className="font-medium">Coach:</span>
                <span>{coach.fullName}</span>
              </div>

              <div className="flex items-center space-x-2 text-slate-700 dark:text-white">
                <Calendar className="w-4 h-4 text-emerald-600" />
                <span className="font-medium">Ngày:</span>
                <span>{formatDate(scheduleDate)}</span>
              </div>

              <div className="flex items-center space-x-2 text-slate-700 dark:text-white">
                <Clock className="w-4 h-4 text-emerald-600" />
                <span className="font-medium ">Giờ:</span>
                <span>{timeSlot.startTime} - {timeSlot.endTime}</span>
              </div>

              {/* {coach.rating && (
                <div className="flex items-center space-x-2 text-slate-700">
                  <span className="font-medium">Đánh giá:</span>
                  <div className="flex items-center space-x-1">
                    <span className="text-yellow-500">★</span>
                    <span>{coach.rating.toFixed(1)}/5</span>
                  </div>
                </div>
              )} */}
            </div>
          </div>

          {/* Note Input */}
          <div className="space-y-3">
            <Label htmlFor="note" className="text-sm font-medium text-slate-700">
              Ghi chú cho buổi tư vấn <span className="text-red-500">*</span>
            </Label>
            <Textarea
              id="note"
              placeholder="Mô tả tình trạng hiện tại, mục tiêu bỏ thuốc, hoặc những vấn đề bạn muốn tham khảo với coach..."
              value={note}
              onChange={(e) => handleNoteChange(e.target.value)}
              disabled={isLoading}
              className={`min-h-[120px] resize-none ${errors.note ? 'border-red-300 focus:border-red-500' : ''
                }`}
              maxLength={500}
            />

            <div className="flex justify-between text-xs text-slate-500">
              <span>{errors.note && <span className="text-red-500">{errors.note}</span>}</span>
              <span>{note.length}/500</span>
            </div>
          </div>

          {/* Action Buttons */}
          <div className="flex space-x-3 pt-4">
            <Button
              variant="outline"
              onClick={handleClose}
              disabled={isLoading}
              className="flex-1 border-slate-300 text-slate-700 hover:bg-slate-50"
            >
              Hủy
            </Button>
            <Button
              onClick={handleSubmit}
              disabled={isLoading || !note.trim()}
              className="flex-1 bg-emerald-600 hover:bg-emerald-700 text-white"
            >
              {isLoading ? (
                <>
                  <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                  Đang đặt lịch...
                </>
              ) : (
                'Xác nhận đặt lịch'
              )}
            </Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  )
}
