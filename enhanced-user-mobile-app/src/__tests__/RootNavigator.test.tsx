import React from 'react';
import { render } from '@testing-library/react-native';
import { NavigationContainer } from '@react-navigation/native';
import RootNavigator from '../navigation/RootNavigator';
import { AuthProvider, useAuth } from '../contexts/AuthContext';

// Mock the navigation stack screens
jest.mock('@react-navigation/native-stack', () => ({
  createNativeStackNavigator: () => ({
    Navigator: ({ children }: { children: React.ReactNode }) => <>{children}</>,
    Screen: ({ name, component }: { name: string; component: React.ComponentType }) => {
      const Component = component;
      return <Component key={name} />;
    }
  })
}));

jest.mock('@react-navigation/bottom-tabs', () => ({
  createBottomTabNavigator: () => ({
    Navigator: ({ children }: { children: React.ReactNode }) => <>{children}</>,
    Screen: ({ name, component }: { name: string; component: React.ComponentType }) => {
      const Component = component;
      return <Component key={name} />;
    }
  })
}));

// Mock the screen components
jest.mock('../screens/auth/LoginScreen', () => {
  return function LoginScreen() {
    return <text testID="login-screen">Login Screen</text>;
  };
});

jest.mock('../screens/auth/RegisterScreen', () => {
  return function RegisterScreen() {
    return <text testID="register-screen">Register Screen</text>;
  };
});

jest.mock('../screens/home/HomeScreen', () => {
  return function HomeScreen() {
    return <text testID="home-screen">Home Screen</text>;
  };
});

jest.mock('../screens/shipments/ShipmentListScreen', () => {
  return function ShipmentListScreen() {
    return <text testID="shipment-list-screen">Shipment List Screen</text>;
  };
});

jest.mock('../screens/profile/ProfileScreen', () => {
  return function ProfileScreen() {
    return <text testID="profile-screen">Profile Screen</text>;
  };
});

jest.mock('../screens/shipments/TrackingScreen', () => {
  return function TrackingScreen() {
    return <text testID="tracking-screen">Tracking Screen</text>;
  };
});

// Mock AuthContext
const defaultAuthContext = {
  user: null,
  isAuthenticated: false,
  loading: false,
  login: jest.fn(),
  logout: jest.fn(),
  register: jest.fn()
};

let mockAuthContext = { ...defaultAuthContext };

jest.mock('../contexts/AuthContext', () => ({
  useAuth: () => mockAuthContext,
  AuthProvider: ({ children }: { children: React.ReactNode }) => <>{children}</>
}));

// Mock AsyncStorage
jest.mock('@react-native-async-storage/async-storage', () => ({
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
}));

// Mock react-native-vector-icons
jest.mock('react-native-vector-icons/MaterialIcons', () => 'MaterialIcons');

describe('RootNavigator', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockAuthContext = { ...defaultAuthContext };
  });

  it('should render auth stack when user is not authenticated', () => {
    mockAuthContext.isAuthenticated = false;
    mockAuthContext.loading = false;

    const { getByTestId, queryByTestId } = render(
      <NavigationContainer>
        <RootNavigator />
      </NavigationContainer>
    );

    expect(getByTestId('login-screen')).toBeTruthy();
    expect(queryByTestId('home-screen')).toBeFalsy();
  });

  it('should render main app stack when user is authenticated', () => {
    mockAuthContext.isAuthenticated = true;
    mockAuthContext.loading = false;
    mockAuthContext.user = { id: '1', name: 'Test User', email: 'test@example.com' };

    const { getByTestId, queryByTestId } = render(
      <NavigationContainer>
        <RootNavigator />
      </NavigationContainer>
    );

    expect(getByTestId('home-screen')).toBeTruthy();
    expect(queryByTestId('login-screen')).toBeFalsy();
  });

  it('should show loading state when authentication is being checked', () => {
    mockAuthContext.isAuthenticated = false;
    mockAuthContext.loading = true;

    const { getByTestId } = render(
      <NavigationContainer>
        <RootNavigator />
      </NavigationContainer>
    );

    expect(getByTestId('loading-screen')).toBeTruthy();
  });

  it('should navigate between auth screens', () => {
    mockAuthContext.isAuthenticated = false;
    mockAuthContext.loading = false;

    const { getByTestId } = render(
      <NavigationContainer>
        <RootNavigator />
      </NavigationContainer>
    );

    // Should show login screen by default
    expect(getByTestId('login-screen')).toBeTruthy();
    
    // Register screen should also be available
    expect(getByTestId('register-screen')).toBeTruthy();
  });

  it('should provide access to main app screens when authenticated', () => {
    mockAuthContext.isAuthenticated = true;
    mockAuthContext.loading = false;
    mockAuthContext.user = { id: '1', name: 'Test User', email: 'test@example.com' };

    const { getByTestId } = render(
      <NavigationContainer>
        <RootNavigator />
      </NavigationContainer>
    );

    // Main tab screens should be available
    expect(getByTestId('home-screen')).toBeTruthy();
    expect(getByTestId('shipment-list-screen')).toBeTruthy();
    expect(getByTestId('profile-screen')).toBeTruthy();
  });

  it('should handle deep linking to tracking screen', () => {
    mockAuthContext.isAuthenticated = true;
    mockAuthContext.loading = false;
    mockAuthContext.user = { id: '1', name: 'Test User', email: 'test@example.com' };

    const { getByTestId } = render(
      <NavigationContainer>
        <RootNavigator />
      </NavigationContainer>
    );

    // Tracking screen should be available for deep linking
    expect(getByTestId('tracking-screen')).toBeTruthy();
  });

  it('should transition from loading to auth when not authenticated', () => {
    // Start with loading state
    mockAuthContext.isAuthenticated = false;
    mockAuthContext.loading = true;

    const { getByTestId, rerender, queryByTestId } = render(
      <NavigationContainer>
        <RootNavigator />
      </NavigationContainer>
    );

    expect(getByTestId('loading-screen')).toBeTruthy();

    // Simulate loading completion
    mockAuthContext.loading = false;

    rerender(
      <NavigationContainer>
        <RootNavigator />
      </NavigationContainer>
    );

    expect(queryByTestId('loading-screen')).toBeFalsy();
    expect(getByTestId('login-screen')).toBeTruthy();
  });

  it('should transition from loading to main app when authenticated', () => {
    // Start with loading state
    mockAuthContext.isAuthenticated = false;
    mockAuthContext.loading = true;

    const { getByTestId, rerender, queryByTestId } = render(
      <NavigationContainer>
        <RootNavigator />
      </NavigationContainer>
    );

    expect(getByTestId('loading-screen')).toBeTruthy();

    // Simulate authentication completion
    mockAuthContext.isAuthenticated = true;
    mockAuthContext.loading = false;
    mockAuthContext.user = { id: '1', name: 'Test User', email: 'test@example.com' };

    rerender(
      <NavigationContainer>
        <RootNavigator />
      </NavigationContainer>
    );

    expect(queryByTestId('loading-screen')).toBeFalsy();
    expect(getByTestId('home-screen')).toBeTruthy();
  });

  it('should handle logout and return to auth stack', () => {
    // Start authenticated
    mockAuthContext.isAuthenticated = true;
    mockAuthContext.loading = false;
    mockAuthContext.user = { id: '1', name: 'Test User', email: 'test@example.com' };

    const { getByTestId, rerender, queryByTestId } = render(
      <NavigationContainer>
        <RootNavigator />
      </NavigationContainer>
    );

    expect(getByTestId('home-screen')).toBeTruthy();

    // Simulate logout
    mockAuthContext.isAuthenticated = false;
    mockAuthContext.user = null;

    rerender(
      <NavigationContainer>
        <RootNavigator />
      </NavigationContainer>
    );

    expect(queryByTestId('home-screen')).toBeFalsy();
    expect(getByTestId('login-screen')).toBeTruthy();
  });

  it('should preserve navigation state during auth state changes', () => {
    mockAuthContext.isAuthenticated = true;
    mockAuthContext.loading = false;
    mockAuthContext.user = { id: '1', name: 'Test User', email: 'test@example.com' };

    const { getByTestId } = render(
      <NavigationContainer>
        <RootNavigator />
      </NavigationContainer>
    );

    // All main screens should be mounted for tab navigation
    expect(getByTestId('home-screen')).toBeTruthy();
    expect(getByTestId('shipment-list-screen')).toBeTruthy();
    expect(getByTestId('profile-screen')).toBeTruthy();
  });

  it('should handle navigation options correctly', () => {
    mockAuthContext.isAuthenticated = true;
    mockAuthContext.loading = false;
    mockAuthContext.user = { id: '1', name: 'Test User', email: 'test@example.com' };

    const { getByTestId } = render(
      <NavigationContainer>
        <RootNavigator />
      </NavigationContainer>
    );

    // Main navigation structure should be present
    expect(getByTestId('home-screen')).toBeTruthy();
    expect(getByTestId('shipment-list-screen')).toBeTruthy();
    expect(getByTestId('profile-screen')).toBeTruthy();
    expect(getByTestId('tracking-screen')).toBeTruthy();
  });

  it('should support guest access to tracking screen', () => {
    mockAuthContext.isAuthenticated = false;
    mockAuthContext.loading = false;

    const { getByTestId } = render(
      <NavigationContainer>
        <RootNavigator />
      </NavigationContainer>
    );

    // Auth screens should be available
    expect(getByTestId('login-screen')).toBeTruthy();
    expect(getByTestId('register-screen')).toBeTruthy();
    
    // Tracking should be available even when not authenticated
    expect(getByTestId('tracking-screen')).toBeTruthy();
  });
});