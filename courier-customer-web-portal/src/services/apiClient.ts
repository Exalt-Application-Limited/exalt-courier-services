import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios';

export interface ApiResponse<T = any> {
  success: boolean;
  data?: T;
  message?: string;
  error?: string;
  errors?: Record<string, string[]>;
}

export interface PaginatedResponse<T> {
  success: boolean;
  data: T[];
  pagination: {
    page: number;
    limit: number;
    total: number;
    totalPages: number;
    hasNext: boolean;
    hasPrev: boolean;
  };
  message?: string;
}

class ApiClient {
  private client: AxiosInstance;
  private baseURL: string;

  constructor() {
    this.baseURL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';
    
    this.client = axios.create({
      baseURL: this.baseURL,
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
      (config: AxiosRequestConfig) => {
        const token = localStorage.getItem('exalt_auth_token');
        if (token && config.headers) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        
        // Add request ID for tracing
        config.headers = {
          ...config.headers,
          'X-Request-ID': this.generateRequestId(),
        };

        console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url}`, {
          headers: config.headers,
          data: config.data,
        });

        return config;
      },
      (error: AxiosError) => {
        console.error('[API Request Error]', error);
        return Promise.reject(error);
      }
    );

    // Response interceptor for error handling
    this.client.interceptors.response.use(
      (response: AxiosResponse) => {
        console.log(`[API Response] ${response.status} ${response.config.url}`, {
          data: response.data,
          headers: response.headers,
        });
        return response;
      },
      async (error: AxiosError) => {
        console.error('[API Response Error]', {
          status: error.response?.status,
          data: error.response?.data,
          url: error.config?.url,
        });

        // Handle token expiration
        if (error.response?.status === 401) {
          const refreshToken = localStorage.getItem('exalt_refresh_token');
          if (refreshToken) {
            try {
              const refreshResponse = await this.refreshAuthToken(refreshToken);
              if (refreshResponse.success && error.config) {
                // Retry the original request with new token
                const token = localStorage.getItem('exalt_auth_token');
                if (token && error.config.headers) {
                  error.config.headers.Authorization = `Bearer ${token}`;
                }
                return this.client.request(error.config);
              }
            } catch (refreshError) {
              // Refresh failed, redirect to login
              this.handleAuthFailure();
            }
          } else {
            this.handleAuthFailure();
          }
        }

        // Handle network errors
        if (!error.response) {
          return Promise.reject({
            success: false,
            error: 'Network error. Please check your connection and try again.',
          });
        }

        // Transform error response
        const apiError: ApiResponse = {
          success: false,
          error: error.response.data?.message || error.response.data?.error || 'An unexpected error occurred',
          errors: error.response.data?.errors,
        };

        return Promise.reject(apiError);
      }
    );
  }

  private generateRequestId(): string {
    return `req_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }

  private async refreshAuthToken(refreshToken: string): Promise<ApiResponse> {
    try {
      const response = await axios.post(`${this.baseURL}/auth/refresh`, {
        refreshToken,
      });

      if (response.data.success) {
        localStorage.setItem('exalt_auth_token', response.data.data.accessToken);
        localStorage.setItem('exalt_refresh_token', response.data.data.refreshToken);
        return { success: true, data: response.data.data };
      }
      
      return { success: false, error: 'Token refresh failed' };
    } catch (error) {
      return { success: false, error: 'Token refresh failed' };
    }
  }

  private handleAuthFailure() {
    localStorage.removeItem('exalt_auth_token');
    localStorage.removeItem('exalt_refresh_token');
    
    // Redirect to login page
    if (window.location.pathname !== '/login') {
      window.location.href = '/login';
    }
  }

  // Generic HTTP methods
  async get<T = any>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    try {
      const response = await this.client.get<ApiResponse<T>>(url, config);
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    try {
      const response = await this.client.post<ApiResponse<T>>(url, data, config);
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    try {
      const response = await this.client.put<ApiResponse<T>>(url, data, config);
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async patch<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    try {
      const response = await this.client.patch<ApiResponse<T>>(url, data, config);
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    try {
      const response = await this.client.delete<ApiResponse<T>>(url, config);
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  // Paginated request
  async getPaginated<T = any>(
    url: string, 
    params?: Record<string, any>, 
    config?: AxiosRequestConfig
  ): Promise<PaginatedResponse<T>> {
    try {
      const response = await this.client.get<PaginatedResponse<T>>(url, {
        ...config,
        params: {
          page: 1,
          limit: 10,
          ...params,
        },
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  // File upload
  async uploadFile<T = any>(
    url: string, 
    file: File, 
    additionalData?: Record<string, any>,
    onUploadProgress?: (progressEvent: any) => void
  ): Promise<ApiResponse<T>> {
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      if (additionalData) {
        Object.keys(additionalData).forEach(key => {
          formData.append(key, additionalData[key]);
        });
      }

      const response = await this.client.post<ApiResponse<T>>(url, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        onUploadProgress,
      });
      
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  // Download file
  async downloadFile(url: string, filename?: string): Promise<void> {
    try {
      const response = await this.client.get(url, {
        responseType: 'blob',
      });

      const blob = new Blob([response.data]);
      const downloadUrl = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = downloadUrl;
      link.download = filename || 'download';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(downloadUrl);
    } catch (error) {
      throw error;
    }
  }

  // Health check
  async healthCheck(): Promise<boolean> {
    try {
      const response = await this.client.get('/health');
      return response.status === 200;
    } catch (error) {
      return false;
    }
  }

  // Get base URL
  getBaseURL(): string {
    return this.baseURL;
  }

  // Update base URL
  setBaseURL(url: string): void {
    this.baseURL = url;
    this.client.defaults.baseURL = url;
  }
}

// Create and export a singleton instance
const apiClient = new ApiClient();
export default apiClient;