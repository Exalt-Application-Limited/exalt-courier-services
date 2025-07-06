import React, { Suspense, useEffect } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { Box, CircularProgress } from '@mui/material';
import MainLayout from './layouts/MainLayout';
import AuthLayout from './layouts/AuthLayout';
import { useAuth } from './hooks/useAuth';
import { loadUser } from './store/slices/authSlice';
import ErrorBoundary from './components/common/ErrorBoundary';
import ProtectedRoute from './components/common/ProtectedRoute';

// Lazy load pages for better performance
const HomePage = React.lazy(() => import('./pages/HomePage'));
const CourierSearchPage = React.lazy(() => import('./pages/CourierSearchPage'));
const CourierDetailPage = React.lazy(() => import('./pages/CourierDetailPage'));
const BookingPage = React.lazy(() => import('./pages/BookingPage'));
const QuotePage = React.lazy(() => import('./pages/QuotePage'));
const TrackingPage = React.lazy(() => import('./pages/TrackingPage'));
const DashboardPage = React.lazy(() => import('./pages/DashboardPage'));
const BookingHistoryPage = React.lazy(() => import('./pages/BookingHistoryPage'));
const BookingDetailPage = React.lazy(() => import('./pages/BookingDetailPage'));
const ProfilePage = React.lazy(() => import('./pages/ProfilePage'));
const SupportPage = React.lazy(() => import('./pages/SupportPage'));
const LoginPage = React.lazy(() => import('./pages/auth/LoginPage'));
const RegisterPage = React.lazy(() => import('./pages/auth/RegisterPage'));
const ForgotPasswordPage = React.lazy(() => import('./pages/auth/ForgotPasswordPage'));
const ResetPasswordPage = React.lazy(() => import('./pages/auth/ResetPasswordPage'));
const NotFoundPage = React.lazy(() => import('./pages/NotFoundPage'));

const LoadingFallback = () => (
  <Box display="flex" justifyContent="center" alignItems="center" minHeight="80vh">
    <CircularProgress />
  </Box>
);

function App() {
  const dispatch = useDispatch();
  const { isAuthenticated } = useAuth();

  useEffect(() => {
    // Load user data if token exists
    const token = localStorage.getItem(process.env.REACT_APP_TOKEN_STORAGE_KEY || 'exalt_courier_token');
    if (token) {
      dispatch(loadUser() as any);
    }
  }, [dispatch]);

  return (
    <ErrorBoundary>
      <Suspense fallback={<LoadingFallback />}>
        <Routes>
          {/* Public routes with MainLayout */}
          <Route path="/" element={<MainLayout />}>
            <Route index element={<HomePage />} />
            <Route path="search" element={<CourierSearchPage />} />
            <Route path="courier/:courierId" element={<CourierDetailPage />} />
            <Route path="quote" element={<QuotePage />} />
            <Route path="track" element={<TrackingPage />} />
            <Route path="track/:trackingNumber" element={<TrackingPage />} />
            <Route path="support" element={<SupportPage />} />
            
            {/* Protected routes */}
            <Route element={<ProtectedRoute />}>
              <Route path="book" element={<BookingPage />} />
              <Route path="book/:courierId" element={<BookingPage />} />
              <Route path="dashboard" element={<DashboardPage />} />
              <Route path="dashboard/bookings" element={<BookingHistoryPage />} />
              <Route path="dashboard/bookings/:bookingId" element={<BookingDetailPage />} />
              <Route path="dashboard/profile" element={<ProfilePage />} />
            </Route>
          </Route>

          {/* Auth routes with AuthLayout */}
          <Route path="/auth" element={<AuthLayout />}>
            <Route path="login" element={<LoginPage />} />
            <Route path="register" element={<RegisterPage />} />
            <Route path="forgot-password" element={<ForgotPasswordPage />} />
            <Route path="reset-password/:token" element={<ResetPasswordPage />} />
          </Route>

          {/* Redirects */}
          <Route path="/login" element={<Navigate to="/auth/login" replace />} />
          <Route path="/register" element={<Navigate to="/auth/register" replace />} />

          {/* 404 Page */}
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </Suspense>
    </ErrorBoundary>
  );
}

export default App;