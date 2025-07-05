import React from 'react';
import {
  Box,
  Container,
  Typography,
  Button,
  Grid,
  Card,
  CardContent,
  CardActions,
  Paper,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Chip,
} from '@mui/material';
import {
  LocalShipping,
  Schedule,
  Security,
  Support,
  BusinessCenter,
  TrackChanges,
  Payment,
  Star,
  CheckCircle,
  TrendingUp,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import HeroSection from '../components/home/HeroSection';
import ServiceCard from '../components/home/ServiceCard';
import TestimonialCard from '../components/home/TestimonialCard';
import StatsSection from '../components/home/StatsSection';

interface Service {
  icon: React.ReactNode;
  title: string;
  description: string;
  features: string[];
  cta: string;
  ctaLink: string;
}

interface Testimonial {
  name: string;
  company: string;
  content: string;
  rating: number;
  avatar?: string;
}

const Home: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  const services: Service[] = [
    {
      icon: <LocalShipping sx={{ fontSize: 40, color: 'primary.main' }} />,
      title: 'Express Delivery',
      description: 'Fast and reliable delivery services for urgent shipments with real-time tracking.',
      features: ['Same-day delivery', 'Real-time tracking', 'Signature confirmation', 'Insurance included'],
      cta: 'Book Express',
      ctaLink: '/book',
    },
    {
      icon: <BusinessCenter sx={{ fontSize: 40, color: 'secondary.main' }} />,
      title: 'Corporate Solutions',
      description: 'Tailored shipping solutions for businesses with volume discounts and dedicated support.',
      features: ['Volume discounts', 'Dedicated account manager', 'API integration', 'Custom reporting'],
      cta: 'Learn More',
      ctaLink: '/corporate/onboarding',
    },
    {
      icon: <TrackChanges sx={{ fontSize: 40, color: 'success.main' }} />,
      title: 'Package Tracking',
      description: 'Track your shipments in real-time with detailed status updates and delivery notifications.',
      features: ['Live GPS tracking', 'SMS/Email alerts', 'Delivery photos', 'Delivery time prediction'],
      cta: 'Track Package',
      ctaLink: '/track',
    },
  ];

  const testimonials: Testimonial[] = [
    {
      name: 'Sarah Johnson',
      company: 'TechStart Inc.',
      content: 'Exalt Courier has been our go-to shipping partner for over two years. Their reliability and customer service are outstanding.',
      rating: 5,
    },
    {
      name: 'Michael Chen',
      company: 'E-Commerce Plus',
      content: 'The real-time tracking and API integration have streamlined our entire fulfillment process. Highly recommended!',
      rating: 5,
    },
    {
      name: 'Lisa Rodriguez',
      company: 'Artisan Crafts',
      content: 'As a small business owner, I appreciate the competitive pricing and personal attention to every shipment.',
      rating: 5,
    },
  ];

  const features = [
    {
      icon: <Security color="primary" />,
      text: 'Secure and insured shipping',
    },
    {
      icon: <Schedule color="primary" />,
      text: '24/7 customer support',
    },
    {
      icon: <Payment color="primary" />,
      text: 'Flexible payment options',
    },
    {
      icon: <TrendingUp color="primary" />,
      text: 'Competitive pricing',
    },
  ];

  const handleGetStarted = () => {
    if (isAuthenticated) {
      navigate('/dashboard');
    } else {
      navigate('/register');
    }
  };

  const handleQuickQuote = () => {
    navigate('/quote');
  };

  return (
    <Box>
      {/* Hero Section */}
      <HeroSection
        title="Ship with Confidence"
        subtitle="Fast, reliable, and secure courier services for individuals and businesses"
        ctaPrimary={{
          text: isAuthenticated ? 'Go to Dashboard' : 'Get Started',
          onClick: handleGetStarted,
        }}
        ctaSecondary={{
          text: 'Get Quote',
          onClick: handleQuickQuote,
        }}
        backgroundImage="/images/hero-courier.jpg"
      />

      {/* Stats Section */}
      <StatsSection
        stats={[
          { value: '50K+', label: 'Packages Delivered' },
          { value: '99.8%', label: 'On-Time Delivery' },
          { value: '24/7', label: 'Customer Support' },
          { value: '100+', label: 'Cities Covered' },
        ]}
      />

      {/* Services Section */}
      <Container maxWidth="lg" sx={{ py: 8 }}>
        <Box textAlign="center" mb={6}>
          <Typography variant="h3" fontWeight="bold" gutterBottom>
            Our Services
          </Typography>
          <Typography variant="h6" color="text.secondary" maxWidth={600} mx="auto">
            Comprehensive shipping solutions designed to meet your unique needs
          </Typography>
        </Box>

        <Grid container spacing={4}>
          {services.map((service, index) => (
            <Grid item xs={12} md={4} key={index}>
              <ServiceCard
                icon={service.icon}
                title={service.title}
                description={service.description}
                features={service.features}
                cta={service.cta}
                onCtaClick={() => navigate(service.ctaLink)}
              />
            </Grid>
          ))}
        </Grid>
      </Container>

      {/* Features Section */}
      <Box sx={{ bgcolor: 'grey.50', py: 8 }}>
        <Container maxWidth="lg">
          <Grid container spacing={6} alignItems="center">
            <Grid item xs={12} md={6}>
              <Typography variant="h4" fontWeight="bold" gutterBottom>
                Why Choose Exalt Courier?
              </Typography>
              <Typography variant="body1" color="text.secondary" paragraph>
                We're committed to providing exceptional courier services that exceed your expectations.
                Our advanced technology and dedicated team ensure your packages are handled with care.
              </Typography>
              
              <List>
                {features.map((feature, index) => (
                  <ListItem key={index} sx={{ px: 0 }}>
                    <ListItemIcon>
                      {feature.icon}
                    </ListItemIcon>
                    <ListItemText
                      primary={feature.text}
                      primaryTypographyProps={{ fontWeight: 500 }}
                    />
                  </ListItem>
                ))}
              </List>

              <Box mt={3}>
                <Button
                  variant="contained"
                  size="large"
                  onClick={handleGetStarted}
                  sx={{ mr: 2 }}
                >
                  Start Shipping
                </Button>
                <Button
                  variant="outlined"
                  size="large"
                  onClick={() => navigate('/contact')}
                >
                  Contact Us
                </Button>
              </Box>
            </Grid>

            <Grid item xs={12} md={6}>
              <Box
                component="img"
                src="/images/delivery-truck.jpg"
                alt="Delivery truck"
                sx={{
                  width: '100%',
                  height: 'auto',
                  borderRadius: 2,
                  boxShadow: 3,
                }}
              />
            </Grid>
          </Grid>
        </Container>
      </Box>

      {/* Testimonials Section */}
      <Container maxWidth="lg" sx={{ py: 8 }}>
        <Box textAlign="center" mb={6}>
          <Typography variant="h4" fontWeight="bold" gutterBottom>
            What Our Customers Say
          </Typography>
          <Typography variant="h6" color="text.secondary">
            Don't just take our word for it
          </Typography>
        </Box>

        <Grid container spacing={4}>
          {testimonials.map((testimonial, index) => (
            <Grid item xs={12} md={4} key={index}>
              <TestimonialCard
                name={testimonial.name}
                company={testimonial.company}
                content={testimonial.content}
                rating={testimonial.rating}
                avatar={testimonial.avatar}
              />
            </Grid>
          ))}
        </Grid>
      </Container>

      {/* CTA Section */}
      <Box
        sx={{
          bgcolor: 'primary.main',
          color: 'primary.contrastText',
          py: 8,
          textAlign: 'center',
        }}
      >
        <Container maxWidth="md">
          <Typography variant="h4" fontWeight="bold" gutterBottom>
            Ready to Ship?
          </Typography>
          <Typography variant="h6" sx={{ mb: 4, opacity: 0.9 }}>
            Join thousands of satisfied customers who trust Exalt Courier with their deliveries
          </Typography>
          
          <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center', flexWrap: 'wrap' }}>
            <Button
              variant="contained"
              color="secondary"
              size="large"
              onClick={handleGetStarted}
              startIcon={<LocalShipping />}
            >
              Start Shipping Now
            </Button>
            <Button
              variant="outlined"
              size="large"
              onClick={handleQuickQuote}
              sx={{
                borderColor: 'primary.contrastText',
                color: 'primary.contrastText',
                '&:hover': {
                  borderColor: 'primary.contrastText',
                  bgcolor: 'rgba(255, 255, 255, 0.1)',
                },
              }}
            >
              Get Instant Quote
            </Button>
          </Box>
        </Container>
      </Box>
    </Box>
  );
};

export default Home;