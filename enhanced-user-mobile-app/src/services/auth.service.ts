import AsyncStorage from '@react-native-async-storage/async-storage';
import { ApiResponse, LoginRequest, RegisterRequest, User } from '../types';
import { apiClient } from './api.service';

const TOKEN_KEY = '@exalt_courier_token';
const REFRESH_TOKEN_KEY = '@exalt_courier_refresh_token';

class AuthService {
  private baseUrl = '/api/v1/auth';

  async login(credentials: LoginRequest): Promise<ApiResponse<{ user: User; token: string; refreshToken: string }>> {
    try {
      const response = await apiClient.post(`${this.baseUrl}/login`, credentials);
      
      if (response.data.success && response.data.data) {
        const { token, refreshToken } = response.data.data;
        
        // Store tokens
        await this.storeTokens(token, refreshToken);
        
        // Set default authorization header
        apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      }
      
      return response.data;
    } catch (error: any) {
      if (error.response?.data) {
        return error.response.data;
      }
      
      return {
        success: false,
        error: 'Network error. Please check your connection and try again.',
      };
    }
  }

  async register(userData: RegisterRequest): Promise<ApiResponse<{ user: User; token: string; refreshToken: string }>> {
    try {
      const response = await apiClient.post(`${this.baseUrl}/register`, userData);
      
      if (response.data.success && response.data.data) {
        const { token, refreshToken } = response.data.data;
        
        // Store tokens
        await this.storeTokens(token, refreshToken);
        
        // Set default authorization header
        apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      }
      
      return response.data;
    } catch (error: any) {
      if (error.response?.data) {
        return error.response.data;
      }
      
      return {
        success: false,
        error: 'Network error. Please check your connection and try again.',
      };
    }
  }

  async logout(): Promise<ApiResponse> {
    try {
      const response = await apiClient.post(`${this.baseUrl}/logout`);
      
      // Clear stored tokens regardless of response
      await this.clearTokens();
      
      // Remove authorization header
      delete apiClient.defaults.headers.common['Authorization'];
      
      return response.data;
    } catch (error: any) {
      // Clear tokens even if logout fails
      await this.clearTokens();
      delete apiClient.defaults.headers.common['Authorization'];
      
      if (error.response?.data) {
        return error.response.data;
      }
      
      return {
        success: false,
        error: 'Network error during logout.',
      };
    }
  }

  async refreshToken(): Promise<ApiResponse<{ user: User; token: string; refreshToken: string }>> {
    try {
      const refreshToken = await AsyncStorage.getItem(REFRESH_TOKEN_KEY);
      
      if (!refreshToken) {
        throw new Error('No refresh token available');
      }
      
      const response = await apiClient.post(`${this.baseUrl}/refresh`, {
        refreshToken,
      });
      
      if (response.data.success && response.data.data) {
        const { token, refreshToken: newRefreshToken } = response.data.data;
        
        // Store new tokens
        await this.storeTokens(token, newRefreshToken);
        
        // Update authorization header
        apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      }
      
      return response.data;
    } catch (error: any) {
      // Clear tokens if refresh fails
      await this.clearTokens();
      delete apiClient.defaults.headers.common['Authorization'];
      
      if (error.response?.data) {
        return error.response.data;
      }
      
      return {
        success: false,
        error: 'Session expired. Please log in again.',
      };
    }
  }

  async verifyToken(): Promise<ApiResponse<User>> {
    try {
      const response = await apiClient.get(`${this.baseUrl}/verify`);
      return response.data;
    } catch (error: any) {
      if (error.response?.data) {
        return error.response.data;
      }
      
      return {
        success: false,
        error: 'Token verification failed.',
      };
    }
  }

  async forgotPassword(email: string): Promise<ApiResponse> {
    try {
      const response = await apiClient.post(`${this.baseUrl}/forgot-password`, { email });
      return response.data;
    } catch (error: any) {
      if (error.response?.data) {
        return error.response.data;
      }
      
      return {
        success: false,
        error: 'Network error. Please try again.',
      };
    }
  }

  async resetPassword(token: string, password: string): Promise<ApiResponse> {
    try {
      const response = await apiClient.post(`${this.baseUrl}/reset-password`, {
        token,
        password,
      });
      return response.data;
    } catch (error: any) {
      if (error.response?.data) {
        return error.response.data;
      }
      
      return {
        success: false,
        error: 'Network error. Please try again.',
      };
    }
  }

  async changePassword(currentPassword: string, newPassword: string): Promise<ApiResponse> {
    try {
      const response = await apiClient.post(`${this.baseUrl}/change-password`, {
        currentPassword,
        newPassword,
      });
      return response.data;
    } catch (error: any) {
      if (error.response?.data) {
        return error.response.data;
      }
      
      return {
        success: false,
        error: 'Network error. Please try again.',
      };
    }
  }

  async updateProfile(userData: Partial<User>): Promise<ApiResponse<User>> {
    try {
      const response = await apiClient.put('/api/v1/profile', userData);
      return response.data;
    } catch (error: any) {
      if (error.response?.data) {
        return error.response.data;
      }
      
      return {
        success: false,
        error: 'Network error. Please try again.',
      };
    }
  }

  async uploadAvatar(imageUri: string): Promise<ApiResponse<{ avatarUrl: string }>> {
    try {
      const formData = new FormData();
      formData.append('avatar', {
        uri: imageUri,
        type: 'image/jpeg',
        name: 'avatar.jpg',
      } as any);
      
      const response = await apiClient.post('/api/v1/profile/avatar', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      
      return response.data;
    } catch (error: any) {
      if (error.response?.data) {
        return error.response.data;
      }
      
      return {
        success: false,
        error: 'Failed to upload avatar. Please try again.',
      };
    }
  }

  async verifyEmail(token: string): Promise<ApiResponse> {
    try {
      const response = await apiClient.post(`${this.baseUrl}/verify-email`, { token });
      return response.data;
    } catch (error: any) {
      if (error.response?.data) {
        return error.response.data;
      }
      
      return {
        success: false,
        error: 'Email verification failed.',
      };
    }
  }

  async resendEmailVerification(): Promise<ApiResponse> {
    try {
      const response = await apiClient.post(`${this.baseUrl}/resend-verification`);
      return response.data;
    } catch (error: any) {
      if (error.response?.data) {
        return error.response.data;
      }
      
      return {
        success: false,
        error: 'Failed to resend verification email.',
      };
    }
  }

  async verifyPhone(code: string): Promise<ApiResponse> {
    try {
      const response = await apiClient.post(`${this.baseUrl}/verify-phone`, { code });
      return response.data;
    } catch (error: any) {
      if (error.response?.data) {
        return error.response.data;
      }
      
      return {
        success: false,
        error: 'Phone verification failed.',
      };
    }
  }

  async resendPhoneVerification(): Promise<ApiResponse> {
    try {
      const response = await apiClient.post(`${this.baseUrl}/resend-phone-verification`);
      return response.data;
    } catch (error: any) {
      if (error.response?.data) {
        return error.response.data;
      }
      
      return {
        success: false,
        error: 'Failed to resend phone verification.',
      };
    }
  }

  private async storeTokens(token: string, refreshToken: string): Promise<void> {
    await Promise.all([
      AsyncStorage.setItem(TOKEN_KEY, token),
      AsyncStorage.setItem(REFRESH_TOKEN_KEY, refreshToken),
    ]);
  }

  private async clearTokens(): Promise<void> {
    await Promise.all([
      AsyncStorage.removeItem(TOKEN_KEY),
      AsyncStorage.removeItem(REFRESH_TOKEN_KEY),
    ]);
  }

  async getStoredToken(): Promise<string | null> {
    return AsyncStorage.getItem(TOKEN_KEY);
  }

  async initializeAuth(): Promise<void> {
    const token = await this.getStoredToken();
    if (token) {
      apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    }
  }
}

export const authService = new AuthService();