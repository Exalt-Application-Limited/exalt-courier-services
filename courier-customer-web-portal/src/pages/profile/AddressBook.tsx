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
  Fab,
  Collapse,
  InputAdornment,
} from '@mui/material';
import {
  Home,
  Business,
  LocationOn,
  Add,
  Edit,
  Delete,
  MoreVert,
  Star,
  StarBorder,
  Map,
  Directions,
  Copy,
  Share,
  Visibility,
  VisibilityOff,
  Search,
  FilterList,
  Clear,
  Check,
  GPS,
  Verified,
  Warning,
  ExpandMore,
  ExpandLess,
  NavigateNext,
  Place,
  Public,
  Lock,
} from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';
import { useNotification } from '../../contexts/NotificationContext';

interface Address {
  id: string;
  label: string;
  type: 'home' | 'work' | 'other';
  name: string;
  company?: string;
  address: string;
  address2?: string;
  city: string;
  state: string;
  zipCode: string;
  country: string;
  phone?: string;
  email?: string;
  instructions?: string;
  isDefault: boolean;
  isFavorite: boolean;
  isVerified: boolean;
  latitude?: number;
  longitude?: number;
  lastUsed?: string;
  usageCount: number;
  createdAt: string;
  updatedAt: string;
  visibility: 'private' | 'shared' | 'public';
  tags: string[];
}

interface AddressForm {
  label: string;
  type: 'home' | 'work' | 'other';
  name: string;
  company: string;
  address: string;
  address2: string;
  city: string;
  state: string;
  zipCode: string;
  country: string;
  phone: string;
  email: string;
  instructions: string;
  visibility: 'private' | 'shared' | 'public';
  tags: string[];
}

interface AddressFilter {
  type?: string;
  search?: string;
  favorites?: boolean;
  verified?: boolean;
}

const AddressBook: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { showNotification } = useNotification();
  
  // Data states
  const [addresses, setAddresses] = useState<Address[]>([]);
  const [filteredAddresses, setFilteredAddresses] = useState<Address[]>([]);
  
  // UI states
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState<string | null>(null);
  const [addDialog, setAddDialog] = useState(false);
  const [editDialog, setEditDialog] = useState(false);
  const [deleteDialog, setDeleteDialog] = useState(false);
  const [viewDialog, setViewDialog] = useState(false);
  const [verifyDialog, setVerifyDialog] = useState(false);
  const [selectedAddress, setSelectedAddress] = useState<Address | null>(null);
  const [menuAnchor, setMenuAnchor] = useState<null | HTMLElement>(null);
  const [filterExpanded, setFilterExpanded] = useState(false);
  
  // Form and filter states
  const [addressForm, setAddressForm] = useState<AddressForm>({
    label: '',
    type: 'home',
    name: '',
    company: '',
    address: '',
    address2: '',
    city: '',
    state: '',
    zipCode: '',
    country: 'US',
    phone: '',
    email: '',
    instructions: '',
    visibility: 'private',
    tags: [],
  });
  
  const [filters, setFilters] = useState<AddressFilter>({
    type: '',
    search: '',
    favorites: false,
    verified: false,
  });
  
  const [newTag, setNewTag] = useState('');

  const addressTypes = [
    { value: 'home', label: 'Home', icon: <Home /> },
    { value: 'work', label: 'Work', icon: <Business /> },
    { value: 'other', label: 'Other', icon: <LocationOn /> },
  ];

  const countries = [
    { value: 'US', label: 'United States' },
    { value: 'CA', label: 'Canada' },
    { value: 'GB', label: 'United Kingdom' },
    { value: 'AU', label: 'Australia' },
  ];

  const visibilityOptions = [
    { value: 'private', label: 'Private', icon: <Lock />, description: 'Only visible to you' },
    { value: 'shared', label: 'Shared', icon: <Share />, description: 'Visible to family members' },
    { value: 'public', label: 'Public', icon: <Public />, description: 'Visible to delivery partners' },
  ];

  useEffect(() => {
    loadAddresses();
  }, []);

  useEffect(() => {
    applyFilters();
  }, [addresses, filters]);

  const loadAddresses = async () => {
    setLoading(true);
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Mock data
      const mockAddresses: Address[] = [
        {
          id: '1',
          label: 'Home',
          type: 'home',
          name: 'John Doe',
          address: '123 Main Street',
          address2: 'Apt 4B',
          city: 'New York',
          state: 'NY',
          zipCode: '10001',
          country: 'US',
          phone: '+1 (555) 123-4567',
          email: 'john@example.com',
          instructions: 'Ring doorbell twice. Package can be left with doorman.',
          isDefault: true,
          isFavorite: true,
          isVerified: true,
          latitude: 40.7128,
          longitude: -74.0060,
          lastUsed: '2024-06-20',
          usageCount: 15,
          createdAt: '2024-01-15',
          updatedAt: '2024-06-20',
          visibility: 'private',
          tags: ['primary', 'verified'],
        },
        {
          id: '2',
          label: 'Office',
          type: 'work',
          name: 'John Doe',
          company: 'Acme Corporation',
          address: '456 Business Ave',
          city: 'New York',
          state: 'NY',
          zipCode: '10016',
          country: 'US',
          phone: '+1 (555) 987-6543',
          instructions: 'Deliver to reception desk on 5th floor.',
          isDefault: false,
          isFavorite: true,
          isVerified: true,
          latitude: 40.7589,
          longitude: -73.9851,
          lastUsed: '2024-06-18',
          usageCount: 8,
          createdAt: '2024-02-01',
          updatedAt: '2024-06-18',
          visibility: 'shared',
          tags: ['work', 'secure'],
        },
        {
          id: '3',
          label: "Mom's House",
          type: 'other',
          name: 'Jane Doe',
          address: '789 Family Lane',
          city: 'Brooklyn',
          state: 'NY',
          zipCode: '11201',
          country: 'US',
          phone: '+1 (555) 555-0123',
          instructions: 'Please call before delivery. Dog in yard.',
          isDefault: false,
          isFavorite: false,
          isVerified: false,
          lastUsed: '2024-05-15',
          usageCount: 3,
          createdAt: '2024-03-10',
          updatedAt: '2024-05-15',
          visibility: 'private',
          tags: ['family'],
        },
      ];
      
      setAddresses(mockAddresses);
    } catch (error) {
      showNotification('Failed to load addresses', 'error');
    } finally {
      setLoading(false);
    }
  };

  const applyFilters = () => {
    let filtered = [...addresses];
    
    if (filters.type) {
      filtered = filtered.filter(addr => addr.type === filters.type);
    }
    
    if (filters.search) {
      const search = filters.search.toLowerCase();
      filtered = filtered.filter(addr => 
        addr.label.toLowerCase().includes(search) ||
        addr.name.toLowerCase().includes(search) ||
        addr.address.toLowerCase().includes(search) ||
        addr.city.toLowerCase().includes(search) ||
        (addr.company && addr.company.toLowerCase().includes(search))
      );
    }
    
    if (filters.favorites) {
      filtered = filtered.filter(addr => addr.isFavorite);
    }
    
    if (filters.verified) {
      filtered = filtered.filter(addr => addr.isVerified);
    }
    
    setFilteredAddresses(filtered);
  };

  const resetForm = () => {
    setAddressForm({
      label: '',
      type: 'home',
      name: '',
      company: '',
      address: '',
      address2: '',
      city: '',
      state: '',
      zipCode: '',
      country: 'US',
      phone: '',
      email: '',
      instructions: '',
      visibility: 'private',
      tags: [],
    });
  };

  const handleAddAddress = async () => {
    setActionLoading('add');
    try {
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      const newAddress: Address = {
        id: Date.now().toString(),
        ...addressForm,
        isDefault: addresses.length === 0,
        isFavorite: false,
        isVerified: false,
        usageCount: 0,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };
      
      setAddresses(prev => [...prev, newAddress]);
      setAddDialog(false);
      resetForm();
      showNotification('Address added successfully', 'success');
    } catch (error) {
      showNotification('Failed to add address', 'error');
    } finally {
      setActionLoading(null);
    }
  };

  const handleEditAddress = async () => {
    if (!selectedAddress) return;
    
    setActionLoading('edit');
    try {
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      setAddresses(prev => prev.map(addr => 
        addr.id === selectedAddress.id 
          ? { ...addr, ...addressForm, updatedAt: new Date().toISOString() }
          : addr
      ));
      
      setEditDialog(false);
      setSelectedAddress(null);
      resetForm();
      showNotification('Address updated successfully', 'success');
    } catch (error) {
      showNotification('Failed to update address', 'error');
    } finally {
      setActionLoading(null);
    }
  };

  const handleDeleteAddress = async () => {
    if (!selectedAddress) return;
    
    setActionLoading('delete');
    try {
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      setAddresses(prev => prev.filter(addr => addr.id !== selectedAddress.id));
      setDeleteDialog(false);
      setSelectedAddress(null);
      showNotification('Address deleted successfully', 'success');
    } catch (error) {
      showNotification('Failed to delete address', 'error');
    } finally {
      setActionLoading(null);
    }
  };

  const handleSetDefault = async (addressId: string) => {
    try {
      setAddresses(prev => prev.map(addr => ({
        ...addr,
        isDefault: addr.id === addressId,
      })));
      showNotification('Default address updated', 'success');
    } catch (error) {
      showNotification('Failed to update default address', 'error');
    }
  };

  const handleToggleFavorite = async (addressId: string) => {
    try {
      setAddresses(prev => prev.map(addr => 
        addr.id === addressId 
          ? { ...addr, isFavorite: !addr.isFavorite }
          : addr
      ));
      showNotification('Favorite status updated', 'success');
    } catch (error) {
      showNotification('Failed to update favorite status', 'error');
    }
  };

  const handleVerifyAddress = async (address: Address) => {
    setActionLoading(address.id);
    try {
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      setAddresses(prev => prev.map(addr => 
        addr.id === address.id 
          ? { ...addr, isVerified: true, latitude: 40.7128, longitude: -74.0060 }
          : addr
      ));
      
      showNotification('Address verified successfully', 'success');
    } catch (error) {
      showNotification('Failed to verify address', 'error');
    } finally {
      setActionLoading(null);
    }
  };

  const handleCopyAddress = (address: Address) => {
    const addressText = `${address.name}\n${address.address}${address.address2 ? '\n' + address.address2 : ''}\n${address.city}, ${address.state} ${address.zipCode}\n${address.country}`;
    navigator.clipboard.writeText(addressText);
    showNotification('Address copied to clipboard', 'success');
  };

  const handleGetDirections = (address: Address) => {
    const query = encodeURIComponent(`${address.address}, ${address.city}, ${address.state} ${address.zipCode}`);
    window.open(`https://www.google.com/maps/dir/?api=1&destination=${query}`, '_blank');
  };

  const handleAddTag = () => {
    if (newTag.trim() && !addressForm.tags.includes(newTag.trim())) {
      setAddressForm(prev => ({
        ...prev,
        tags: [...prev.tags, newTag.trim()],
      }));
      setNewTag('');
    }
  };

  const handleRemoveTag = (tagToRemove: string) => {
    setAddressForm(prev => ({
      ...prev,
      tags: prev.tags.filter(tag => tag !== tagToRemove),
    }));
  };

  const getAddressTypeIcon = (type: string) => {
    const addressType = addressTypes.find(t => t.value === type);
    return addressType?.icon || <LocationOn />;
  };

  const renderFilters = () => (
    <Paper sx={{ p: 2, mb: 3 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h6">
          <FilterList sx={{ mr: 1, verticalAlign: 'middle' }} />
          Filters
        </Typography>
        <Button
          onClick={() => setFilterExpanded(!filterExpanded)}
          endIcon={filterExpanded ? <ExpandLess /> : <ExpandMore />}
        >
          {filterExpanded ? 'Hide' : 'Show'} Filters
        </Button>
      </Box>
      
      <Collapse in={filterExpanded}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} md={4}>
            <TextField
              fullWidth
              label="Search Addresses"
              value={filters.search}
              onChange={(e) => setFilters(prev => ({ ...prev, search: e.target.value }))}
              placeholder="Name, address, city, company..."
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Search />
                  </InputAdornment>
                ),
              }}
            />
          </Grid>
          
          <Grid item xs={12} md={2}>
            <FormControl fullWidth>
              <InputLabel>Type</InputLabel>
              <Select
                value={filters.type}
                label="Type"
                onChange={(e) => setFilters(prev => ({ ...prev, type: e.target.value }))}
              >
                <MenuItem value="">All Types</MenuItem>
                {addressTypes.map(type => (
                  <MenuItem key={type.value} value={type.value}>
                    <Box display="flex" alignItems="center" gap={1}>
                      {type.icon}
                      {type.label}
                    </Box>
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          
          <Grid item xs={12} md={3}>
            <FormControlLabel
              control={
                <Switch
                  checked={filters.favorites}
                  onChange={(e) => setFilters(prev => ({ ...prev, favorites: e.target.checked }))}
                />
              }
              label="Favorites Only"
            />
          </Grid>
          
          <Grid item xs={12} md={3}>
            <FormControlLabel
              control={
                <Switch
                  checked={filters.verified}
                  onChange={(e) => setFilters(prev => ({ ...prev, verified: e.target.checked }))}
                />
              }
              label="Verified Only"
            />
          </Grid>
          
          <Grid item xs={12}>
            <Button
              variant="outlined"
              startIcon={<Clear />}
              onClick={() => setFilters({ type: '', search: '', favorites: false, verified: false })}
            >
              Clear Filters
            </Button>
          </Grid>
        </Grid>
      </Collapse>
    </Paper>
  );

  const renderAddressCard = (address: Address) => (
    <Card key={address.id} sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <CardContent sx={{ flexGrow: 1 }}>
        <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
          <Box display="flex" alignItems="center" gap={1}>
            {getAddressTypeIcon(address.type)}
            <Typography variant="h6">{address.label}</Typography>
            {address.isDefault && (
              <Chip label="Default" color="primary" size="small" />
            )}
          </Box>
          
          <Box display="flex" alignItems="center" gap={0.5}>
            <IconButton
              size="small"
              onClick={() => handleToggleFavorite(address.id)}
              color={address.isFavorite ? 'warning' : 'default'}
            >
              {address.isFavorite ? <Star /> : <StarBorder />}
            </IconButton>
            
            <IconButton
              size="small"
              onClick={(e) => {
                setSelectedAddress(address);
                setMenuAnchor(e.currentTarget);
              }}
            >
              <MoreVert />
            </IconButton>
          </Box>
        </Box>
        
        <Typography variant="subtitle2" gutterBottom>
          {address.name}
        </Typography>
        
        {address.company && (
          <Typography variant="body2" color="text.secondary" gutterBottom>
            {address.company}
          </Typography>
        )}
        
        <Typography variant="body2" color="text.secondary" gutterBottom>
          {address.address}
          {address.address2 && <><br />{address.address2}</>}
          <br />
          {address.city}, {address.state} {address.zipCode}
          <br />
          {address.country}
        </Typography>
        
        {address.phone && (
          <Typography variant="body2" color="text.secondary">
            📞 {address.phone}
          </Typography>
        )}
        
        <Box display="flex" alignItems="center" gap={1} mt={2}>
          {address.isVerified ? (
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
          
          <Chip
            icon={address.visibility === 'private' ? <Lock /> : address.visibility === 'shared' ? <Share /> : <Public />}
            label={address.visibility}
            size="small"
            variant="outlined"
          />
        </Box>
        
        {address.tags.length > 0 && (
          <Box mt={1}>
            {address.tags.map(tag => (
              <Chip
                key={tag}
                label={tag}
                size="small"
                variant="outlined"
                sx={{ mr: 0.5, mb: 0.5 }}
              />
            ))}
          </Box>
        )}
        
        <Box mt={2}>
          <Typography variant="caption" color="text.secondary">
            Used {address.usageCount} times
            {address.lastUsed && ` • Last used ${new Date(address.lastUsed).toLocaleDateString()}`}
          </Typography>
        </Box>
      </CardContent>
      
      <CardActions>
        <Button
          size="small"
          startIcon={<Edit />}
          onClick={() => {
            setSelectedAddress(address);
            setAddressForm({
              label: address.label,
              type: address.type,
              name: address.name,
              company: address.company || '',
              address: address.address,
              address2: address.address2 || '',
              city: address.city,
              state: address.state,
              zipCode: address.zipCode,
              country: address.country,
              phone: address.phone || '',
              email: address.email || '',
              instructions: address.instructions || '',
              visibility: address.visibility,
              tags: address.tags,
            });
            setEditDialog(true);
          }}
        >
          Edit
        </Button>
        
        {!address.isVerified && (
          <Button
            size="small"
            startIcon={actionLoading === address.id ? <CircularProgress size={16} /> : <GPS />}
            onClick={() => handleVerifyAddress(address)}
            disabled={actionLoading === address.id}
          >
            Verify
          </Button>
        )}
        
        {!address.isDefault && (
          <Button
            size="small"
            onClick={() => handleSetDefault(address.id)}
          >
            Set Default
          </Button>
        )}
      </CardActions>
    </Card>
  );

  const renderAddressForm = () => (
    <Grid container spacing={2}>
      <Grid item xs={12} md={6}>
        <TextField
          fullWidth
          label="Address Label"
          value={addressForm.label}
          onChange={(e) => setAddressForm(prev => ({ ...prev, label: e.target.value }))}
          placeholder="e.g., Home, Office, Mom's House"
          required
        />
      </Grid>
      
      <Grid item xs={12} md={6}>
        <FormControl fullWidth required>
          <InputLabel>Address Type</InputLabel>
          <Select
            value={addressForm.type}
            label="Address Type"
            onChange={(e) => setAddressForm(prev => ({ ...prev, type: e.target.value as any }))}
          >
            {addressTypes.map(type => (
              <MenuItem key={type.value} value={type.value}>
                <Box display="flex" alignItems="center" gap={1}>
                  {type.icon}
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
          label="Full Name"
          value={addressForm.name}
          onChange={(e) => setAddressForm(prev => ({ ...prev, name: e.target.value }))}
          required
        />
      </Grid>
      
      <Grid item xs={12} md={6}>
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
      
      <Grid item xs={12}>
        <TextField
          fullWidth
          label="Apartment, Suite, Unit (Optional)"
          value={addressForm.address2}
          onChange={(e) => setAddressForm(prev => ({ ...prev, address2: e.target.value }))}
        />
      </Grid>
      
      <Grid item xs={12} md={4}>
        <TextField
          fullWidth
          label="City"
          value={addressForm.city}
          onChange={(e) => setAddressForm(prev => ({ ...prev, city: e.target.value }))}
          required
        />
      </Grid>
      
      <Grid item xs={6} md={4}>
        <TextField
          fullWidth
          label="State/Province"
          value={addressForm.state}
          onChange={(e) => setAddressForm(prev => ({ ...prev, state: e.target.value }))}
          required
        />
      </Grid>
      
      <Grid item xs={6} md={4}>
        <TextField
          fullWidth
          label="ZIP/Postal Code"
          value={addressForm.zipCode}
          onChange={(e) => setAddressForm(prev => ({ ...prev, zipCode: e.target.value }))}
          required
        />
      </Grid>
      
      <Grid item xs={12} md={6}>
        <FormControl fullWidth required>
          <InputLabel>Country</InputLabel>
          <Select
            value={addressForm.country}
            label="Country"
            onChange={(e) => setAddressForm(prev => ({ ...prev, country: e.target.value }))}
          >
            {countries.map(country => (
              <MenuItem key={country.value} value={country.value}>
                {country.label}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </Grid>
      
      <Grid item xs={12} md={6}>
        <TextField
          fullWidth
          label="Phone Number (Optional)"
          value={addressForm.phone}
          onChange={(e) => setAddressForm(prev => ({ ...prev, phone: e.target.value }))}
          placeholder="+1 (555) 123-4567"
        />
      </Grid>
      
      <Grid item xs={12}>
        <TextField
          fullWidth
          label="Delivery Instructions (Optional)"
          value={addressForm.instructions}
          onChange={(e) => setAddressForm(prev => ({ ...prev, instructions: e.target.value }))}
          multiline
          rows={2}
          placeholder="Ring doorbell, gate code, special instructions..."
        />
      </Grid>
      
      <Grid item xs={12}>
        <FormControl fullWidth>
          <InputLabel>Visibility</InputLabel>
          <Select
            value={addressForm.visibility}
            label="Visibility"
            onChange={(e) => setAddressForm(prev => ({ ...prev, visibility: e.target.value as any }))}
          >
            {visibilityOptions.map(option => (
              <MenuItem key={option.value} value={option.value}>
                <Box>
                  <Box display="flex" alignItems="center" gap={1}>
                    {option.icon}
                    {option.label}
                  </Box>
                  <Typography variant="caption" color="text.secondary">
                    {option.description}
                  </Typography>
                </Box>
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </Grid>
      
      <Grid item xs={12}>
        <Typography variant="subtitle2" gutterBottom>
          Tags (Optional)
        </Typography>
        <Box display="flex" flexWrap="wrap" gap={0.5} mb={1}>
          {addressForm.tags.map(tag => (
            <Chip
              key={tag}
              label={tag}
              size="small"
              onDelete={() => handleRemoveTag(tag)}
            />
          ))}
        </Box>
        <Box display="flex" gap={1}>
          <TextField
            size="small"
            label="Add Tag"
            value={newTag}
            onChange={(e) => setNewTag(e.target.value)}
            onKeyPress={(e) => {
              if (e.key === 'Enter') {
                e.preventDefault();
                handleAddTag();
              }
            }}
          />
          <Button
            size="small"
            variant="outlined"
            onClick={handleAddTag}
            disabled={!newTag.trim()}
          >
            Add
          </Button>
        </Box>
      </Grid>
    </Grid>
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
            Address Book
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Manage your saved addresses for faster shipping
          </Typography>
        </Box>
        
        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={() => setAddDialog(true)}
          size="large"
        >
          Add Address
        </Button>
      </Box>

      {renderFilters()}

      {filteredAddresses.length === 0 ? (
        <Paper sx={{ p: 4, textAlign: 'center' }}>
          <LocationOn sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
          <Typography variant="h6" gutterBottom>
            No addresses found
          </Typography>
          <Typography variant="body1" color="text.secondary" paragraph>
            {addresses.length === 0 
              ? "You haven't added any addresses yet. Add your first address to get started."
              : "No addresses match your current filters. Try adjusting your search criteria."
            }
          </Typography>
          {addresses.length === 0 && (
            <Button
              variant="contained"
              startIcon={<Add />}
              onClick={() => setAddDialog(true)}
            >
              Add Your First Address
            </Button>
          )}
        </Paper>
      ) : (
        <Grid container spacing={3}>
          {filteredAddresses.map(address => (
            <Grid item xs={12} md={6} lg={4} key={address.id}>
              {renderAddressCard(address)}
            </Grid>
          ))}
        </Grid>
      )}

      {/* Action Menu */}
      <Menu
        anchorEl={menuAnchor}
        open={Boolean(menuAnchor)}
        onClose={() => setMenuAnchor(null)}
      >
        <MenuList>
          <MenuItem onClick={() => {
            if (selectedAddress) {
              setViewDialog(true);
            }
            setMenuAnchor(null);
          }}>
            <ListItemIcon><Visibility /></ListItemIcon>
            <ListItemText>View Details</ListItemText>
          </MenuItem>
          
          <MenuItem onClick={() => {
            if (selectedAddress) {
              handleCopyAddress(selectedAddress);
            }
            setMenuAnchor(null);
          }}>
            <ListItemIcon><Copy /></ListItemIcon>
            <ListItemText>Copy Address</ListItemText>
          </MenuItem>
          
          <MenuItem onClick={() => {
            if (selectedAddress) {
              handleGetDirections(selectedAddress);
            }
            setMenuAnchor(null);
          }}>
            <ListItemIcon><Directions /></ListItemIcon>
            <ListItemText>Get Directions</ListItemText>
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
            <ListItemText>Delete Address</ListItemText>
          </MenuItem>
        </MenuList>
      </Menu>

      {/* Add Address Dialog */}
      <Dialog open={addDialog} onClose={() => setAddDialog(false)} maxWidth="md" fullWidth>
        <DialogTitle>Add New Address</DialogTitle>
        <DialogContent>
          <Box sx={{ mt: 1 }}>
            {renderAddressForm()}
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
            onClick={handleAddAddress}
            variant="contained"
            disabled={actionLoading === 'add' || !addressForm.label || !addressForm.name || !addressForm.address}
            startIcon={actionLoading === 'add' ? <CircularProgress size={20} /> : <Add />}
          >
            Add Address
          </Button>
        </DialogActions>
      </Dialog>

      {/* Edit Address Dialog */}
      <Dialog open={editDialog} onClose={() => setEditDialog(false)} maxWidth="md" fullWidth>
        <DialogTitle>Edit Address</DialogTitle>
        <DialogContent>
          <Box sx={{ mt: 1 }}>
            {renderAddressForm()}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => {
            setEditDialog(false);
            setSelectedAddress(null);
            resetForm();
          }}>
            Cancel
          </Button>
          <Button
            onClick={handleEditAddress}
            variant="contained"
            disabled={actionLoading === 'edit'}
            startIcon={actionLoading === 'edit' ? <CircularProgress size={20} /> : <Check />}
          >
            Save Changes
          </Button>
        </DialogActions>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialog} onClose={() => setDeleteDialog(false)}>
        <DialogTitle>Delete Address</DialogTitle>
        <DialogContent>
          <Typography>
            Are you sure you want to delete "{selectedAddress?.label}"? This action cannot be undone.
          </Typography>
          {selectedAddress?.isDefault && (
            <Alert severity="warning" sx={{ mt: 2 }}>
              This is your default address. You'll need to set a new default address after deletion.
            </Alert>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialog(false)}>Cancel</Button>
          <Button
            onClick={handleDeleteAddress}
            color="error"
            variant="contained"
            disabled={actionLoading === 'delete'}
            startIcon={actionLoading === 'delete' ? <CircularProgress size={20} /> : <Delete />}
          >
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default AddressBook;