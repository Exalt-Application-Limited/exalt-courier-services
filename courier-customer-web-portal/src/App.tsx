import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';

// Layouts
import MainLayout from './layouts/MainLayout';

// Auth pages
import Login from './pages/auth/Login';
import Register from './pages/auth/Register';
import ForgotPassword from './pages/auth/ForgotPassword';
import ProtectedRoute from './components/auth/ProtectedRoute';

// Main pages
import Home from './pages/Home';
import ServiceQuote from './pages/services/ServiceQuote';
import ShipmentBooking from './pages/shipments/ShipmentBooking';
import ShipmentTracking from './pages/shipments/ShipmentTracking';
import ShipmentHistory from './pages/shipments/ShipmentHistory';
import ShipmentDetail from './pages/shipments/ShipmentDetail';

// Account pages
import Dashboard from './pages/account/Dashboard';
import Profile from './pages/account/Profile';
import BillingInfo from './pages/account/BillingInfo';
import InvoiceHistory from './pages/account/InvoiceHistory';
import InvoiceDetail from './pages/account/InvoiceDetail';
import AddressBook from './pages/account/AddressBook';
import PaymentMethods from './pages/account/PaymentMethods';

// Support pages
import SupportCenter from './pages/support/SupportCenter';
import TicketDetail from './pages/support/TicketDetail';
import ContactUs from './pages/support/ContactUs';

// Corporate pages
import CorporateOnboarding from './pages/corporate/CorporateOnboarding';
import CorporateDashboard from './pages/corporate/CorporateDashboard';

import NotFound from './pages/NotFound';

// Context providers
import { AuthProvider } from './contexts/AuthContext';
import { NotificationProvider } from './contexts/NotificationContext';

// Create theme for Exalt Courier branding
const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#1976d2', // Exalt Courier Blue
      light: '#42a5f5',
      dark: '#1565c0',
    },
    secondary: {
      main: '#ff6b35', // Exalt Courier Orange
      light: '#ff8a65',
      dark: '#e64a19',
    },
    background: {
      default: '#f8fafc',
      paper: '#ffffff',
    },
    success: {
      main: '#4caf50',
    },
    warning: {
      main: '#ff9800',
    },
    error: {
      main: '#f44336',
    },
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    h1: {
      fontSize: '2.5rem',
      fontWeight: 600,
    },
    h2: {
      fontSize: '2rem',
      fontWeight: 600,
    },
    h3: {
      fontSize: '1.75rem',
      fontWeight: 600,
    },
    h4: {
      fontSize: '1.5rem',
      fontWeight: 600,
    },
    h5: {
      fontSize: '1.25rem',
      fontWeight: 600,
    },
    h6: {
      fontSize: '1rem',
      fontWeight: 600,
    },
    button: {
      textTransform: 'none',
      fontWeight: 500,
    },
  },
  shape: {
    borderRadius: 8,
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          padding: '10px 20px',
          fontSize: '0.875rem',
        },
        contained: {
          boxShadow: '0px 2px 4px rgba(0, 0, 0, 0.1)',
          '&:hover': {
            boxShadow: '0px 4px 8px rgba(0, 0, 0, 0.15)',
          },
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          boxShadow: '0px 2px 12px rgba(0, 0, 0, 0.08)',
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          boxShadow: '0px 2px 8px rgba(0, 0, 0, 0.05)',
          backgroundColor: '#ffffff',
          color: '#1976d2',
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            borderRadius: 8,
          },
        },
      },
    },
  },
});

const App: React.FC = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <NotificationProvider>
          <Router>
              <Routes>
                {/* Auth Routes */}
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/forgot-password" element={<ForgotPassword />} />
                <Route path="/corporate/onboarding" element={<CorporateOnboarding />} />
                
                {/* Main Layout Routes */}
                <Route path="/" element={<MainLayout />}>
                  <Route index element={<Home />} />
                  
                  {/* Service Routes */}
                  <Route path="quote" element={<ServiceQuote />} />
                  <Route path="book" element={<ShipmentBooking />} />
                  <Route path="track" element={<ShipmentTracking />} />
                  
                  {/* Protected Routes */}
                  <Route path="dashboard" element={
                    <ProtectedRoute>
                      <Dashboard />
                    </ProtectedRoute>
                  } />
                  
                  {/* Shipment Management */}
                  <Route path="shipments" element={
                    <ProtectedRoute>
                      <ShipmentHistory />
                    </ProtectedRoute>
                  } />
                  <Route path="shipments/:id" element={
                    <ProtectedRoute>
                      <ShipmentDetail />
                    </ProtectedRoute>
                  } />
                  
                  {/* Account Management */}
                  <Route path="account/profile" element={
                    <ProtectedRoute>
                      <Profile />
                    </ProtectedRoute>
                  } />
                  <Route path="account/billing" element={
                    <ProtectedRoute>
                      <BillingInfo />
                    </ProtectedRoute>
                  } />
                  <Route path="account/invoices" element={
                    <ProtectedRoute>
                      <InvoiceHistory />
                    </ProtectedRoute>
                  } />
                  <Route path="account/invoices/:id" element={
                    <ProtectedRoute>
                      <InvoiceDetail />
                    </ProtectedRoute>
                  } />
                  <Route path="account/addresses" element={
                    <ProtectedRoute>
                      <AddressBook />
                    </ProtectedRoute>
                  } />
                  <Route path="account/payment-methods" element={
                    <ProtectedRoute>
                      <PaymentMethods />
                    </ProtectedRoute>
                  } />
                  
                  {/* Corporate Routes */}
                  <Route path="corporate/dashboard" element={
                    <ProtectedRoute requiresCorporate>
                      <CorporateDashboard />
                    </ProtectedRoute>
                  } />
                  
                  {/* Support Routes */}
                  <Route path="support" element={<SupportCenter />} />
                  <Route path="support/tickets/:id" element={
                    <ProtectedRoute>
                      <TicketDetail />
                    </ProtectedRoute>
                  } />
                  <Route path="contact" element={<ContactUs />} />
                  
                  {/* Catch-all route */}
                  <Route path="*" element={<NotFound />} />
                </Route>
              </Routes>
            </Router>
          </NotificationProvider>
        </AuthProvider>
    </ThemeProvider>
  );
};

export default App;