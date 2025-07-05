import React, { useState } from 'react';
import {
  Box,
  Container,
  Paper,
  Typography,
  Stepper,
  Step,
  StepLabel,
  Button,
  Grid,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormControlLabel,
  Checkbox,
  Card,
  CardContent,
  Divider,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Chip,
  IconButton,
  InputAdornment,
} from '@mui/material';
import {
  LocationOn,
  Schedule,
  LocalShipping,
  Payment,
  CheckCircle,
  Add,
  Remove,
  Calculator,
  Save,
  Print,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useNotification } from '../../contexts/NotificationContext';
import AddressAutocomplete from '../../components/forms/AddressAutocomplete';
import PackageDimensions from '../../components/forms/PackageDimensions';
import ServiceOptions from '../../components/forms/ServiceOptions';
import PricingCalculator from '../../components/forms/PricingCalculator';
import PaymentMethodSelector from '../../components/forms/PaymentMethodSelector';

interface Address {
  street: string;
  city: string;
  state: string;
  zipCode: string;
  country: string;
  contactName: string;
  contactPhone: string;
  contactEmail: string;
  company?: string;
  instructions?: string;
}

interface Package {
  id: string;
  weight: number;
  length: number;
  width: number;
  height: number;
  description: string;
  value: number;
  fragile: boolean;
}

interface ServiceOption {
  id: string;
  name: string;
  description: string;
  price: number;
  estimatedDays: string;
  features: string[];
}

interface ShipmentData {
  senderAddress: Address;
  recipientAddress: Address;
  packages: Package[];
  serviceType: string;
  pickupDate: string;
  deliveryInstructions: string;
  signatureRequired: boolean;
  insuranceRequested: boolean;
  insuranceValue: number;
  paymentMethodId: string;
}

const steps = ['Addresses', 'Package Details', 'Service Options', 'Payment', 'Confirmation'];

const ShipmentBooking: React.FC = () => {
  const navigate = useNavigate();
  const { showNotification } = useNotification();
  
  const [activeStep, setActiveStep] = useState(0);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<{ [key: string]: string }>({});
  const [showPricing, setShowPricing] = useState(false);
  const [calculatedPrice, setCalculatedPrice] = useState<number | null>(null);
  
  const [shipmentData, setShipmentData] = useState<ShipmentData>({
    senderAddress: {
      street: '',
      city: '',
      state: '',
      zipCode: '',
      country: 'US',
      contactName: '',
      contactPhone: '',
      contactEmail: '',
      company: '',
      instructions: '',
    },
    recipientAddress: {
      street: '',
      city: '',
      state: '',
      zipCode: '',
      country: 'US',
      contactName: '',
      contactPhone: '',
      contactEmail: '',
      company: '',
      instructions: '',
    },
    packages: [
      {
        id: '1',
        weight: 0,
        length: 0,
        width: 0,
        height: 0,
        description: '',
        value: 0,
        fragile: false,
      },
    ],
    serviceType: '',
    pickupDate: '',
    deliveryInstructions: '',
    signatureRequired: false,
    insuranceRequested: false,
    insuranceValue: 0,
    paymentMethodId: '',
  });

  const serviceOptions: ServiceOption[] = [
    {
      id: 'express',
      name: 'Express Delivery',
      description: 'Next business day delivery',
      price: 25.99,
      estimatedDays: '1 business day',
      features: ['Real-time tracking', 'Signature required', 'Insurance included'],
    },
    {
      id: 'standard',
      name: 'Standard Delivery',
      description: 'Reliable delivery in 2-3 business days',
      price: 12.99,
      estimatedDays: '2-3 business days',
      features: ['Real-time tracking', 'Basic insurance'],
    },
    {
      id: 'economy',
      name: 'Economy Delivery',
      description: 'Cost-effective delivery in 5-7 business days',
      price: 7.99,
      estimatedDays: '5-7 business days',
      features: ['Basic tracking'],
    },
  ];

  const handleNext = () => {
    if (validateStep(activeStep)) {
      setActiveStep((prevActiveStep) => prevActiveStep + 1);
    }
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const validateStep = (step: number): boolean => {
    const newErrors: { [key: string]: string } = {};
    
    switch (step) {
      case 0: // Addresses
        if (!shipmentData.senderAddress.street) newErrors.senderStreet = 'Sender address is required';
        if (!shipmentData.senderAddress.contactName) newErrors.senderName = 'Sender name is required';
        if (!shipmentData.recipientAddress.street) newErrors.recipientStreet = 'Recipient address is required';
        if (!shipmentData.recipientAddress.contactName) newErrors.recipientName = 'Recipient name is required';
        break;
      case 1: // Package Details
        shipmentData.packages.forEach((pkg, index) => {
          if (pkg.weight <= 0) newErrors[`package${index}Weight`] = 'Weight is required';
          if (!pkg.description) newErrors[`package${index}Description`] = 'Description is required';
        });
        break;
      case 2: // Service Options
        if (!shipmentData.serviceType) newErrors.serviceType = 'Please select a service option';
        if (!shipmentData.pickupDate) newErrors.pickupDate = 'Pickup date is required';
        break;
      case 3: // Payment
        if (!shipmentData.paymentMethodId) newErrors.paymentMethod = 'Please select a payment method';
        break;
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleInputChange = (section: keyof ShipmentData, field: string, value: any) => {
    setShipmentData(prev => ({
      ...prev,
      [section]: {
        ...prev[section],
        [field]: value,
      },
    }));
  };

  const handlePackageChange = (packageId: string, field: string, value: any) => {
    setShipmentData(prev => ({
      ...prev,
      packages: prev.packages.map(pkg =>
        pkg.id === packageId ? { ...pkg, [field]: value } : pkg
      ),
    }));
  };

  const addPackage = () => {
    const newPackage: Package = {
      id: Date.now().toString(),
      weight: 0,
      length: 0,
      width: 0,
      height: 0,
      description: '',
      value: 0,
      fragile: false,
    };
    
    setShipmentData(prev => ({
      ...prev,
      packages: [...prev.packages, newPackage],
    }));
  };

  const removePackage = (packageId: string) => {
    if (shipmentData.packages.length > 1) {
      setShipmentData(prev => ({
        ...prev,
        packages: prev.packages.filter(pkg => pkg.id !== packageId),
      }));
    }
  };

  const calculatePricing = async () => {
    setLoading(true);
    try {
      // Simulate pricing calculation
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      const selectedService = serviceOptions.find(s => s.id === shipmentData.serviceType);
      if (selectedService) {
        const totalWeight = shipmentData.packages.reduce((sum, pkg) => sum + pkg.weight, 0);
        const basePrice = selectedService.price;
        const weightSurcharge = Math.max(0, (totalWeight - 5) * 2); // $2 per lb over 5 lbs
        const insurancePrice = shipmentData.insuranceRequested ? shipmentData.insuranceValue * 0.01 : 0;
        
        const total = basePrice + weightSurcharge + insurancePrice;
        setCalculatedPrice(total);
        setShowPricing(true);
      }
    } catch (error) {
      showNotification('Failed to calculate pricing', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async () => {
    setLoading(true);
    try {
      // Simulate shipment creation
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      const trackingNumber = `EXL${Date.now().toString().slice(-8)}`;
      
      showNotification('Shipment booked successfully!', 'success');
      navigate(`/shipments/${trackingNumber}`);
    } catch (error) {
      showNotification('Failed to book shipment', 'error');
    } finally {
      setLoading(false);
    }
  };

  const renderStepContent = (step: number) => {
    switch (step) {
      case 0:
        return (
          <Grid container spacing={3}>
            {/* Sender Address */}
            <Grid item xs={12} md={6}>
              <Typography variant="h6" gutterBottom>
                <LocationOn sx={{ mr: 1, verticalAlign: 'middle' }} />
                Sender Information
              </Typography>
              <AddressAutocomplete
                address={shipmentData.senderAddress}
                onChange={(field, value) => handleInputChange('senderAddress', field, value)}
                errors={errors}
                prefix="sender"
              />
            </Grid>
            
            {/* Recipient Address */}
            <Grid item xs={12} md={6}>
              <Typography variant="h6" gutterBottom>
                <LocationOn sx={{ mr: 1, verticalAlign: 'middle' }} />
                Recipient Information
              </Typography>
              <AddressAutocomplete
                address={shipmentData.recipientAddress}
                onChange={(field, value) => handleInputChange('recipientAddress', field, value)}
                errors={errors}
                prefix="recipient"
              />
            </Grid>
          </Grid>
        );
        
      case 1:
        return (
          <Box>
            <Typography variant="h6" gutterBottom>
              Package Details
            </Typography>
            
            {shipmentData.packages.map((pkg, index) => (
              <Card key={pkg.id} sx={{ mb: 2 }}>
                <CardContent>
                  <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                    <Typography variant="subtitle1" fontWeight="bold">
                      Package {index + 1}
                    </Typography>
                    {shipmentData.packages.length > 1 && (
                      <IconButton
                        onClick={() => removePackage(pkg.id)}
                        color="error"
                        size="small"
                      >
                        <Remove />
                      </IconButton>
                    )}
                  </Box>
                  
                  <PackageDimensions
                    package={pkg}
                    onChange={(field, value) => handlePackageChange(pkg.id, field, value)}
                    errors={errors}
                    index={index}
                  />
                </CardContent>
              </Card>
            ))}
            
            <Button
              startIcon={<Add />}
              onClick={addPackage}
              variant="outlined"
              sx={{ mt: 2 }}
            >
              Add Another Package
            </Button>
          </Box>
        );
        
      case 2:
        return (
          <Box>
            <ServiceOptions
              options={serviceOptions}
              selectedService={shipmentData.serviceType}
              onServiceSelect={(serviceId) => handleInputChange('serviceType' as any, serviceId, serviceId)}
              pickupDate={shipmentData.pickupDate}
              onPickupDateChange={(date) => handleInputChange('pickupDate' as any, date, date)}
              deliveryInstructions={shipmentData.deliveryInstructions}
              onDeliveryInstructionsChange={(instructions) => 
                handleInputChange('deliveryInstructions' as any, instructions, instructions)
              }
              signatureRequired={shipmentData.signatureRequired}
              onSignatureRequiredChange={(required) => 
                handleInputChange('signatureRequired' as any, required, required)
              }
              insuranceRequested={shipmentData.insuranceRequested}
              onInsuranceRequestedChange={(requested) => 
                handleInputChange('insuranceRequested' as any, requested, requested)
              }
              insuranceValue={shipmentData.insuranceValue}
              onInsuranceValueChange={(value) => 
                handleInputChange('insuranceValue' as any, value, value)
              }
              errors={errors}
            />
            
            {shipmentData.serviceType && (
              <Box mt={3}>
                <Button
                  variant="outlined"
                  startIcon={<Calculator />}
                  onClick={calculatePricing}
                  loading={loading}
                >
                  Calculate Pricing
                </Button>
              </Box>
            )}
          </Box>
        );
        
      case 3:
        return (
          <Box>
            <Typography variant="h6" gutterBottom>
              Payment Information
            </Typography>
            
            {calculatedPrice && (
              <PricingCalculator
                price={calculatedPrice}
                serviceType={shipmentData.serviceType}
                packages={shipmentData.packages}
                insurance={shipmentData.insuranceRequested ? shipmentData.insuranceValue : 0}
              />
            )}
            
            <PaymentMethodSelector
              selectedPaymentMethod={shipmentData.paymentMethodId}
              onPaymentMethodSelect={(methodId) => 
                handleInputChange('paymentMethodId' as any, methodId, methodId)
              }
              errors={errors}
            />
          </Box>
        );
        
      case 4:
        return (
          <Box>
            <Typography variant="h6" gutterBottom>
              <CheckCircle sx={{ mr: 1, verticalAlign: 'middle', color: 'success.main' }} />
              Confirm Your Shipment
            </Typography>
            
            {/* Shipment Summary */}
            <Card sx={{ mb: 3 }}>
              <CardContent>
                <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
                  Shipment Summary
                </Typography>
                
                <Grid container spacing={2}>
                  <Grid item xs={12} md={6}>
                    <Typography variant="body2" color="text.secondary">From:</Typography>
                    <Typography variant="body1">
                      {shipmentData.senderAddress.contactName}<br />
                      {shipmentData.senderAddress.street}<br />
                      {shipmentData.senderAddress.city}, {shipmentData.senderAddress.state} {shipmentData.senderAddress.zipCode}
                    </Typography>
                  </Grid>
                  
                  <Grid item xs={12} md={6}>
                    <Typography variant="body2" color="text.secondary">To:</Typography>
                    <Typography variant="body1">
                      {shipmentData.recipientAddress.contactName}<br />
                      {shipmentData.recipientAddress.street}<br />
                      {shipmentData.recipientAddress.city}, {shipmentData.recipientAddress.state} {shipmentData.recipientAddress.zipCode}
                    </Typography>
                  </Grid>
                </Grid>
                
                <Divider sx={{ my: 2 }} />
                
                <Typography variant="body2" color="text.secondary">Service:</Typography>
                <Typography variant="body1">
                  {serviceOptions.find(s => s.id === shipmentData.serviceType)?.name}
                </Typography>
                
                <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>Pickup Date:</Typography>
                <Typography variant="body1">
                  {new Date(shipmentData.pickupDate).toLocaleDateString()}
                </Typography>
                
                {calculatedPrice && (
                  <>
                    <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>Total Cost:</Typography>
                    <Typography variant="h6" color="primary.main">
                      ${calculatedPrice.toFixed(2)}
                    </Typography>
                  </>
                )}
              </CardContent>
            </Card>
            
            <Alert severity="info" sx={{ mb: 2 }}>
              By confirming this shipment, you agree to our terms of service and confirm that all information provided is accurate.
            </Alert>
          </Box>
        );
        
      default:
        return 'Unknown step';
    }
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" fontWeight="bold" gutterBottom>
        Book a Shipment
      </Typography>
      
      <Paper sx={{ p: 3 }}>
        <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
          {steps.map((label) => (
            <Step key={label}>
              <StepLabel>{label}</StepLabel>
            </Step>
          ))}
        </Stepper>
        
        {renderStepContent(activeStep)}
        
        <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>
          <Button
            color="inherit"
            disabled={activeStep === 0}
            onClick={handleBack}
            sx={{ mr: 1 }}
          >
            Back
          </Button>
          <Box sx={{ flex: '1 1 auto' }} />
          {activeStep === steps.length - 1 ? (
            <Button
              variant="contained"
              onClick={handleSubmit}
              disabled={loading}
              startIcon={loading ? undefined : <CheckCircle />}
            >
              {loading ? 'Processing...' : 'Confirm & Book Shipment'}
            </Button>
          ) : (
            <Button
              variant="contained"
              onClick={handleNext}
            >
              Next
            </Button>
          )}
        </Box>
      </Paper>
      
      {/* Pricing Dialog */}
      <Dialog open={showPricing} onClose={() => setShowPricing(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Shipping Cost Estimate</DialogTitle>
        <DialogContent>
          {calculatedPrice && (
            <PricingCalculator
              price={calculatedPrice}
              serviceType={shipmentData.serviceType}
              packages={shipmentData.packages}
              insurance={shipmentData.insuranceRequested ? shipmentData.insuranceValue : 0}
            />
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setShowPricing(false)}>Close</Button>
          <Button variant="contained" onClick={() => setShowPricing(false)}>Continue</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default ShipmentBooking;