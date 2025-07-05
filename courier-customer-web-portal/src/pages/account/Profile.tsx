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
  CardHeader,
  Avatar,
  IconButton,
  Divider,
  Switch,
  FormControlLabel,
  Alert,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Tab,
  Tabs,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
} from '@mui/material';
import {
  Edit,
  Save,
  Cancel,
  PhotoCamera,
  Security,
  Notifications,
  LocationOn,
  Phone,
  Email,
  Business,
  Person,
  Delete,
  Add,
  CreditCard,
} from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';
import { useNotification } from '../../contexts/NotificationContext';

interface UserProfile {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  avatar?: string;
  company?: string;
  accountType: 'individual' | 'business';
  addresses: Address[];
  preferences: UserPreferences;
  notifications: NotificationSettings;
  paymentMethods: PaymentMethod[];
}

interface Address {
  id: string;
  type: 'home' | 'work' | 'other';
  address: string;
  city: string;
  state: string;
  zipCode: string;
  isDefault: boolean;
}

interface UserPreferences {
  language: string;
  timezone: string;
  currency: string;
  defaultServiceType: string;
}

interface NotificationSettings {
  email: boolean;
  sms: boolean;
  push: boolean;
  marketingEmails: boolean;
  orderUpdates: boolean;
  promotions: boolean;
}

interface PaymentMethod {
  id: string;
  type: 'card' | 'bank';
  lastFour: string;
  brand?: string;
  isDefault: boolean;
  expiryDate?: string;
}

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

const TabPanel: React.FC<TabPanelProps> = ({ children, value, index, ...other }) => {
  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`profile-tabpanel-${index}`}
      aria-labelledby={`profile-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ py: 3 }}>{children}</Box>}
    </div>
  );
};

const Profile: React.FC = () => {
  const { user } = useAuth();
  const { showNotification } = useNotification();
  
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [editingProfile, setEditingProfile] = useState(false);
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState(0);
  const [addressDialog, setAddressDialog] = useState(false);
  const [newAddress, setNewAddress] = useState<Partial<Address>>({
    type: 'home',
    address: '',
    city: '',
    state: '',
    zipCode: '',
    isDefault: false,
  });

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    setLoading(true);
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Mock profile data
      const mockProfile: UserProfile = {
        id: user?.id || '1',
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@email.com',
        phone: '+1 (555) 123-4567',
        company: 'Acme Corporation',
        accountType: 'business',
        addresses: [
          {
            id: '1',
            type: 'work',
            address: '123 Business St',
            city: 'New York',
            state: 'NY',
            zipCode: '10001',
            isDefault: true,
          },
          {
            id: '2',
            type: 'home',
            address: '456 Home Ave',
            city: 'Brooklyn',
            state: 'NY',
            zipCode: '11201',
            isDefault: false,
          },
        ],
        preferences: {
          language: 'en',
          timezone: 'America/New_York',
          currency: 'USD',
          defaultServiceType: 'express',
        },
        notifications: {
          email: true,
          sms: true,
          push: true,
          marketingEmails: false,
          orderUpdates: true,
          promotions: false,
        },
        paymentMethods: [
          {
            id: '1',
            type: 'card',
            lastFour: '4242',
            brand: 'Visa',
            isDefault: true,
            expiryDate: '12/25',
          },
          {
            id: '2',
            type: 'card',
            lastFour: '8888',
            brand: 'MasterCard',
            isDefault: false,
            expiryDate: '08/26',
          },
        ],
      };
      
      setProfile(mockProfile);
    } catch (error) {
      showNotification('Failed to load profile', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleSaveProfile = async () => {
    if (!profile) return;
    
    setLoading(true);
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      setEditingProfile(false);
      showNotification('Profile updated successfully', 'success');
    } catch (error) {
      showNotification('Failed to update profile', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleAddAddress = async () => {
    if (!profile || !newAddress.address || !newAddress.city || !newAddress.state || !newAddress.zipCode) {
      showNotification('Please fill in all address fields', 'warning');
      return;
    }
    
    const addressToAdd: Address = {
      id: `addr_${Date.now()}`,
      type: newAddress.type as 'home' | 'work' | 'other',
      address: newAddress.address,
      city: newAddress.city,
      state: newAddress.state,
      zipCode: newAddress.zipCode,
      isDefault: newAddress.isDefault || false,
    };
    
    setProfile({
      ...profile,
      addresses: [...profile.addresses, addressToAdd],
    });
    
    setAddressDialog(false);
    setNewAddress({
      type: 'home',
      address: '',
      city: '',
      state: '',
      zipCode: '',
      isDefault: false,
    });
    
    showNotification('Address added successfully', 'success');
  };

  const handleDeleteAddress = (addressId: string) => {
    if (!profile) return;
    
    setProfile({
      ...profile,
      addresses: profile.addresses.filter(addr => addr.id !== addressId),
    });
    
    showNotification('Address deleted successfully', 'success');
  };

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setActiveTab(newValue);
  };

  const handleNotificationChange = (setting: keyof NotificationSettings) => {
    if (!profile) return;
    
    setProfile({
      ...profile,
      notifications: {
        ...profile.notifications,
        [setting]: !profile.notifications[setting],
      },
    });
  };

  if (loading && !profile) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Typography>Loading profile...</Typography>
      </Container>
    );
  }

  if (!profile) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Alert severity="error">Failed to load profile</Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" fontWeight="bold" gutterBottom>
        Account Profile
      </Typography>
      
      <Paper sx={{ mb: 4 }}>
        <Tabs
          value={activeTab}
          onChange={handleTabChange}
          sx={{ borderBottom: 1, borderColor: 'divider' }}
        >
          <Tab label="Personal Information" icon={<Person />} iconPosition="start" />
          <Tab label="Addresses" icon={<LocationOn />} iconPosition="start" />
          <Tab label="Notifications" icon={<Notifications />} iconPosition="start" />
          <Tab label="Payment Methods" icon={<CreditCard />} iconPosition="start" />
          <Tab label="Security" icon={<Security />} iconPosition="start" />
        </Tabs>

        {/* Personal Information Tab */}
        <TabPanel value={activeTab} index={0}>
          <Grid container spacing={3}>
            {/* Profile Header */}
            <Grid item xs={12}>
              <Card>
                <CardContent>
                  <Box display="flex" alignItems="center" gap={3}>
                    <Box position="relative">
                      <Avatar
                        src={profile.avatar}
                        sx={{ width: 80, height: 80 }}
                      >
                        {profile.firstName[0]}{profile.lastName[0]}
                      </Avatar>
                      <IconButton
                        size="small"
                        sx={{
                          position: 'absolute',
                          bottom: 0,
                          right: 0,
                          bgcolor: 'primary.main',
                          color: 'white',
                          '&:hover': { bgcolor: 'primary.dark' },
                        }}
                      >
                        <PhotoCamera fontSize="small" />
                      </IconButton>
                    </Box>
                    
                    <Box flex={1}>
                      <Typography variant="h6" fontWeight="bold">
                        {profile.firstName} {profile.lastName}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {profile.email}
                      </Typography>
                      <Chip
                        label={profile.accountType === 'business' ? 'Business Account' : 'Individual Account'}
                        size="small"
                        color="primary"
                        variant="outlined"
                        sx={{ mt: 1 }}
                      />
                    </Box>
                    
                    <Button
                      variant={editingProfile ? "contained" : "outlined"}
                      startIcon={editingProfile ? <Save /> : <Edit />}
                      onClick={editingProfile ? handleSaveProfile : () => setEditingProfile(true)}
                      disabled={loading}
                    >
                      {editingProfile ? 'Save Changes' : 'Edit Profile'}
                    </Button>
                    
                    {editingProfile && (
                      <Button
                        variant="outlined"
                        startIcon={<Cancel />}
                        onClick={() => setEditingProfile(false)}
                      >
                        Cancel
                      </Button>
                    )}
                  </Box>
                </CardContent>
              </Card>
            </Grid>

            {/* Basic Information */}
            <Grid item xs={12} md={6}>
              <Card>
                <CardHeader title="Basic Information" />
                <CardContent>
                  <Grid container spacing={2}>
                    <Grid item xs={6}>
                      <TextField
                        fullWidth
                        label="First Name"
                        value={profile.firstName}
                        onChange={(e) => setProfile({ ...profile, firstName: e.target.value })}
                        disabled={!editingProfile}
                      />
                    </Grid>
                    <Grid item xs={6}>
                      <TextField
                        fullWidth
                        label="Last Name"
                        value={profile.lastName}
                        onChange={(e) => setProfile({ ...profile, lastName: e.target.value })}
                        disabled={!editingProfile}
                      />
                    </Grid>
                    <Grid item xs={12}>
                      <TextField
                        fullWidth
                        label="Email Address"
                        value={profile.email}
                        onChange={(e) => setProfile({ ...profile, email: e.target.value })}
                        disabled={!editingProfile}
                        type="email"
                      />
                    </Grid>
                    <Grid item xs={12}>
                      <TextField
                        fullWidth
                        label="Phone Number"
                        value={profile.phone}
                        onChange={(e) => setProfile({ ...profile, phone: e.target.value })}
                        disabled={!editingProfile}
                      />
                    </Grid>
                    {profile.accountType === 'business' && (
                      <Grid item xs={12}>
                        <TextField
                          fullWidth
                          label="Company Name"
                          value={profile.company || ''}
                          onChange={(e) => setProfile({ ...profile, company: e.target.value })}
                          disabled={!editingProfile}
                        />
                      </Grid>
                    )}
                  </Grid>
                </CardContent>
              </Card>
            </Grid>

            {/* Preferences */}
            <Grid item xs={12} md={6}>
              <Card>
                <CardHeader title="Preferences" />
                <CardContent>
                  <Grid container spacing={2}>
                    <Grid item xs={12}>
                      <FormControl fullWidth disabled={!editingProfile}>
                        <InputLabel>Language</InputLabel>
                        <Select
                          value={profile.preferences.language}
                          label="Language"
                          onChange={(e) => setProfile({
                            ...profile,
                            preferences: { ...profile.preferences, language: e.target.value }
                          })}
                        >
                          <MenuItem value="en">English</MenuItem>
                          <MenuItem value="es">Spanish</MenuItem>
                          <MenuItem value="fr">French</MenuItem>
                        </Select>
                      </FormControl>
                    </Grid>
                    <Grid item xs={12}>
                      <FormControl fullWidth disabled={!editingProfile}>
                        <InputLabel>Timezone</InputLabel>
                        <Select
                          value={profile.preferences.timezone}
                          label="Timezone"
                          onChange={(e) => setProfile({
                            ...profile,
                            preferences: { ...profile.preferences, timezone: e.target.value }
                          })}
                        >
                          <MenuItem value="America/New_York">Eastern Time</MenuItem>
                          <MenuItem value="America/Chicago">Central Time</MenuItem>
                          <MenuItem value="America/Denver">Mountain Time</MenuItem>
                          <MenuItem value="America/Los_Angeles">Pacific Time</MenuItem>
                        </Select>
                      </FormControl>
                    </Grid>
                    <Grid item xs={12}>
                      <FormControl fullWidth disabled={!editingProfile}>
                        <InputLabel>Default Service Type</InputLabel>
                        <Select
                          value={profile.preferences.defaultServiceType}
                          label="Default Service Type"
                          onChange={(e) => setProfile({
                            ...profile,
                            preferences: { ...profile.preferences, defaultServiceType: e.target.value }
                          })}
                        >
                          <MenuItem value="standard">Standard Delivery</MenuItem>
                          <MenuItem value="express">Express Delivery</MenuItem>
                          <MenuItem value="overnight">Overnight Delivery</MenuItem>
                          <MenuItem value="same_day">Same Day Delivery</MenuItem>
                        </Select>
                      </FormControl>
                    </Grid>
                  </Grid>
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        </TabPanel>

        {/* Addresses Tab */}
        <TabPanel value={activeTab} index={1}>
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
            <Typography variant="h6" fontWeight="bold">
              Saved Addresses
            </Typography>
            <Button
              variant="contained"
              startIcon={<Add />}
              onClick={() => setAddressDialog(true)}
            >
              Add Address
            </Button>
          </Box>
          
          <Grid container spacing={2}>
            {profile.addresses.map((address) => (
              <Grid item xs={12} md={6} key={address.id}>
                <Card>
                  <CardContent>
                    <Box display="flex" justifyContent="space-between" alignItems="flex-start">
                      <Box>
                        <Box display="flex" alignItems="center" gap={1} mb={1}>
                          <Chip
                            label={address.type.charAt(0).toUpperCase() + address.type.slice(1)}
                            size="small"
                            color="primary"
                            variant="outlined"
                          />
                          {address.isDefault && (
                            <Chip
                              label="Default"
                              size="small"
                              color="success"
                              variant="filled"
                            />
                          )}
                        </Box>
                        <Typography variant="body2">
                          {address.address}<br />
                          {address.city}, {address.state} {address.zipCode}
                        </Typography>
                      </Box>
                      <IconButton
                        size="small"
                        onClick={() => handleDeleteAddress(address.id)}
                        color="error"
                      >
                        <Delete />
                      </IconButton>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </TabPanel>

        {/* Notifications Tab */}
        <TabPanel value={activeTab} index={2}>
          <Card>
            <CardHeader title="Notification Preferences" />
            <CardContent>
              <Typography variant="body2" color="text.secondary" mb={3}>
                Choose how you'd like to receive notifications about your shipments and account.
              </Typography>
              
              <Grid container spacing={3}>
                <Grid item xs={12}>
                  <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
                    Delivery Methods
                  </Typography>
                  <List>
                    <ListItem>
                      <ListItemText
                        primary="Email Notifications"
                        secondary="Receive notifications via email"
                      />
                      <ListItemSecondaryAction>
                        <Switch
                          checked={profile.notifications.email}
                          onChange={() => handleNotificationChange('email')}
                        />
                      </ListItemSecondaryAction>
                    </ListItem>
                    <ListItem>
                      <ListItemText
                        primary="SMS Notifications"
                        secondary="Receive notifications via text message"
                      />
                      <ListItemSecondaryAction>
                        <Switch
                          checked={profile.notifications.sms}
                          onChange={() => handleNotificationChange('sms')}
                        />
                      </ListItemSecondaryAction>
                    </ListItem>
                    <ListItem>
                      <ListItemText
                        primary="Push Notifications"
                        secondary="Receive notifications in the app"
                      />
                      <ListItemSecondaryAction>
                        <Switch
                          checked={profile.notifications.push}
                          onChange={() => handleNotificationChange('push')}
                        />
                      </ListItemSecondaryAction>
                    </ListItem>
                  </List>
                </Grid>
                
                <Grid item xs={12}>
                  <Divider />
                </Grid>
                
                <Grid item xs={12}>
                  <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
                    Notification Types
                  </Typography>
                  <List>
                    <ListItem>
                      <ListItemText
                        primary="Order Updates"
                        secondary="Notifications about shipment status changes"
                      />
                      <ListItemSecondaryAction>
                        <Switch
                          checked={profile.notifications.orderUpdates}
                          onChange={() => handleNotificationChange('orderUpdates')}
                        />
                      </ListItemSecondaryAction>
                    </ListItem>
                    <ListItem>
                      <ListItemText
                        primary="Marketing Emails"
                        secondary="Promotional content and service updates"
                      />
                      <ListItemSecondaryAction>
                        <Switch
                          checked={profile.notifications.marketingEmails}
                          onChange={() => handleNotificationChange('marketingEmails')}
                        />
                      </ListItemSecondaryAction>
                    </ListItem>
                    <ListItem>
                      <ListItemText
                        primary="Promotions"
                        secondary="Special offers and discounts"
                      />
                      <ListItemSecondaryAction>
                        <Switch
                          checked={profile.notifications.promotions}
                          onChange={() => handleNotificationChange('promotions')}
                        />
                      </ListItemSecondaryAction>
                    </ListItem>
                  </List>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </TabPanel>

        {/* Payment Methods Tab */}
        <TabPanel value={activeTab} index={3}>
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
            <Typography variant="h6" fontWeight="bold">
              Payment Methods
            </Typography>
            <Button
              variant="contained"
              startIcon={<Add />}
              onClick={() => showNotification('Add payment method coming soon', 'info')}
            >
              Add Payment Method
            </Button>
          </Box>
          
          <Grid container spacing={2}>
            {profile.paymentMethods.map((method) => (
              <Grid item xs={12} md={6} key={method.id}>
                <Card>
                  <CardContent>
                    <Box display="flex" justifyContent="space-between" alignItems="center">
                      <Box display="flex" alignItems="center" gap={2}>
                        <CreditCard color="primary" />
                        <Box>
                          <Typography variant="subtitle2">
                            {method.brand} •••• {method.lastFour}
                          </Typography>
                          {method.expiryDate && (
                            <Typography variant="body2" color="text.secondary">
                              Expires {method.expiryDate}
                            </Typography>
                          )}
                          {method.isDefault && (
                            <Chip
                              label="Default"
                              size="small"
                              color="success"
                              variant="filled"
                              sx={{ mt: 0.5 }}
                            />
                          )}
                        </Box>
                      </Box>
                      <IconButton
                        size="small"
                        color="error"
                        onClick={() => showNotification('Remove payment method coming soon', 'info')}
                      >
                        <Delete />
                      </IconButton>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </TabPanel>

        {/* Security Tab */}
        <TabPanel value={activeTab} index={4}>
          <Grid container spacing={3}>
            <Grid item xs={12} md={6}>
              <Card>
                <CardHeader title="Password" />
                <CardContent>
                  <Typography variant="body2" color="text.secondary" mb={2}>
                    Keep your account secure by using a strong password.
                  </Typography>
                  <Button
                    variant="outlined"
                    onClick={() => showNotification('Change password coming soon', 'info')}
                    fullWidth
                  >
                    Change Password
                  </Button>
                </CardContent>
              </Card>
            </Grid>
            
            <Grid item xs={12} md={6}>
              <Card>
                <CardHeader title="Two-Factor Authentication" />
                <CardContent>
                  <Typography variant="body2" color="text.secondary" mb={2}>
                    Add an extra layer of security to your account.
                  </Typography>
                  <Button
                    variant="outlined"
                    onClick={() => showNotification('Two-factor authentication coming soon', 'info')}
                    fullWidth
                  >
                    Enable 2FA
                  </Button>
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        </TabPanel>
      </Paper>

      {/* Add Address Dialog */}
      <Dialog open={addressDialog} onClose={() => setAddressDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Add New Address</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <FormControl fullWidth>
                <InputLabel>Address Type</InputLabel>
                <Select
                  value={newAddress.type}
                  label="Address Type"
                  onChange={(e) => setNewAddress({ ...newAddress, type: e.target.value as 'home' | 'work' | 'other' })}
                >
                  <MenuItem value="home">Home</MenuItem>
                  <MenuItem value="work">Work</MenuItem>
                  <MenuItem value="other">Other</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Street Address"
                value={newAddress.address}
                onChange={(e) => setNewAddress({ ...newAddress, address: e.target.value })}
              />
            </Grid>
            <Grid item xs={6}>
              <TextField
                fullWidth
                label="City"
                value={newAddress.city}
                onChange={(e) => setNewAddress({ ...newAddress, city: e.target.value })}
              />
            </Grid>
            <Grid item xs={3}>
              <TextField
                fullWidth
                label="State"
                value={newAddress.state}
                onChange={(e) => setNewAddress({ ...newAddress, state: e.target.value })}
              />
            </Grid>
            <Grid item xs={3}>
              <TextField
                fullWidth
                label="ZIP Code"
                value={newAddress.zipCode}
                onChange={(e) => setNewAddress({ ...newAddress, zipCode: e.target.value })}
              />
            </Grid>
            <Grid item xs={12}>
              <FormControlLabel
                control={
                  <Switch
                    checked={newAddress.isDefault}
                    onChange={(e) => setNewAddress({ ...newAddress, isDefault: e.target.checked })}
                  />
                }
                label="Set as default address"
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setAddressDialog(false)}>Cancel</Button>
          <Button onClick={handleAddAddress} variant="contained">Add Address</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default Profile;