import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  Box,
  Container,
  Paper,
  Typography,
  TextField,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormControlLabel,
  Checkbox,
  Alert,
  Stepper,
  Step,
  StepLabel,
  Grid,
  Divider,
} from '@mui/material';
import { Business, Person, Email, Lock, Phone } from '@mui/icons-material';
import { useAuth, RegisterData } from '../../contexts/AuthContext';
import { useNotification } from '../../contexts/NotificationContext';

const Register: React.FC = () => {
  const navigate = useNavigate();
  const { register, isLoading } = useAuth();
  const { showNotification } = useNotification();
  
  const [activeStep, setActiveStep] = useState(0);
  const [formData, setFormData] = useState<RegisterData>({
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    accountType: 'individual',
    phone: '',
    companyName: '',
  });
  
  const [confirmPassword, setConfirmPassword] = useState('');
  const [agreeToTerms, setAgreeToTerms] = useState(false);
  const [error, setError] = useState('');

  const steps = ['Account Type', 'Personal Information', 'Account Security'];

  const validateStep = (step: number): boolean => {
    switch (step) {
      case 0:
        return true; // Account type is always valid
      case 1:
        if (!formData.firstName || !formData.lastName || !formData.email) {
          setError('Please fill in all required fields');
          return false;
        }
        if (formData.accountType === 'corporate' && !formData.companyName) {
          setError('Company name is required for corporate accounts');
          return false;
        }
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(formData.email)) {
          setError('Please enter a valid email address');
          return false;
        }
        break;
      case 2:
        if (!formData.password || !confirmPassword) {
          setError('Please enter and confirm your password');
          return false;
        }
        if (formData.password.length < 8) {
          setError('Password must be at least 8 characters long');
          return false;
        }
        if (formData.password !== confirmPassword) {
          setError('Passwords do not match');
          return false;
        }
        if (!agreeToTerms) {
          setError('You must agree to the terms and conditions');
          return false;
        }
        break;
    }
    setError('');
    return true;
  };

  const handleNext = () => {
    if (validateStep(activeStep)) {
      setActiveStep((prevStep) => prevStep + 1);
    }
  };

  const handleBack = () => {
    setActiveStep((prevStep) => prevStep - 1);
    setError('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateStep(2)) return;
    
    try {
      const result = await register(formData);
      if (result.success) {
        showNotification('Account created successfully! Welcome to Exalt Courier.', 'success');
        navigate('/dashboard');
      } else {
        setError(result.error || 'Registration failed');
      }
    } catch (error) {
      setError('An unexpected error occurred. Please try again.');
    }
  };

  const renderStepContent = () => {
    switch (activeStep) {
      case 0:
        return (
          <Box sx={{ py: 4 }}>
            <Typography variant="h6" gutterBottom align="center">
              Choose Your Account Type
            </Typography>
            <Typography variant="body2" color="text.secondary" align="center" paragraph>
              Select the type of account that best describes your shipping needs
            </Typography>
            
            <Grid container spacing={3} sx={{ mt: 2 }}>
              <Grid item xs={12} md={6}>
                <Paper
                  sx={{
                    p: 3,
                    cursor: 'pointer',
                    border: formData.accountType === 'individual' ? 2 : 1,
                    borderColor: formData.accountType === 'individual' ? 'primary.main' : 'divider',
                    '&:hover': { borderColor: 'primary.main' },
                  }}
                  onClick={() => setFormData({ ...formData, accountType: 'individual' })}
                >
                  <Box display="flex" flexDirection="column" alignItems="center" gap={2}>
                    <Person color={formData.accountType === 'individual' ? 'primary' : 'inherit'} sx={{ fontSize: 48 }} />
                    <Typography variant="h6">Individual Account</Typography>
                    <Typography variant="body2" color="text.secondary" align="center">
                      Perfect for personal shipping needs and individual customers
                    </Typography>
                    <Box component="ul" sx={{ pl: 2, '& li': { fontSize: '0.875rem', color: 'text.secondary' } }}>
                      <li>Personal shipment tracking</li>
                      <li>Standard pricing</li>
                      <li>Basic support</li>
                    </Box>
                  </Box>
                </Paper>
              </Grid>
              
              <Grid item xs={12} md={6}>
                <Paper
                  sx={{
                    p: 3,
                    cursor: 'pointer',
                    border: formData.accountType === 'corporate' ? 2 : 1,
                    borderColor: formData.accountType === 'corporate' ? 'primary.main' : 'divider',
                    '&:hover': { borderColor: 'primary.main' },
                  }}
                  onClick={() => setFormData({ ...formData, accountType: 'corporate' })}
                >
                  <Box display="flex" flexDirection="column" alignItems="center" gap={2}>
                    <Business color={formData.accountType === 'corporate' ? 'primary' : 'inherit'} sx={{ fontSize: 48 }} />
                    <Typography variant="h6">Corporate Account</Typography>
                    <Typography variant="body2" color="text.secondary" align="center">
                      Designed for businesses with high-volume shipping requirements
                    </Typography>
                    <Box component="ul" sx={{ pl: 2, '& li': { fontSize: '0.875rem', color: 'text.secondary' } }}>
                      <li>Volume discounts</li>
                      <li>Advanced analytics</li>
                      <li>Priority support</li>
                      <li>API access</li>
                    </Box>
                  </Box>
                </Paper>
              </Grid>
            </Grid>
          </Box>
        );

      case 1:
        return (
          <Box sx={{ py: 4 }}>
            <Typography variant="h6" gutterBottom align="center">
              Personal Information
            </Typography>
            <Typography variant="body2" color="text.secondary" align="center" paragraph>
              Tell us about yourself to personalize your experience
            </Typography>
            
            <Grid container spacing={3} sx={{ mt: 2 }}>
              <Grid item xs={12} md={6}>
                <TextField
                  fullWidth
                  label="First Name"
                  value={formData.firstName}
                  onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                  required
                  InputProps={{
                    startAdornment: <Person sx={{ mr: 1, color: 'text.secondary' }} />,
                  }}
                />
              </Grid>
              <Grid item xs={12} md={6}>
                <TextField
                  fullWidth
                  label="Last Name"
                  value={formData.lastName}
                  onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                  required
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Email Address"
                  type="email"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  required
                  InputProps={{
                    startAdornment: <Email sx={{ mr: 1, color: 'text.secondary' }} />,
                  }}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Phone Number"
                  value={formData.phone}
                  onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                  placeholder="+1 (555) 123-4567"
                  InputProps={{
                    startAdornment: <Phone sx={{ mr: 1, color: 'text.secondary' }} />,
                  }}
                />
              </Grid>
              {formData.accountType === 'corporate' && (
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Company Name"
                    value={formData.companyName}
                    onChange={(e) => setFormData({ ...formData, companyName: e.target.value })}
                    required
                    InputProps={{
                      startAdornment: <Business sx={{ mr: 1, color: 'text.secondary' }} />,
                    }}
                  />
                </Grid>
              )}
            </Grid>
          </Box>
        );

      case 2:
        return (
          <Box sx={{ py: 4 }}>
            <Typography variant="h6" gutterBottom align="center">
              Account Security
            </Typography>
            <Typography variant="body2" color="text.secondary" align="center" paragraph>
              Create a secure password to protect your account
            </Typography>
            
            <Grid container spacing={3} sx={{ mt: 2 }}>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Password"
                  type="password"
                  value={formData.password}
                  onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                  required
                  helperText="Password must be at least 8 characters long"
                  InputProps={{
                    startAdornment: <Lock sx={{ mr: 1, color: 'text.secondary' }} />,
                  }}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Confirm Password"
                  type="password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  required
                  error={confirmPassword !== '' && formData.password !== confirmPassword}
                  helperText={
                    confirmPassword !== '' && formData.password !== confirmPassword
                      ? 'Passwords do not match'
                      : ''
                  }
                />
              </Grid>
              <Grid item xs={12}>
                <FormControlLabel
                  control={
                    <Checkbox
                      checked={agreeToTerms}
                      onChange={(e) => setAgreeToTerms(e.target.checked)}
                      color="primary"
                    />
                  }
                  label={
                    <Typography variant="body2">
                      I agree to the{' '}
                      <Link to="/terms" target="_blank" style={{ color: 'inherit' }}>
                        Terms of Service
                      </Link>{' '}
                      and{' '}
                      <Link to="/privacy" target="_blank" style={{ color: 'inherit' }}>
                        Privacy Policy
                      </Link>
                    </Typography>
                  }
                />
              </Grid>
            </Grid>
          </Box>
        );

      default:
        return null;
    }
  };

  return (
    <Container maxWidth="md" sx={{ py: 8 }}>
      <Paper sx={{ p: 4 }}>
        {/* Header */}
        <Box textAlign="center" mb={4}>
          <Typography variant="h4" fontWeight="bold" gutterBottom>
            Create Your Account
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Join thousands of customers who trust Exalt Courier for their shipping needs
          </Typography>
        </Box>

        {/* Stepper */}
        <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
          {steps.map((label) => (
            <Step key={label}>
              <StepLabel>{label}</StepLabel>
            </Step>
          ))}
        </Stepper>

        {/* Error Alert */}
        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}

        {/* Step Content */}
        <form onSubmit={handleSubmit}>
          {renderStepContent()}

          {/* Navigation Buttons */}
          <Box display="flex" justifyContent="space-between" mt={4}>
            <Button
              disabled={activeStep === 0}
              onClick={handleBack}
              variant="outlined"
            >
              Back
            </Button>
            
            <Box display="flex" gap={2}>
              {activeStep === steps.length - 1 ? (
                <Button
                  type="submit"
                  variant="contained"
                  disabled={isLoading}
                  size="large"
                >
                  {isLoading ? 'Creating Account...' : 'Create Account'}
                </Button>
              ) : (
                <Button
                  onClick={handleNext}
                  variant="contained"
                  size="large"
                >
                  Next
                </Button>
              )}
            </Box>
          </Box>
        </form>

        <Divider sx={{ my: 4 }} />

        {/* Login Link */}
        <Box textAlign="center">
          <Typography variant="body2" color="text.secondary">
            Already have an account?{' '}
            <Link to="/login" style={{ color: 'inherit', fontWeight: 'bold' }}>
              Sign in here
            </Link>
          </Typography>
        </Box>
      </Paper>
    </Container>
  );
};

export default Register;