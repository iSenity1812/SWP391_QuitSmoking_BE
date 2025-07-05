import { authService } from "@/services/authService";
import type { AccountResponse, ApiResponse, LoginRequest, RegisterRequest } from "@/types/auth";
import { createContext, useCallback, useEffect, useState, type ReactNode } from "react";
import { useNavigate } from "react-router-dom";

interface AuthContextType {
  isAuthenticated: boolean;
  user: AccountResponse | null; // Thông tin người dùng khi đã đăng nhập
  token: string | null; // JWT token
  login: (credentials: LoginRequest) => Promise<ApiResponse<AccountResponse>>;
  register: (data: RegisterRequest) => Promise<ApiResponse<AccountResponse>>;
  logout: () => void;
  isLoading: boolean; // Trạng thái đang tải
}

// jwt & user info
const AUTH_TOKEN = "jwt_token";
const USER_INFO = "user_info";

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [user, setUser] = useState<AccountResponse | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true); // Start with true for initial auth check
  const navigate = useNavigate();
  // Helper function to validate token
  const isTokenValid = (token: string): boolean => {
    try {
      // Basic JWT structure check
      const parts = token.split('.');
      if (parts.length !== 3) return false;

      // Decode payload to check expiry
      const payload = JSON.parse(atob(parts[1]));
      const currentTime = Math.floor(Date.now() / 1000);

      // Check if token is expired (with 30 second buffer)
      return payload.exp && payload.exp > (currentTime + 30);
    } catch {
      return false;
    }
  };

  const clearAuthData = useCallback(() => {
    setToken(null);
    setUser(null);
    setIsAuthenticated(false);
    localStorage.removeItem(AUTH_TOKEN);
    localStorage.removeItem(USER_INFO);
  }, []);

  useEffect(() => {
    const initializeAuth = async () => {
      try {
        const storedToken = localStorage.getItem(AUTH_TOKEN);
        const storedUser = localStorage.getItem(USER_INFO);

        if (storedToken && storedUser) {
          // Validate token before using it
          if (isTokenValid(storedToken)) {
            const parsedUser = JSON.parse(storedUser) as AccountResponse;
            setToken(storedToken);
            setUser(parsedUser);
            setIsAuthenticated(true);
          } else {
            // Token is invalid or expired, clear data
            console.warn("Stored token is invalid or expired");
            clearAuthData();
          }
        }
      } catch (err) {
        console.error("Error initializing auth from localStorage:", err);
        clearAuthData();
      } finally {
        setIsLoading(false);
      }
    };

    initializeAuth();
  }, [clearAuthData]);

  const login = useCallback(async (credentials: LoginRequest): Promise<ApiResponse<AccountResponse>> => {
    setIsLoading(true);
    try {
      const response = await authService.login(credentials);
      if (response.status === 200 && response.data) {
        const { token, ...userData } = response.data;

        // Save to localStorage immediately
        localStorage.setItem(AUTH_TOKEN, token);
        localStorage.setItem(USER_INFO, JSON.stringify(userData));

        // Update state
        setToken(token);
        setUser(userData as AccountResponse);
        setIsAuthenticated(true);

        if (userData.role === "SUPER_ADMIN") {
          navigate("/admin/dashboard");
        } else if (userData.role === "NORMAL_MEMBER" || userData.role === "PREMIUM_MEMBER") {
          navigate("/");
        }
      }
      return response;
    } catch (err) {
      console.error("Login failed:", err);
      clearAuthData();
      return {
        status: 500,
        message: "Login failed",
        data: null,
        error: "An error occurred during login",
        errorCode: null,
        timestamp: new Date().toISOString(),
      };
    } finally {
      setIsLoading(false);
    }
  }, [navigate, clearAuthData]);
  // sign up xong thi cap token luon de sang trang home
  const register = useCallback(async (data: RegisterRequest): Promise<ApiResponse<AccountResponse>> => {
    setIsLoading(true);
    try {
      const response = await authService.register(data);
      if (response.status === 200 && response.data) {
        const { token, ...userData } = response.data;

        // Save to localStorage immediately
        localStorage.setItem(AUTH_TOKEN, token);
        localStorage.setItem(USER_INFO, JSON.stringify(userData));

        // Update state
        setToken(token);
        setUser(userData as AccountResponse);
        setIsAuthenticated(true);

        if (userData.role === "SUPER_ADMIN") {
          navigate("/admin/dashboard");
        } else if (userData.role === "NORMAL_MEMBER" || userData.role === "PREMIUM_MEMBER") {
          navigate("/");
        }
      }
      return response;
    } catch (err) {
      console.error("Registration failed:", err);
      clearAuthData();
      return {
        status: 500,
        message: "Registration failed",
        data: null,
        error: "An error occurred during registration",
        errorCode: null,
        timestamp: new Date().toISOString(),
      };
    } finally {
      setIsLoading(false);
    }
  }, [navigate, clearAuthData]);
  const logout = useCallback(() => {
    setIsLoading(true);
    authService.logout().then(() => {
      clearAuthData();
      navigate("/");
    }).catch((err) => {
      console.error("Logout failed:", err);
      // Even if logout API fails, clear local data
      clearAuthData();
      navigate("/");
    }).finally(() => {
      setIsLoading(false);
    });
  }, [navigate, clearAuthData]);

  const authContextValue: AuthContextType = {
    isAuthenticated,
    user,
    token,
    login,
    register,
    logout,
    isLoading,
  };
  return (
    <AuthContext.Provider value={authContextValue}>
      {children}
    </AuthContext.Provider>
  );
}

// useAuth hook moved to useAuth.ts