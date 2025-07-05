import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Container,
  Typography,
  Button,
  Paper,
  Grid,
} from '@mui/material';
import {
  Home,
  Search,
  LocalShipping,
  ArrowBack,
  SentimentDissatisfied,
} from '@mui/icons-material';

const NotFound: React.FC = () => {
  const navigate = useNavigate();

  const quickActions = [
    {
      label: 'Go Home',
      icon: <Home />,
      action: () => navigate('/'),
      description: 'Return to the homepage',
    },
    {
      label: 'Track Package',
      icon: <LocalShipping />,
      action: () => navigate('/track'),
      description: 'Track your shipment',
    },
    {
      label: 'Get Quote',
      icon: <Search />,
      action: () => navigate('/quote'),
      description: 'Get shipping quote',
    },
    {
      label: 'Go Back',
      icon: <ArrowBack />,
      action: () => navigate(-1),
      description: 'Return to previous page',
    },
  ];

  return (
    <Container maxWidth="lg" sx={{ py: 8 }}>
      <Box textAlign="center">
        {/* 404 Illustration */}
        <Box mb={4}>
          <Typography
            variant="h1"
            sx={{
              fontSize: { xs: '6rem', md: '8rem' },
              fontWeight: 'bold',
              color: 'primary.main',
              opacity: 0.3,
            }}
          >
            404
          </Typography>
        </Box>

        {/* Main Message */}
        <Box mb={4}>
          <Box display="flex" justifyContent="center" alignItems="center" mb={2}>
            <SentimentDissatisfied sx={{ fontSize: 48, color: 'text.secondary', mr: 1 }} />
            <Typography variant="h4" fontWeight="bold">
              Oops! Page Not Found
            </Typography>
          </Box>
          <Typography variant="h6" color="text.secondary" gutterBottom>
            The page you're looking for seems to have been moved, deleted, or doesn't exist.
          </Typography>
          <Typography variant="body1" color="text.secondary">
            But don't worry, we can help you find what you're looking for!
          </Typography>
        </Box>

        {/* Quick Actions */}
        <Paper sx={{ p: 4, mb: 4 }}>
          <Typography variant="h6" gutterBottom>
            What would you like to do?
          </Typography>
          <Grid container spacing={2} sx={{ mt: 2 }}>
            {quickActions.map((action, index) => (
              <Grid item xs={12} sm={6} md={3} key={index}>
                <Button
                  variant="outlined"
                  fullWidth
                  startIcon={action.icon}
                  onClick={action.action}
                  sx={{
                    height: 80,
                    flexDirection: 'column',
                    gap: 1,
                    '&:hover': {
                      backgroundColor: 'primary.main',
                      color: 'white',
                    },
                  }}
                >
                  <Typography variant="button" fontWeight="bold">
                    {action.label}
                  </Typography>
                  <Typography variant="caption" opacity={0.8}>
                    {action.description}
                  </Typography>
                </Button>
              </Grid>
            ))}
          </Grid>
        </Paper>

        {/* Help Section */}
        <Box sx={{ p: 3, backgroundColor: 'background.paper', borderRadius: 2 }}>
          <Typography variant="h6" gutterBottom>
            Still need help?
          </Typography>
          <Typography variant="body2" color="text.secondary" paragraph>
            If you believe this is an error or you were expecting to find something here,
            please don't hesitate to contact our support team.
          </Typography>
          <Box display="flex" justifyContent="center" gap={2} flexWrap="wrap">
            <Button
              variant="contained"
              onClick={() => navigate('/support')}
            >
              Contact Support
            </Button>
            <Button
              variant="outlined"
              onClick={() => navigate('/contact')}
            >
              Send Feedback
            </Button>
          </Box>
        </Box>

        {/* Error Code for Technical Support */}
        <Box mt={4}>
          <Typography variant="caption" color="text.secondary">
            Error Code: 404 | Page Not Found | {new Date().toISOString()}
          </Typography>
        </Box>
      </Box>
    </Container>
  );
};

export default NotFound;