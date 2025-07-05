import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
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
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  IconButton,
  Tooltip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  List,
  ListItem,
  ListItemText,
  Divider,
  Collapse,
  CircularProgress,
  Skeleton,
  InputAdornment,
  Stack,
  Badge,
} from '@mui/material';
import {
  Receipt,
  Download,
  Visibility,
  Email,
  FilterList,
  Search,
  Clear,
  ExpandMore,
  ExpandLess,
  AttachMoney,
  CalendarToday,
  CheckCircle,
  Warning,
  Error as ErrorIcon,
  Pending,
  CreditCard,
  Print,
  Share,
  Archive,
  Refresh,
  DateRange,
  TrendingUp,
  Assessment,
} from '@mui/icons-material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { useAuth } from '../../contexts/AuthContext';
import { useNotification } from '../../contexts/NotificationContext';

interface Invoice {
  id: string;
  invoiceNumber: string;
  date: string;
  dueDate: string;
  amount: number;
  status: 'paid' | 'pending' | 'overdue' | 'cancelled' | 'refunded';
  currency: string;
  description: string;
  services: InvoiceLineItem[];
  paymentMethod?: string;
  paidDate?: string;
  downloadUrl?: string;
  taxAmount: number;
  subtotal: number;
  discountAmount?: number;
  billingAddress: {
    name: string;
    address: string;
    city: string;
    state: string;
    zipCode: string;
  };
}

interface InvoiceLineItem {
  id: string;
  description: string;
  quantity: number;
  unitPrice: number;
  total: number;
  shipmentId?: string;
  serviceType: string;
}

interface InvoiceFilter {
  status?: string;
  dateFrom?: Date | null;
  dateTo?: Date | null;
  amountMin?: number;
  amountMax?: number;
  search?: string;
}

interface InvoiceSummary {
  totalInvoices: number;
  totalAmount: number;
  paidAmount: number;
  pendingAmount: number;
  overdueAmount: number;
  averageAmount: number;
}

const InvoiceHistory: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const { user } = useAuth();
  const { showNotification } = useNotification();
  
  // Data states
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [summary, setSummary] = useState<InvoiceSummary>({
    totalInvoices: 0,
    totalAmount: 0,
    paidAmount: 0,
    pendingAmount: 0,
    overdueAmount: 0,
    averageAmount: 0,
  });
  
  // UI states
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [expandedInvoice, setExpandedInvoice] = useState<string | null>(null);
  const [selectedInvoice, setSelectedInvoice] = useState<Invoice | null>(null);
  const [detailDialog, setDetailDialog] = useState(false);
  const [filterExpanded, setFilterExpanded] = useState(false);
  
  // Filter states
  const [filters, setFilters] = useState<InvoiceFilter>({
    status: searchParams.get('status') || '',
    dateFrom: searchParams.get('dateFrom') ? new Date(searchParams.get('dateFrom')!) : null,
    dateTo: searchParams.get('dateTo') ? new Date(searchParams.get('dateTo')!) : null,
    amountMin: searchParams.get('amountMin') ? parseFloat(searchParams.get('amountMin')!) : undefined,
    amountMax: searchParams.get('amountMax') ? parseFloat(searchParams.get('amountMax')!) : undefined,
    search: searchParams.get('search') || '',
  });

  useEffect(() => {
    loadInvoiceData();
  }, [page, rowsPerPage, filters]);

  useEffect(() => {
    // Update URL params when filters change
    const params = new URLSearchParams();
    if (filters.status) params.set('status', filters.status);
    if (filters.dateFrom) params.set('dateFrom', filters.dateFrom.toISOString());
    if (filters.dateTo) params.set('dateTo', filters.dateTo.toISOString());
    if (filters.amountMin) params.set('amountMin', filters.amountMin.toString());
    if (filters.amountMax) params.set('amountMax', filters.amountMax.toString());
    if (filters.search) params.set('search', filters.search);
    
    setSearchParams(params);
  }, [filters, setSearchParams]);

  const loadInvoiceData = async () => {
    setLoading(true);
    try {
      // Simulate API call - replace with actual service
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Mock data - replace with actual API response
      const mockInvoices: Invoice[] = [
        {
          id: '1',
          invoiceNumber: 'INV-2024-001',
          date: '2024-06-15',
          dueDate: '2024-07-15',
          amount: 89.50,
          status: 'paid',
          currency: 'USD',
          description: 'Express Delivery Services',
          paidDate: '2024-06-20',
          paymentMethod: 'Visa **** 4242',
          taxAmount: 7.16,
          subtotal: 82.34,
          services: [
            {
              id: '1',
              description: 'Next Day Delivery',
              quantity: 2,
              unitPrice: 35.00,
              total: 70.00,
              shipmentId: 'SH-001',
              serviceType: 'express',
            },
            {
              id: '2',
              description: 'Insurance Coverage',
              quantity: 1,
              unitPrice: 12.34,
              total: 12.34,
              serviceType: 'insurance',
            },
          ],
          billingAddress: {
            name: 'John Doe',
            address: '123 Main St',
            city: 'New York',
            state: 'NY',
            zipCode: '10001',
          },
        },
        {
          id: '2',
          invoiceNumber: 'INV-2024-002',
          date: '2024-06-20',
          dueDate: '2024-07-20',
          amount: 45.75,
          status: 'pending',
          currency: 'USD',
          description: 'Standard Delivery Services',
          taxAmount: 3.66,
          subtotal: 42.09,
          services: [
            {
              id: '3',
              description: 'Standard Delivery',
              quantity: 1,
              unitPrice: 42.09,
              total: 42.09,
              shipmentId: 'SH-002',
              serviceType: 'standard',
            },
          ],
          billingAddress: {
            name: 'John Doe',
            address: '123 Main St',
            city: 'New York',
            state: 'NY',
            zipCode: '10001',
          },
        },
        {
          id: '3',
          invoiceNumber: 'INV-2024-003',
          date: '2024-05-10',
          dueDate: '2024-06-10',
          amount: 156.80,
          status: 'overdue',
          currency: 'USD',
          description: 'Bulk Shipping Services',
          taxAmount: 12.54,
          subtotal: 144.26,
          discountAmount: 15.00,
          services: [
            {
              id: '4',
              description: 'Bulk Standard Delivery',
              quantity: 5,
              unitPrice: 25.00,
              total: 125.00,
              serviceType: 'standard',
            },
            {
              id: '5',
              description: 'Express Upgrade',
              quantity: 1,
              unitPrice: 34.26,
              total: 34.26,
              serviceType: 'express',
            },
          ],
          billingAddress: {
            name: 'John Doe',
            address: '123 Main St',
            city: 'New York',
            state: 'NY',
            zipCode: '10001',
          },
        },
      ];
      
      // Apply filters
      const filteredInvoices = mockInvoices.filter(invoice => {
        if (filters.status && invoice.status !== filters.status) return false;
        if (filters.search && !invoice.invoiceNumber.toLowerCase().includes(filters.search.toLowerCase()) && 
            !invoice.description.toLowerCase().includes(filters.search.toLowerCase())) return false;
        if (filters.dateFrom && new Date(invoice.date) < filters.dateFrom) return false;
        if (filters.dateTo && new Date(invoice.date) > filters.dateTo) return false;
        if (filters.amountMin && invoice.amount < filters.amountMin) return false;
        if (filters.amountMax && invoice.amount > filters.amountMax) return false;
        return true;
      });
      
      setInvoices(filteredInvoices);
      
      // Calculate summary
      const totalAmount = filteredInvoices.reduce((sum, inv) => sum + inv.amount, 0);
      const paidAmount = filteredInvoices.filter(inv => inv.status === 'paid').reduce((sum, inv) => sum + inv.amount, 0);
      const pendingAmount = filteredInvoices.filter(inv => inv.status === 'pending').reduce((sum, inv) => sum + inv.amount, 0);
      const overdueAmount = filteredInvoices.filter(inv => inv.status === 'overdue').reduce((sum, inv) => sum + inv.amount, 0);
      
      setSummary({
        totalInvoices: filteredInvoices.length,
        totalAmount,
        paidAmount,
        pendingAmount,
        overdueAmount,
        averageAmount: filteredInvoices.length > 0 ? totalAmount / filteredInvoices.length : 0,
      });
      
    } catch (error) {
      showNotification('Failed to load invoice data', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleDownloadInvoice = async (invoice: Invoice) => {
    setActionLoading(invoice.id);
    try {
      // Simulate download
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      // Create a mock PDF download
      const blob = new Blob(['Mock PDF content'], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `${invoice.invoiceNumber}.pdf`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      
      showNotification('Invoice downloaded successfully', 'success');
    } catch (error) {
      showNotification('Failed to download invoice', 'error');
    } finally {
      setActionLoading(null);
    }
  };

  const handleEmailInvoice = async (invoice: Invoice) => {
    setActionLoading(invoice.id);
    try {
      await new Promise(resolve => setTimeout(resolve, 1000));
      showNotification('Invoice emailed successfully', 'success');
    } catch (error) {
      showNotification('Failed to email invoice', 'error');
    } finally {
      setActionLoading(null);
    }
  };

  const handlePayInvoice = (invoice: Invoice) => {
    navigate(`/billing/pay/${invoice.id}`, { 
      state: { invoice } 
    });
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'paid':
        return <CheckCircle color="success" />;
      case 'pending':
        return <Pending color="warning" />;
      case 'overdue':
        return <ErrorIcon color="error" />;
      case 'cancelled':
        return <Clear color="disabled" />;
      case 'refunded':
        return <Refresh color="info" />;
      default:
        return <Receipt />;
    }
  };

  const getStatusColor = (status: string): "success" | "warning" | "error" | "default" | "info" => {
    switch (status) {
      case 'paid':
        return 'success';
      case 'pending':
        return 'warning';
      case 'overdue':
        return 'error';
      case 'refunded':
        return 'info';
      default:
        return 'default';
    }
  };

  const clearFilters = () => {
    setFilters({
      status: '',
      dateFrom: null,
      dateTo: null,
      amountMin: undefined,
      amountMax: undefined,
      search: '',
    });
  };

  const renderSummaryCards = () => (
    <Grid container spacing={2} sx={{ mb: 3 }}>
      <Grid item xs={12} sm={6} md={2}>
        <Card>
          <CardContent sx={{ textAlign: 'center', py: 2 }}>
            <Typography variant="h6" color="primary" fontWeight="bold">
              {summary.totalInvoices}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Total Invoices
            </Typography>
          </CardContent>
        </Card>
      </Grid>
      
      <Grid item xs={12} sm={6} md={2}>
        <Card>
          <CardContent sx={{ textAlign: 'center', py: 2 }}>
            <Typography variant="h6" color="success.main" fontWeight="bold">
              ${summary.totalAmount.toFixed(2)}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Total Amount
            </Typography>
          </CardContent>
        </Card>
      </Grid>
      
      <Grid item xs={12} sm={6} md={2}>
        <Card>
          <CardContent sx={{ textAlign: 'center', py: 2 }}>
            <Typography variant="h6" color="success.main" fontWeight="bold">
              ${summary.paidAmount.toFixed(2)}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Paid
            </Typography>
          </CardContent>
        </Card>
      </Grid>
      
      <Grid item xs={12} sm={6} md={2}>
        <Card>
          <CardContent sx={{ textAlign: 'center', py: 2 }}>
            <Typography variant="h6" color="warning.main" fontWeight="bold">
              ${summary.pendingAmount.toFixed(2)}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Pending
            </Typography>
          </CardContent>
        </Card>
      </Grid>
      
      <Grid item xs={12} sm={6} md={2}>
        <Card>
          <CardContent sx={{ textAlign: 'center', py: 2 }}>
            <Typography variant="h6" color="error.main" fontWeight="bold">
              ${summary.overdueAmount.toFixed(2)}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Overdue
            </Typography>
          </CardContent>
        </Card>
      </Grid>
      
      <Grid item xs={12} sm={6} md={2}>
        <Card>
          <CardContent sx={{ textAlign: 'center', py: 2 }}>
            <Typography variant="h6" color="info.main" fontWeight="bold">
              ${summary.averageAmount.toFixed(2)}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Average
            </Typography>
          </CardContent>
        </Card>
      </Grid>
    </Grid>
  );

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
          <Grid item xs={12} md={3}>
            <TextField
              fullWidth
              label="Search"
              value={filters.search}
              onChange={(e) => setFilters(prev => ({ ...prev, search: e.target.value }))}
              placeholder="Invoice number or description"
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
              <InputLabel>Status</InputLabel>
              <Select
                value={filters.status}
                label="Status"
                onChange={(e) => setFilters(prev => ({ ...prev, status: e.target.value }))}
              >
                <MenuItem value="">All</MenuItem>
                <MenuItem value="paid">Paid</MenuItem>
                <MenuItem value="pending">Pending</MenuItem>
                <MenuItem value="overdue">Overdue</MenuItem>
                <MenuItem value="cancelled">Cancelled</MenuItem>
                <MenuItem value="refunded">Refunded</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          
          <Grid item xs={12} md={2}>
            <LocalizationProvider dateAdapter={AdapterDateFns}>
              <DatePicker
                label="From Date"
                value={filters.dateFrom}
                onChange={(date) => setFilters(prev => ({ ...prev, dateFrom: date }))}
                slotProps={{ textField: { fullWidth: true } }}
              />
            </LocalizationProvider>
          </Grid>
          
          <Grid item xs={12} md={2}>
            <LocalizationProvider dateAdapter={AdapterDateFns}>
              <DatePicker
                label="To Date"
                value={filters.dateTo}
                onChange={(date) => setFilters(prev => ({ ...prev, dateTo: date }))}
                slotProps={{ textField: { fullWidth: true } }}
              />
            </LocalizationProvider>
          </Grid>
          
          <Grid item xs={12} md={1.5}>
            <TextField
              fullWidth
              label="Min Amount"
              type="number"
              value={filters.amountMin || ''}
              onChange={(e) => setFilters(prev => ({ 
                ...prev, 
                amountMin: e.target.value ? parseFloat(e.target.value) : undefined 
              }))}
              InputProps={{
                startAdornment: <InputAdornment position="start">$</InputAdornment>,
              }}
            />
          </Grid>
          
          <Grid item xs={12} md={1.5}>
            <TextField
              fullWidth
              label="Max Amount"
              type="number"
              value={filters.amountMax || ''}
              onChange={(e) => setFilters(prev => ({ 
                ...prev, 
                amountMax: e.target.value ? parseFloat(e.target.value) : undefined 
              }))}
              InputProps={{
                startAdornment: <InputAdornment position="start">$</InputAdornment>,
              }}
            />
          </Grid>
          
          <Grid item xs={12} md={12}>
            <Stack direction="row" spacing={1}>
              <Button
                variant="outlined"
                startIcon={<Clear />}
                onClick={clearFilters}
              >
                Clear Filters
              </Button>
              <Button
                variant="contained"
                startIcon={<Refresh />}
                onClick={loadInvoiceData}
              >
                Refresh
              </Button>
            </Stack>
          </Grid>
        </Grid>
      </Collapse>
    </Paper>
  );

  const renderInvoiceTable = () => {
    if (loading) {
      return (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                {['Invoice', 'Date', 'Amount', 'Status', 'Actions'].map((header) => (
                  <TableCell key={header}>
                    <Skeleton width="100%" />
                  </TableCell>
                ))}
              </TableRow>
            </TableHead>
            <TableBody>
              {[1, 2, 3, 4, 5].map((row) => (
                <TableRow key={row}>
                  {[1, 2, 3, 4, 5].map((cell) => (
                    <TableCell key={cell}>
                      <Skeleton width="100%" />
                    </TableCell>
                  ))}
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      );
    }

    return (
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Invoice</TableCell>
              <TableCell>Date</TableCell>
              <TableCell>Due Date</TableCell>
              <TableCell align="right">Amount</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Description</TableCell>
              <TableCell align="center">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {invoices
              .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
              .map((invoice) => (
                <React.Fragment key={invoice.id}>
                  <TableRow hover>
                    <TableCell>
                      <Typography variant="subtitle2" fontWeight="bold">
                        {invoice.invoiceNumber}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      {new Date(invoice.date).toLocaleDateString()}
                    </TableCell>
                    <TableCell>
                      <Typography 
                        color={
                          invoice.status === 'overdue' 
                            ? 'error' 
                            : new Date(invoice.dueDate) < new Date() && invoice.status === 'pending'
                              ? 'warning.main'
                              : 'text.primary'
                        }
                      >
                        {new Date(invoice.dueDate).toLocaleDateString()}
                      </Typography>
                    </TableCell>
                    <TableCell align="right">
                      <Typography variant="subtitle2" fontWeight="bold">
                        ${invoice.amount.toFixed(2)}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {invoice.currency}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Chip
                        icon={getStatusIcon(invoice.status)}
                        label={invoice.status.toUpperCase()}
                        color={getStatusColor(invoice.status)}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2">
                        {invoice.description}
                      </Typography>
                      {invoice.paymentMethod && (
                        <Typography variant="caption" color="text.secondary">
                          Paid via {invoice.paymentMethod}
                        </Typography>
                      )}
                    </TableCell>
                    <TableCell align="center">
                      <Stack direction="row" spacing={1} justifyContent="center">
                        <Tooltip title="View Details">
                          <IconButton
                            size="small"
                            onClick={() => {
                              setSelectedInvoice(invoice);
                              setDetailDialog(true);
                            }}
                          >
                            <Visibility />
                          </IconButton>
                        </Tooltip>
                        
                        <Tooltip title="Download PDF">
                          <IconButton
                            size="small"
                            onClick={() => handleDownloadInvoice(invoice)}
                            disabled={actionLoading === invoice.id}
                          >
                            {actionLoading === invoice.id ? (
                              <CircularProgress size={16} />
                            ) : (
                              <Download />
                            )}
                          </IconButton>
                        </Tooltip>
                        
                        <Tooltip title="Email Invoice">
                          <IconButton
                            size="small"
                            onClick={() => handleEmailInvoice(invoice)}
                          >
                            <Email />
                          </IconButton>
                        </Tooltip>
                        
                        {(invoice.status === 'pending' || invoice.status === 'overdue') && (
                          <Tooltip title="Pay Now">
                            <Button
                              size="small"
                              variant="contained"
                              startIcon={<CreditCard />}
                              onClick={() => handlePayInvoice(invoice)}
                            >
                              Pay
                            </Button>
                          </Tooltip>
                        )}
                      </Stack>
                    </TableCell>
                  </TableRow>
                </React.Fragment>
              ))}
          </TableBody>
        </Table>
        
        <TablePagination
          rowsPerPageOptions={[5, 10, 25, 50]}
          component="div"
          count={invoices.length}
          rowsPerPage={rowsPerPage}
          page={page}
          onPageChange={(_, newPage) => setPage(newPage)}
          onRowsPerPageChange={(event) => {
            setRowsPerPage(parseInt(event.target.value, 10));
            setPage(0);
          }}
        />
      </TableContainer>
    );
  };

  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Box>
          <Typography variant="h4" fontWeight="bold" gutterBottom>
            Invoice History
          </Typography>
          <Typography variant="body1" color="text.secondary">
            View and manage your billing invoices
          </Typography>
        </Box>
        
        <Stack direction="row" spacing={2}>
          <Button
            variant="outlined"
            startIcon={<Assessment />}
            onClick={() => navigate('/billing/analytics')}
          >
            View Analytics
          </Button>
          <Button
            variant="contained"
            startIcon={<Download />}
            onClick={() => {
              // Export all invoices
              showNotification('Exporting invoices...', 'info');
            }}
          >
            Export All
          </Button>
        </Stack>
      </Box>

      {renderSummaryCards()}
      {renderFilters()}
      {renderInvoiceTable()}

      {/* Invoice Detail Dialog */}
      <Dialog open={detailDialog} onClose={() => setDetailDialog(false)} maxWidth="md" fullWidth>
        <DialogTitle>
          <Box display="flex" justifyContent="space-between" alignItems="center">
            <Typography variant="h6">
              Invoice Details - {selectedInvoice?.invoiceNumber}
            </Typography>
            <Chip
              icon={getStatusIcon(selectedInvoice?.status || '')}
              label={selectedInvoice?.status?.toUpperCase()}
              color={getStatusColor(selectedInvoice?.status || '')}
            />
          </Box>
        </DialogTitle>
        
        <DialogContent>
          {selectedInvoice && (
            <Grid container spacing={3}>
              <Grid item xs={12} md={6}>
                <Typography variant="subtitle2" gutterBottom>Invoice Information</Typography>
                <List dense>
                  <ListItem>
                    <ListItemText
                      primary="Invoice Number"
                      secondary={selectedInvoice.invoiceNumber}
                    />
                  </ListItem>
                  <ListItem>
                    <ListItemText
                      primary="Date"
                      secondary={new Date(selectedInvoice.date).toLocaleDateString()}
                    />
                  </ListItem>
                  <ListItem>
                    <ListItemText
                      primary="Due Date"
                      secondary={new Date(selectedInvoice.dueDate).toLocaleDateString()}
                    />
                  </ListItem>
                  {selectedInvoice.paidDate && (
                    <ListItem>
                      <ListItemText
                        primary="Paid Date"
                        secondary={new Date(selectedInvoice.paidDate).toLocaleDateString()}
                      />
                    </ListItem>
                  )}
                </List>
              </Grid>
              
              <Grid item xs={12} md={6}>
                <Typography variant="subtitle2" gutterBottom>Billing Address</Typography>
                <Typography variant="body2">
                  {selectedInvoice.billingAddress.name}<br />
                  {selectedInvoice.billingAddress.address}<br />
                  {selectedInvoice.billingAddress.city}, {selectedInvoice.billingAddress.state} {selectedInvoice.billingAddress.zipCode}
                </Typography>
              </Grid>
              
              <Grid item xs={12}>
                <Divider />
                <Typography variant="subtitle2" sx={{ mt: 2, mb: 1 }}>Line Items</Typography>
                <TableContainer>
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell>Description</TableCell>
                        <TableCell align="center">Qty</TableCell>
                        <TableCell align="right">Unit Price</TableCell>
                        <TableCell align="right">Total</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {selectedInvoice.services.map((service) => (
                        <TableRow key={service.id}>
                          <TableCell>{service.description}</TableCell>
                          <TableCell align="center">{service.quantity}</TableCell>
                          <TableCell align="right">${service.unitPrice.toFixed(2)}</TableCell>
                          <TableCell align="right">${service.total.toFixed(2)}</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              </Grid>
              
              <Grid item xs={12}>
                <Box sx={{ mt: 2, textAlign: 'right' }}>
                  <Typography variant="body2">
                    Subtotal: ${selectedInvoice.subtotal.toFixed(2)}
                  </Typography>
                  {selectedInvoice.discountAmount && (
                    <Typography variant="body2" color="success.main">
                      Discount: -${selectedInvoice.discountAmount.toFixed(2)}
                    </Typography>
                  )}
                  <Typography variant="body2">
                    Tax: ${selectedInvoice.taxAmount.toFixed(2)}
                  </Typography>
                  <Typography variant="h6" fontWeight="bold">
                    Total: ${selectedInvoice.amount.toFixed(2)}
                  </Typography>
                </Box>
              </Grid>
            </Grid>
          )}
        </DialogContent>
        
        <DialogActions>
          <Button onClick={() => setDetailDialog(false)}>Close</Button>
          {selectedInvoice && (
            <>
              <Button
                startIcon={<Download />}
                onClick={() => handleDownloadInvoice(selectedInvoice)}
              >
                Download
              </Button>
              <Button
                startIcon={<Email />}
                onClick={() => handleEmailInvoice(selectedInvoice)}
              >
                Email
              </Button>
              {(selectedInvoice.status === 'pending' || selectedInvoice.status === 'overdue') && (
                <Button
                  variant="contained"
                  startIcon={<CreditCard />}
                  onClick={() => {
                    setDetailDialog(false);
                    handlePayInvoice(selectedInvoice);
                  }}
                >
                  Pay Now
                </Button>
              )}
            </>
          )}
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default InvoiceHistory;