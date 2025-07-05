import axios from '../config/axiosConfig';

export interface CoachProfile {
  coachId: string;
  fullName: string;
  email: string;
  phone?: string;
  avatar?: string;
  title?: string;
  coachBio?: string;
  specializations?: string[];
  experience?: number;
  languages?: string[];
  location?: string;
  stats?: {
    totalClients: number;
    successRate: number;
    totalSessions: number;
    rating: number;
    reviewCount: number;
  };
  availability?: Record<string, string>;
}

export const getCoachProfile = (coachId: string): Promise<CoachProfile> =>
  axios.get(`/coach/${coachId}`).then(res => res.data.data);

export const updateCoachProfile = (coachId: string, data: Partial<CoachProfile>): Promise<CoachProfile> =>
  axios.put(`/coach/${coachId}`, data).then(res => res.data); 