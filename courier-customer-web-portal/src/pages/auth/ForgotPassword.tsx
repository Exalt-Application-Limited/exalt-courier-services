import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  Box,
  Container,
  Paper,
  Typography,
  TextField,
  Button,
  Alert,
  Stepper,
  Step,
  StepLabel,
  InputAdornment,
} from '@mui/material';
import { Email, Lock, CheckCircle, ArrowBack } from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';
import { useNotification } from '../../contexts/NotificationContext';

const ForgotPassword: React.FC = () => {
  const navigate = useNavigate();
  const { forgotPassword, resetPassword, isLoading } = useAuth();
  const { showNotification } = useNotification();
  
  const [activeStep, setActiveStep] = useState(0);
  const [email, setEmail] = useState('');
  const [resetToken, setResetToken] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const steps = ['Enter Email', 'Check Email', 'Reset Password'];

  const validateEmail = (email: string): boolean => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const handleSendResetEmail = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!email) {
      setError('Please enter your email address');
      return;
    }

    if (!validateEmail(email)) {
      setError('Please enter a valid email address');
      return;
    }

    try {
      const result = await forgotPassword(email);
      if (result.success) {
        setSuccess('Password reset email sent successfully!');
        setActiveStep(1);
        showNotification('Check your email for reset instructions', 'success');
      } else {
        setError(result.error || 'Failed to send reset email');
      }
    } catch (error) {
      setError('An unexpected error occurred. Please try again.');
    }
  };

  const handleResetPassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!resetToken) {
      setError('Please enter the reset token from your email');
      return;
    }

    if (!newPassword) {
      setError('Please enter a new password');
      return;
    }

    if (newPassword.length < 8) {
      setError('Password must be at least 8 characters long');
      return;
    }

    if (newPassword !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    try {
      const result = await resetPassword(resetToken, newPassword);
      if (result.success) {
        setSuccess('Password reset successfully!');
        setActiveStep(3);
        showNotification('Password reset successfully! You can now log in.', 'success');
        setTimeout(() => {
          navigate('/login');
        }, 2000);
      } else {
        setError(result.error || 'Failed to reset password');
      }
    } catch (error) {
      setError('An unexpected error occurred. Please try again.');
    }
  };

  const renderStepContent = () => {
    switch (activeStep) {
      case 0:
        return (
          <Box>
            <Typography variant="h5" fontWeight="bold" gutterBottom align="center">
              Forgot Your Password?
            </Typography>
            <Typography variant="body1" color="text.secondary" align="center" paragraph>
              No worries! Enter your email address and we'll send you a reset link.
            </Typography>
            
            <form onSubmit={handleSendResetEmail}>
              <TextField
                fullWidth
                label="Email Address"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                margin="normal"
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Email />
                    </InputAdornment>
                  ),
                }}
                placeholder="Enter your registered email address"
              />
              
              <Button
                type="submit"
                fullWidth
                variant="contained"
                size="large"
                disabled={isLoading}
                sx={{ mt: 3, mb: 2 }}
              >
                {isLoading ? 'Sending...' : 'Send Reset Email'}
              </Button>
            </form>
          </Box>
        );

      case 1:
        return (
          <Box textAlign="center">
            <CheckCircle color="success" sx={{ fontSize: 64, mb: 2 }} />
            <Typography variant="h5" fontWeight="bold" gutterBottom>
              Check Your Email
            </Typography>
            <Typography variant="body1" color="text.secondary" paragraph>
              We've sent a password reset link to:
            </Typography>
            <Typography variant="h6" color="primary" gutterBottom>
              {email}
            </Typography>
            <Typography variant="body2" color="text.secondary" paragraph>
              Click the link in the email to reset your password. If you don't see the email,
              check your spam folder.
            </Typography>
            
            <Alert severity="info" sx={{ mt: 3, mb: 2 }}>
              <Typography variant="body2">
                <strong>Didn't receive the email?</strong> Make sure you entered the correct
                email address and check your spam folder. The link will expire in 24 hours.
              </Typography>
            </Alert>
            
            <Button
              variant="outlined"
              onClick={() => setActiveStep(2)}
              sx={{ mt: 2 }}
            >
              I Have the Reset Code
            </Button>
          </Box>
        );

      case 2:
        return (
          <Box>
            <Typography variant="h5" fontWeight="bold" gutterBottom align="center">
              Reset Your Password
            </Typography>
            <Typography variant="body1" color="text.secondary" align="center" paragraph>
              Enter the reset code from your email and create a new password.
            </Typography>
            
            <form onSubmit={handleResetPassword}>
              <TextField
                fullWidth
                label="Reset Code"
                value={resetToken}
                onChange={(e) => setResetToken(e.target.value)}
                required
                margin="normal"
                placeholder="Enter the code from your email"
                helperText="Check your email for the 6-digit reset code"
              />
              
              <TextField
                fullWidth
                label="New Password"
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                required
                margin="normal"
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Lock />
                    </InputAdornment>
                  ),
                }}
                helperText="Password must be at least 8 characters long"
              />
              
              <TextField
                fullWidth
                label="Confirm New Password"
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
                margin="normal"
                error={confirmPassword !== '' && newPassword !== confirmPassword}
                helperText={
                  confirmPassword !== '' && newPassword !== confirmPassword
                    ? 'Passwords do not match'
                    : 'Re-enter your new password'
                }
              />
              
              <Button
                type="submit"
                fullWidth
                variant="contained"
                size="large"
                disabled={isLoading}
                sx={{ mt: 3, mb: 2 }}
              >
                {isLoading ? 'Resetting...' : 'Reset Password'}
              </Button>
            </form>
          </Box>
        );

      case 3:
        return (
          <Box textAlign="center">
            <CheckCircle color="success" sx={{ fontSize: 64, mb: 2 }} />
            <Typography variant="h5" fontWeight="bold" gutterBottom>
              Password Reset Successful!
            </Typography>
            <Typography variant="body1" color="text.secondary" paragraph>
              Your password has been successfully reset. You can now log in with your new password.
            </Typography>
            <Typography variant="body2" color="text.secondary" paragraph>
              Redirecting you to the login page...
            </Typography>
          </Box>
        );

      default:
        return null;
    }
  };

  return (
    <Container maxWidth="sm" sx={{ py: 8 }}>
      <Paper sx={{ p: 4 }}>
        {/* Back Button */}
        <Box mb={3}>
          <Button
            startIcon={<ArrowBack />}
            onClick={() => navigate('/login')}
            color="inherit"
          >
            Back to Login
          </Button>
        </Box>

        {/* Progress Stepper */}
        {activeStep < 3 && (
          <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
            {steps.map((label) => (
              <Step key={label}>
                <StepLabel>{label}</StepLabel>
              </Step>
            ))}
          </Stepper>
        )}

        {/* Error Alert */}
        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}

        {/* Success Alert */}
        {success && (
          <Alert severity="success" sx={{ mb: 3 }}>
            {success}
          </Alert>
        )}

        {/* Step Content */}
        {renderStepContent()}

        {/* Help Section */}
        {activeStep < 3 && (
          <Box mt={4} pt={3} borderTop={1} borderColor="divider">
            <Typography variant="body2" color="text.secondary" align="center">
              Still having trouble?{' '}
              <Link to="/contact" style={{ color: 'inherit', fontWeight: 'bold' }}>
                Contact our support team
              </Link>
            </Typography>
          </Box>
        )}
      </Paper>
    </Container>
  );
};

export default ForgotPassword;