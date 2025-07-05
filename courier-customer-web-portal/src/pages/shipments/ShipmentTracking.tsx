import React, { useState, useEffect } from 'react';
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
  Chip,
  Stepper,
  Step,
  StepLabel,
  StepContent,
  Timeline,
  TimelineItem,
  TimelineSeparator,
  TimelineConnector,
  TimelineContent,
  TimelineDot,
  TimelineOppositeContent,
  Alert,
  Divider,
  IconButton,
  Tooltip,
} from '@mui/material';
import {
  Search,
  LocalShipping,
  LocationOn,
  Schedule,
  CheckCircle,
  Error,
  Warning,
  Info,
  Print,
  Share,
  Refresh,
  Map,
  Phone,
  Email,
} from '@mui/icons-material';
import { useParams, useNavigate } from 'react-router-dom';
import { useNotification } from '../../contexts/NotificationContext';

interface TrackingEvent {
  id: string;
  timestamp: string;
  status: string;
  location: string;
  description: string;
  type: 'info' | 'success' | 'warning' | 'error';
}

interface ShipmentDetails {
  trackingNumber: string;
  status: string;
  origin: {
    address: string;
    city: string;
    state: string;
    zipCode: string;
  };
  destination: {
    address: string;
    city: string;
    state: string;
    zipCode: string;
  };
  estimatedDelivery: string;
  actualDelivery?: string;
  serviceType: string;
  weight: number;
  dimensions: {
    length: number;
    width: number;
    height: number;
  };
  signatureRequired: boolean;
  currentLocation?: string;
  events: TrackingEvent[];
}

const ShipmentTracking: React.FC = () => {
  const { trackingNumber: urlTrackingNumber } = useParams();
  const navigate = useNavigate();
  const { showNotification } = useNotification();
  
  const [trackingNumber, setTrackingNumber] = useState(urlTrackingNumber || '');
  const [shipmentDetails, setShipmentDetails] = useState<ShipmentDetails | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (urlTrackingNumber) {
      handleTrackingSearch();
    }
  }, [urlTrackingNumber]);

  const handleTrackingSearch = async () => {
    if (!trackingNumber.trim()) {
      setError('Please enter a tracking number');
      return;
    }

    setLoading(true);
    setError('');
    
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      // Mock data - replace with actual API call
      const mockShipmentDetails: ShipmentDetails = {
        trackingNumber: trackingNumber,
        status: 'IN_TRANSIT',
        origin: {
          address: '123 Main St',
          city: 'Los Angeles',
          state: 'CA',
          zipCode: '90210',
        },
        destination: {
          address: '456 Oak Ave',
          city: 'New York',
          state: 'NY',
          zipCode: '10001',
        },
        estimatedDelivery: '2024-01-18T15:00:00Z',
        serviceType: 'Express Delivery',
        weight: 2.5,
        dimensions: {
          length: 12,
          width: 8,
          height: 6,
        },
        signatureRequired: true,
        currentLocation: 'Phoenix, AZ Distribution Center',
        events: [
          {
            id: '1',
            timestamp: '2024-01-15T10:30:00Z',
            status: 'PICKED_UP',
            location: 'Los Angeles, CA',
            description: 'Package picked up from sender',
            type: 'success',
          },
          {
            id: '2',
            timestamp: '2024-01-15T14:20:00Z',
            status: 'IN_TRANSIT',
            location: 'Los Angeles, CA Hub',
            description: 'Package processed at Los Angeles facility',
            type: 'info',
          },
          {
            id: '3',
            timestamp: '2024-01-16T08:15:00Z',
            status: 'IN_TRANSIT',
            location: 'Phoenix, AZ Hub',
            description: 'Package arrived at Phoenix distribution center',
            type: 'info',
          },
          {
            id: '4',
            timestamp: '2024-01-16T12:45:00Z',
            status: 'IN_TRANSIT',
            location: 'Phoenix, AZ Hub',
            description: 'Package sorted and prepared for next transit',
            type: 'info',
          },
        ],
      };
      
      setShipmentDetails(mockShipmentDetails);
    } catch (error) {
      setError('Unable to find shipment with this tracking number');
      setShipmentDetails(null);
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'DELIVERED':
        return 'success';
      case 'IN_TRANSIT':
        return 'info';
      case 'PICKED_UP':
        return 'primary';
      case 'EXCEPTION':
        return 'error';
      case 'PENDING':
        return 'warning';
      default:
        return 'default';
    }
  };

  const getEventIcon = (type: 'info' | 'success' | 'warning' | 'error') => {
    switch (type) {
      case 'success':
        return <CheckCircle color="success" />;
      case 'warning':
        return <Warning color="warning" />;
      case 'error':
        return <Error color="error" />;
      default:
        return <Info color="info" />;
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const handleShare = () => {
    const url = `${window.location.origin}/track/${trackingNumber}`;
    navigator.clipboard.writeText(url);
    showNotification('Tracking link copied to clipboard', 'success');
  };

  const handlePrint = () => {
    window.print();
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" fontWeight="bold" gutterBottom>
        Track Your Shipment
      </Typography>
      
      {/* Search Section */}
      <Paper sx={{ p: 3, mb: 4 }}>
        <Box display="flex" gap={2} alignItems="center">
          <TextField
            fullWidth
            label="Enter Tracking Number"
            value={trackingNumber}
            onChange={(e) => setTrackingNumber(e.target.value)}
            placeholder="e.g., EXL123456789"
            onKeyPress={(e) => e.key === 'Enter' && handleTrackingSearch()}
          />
          <Button
            variant="contained"
            onClick={handleTrackingSearch}
            disabled={loading}
            startIcon={<Search />}
            sx={{ minWidth: 120 }}
          >
            {loading ? 'Tracking...' : 'Track'}
          </Button>
        </Box>
        
        {error && (
          <Alert severity="error" sx={{ mt: 2 }}>
            {error}
          </Alert>
        )}
      </Paper>

      {/* Shipment Details */}
      {shipmentDetails && (
        <Grid container spacing={3}>
          {/* Status Overview */}
          <Grid item xs={12}>
            <Paper sx={{ p: 3 }}>
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                <Typography variant="h6" fontWeight="bold">
                  Shipment Status
                </Typography>
                <Box display="flex" gap={1}>
                  <Tooltip title="Refresh tracking">
                    <IconButton onClick={handleTrackingSearch}>
                      <Refresh />
                    </IconButton>
                  </Tooltip>
                  <Tooltip title="Share tracking link">
                    <IconButton onClick={handleShare}>
                      <Share />
                    </IconButton>
                  </Tooltip>
                  <Tooltip title="Print tracking details">
                    <IconButton onClick={handlePrint}>
                      <Print />
                    </IconButton>
                  </Tooltip>
                </Box>
              </Box>
              
              <Grid container spacing={3}>
                <Grid item xs={12} md={3}>
                  <Typography variant="body2" color="text.secondary">
                    Tracking Number
                  </Typography>
                  <Typography variant="h6" fontWeight="bold">
                    {shipmentDetails.trackingNumber}
                  </Typography>
                </Grid>
                
                <Grid item xs={12} md={3}>
                  <Typography variant="body2" color="text.secondary">
                    Status
                  </Typography>
                  <Chip
                    label={shipmentDetails.status.replace('_', ' ')}
                    color={getStatusColor(shipmentDetails.status) as any}
                    variant="outlined"
                    size="medium"
                  />
                </Grid>
                
                <Grid item xs={12} md={3}>
                  <Typography variant="body2" color="text.secondary">
                    Service Type
                  </Typography>
                  <Typography variant="body1" fontWeight="medium">
                    {shipmentDetails.serviceType}
                  </Typography>
                </Grid>
                
                <Grid item xs={12} md={3}>
                  <Typography variant="body2" color="text.secondary">
                    Estimated Delivery
                  </Typography>
                  <Typography variant="body1" fontWeight="medium">
                    {formatDate(shipmentDetails.estimatedDelivery)}
                  </Typography>
                </Grid>
              </Grid>
              
              {shipmentDetails.currentLocation && (
                <Box mt={2}>
                  <Alert severity="info" icon={<LocationOn />}>
                    <Typography variant="body2">
                      <strong>Current Location:</strong> {shipmentDetails.currentLocation}
                    </Typography>
                  </Alert>
                </Box>
              )}
            </Paper>
          </Grid>

          {/* Addresses */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" fontWeight="bold" gutterBottom>
                  <LocationOn sx={{ mr: 1, verticalAlign: 'middle' }} />
                  Origin
                </Typography>
                <Typography variant="body2">
                  {shipmentDetails.origin.address}<br />
                  {shipmentDetails.origin.city}, {shipmentDetails.origin.state} {shipmentDetails.origin.zipCode}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" fontWeight="bold" gutterBottom>
                  <LocalShipping sx={{ mr: 1, verticalAlign: 'middle' }} />
                  Destination
                </Typography>
                <Typography variant="body2">
                  {shipmentDetails.destination.address}<br />
                  {shipmentDetails.destination.city}, {shipmentDetails.destination.state} {shipmentDetails.destination.zipCode}
                </Typography>
                {shipmentDetails.signatureRequired && (
                  <Chip
                    label="Signature Required"
                    size="small"
                    color="warning"
                    variant="outlined"
                    sx={{ mt: 1 }}
                  />
                )}
              </CardContent>
            </Card>
          </Grid>

          {/* Package Details */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" fontWeight="bold" gutterBottom>
                  Package Details
                </Typography>
                <Grid container spacing={2}>
                  <Grid item xs={6}>
                    <Typography variant="body2" color="text.secondary">
                      Weight
                    </Typography>
                    <Typography variant="body1">
                      {shipmentDetails.weight} lbs
                    </Typography>
                  </Grid>
                  <Grid item xs={6}>
                    <Typography variant="body2" color="text.secondary">
                      Dimensions
                    </Typography>
                    <Typography variant="body1">
                      {shipmentDetails.dimensions.length}" × {shipmentDetails.dimensions.width}" × {shipmentDetails.dimensions.height}"
                    </Typography>
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          </Grid>

          {/* Quick Actions */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" fontWeight="bold" gutterBottom>
                  Need Help?
                </Typography>
                <Box display="flex" flexDirection="column" gap={1}>
                  <Button
                    variant="outlined"
                    startIcon={<Phone />}
                    fullWidth
                    onClick={() => window.open('tel:1-800-EXALT-01')}
                  >
                    Call Support: 1-800-EXALT-01
                  </Button>
                  <Button
                    variant="outlined"
                    startIcon={<Email />}
                    fullWidth
                    onClick={() => navigate('/support')}
                  >
                    Contact Customer Service
                  </Button>
                  <Button
                    variant="outlined"
                    startIcon={<Map />}
                    fullWidth
                    onClick={() => showNotification('Map view coming soon', 'info')}
                  >
                    View on Map
                  </Button>
                </Box>
              </CardContent>
            </Card>
          </Grid>

          {/* Tracking Timeline */}
          <Grid item xs={12}>
            <Paper sx={{ p: 3 }}>
              <Typography variant="h6" fontWeight="bold" gutterBottom>
                Tracking History
              </Typography>
              
              <Timeline>
                {shipmentDetails.events.map((event, index) => (
                  <TimelineItem key={event.id}>
                    <TimelineOppositeContent
                      sx={{ m: 'auto 0' }}
                      align="right"
                      variant="body2"
                      color="text.secondary"
                    >
                      {formatDate(event.timestamp)}
                    </TimelineOppositeContent>
                    <TimelineSeparator>
                      <TimelineDot color={event.type === 'success' ? 'primary' : 'grey'}>
                        {getEventIcon(event.type)}
                      </TimelineDot>
                      {index < shipmentDetails.events.length - 1 && <TimelineConnector />}
                    </TimelineSeparator>
                    <TimelineContent sx={{ py: '12px', px: 2 }}>
                      <Typography variant="subtitle2" component="span" fontWeight="bold">
                        {event.status.replace('_', ' ')}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {event.location}
                      </Typography>
                      <Typography variant="body2">
                        {event.description}
                      </Typography>
                    </TimelineContent>
                  </TimelineItem>
                ))}
              </Timeline>
            </Paper>
          </Grid>
        </Grid>
      )}
    </Container>
  );
};

export default ShipmentTracking;