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
  CardActions,
  Chip,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  DatePicker,
  LocalizationProvider,
  Tabs,
  Tab,
  Divider,
  Avatar,
  IconButton,
  Tooltip,
  Badge,
} from '@mui/material';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import {
  Search,
  FilterList,
  Download,
  Visibility,
  LocalShipping,
  CheckCircle,
  Error,
  Schedule,
  ExpandMore,
  Print,
  Star,
  StarBorder,
  Receipt,
  Refresh,
  Sort,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useNotification } from '../../contexts/NotificationContext';
import { useAuth } from '../../contexts/AuthContext';

interface DeliveryItem {
  id: string;
  trackingNumber: string;
  status: 'delivered' | 'in_transit' | 'cancelled' | 'returned';
  serviceType: string;
  origin: {
    name: string;
    address: string;
    city: string;
    state: string;
  };
  destination: {
    name: string;
    address: string;
    city: string;
    state: string;
  };
  orderDate: string;
  deliveryDate?: string;
  estimatedDelivery: string;
  cost: number;
  weight: number;
  dimensions: {
    length: number;
    width: number;
    height: number;
  };
  rating?: number;
  isFavorite: boolean;
  hasInvoice: boolean;
  packageType: string;
  courierName?: string;
  deliveryNotes?: string;
}

interface FilterState {
  status: string;
  serviceType: string;
  dateFrom: Date | null;
  dateTo: Date | null;
  searchTerm: string;
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
      id={`history-tabpanel-${index}`}
      aria-labelledby={`history-tab-${index}`}
      {...other}
    >
      {value === index && <Box>{children}</Box>}
    </div>
  );
};

const DeliveryHistory: React.FC = () => {
  const navigate = useNavigate();
  const { showNotification } = useNotification();
  const { user } = useAuth();
  
  const [deliveries, setDeliveries] = useState<DeliveryItem[]>([]);
  const [filteredDeliveries, setFilteredDeliveries] = useState<DeliveryItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [activeTab, setActiveTab] = useState(0);
  const [selectedDelivery, setSelectedDelivery] = useState<DeliveryItem | null>(null);
  const [detailsDialog, setDetailsDialog] = useState(false);
  const [filterDialog, setFilterDialog] = useState(false);
  const [sortBy, setSortBy] = useState<'date' | 'status' | 'cost'>('date');
  const [sortOrder, setSortOrder] = useState<'asc' | 'desc'>('desc');
  
  const [filters, setFilters] = useState<FilterState>({
    status: 'all',
    serviceType: 'all',
    dateFrom: null,
    dateTo: null,
    searchTerm: '',
  });

  useEffect(() => {
    loadDeliveryHistory();
  }, []);

  useEffect(() => {
    applyFiltersAndSort();
  }, [deliveries, filters, sortBy, sortOrder, activeTab]);

  const loadDeliveryHistory = async () => {
    setLoading(true);
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      // Mock delivery history data
      const mockDeliveries: DeliveryItem[] = [
        {
          id: '1',
          trackingNumber: 'EXL123456789',
          status: 'delivered',
          serviceType: 'Express Delivery',
          origin: {
            name: 'Acme Corporation',
            address: '123 Business St',
            city: 'New York',
            state: 'NY',
          },
          destination: {
            name: 'John Doe',
            address: '456 Home Ave',
            city: 'Brooklyn',
            state: 'NY',
          },
          orderDate: '2024-01-10T10:30:00Z',
          deliveryDate: '2024-01-12T14:20:00Z',
          estimatedDelivery: '2024-01-12T16:00:00Z',
          cost: 24.99,
          weight: 2.5,
          dimensions: { length: 12, width: 8, height: 6 },
          rating: 5,
          isFavorite: true,
          hasInvoice: true,
          packageType: 'Document',
          courierName: 'Mike Johnson',
          deliveryNotes: 'Left at front door as requested',
        },
        {
          id: '2',
          trackingNumber: 'EXL987654321',
          status: 'delivered',
          serviceType: 'Standard Delivery',
          origin: {
            name: 'Online Store',
            address: '789 Commerce Blvd',
            city: 'Los Angeles',
            state: 'CA',
          },
          destination: {
            name: 'Jane Smith',
            address: '321 Oak Street',
            city: 'San Francisco',
            state: 'CA',
          },
          orderDate: '2024-01-08T15:45:00Z',
          deliveryDate: '2024-01-11T11:30:00Z',
          estimatedDelivery: '2024-01-11T18:00:00Z',
          cost: 15.50,
          weight: 1.2,
          dimensions: { length: 10, width: 6, height: 4 },
          rating: 4,
          isFavorite: false,
          hasInvoice: true,
          packageType: 'Package',
          courierName: 'Sarah Davis',
        },
        {
          id: '3',
          trackingNumber: 'EXL456789123',
          status: 'in_transit',
          serviceType: 'Overnight Delivery',
          origin: {
            name: 'Tech Solutions Inc',
            address: '555 Innovation Way',
            city: 'Austin',
            state: 'TX',
          },
          destination: {
            name: 'Alex Brown',
            address: '777 Pine Street',
            city: 'Denver',
            state: 'CO',
          },
          orderDate: '2024-01-15T09:15:00Z',
          estimatedDelivery: '2024-01-16T12:00:00Z',
          cost: 45.00,
          weight: 3.8,
          dimensions: { length: 15, width: 10, height: 8 },
          isFavorite: false,
          hasInvoice: false,
          packageType: 'Electronics',
        },
        {
          id: '4',
          trackingNumber: 'EXL789123456',
          status: 'cancelled',
          serviceType: 'Standard Delivery',
          origin: {
            name: 'Local Store',
            address: '111 Main Street',
            city: 'Chicago',
            state: 'IL',
          },
          destination: {
            name: 'Chris Wilson',
            address: '222 Elm Avenue',
            city: 'Milwaukee',
            state: 'WI',
          },
          orderDate: '2024-01-05T13:20:00Z',
          estimatedDelivery: '2024-01-08T17:00:00Z',
          cost: 12.75,
          weight: 0.8,
          dimensions: { length: 8, width: 6, height: 3 },
          isFavorite: false,
          hasInvoice: false,
          packageType: 'Document',
        },
      ];
      
      setDeliveries(mockDeliveries);
    } catch (error) {
      showNotification('Failed to load delivery history', 'error');
    } finally {
      setLoading(false);
    }
  };

  const applyFiltersAndSort = () => {
    let filtered = [...deliveries];
    
    // Apply tab filter
    if (activeTab === 1) {
      filtered = filtered.filter(d => d.status === 'delivered');
    } else if (activeTab === 2) {
      filtered = filtered.filter(d => d.status === 'in_transit');
    } else if (activeTab === 3) {
      filtered = filtered.filter(d => ['cancelled', 'returned'].includes(d.status));
    }
    
    // Apply search filter
    if (filters.searchTerm) {
      filtered = filtered.filter(d =>
        d.trackingNumber.toLowerCase().includes(filters.searchTerm.toLowerCase()) ||
        d.origin.name.toLowerCase().includes(filters.searchTerm.toLowerCase()) ||
        d.destination.name.toLowerCase().includes(filters.searchTerm.toLowerCase())
      );
    }
    
    // Apply status filter
    if (filters.status !== 'all') {
      filtered = filtered.filter(d => d.status === filters.status);
    }
    
    // Apply service type filter
    if (filters.serviceType !== 'all') {
      filtered = filtered.filter(d => d.serviceType === filters.serviceType);
    }
    
    // Apply date filters
    if (filters.dateFrom) {
      filtered = filtered.filter(d => new Date(d.orderDate) >= filters.dateFrom!);
    }
    if (filters.dateTo) {
      filtered = filtered.filter(d => new Date(d.orderDate) <= filters.dateTo!);
    }
    
    // Apply sorting
    filtered.sort((a, b) => {
      let aValue: any, bValue: any;
      
      switch (sortBy) {
        case 'date':
          aValue = new Date(a.orderDate);
          bValue = new Date(b.orderDate);
          break;
        case 'cost':
          aValue = a.cost;
          bValue = b.cost;
          break;
        case 'status':
          aValue = a.status;
          bValue = b.status;
          break;
        default:
          aValue = a.orderDate;
          bValue = b.orderDate;
      }
      
      if (sortOrder === 'asc') {
        return aValue > bValue ? 1 : -1;
      } else {
        return aValue < bValue ? 1 : -1;
      }
    });
    
    setFilteredDeliveries(filtered);
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'delivered':
        return 'success';
      case 'in_transit':
        return 'info';
      case 'cancelled':
        return 'error';
      case 'returned':
        return 'warning';
      default:
        return 'default';
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'delivered':
        return <CheckCircle />;
      case 'in_transit':
        return <LocalShipping />;
      case 'cancelled':
      case 'returned':
        return <Error />;
      default:
        return <Schedule />;
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const handleToggleFavorite = (deliveryId: string) => {
    setDeliveries(prev =>
      prev.map(d =>
        d.id === deliveryId ? { ...d, isFavorite: !d.isFavorite } : d
      )
    );
    showNotification('Updated favorites', 'success');
  };

  const handleViewDetails = (delivery: DeliveryItem) => {
    setSelectedDelivery(delivery);
    setDetailsDialog(true);
  };

  const handleTrackShipment = (trackingNumber: string) => {
    navigate(`/track/${trackingNumber}`);
  };

  const handleDownloadInvoice = (delivery: DeliveryItem) => {
    showNotification(`Downloading invoice for ${delivery.trackingNumber}`, 'info');
  };

  const handleExportHistory = () => {
    showNotification('Exporting delivery history...', 'info');
  };

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setActiveTab(newValue);
    setPage(0);
  };

  const renderDeliveryCard = (delivery: DeliveryItem) => (
    <Card key={delivery.id} sx={{ mb: 2 }}>
      <CardContent>
        <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
          <Box>
            <Typography variant="h6" fontWeight="bold" gutterBottom>
              {delivery.trackingNumber}
            </Typography>
            <Box display="flex" alignItems="center" gap={1} mb={1}>
              <Chip
                icon={getStatusIcon(delivery.status)}
                label={delivery.status.replace('_', ' ').toUpperCase()}
                color={getStatusColor(delivery.status) as any}
                size="small"
              />
              <Typography variant="body2" color="text.secondary">
                {delivery.serviceType}
              </Typography>
            </Box>
          </Box>
          
          <Box display="flex" alignItems="center" gap={1}>
            <Tooltip title={delivery.isFavorite ? 'Remove from favorites' : 'Add to favorites'}>
              <IconButton
                size="small"
                onClick={() => handleToggleFavorite(delivery.id)}
                color={delivery.isFavorite ? 'warning' : 'default'}
              >
                {delivery.isFavorite ? <Star /> : <StarBorder />}
              </IconButton>
            </Tooltip>
            <Typography variant="h6" fontWeight="bold" color="primary">
              ${delivery.cost.toFixed(2)}
            </Typography>
          </Box>
        </Box>
        
        <Grid container spacing={2}>
          <Grid item xs={12} md={6}>
            <Typography variant="body2" color="text.secondary">
              From: {delivery.origin.name}
            </Typography>
            <Typography variant="body2">
              {delivery.origin.city}, {delivery.origin.state}
            </Typography>
          </Grid>
          <Grid item xs={12} md={6}>
            <Typography variant="body2" color="text.secondary">
              To: {delivery.destination.name}
            </Typography>
            <Typography variant="body2">
              {delivery.destination.city}, {delivery.destination.state}
            </Typography>
          </Grid>
          <Grid item xs={12} md={6}>
            <Typography variant="body2" color="text.secondary">
              Order Date: {formatDate(delivery.orderDate)}
            </Typography>
          </Grid>
          <Grid item xs={12} md={6}>
            {delivery.deliveryDate ? (
              <Typography variant="body2" color="text.secondary">
                Delivered: {formatDate(delivery.deliveryDate)}
              </Typography>
            ) : (
              <Typography variant="body2" color="text.secondary">
                Est. Delivery: {formatDate(delivery.estimatedDelivery)}
              </Typography>
            )}
          </Grid>
        </Grid>
      </CardContent>
      
      <CardActions>
        <Button
          size="small"
          startIcon={<Visibility />}
          onClick={() => handleViewDetails(delivery)}
        >
          View Details
        </Button>
        {delivery.status !== 'cancelled' && (
          <Button
            size="small"
            startIcon={<LocalShipping />}
            onClick={() => handleTrackShipment(delivery.trackingNumber)}
          >
            Track
          </Button>
        )}
        {delivery.hasInvoice && (
          <Button
            size="small"
            startIcon={<Receipt />}
            onClick={() => handleDownloadInvoice(delivery)}
          >
            Invoice
          </Button>
        )}
        {delivery.status === 'delivered' && !delivery.rating && (
          <Button
            size="small"
            startIcon={<Star />}
            onClick={() => showNotification('Rating feature coming soon', 'info')}
          >
            Rate
          </Button>
        )}
      </CardActions>
    </Card>
  );

  const renderTableView = () => (
    <TableContainer component={Paper}>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Tracking Number</TableCell>
            <TableCell>Status</TableCell>
            <TableCell>Service Type</TableCell>
            <TableCell>From</TableCell>
            <TableCell>To</TableCell>
            <TableCell>Order Date</TableCell>
            <TableCell>Cost</TableCell>
            <TableCell>Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {filteredDeliveries
            .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
            .map((delivery) => (
              <TableRow key={delivery.id}>
                <TableCell>
                  <Box display="flex" alignItems="center" gap={1}>
                    {delivery.isFavorite && <Star color="warning" fontSize="small" />}
                    {delivery.trackingNumber}
                  </Box>
                </TableCell>
                <TableCell>
                  <Chip
                    icon={getStatusIcon(delivery.status)}
                    label={delivery.status.replace('_', ' ')}
                    color={getStatusColor(delivery.status) as any}
                    size="small"
                  />
                </TableCell>
                <TableCell>{delivery.serviceType}</TableCell>
                <TableCell>{delivery.origin.city}, {delivery.origin.state}</TableCell>
                <TableCell>{delivery.destination.city}, {delivery.destination.state}</TableCell>
                <TableCell>{formatDate(delivery.orderDate)}</TableCell>
                <TableCell>${delivery.cost.toFixed(2)}</TableCell>
                <TableCell>
                  <Box display="flex" gap={1}>
                    <Tooltip title="View Details">
                      <IconButton
                        size="small"
                        onClick={() => handleViewDetails(delivery)}
                      >
                        <Visibility />
                      </IconButton>
                    </Tooltip>
                    {delivery.status !== 'cancelled' && (
                      <Tooltip title="Track Shipment">
                        <IconButton
                          size="small"
                          onClick={() => handleTrackShipment(delivery.trackingNumber)}
                        >
                          <LocalShipping />
                        </IconButton>
                      </Tooltip>
                    )}
                    {delivery.hasInvoice && (
                      <Tooltip title="Download Invoice">
                        <IconButton
                          size="small"
                          onClick={() => handleDownloadInvoice(delivery)}
                        >
                          <Receipt />
                        </IconButton>
                      </Tooltip>
                    )}
                  </Box>
                </TableCell>
              </TableRow>
            ))}
        </TableBody>
      </Table>
      <TablePagination
        rowsPerPageOptions={[10, 25, 50]}
        component="div"
        count={filteredDeliveries.length}
        rowsPerPage={rowsPerPage}
        page={page}
        onPageChange={(event, newPage) => setPage(newPage)}
        onRowsPerPageChange={(event) => {
          setRowsPerPage(parseInt(event.target.value, 10));
          setPage(0);
        }}
      />
    </TableContainer>
  );

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Typography variant="h4" fontWeight="bold" gutterBottom>
          Delivery History
        </Typography>
        
        {/* Search and Filter Bar */}
        <Paper sx={{ p: 3, mb: 3 }}>
          <Grid container spacing={2} alignItems="center">
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="Search deliveries"
                placeholder="Enter tracking number, sender, or recipient"
                value={filters.searchTerm}
                onChange={(e) => setFilters({ ...filters, searchTerm: e.target.value })}
                InputProps={{
                  startAdornment: <Search sx={{ mr: 1, color: 'text.secondary' }} />,
                }}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <Box display="flex" gap={2}>
                <Button
                  variant="outlined"
                  startIcon={<FilterList />}
                  onClick={() => setFilterDialog(true)}
                >
                  Filters
                </Button>
                <Button
                  variant="outlined"
                  startIcon={<Sort />}
                  onClick={() => setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc')}
                >
                  Sort {sortOrder === 'asc' ? '↑' : '↓'}
                </Button>
                <Button
                  variant="outlined"
                  startIcon={<Download />}
                  onClick={handleExportHistory}
                >
                  Export
                </Button>
                <Button
                  variant="outlined"
                  startIcon={<Refresh />}
                  onClick={loadDeliveryHistory}
                  disabled={loading}
                >
                  Refresh
                </Button>
              </Box>
            </Grid>
          </Grid>
        </Paper>

        {/* Status Tabs */}
        <Paper sx={{ mb: 3 }}>
          <Tabs value={activeTab} onChange={handleTabChange}>
            <Tab
              label={
                <Badge badgeContent={deliveries.length} color="primary" max={999}>
                  All Deliveries
                </Badge>
              }
            />
            <Tab
              label={
                <Badge
                  badgeContent={deliveries.filter(d => d.status === 'delivered').length}
                  color="success"
                  max={999}
                >
                  Delivered
                </Badge>
              }
            />
            <Tab
              label={
                <Badge
                  badgeContent={deliveries.filter(d => d.status === 'in_transit').length}
                  color="info"
                  max={999}
                >
                  In Transit
                </Badge>
              }
            />
            <Tab
              label={
                <Badge
                  badgeContent={
                    deliveries.filter(d => ['cancelled', 'returned'].includes(d.status)).length
                  }
                  color="error"
                  max={999}
                >
                  Issues
                </Badge>
              }
            />
          </Tabs>
        </Paper>

        {/* Delivery List */}
        <TabPanel value={activeTab} index={0}>
          {filteredDeliveries.map(renderDeliveryCard)}
        </TabPanel>
        <TabPanel value={activeTab} index={1}>
          {filteredDeliveries.map(renderDeliveryCard)}
        </TabPanel>
        <TabPanel value={activeTab} index={2}>
          {filteredDeliveries.map(renderDeliveryCard)}
        </TabPanel>
        <TabPanel value={activeTab} index={3}>
          {filteredDeliveries.map(renderDeliveryCard)}
        </TabPanel>

        {filteredDeliveries.length === 0 && !loading && (
          <Paper sx={{ p: 4, textAlign: 'center' }}>
            <Typography variant="h6" color="text.secondary">
              No deliveries found
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Try adjusting your search criteria or filters
            </Typography>
          </Paper>
        )}

        {/* Filter Dialog */}
        <Dialog open={filterDialog} onClose={() => setFilterDialog(false)} maxWidth="sm" fullWidth>
          <DialogTitle>Filter Deliveries</DialogTitle>
          <DialogContent>
            <Grid container spacing={3} sx={{ mt: 1 }}>
              <Grid item xs={12} md={6}>
                <FormControl fullWidth>
                  <InputLabel>Status</InputLabel>
                  <Select
                    value={filters.status}
                    label="Status"
                    onChange={(e) => setFilters({ ...filters, status: e.target.value })}
                  >
                    <MenuItem value="all">All Statuses</MenuItem>
                    <MenuItem value="delivered">Delivered</MenuItem>
                    <MenuItem value="in_transit">In Transit</MenuItem>
                    <MenuItem value="cancelled">Cancelled</MenuItem>
                    <MenuItem value="returned">Returned</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} md={6}>
                <FormControl fullWidth>
                  <InputLabel>Service Type</InputLabel>
                  <Select
                    value={filters.serviceType}
                    label="Service Type"
                    onChange={(e) => setFilters({ ...filters, serviceType: e.target.value })}
                  >
                    <MenuItem value="all">All Service Types</MenuItem>
                    <MenuItem value="Standard Delivery">Standard</MenuItem>
                    <MenuItem value="Express Delivery">Express</MenuItem>
                    <MenuItem value="Overnight Delivery">Overnight</MenuItem>
                    <MenuItem value="Same Day Delivery">Same Day</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} md={6}>
                <DatePicker
                  label="From Date"
                  value={filters.dateFrom}
                  onChange={(date) => setFilters({ ...filters, dateFrom: date })}
                  renderInput={(params) => <TextField {...params} fullWidth />}
                />
              </Grid>
              <Grid item xs={12} md={6}>
                <DatePicker
                  label="To Date"
                  value={filters.dateTo}
                  onChange={(date) => setFilters({ ...filters, dateTo: date })}
                  renderInput={(params) => <TextField {...params} fullWidth />}
                />
              </Grid>
            </Grid>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setFilterDialog(false)}>Cancel</Button>
            <Button
              onClick={() => {
                setFilters({
                  status: 'all',
                  serviceType: 'all',
                  dateFrom: null,
                  dateTo: null,
                  searchTerm: '',
                });
              }}
            >
              Clear All
            </Button>
            <Button onClick={() => setFilterDialog(false)} variant="contained">
              Apply Filters
            </Button>
          </DialogActions>
        </Dialog>

        {/* Details Dialog */}
        <Dialog
          open={detailsDialog}
          onClose={() => setDetailsDialog(false)}
          maxWidth="md"
          fullWidth
        >
          <DialogTitle>
            Delivery Details - {selectedDelivery?.trackingNumber}
          </DialogTitle>
          <DialogContent>
            {selectedDelivery && (
              <Grid container spacing={3}>
                <Grid item xs={12}>
                  <Box display="flex" alignItems="center" gap={2} mb={2}>
                    <Chip
                      icon={getStatusIcon(selectedDelivery.status)}
                      label={selectedDelivery.status.replace('_', ' ').toUpperCase()}
                      color={getStatusColor(selectedDelivery.status) as any}
                    />
                    <Typography variant="body1">
                      {selectedDelivery.serviceType}
                    </Typography>
                  </Box>
                </Grid>
                
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle2" fontWeight="bold" gutterBottom>
                    Pickup Information
                  </Typography>
                  <Typography variant="body2">
                    <strong>{selectedDelivery.origin.name}</strong><br />
                    {selectedDelivery.origin.address}<br />
                    {selectedDelivery.origin.city}, {selectedDelivery.origin.state}
                  </Typography>
                </Grid>
                
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle2" fontWeight="bold" gutterBottom>
                    Delivery Information
                  </Typography>
                  <Typography variant="body2">
                    <strong>{selectedDelivery.destination.name}</strong><br />
                    {selectedDelivery.destination.address}<br />
                    {selectedDelivery.destination.city}, {selectedDelivery.destination.state}
                  </Typography>
                </Grid>
                
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle2" fontWeight="bold" gutterBottom>
                    Package Details
                  </Typography>
                  <Typography variant="body2">
                    Type: {selectedDelivery.packageType}<br />
                    Weight: {selectedDelivery.weight} lbs<br />
                    Dimensions: {selectedDelivery.dimensions.length}" × {selectedDelivery.dimensions.width}" × {selectedDelivery.dimensions.height}"
                  </Typography>
                </Grid>
                
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle2" fontWeight="bold" gutterBottom>
                    Timeline
                  </Typography>
                  <Typography variant="body2">
                    Ordered: {formatDate(selectedDelivery.orderDate)}<br />
                    {selectedDelivery.deliveryDate ? (
                      <>Delivered: {formatDate(selectedDelivery.deliveryDate)}</>
                    ) : (
                      <>Est. Delivery: {formatDate(selectedDelivery.estimatedDelivery)}</>
                    )}
                  </Typography>
                </Grid>
                
                {selectedDelivery.courierName && (
                  <Grid item xs={12}>
                    <Typography variant="subtitle2" fontWeight="bold" gutterBottom>
                      Courier Information
                    </Typography>
                    <Typography variant="body2">
                      {selectedDelivery.courierName}
                    </Typography>
                  </Grid>
                )}
                
                {selectedDelivery.deliveryNotes && (
                  <Grid item xs={12}>
                    <Typography variant="subtitle2" fontWeight="bold" gutterBottom>
                      Delivery Notes
                    </Typography>
                    <Typography variant="body2">
                      {selectedDelivery.deliveryNotes}
                    </Typography>
                  </Grid>
                )}
                
                {selectedDelivery.rating && (
                  <Grid item xs={12}>
                    <Typography variant="subtitle2" fontWeight="bold" gutterBottom>
                      Your Rating
                    </Typography>
                    <Box display="flex" alignItems="center" gap={1}>
                      {[...Array(5)].map((_, i) => (
                        <Star
                          key={i}
                          color={i < selectedDelivery.rating! ? 'warning' : 'disabled'}
                        />
                      ))}
                      <Typography variant="body2">
                        ({selectedDelivery.rating}/5)
                      </Typography>
                    </Box>
                  </Grid>
                )}
              </Grid>
            )}
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setDetailsDialog(false)}>Close</Button>
            {selectedDelivery?.hasInvoice && (
              <Button
                startIcon={<Print />}
                onClick={() => handleDownloadInvoice(selectedDelivery)}
              >
                Print Invoice
              </Button>
            )}
            {selectedDelivery?.status !== 'cancelled' && (
              <Button
                variant="contained"
                startIcon={<LocalShipping />}
                onClick={() => {
                  setDetailsDialog(false);
                  handleTrackShipment(selectedDelivery.trackingNumber);
                }}
              >
                Track Shipment
              </Button>
            )}
          </DialogActions>
        </Dialog>
      </Container>
    </LocalizationProvider>
  );
};

export default DeliveryHistory;