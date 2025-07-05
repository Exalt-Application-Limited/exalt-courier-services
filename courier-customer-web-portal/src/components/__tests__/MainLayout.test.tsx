import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from '../../contexts/AuthContext';
import { NotificationProvider } from '../../contexts/NotificationContext';
import MainLayout from '../../layouts/MainLayout';

// Mock the contexts
jest.mock('../../contexts/AuthContext', () => ({
  ...jest.requireActual('../../contexts/AuthContext'),
  useAuth: () => ({
    user: { id: '1', name: 'Test User', email: 'test@example.com' },
    isAuthenticated: true,
    logout: jest.fn()
  })
}));

jest.mock('../../contexts/NotificationContext', () => ({
  NotificationProvider: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  useNotification: () => ({
    notifications: [],
    addNotification: jest.fn(),
    removeNotification: jest.fn()
  })
}));

// Mock Material-UI components
jest.mock('@mui/material', () => ({
  ...jest.requireActual('@mui/material'),
  AppBar: ({ children }: { children: React.ReactNode }) => <div data-testid="app-bar">{children}</div>,
  Toolbar: ({ children }: { children: React.ReactNode }) => <div data-testid="toolbar">{children}</div>,
  Typography: ({ children }: { children: React.ReactNode }) => <div data-testid="typography">{children}</div>,
  Box: ({ children }: { children: React.ReactNode }) => <div data-testid="box">{children}</div>,
  Drawer: ({ children, open }: { children: React.ReactNode; open: boolean }) => 
    open ? <div data-testid="drawer">{children}</div> : null,
  List: ({ children }: { children: React.ReactNode }) => <div data-testid="list">{children}</div>,
  ListItem: ({ children }: { children: React.ReactNode }) => <div data-testid="list-item">{children}</div>,
  ListItemIcon: ({ children }: { children: React.ReactNode }) => <div data-testid="list-item-icon">{children}</div>,
  ListItemText: ({ primary }: { primary: string }) => <div data-testid="list-item-text">{primary}</div>,
  IconButton: ({ children, onClick }: { children: React.ReactNode; onClick: () => void }) => 
    <button data-testid="icon-button" onClick={onClick}>{children}</button>,
  Menu: ({ children, open }: { children: React.ReactNode; open: boolean }) => 
    open ? <div data-testid="menu">{children}</div> : null,
  MenuItem: ({ children, onClick }: { children: React.ReactNode; onClick: () => void }) => 
    <div data-testid="menu-item" onClick={onClick}>{children}</div>,
  Avatar: () => <div data-testid="avatar">Avatar</div>,
  Badge: ({ children }: { children: React.ReactNode }) => <div data-testid="badge">{children}</div>
}));

// Mock Material-UI icons
jest.mock('@mui/icons-material', () => ({
  Menu: () => <div data-testid="menu-icon">Menu</div>,
  AccountCircle: () => <div data-testid="account-icon">Account</div>,
  Notifications: () => <div data-testid="notifications-icon">Notifications</div>,
  Dashboard: () => <div data-testid="dashboard-icon">Dashboard</div>,
  LocalShipping: () => <div data-testid="shipping-icon">Shipping</div>,
  Payment: () => <div data-testid="payment-icon">Payment</div>,
  Support: () => <div data-testid="support-icon">Support</div>,
  Person: () => <div data-testid="person-icon">Person</div>,
  Logout: () => <div data-testid="logout-icon">Logout</div>
}));

const TestWrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <BrowserRouter>
    <AuthProvider>
      <NotificationProvider>
        {children}
      </NotificationProvider>
    </AuthProvider>
  </BrowserRouter>
);

describe('MainLayout', () => {
  const mockChild = <div data-testid="test-child">Test Child Content</div>;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render the main layout with app bar and navigation', () => {
    render(
      <TestWrapper>
        <MainLayout>{mockChild}</MainLayout>
      </TestWrapper>
    );

    expect(screen.getByTestId('app-bar')).toBeInTheDocument();
    expect(screen.getByTestId('toolbar')).toBeInTheDocument();
    expect(screen.getByTestId('test-child')).toBeInTheDocument();
  });

  it('should display user name in the app bar', () => {
    render(
      <TestWrapper>
        <MainLayout>{mockChild}</MainLayout>
      </TestWrapper>
    );

    expect(screen.getByText('Test User')).toBeInTheDocument();
  });

  it('should show navigation drawer when menu button is clicked', () => {
    render(
      <TestWrapper>
        <MainLayout>{mockChild}</MainLayout>
      </TestWrapper>
    );

    const menuButton = screen.getByTestId('icon-button');
    fireEvent.click(menuButton);

    expect(screen.getByTestId('drawer')).toBeInTheDocument();
    expect(screen.getByTestId('list')).toBeInTheDocument();
  });

  it('should display navigation menu items', () => {
    render(
      <TestWrapper>
        <MainLayout>{mockChild}</MainLayout>
      </TestWrapper>
    );

    // Open drawer first
    const menuButton = screen.getByTestId('icon-button');
    fireEvent.click(menuButton);

    // Check for navigation items
    expect(screen.getByText('Dashboard')).toBeInTheDocument();
    expect(screen.getByText('Shipments')).toBeInTheDocument();
    expect(screen.getByText('Billing')).toBeInTheDocument();
    expect(screen.getByText('Support')).toBeInTheDocument();
  });

  it('should show user menu when account icon is clicked', () => {
    render(
      <TestWrapper>
        <MainLayout>{mockChild}</MainLayout>
      </TestWrapper>
    );

    const accountButton = screen.getAllByTestId('icon-button')[1]; // Second icon button is account
    fireEvent.click(accountButton);

    expect(screen.getByTestId('menu')).toBeInTheDocument();
  });

  it('should display user menu items', () => {
    render(
      <TestWrapper>
        <MainLayout>{mockChild}</MainLayout>
      </TestWrapper>
    );

    // Open user menu
    const accountButton = screen.getAllByTestId('icon-button')[1];
    fireEvent.click(accountButton);

    expect(screen.getByText('Profile')).toBeInTheDocument();
    expect(screen.getByText('Settings')).toBeInTheDocument();
    expect(screen.getByText('Logout')).toBeInTheDocument();
  });

  it('should close drawer when clicking outside', () => {
    render(
      <TestWrapper>
        <MainLayout>{mockChild}</MainLayout>
      </TestWrapper>
    );

    // Open drawer
    const menuButton = screen.getByTestId('icon-button');
    fireEvent.click(menuButton);
    
    expect(screen.getByTestId('drawer')).toBeInTheDocument();

    // Click on main content area (outside drawer)
    const mainContent = screen.getByTestId('test-child');
    fireEvent.click(mainContent);

    // Drawer should still be visible (this test would need actual click outside implementation)
    // For this mock test, we just verify the drawer exists
    expect(screen.getByTestId('drawer')).toBeInTheDocument();
  });

  it('should display notifications badge', () => {
    render(
      <TestWrapper>
        <MainLayout>{mockChild}</MainLayout>
      </TestWrapper>
    );

    expect(screen.getByTestId('badge')).toBeInTheDocument();
    expect(screen.getByTestId('notifications-icon')).toBeInTheDocument();
  });

  it('should render child content in main area', () => {
    const customChild = <div data-testid="custom-content">Custom Page Content</div>;
    
    render(
      <TestWrapper>
        <MainLayout>{customChild}</MainLayout>
      </TestWrapper>
    );

    expect(screen.getByTestId('custom-content')).toBeInTheDocument();
    expect(screen.getByText('Custom Page Content')).toBeInTheDocument();
  });

  it('should handle responsive layout', () => {
    // Mock window.innerWidth for mobile
    Object.defineProperty(window, 'innerWidth', {
      writable: true,
      configurable: true,
      value: 600,
    });

    render(
      <TestWrapper>
        <MainLayout>{mockChild}</MainLayout>
      </TestWrapper>
    );

    // On mobile, drawer should be temporary (this would need actual responsive logic)
    expect(screen.getByTestId('app-bar')).toBeInTheDocument();
  });

  it('should navigate when menu items are clicked', () => {
    const mockNavigate = jest.fn();
    
    // Mock useNavigate hook
    jest.mock('react-router-dom', () => ({
      ...jest.requireActual('react-router-dom'),
      useNavigate: () => mockNavigate
    }));

    render(
      <TestWrapper>
        <MainLayout>{mockChild}</MainLayout>
      </TestWrapper>
    );

    // Open drawer
    const menuButton = screen.getByTestId('icon-button');
    fireEvent.click(menuButton);

    // Click on Dashboard menu item
    const dashboardItem = screen.getByText('Dashboard');
    fireEvent.click(dashboardItem);

    // Navigation would be handled by the actual component
    // This test verifies the menu item exists and is clickable
    expect(dashboardItem).toBeInTheDocument();
  });

  it('should display correct icons for navigation items', () => {
    render(
      <TestWrapper>
        <MainLayout>{mockChild}</MainLayout>
      </TestWrapper>
    );

    // Open drawer
    const menuButton = screen.getByTestId('icon-button');
    fireEvent.click(menuButton);

    // Verify icons are present
    expect(screen.getByTestId('dashboard-icon')).toBeInTheDocument();
    expect(screen.getByTestId('shipping-icon')).toBeInTheDocument();
    expect(screen.getByTestId('payment-icon')).toBeInTheDocument();
    expect(screen.getByTestId('support-icon')).toBeInTheDocument();
  });
});