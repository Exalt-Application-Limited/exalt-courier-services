import React from 'react';
import { render, screen, act, waitFor } from '@testing-library/react';
import { AuthProvider, useAuth } from '../../contexts/AuthContext';

// Mock localStorage
const localStorageMock = {
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
  clear: jest.fn(),
};
Object.defineProperty(window, 'localStorage', {
  value: localStorageMock
});

// Test component that uses the auth context
const TestComponent: React.FC = () => {
  const { user, login, logout, isAuthenticated, loading } = useAuth();

  return (
    <div>
      <div data-testid="loading">{loading ? 'Loading' : 'Not Loading'}</div>
      <div data-testid="authenticated">{isAuthenticated ? 'Authenticated' : 'Not Authenticated'}</div>
      <div data-testid="user">{user ? user.name : 'No User'}</div>
      <button onClick={() => login('test@example.com', 'password')} data-testid="login-btn">
        Login
      </button>
      <button onClick={logout} data-testid="logout-btn">
        Logout
      </button>
    </div>
  );
};

describe('AuthContext', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorageMock.getItem.mockReturnValue(null);
  });

  it('should provide initial unauthenticated state', () => {
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    expect(screen.getByTestId('loading')).toHaveTextContent('Not Loading');
    expect(screen.getByTestId('authenticated')).toHaveTextContent('Not Authenticated');
    expect(screen.getByTestId('user')).toHaveTextContent('No User');
  });

  it('should restore authentication state from localStorage', () => {
    const mockUser = { id: '1', name: 'Test User', email: 'test@example.com' };
    const mockToken = 'mock-jwt-token';
    
    localStorageMock.getItem.mockImplementation((key) => {
      if (key === 'user') return JSON.stringify(mockUser);
      if (key === 'token') return mockToken;
      return null;
    });

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    expect(screen.getByTestId('authenticated')).toHaveTextContent('Authenticated');
    expect(screen.getByTestId('user')).toHaveTextContent('Test User');
  });

  it('should handle login successfully', async () => {
    // Mock successful API response
    global.fetch = jest.fn().mockResolvedValue({
      ok: true,
      json: jest.fn().mockResolvedValue({
        user: { id: '1', name: 'Test User', email: 'test@example.com' },
        token: 'new-jwt-token'
      })
    }) as jest.MockedFunction<typeof fetch>;

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    const loginButton = screen.getByTestId('login-btn');
    
    await act(async () => {
      loginButton.click();
    });

    await waitFor(() => {
      expect(screen.getByTestId('authenticated')).toHaveTextContent('Authenticated');
      expect(screen.getByTestId('user')).toHaveTextContent('Test User');
    });

    expect(localStorageMock.setItem).toHaveBeenCalledWith('token', 'new-jwt-token');
    expect(localStorageMock.setItem).toHaveBeenCalledWith('user', JSON.stringify({
      id: '1',
      name: 'Test User',
      email: 'test@example.com'
    }));
  });

  it('should handle login failure', async () => {
    // Mock failed API response
    global.fetch = jest.fn().mockResolvedValue({
      ok: false,
      json: jest.fn().mockResolvedValue({
        message: 'Invalid credentials'
      })
    }) as jest.MockedFunction<typeof fetch>;

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    const loginButton = screen.getByTestId('login-btn');
    
    await act(async () => {
      loginButton.click();
    });

    await waitFor(() => {
      expect(screen.getByTestId('authenticated')).toHaveTextContent('Not Authenticated');
      expect(screen.getByTestId('user')).toHaveTextContent('No User');
    });

    expect(localStorageMock.setItem).not.toHaveBeenCalled();
  });

  it('should handle logout', async () => {
    // Set up authenticated state first
    const mockUser = { id: '1', name: 'Test User', email: 'test@example.com' };
    localStorageMock.getItem.mockImplementation((key) => {
      if (key === 'user') return JSON.stringify(mockUser);
      if (key === 'token') return 'mock-token';
      return null;
    });

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    // Verify initially authenticated
    expect(screen.getByTestId('authenticated')).toHaveTextContent('Authenticated');

    const logoutButton = screen.getByTestId('logout-btn');
    
    await act(async () => {
      logoutButton.click();
    });

    expect(screen.getByTestId('authenticated')).toHaveTextContent('Not Authenticated');
    expect(screen.getByTestId('user')).toHaveTextContent('No User');
    expect(localStorageMock.removeItem).toHaveBeenCalledWith('token');
    expect(localStorageMock.removeItem).toHaveBeenCalledWith('user');
  });

  it('should handle network errors during login', async () => {
    // Mock network error
    global.fetch = jest.fn().mockRejectedValue(new Error('Network error'));

    const consoleSpy = jest.spyOn(console, 'error').mockImplementation();

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    const loginButton = screen.getByTestId('login-btn');
    
    await act(async () => {
      loginButton.click();
    });

    await waitFor(() => {
      expect(screen.getByTestId('authenticated')).toHaveTextContent('Not Authenticated');
    });

    expect(consoleSpy).toHaveBeenCalledWith('Login error:', expect.any(Error));
    
    consoleSpy.mockRestore();
  });

  it('should handle malformed localStorage data', () => {
    localStorageMock.getItem.mockImplementation((key) => {
      if (key === 'user') return 'invalid-json';
      if (key === 'token') return 'valid-token';
      return null;
    });

    const consoleSpy = jest.spyOn(console, 'error').mockImplementation();

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    expect(screen.getByTestId('authenticated')).toHaveTextContent('Not Authenticated');
    expect(consoleSpy).toHaveBeenCalled();
    
    consoleSpy.mockRestore();
  });
});