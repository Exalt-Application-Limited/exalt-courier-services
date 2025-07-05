import React from 'react';
import { Box, Container, Typography, Grid, Button, Card, CardContent } from '@mui/material';
import { styled } from '@mui/material/styles';
import { 
  LocalShippingRounded,
  SpeedRounded,
  SecurityRounded,
  TrackChangesRounded,
  ScheduleRounded,
  LocationOnRounded,
  PhoneRounded,
  SearchRounded
} from '@mui/icons-material';
import { Link } from 'react-router-dom';
import HeroSection from '../components/home/HeroSection';
import CourierSearchForm from '../components/search/CourierSearchForm';
import FeaturedCouriers from '../components/home/FeaturedCouriers';
import ServiceTypes from '../components/home/ServiceTypes';
import HowItWorks from '../components/home/HowItWorks';
import CustomerTestimonials from '../components/home/CustomerTestimonials';
import PackageTracker from '../components/tracking/PackageTracker';

const Section = styled(Box)(({ theme }) => ({
  padding: theme.spacing(8, 0),
  [theme.breakpoints.down('sm')]: {
    padding: theme.spacing(4, 0),
  },
}));

const FeatureCard = styled(Card)(({ theme }) => ({
  height: '100%',
  display: 'flex',
  flexDirection: 'column',
  textAlign: 'center',
  padding: theme.spacing(3),
  transition: 'transform 0.3s ease, box-shadow 0.3s ease',
  '&:hover': {
    transform: 'translateY(-4px)',
    boxShadow: theme.shadows[8],
  },
}));

const FeatureIcon = styled(Box)(({ theme }) => ({
  width: 64,
  height: 64,
  borderRadius: '50%',
  background: `linear-gradient(135deg, ${theme.palette.primary.main} 0%, ${theme.palette.primary.dark} 100%)`,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  margin: '0 auto 16px',
  color: 'white',
}));

const StatCard = styled(Box)(({ theme }) => ({
  textAlign: 'center',
  padding: theme.spacing(2),
}));

const HomePage: React.FC = () => {
  const features = [
    {
      icon: <LocalShippingRounded fontSize="large" />,
      title: 'Reliable Delivery',
      description: 'Professional couriers with verified backgrounds and excellent track records.',
    },
    {
      icon: <SpeedRounded fontSize="large" />,
      title: 'Fast Service',
      description: 'Same-day, express, and scheduled delivery options to meet your timeline.',
    },
    {
      icon: <TrackChangesRounded fontSize="large" />,
      title: 'Real-Time Tracking',
      description: 'Track your packages with live GPS updates and delivery notifications.',
    },
    {
      icon: <SecurityRounded fontSize="large" />,
      title: 'Secure & Insured',
      description: 'All deliveries are insured and handled with maximum security protocols.',
    },
  ];

  const stats = [
    { number: '50,000+', label: 'Deliveries Completed' },
    { number: '2,500+', label: 'Verified Couriers' },
    { number: '98%', label: 'On-Time Delivery' },
    { number: '4.9/5', label: 'Customer Rating' },
  ];

  return (
    <Box>
      {/* Hero Section with Search */}
      <Section 
        sx={{ 
          background: 'linear-gradient(135deg, #1976d2 0%, #2196f3 50%, #42a5f5 100%)',
          color: 'white',
          position: 'relative',
          overflow: 'hidden',
          '&::before': {
            content: '""',
            position: 'absolute',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            background: 'url(/images/delivery-hero-bg.jpg) center/cover',
            opacity: 0.1,
            zIndex: 0,
          }
        }}
      >
        <Container sx={{ position: 'relative', zIndex: 1 }}>
          <Box textAlign="center" mb={6}>
            <Typography variant="h2" component="h1" gutterBottom fontWeight={700}>
              Fast, Reliable Courier Services
            </Typography>
            <Typography variant="h5" mb={4} sx={{ opacity: 0.9 }}>
              Find trusted couriers near you for same-day delivery and express shipping
            </Typography>
            <Box display="flex" gap={2} justifyContent="center" flexWrap="wrap">
              <Button
                component={Link}
                to="/quote"
                variant="contained"
                size="large"
                color="secondary"
                startIcon={<SearchRounded />}
              >
                Get Instant Quote
              </Button>
              <Button
                component={Link}
                to="/track"
                variant="outlined"
                size="large"
                startIcon={<TrackChangesRounded />}
                sx={{ 
                  color: 'white', 
                  borderColor: 'white',
                  '&:hover': {
                    backgroundColor: 'rgba(255,255,255,0.1)',
                    borderColor: 'white',
                  }
                }}
              >
                Track Package
              </Button>
            </Box>
          </Box>
          
          {/* Search Form */}
          <Box maxWidth={800} mx="auto">
            <CourierSearchForm />
          </Box>
        </Container>
      </Section>
      
      {/* Stats Section */}
      <Section sx={{ bgcolor: 'background.paper', py: 4 }}>
        <Container>
          <Grid container spacing={4}>
            {stats.map((stat, index) => (
              <Grid item xs={6} md={3} key={index}>
                <StatCard>
                  <Typography variant="h3" color="primary" fontWeight={700}>
                    {stat.number}
                  </Typography>
                  <Typography variant="h6" color="text.secondary">
                    {stat.label}
                  </Typography>
                </StatCard>
              </Grid>
            ))}
          </Grid>
        </Container>
      </Section>
      
      {/* Features Section */}
      <Section>
        <Container>
          <Typography variant="h4" textAlign="center" gutterBottom fontWeight={600}>
            Why Choose Our Courier Services
          </Typography>
          <Typography variant="h6" textAlign="center" color="text.secondary" mb={6}>
            Trusted by thousands of customers and businesses
          </Typography>
          
          <Grid container spacing={4}>
            {features.map((feature, index) => (
              <Grid item xs={12} sm={6} md={3} key={index}>
                <FeatureCard>
                  <CardContent>
                    <FeatureIcon>
                      {feature.icon}
                    </FeatureIcon>
                    <Typography variant="h6" gutterBottom fontWeight={600}>
                      {feature.title}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {feature.description}
                    </Typography>
                  </CardContent>
                </FeatureCard>
              </Grid>
            ))}
          </Grid>
        </Container>
      </Section>
      
      {/* Service Types */}
      <Section sx={{ bgcolor: 'background.paper' }}>
        <Container>
          <Typography variant="h4" textAlign="center" gutterBottom fontWeight={600}>
            Delivery Services
          </Typography>
          <Typography variant="h6" textAlign="center" color="text.secondary" mb={6}>
            Choose the service that fits your needs
          </Typography>
          <ServiceTypes />
        </Container>
      </Section>
      
      {/* Package Tracker */}
      <Section>
        <Container>
          <Typography variant="h4" textAlign="center" gutterBottom fontWeight={600}>
            Track Your Package
          </Typography>
          <Typography variant="h6" textAlign="center" color="text.secondary" mb={6}>
            Enter your tracking number for real-time updates
          </Typography>
          <Box maxWidth={600} mx="auto">
            <PackageTracker />
          </Box>
        </Container>
      </Section>
      
      {/* Featured Couriers */}
      <Section sx={{ bgcolor: 'background.paper' }}>
        <Container>
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
            <Typography variant="h4" fontWeight={600}>
              Top Rated Couriers
            </Typography>
            <Button 
              component={Link} 
              to="/search" 
              endIcon={<SearchRounded />}
              color="primary"
            >
              View All Couriers
            </Button>
          </Box>
          <FeaturedCouriers />
        </Container>
      </Section>
      
      {/* How It Works */}
      <Section>
        <Container>
          <Typography variant="h4" textAlign="center" gutterBottom fontWeight={600}>
            How It Works
          </Typography>
          <Typography variant="h6" textAlign="center" color="text.secondary" mb={6}>
            Simple steps to get your package delivered
          </Typography>
          <HowItWorks />
        </Container>
      </Section>
      
      {/* Customer Testimonials */}
      <Section sx={{ bgcolor: 'background.paper' }}>
        <Container>
          <Typography variant="h4" textAlign="center" gutterBottom fontWeight={600}>
            What Our Customers Say
          </Typography>
          <Typography variant="h6" textAlign="center" color="text.secondary" mb={6}>
            Real reviews from verified customers
          </Typography>
          <CustomerTestimonials />
        </Container>
      </Section>
      
      {/* Call to Action */}
      <Section 
        sx={{ 
          bgcolor: 'primary.main',
          color: 'white',
        }}
      >
        <Container>
          <Box textAlign="center">
            <Typography variant="h4" gutterBottom fontWeight={600}>
              Ready to Ship?
            </Typography>
            <Typography variant="h6" mb={4} sx={{ opacity: 0.9 }}>
              Get instant quotes and book reliable courier services
            </Typography>
            <Box display="flex" gap={2} justifyContent="center" flexWrap="wrap">
              <Button
                component={Link}
                to="/quote"
                variant="contained"
                size="large"
                color="secondary"
                startIcon={<SearchRounded />}
              >
                Get Quote Now
              </Button>
              <Button
                component={Link}
                to="/search"
                variant="outlined"
                size="large"
                startIcon={<LocalShippingRounded />}
                sx={{ 
                  color: 'white', 
                  borderColor: 'white',
                  '&:hover': {
                    backgroundColor: 'rgba(255,255,255,0.1)',
                    borderColor: 'white',
                  }
                }}
              >
                Find Couriers
              </Button>
            </Box>
            
            {/* Contact Info */}
            <Box mt={4} display="flex" justifyContent="center" gap={4} flexWrap="wrap">
              <Box display="flex" alignItems="center" gap={1}>
                <PhoneRounded />
                <Typography variant="body1">1-800-COURIER</Typography>
              </Box>
              <Box display="flex" alignItems="center" gap={1}>
                <ScheduleRounded />
                <Typography variant="body1">24/7 Support</Typography>
              </Box>
              <Box display="flex" alignItems="center" gap={1}>
                <LocationOnRounded />
                <Typography variant="body1">Nationwide Coverage</Typography>
              </Box>
            </Box>
          </Box>
        </Container>
      </Section>
    </Box>
  );
};

export default HomePage;