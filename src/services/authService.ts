import type { AccountResponse, ApiResponse, LoginRequest, RegisterRequest } from "@/types/auth";
import axiosConfig from "@/config/axiosConfig";

const AUTH_ENDPOINT_PREFIX = "/auth";
const AUTH_TOKEN = "jwt_token";
const USER_INFO = "user_info";

export const authService = {
  register: async (data: RegisterRequest): Promise<ApiResponse<AccountResponse>> => {
    const response = await axiosConfig.post<ApiResponse<AccountResponse>>(
      `${AUTH_ENDPOINT_PREFIX}/register`, data)

      if (response.data && response.data.data) {
        // Save token and user info to localStorage
        localStorage.setItem(AUTH_TOKEN, response.data.data.token);
        localStorage.setItem(USER_INFO, JSON.stringify(response.data.data));
      }
      return response.data;
  },

  login: async (data: LoginRequest): Promise<ApiResponse<AccountResponse>> => {
    const response = await axiosConfig.post<ApiResponse<AccountResponse>>(
      `${AUTH_ENDPOINT_PREFIX}/login`, data);

    if (response.data && response.data.data) {
      // Save token and user info to localStorage
      localStorage.setItem(AUTH_TOKEN, response.data.data.token);
      localStorage.setItem(USER_INFO, JSON.stringify(response.data.data));
    }
    return response.data;
  },

  logout: async () => {
    try {
      await axiosConfig.post(`${AUTH_ENDPOINT_PREFIX}/logout`);
    } catch (err) {
      console.error("Logout failed, clearing localStorage:", err);
    } finally {
      localStorage.removeItem(AUTH_TOKEN);
      localStorage.removeItem(USER_INFO);
      delete axiosConfig.defaults.headers.common['Authorization']; // Xoá token khỏi header
    }
  },

  getToken: (): string | null => {
    return localStorage.getItem(AUTH_TOKEN);
  },
}