import { useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'react-toastify'
import { appointmentService } from '@/services/appointmentService'

type AppointmentStatusType = 'CONFIRMED' | 'MISSED' | 'CANCELLED' | 'COMPLETED'

export const useAppointmentStatus = (onSuccessCallback?: () => void) => {
  const queryClient = useQueryClient()
  const updateStatusMutation = useMutation({
    mutationFn: ({ appointmentId, newStatus }: { 
      appointmentId: number
      newStatus: AppointmentStatusType 
    }) => appointmentService.updateAppointmentStatus(appointmentId, newStatus),
    
    onMutate: async ({ appointmentId, newStatus }) => {
      // Cancel any outgoing refetches (so they don't overwrite our optimistic update)
      await queryClient.cancelQueries({ queryKey: ['weeklySchedule'] })
      
      // Show immediate feedback
      const statusTextMap: { [key: string]: string } = {
        'COMPLETED': 'hoàn thành',
        'CANCELLED': 'hủy',
        'CONFIRMED': 'xác nhận',
        'MISSED': 'đánh dấu bỏ lỡ'
      }
      
      const statusText = statusTextMap[newStatus] || 'cập nhật'
      
      toast.info(`Đang ${statusText} lịch hẹn...`, {
        position: "top-right",
        autoClose: 2000,
      })
      
      // Return a context object with the snapshotted value
      return { appointmentId, newStatus }
    },
      onSuccess: (_, variables) => {
      // Invalidate and refetch weekly schedule with specific query keys
      queryClient.invalidateQueries({ queryKey: ['weeklySchedule'] })
      queryClient.invalidateQueries({ queryKey: ['coachSchedule'] })
      
      // Force refetch all related queries
      queryClient.refetchQueries({ queryKey: ['weeklySchedule'] })
      
      // Call success callback if provided (to trigger UI refresh)
      if (onSuccessCallback) {
        // Small delay to ensure API response is processed
        setTimeout(() => {
          onSuccessCallback()
        }, 100)
      }
      
      // Show success message
      const statusTextMap: { [key: string]: string } = {
        'COMPLETED': 'hoàn thành',
        'CANCELLED': 'hủy',
        'CONFIRMED': 'xác nhận',
        'MISSED': 'đánh dấu bỏ lỡ'
      }
      
      const statusText = statusTextMap[variables.newStatus] || 'cập nhật'
      
      toast.success(`✅ Đã ${statusText} lịch hẹn thành công!`, {
        position: "top-right",
        autoClose: 3000,
      })
    },
    
    onError: (error: Error) => {
      // Handle axios error structure
      const axiosError = error as Error & {
        response?: {
          data?: {
            error?: string
            message?: string
          }
        }
      }
      const errorMessage = axiosError?.response?.data?.error || 
                          axiosError?.response?.data?.message || 
                          error?.message || 
                          'Có lỗi xảy ra khi cập nhật trạng thái lịch hẹn'
      
      toast.error(errorMessage, {
        position: "top-right",
        autoClose: 5000,
      })
    }
  })

  return {
    updateAppointmentStatus: updateStatusMutation.mutate,
    isUpdating: updateStatusMutation.isPending,
    error: updateStatusMutation.error
  }
}
