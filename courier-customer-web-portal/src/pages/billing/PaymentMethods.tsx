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
  Divider,
  CircularProgress,
  Skeleton,
  Stack,
  Menu,
  MenuList,
  Tabs,
  Tab,
  InputAdornment,
  LinearProgress,
} from '@mui/material';
import {
  CreditCard,
  AccountBalance,
  Payment,
  PayPal,
  Apple,
  Google,
  Add,
  Edit,
  Delete,
  MoreVert,
  Star,
  StarBorder,
  Security,
  CheckCircle,
  Warning,
  Error as ErrorIcon,
  Visibility,
  VisibilityOff,
  Refresh,
  Verified,
  Schedule,
  History,
  Settings,
  Info,
  Lock,
  Shield,
  AccountBalanceWallet,
  ContactlessOutlined,
  QrCode2,
  Phone,
  Email,
} from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';
import { useNotification } from '../../contexts/NotificationContext';

interface PaymentMethod {
  id: string;
  type: 'credit_card' | 'debit_card' | 'bank_account' | 'paypal' | 'apple_pay' | 'google_pay' | 'crypto_wallet';
  name: string;
  lastFour: string;
  expiryDate?: string;
  cardholderName?: string;
  bankName?: string;
  accountType?: 'checking' | 'savings';
  routingNumber?: string;
  email?: string;
  phone?: string;
  walletAddress?: string;
  isDefault: boolean;
  isVerified: boolean;
  isActive: boolean;
  addedDate: string;
  lastUsed?: string;
  usageCount: number;
  securityFeatures: {
    twoFactorEnabled: boolean;
    fraudProtection: boolean;
    contactlessEnabled: boolean;
    biometricEnabled: boolean;
  };
  limits: {
    dailyLimit: number;
    monthlyLimit: number;
    singleTransactionLimit: number;
  };
  fees: {
    processingFee: number;
    internationalFee: number;
    currency: string;
  };
}

interface PaymentForm {
  type: string;
  cardNumber: string;
  expiryDate: string;
  cvv: string;
  cardholderName: string;
  bankName: string;
  accountNumber: string;
  routingNumber: string;
  accountType: string;
  email: string;
  phone: string;
  walletAddress: string;
  enableTwoFactor: boolean;
  enableContactless: boolean;
  enableBiometric: boolean;
  dailyLimit: number;
  monthlyLimit: number;
  singleTransactionLimit: number;
}

interface SecuritySettings {
  twoFactorAuthEnabled: boolean;
  biometricAuthEnabled: boolean;
  fraudAlertsEnabled: boolean;
  transactionNotifications: boolean;
  autoLockEnabled: boolean;
  secureMode: boolean;
}

const PaymentMethods: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { showNotification } = useNotification();
  
  // Data states
  const [paymentMethods, setPaymentMethods] = useState<PaymentMethod[]>([]);
  const [securitySettings, setSecuritySettings] = useState<SecuritySettings>({
    twoFactorAuthEnabled: false,
    biometricAuthEnabled: false,
    fraudAlertsEnabled: true,
    transactionNotifications: true,
    autoLockEnabled: false,
    secureMode: false,
  });
  
  // UI states
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState(0);
  const [addDialog, setAddDialog] = useState(false);
  const [editDialog, setEditDialog] = useState(false);
  const [deleteDialog, setDeleteDialog] = useState(false);
  const [securityDialog, setSecurityDialog] = useState(false);
  const [verifyDialog, setVerifyDialog] = useState(false);
  const [selectedMethod, setSelectedMethod] = useState<PaymentMethod | null>(null);
  const [menuAnchor, setMenuAnchor] = useState<null | HTMLElement>(null);
  const [showCvv, setShowCvv] = useState(false);
  const [verificationStep, setVerificationStep] = useState(0);
  const [verificationCode, setVerificationCode] = useState('');
  
  // Form state
  const [paymentForm, setPaymentForm] = useState<PaymentForm>({
    type: 'credit_card',
    cardNumber: '',
    expiryDate: '',
    cvv: '',
    cardholderName: '',
    bankName: '',
    accountNumber: '',
    routingNumber: '',
    accountType: 'checking',
    email: '',
    phone: '',
    walletAddress: '',
    enableTwoFactor: false,
    enableContactless: true,
    enableBiometric: false,
    dailyLimit: 1000,
    monthlyLimit: 10000,
    singleTransactionLimit: 500,
  });

  const paymentTypes = [
    { value: 'credit_card', label: 'Credit Card', icon: <CreditCard />, description: 'Visa, Mastercard, Amex' },
    { value: 'debit_card', label: 'Debit Card', icon: <CreditCard />, description: 'Bank debit card' },
    { value: 'bank_account', label: 'Bank Account', icon: <AccountBalance />, description: 'Checking or savings' },
    { value: 'paypal', label: 'PayPal', icon: <PayPal />, description: 'PayPal account' },
    { value: 'apple_pay', label: 'Apple Pay', icon: <Apple />, description: 'Apple Wallet' },
    { value: 'google_pay', label: 'Google Pay', icon: <Google />, description: 'Google Wallet' },
    { value: 'crypto_wallet', label: 'Crypto Wallet', icon: <AccountBalanceWallet />, description: 'Bitcoin, Ethereum' },
  ];

  const bankAccountTypes = [
    { value: 'checking', label: 'Checking Account' },
    { value: 'savings', label: 'Savings Account' },
  ];

  useEffect(() => {
    loadPaymentData();
  }, []);

  const loadPaymentData = async () => {
    setLoading(true);
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Mock data
      const mockMethods: PaymentMethod[] = [
        {
          id: '1',
          type: 'credit_card',
          name: 'Visa Platinum',
          lastFour: '4242',
          expiryDate: '12/25',
          cardholderName: 'John Doe',
          isDefault: true,
          isVerified: true,
          isActive: true,
          addedDate: '2024-01-15',
          lastUsed: '2024-06-20',
          usageCount: 45,
          securityFeatures: {
            twoFactorEnabled: true,
            fraudProtection: true,
            contactlessEnabled: true,
            biometricEnabled: false,
          },
          limits: {
            dailyLimit: 2000,
            monthlyLimit: 15000,
            singleTransactionLimit: 1000,
          },
          fees: {
            processingFee: 0.029,
            internationalFee: 0.035,
            currency: 'USD',
          },
        },
        {
          id: '2',
          type: 'bank_account',
          name: 'Chase Checking',
          lastFour: '8765',
          bankName: 'JPMorgan Chase',
          accountType: 'checking',
          routingNumber: '021000021',
          isDefault: false,
          isVerified: true,
          isActive: true,
          addedDate: '2024-02-20',
          lastUsed: '2024-06-15',
          usageCount: 12,
          securityFeatures: {
            twoFactorEnabled: false,
            fraudProtection: true,
            contactlessEnabled: false,
            biometricEnabled: false,
          },
          limits: {
            dailyLimit: 5000,
            monthlyLimit: 50000,
            singleTransactionLimit: 2500,
          },
          fees: {
            processingFee: 0.008,
            internationalFee: 0.015,
            currency: 'USD',
          },
        },
        {
          id: '3',
          type: 'paypal',
          name: 'PayPal Account',
          lastFour: 'john@example.com',
          email: 'john@example.com',
          isDefault: false,
          isVerified: false,
          isActive: true,
          addedDate: '2024-03-10',
          usageCount: 3,
          securityFeatures: {
            twoFactorEnabled: false,
            fraudProtection: true,
            contactlessEnabled: false,
            biometricEnabled: false,
          },
          limits: {
            dailyLimit: 1500,
            monthlyLimit: 8000,
            singleTransactionLimit: 750,
          },
          fees: {
            processingFee: 0.034,
            internationalFee: 0.044,
            currency: 'USD',
          },
        },
      ];
      
      setPaymentMethods(mockMethods);
    } catch (error) {
      showNotification('Failed to load payment methods', 'error');
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setPaymentForm({
      type: 'credit_card',
      cardNumber: '',
      expiryDate: '',
      cvv: '',
      cardholderName: '',
      bankName: '',
      accountNumber: '',
      routingNumber: '',
      accountType: 'checking',
      email: '',
      phone: '',
      walletAddress: '',
      enableTwoFactor: false,
      enableContactless: true,
      enableBiometric: false,
      dailyLimit: 1000,
      monthlyLimit: 10000,
      singleTransactionLimit: 500,
    });
  };

  const handleAddPaymentMethod = async () => {
    setActionLoading('add');
    try {
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      const newMethod: PaymentMethod = {
        id: Date.now().toString(),
        type: paymentForm.type as any,
        name: paymentForm.type === 'credit_card' || paymentForm.type === 'debit_card' 
          ? `${paymentForm.cardholderName}'s Card`
          : paymentForm.type === 'bank_account'
          ? `${paymentForm.bankName} ${paymentForm.accountType}`
          : paymentForm.type === 'paypal'
          ? 'PayPal Account'
          : paymentForm.type,
        lastFour: paymentForm.type === 'credit_card' || paymentForm.type === 'debit_card'
          ? paymentForm.cardNumber.slice(-4)
          : paymentForm.type === 'bank_account'
          ? paymentForm.accountNumber.slice(-4)
          : paymentForm.type === 'paypal'
          ? paymentForm.email
          : 'N/A',
        expiryDate: paymentForm.type.includes('card') ? paymentForm.expiryDate : undefined,
        cardholderName: paymentForm.type.includes('card') ? paymentForm.cardholderName : undefined,
        bankName: paymentForm.type === 'bank_account' ? paymentForm.bankName : undefined,
        accountType: paymentForm.type === 'bank_account' ? paymentForm.accountType as any : undefined,
        routingNumber: paymentForm.type === 'bank_account' ? paymentForm.routingNumber : undefined,
        email: paymentForm.type === 'paypal' ? paymentForm.email : undefined,
        phone: paymentForm.phone || undefined,
        walletAddress: paymentForm.type === 'crypto_wallet' ? paymentForm.walletAddress : undefined,
        isDefault: paymentMethods.length === 0,
        isVerified: false,
        isActive: true,
        addedDate: new Date().toISOString(),
        usageCount: 0,
        securityFeatures: {
          twoFactorEnabled: paymentForm.enableTwoFactor,
          fraudProtection: true,
          contactlessEnabled: paymentForm.enableContactless,
          biometricEnabled: paymentForm.enableBiometric,
        },
        limits: {
          dailyLimit: paymentForm.dailyLimit,
          monthlyLimit: paymentForm.monthlyLimit,
          singleTransactionLimit: paymentForm.singleTransactionLimit,
        },
        fees: {
          processingFee: 0.029,
          internationalFee: 0.035,
          currency: 'USD',
        },
      };
      
      setPaymentMethods(prev => [...prev, newMethod]);
      setAddDialog(false);
      resetForm();
      showNotification('Payment method added successfully', 'success');
    } catch (error) {
      showNotification('Failed to add payment method', 'error');
    } finally {
      setActionLoading(null);
    }
  };

  const handleSetDefault = async (methodId: string) => {
    try {
      setPaymentMethods(prev => prev.map(method => ({
        ...method,
        isDefault: method.id === methodId,
      })));
      showNotification('Default payment method updated', 'success');
    } catch (error) {
      showNotification('Failed to update default payment method', 'error');
    }
  };

  const handleDeletePaymentMethod = async () => {
    if (!selectedMethod) return;
    
    setActionLoading('delete');
    try {
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      setPaymentMethods(prev => prev.filter(method => method.id !== selectedMethod.id));
      setDeleteDialog(false);
      setSelectedMethod(null);
      showNotification('Payment method removed', 'success');
    } catch (error) {
      showNotification('Failed to remove payment method', 'error');
    } finally {
      setActionLoading(null);
    }
  };

  const handleVerifyPaymentMethod = async (method: PaymentMethod) => {
    setSelectedMethod(method);
    setVerifyDialog(true);
    setVerificationStep(0);
  };

  const handleVerificationSubmit = async () => {
    setActionLoading('verify');
    try {
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      setPaymentMethods(prev => prev.map(method => 
        method.id === selectedMethod?.id 
          ? { ...method, isVerified: true }
          : method
      ));
      
      setVerifyDialog(false);
      setVerificationStep(0);
      setVerificationCode('');
      showNotification('Payment method verified successfully', 'success');
    } catch (error) {
      showNotification('Verification failed', 'error');
    } finally {
      setActionLoading(null);
    }
  };

  const getPaymentIcon = (type: string) => {
    const paymentType = paymentTypes.find(t => t.value === type);
    return paymentType?.icon || <CreditCard />;
  };

  const getPaymentMethodSecurity = (method: PaymentMethod) => {
    const securityScore = Object.values(method.securityFeatures).filter(Boolean).length;
    const maxScore = Object.keys(method.securityFeatures).length;
    return (securityScore / maxScore) * 100;
  };

  const renderPaymentMethodCard = (method: PaymentMethod) => {
    const securityScore = getPaymentMethodSecurity(method);
    
    return (
      <Card key={method.id} sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <CardContent sx={{ flexGrow: 1 }}>
          <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
            <Box display="flex" alignItems="center" gap={2}>
              {getPaymentIcon(method.type)}
              <Box>
                <Typography variant="h6">{method.name}</Typography>
                <Typography variant="body2" color="text.secondary">
                  {method.type === 'bank_account' ? `${method.bankName} ****${method.lastFour}` : 
                   method.type === 'paypal' ? method.email :
                   method.type.includes('card') ? `****${method.lastFour}` :
                   method.lastFour}
                </Typography>
              </Box>
            </Box>
            
            <Box display="flex" alignItems="center" gap={0.5}>
              {method.isDefault && (
                <Chip label="Default" color="primary" size="small" />
              )}
              <IconButton
                size="small"
                onClick={(e) => {
                  setSelectedMethod(method);
                  setMenuAnchor(e.currentTarget);
                }}
              >
                <MoreVert />
              </IconButton>
            </Box>
          </Box>
          
          {method.expiryDate && (
            <Typography variant="body2" color="text.secondary" gutterBottom>
              Expires {method.expiryDate}
            </Typography>
          )}
          
          <Box display="flex" alignItems="center" gap={1} mb={2}>
            {method.isVerified ? (
              <Chip
                icon={<Verified />}
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
            
            {method.isActive ? (
              <Chip
                icon={<CheckCircle />}
                label="Active"
                color="success"
                size="small"
                variant="outlined"
              />
            ) : (
              <Chip
                icon={<ErrorIcon />}
                label="Inactive"
                color="error"
                size="small"
                variant="outlined"
              />
            )}
          </Box>
          
          <Box mb={2}>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={1}>
              <Typography variant="body2">Security Score</Typography>
              <Typography variant="body2" fontWeight="bold">
                {securityScore.toFixed(0)}%
              </Typography>
            </Box>
            <LinearProgress 
              variant="determinate" 
              value={securityScore} 
              color={securityScore > 75 ? 'success' : securityScore > 50 ? 'warning' : 'error'}
              sx={{ height: 6, borderRadius: 3 }}
            />
          </Box>
          
          <Grid container spacing={1} sx={{ mb: 2 }}>
            <Grid item xs={6}>
              <Typography variant="caption" color="text.secondary">Daily Limit</Typography>
              <Typography variant="body2" fontWeight="bold">
                ${method.limits.dailyLimit.toLocaleString()}
              </Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography variant="caption" color="text.secondary">Monthly Limit</Typography>
              <Typography variant="body2" fontWeight="bold">
                ${method.limits.monthlyLimit.toLocaleString()}
              </Typography>
            </Grid>
          </Grid>
          
          <Typography variant="caption" color="text.secondary">
            Added {new Date(method.addedDate).toLocaleDateString()}
            {method.lastUsed && ` • Last used ${new Date(method.lastUsed).toLocaleDateString()}`}
            {method.usageCount > 0 && ` • Used ${method.usageCount} times`}
          </Typography>
        </CardContent>
        
        <CardActions>
          <Button
            size="small"
            startIcon={<Edit />}
            onClick={() => {
              setSelectedMethod(method);
              setEditDialog(true);
            }}
          >
            Edit
          </Button>
          
          {!method.isVerified && (
            <Button
              size="small"
              startIcon={<Security />}
              onClick={() => handleVerifyPaymentMethod(method)}
            >
              Verify
            </Button>
          )}
          
          {!method.isDefault && (
            <Button
              size="small"
              onClick={() => handleSetDefault(method.id)}
            >
              Set Default
            </Button>
          )}
        </CardActions>
      </Card>
    );
  };

  const renderPaymentForm = () => (
    <Grid container spacing={2}>
      <Grid item xs={12}>
        <FormControl fullWidth required>
          <InputLabel>Payment Method Type</InputLabel>
          <Select
            value={paymentForm.type}
            label="Payment Method Type"
            onChange={(e) => setPaymentForm(prev => ({ ...prev, type: e.target.value }))}
          >
            {paymentTypes.map(type => (
              <MenuItem key={type.value} value={type.value}>
                <Box display="flex" alignItems="center" gap={1} width="100%">
                  {type.icon}
                  <Box>
                    <Typography>{type.label}</Typography>
                    <Typography variant="caption" color="text.secondary">
                      {type.description}
                    </Typography>
                  </Box>
                </Box>
              </MenuItem>
            ))}
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
              required
            />
          </Grid>
          
          <Grid item xs={6}>
            <TextField
              fullWidth
              label="Expiry Date"
              value={paymentForm.expiryDate}
              onChange={(e) => setPaymentForm(prev => ({ ...prev, expiryDate: e.target.value }))}
              placeholder="MM/YY"
              required
            />
          </Grid>
          
          <Grid item xs={6}>
            <TextField
              fullWidth
              label="CVV"
              type={showCvv ? 'text' : 'password'}
              value={paymentForm.cvv}
              onChange={(e) => setPaymentForm(prev => ({ ...prev, cvv: e.target.value }))}
              placeholder="123"
              required
              InputProps={{
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton onClick={() => setShowCvv(!showCvv)} edge="end">
                      {showCvv ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
            />
          </Grid>
          
          <Grid item xs={12}>
            <TextField
              fullWidth
              label="Cardholder Name"
              value={paymentForm.cardholderName}
              onChange={(e) => setPaymentForm(prev => ({ ...prev, cardholderName: e.target.value }))}
              required
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
              required
            />
          </Grid>
          
          <Grid item xs={6}>
            <TextField
              fullWidth
              label="Account Number"
              value={paymentForm.accountNumber}
              onChange={(e) => setPaymentForm(prev => ({ ...prev, accountNumber: e.target.value }))}
              required
            />
          </Grid>
          
          <Grid item xs={6}>
            <TextField
              fullWidth
              label="Routing Number"
              value={paymentForm.routingNumber}
              onChange={(e) => setPaymentForm(prev => ({ ...prev, routingNumber: e.target.value }))}
              required
            />
          </Grid>
          
          <Grid item xs={12}>
            <FormControl fullWidth required>
              <InputLabel>Account Type</InputLabel>
              <Select
                value={paymentForm.accountType}
                label="Account Type"
                onChange={(e) => setPaymentForm(prev => ({ ...prev, accountType: e.target.value }))}
              >
                {bankAccountTypes.map(type => (
                  <MenuItem key={type.value} value={type.value}>
                    {type.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
        </>
      )}
      
      {paymentForm.type === 'paypal' && (
        <Grid item xs={12}>
          <TextField
            fullWidth
            label="PayPal Email"
            type="email"
            value={paymentForm.email}
            onChange={(e) => setPaymentForm(prev => ({ ...prev, email: e.target.value }))}
            required
          />
        </Grid>
      )}
      
      {paymentForm.type === 'crypto_wallet' && (
        <Grid item xs={12}>
          <TextField
            fullWidth
            label="Wallet Address"
            value={paymentForm.walletAddress}
            onChange={(e) => setPaymentForm(prev => ({ ...prev, walletAddress: e.target.value }))}
            placeholder="1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
            required
          />
        </Grid>
      )}
      
      <Grid item xs={12}>
        <Typography variant="subtitle2" gutterBottom>
          Security Features
        </Typography>
        <FormControlLabel
          control={
            <Switch
              checked={paymentForm.enableTwoFactor}
              onChange={(e) => setPaymentForm(prev => ({ ...prev, enableTwoFactor: e.target.checked }))}
            />
          }
          label="Enable Two-Factor Authentication"
        />
        <FormControlLabel
          control={
            <Switch
              checked={paymentForm.enableContactless}
              onChange={(e) => setPaymentForm(prev => ({ ...prev, enableContactless: e.target.checked }))}
            />
          }
          label="Enable Contactless Payments"
        />
        <FormControlLabel
          control={
            <Switch
              checked={paymentForm.enableBiometric}
              onChange={(e) => setPaymentForm(prev => ({ ...prev, enableBiometric: e.target.checked }))}
            />
          }
          label="Enable Biometric Authentication"
        />
      </Grid>
      
      <Grid item xs={12}>
        <Typography variant="subtitle2" gutterBottom>
          Transaction Limits
        </Typography>
      </Grid>
      
      <Grid item xs={4}>
        <TextField
          fullWidth
          label="Daily Limit"
          type="number"
          value={paymentForm.dailyLimit}
          onChange={(e) => setPaymentForm(prev => ({ ...prev, dailyLimit: parseFloat(e.target.value) || 0 }))}
          InputProps={{
            startAdornment: <InputAdornment position="start">$</InputAdornment>,
          }}
        />
      </Grid>
      
      <Grid item xs={4}>
        <TextField
          fullWidth
          label="Monthly Limit"
          type="number"
          value={paymentForm.monthlyLimit}
          onChange={(e) => setPaymentForm(prev => ({ ...prev, monthlyLimit: parseFloat(e.target.value) || 0 }))}
          InputProps={{
            startAdornment: <InputAdornment position="start">$</InputAdornment>,
          }}
        />
      </Grid>
      
      <Grid item xs={4}>
        <TextField
          fullWidth
          label="Single Transaction"
          type="number"
          value={paymentForm.singleTransactionLimit}
          onChange={(e) => setPaymentForm(prev => ({ ...prev, singleTransactionLimit: parseFloat(e.target.value) || 0 }))}
          InputProps={{
            startAdornment: <InputAdornment position="start">$</InputAdornment>,
          }}
        />
      </Grid>
    </Grid>
  );

  const renderSecuritySettings = () => (
    <Paper sx={{ p: 3 }}>
      <Typography variant="h6" gutterBottom>
        <Shield sx={{ mr: 1, verticalAlign: 'middle' }} />
        Security Settings
      </Typography>
      
      <List>
        <ListItem>
          <ListItemIcon>
            <Security />
          </ListItemIcon>
          <ListItemText
            primary="Two-Factor Authentication"
            secondary="Add an extra layer of security to your account"
          />
          <ListItemSecondaryAction>
            <Switch
              checked={securitySettings.twoFactorAuthEnabled}
              onChange={(e) => setSecuritySettings(prev => ({ 
                ...prev, 
                twoFactorAuthEnabled: e.target.checked 
              }))}
            />
          </ListItemSecondaryAction>
        </ListItem>
        
        <ListItem>
          <ListItemIcon>
            <ContactlessOutlined />
          </ListItemIcon>
          <ListItemText
            primary="Biometric Authentication"
            secondary="Use fingerprint or face recognition for payments"
          />
          <ListItemSecondaryAction>
            <Switch
              checked={securitySettings.biometricAuthEnabled}
              onChange={(e) => setSecuritySettings(prev => ({ 
                ...prev, 
                biometricAuthEnabled: e.target.checked 
              }))}
            />
          </ListItemSecondaryAction>
        </ListItem>
        
        <ListItem>
          <ListItemIcon>
            <Warning />
          </ListItemIcon>
          <ListItemText
            primary="Fraud Alerts"
            secondary="Get notified of suspicious activity"
          />
          <ListItemSecondaryAction>
            <Switch
              checked={securitySettings.fraudAlertsEnabled}
              onChange={(e) => setSecuritySettings(prev => ({ 
                ...prev, 
                fraudAlertsEnabled: e.target.checked 
              }))}
            />
          </ListItemSecondaryAction>
        </ListItem>
        
        <ListItem>
          <ListItemIcon>
            <Email />
          </ListItemIcon>
          <ListItemText
            primary="Transaction Notifications"
            secondary="Email notifications for all transactions"
          />
          <ListItemSecondaryAction>
            <Switch
              checked={securitySettings.transactionNotifications}
              onChange={(e) => setSecuritySettings(prev => ({ 
                ...prev, 
                transactionNotifications: e.target.checked 
              }))}
            />
          </ListItemSecondaryAction>
        </ListItem>
        
        <ListItem>
          <ListItemIcon>
            <Lock />
          </ListItemIcon>
          <ListItemText
            primary="Auto-Lock"
            secondary="Automatically lock payment methods after inactivity"
          />
          <ListItemSecondaryAction>
            <Switch
              checked={securitySettings.autoLockEnabled}
              onChange={(e) => setSecuritySettings(prev => ({ 
                ...prev, 
                autoLockEnabled: e.target.checked 
              }))}
            />
          </ListItemSecondaryAction>
        </ListItem>
        
        <ListItem>
          <ListItemIcon>
            <Shield />
          </ListItemIcon>
          <ListItemText
            primary="Secure Mode"
            secondary="Enhanced security for high-value transactions"
          />
          <ListItemSecondaryAction>
            <Switch
              checked={securitySettings.secureMode}
              onChange={(e) => setSecuritySettings(prev => ({ 
                ...prev, 
                secureMode: e.target.checked 
              }))}
            />
          </ListItemSecondaryAction>
        </ListItem>
      </List>
    </Paper>
  );

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Skeleton variant="text" width={300} height={40} />
        <Skeleton variant="text" width={200} height={24} sx={{ mb: 3 }} />
        <Grid container spacing={3}>
          {[1, 2, 3].map(i => (
            <Grid item xs={12} md={6} lg={4} key={i}>
              <Skeleton variant="rectangular" height={300} />
            </Grid>
          ))}
        </Grid>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Box>
          <Typography variant="h4" fontWeight="bold" gutterBottom>
            Payment Methods
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Manage your payment options and security settings
          </Typography>
        </Box>
        
        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={() => setAddDialog(true)}
          size="large"
        >
          Add Payment Method
        </Button>
      </Box>

      <Paper sx={{ mb: 3 }}>
        <Tabs value={activeTab} onChange={(_, newValue) => setActiveTab(newValue)}>
          <Tab label="Payment Methods" />
          <Tab label="Security Settings" />
          <Tab label="Transaction History" />
        </Tabs>
      </Paper>

      {activeTab === 0 && (
        <>
          {paymentMethods.length === 0 ? (
            <Paper sx={{ p: 4, textAlign: 'center' }}>
              <CreditCard sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
              <Typography variant="h6" gutterBottom>
                No payment methods added
              </Typography>
              <Typography variant="body1" color="text.secondary" paragraph>
                Add your first payment method to start making secure transactions.
              </Typography>
              <Button
                variant="contained"
                startIcon={<Add />}
                onClick={() => setAddDialog(true)}
              >
                Add Payment Method
              </Button>
            </Paper>
          ) : (
            <Grid container spacing={3}>
              {paymentMethods.map(method => (
                <Grid item xs={12} md={6} lg={4} key={method.id}>
                  {renderPaymentMethodCard(method)}
                </Grid>
              ))}
            </Grid>
          )}
        </>
      )}

      {activeTab === 1 && renderSecuritySettings()}

      {activeTab === 2 && (
        <Paper sx={{ p: 3 }}>
          <Typography variant="h6" gutterBottom>
            <History sx={{ mr: 1, verticalAlign: 'middle' }} />
            Transaction History
          </Typography>
          <Typography variant="body2" color="text.secondary">
            View your payment transaction history and manage recurring payments.
          </Typography>
          <Button
            variant="outlined"
            startIcon={<History />}
            onClick={() => navigate('/billing/history')}
            sx={{ mt: 2 }}
          >
            View Full History
          </Button>
        </Paper>
      )}

      {/* Action Menu */}
      <Menu
        anchorEl={menuAnchor}
        open={Boolean(menuAnchor)}
        onClose={() => setMenuAnchor(null)}
      >
        <MenuList>
          <MenuItem onClick={() => {
            setMenuAnchor(null);
            // View details
          }}>
            <ListItemIcon><Visibility /></ListItemIcon>
            <ListItemText>View Details</ListItemText>
          </MenuItem>
          
          <MenuItem onClick={() => {
            setEditDialog(true);
            setMenuAnchor(null);
          }}>
            <ListItemIcon><Edit /></ListItemIcon>
            <ListItemText>Edit</ListItemText>
          </MenuItem>
          
          <MenuItem onClick={() => {
            setSecurityDialog(true);
            setMenuAnchor(null);
          }}>
            <ListItemIcon><Security /></ListItemIcon>
            <ListItemText>Security Settings</ListItemText>
          </MenuItem>
          
          <Divider />
          
          <MenuItem 
            onClick={() => {
              setDeleteDialog(true);
              setMenuAnchor(null);
            }}
            sx={{ color: 'error.main' }}
          >
            <ListItemIcon sx={{ color: 'error.main' }}><Delete /></ListItemIcon>
            <ListItemText>Remove</ListItemText>
          </MenuItem>
        </MenuList>
      </Menu>

      {/* Add Payment Method Dialog */}
      <Dialog open={addDialog} onClose={() => setAddDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Add Payment Method</DialogTitle>
        <DialogContent>
          <Box sx={{ mt: 1 }}>
            {renderPaymentForm()}
            
            <Alert severity="info" sx={{ mt: 2 }}>
              <Security sx={{ mr: 1 }} />
              Your payment information is encrypted and stored securely using industry-standard security measures.
            </Alert>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => {
            setAddDialog(false);
            resetForm();
          }}>
            Cancel
          </Button>
          <Button
            onClick={handleAddPaymentMethod}
            variant="contained"
            disabled={actionLoading === 'add'}
            startIcon={actionLoading === 'add' ? <CircularProgress size={20} /> : <Add />}
          >
            Add Payment Method
          </Button>
        </DialogActions>
      </Dialog>

      {/* Verification Dialog */}
      <Dialog open={verifyDialog} onClose={() => setVerifyDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Verify Payment Method</DialogTitle>
        <DialogContent>
          {verificationStep === 0 && (
            <Box sx={{ textAlign: 'center', py: 2 }}>
              <Security sx={{ fontSize: 64, color: 'primary.main', mb: 2 }} />
              <Typography variant="h6" gutterBottom>
                Verification Required
              </Typography>
              <Typography variant="body2" color="text.secondary" paragraph>
                We need to verify your payment method for security purposes. 
                We'll send a verification code to your registered contact method.
              </Typography>
              <Button
                variant="contained"
                onClick={() => setVerificationStep(1)}
                fullWidth
              >
                Send Verification Code
              </Button>
            </Box>
          )}
          
          {verificationStep === 1 && (
            <Box sx={{ py: 2 }}>
              <Typography variant="h6" gutterBottom>
                Enter Verification Code
              </Typography>
              <Typography variant="body2" color="text.secondary" paragraph>
                Enter the 6-digit code we sent to your registered email or phone number.
              </Typography>
              <TextField
                fullWidth
                label="Verification Code"
                value={verificationCode}
                onChange={(e) => setVerificationCode(e.target.value)}
                placeholder="123456"
                inputProps={{ maxLength: 6 }}
                sx={{ mb: 2 }}
              />
              <Button
                variant="text"
                onClick={() => setVerificationStep(0)}
              >
                Resend Code
              </Button>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setVerifyDialog(false)}>Cancel</Button>
          {verificationStep === 1 && (
            <Button
              onClick={handleVerificationSubmit}
              variant="contained"
              disabled={actionLoading === 'verify' || verificationCode.length !== 6}
              startIcon={actionLoading === 'verify' ? <CircularProgress size={20} /> : <Verified />}
            >
              Verify
            </Button>
          )}
        </DialogActions>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialog} onClose={() => setDeleteDialog(false)}>
        <DialogTitle>Remove Payment Method</DialogTitle>
        <DialogContent>
          <Typography>
            Are you sure you want to remove "{selectedMethod?.name}"? This action cannot be undone.
          </Typography>
          {selectedMethod?.isDefault && (
            <Alert severity="warning" sx={{ mt: 2 }}>
              This is your default payment method. You'll need to set a new default payment method.
            </Alert>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialog(false)}>Cancel</Button>
          <Button
            onClick={handleDeletePaymentMethod}
            color="error"
            variant="contained"
            disabled={actionLoading === 'delete'}
            startIcon={actionLoading === 'delete' ? <CircularProgress size={20} /> : <Delete />}
          >
            Remove
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default PaymentMethods;