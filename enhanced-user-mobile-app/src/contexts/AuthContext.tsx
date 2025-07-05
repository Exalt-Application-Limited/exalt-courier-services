import React, { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { authService } from '../services/auth.service';
import { ApiResponse, User, LoginRequest, RegisterRequest } from '../types';

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: LoginRequest) => Promise<ApiResponse<{ user: User; token: string }>>;
  register: (userData: RegisterRequest) => Promise<ApiResponse<{ user: User; token: string }>>;
  logout: () => Promise<void>;
  refreshToken: () => Promise<void>;
  updateProfile: (userData: Partial<User>) => Promise<ApiResponse<User>>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

const TOKEN_KEY = '@exalt_courier_token';
const USER_KEY = '@exalt_courier_user';

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const isAuthenticated = !!user;

  useEffect(() => {
    loadStoredAuth();
  }, []);

  const loadStoredAuth = async () => {
    try {
      const [token, storedUser] = await Promise.all([
        AsyncStorage.getItem(TOKEN_KEY),
        AsyncStorage.getItem(USER_KEY),
      ]);

      if (token && storedUser) {
        const userData = JSON.parse(storedUser);
        setUser(userData);
        
        // Verify token is still valid
        try {
          await authService.verifyToken();
        } catch (error) {
          // Token is invalid, clear stored data
          await clearStoredAuth();
        }
      }
    } catch (error) {
      console.error('Error loading stored auth:', error);
      await clearStoredAuth();
    } finally {
      setIsLoading(false);
    }
  };

  const clearStoredAuth = async () => {
    await Promise.all([
      AsyncStorage.removeItem(TOKEN_KEY),
      AsyncStorage.removeItem(USER_KEY),
    ]);
    setUser(null);
  };

  const login = async (credentials: LoginRequest): Promise<ApiResponse<{ user: User; token: string }>> => {
    try {
      const response = await authService.login(credentials);
      
      if (response.success && response.data) {
        const { user: userData, token } = response.data;
        
        // Store token and user data
        await Promise.all([
          AsyncStorage.setItem(TOKEN_KEY, token),
          AsyncStorage.setItem(USER_KEY, JSON.stringify(userData)),
        ]);
        
        setUser(userData);
      }
      
      return response;
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  };

  const register = async (userData: RegisterRequest): Promise<ApiResponse<{ user: User; token: string }>> => {
    try {
      const response = await authService.register(userData);
      
      if (response.success && response.data) {
        const { user: newUser, token } = response.data;
        
        // Store token and user data
        await Promise.all([
          AsyncStorage.setItem(TOKEN_KEY, token),
          AsyncStorage.setItem(USER_KEY, JSON.stringify(newUser)),
        ]);
        
        setUser(newUser);
      }
      
      return response;
    } catch (error) {
      console.error('Registration error:', error);
      throw error;
    }
  };

  const logout = async () => {
    try {
      await authService.logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      await clearStoredAuth();
    }
  };

  const refreshToken = async () => {
    try {
      const response = await authService.refreshToken();
      
      if (response.success && response.data) {
        const { token, user: userData } = response.data;
        
        // Update stored token and user data
        await Promise.all([
          AsyncStorage.setItem(TOKEN_KEY, token),
          AsyncStorage.setItem(USER_KEY, JSON.stringify(userData)),
        ]);
        
        setUser(userData);
      }
    } catch (error) {
      console.error('Token refresh error:', error);
      await clearStoredAuth();
    }
  };

  const updateProfile = async (userData: Partial<User>): Promise<ApiResponse<User>> => {
    try {
      const response = await authService.updateProfile(userData);
      
      if (response.success && response.data) {
        const updatedUser = response.data;
        
        // Update stored user data
        await AsyncStorage.setItem(USER_KEY, JSON.stringify(updatedUser));
        setUser(updatedUser);
      }
      
      return response;
    } catch (error) {
      console.error('Profile update error:', error);
      throw error;
    }
  };

  const value: AuthContextType = {
    user,
    isAuthenticated,
    isLoading,
    login,
    register,
    logout,
    refreshToken,
    updateProfile,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};