import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { Alert } from 'react-native';
import Config from 'react-native-config';

interface ApiConfig extends AxiosRequestConfig {
  skipAuth?: boolean;
  skipLoader?: boolean;
}

class ApiService {
  private client: AxiosInstance;
  private refreshing = false;
  private failedQueue: Array<{
    resolve: (token: string) => void;
    reject: (error: any) => void;
  }> = [];

  constructor() {
    this.client = axios.create({
      baseURL: Config.API_BASE_URL || 'http://localhost:8310',
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  private setupInterceptors() {
    // Request interceptor to add auth token
    this.client.interceptors.request.use(
      async (config: any) => {
        // Add auth token if not explicitly skipped
        if (!config.skipAuth) {
          const token = await AsyncStorage.getItem('@exalt_courier_token');
          if (token) {
            config.headers.Authorization = `Bearer ${token}`;
          }
        }

        // Add request ID for tracking
        config.headers['X-Request-ID'] = this.generateRequestId();

        console.log(`🚀 API Request: ${config.method?.toUpperCase()} ${config.url}`);
        return config;
      },
      (error) => {
        console.error('❌ Request interceptor error:', error);
        return Promise.reject(error);
      }
    );

    // Response interceptor for error handling and token refresh
    this.client.interceptors.response.use(
      (response: AxiosResponse) => {
        console.log(`✅ API Response: ${response.status} ${response.config.url}`);
        return response;
      },
      async (error) => {
        const originalRequest = error.config;

        console.error(`❌ API Error: ${error.response?.status} ${originalRequest?.url}`);

        // Handle 401 Unauthorized - Token expired
        if (error.response?.status === 401 && !originalRequest._retry) {
          if (this.refreshing) {
            // If already refreshing, queue this request
            return new Promise((resolve, reject) => {
              this.failedQueue.push({ resolve, reject });
            }).then((token: string) => {
              originalRequest.headers.Authorization = `Bearer ${token}`;
              return this.client(originalRequest);
            }).catch((err) => {
              return Promise.reject(err);
            });
          }

          originalRequest._retry = true;
          this.refreshing = true;

          try {
            const refreshToken = await AsyncStorage.getItem('@exalt_courier_refresh_token');
            
            if (!refreshToken) {
              throw new Error('No refresh token available');
            }

            const response = await axios.post(
              `${this.client.defaults.baseURL}/api/v1/auth/refresh`,
              { refreshToken },
              { skipAuth: true } as any
            );

            if (response.data.success) {
              const { token, refreshToken: newRefreshToken } = response.data.data;
              
              // Store new tokens
              await Promise.all([
                AsyncStorage.setItem('@exalt_courier_token', token),
                AsyncStorage.setItem('@exalt_courier_refresh_token', newRefreshToken),
              ]);

              // Update default headers
              this.client.defaults.headers.common['Authorization'] = `Bearer ${token}`;

              // Process failed queue
              this.processQueue(null, token);

              // Retry original request
              originalRequest.headers.Authorization = `Bearer ${token}`;
              return this.client(originalRequest);
            }
          } catch (refreshError) {
            // Refresh failed, clear tokens and redirect to login
            this.processQueue(refreshError, null);
            await this.clearAuthData();
            this.handleAuthFailure();
          } finally {
            this.refreshing = false;
          }
        }

        // Handle other errors
        this.handleApiError(error);
        return Promise.reject(error);
      }
    );
  }

  private processQueue(error: any, token: string | null) {
    this.failedQueue.forEach(({ resolve, reject }) => {
      if (error) {
        reject(error);
      } else if (token) {
        resolve(token);
      }
    });

    this.failedQueue = [];
  }

  private async clearAuthData() {
    await Promise.all([
      AsyncStorage.removeItem('@exalt_courier_token'),
      AsyncStorage.removeItem('@exalt_courier_refresh_token'),
      AsyncStorage.removeItem('@exalt_courier_user'),
    ]);

    delete this.client.defaults.headers.common['Authorization'];
  }

  private handleAuthFailure() {
    Alert.alert(
      'Session Expired',
      'Your session has expired. Please log in again.',
      [
        {
          text: 'OK',
          onPress: () => {
            // Navigate to login screen
            // NavigationService.reset('Login');
          },
        },
      ]
    );
  }

  private handleApiError(error: any) {
    const status = error.response?.status;
    const message = error.response?.data?.message || error.message;

    switch (status) {
      case 400:
        console.error('Bad Request:', message);
        break;
      case 403:
        console.error('Forbidden:', message);
        Alert.alert('Access Denied', 'You do not have permission to perform this action.');
        break;
      case 404:
        console.error('Not Found:', message);
        break;
      case 422:
        console.error('Validation Error:', error.response.data);
        break;
      case 429:
        console.error('Rate Limited:', message);
        Alert.alert('Too Many Requests', 'Please wait a moment before trying again.');
        break;
      case 500:
        console.error('Server Error:', message);
        Alert.alert('Server Error', 'Something went wrong on our end. Please try again later.');
        break;
      case 503:
        console.error('Service Unavailable:', message);
        Alert.alert('Service Unavailable', 'The service is temporarily unavailable. Please try again later.');
        break;
      default:
        if (!error.response) {
          console.error('Network Error:', message);
          Alert.alert('Network Error', 'Please check your internet connection and try again.');
        }
    }
  }

  private generateRequestId(): string {
    return `req_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }

  // HTTP Methods
  async get<T = any>(url: string, config?: ApiConfig): Promise<AxiosResponse<T>> {
    return this.client.get(url, config);
  }

  async post<T = any>(url: string, data?: any, config?: ApiConfig): Promise<AxiosResponse<T>> {
    return this.client.post(url, data, config);
  }

  async put<T = any>(url: string, data?: any, config?: ApiConfig): Promise<AxiosResponse<T>> {
    return this.client.put(url, data, config);
  }

  async patch<T = any>(url: string, data?: any, config?: ApiConfig): Promise<AxiosResponse<T>> {
    return this.client.patch(url, data, config);
  }

  async delete<T = any>(url: string, config?: ApiConfig): Promise<AxiosResponse<T>> {
    return this.client.delete(url, config);
  }

  // File upload helper
  async uploadFile<T = any>(
    url: string,
    file: any,
    onUploadProgress?: (progressEvent: any) => void
  ): Promise<AxiosResponse<T>> {
    const formData = new FormData();
    formData.append('file', file);

    return this.client.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      onUploadProgress,
    });
  }

  // Download file helper
  async downloadFile(url: string, filename?: string): Promise<void> {
    try {
      const response = await this.client.get(url, {
        responseType: 'blob',
      });

      // Handle file download based on platform
      // This would need platform-specific implementation
      console.log('File downloaded:', response.data);
    } catch (error) {
      console.error('Download failed:', error);
      throw error;
    }
  }

  // Cancel request helper
  cancelRequest(requestId?: string) {
    // Implementation for canceling requests
    console.log('Canceling request:', requestId);
  }

  // Health check
  async healthCheck(): Promise<boolean> {
    try {
      const response = await this.client.get('/health', { skipAuth: true });
      return response.status === 200;
    } catch (error) {
      console.error('Health check failed:', error);
      return false;
    }
  }

  // Get base URL
  getBaseURL(): string {
    return this.client.defaults.baseURL || '';
  }

  // Update base URL
  updateBaseURL(baseURL: string) {
    this.client.defaults.baseURL = baseURL;
  }

  // Get current auth token
  async getCurrentToken(): Promise<string | null> {
    return AsyncStorage.getItem('@exalt_courier_token');
  }

  // Set auth token manually
  async setAuthToken(token: string) {
    await AsyncStorage.setItem('@exalt_courier_token', token);
    this.client.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  }

  // Clear auth token
  async clearAuthToken() {
    await AsyncStorage.removeItem('@exalt_courier_token');
    delete this.client.defaults.headers.common['Authorization'];
  }
}

// Export singleton instance
export const apiClient = new ApiService();
export { ApiConfig };