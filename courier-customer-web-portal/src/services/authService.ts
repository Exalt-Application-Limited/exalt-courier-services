import apiClient, { ApiResponse } from './apiClient';
import { User, RegisterData } from '../contexts/AuthContext';

export interface LoginRequest {
  email: string;
  password: string;
  rememberMe?: boolean;
}

export interface LoginResponse {
  user: User;
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  password: string;
  confirmPassword: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface ProfileUpdateRequest {
  firstName?: string;
  lastName?: string;
  phone?: string;
  companyName?: string;
  avatar?: string;
}

class AuthService {
  private readonly basePath = '/auth';

  // Authentication methods
  async login(credentials: LoginRequest): Promise<ApiResponse<LoginResponse>> {
    return apiClient.post(`${this.basePath}/login`, credentials);
  }

  async register(userData: RegisterData): Promise<ApiResponse<LoginResponse>> {
    return apiClient.post(`${this.basePath}/register`, userData);
  }

  async logout(): Promise<ApiResponse> {
    const refreshToken = localStorage.getItem('exalt_refresh_token');
    return apiClient.post(`${this.basePath}/logout`, { refreshToken });
  }

  async forgotPassword(request: ForgotPasswordRequest): Promise<ApiResponse> {
    return apiClient.post(`${this.basePath}/forgot-password`, request);
  }

  async resetPassword(request: ResetPasswordRequest): Promise<ApiResponse> {
    return apiClient.post(`${this.basePath}/reset-password`, request);
  }

  async changePassword(request: ChangePasswordRequest): Promise<ApiResponse> {
    return apiClient.post(`${this.basePath}/change-password`, request);
  }

  async refreshToken(refreshToken: string): Promise<ApiResponse<LoginResponse>> {
    return apiClient.post(`${this.basePath}/refresh`, { refreshToken });
  }

  // Profile management
  async getProfile(): Promise<ApiResponse<User>> {
    return apiClient.get(`${this.basePath}/profile`);
  }

  async updateProfile(request: ProfileUpdateRequest): Promise<ApiResponse<User>> {
    return apiClient.put(`${this.basePath}/profile`, request);
  }

  async uploadAvatar(file: File): Promise<ApiResponse<{ avatarUrl: string }>> {
    return apiClient.uploadFile(`${this.basePath}/profile/avatar`, file);
  }

  async deleteAvatar(): Promise<ApiResponse> {
    return apiClient.delete(`${this.basePath}/profile/avatar`);
  }

  // Account verification
  async sendVerificationEmail(): Promise<ApiResponse> {
    return apiClient.post(`${this.basePath}/verify/send`);
  }

  async verifyEmail(token: string): Promise<ApiResponse> {
    return apiClient.post(`${this.basePath}/verify/email`, { token });
  }

  async verifyPhone(code: string): Promise<ApiResponse> {
    return apiClient.post(`${this.basePath}/verify/phone`, { code });
  }

  async sendPhoneVerificationCode(): Promise<ApiResponse> {
    return apiClient.post(`${this.basePath}/verify/phone/send`);
  }

  // Account management
  async deactivateAccount(password: string): Promise<ApiResponse> {
    return apiClient.post(`${this.basePath}/account/deactivate`, { password });
  }

  async deleteAccount(password: string): Promise<ApiResponse> {
    return apiClient.delete(`${this.basePath}/account`, {
      data: { password }
    });
  }

  // Corporate account management
  async upgradeToCorporate(companyInfo: {
    companyName: string;
    businessNumber: string;
    industry: string;
    estimatedMonthlyVolume: number;
  }): Promise<ApiResponse> {
    return apiClient.post(`${this.basePath}/upgrade/corporate`, companyInfo);
  }

  async getCorporateInfo(): Promise<ApiResponse<{
    companyName: string;
    businessNumber: string;
    industry: string;
    accountManager?: string;
    discountTier: string;
    monthlyVolume: number;
  }>> {
    return apiClient.get(`${this.basePath}/corporate/info`);
  }

  // Session management
  async validateToken(): Promise<ApiResponse<User>> {
    return apiClient.get(`${this.basePath}/validate`);
  }

  async getSessions(): Promise<ApiResponse<Array<{
    id: string;
    device: string;
    browser: string;
    location: string;
    lastActive: string;
    current: boolean;
  }>>> {
    return apiClient.get(`${this.basePath}/sessions`);
  }

  async terminateSession(sessionId: string): Promise<ApiResponse> {
    return apiClient.delete(`${this.basePath}/sessions/${sessionId}`);
  }

  async terminateAllSessions(): Promise<ApiResponse> {
    return apiClient.delete(`${this.basePath}/sessions/all`);
  }

  // Two-factor authentication
  async enableTwoFactor(): Promise<ApiResponse<{
    qrCode: string;
    secret: string;
    backupCodes: string[];
  }>> {
    return apiClient.post(`${this.basePath}/2fa/enable`);
  }

  async confirmTwoFactor(code: string): Promise<ApiResponse> {
    return apiClient.post(`${this.basePath}/2fa/confirm`, { code });
  }

  async disableTwoFactor(password: string): Promise<ApiResponse> {
    return apiClient.post(`${this.basePath}/2fa/disable`, { password });
  }

  async verifyTwoFactor(code: string): Promise<ApiResponse> {
    return apiClient.post(`${this.basePath}/2fa/verify`, { code });
  }

  async regenerateBackupCodes(): Promise<ApiResponse<{ backupCodes: string[] }>> {
    return apiClient.post(`${this.basePath}/2fa/backup-codes/regenerate`);
  }

  // Privacy and security
  async getLoginHistory(): Promise<ApiResponse<Array<{
    id: string;
    timestamp: string;
    ipAddress: string;
    userAgent: string;
    location: string;
    success: boolean;
  }>>> {
    return apiClient.get(`${this.basePath}/login-history`);
  }

  async downloadPersonalData(): Promise<void> {
    return apiClient.downloadFile(`${this.basePath}/data/export`, 'personal-data.json');
  }

  async getPrivacySettings(): Promise<ApiResponse<{
    dataProcessing: boolean;
    marketingEmails: boolean;
    analyticsTracking: boolean;
    thirdPartySharing: boolean;
  }>> {
    return apiClient.get(`${this.basePath}/privacy/settings`);
  }

  async updatePrivacySettings(settings: {
    dataProcessing?: boolean;
    marketingEmails?: boolean;
    analyticsTracking?: boolean;
    thirdPartySharing?: boolean;
  }): Promise<ApiResponse> {
    return apiClient.put(`${this.basePath}/privacy/settings`, settings);
  }
}

// Create and export singleton instance
const authService = new AuthService();
export default authService;