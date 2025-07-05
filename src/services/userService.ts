import axiosConfig from "@/config/axiosConfig";

export interface UserProfileResponse {
    userId: string;
    username: string;
    email: string;
    profilePicture?: string;
    createdAt: string;
    updatedAt?: string;
    isActive?: boolean;
}

export interface UpdateProfileRequest {
    name: string;
    email: string;
}

export interface ChangePasswordRequest {
    currentPassword: string;
    newPassword: string;
}

export interface UserQuitStatsResponse {
    daysWithoutSmoking: number;
    cigarettesAvoided: number;
    moneySaved: number;
}

export const userService = {
    getPublicProfile: async (userId: string): Promise<UserProfileResponse> => {
        const response = await axiosConfig.get<UserProfileResponse>(`/public/profile/${userId}`);
        return response.data;
    },

    isFollowing: async (userId: string, currentUserId: string): Promise<boolean> => {
        const response = await axiosConfig.get<boolean>(
            `/api/follow/is-following/${userId}`,
            { headers: { 'X-User-Id': currentUserId } }
        );
        return response.data;
    },

    searchUsers: async (query: string): Promise<UserProfileResponse[]> => {
        const response = await axiosConfig.get(`/public/users/search?query=${encodeURIComponent(query)}`);
        return response.data && Array.isArray(response.data.data) ? response.data.data : [];
    },

    follow: async (userId: string, currentUserId: string): Promise<void> => {
        await axiosConfig.post(`/api/follow/${userId}`, null, { headers: { 'X-User-Id': currentUserId } });
    },

    unfollow: async (userId: string, currentUserId: string): Promise<void> => {
        await axiosConfig.delete(`/api/follow/${userId}`, { headers: { 'X-User-Id': currentUserId } });
    },

    getCurrentUser: async (): Promise<UserProfileResponse> => {
        const response = await axiosConfig.get<UserProfileResponse>("/profile");
        return response.data;
    },

    getQuitStats: async (): Promise<UserQuitStatsResponse> => {
        const response = await axiosConfig.get<UserQuitStatsResponse>("/profile/quit-stats");
        return response.data;
    },

    updateProfile: async (data: UpdateProfileRequest): Promise<UserProfileResponse> => {
        const response = await axiosConfig.patch<UserProfileResponse>(
            "/profile",
            {
                username: data.name,
                email: data.email
            }
        );
        return response.data;
    },

    changePassword: async (data: ChangePasswordRequest): Promise<void> => {
        await axiosConfig.patch(
            "/profile/password",
            {
                currentPassword: data.currentPassword,
                newPassword: data.newPassword
            }
        );
    },
}; 