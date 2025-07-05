import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Container,
  Paper,
  Typography,
  TextField,
  Button,
  Grid,
  Card,
  CardContent,
  CardActions,
  Stepper,
  Step,
  StepLabel,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
  Alert,
  Divider,
  IconButton,
  Tooltip,
  Collapse,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Switch,
  FormControlLabel,
  CircularProgress,
} from '@mui/material';
import {
  LocationOn,
  LocalShipping,
  Schedule,
  AttachMoney,
  Inbox,
  ExpandMore,
  ExpandLess,
  Info,
  Star,
  Speed,
  Security,
  CheckCircle,
  Compare,
  Save,
  ShoppingCart,
} from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';
import { useNotification } from '../../contexts/NotificationContext';
import { shipmentService, ShipmentQuoteRequest, ShipmentQuote } from '../../services';

const ServiceQuote: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { showNotification } = useNotification();
  
  const [activeStep, setActiveStep] = useState(0);
  const [loading, setLoading] = useState(false);
  const [quotes, setQuotes] = useState<ShipmentQuote[]>([]);
  const [selectedQuote, setSelectedQuote] = useState<ShipmentQuote | null>(null);
  const [expandedQuote, setExpandedQuote] = useState<string | null>(null);
  const [savedQuotes, setSavedQuotes] = useState<Set<string>>(new Set());
  
  const [quoteRequest, setQuoteRequest] = useState<ShipmentQuoteRequest>({
    origin: {
      address: '',
      city: '',
      state: '',
      zipCode: '',
      country: 'US',
    },
    destination: {
      address: '',
      city: '',
      state: '',
      zipCode: '',
      country: 'US',
    },
    package: {
      weight: 1,
      dimensions: {
        length: 10,
        width: 10,
        height: 10,
      },
      type: 'package',
      value: 100,
      description: '',
    },
    serviceType: 'standard',
    pickupDate: '',
    insurance: false,
    signatureRequired: false,
  });

  const steps = ['Package Details', 'Addresses', 'Service Options', 'Compare Quotes'];

  const packageTypes = [
    { value: 'document', label: 'Document', icon: '📄' },
    { value: 'package', label: 'Package', icon: '📦' },
    { value: 'fragile', label: 'Fragile', icon: '🔮' },
    { value: 'hazardous', label: 'Hazardous', icon: '⚠️' },
  ];

  const serviceTypes = [
    {
      value: 'standard',
      label: 'Standard Delivery',
      description: '3-5 business days',
      icon: <LocalShipping />,
      features: ['Ground shipping', 'Basic tracking', 'Signature on delivery'],
    },
    {
      value: 'express',
      label: 'Express Delivery',
      description: '1-2 business days',
      icon: <Speed />,
      features: ['Priority handling', 'Real-time tracking', 'Signature required'],
    },
    {
      value: 'overnight',
      label: 'Overnight Delivery',
      description: 'Next business day',
      icon: <Schedule />,
      features: ['Next day delivery', 'Premium tracking', 'Morning delivery'],
    },
    {
      value: 'same_day',
      label: 'Same Day Delivery',
      description: 'Within 24 hours',
      icon: <Star />,
      features: ['Same day delivery', 'Live tracking', 'Direct courier'],
    },
  ];

  useEffect(() => {
    // Set default pickup date to tomorrow
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    setQuoteRequest(prev => ({
      ...prev,
      pickupDate: tomorrow.toISOString().split('T')[0],
    }));
  }, []);

  const validateStep = (step: number): boolean => {
    switch (step) {
      case 0:
        return quoteRequest.package.weight > 0 && 
               quoteRequest.package.dimensions.length > 0 &&
               quoteRequest.package.dimensions.width > 0 &&
               quoteRequest.package.dimensions.height > 0;
      case 1:
        return quoteRequest.origin.address && quoteRequest.origin.city && 
               quoteRequest.origin.state && quoteRequest.origin.zipCode &&
               quoteRequest.destination.address && quoteRequest.destination.city && 
               quoteRequest.destination.state && quoteRequest.destination.zipCode;
      case 2:
        return quoteRequest.serviceType && quoteRequest.pickupDate;
      default:
        return true;
    }
  };

  const handleNext = () => {
    if (validateStep(activeStep)) {
      if (activeStep === 2) {
        handleGetQuotes();
      } else {
        setActiveStep(prev => prev + 1);
      }
    } else {
      showNotification('Please fill in all required fields', 'warning');
    }
  };

  const handleBack = () => {
    setActiveStep(prev => prev - 1);
  };

  const handleGetQuotes = async () => {
    setLoading(true);
    try {
      const response = await shipmentService.getQuote(quoteRequest);
      if (response.success && response.data) {
        setQuotes(response.data);
        setActiveStep(3);
        showNotification('Quotes retrieved successfully!', 'success');
      } else {
        showNotification(response.error || 'Failed to get quotes', 'error');
      }
    } catch (error: any) {
      showNotification(error.error || 'Failed to get quotes', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleSaveQuote = async (quote: ShipmentQuote) => {
    try {
      const response = await shipmentService.saveQuote(quote.id);
      if (response.success) {
        setSavedQuotes(prev => new Set([...prev, quote.id]));
        showNotification('Quote saved successfully!', 'success');
      }
    } catch (error: any) {
      showNotification(error.error || 'Failed to save quote', 'error');
    }
  };

  const handleBookShipment = (quote: ShipmentQuote) => {
    // Navigate to booking page with quote data
    navigate('/book', { state: { quote, quoteRequest } });
  };

  const renderStepContent = () => {
    switch (activeStep) {
      case 0:
        return (
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <Typography variant="h6" gutterBottom>
                Package Information
              </Typography>
            </Grid>
            
            <Grid item xs={12} md={6}>
              <FormControl fullWidth>
                <InputLabel>Package Type</InputLabel>
                <Select
                  value={quoteRequest.package.type}
                  label="Package Type"
                  onChange={(e) => setQuoteRequest(prev => ({
                    ...prev,
                    package: { ...prev.package, type: e.target.value as any }
                  }))}
                >
                  {packageTypes.map(type => (
                    <MenuItem key={type.value} value={type.value}>
                      <Box display="flex" alignItems="center" gap={1}>
                        <span>{type.icon}</span>
                        {type.label}
                      </Box>
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="Weight (lbs)"
                type="number"
                value={quoteRequest.package.weight}
                onChange={(e) => setQuoteRequest(prev => ({
                  ...prev,
                  package: { ...prev.package, weight: parseFloat(e.target.value) || 0 }
                }))}
                inputProps={{ min: 0.1, step: 0.1 }}
              />
            </Grid>
            
            <Grid item xs={12}>
              <Typography variant="subtitle2" gutterBottom>
                Dimensions (inches)
              </Typography>
            </Grid>
            
            <Grid item xs={4}>
              <TextField
                fullWidth
                label="Length"
                type="number"
                value={quoteRequest.package.dimensions.length}
                onChange={(e) => setQuoteRequest(prev => ({
                  ...prev,
                  package: {
                    ...prev.package,
                    dimensions: {
                      ...prev.package.dimensions,
                      length: parseFloat(e.target.value) || 0
                    }
                  }
                }))}
                inputProps={{ min: 1, step: 0.1 }}
              />
            </Grid>
            
            <Grid item xs={4}>
              <TextField
                fullWidth
                label="Width"
                type="number"
                value={quoteRequest.package.dimensions.width}
                onChange={(e) => setQuoteRequest(prev => ({
                  ...prev,
                  package: {
                    ...prev.package,
                    dimensions: {
                      ...prev.package.dimensions,
                      width: parseFloat(e.target.value) || 0
                    }
                  }
                }))}
                inputProps={{ min: 1, step: 0.1 }}
              />
            </Grid>
            
            <Grid item xs={4}>
              <TextField
                fullWidth
                label="Height"
                type="number"
                value={quoteRequest.package.dimensions.height}
                onChange={(e) => setQuoteRequest(prev => ({
                  ...prev,
                  package: {
                    ...prev.package,
                    dimensions: {
                      ...prev.package.dimensions,
                      height: parseFloat(e.target.value) || 0
                    }
                  }
                }))}
                inputProps={{ min: 1, step: 0.1 }}
              />
            </Grid>
            
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="Declared Value ($)"
                type="number"
                value={quoteRequest.package.value}
                onChange={(e) => setQuoteRequest(prev => ({
                  ...prev,
                  package: { ...prev.package, value: parseFloat(e.target.value) || 0 }
                }))}
                inputProps={{ min: 0, step: 0.01 }}
              />
            </Grid>
            
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="Package Description"
                value={quoteRequest.package.description}
                onChange={(e) => setQuoteRequest(prev => ({
                  ...prev,
                  package: { ...prev.package, description: e.target.value }
                }))}
                placeholder="Brief description of contents"
              />
            </Grid>
          </Grid>
        );

      case 1:
        return (
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <Typography variant="h6" gutterBottom>
                <LocationOn sx={{ mr: 1, verticalAlign: 'middle' }} />
                Pickup Address
              </Typography>
            </Grid>
            
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Street Address"
                value={quoteRequest.origin.address}
                onChange={(e) => setQuoteRequest(prev => ({
                  ...prev,
                  origin: { ...prev.origin, address: e.target.value }
                }))}
                required
              />
            </Grid>
            
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="City"
                value={quoteRequest.origin.city}
                onChange={(e) => setQuoteRequest(prev => ({
                  ...prev,
                  origin: { ...prev.origin, city: e.target.value }
                }))}
                required
              />
            </Grid>
            
            <Grid item xs={6} md={3}>
              <TextField
                fullWidth
                label="State"
                value={quoteRequest.origin.state}
                onChange={(e) => setQuoteRequest(prev => ({
                  ...prev,
                  origin: { ...prev.origin, state: e.target.value }
                }))}
                required
              />
            </Grid>
            
            <Grid item xs={6} md={3}>
              <TextField
                fullWidth
                label="ZIP Code"
                value={quoteRequest.origin.zipCode}
                onChange={(e) => setQuoteRequest(prev => ({
                  ...prev,
                  origin: { ...prev.origin, zipCode: e.target.value }
                }))}
                required
              />
            </Grid>
            
            <Grid item xs={12} sx={{ mt: 2 }}>
              <Divider />
            </Grid>
            
            <Grid item xs={12}>
              <Typography variant="h6" gutterBottom>
                <LocalShipping sx={{ mr: 1, verticalAlign: 'middle' }} />
                Delivery Address
              </Typography>
            </Grid>
            
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Street Address"
                value={quoteRequest.destination.address}
                onChange={(e) => setQuoteRequest(prev => ({
                  ...prev,
                  destination: { ...prev.destination, address: e.target.value }
                }))}
                required
              />
            </Grid>
            
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="City"
                value={quoteRequest.destination.city}
                onChange={(e) => setQuoteRequest(prev => ({
                  ...prev,
                  destination: { ...prev.destination, city: e.target.value }
                }))}
                required
              />
            </Grid>
            
            <Grid item xs={6} md={3}>
              <TextField
                fullWidth
                label="State"
                value={quoteRequest.destination.state}
                onChange={(e) => setQuoteRequest(prev => ({
                  ...prev,
                  destination: { ...prev.destination, state: e.target.value }
                }))}
                required
              />
            </Grid>
            
            <Grid item xs={6} md={3}>
              <TextField
                fullWidth
                label="ZIP Code"
                value={quoteRequest.destination.zipCode}
                onChange={(e) => setQuoteRequest(prev => ({
                  ...prev,
                  destination: { ...prev.destination, zipCode: e.target.value }
                }))}
                required
              />
            </Grid>
          </Grid>
        );

      case 2:
        return (
          <Box>
            <Typography variant="h6" gutterBottom>
              Service Options
            </Typography>
            
            <Grid container spacing={2} sx={{ mb: 3 }}>
              {serviceTypes.map(service => (
                <Grid item xs={12} md={6} key={service.value}>
                  <Card
                    sx={{
                      cursor: 'pointer',
                      border: quoteRequest.serviceType === service.value ? 2 : 1,
                      borderColor: quoteRequest.serviceType === service.value ? 'primary.main' : 'divider',
                    }}
                    onClick={() => setQuoteRequest(prev => ({
                      ...prev,
                      serviceType: service.value as any
                    }))}
                  >
                    <CardContent>
                      <Box display="flex" alignItems="center" gap={2} mb={2}>
                        {service.icon}
                        <Box>
                          <Typography variant="h6">{service.label}</Typography>
                          <Typography variant="body2" color="text.secondary">
                            {service.description}
                          </Typography>
                        </Box>
                      </Box>
                      <List dense>
                        {service.features.map((feature, index) => (
                          <ListItem key={index} sx={{ px: 0 }}>
                            <ListItemIcon sx={{ minWidth: 32 }}>
                              <CheckCircle color="success" fontSize="small" />
                            </ListItemIcon>
                            <ListItemText primary={feature} />
                          </ListItem>
                        ))}
                      </List>
                    </CardContent>
                  </Card>
                </Grid>
              ))}
            </Grid>
            
            <Grid container spacing={3} sx={{ mt: 2 }}>
              <Grid item xs={12} md={6}>
                <TextField
                  fullWidth
                  label="Pickup Date"
                  type="date"
                  value={quoteRequest.pickupDate}
                  onChange={(e) => setQuoteRequest(prev => ({
                    ...prev,
                    pickupDate: e.target.value
                  }))}
                  InputLabelProps={{ shrink: true }}
                  inputProps={{
                    min: new Date().toISOString().split('T')[0]
                  }}
                  required
                />
              </Grid>
              
              <Grid item xs={12}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={quoteRequest.insurance}
                      onChange={(e) => setQuoteRequest(prev => ({
                        ...prev,
                        insurance: e.target.checked
                      }))}
                    />
                  }
                  label={
                    <Box display="flex" alignItems="center" gap={1}>
                      <Security />
                      <span>Add shipping insurance</span>
                      <Tooltip title="Protects your package value in case of loss or damage">
                        <Info fontSize="small" color="action" />
                      </Tooltip>
                    </Box>
                  }
                />
              </Grid>
              
              <Grid item xs={12}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={quoteRequest.signatureRequired}
                      onChange={(e) => setQuoteRequest(prev => ({
                        ...prev,
                        signatureRequired: e.target.checked
                      }))}
                    />
                  }
                  label="Require signature upon delivery"
                />
              </Grid>
            </Grid>
          </Box>
        );

      case 3:
        return (
          <Box>
            <Typography variant="h6" gutterBottom>
              Available Shipping Options
            </Typography>
            
            {quotes.length === 0 ? (
              <Alert severity="info">
                No quotes available for your shipment. Please try adjusting your package details or service options.
              </Alert>
            ) : (
              <Grid container spacing={2}>
                {quotes.map((quote) => (
                  <Grid item xs={12} key={quote.id}>
                    <Card
                      sx={{
                        border: selectedQuote?.id === quote.id ? 2 : 1,
                        borderColor: selectedQuote?.id === quote.id ? 'primary.main' : 'divider',
                      }}
                    >
                      <CardContent>
                        <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
                          <Box>
                            <Typography variant="h6" gutterBottom>
                              {quote.serviceType.replace('_', ' ').toUpperCase()}
                            </Typography>
                            <Box display="flex" alignItems="center" gap={2}>
                              <Chip
                                icon={<Schedule />}
                                label={quote.transitTime}
                                size="small"
                                color="primary"
                                variant="outlined"
                              />
                              <Typography variant="body2" color="text.secondary">
                                Est. Delivery: {new Date(quote.estimatedDelivery).toLocaleDateString()}
                              </Typography>
                            </Box>
                          </Box>
                          
                          <Box textAlign="right">
                            <Typography variant="h4" color="primary" fontWeight="bold">
                              ${quote.totalPrice.toFixed(2)}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                              {quote.currency}
                            </Typography>
                          </Box>
                        </Box>
                        
                        <Box display="flex" justifyContent="space-between" alignItems="center">
                          <Button
                            onClick={() => setExpandedQuote(
                              expandedQuote === quote.id ? null : quote.id
                            )}
                            endIcon={expandedQuote === quote.id ? <ExpandLess /> : <ExpandMore />}
                          >
                            Price Breakdown
                          </Button>
                          
                          <Box display="flex" gap={1}>
                            <Tooltip title="Save quote">
                              <IconButton
                                onClick={() => handleSaveQuote(quote)}
                                color={savedQuotes.has(quote.id) ? "primary" : "default"}
                              >
                                <Save />
                              </IconButton>
                            </Tooltip>
                            <Button
                              variant="outlined"
                              onClick={() => setSelectedQuote(quote)}
                            >
                              <Compare sx={{ mr: 1 }} />
                              Compare
                            </Button>
                          </Box>
                        </Box>
                        
                        <Collapse in={expandedQuote === quote.id}>
                          <Box mt={2} p={2} bgcolor="grey.50" borderRadius={1}>
                            <Typography variant="subtitle2" gutterBottom>
                              Price Breakdown
                            </Typography>
                            <Grid container spacing={2}>
                              <Grid item xs={6}>
                                <Typography variant="body2">Base Rate:</Typography>
                              </Grid>
                              <Grid item xs={6}>
                                <Typography variant="body2" textAlign="right">
                                  ${quote.breakdown.baseRate.toFixed(2)}
                                </Typography>
                              </Grid>
                              <Grid item xs={6}>
                                <Typography variant="body2">Fuel Surcharge:</Typography>
                              </Grid>
                              <Grid item xs={6}>
                                <Typography variant="body2" textAlign="right">
                                  ${quote.breakdown.fuelSurcharge.toFixed(2)}
                                </Typography>
                              </Grid>
                              {quote.breakdown.insurance && (
                                <>
                                  <Grid item xs={6}>
                                    <Typography variant="body2">Insurance:</Typography>
                                  </Grid>
                                  <Grid item xs={6}>
                                    <Typography variant="body2" textAlign="right">
                                      ${quote.breakdown.insurance.toFixed(2)}
                                    </Typography>
                                  </Grid>
                                </>
                              )}
                              <Grid item xs={6}>
                                <Typography variant="body2">Taxes & Fees:</Typography>
                              </Grid>
                              <Grid item xs={6}>
                                <Typography variant="body2" textAlign="right">
                                  ${(quote.breakdown.taxes + quote.breakdown.additionalFees).toFixed(2)}
                                </Typography>
                              </Grid>
                              <Grid item xs={12}>
                                <Divider />
                              </Grid>
                              <Grid item xs={6}>
                                <Typography variant="subtitle2" fontWeight="bold">Total:</Typography>
                              </Grid>
                              <Grid item xs={6}>
                                <Typography variant="subtitle2" fontWeight="bold" textAlign="right">
                                  ${quote.totalPrice.toFixed(2)}
                                </Typography>
                              </Grid>
                            </Grid>
                          </Box>
                        </Collapse>
                      </CardContent>
                      
                      <CardActions>
                        <Button
                          fullWidth
                          variant="contained"
                          startIcon={<ShoppingCart />}
                          onClick={() => handleBookShipment(quote)}
                          size="large"
                        >
                          Book This Shipment
                        </Button>
                      </CardActions>
                    </Card>
                  </Grid>
                ))}
              </Grid>
            )}
          </Box>
        );

      default:
        return null;
    }
  };

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Typography variant="h4" fontWeight="bold" gutterBottom align="center">
        Get Shipping Quote
      </Typography>
      <Typography variant="body1" color="text.secondary" align="center" paragraph>
        Compare shipping options and get instant quotes for your package
      </Typography>

      {/* Progress Stepper */}
      <Paper sx={{ p: 3, mb: 4 }}>
        <Stepper activeStep={activeStep} alternativeLabel>
          {steps.map((label) => (
            <Step key={label}>
              <StepLabel>{label}</StepLabel>
            </Step>
          ))}
        </Stepper>
      </Paper>

      {/* Step Content */}
      <Paper sx={{ p: 4, mb: 3 }}>
        {renderStepContent()}
      </Paper>

      {/* Navigation Buttons */}
      <Box display="flex" justifyContent="space-between">
        <Button
          disabled={activeStep === 0}
          onClick={handleBack}
          variant="outlined"
          size="large"
        >
          Back
        </Button>
        
        <Box display="flex" gap={2}>
          {activeStep < 2 && (
            <Button
              onClick={handleNext}
              variant="contained"
              size="large"
            >
              Next
            </Button>
          )}
          
          {activeStep === 2 && (
            <Button
              onClick={handleNext}
              variant="contained"
              size="large"
              disabled={loading}
              startIcon={loading ? <CircularProgress size={20} /> : <Search />}
            >
              {loading ? 'Getting Quotes...' : 'Get Quotes'}
            </Button>
          )}
          
          {activeStep === 3 && (
            <Button
              onClick={() => setActiveStep(0)}
              variant="outlined"
              size="large"
            >
              New Quote
            </Button>
          )}
        </Box>
      </Box>
    </Container>
  );
};

export default ServiceQuote;