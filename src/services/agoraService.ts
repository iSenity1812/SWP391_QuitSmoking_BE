import axiosConfig from "@/config/axiosConfig";
import { isAxiosError } from "axios";
import { toast } from "react-toastify";

export interface AgoraTokenResponse {
  agoraAppId: string
  agoraUid: string
  agoraChannelName: string
  agoraToken: string
}

class AgoraService {
  /**
 * Lấy token Agora cho một cuộc hẹn
 * @param appointmentId ID của cuộc hẹn
 * @param isPublisher True nếu là coach (người publish), false nếu là member (người subscribe)
 */

  async getAgoraToken(appointmentId: number, isPublisher: boolean): Promise<AgoraTokenResponse> {
    try {
      console.log(`Lấy agora token cho appointment ${appointmentId}, isPublisher: ${isPublisher}`);
      const response = await axiosConfig.get(`/agora/token/${appointmentId}`, {
        params: { isPublisher },
      });

      // Backend sẽ trả về dữ liệu dạng
      //       {
      //   "status": 200,
      //   "message": "Agora token generated successfully.",
      //   "data": {
      //     "agoraAppId": "app id của agora",
      //     "agoraUid": "uuid của người dùng",
      //     "agoraChannelName": "appointment_UUID",
      //     "agoraToken": "token của agora"
      //   },
      //   "error": null,
      //   "errorCode": null,
      //   "timestamp": "2025-06-28T10:50:04.8651041"
      // }
      console.log('Agora token response:', response.data);
      if (response.data.status === 200 && response.data.data) {
        return response.data.data;
      } else {
        throw new Error(response.data.message || 'Failed to get Agora token');
      }
    } catch (error: unknown) {
      console.error('Error getting Agora token:', error);
      // Lấy lỗi từ backend và hiển thị thông báo lỗi
      let errorMessage = 'Lỗi không xác định';
      if (isAxiosError(error) && error.response?.data?.message) {
        errorMessage = error.response.data.message;
      }
      toast.error(`Không thể kết nối đến cuộc gọi video. Vui lòng thử lại sau. ${errorMessage}`);

      // Handle different types of errors
      if (isAxiosError(error) && error.response) {
        // Server responded with error status
        const errorMessage = error.response.data?.message || 'Server error occurred';
        throw new Error(errorMessage);
      } else if (isAxiosError(error) && error.request) {
        // Request was made but no response received
        throw new Error('Không thể kết nối đến server. Vui lòng thử lại.');
      } else {
        // Something else happened
        const message = isAxiosError(error) && error.message ? error.message : 'Đã xảy ra lỗi không xác định';
        throw new Error(message);
      }
    }
  }
}

export const agoraService = new AgoraService();