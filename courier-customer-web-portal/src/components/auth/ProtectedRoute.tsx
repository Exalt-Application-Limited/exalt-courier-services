import React, { useEffect, useState } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { Box, CircularProgress, Typography, Alert } from '@mui/material';
import { useAuth } from '../../contexts/AuthContext';
import { useNotification } from '../../contexts/NotificationContext';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiresCorporate?: boolean;
  requiresVerification?: boolean;
  fallbackPath?: string;
  showLoader?: boolean;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  requiresCorporate = false,
  requiresVerification = false,
  fallbackPath = '/login',
  showLoader = true,
}) => {
  const { user, isAuthenticated, isLoading } = useAuth();
  const { showNotification } = useNotification();
  const location = useLocation();
  const [hasShownNotification, setHasShownNotification] = useState(false);

  useEffect(() => {
    // Show notification only once per route change
    if (!isLoading && !isAuthenticated && !hasShownNotification) {
      showNotification('Please log in to access this page', 'warning');
      setHasShownNotification(true);
    }
  }, [isLoading, isAuthenticated, hasShownNotification, showNotification]);

  // Reset notification flag when location changes
  useEffect(() => {
    setHasShownNotification(false);
  }, [location.pathname]);

  // Show loading spinner while checking authentication
  if (isLoading && showLoader) {
    return (
      <Box
        display="flex"
        flexDirection="column"
        justifyContent="center"
        alignItems="center"
        minHeight="60vh"
        gap={2}
      >
        <CircularProgress size={48} />
        <Typography variant="body1" color="text.secondary">
          Verifying authentication...
        </Typography>
      </Box>
    );
  }

  // Redirect to login if not authenticated
  if (!isAuthenticated) {
    return (
      <Navigate
        to={fallbackPath}
        state={{ from: location.pathname }}
        replace
      />
    );
  }

  // Check corporate account requirement
  if (requiresCorporate && user?.accountType !== 'corporate') {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="warning" sx={{ mb: 2 }}>
          <Typography variant="h6" gutterBottom>
            Corporate Account Required
          </Typography>
          <Typography variant="body2">
            This feature is only available for corporate accounts. Please upgrade your account
            or contact support for assistance.
          </Typography>
        </Alert>
      </Box>
    );
  }

  // Check email verification requirement
  if (requiresVerification && !user?.isVerified) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="info" sx={{ mb: 2 }}>
          <Typography variant="h6" gutterBottom>
            Email Verification Required
          </Typography>
          <Typography variant="body2" paragraph>
            Please verify your email address to access this feature.
            Check your inbox for a verification email.
          </Typography>
          <Typography variant="body2">
            Didn't receive the email? 
            <Box component="span" sx={{ ml: 1 }}>
              <button
                onClick={() => showNotification('Verification email sent!', 'success')}
                style={{
                  background: 'none',
                  border: 'none',
                  color: 'inherit',
                  textDecoration: 'underline',
                  cursor: 'pointer',
                }}
              >
                Resend verification email
              </button>
            </Box>
          </Typography>
        </Alert>
      </Box>
    );
  }

  // Additional permission checks can be added here
  // For example, role-based access control
  
  // If all checks pass, render the protected content
  return <>{children}</>;
};

export default ProtectedRoute;