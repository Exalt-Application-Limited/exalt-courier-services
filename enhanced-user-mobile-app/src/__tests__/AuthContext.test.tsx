import React from 'react';
import { render } from '@testing-library/react-native';
import { AuthProvider, useAuth } from '../contexts/AuthContext';
import AsyncStorage from '@react-native-async-storage/async-storage';

// Mock AsyncStorage
jest.mock('@react-native-async-storage/async-storage', () => ({
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
  clear: jest.fn(),
}));

// Mock fetch
global.fetch = jest.fn();

// Test component that uses the auth context
const TestComponent: React.FC = () => {
  const { user, login, logout, isAuthenticated, loading } = useAuth();

  return (
    <>
      <text testID="loading">{loading ? 'Loading' : 'Not Loading'}</text>
      <text testID="authenticated">{isAuthenticated ? 'Authenticated' : 'Not Authenticated'}</text>
      <text testID="user">{user ? user.name : 'No User'}</text>
    </>
  );
};

describe('AuthContext (Mobile)', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (AsyncStorage.getItem as jest.Mock).mockResolvedValue(null);
  });

  it('should provide initial unauthenticated state', () => {
    const { getByTestId } = render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    expect(getByTestId('loading').children[0]).toBe('Not Loading');
    expect(getByTestId('authenticated').children[0]).toBe('Not Authenticated');
    expect(getByTestId('user').children[0]).toBe('No User');
  });

  it('should restore authentication state from AsyncStorage', async () => {
    const mockUser = { id: '1', name: 'Test User', email: 'test@example.com' };
    const mockToken = 'mock-jwt-token';
    
    (AsyncStorage.getItem as jest.Mock).mockImplementation((key) => {
      if (key === 'user') return Promise.resolve(JSON.stringify(mockUser));
      if (key === 'token') return Promise.resolve(mockToken);
      return Promise.resolve(null);
    });

    const { getByTestId } = render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    // Wait for async storage to be checked
    await new Promise(resolve => setTimeout(resolve, 100));

    expect(getByTestId('authenticated').children[0]).toBe('Authenticated');
    expect(getByTestId('user').children[0]).toBe('Test User');
  });

  it('should handle successful login', async () => {
    const mockUser = { id: '1', name: 'Test User', email: 'test@example.com' };
    const mockToken = 'new-jwt-token';

    (global.fetch as jest.Mock).mockResolvedValue({
      ok: true,
      json: jest.fn().mockResolvedValue({
        user: mockUser,
        token: mockToken
      })
    });

    const LoginTestComponent: React.FC = () => {
      const { user, login, isAuthenticated } = useAuth();

      const handleLogin = () => {
        login('test@example.com', 'password');
      };

      return (
        <>
          <text testID="authenticated">{isAuthenticated ? 'Authenticated' : 'Not Authenticated'}</text>
          <text testID="user">{user ? user.name : 'No User'}</text>
          <text testID="login-trigger" onPress={handleLogin}>Login</text>
        </>
      );
    };

    const { getByTestId } = render(
      <AuthProvider>
        <LoginTestComponent />
      </AuthProvider>
    );

    // Trigger login
    const loginTrigger = getByTestId('login-trigger');
    loginTrigger.props.onPress();

    // Wait for login to complete
    await new Promise(resolve => setTimeout(resolve, 100));

    expect(AsyncStorage.setItem).toHaveBeenCalledWith('token', mockToken);
    expect(AsyncStorage.setItem).toHaveBeenCalledWith('user', JSON.stringify(mockUser));
  });

  it('should handle logout', async () => {
    // Set up authenticated state first
    const mockUser = { id: '1', name: 'Test User', email: 'test@example.com' };
    (AsyncStorage.getItem as jest.Mock).mockImplementation((key) => {
      if (key === 'user') return Promise.resolve(JSON.stringify(mockUser));
      if (key === 'token') return Promise.resolve('mock-token');
      return Promise.resolve(null);
    });

    const LogoutTestComponent: React.FC = () => {
      const { user, logout, isAuthenticated } = useAuth();

      const handleLogout = () => {
        logout();
      };

      return (
        <>
          <text testID="authenticated">{isAuthenticated ? 'Authenticated' : 'Not Authenticated'}</text>
          <text testID="user">{user ? user.name : 'No User'}</text>
          <text testID="logout-trigger" onPress={handleLogout}>Logout</text>
        </>
      );
    };

    const { getByTestId } = render(
      <AuthProvider>
        <LogoutTestComponent />
      </AuthProvider>
    );

    // Wait for initial state to load
    await new Promise(resolve => setTimeout(resolve, 100));

    // Verify initially authenticated
    expect(getByTestId('authenticated').children[0]).toBe('Authenticated');

    // Trigger logout
    const logoutTrigger = getByTestId('logout-trigger');
    logoutTrigger.props.onPress();

    // Wait for logout to complete
    await new Promise(resolve => setTimeout(resolve, 100));

    expect(AsyncStorage.removeItem).toHaveBeenCalledWith('token');
    expect(AsyncStorage.removeItem).toHaveBeenCalledWith('user');
    expect(getByTestId('authenticated').children[0]).toBe('Not Authenticated');
    expect(getByTestId('user').children[0]).toBe('No User');
  });

  it('should handle login failure', async () => {
    (global.fetch as jest.Mock).mockResolvedValue({
      ok: false,
      json: jest.fn().mockResolvedValue({
        message: 'Invalid credentials'
      })
    });

    const FailedLoginTestComponent: React.FC = () => {
      const { login, isAuthenticated, error } = useAuth();

      const handleLogin = () => {
        login('test@example.com', 'wrongpassword');
      };

      return (
        <>
          <text testID="authenticated">{isAuthenticated ? 'Authenticated' : 'Not Authenticated'}</text>
          <text testID="error">{error || 'No Error'}</text>
          <text testID="login-trigger" onPress={handleLogin}>Login</text>
        </>
      );
    };

    const { getByTestId } = render(
      <AuthProvider>
        <FailedLoginTestComponent />
      </AuthProvider>
    );

    // Trigger failed login
    const loginTrigger = getByTestId('login-trigger');
    loginTrigger.props.onPress();

    // Wait for login attempt to complete
    await new Promise(resolve => setTimeout(resolve, 100));

    expect(getByTestId('authenticated').children[0]).toBe('Not Authenticated');
    expect(AsyncStorage.setItem).not.toHaveBeenCalled();
  });

  it('should handle network errors', async () => {
    (global.fetch as jest.Mock).mockRejectedValue(new Error('Network error'));

    const NetworkErrorTestComponent: React.FC = () => {
      const { login, isAuthenticated, error } = useAuth();

      const handleLogin = () => {
        login('test@example.com', 'password');
      };

      return (
        <>
          <text testID="authenticated">{isAuthenticated ? 'Authenticated' : 'Not Authenticated'}</text>
          <text testID="error">{error || 'No Error'}</text>
          <text testID="login-trigger" onPress={handleLogin}>Login</text>
        </>
      );
    };

    const { getByTestId } = render(
      <AuthProvider>
        <NetworkErrorTestComponent />
      </AuthProvider>
    );

    // Trigger login with network error
    const loginTrigger = getByTestId('login-trigger');
    loginTrigger.props.onPress();

    // Wait for error to be set
    await new Promise(resolve => setTimeout(resolve, 100));

    expect(getByTestId('authenticated').children[0]).toBe('Not Authenticated');
    expect(getByTestId('error').children[0]).toBe('Network error');
  });

  it('should handle malformed AsyncStorage data', async () => {
    (AsyncStorage.getItem as jest.Mock).mockImplementation((key) => {
      if (key === 'user') return Promise.resolve('invalid-json');
      if (key === 'token') return Promise.resolve('valid-token');
      return Promise.resolve(null);
    });

    const { getByTestId } = render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    // Wait for async storage to be checked
    await new Promise(resolve => setTimeout(resolve, 100));

    // Should not be authenticated with malformed data
    expect(getByTestId('authenticated').children[0]).toBe('Not Authenticated');
  });

  it('should update user profile', async () => {
    const mockUser = { id: '1', name: 'Test User', email: 'test@example.com' };
    const updatedUser = { id: '1', name: 'Updated User', email: 'updated@example.com' };

    (AsyncStorage.getItem as jest.Mock).mockImplementation((key) => {
      if (key === 'user') return Promise.resolve(JSON.stringify(mockUser));
      if (key === 'token') return Promise.resolve('token');
      return Promise.resolve(null);
    });

    const UpdateProfileTestComponent: React.FC = () => {
      const { user, updateProfile } = useAuth();

      const handleUpdate = () => {
        updateProfile(updatedUser);
      };

      return (
        <>
          <text testID="user">{user ? user.name : 'No User'}</text>
          <text testID="update-trigger" onPress={handleUpdate}>Update</text>
        </>
      );
    };

    const { getByTestId } = render(
      <AuthProvider>
        <UpdateProfileTestComponent />
      </AuthProvider>
    );

    // Wait for initial state
    await new Promise(resolve => setTimeout(resolve, 100));
    expect(getByTestId('user').children[0]).toBe('Test User');

    // Trigger profile update
    const updateTrigger = getByTestId('update-trigger');
    updateTrigger.props.onPress();

    // Wait for update to complete
    await new Promise(resolve => setTimeout(resolve, 100));

    expect(getByTestId('user').children[0]).toBe('Updated User');
    expect(AsyncStorage.setItem).toHaveBeenCalledWith('user', JSON.stringify(updatedUser));
  });

  it('should check token validity on app resume', async () => {
    const mockUser = { id: '1', name: 'Test User', email: 'test@example.com' };
    
    (AsyncStorage.getItem as jest.Mock).mockImplementation((key) => {
      if (key === 'user') return Promise.resolve(JSON.stringify(mockUser));
      if (key === 'token') return Promise.resolve('expired-token');
      return Promise.resolve(null);
    });

    // Mock token validation endpoint
    (global.fetch as jest.Mock).mockResolvedValue({
      ok: false,
      status: 401,
      json: jest.fn().mockResolvedValue({ message: 'Token expired' })
    });

    const { getByTestId } = render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    // Wait for token validation
    await new Promise(resolve => setTimeout(resolve, 200));

    // Should be logged out due to expired token
    expect(getByTestId('authenticated').children[0]).toBe('Not Authenticated');
    expect(AsyncStorage.removeItem).toHaveBeenCalledWith('token');
    expect(AsyncStorage.removeItem).toHaveBeenCalledWith('user');
  });
});