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
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
  Alert,
  Divider,
  IconButton,
  Tooltip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  ListItemSecondaryAction,
  Switch,
  FormControlLabel,
  Tabs,
  Tab,
  CircularProgress,
  Skeleton,
} from '@mui/material';
import {
  CreditCard,
  AccountBalance,
  Payment,
  Add,
  Edit,
  Delete,
  Visibility,
  Security,
  CheckCircle,
  Warning,
  Error as ErrorIcon,
  Download,
  Receipt,
  Settings,
  Notifications,
  History,
  AutoPay,
  Schedule,
  AttachMoney,
  Info,
} from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';
import { useNotification } from '../../contexts/NotificationContext';

interface PaymentMethod {
  id: string;
  type: 'credit_card' | 'debit_card' | 'bank_account' | 'paypal';
  name: string;
  lastFour: string;
  expiryDate?: string;
  bankName?: string;
  isDefault: boolean;
  isVerified: boolean;
  addedDate: string;
}

interface BillingAddress {
  id: string;
  name: string;
  company?: string;
  address: string;
  city: string;
  state: string;
  zipCode: string;
  country: string;
  isDefault: boolean;
}

interface AutoPaySettings {
  enabled: boolean;
  paymentMethodId: string;
  threshold: number;
  frequency: 'weekly' | 'monthly';
  notifications: boolean;
}

interface BillingPreferences {
  currency: string;
  invoiceFormat: 'pdf' | 'html';
  emailNotifications: boolean;
  smsNotifications: boolean;
  autoPaySettings: AutoPaySettings;
}

const BillingInfo: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { showNotification } = useNotification();
  
  const [activeTab, setActiveTab] = useState(0);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  
  // Data states
  const [paymentMethods, setPaymentMethods] = useState<PaymentMethod[]>([]);
  const [billingAddresses, setBillingAddresses] = useState<BillingAddress[]>([]);
  const [billingPreferences, setBillingPreferences] = useState<BillingPreferences>({
    currency: 'USD',
    invoiceFormat: 'pdf',
    emailNotifications: true,
    smsNotifications: false,
    autoPaySettings: {
      enabled: false,
      paymentMethodId: '',
      threshold: 100,
      frequency: 'monthly',
      notifications: true,
    },
  });
  
  // Dialog states
  const [addPaymentDialog, setAddPaymentDialog] = useState(false);
  const [addAddressDialog, setAddAddressDialog] = useState(false);
  const [editAddressDialog, setEditAddressDialog] = useState(false);
  const [deleteConfirmDialog, setDeleteConfirmDialog] = useState(false);
  const [selectedItem, setSelectedItem] = useState<any>(null);
  
  // Form states
  const [paymentForm, setPaymentForm] = useState({
    type: 'credit_card',
    cardNumber: '',
    expiryDate: '',
    cvv: '',
    cardholderName: '',
    bankName: '',
    accountNumber: '',
    routingNumber: '',
  });
  
  const [addressForm, setAddressForm] = useState({
    name: '',
    company: '',
    address: '',
    city: '',
    state: '',
    zipCode: '',
    country: 'US',
  });

  useEffect(() => {
    loadBillingData();
  }, []);

  const loadBillingData = async () => {
    setLoading(true);
    try {
      // Simulate API calls - replace with actual service calls
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Mock data - replace with actual API responses
      setPaymentMethods([
        {
          id: '1',
          type: 'credit_card',
          name: 'Visa **** 4242',
          lastFour: '4242',
          expiryDate: '12/25',
          isDefault: true,
          isVerified: true,
          addedDate: '2024-01-15',
        },
        {
          id: '2',
          type: 'bank_account',
          name: 'Chase Checking',
          lastFour: '8765',
          bankName: 'JPMorgan Chase',
          isDefault: false,
          isVerified: true,
          addedDate: '2024-02-20',
        },
      ]);
      
      setBillingAddresses([
        {
          id: '1',
          name: 'John Doe',
          company: 'Acme Corp',
          address: '123 Main St',
          city: 'New York',
          state: 'NY',
          zipCode: '10001',
          country: 'US',
          isDefault: true,
        },
      ]);
      
    } catch (error) {
      showNotification('Failed to load billing information', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleAddPaymentMethod = async () => {
    setActionLoading(true);
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      const newMethod: PaymentMethod = {
        id: Date.now().toString(),
        type: paymentForm.type as any,
        name: paymentForm.type === 'credit_card' 
          ? `**** ${paymentForm.cardNumber.slice(-4)}`
          : `${paymentForm.bankName} ****${paymentForm.accountNumber.slice(-4)}`,
        lastFour: paymentForm.type === 'credit_card' 
          ? paymentForm.cardNumber.slice(-4)
          : paymentForm.accountNumber.slice(-4),
        expiryDate: paymentForm.type === 'credit_card' ? paymentForm.expiryDate : undefined,
        bankName: paymentForm.type !== 'credit_card' ? paymentForm.bankName : undefined,
        isDefault: paymentMethods.length === 0,
        isVerified: false,
        addedDate: new Date().toISOString(),
      };
      
      setPaymentMethods(prev => [...prev, newMethod]);
      setAddPaymentDialog(false);
      setPaymentForm({
        type: 'credit_card',
        cardNumber: '',
        expiryDate: '',
        cvv: '',
        cardholderName: '',
        bankName: '',
        accountNumber: '',
        routingNumber: '',
      });
      
      showNotification('Payment method added successfully', 'success');
    } catch (error) {
      showNotification('Failed to add payment method', 'error');
    } finally {
      setActionLoading(false);
    }
  };

  const handleSetDefaultPayment = async (paymentId: string) => {
    try {
      setPaymentMethods(prev => prev.map(method => ({
        ...method,
        isDefault: method.id === paymentId,
      })));
      showNotification('Default payment method updated', 'success');
    } catch (error) {
      showNotification('Failed to update default payment method', 'error');
    }
  };

  const handleDeletePaymentMethod = async (paymentId: string) => {
    try {
      setPaymentMethods(prev => prev.filter(method => method.id !== paymentId));
      setDeleteConfirmDialog(false);
      showNotification('Payment method removed', 'success');
    } catch (error) {
      showNotification('Failed to remove payment method', 'error');
    }
  };

  const handleAddAddress = async () => {
    setActionLoading(true);
    try {
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      const newAddress: BillingAddress = {
        id: Date.now().toString(),
        ...addressForm,
        isDefault: billingAddresses.length === 0,
      };
      
      setBillingAddresses(prev => [...prev, newAddress]);
      setAddAddressDialog(false);
      setAddressForm({
        name: '',
        company: '',
        address: '',
        city: '',
        state: '',
        zipCode: '',
        country: 'US',
      });
      
      showNotification('Billing address added successfully', 'success');
    } catch (error) {
      showNotification('Failed to add billing address', 'error');
    } finally {
      setActionLoading(false);
    }
  };

  const handleUpdatePreferences = async () => {
    setActionLoading(true);
    try {
      await new Promise(resolve => setTimeout(resolve, 1000));
      showNotification('Billing preferences updated', 'success');
    } catch (error) {
      showNotification('Failed to update preferences', 'error');
    } finally {
      setActionLoading(false);
    }
  };

  const getPaymentMethodIcon = (type: string) => {
    switch (type) {
      case 'credit_card':
      case 'debit_card':
        return <CreditCard />;
      case 'bank_account':
        return <AccountBalance />;
      case 'paypal':
        return <Payment />;
      default:
        return <CreditCard />;
    }
  };

  const renderPaymentMethods = () => (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h6">Payment Methods</Typography>
        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={() => setAddPaymentDialog(true)}
        >
          Add Payment Method
        </Button>
      </Box>

      {loading ? (
        <Grid container spacing={2}>
          {[1, 2].map(i => (
            <Grid item xs={12} md={6} key={i}>
              <Skeleton variant="rectangular" height={180} />
            </Grid>
          ))}
        </Grid>
      ) : (
        <Grid container spacing={2}>
          {paymentMethods.map((method) => (
            <Grid item xs={12} md={6} key={method.id}>
              <Card>
                <CardContent>
                  <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
                    <Box display="flex" alignItems="center" gap={2}>
                      {getPaymentMethodIcon(method.type)}
                      <Box>
                        <Typography variant="h6">{method.name}</Typography>
                        <Typography variant="body2" color="text.secondary">
                          {method.bankName ? `${method.bankName}` : `Expires ${method.expiryDate}`}
                        </Typography>
                      </Box>
                    </Box>
                    <Box display="flex" gap={1}>
                      {method.isDefault && (
                        <Chip label="Default" color="primary" size="small" />
                      )}
                      {method.isVerified ? (
                        <Chip 
                          icon={<CheckCircle />} 
                          label="Verified" 
                          color="success" 
                          size="small" 
                        />
                      ) : (
                        <Chip 
                          icon={<Warning />} 
                          label="Unverified" 
                          color="warning" 
                          size="small" 
                        />
                      )}
                    </Box>
                  </Box>
                  
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    Added {new Date(method.addedDate).toLocaleDateString()}
                  </Typography>
                </CardContent>
                
                <CardActions>
                  {!method.isDefault && (
                    <Button 
                      size="small" 
                      onClick={() => handleSetDefaultPayment(method.id)}
                    >
                      Set as Default
                    </Button>
                  )}
                  <Button 
                    size="small" 
                    color="error"
                    onClick={() => {
                      setSelectedItem(method);
                      setDeleteConfirmDialog(true);
                    }}
                  >
                    Remove
                  </Button>
                </CardActions>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Box>
  );

  const renderBillingAddresses = () => (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h6">Billing Addresses</Typography>
        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={() => setAddAddressDialog(true)}
        >
          Add Address
        </Button>
      </Box>

      {loading ? (
        <Grid container spacing={2}>
          {[1].map(i => (
            <Grid item xs={12} md={6} key={i}>
              <Skeleton variant="rectangular" height={200} />
            </Grid>
          ))}
        </Grid>
      ) : (
        <Grid container spacing={2}>
          {billingAddresses.map((address) => (
            <Grid item xs={12} md={6} key={address.id}>
              <Card>
                <CardContent>
                  <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
                    <Typography variant="h6">{address.name}</Typography>
                    {address.isDefault && (
                      <Chip label="Default" color="primary" size="small" />
                    )}
                  </Box>
                  
                  {address.company && (
                    <Typography variant="body2" gutterBottom>
                      {address.company}
                    </Typography>
                  )}
                  
                  <Typography variant="body2" color="text.secondary">
                    {address.address}<br />
                    {address.city}, {address.state} {address.zipCode}<br />
                    {address.country}
                  </Typography>
                </CardContent>
                
                <CardActions>
                  <IconButton size="small">
                    <Edit />
                  </IconButton>
                  <IconButton 
                    size="small" 
                    color="error"
                    onClick={() => {
                      setSelectedItem(address);
                      setDeleteConfirmDialog(true);
                    }}
                  >
                    <Delete />
                  </IconButton>
                </CardActions>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Box>
  );

  const renderBillingPreferences = () => (
    <Box>
      <Typography variant="h6" gutterBottom>Billing Preferences</Typography>
      
      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="subtitle1" gutterBottom>
              <Settings sx={{ mr: 1, verticalAlign: 'middle' }} />
              General Settings
            </Typography>
            
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <FormControl fullWidth>
                  <InputLabel>Currency</InputLabel>
                  <Select
                    value={billingPreferences.currency}
                    label="Currency"
                    onChange={(e) => setBillingPreferences(prev => ({
                      ...prev,
                      currency: e.target.value
                    }))}
                  >
                    <MenuItem value="USD">USD - US Dollar</MenuItem>
                    <MenuItem value="EUR">EUR - Euro</MenuItem>
                    <MenuItem value="GBP">GBP - British Pound</MenuItem>
                    <MenuItem value="CAD">CAD - Canadian Dollar</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
              
              <Grid item xs={12}>
                <FormControl fullWidth>
                  <InputLabel>Invoice Format</InputLabel>
                  <Select
                    value={billingPreferences.invoiceFormat}
                    label="Invoice Format"
                    onChange={(e) => setBillingPreferences(prev => ({
                      ...prev,
                      invoiceFormat: e.target.value as any
                    }))}
                  >
                    <MenuItem value="pdf">PDF</MenuItem>
                    <MenuItem value="html">HTML</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
            </Grid>
          </Paper>
        </Grid>
        
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="subtitle1" gutterBottom>
              <Notifications sx={{ mr: 1, verticalAlign: 'middle' }} />
              Notification Preferences
            </Typography>
            
            <List>
              <ListItem>
                <ListItemText 
                  primary="Email Notifications"
                  secondary="Receive billing updates via email"
                />
                <ListItemSecondaryAction>
                  <Switch
                    checked={billingPreferences.emailNotifications}
                    onChange={(e) => setBillingPreferences(prev => ({
                      ...prev,
                      emailNotifications: e.target.checked
                    }))}
                  />
                </ListItemSecondaryAction>
              </ListItem>
              
              <ListItem>
                <ListItemText 
                  primary="SMS Notifications"
                  secondary="Receive payment alerts via SMS"
                />
                <ListItemSecondaryAction>
                  <Switch
                    checked={billingPreferences.smsNotifications}
                    onChange={(e) => setBillingPreferences(prev => ({
                      ...prev,
                      smsNotifications: e.target.checked
                    }))}
                  />
                </ListItemSecondaryAction>
              </ListItem>
            </List>
          </Paper>
        </Grid>
        
        <Grid item xs={12}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="subtitle1" gutterBottom>
              <AutoPay sx={{ mr: 1, verticalAlign: 'middle' }} />
              Auto-Pay Settings
            </Typography>
            
            <Grid container spacing={2} alignItems="center">
              <Grid item xs={12}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={billingPreferences.autoPaySettings.enabled}
                      onChange={(e) => setBillingPreferences(prev => ({
                        ...prev,
                        autoPaySettings: {
                          ...prev.autoPaySettings,
                          enabled: e.target.checked
                        }
                      }))}
                    />
                  }
                  label="Enable Auto-Pay"
                />
              </Grid>
              
              {billingPreferences.autoPaySettings.enabled && (
                <>
                  <Grid item xs={12} md={4}>
                    <FormControl fullWidth>
                      <InputLabel>Payment Method</InputLabel>
                      <Select
                        value={billingPreferences.autoPaySettings.paymentMethodId}
                        label="Payment Method"
                        onChange={(e) => setBillingPreferences(prev => ({
                          ...prev,
                          autoPaySettings: {
                            ...prev.autoPaySettings,
                            paymentMethodId: e.target.value
                          }
                        }))}
                      >
                        {paymentMethods.map(method => (
                          <MenuItem key={method.id} value={method.id}>
                            {method.name}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  </Grid>
                  
                  <Grid item xs={12} md={4}>
                    <TextField
                      fullWidth
                      label="Threshold Amount"
                      type="number"
                      value={billingPreferences.autoPaySettings.threshold}
                      onChange={(e) => setBillingPreferences(prev => ({
                        ...prev,
                        autoPaySettings: {
                          ...prev.autoPaySettings,
                          threshold: parseFloat(e.target.value) || 0
                        }
                      }))}
                      InputProps={{
                        startAdornment: <AttachMoney />,
                      }}
                    />
                  </Grid>
                  
                  <Grid item xs={12} md={4}>
                    <FormControl fullWidth>
                      <InputLabel>Frequency</InputLabel>
                      <Select
                        value={billingPreferences.autoPaySettings.frequency}
                        label="Frequency"
                        onChange={(e) => setBillingPreferences(prev => ({
                          ...prev,
                          autoPaySettings: {
                            ...prev.autoPaySettings,
                            frequency: e.target.value as any
                          }
                        }))}
                      >
                        <MenuItem value="weekly">Weekly</MenuItem>
                        <MenuItem value="monthly">Monthly</MenuItem>
                      </Select>
                    </FormControl>
                  </Grid>
                </>
              )}
            </Grid>
          </Paper>
        </Grid>
        
        <Grid item xs={12}>
          <Button
            variant="contained"
            onClick={handleUpdatePreferences}
            disabled={actionLoading}
            startIcon={actionLoading ? <CircularProgress size={20} /> : <Save />}
          >
            Save Preferences
          </Button>
        </Grid>
      </Grid>
    </Box>
  );

  const renderQuickActions = () => (
    <Paper sx={{ p: 3, mb: 3 }}>
      <Typography variant="h6" gutterBottom>Quick Actions</Typography>
      
      <Grid container spacing={2}>
        <Grid item xs={12} sm={6} md={3}>
          <Button
            fullWidth
            variant="outlined"
            startIcon={<Receipt />}
            onClick={() => navigate('/billing/invoices')}
          >
            View Invoices
          </Button>
        </Grid>
        
        <Grid item xs={12} sm={6} md={3}>
          <Button
            fullWidth
            variant="outlined"
            startIcon={<History />}
            onClick={() => navigate('/billing/history')}
          >
            Payment History
          </Button>
        </Grid>
        
        <Grid item xs={12} sm={6} md={3}>
          <Button
            fullWidth
            variant="outlined"
            startIcon={<Download />}
            onClick={() => {
              // Trigger download of billing summary
              showNotification('Downloading billing summary...', 'info');
            }}
          >
            Download Summary
          </Button>
        </Grid>
        
        <Grid item xs={12} sm={6} md={3}>
          <Button
            fullWidth
            variant="outlined"
            startIcon={<Schedule />}
            onClick={() => navigate('/billing/schedule')}
          >
            Schedule Payment
          </Button>
        </Grid>
      </Grid>
    </Paper>
  );

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" fontWeight="bold" gutterBottom>
        Billing & Payment Information
      </Typography>
      <Typography variant="body1" color="text.secondary" paragraph>
        Manage your payment methods, billing addresses, and preferences
      </Typography>

      {renderQuickActions()}

      <Paper sx={{ p: 1, mb: 3 }}>
        <Tabs value={activeTab} onChange={(_, newValue) => setActiveTab(newValue)}>
          <Tab label="Payment Methods" />
          <Tab label="Billing Addresses" />
          <Tab label="Preferences" />
        </Tabs>
      </Paper>

      <Box sx={{ mt: 3 }}>
        {activeTab === 0 && renderPaymentMethods()}
        {activeTab === 1 && renderBillingAddresses()}
        {activeTab === 2 && renderBillingPreferences()}
      </Box>

      {/* Add Payment Method Dialog */}
      <Dialog open={addPaymentDialog} onClose={() => setAddPaymentDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Add Payment Method</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <FormControl fullWidth>
                <InputLabel>Payment Method Type</InputLabel>
                <Select
                  value={paymentForm.type}
                  label="Payment Method Type"
                  onChange={(e) => setPaymentForm(prev => ({ ...prev, type: e.target.value }))}
                >
                  <MenuItem value="credit_card">Credit Card</MenuItem>
                  <MenuItem value="debit_card">Debit Card</MenuItem>
                  <MenuItem value="bank_account">Bank Account</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            
            {(paymentForm.type === 'credit_card' || paymentForm.type === 'debit_card') && (
              <>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Card Number"
                    value={paymentForm.cardNumber}
                    onChange={(e) => setPaymentForm(prev => ({ ...prev, cardNumber: e.target.value }))}
                    placeholder="1234 5678 9012 3456"
                  />
                </Grid>
                
                <Grid item xs={6}>
                  <TextField
                    fullWidth
                    label="Expiry Date"
                    value={paymentForm.expiryDate}
                    onChange={(e) => setPaymentForm(prev => ({ ...prev, expiryDate: e.target.value }))}
                    placeholder="MM/YY"
                  />
                </Grid>
                
                <Grid item xs={6}>
                  <TextField
                    fullWidth
                    label="CVV"
                    value={paymentForm.cvv}
                    onChange={(e) => setPaymentForm(prev => ({ ...prev, cvv: e.target.value }))}
                    placeholder="123"
                  />
                </Grid>
                
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Cardholder Name"
                    value={paymentForm.cardholderName}
                    onChange={(e) => setPaymentForm(prev => ({ ...prev, cardholderName: e.target.value }))}
                  />
                </Grid>
              </>
            )}
            
            {paymentForm.type === 'bank_account' && (
              <>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Bank Name"
                    value={paymentForm.bankName}
                    onChange={(e) => setPaymentForm(prev => ({ ...prev, bankName: e.target.value }))}
                  />
                </Grid>
                
                <Grid item xs={6}>
                  <TextField
                    fullWidth
                    label="Account Number"
                    value={paymentForm.accountNumber}
                    onChange={(e) => setPaymentForm(prev => ({ ...prev, accountNumber: e.target.value }))}
                  />
                </Grid>
                
                <Grid item xs={6}>
                  <TextField
                    fullWidth
                    label="Routing Number"
                    value={paymentForm.routingNumber}
                    onChange={(e) => setPaymentForm(prev => ({ ...prev, routingNumber: e.target.value }))}
                  />
                </Grid>
              </>
            )}
          </Grid>
          
          <Alert severity="info" sx={{ mt: 2 }}>
            <Security sx={{ mr: 1 }} />
            Your payment information is encrypted and securely stored.
          </Alert>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setAddPaymentDialog(false)}>Cancel</Button>
          <Button 
            onClick={handleAddPaymentMethod}
            variant="contained"
            disabled={actionLoading}
          >
            {actionLoading ? <CircularProgress size={20} /> : 'Add Payment Method'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Add Address Dialog */}
      <Dialog open={addAddressDialog} onClose={() => setAddAddressDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Add Billing Address</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Full Name"
                value={addressForm.name}
                onChange={(e) => setAddressForm(prev => ({ ...prev, name: e.target.value }))}
                required
              />
            </Grid>
            
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Company (Optional)"
                value={addressForm.company}
                onChange={(e) => setAddressForm(prev => ({ ...prev, company: e.target.value }))}
              />
            </Grid>
            
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Street Address"
                value={addressForm.address}
                onChange={(e) => setAddressForm(prev => ({ ...prev, address: e.target.value }))}
                required
              />
            </Grid>
            
            <Grid item xs={6}>
              <TextField
                fullWidth
                label="City"
                value={addressForm.city}
                onChange={(e) => setAddressForm(prev => ({ ...prev, city: e.target.value }))}
                required
              />
            </Grid>
            
            <Grid item xs={3}>
              <TextField
                fullWidth
                label="State"
                value={addressForm.state}
                onChange={(e) => setAddressForm(prev => ({ ...prev, state: e.target.value }))}
                required
              />
            </Grid>
            
            <Grid item xs={3}>
              <TextField
                fullWidth
                label="ZIP Code"
                value={addressForm.zipCode}
                onChange={(e) => setAddressForm(prev => ({ ...prev, zipCode: e.target.value }))}
                required
              />
            </Grid>
            
            <Grid item xs={12}>
              <FormControl fullWidth>
                <InputLabel>Country</InputLabel>
                <Select
                  value={addressForm.country}
                  label="Country"
                  onChange={(e) => setAddressForm(prev => ({ ...prev, country: e.target.value }))}
                >
                  <MenuItem value="US">United States</MenuItem>
                  <MenuItem value="CA">Canada</MenuItem>
                  <MenuItem value="GB">United Kingdom</MenuItem>
                </Select>
              </FormControl>
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setAddAddressDialog(false)}>Cancel</Button>
          <Button 
            onClick={handleAddAddress}
            variant="contained"
            disabled={actionLoading}
          >
            {actionLoading ? <CircularProgress size={20} /> : 'Add Address'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteConfirmDialog} onClose={() => setDeleteConfirmDialog(false)}>
        <DialogTitle>Confirm Deletion</DialogTitle>
        <DialogContent>
          <Typography>
            Are you sure you want to remove this {selectedItem?.type ? 'payment method' : 'billing address'}?
            This action cannot be undone.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteConfirmDialog(false)}>Cancel</Button>
          <Button 
            onClick={() => {
              if (selectedItem?.type) {
                handleDeletePaymentMethod(selectedItem.id);
              } else {
                // Handle address deletion
                setBillingAddresses(prev => prev.filter(addr => addr.id !== selectedItem?.id));
                setDeleteConfirmDialog(false);
                showNotification('Billing address removed', 'success');
              }
            }}
            color="error"
            variant="contained"
          >
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default BillingInfo;