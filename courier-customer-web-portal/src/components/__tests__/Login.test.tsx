import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import Login from '../../pages/auth/Login';
import { AuthProvider } from '../../contexts/AuthContext';

// Mock react-router-dom
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
  Link: ({ children, to }: { children: React.ReactNode; to: string }) => 
    <a href={to}>{children}</a>
}));

// Mock Material-UI components
jest.mock('@mui/material', () => ({
  Container: ({ children }: { children: React.ReactNode }) => <div data-testid="container">{children}</div>,
  Paper: ({ children }: { children: React.ReactNode }) => <div data-testid="paper">{children}</div>,
  Box: ({ children }: { children: React.ReactNode }) => <div data-testid="box">{children}</div>,
  Typography: ({ children, variant }: { children: React.ReactNode; variant?: string }) => 
    <div data-testid={`typography-${variant || 'default'}`}>{children}</div>,
  TextField: ({ label, type, value, onChange, error, helperText, ...props }: any) => (
    <div data-testid="text-field">
      <label>{label}</label>
      <input
        type={type}
        value={value}
        onChange={onChange}
        data-testid={`input-${label?.toLowerCase().replace(/\s+/g, '-')}`}
        {...props}
      />
      {error && <div data-testid="error-text">{helperText}</div>}
    </div>
  ),
  Button: ({ children, onClick, disabled, type }: any) => (
    <button 
      onClick={onClick} 
      disabled={disabled} 
      type={type}
      data-testid="submit-button"
    >
      {children}
    </button>
  ),
  Alert: ({ children, severity }: { children: React.ReactNode; severity: string }) => (
    <div data-testid={`alert-${severity}`}>{children}</div>
  ),
  CircularProgress: () => <div data-testid="loading-spinner">Loading...</div>
}));

// Mock AuthContext
const mockLogin = jest.fn();
const mockAuthContext = {
  user: null,
  isAuthenticated: false,
  loading: false,
  login: mockLogin,
  logout: jest.fn(),
  register: jest.fn()
};

jest.mock('../../contexts/AuthContext', () => ({
  useAuth: () => mockAuthContext,
  AuthProvider: ({ children }: { children: React.ReactNode }) => <div>{children}</div>
}));

const TestWrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <BrowserRouter>
    <AuthProvider>
      {children}
    </AuthProvider>
  </BrowserRouter>
);

describe('Login Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockAuthContext.loading = false;
  });

  it('should render login form', () => {
    render(
      <TestWrapper>
        <Login />
      </TestWrapper>
    );

    expect(screen.getByTestId('typography-h4')).toHaveTextContent('Sign In');
    expect(screen.getByDisplayValue('')).toBeInTheDocument(); // Email input
    expect(screen.getByLabelText('Email')).toBeInTheDocument();
    expect(screen.getByLabelText('Password')).toBeInTheDocument();
    expect(screen.getByTestId('submit-button')).toHaveTextContent('Sign In');
  });

  it('should handle email input changes', async () => {
    const user = userEvent.setup();
    
    render(
      <TestWrapper>
        <Login />
      </TestWrapper>
    );

    const emailInput = screen.getByTestId('input-email');
    await user.type(emailInput, 'test@example.com');

    expect(emailInput).toHaveValue('test@example.com');
  });

  it('should handle password input changes', async () => {
    const user = userEvent.setup();
    
    render(
      <TestWrapper>
        <Login />
      </TestWrapper>
    );

    const passwordInput = screen.getByTestId('input-password');
    await user.type(passwordInput, 'password123');

    expect(passwordInput).toHaveValue('password123');
  });

  it('should validate email format', async () => {
    const user = userEvent.setup();
    
    render(
      <TestWrapper>
        <Login />
      </TestWrapper>
    );

    const emailInput = screen.getByTestId('input-email');
    const submitButton = screen.getByTestId('submit-button');

    await user.type(emailInput, 'invalid-email');
    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByTestId('error-text')).toHaveTextContent('Please enter a valid email address');
    });
  });

  it('should validate required fields', async () => {
    const user = userEvent.setup();
    
    render(
      <TestWrapper>
        <Login />
      </TestWrapper>
    );

    const submitButton = screen.getByTestId('submit-button');
    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Email is required')).toBeInTheDocument();
      expect(screen.getByText('Password is required')).toBeInTheDocument();
    });
  });

  it('should submit form with valid credentials', async () => {
    const user = userEvent.setup();
    mockLogin.mockResolvedValue({ success: true });
    
    render(
      <TestWrapper>
        <Login />
      </TestWrapper>
    );

    const emailInput = screen.getByTestId('input-email');
    const passwordInput = screen.getByTestId('input-password');
    const submitButton = screen.getByTestId('submit-button');

    await user.type(emailInput, 'test@example.com');
    await user.type(passwordInput, 'password123');
    await user.click(submitButton);

    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledWith('test@example.com', 'password123');
    });
  });

  it('should display error message on login failure', async () => {
    const user = userEvent.setup();
    mockLogin.mockRejectedValue(new Error('Invalid credentials'));
    
    render(
      <TestWrapper>
        <Login />
      </TestWrapper>
    );

    const emailInput = screen.getByTestId('input-email');
    const passwordInput = screen.getByTestId('input-password');
    const submitButton = screen.getByTestId('submit-button');

    await user.type(emailInput, 'test@example.com');
    await user.type(passwordInput, 'wrongpassword');
    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByTestId('alert-error')).toHaveTextContent('Invalid credentials');
    });
  });

  it('should show loading state during submission', async () => {
    const user = userEvent.setup();
    mockAuthContext.loading = true;
    
    render(
      <TestWrapper>
        <Login />
      </TestWrapper>
    );

    expect(screen.getByTestId('loading-spinner')).toBeInTheDocument();
    expect(screen.getByTestId('submit-button')).toBeDisabled();
  });

  it('should navigate to dashboard on successful login', async () => {
    const user = userEvent.setup();
    mockLogin.mockResolvedValue({ success: true });
    mockAuthContext.isAuthenticated = true;
    
    render(
      <TestWrapper>
        <Login />
      </TestWrapper>
    );

    const emailInput = screen.getByTestId('input-email');
    const passwordInput = screen.getByTestId('input-password');
    const submitButton = screen.getByTestId('submit-button');

    await user.type(emailInput, 'test@example.com');
    await user.type(passwordInput, 'password123');
    await user.click(submitButton);

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/dashboard');
    });
  });

  it('should have links to register and forgot password', () => {
    render(
      <TestWrapper>
        <Login />
      </TestWrapper>
    );

    expect(screen.getByText("Don't have an account? Sign up")).toBeInTheDocument();
    expect(screen.getByText('Forgot password?')).toBeInTheDocument();
  });

  it('should prevent form submission when already loading', async () => {
    const user = userEvent.setup();
    mockAuthContext.loading = true;
    
    render(
      <TestWrapper>
        <Login />
      </TestWrapper>
    );

    const submitButton = screen.getByTestId('submit-button');
    expect(submitButton).toBeDisabled();

    await user.click(submitButton);
    expect(mockLogin).not.toHaveBeenCalled();
  });

  it('should clear error message when user starts typing', async () => {
    const user = userEvent.setup();
    mockLogin.mockRejectedValue(new Error('Invalid credentials'));
    
    render(
      <TestWrapper>
        <Login />
      </TestWrapper>
    );

    const emailInput = screen.getByTestId('input-email');
    const passwordInput = screen.getByTestId('input-password');
    const submitButton = screen.getByTestId('submit-button');

    // Trigger error
    await user.type(emailInput, 'test@example.com');
    await user.type(passwordInput, 'wrongpassword');
    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByTestId('alert-error')).toBeInTheDocument();
    });

    // Start typing again - error should clear
    await user.type(emailInput, 'new');

    await waitFor(() => {
      expect(screen.queryByTestId('alert-error')).not.toBeInTheDocument();
    });
  });

  it('should handle keyboard submission (Enter key)', async () => {
    const user = userEvent.setup();
    mockLogin.mockResolvedValue({ success: true });
    
    render(
      <TestWrapper>
        <Login />
      </TestWrapper>
    );

    const emailInput = screen.getByTestId('input-email');
    const passwordInput = screen.getByTestId('input-password');

    await user.type(emailInput, 'test@example.com');
    await user.type(passwordInput, 'password123{enter}');

    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledWith('test@example.com', 'password123');
    });
  });

  it('should display company branding', () => {
    render(
      <TestWrapper>
        <Login />
      </TestWrapper>
    );

    expect(screen.getByText('Exalt Courier')).toBeInTheDocument();
    expect(screen.getByText('Customer Portal')).toBeInTheDocument();
  });

  it('should be accessible with proper labels and ARIA attributes', () => {
    render(
      <TestWrapper>
        <Login />
      </TestWrapper>
    );

    const emailInput = screen.getByLabelText('Email');
    const passwordInput = screen.getByLabelText('Password');

    expect(emailInput).toHaveAttribute('type', 'email');
    expect(passwordInput).toHaveAttribute('type', 'password');
    expect(screen.getByRole('button', { name: 'Sign In' })).toBeInTheDocument();
  });
});