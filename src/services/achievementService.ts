import axiosConfig from '@/config/axiosConfig';
import type { Achievement, MemberAchievement, AchievementWithStatus } from '@/types/achievement';

const API_BASE_URL = '/achievements'; // Đã có /api ở baseURL axiosConfig

export interface ProgressResponse {
  daysQuit: number;
  moneySaved: number;
  cigarettesNotSmoked: number;
}

export const achievementService = {
  async getAllAchievements(): Promise<Achievement[]> {
    try {
      const res = await axiosConfig.get(API_BASE_URL);
      if (Array.isArray(res.data)) return res.data;
      if (res.data && Array.isArray(res.data.data)) return res.data.data;
      return [];
    } catch (err) {
      throw new Error('Không thể lấy danh sách thành tựu');
    }
  },

  async getAchievementById(id: number): Promise<Achievement> {
    const res = await axiosConfig.get(`${API_BASE_URL}/${id}`);
    return res.data;
  },

  async getMemberAchievements(memberId: string): Promise<any[]> {
    const res = await axiosConfig.get(`${API_BASE_URL}/member/${memberId}/all`);
    if (Array.isArray(res.data)) return res.data;
    if (res.data && Array.isArray(res.data.data)) return res.data.data;
    return [];
  },

  async getUnlockedAchievements(memberId: string): Promise<Achievement[]> {
    const res = await axiosConfig.get(`${API_BASE_URL}/member/${memberId}/unlocked`);
    if (Array.isArray(res.data)) return res.data;
    if (res.data && Array.isArray(res.data.data)) return res.data.data;
    return [];
  },

  async getLockedAchievements(memberId: string): Promise<Achievement[]> {
    const res = await axiosConfig.get(`${API_BASE_URL}/member/${memberId}/locked`);
    if (Array.isArray(res.data)) return res.data;
    if (res.data && Array.isArray(res.data.data)) return res.data.data;
    return [];
  },

  async checkAndUnlockAchievements(memberId: string): Promise<string> {
    const res = await axiosConfig.post(`${API_BASE_URL}/member/${memberId}/check-unlock`);
    return res.data;
  },

  async getMemberProgress(memberId: string): Promise<ProgressResponse> {
    const res = await axiosConfig.get(`${API_BASE_URL}/member/${memberId}/progress`);
    return res.data;
  },

  async initializeDefaultAchievements(): Promise<string> {
    const res = await axiosConfig.post('/achievements/initialize');
    return res.data;
  },

  async createAchievement(data: Partial<Achievement>): Promise<Achievement> {
    try {
      const res = await axiosConfig.post(API_BASE_URL, data);
      return res.data;
    } catch (err) {
      throw new Error('Không thể tạo thành tựu mới');
    }
  },

  async updateAchievement(id: number, data: Partial<Achievement>): Promise<Achievement> {
    try {
      const res = await axiosConfig.put(`${API_BASE_URL}/${id}`, data);
      return res.data;
    } catch (err) {
      throw new Error('Không thể cập nhật thành tựu');
    }
  },

  async deleteAchievement(id: number): Promise<boolean> {
    try {
      await axiosConfig.delete(`${API_BASE_URL}/${id}`);
      return true;
    } catch (err) {
      throw new Error('Không thể xóa thành tựu');
    }
  },

  async getAllAchievementsForUser(memberId: string): Promise<AchievementWithStatus[]> {
    const res = await axiosConfig.get(`/achievements/member/${memberId}/all`);
    if (Array.isArray(res.data)) return res.data;
    if (res.data && Array.isArray(res.data.data)) return res.data.data;
    return [];
  },

  /**
   * Xóa tất cả thành tựu không còn đủ điều kiện cho user (admin hoặc user tự đồng bộ)
   */
  async cleanInvalidAchievements(memberId: string): Promise<string> {
    const res = await axiosConfig.delete(`/achievements/member/${memberId}/clean-invalid`);
    return res.data;
  },

  /**
   * Lấy milestone/cột mốc tiếp theo cho user từ backend
   */
  async getNextMilestone(memberId: string): Promise<any> {
    const res = await axiosConfig.get(`/achievements/member/${memberId}/next-milestone`);
    return res.data;
  },
}; 
 