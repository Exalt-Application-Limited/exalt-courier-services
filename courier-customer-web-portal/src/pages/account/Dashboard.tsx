import React, { useState, useEffect } from 'react';
import {
  Box,
  Container,
  Grid,
  Paper,
  Typography,
  Card,
  CardContent,
  Button,
  Chip,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Avatar,
  Divider,
  IconButton,
  Menu,
  MenuItem,
  Alert,
} from '@mui/material';
import {
  LocalShipping,
  TrendingUp,
  Receipt,
  Schedule,
  MoreVert,
  Add,
  Visibility,
  GetApp,
  Notifications,
  AccountCircle,
  CreditCard,
  Support,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { useNotification } from '../../contexts/NotificationContext';
import DashboardCard from '../../components/dashboard/DashboardCard';
import RecentShipments from '../../components/dashboard/RecentShipments';
import QuickActions from '../../components/dashboard/QuickActions';
import ShipmentChart from '../../components/dashboard/ShipmentChart';
import NotificationPanel from '../../components/dashboard/NotificationPanel';

interface DashboardStats {
  totalShipments: number;
  pendingShipments: number;
  completedShipments: number;
  totalSpent: number;
  thisMonthSpent: number;
  averageDeliveryTime: number;
}

interface RecentShipment {
  id: string;
  trackingNumber: string;
  destination: string;
  status: string;
  createdAt: string;
  estimatedDelivery: string;
}

interface QuickAction {
  icon: React.ReactNode;
  title: string;
  description: string;
  onClick: () => void;
  color?: 'primary' | 'secondary' | 'success' | 'info';
}

const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { showNotification } = useNotification();
  
  const [stats, setStats] = useState<DashboardStats>({
    totalShipments: 0,
    pendingShipments: 0,
    completedShipments: 0,
    totalSpent: 0,
    thisMonthSpent: 0,
    averageDeliveryTime: 0,
  });
  
  const [recentShipments, setRecentShipments] = useState<RecentShipment[]>([]);
  const [loading, setLoading] = useState(true);
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      
      // Simulate API calls
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Mock data - replace with actual API calls
      setStats({
        totalShipments: 156,
        pendingShipments: 8,
        completedShipments: 148,
        totalSpent: 4250.75,
        thisMonthSpent: 680.50,
        averageDeliveryTime: 2.3,
      });
      
      setRecentShipments([
        {
          id: '1',
          trackingNumber: 'EXL123456789',
          destination: 'New York, NY',
          status: 'IN_TRANSIT',
          createdAt: '2024-01-15T10:30:00Z',
          estimatedDelivery: '2024-01-16T15:00:00Z',
        },
        {
          id: '2',
          trackingNumber: 'EXL987654321',
          destination: 'Los Angeles, CA',
          status: 'DELIVERED',
          createdAt: '2024-01-14T14:20:00Z',
          estimatedDelivery: '2024-01-15T12:00:00Z',
        },
        {
          id: '3',
          trackingNumber: 'EXL456789123',
          destination: 'Chicago, IL',
          status: 'PENDING_PICKUP',
          createdAt: '2024-01-13T09:15:00Z',
          estimatedDelivery: '2024-01-17T16:30:00Z',
        },
      ]);
    } catch (error) {
      showNotification('Failed to load dashboard data', 'error');
    } finally {
      setLoading(false);
    }
  };

  const quickActions: QuickAction[] = [
    {
      icon: <Add />,
      title: 'Book Shipment',
      description: 'Create a new shipping order',
      onClick: () => navigate('/book'),
      color: 'primary',
    },
    {
      icon: <LocalShipping />,
      title: 'Track Package',
      description: 'Track your shipments',
      onClick: () => navigate('/track'),
      color: 'info',
    },
    {
      icon: <Receipt />,
      title: 'View Invoices',
      description: 'Manage billing and payments',
      onClick: () => navigate('/account/invoices'),
      color: 'secondary',
    },
    {
      icon: <Support />,
      title: 'Get Support',
      description: 'Contact customer service',
      onClick: () => navigate('/support'),
      color: 'success',
    },
  ];

  const handleMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'DELIVERED':
        return 'success';
      case 'IN_TRANSIT':
        return 'info';
      case 'PENDING_PICKUP':
        return 'warning';
      case 'CANCELLED':
        return 'error';
      default:
        return 'default';
    }
  };

  const formatStatus = (status: string) => {
    return status.replace('_', ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase());
  };

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Typography>Loading dashboard...</Typography>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      {/* Welcome Header */}
      <Box mb={4}>
        <Typography variant="h4" fontWeight="bold" gutterBottom>
          Welcome back, {user?.firstName}!
        </Typography>
        <Typography variant="subtitle1" color="text.secondary">
          Here's what's happening with your shipments
        </Typography>
      </Box>

      {/* Stats Cards */}
      <Grid container spacing={3} mb={4}>
        <Grid item xs={12} sm={6} md={3}>
          <DashboardCard
            title="Total Shipments"
            value={stats.totalShipments.toString()}
            icon={<LocalShipping sx={{ fontSize: 40, color: 'primary.main' }} />}
            trend="+12% from last month"
            trendDirection="up"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <DashboardCard
            title="Pending Shipments"
            value={stats.pendingShipments.toString()}
            icon={<Schedule sx={{ fontSize: 40, color: 'warning.main' }} />}
            trend="2 need attention"
            trendDirection="neutral"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <DashboardCard
            title="This Month Spent"
            value={`$${stats.thisMonthSpent.toFixed(2)}`}
            icon={<CreditCard sx={{ fontSize: 40, color: 'success.main' }} />}
            trend="-8% from last month"
            trendDirection="down"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <DashboardCard
            title="Avg. Delivery Time"
            value={`${stats.averageDeliveryTime} days`}
            icon={<TrendingUp sx={{ fontSize: 40, color: 'info.main' }} />}
            trend="0.3 days faster"
            trendDirection="up"
          />
        </Grid>
      </Grid>

      <Grid container spacing={3}>
        {/* Quick Actions */}
        <Grid item xs={12} md={4}>
          <QuickActions actions={quickActions} />
        </Grid>

        {/* Recent Shipments */}
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 3, height: 'fit-content' }}>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
              <Typography variant="h6" fontWeight="bold">
                Recent Shipments
              </Typography>
              <IconButton onClick={handleMenuOpen}>
                <MoreVert />
              </IconButton>
              <Menu
                anchorEl={anchorEl}
                open={Boolean(anchorEl)}
                onClose={handleMenuClose}
              >
                <MenuItem onClick={() => navigate('/shipments')}>
                  <Visibility sx={{ mr: 1 }} /> View All
                </MenuItem>
                <MenuItem onClick={() => navigate('/shipments/export')}>
                  <GetApp sx={{ mr: 1 }} /> Export
                </MenuItem>
              </Menu>
            </Box>

            <List>
              {recentShipments.map((shipment, index) => (
                <React.Fragment key={shipment.id}>
                  <ListItem
                    sx={{
                      cursor: 'pointer',
                      '&:hover': { bgcolor: 'action.hover' },
                      borderRadius: 1,
                    }}
                    onClick={() => navigate(`/shipments/${shipment.id}`)}
                  >
                    <ListItemIcon>
                      <LocalShipping color="primary" />
                    </ListItemIcon>
                    <ListItemText
                      primary={
                        <Box display="flex" alignItems="center" gap={1}>
                          <Typography variant="subtitle2" fontWeight="bold">
                            {shipment.trackingNumber}
                          </Typography>
                          <Chip
                            label={formatStatus(shipment.status)}
                            size="small"
                            color={getStatusColor(shipment.status) as any}
                            variant="outlined"
                          />
                        </Box>
                      }
                      secondary={
                        <Box>
                          <Typography variant="body2" color="text.secondary">
                            To: {shipment.destination}
                          </Typography>
                          <Typography variant="caption" color="text.secondary">
                            Est. delivery: {new Date(shipment.estimatedDelivery).toLocaleDateString()}
                          </Typography>
                        </Box>
                      }
                    />
                  </ListItem>
                  {index < recentShipments.length - 1 && <Divider />}
                </React.Fragment>
              ))}
            </List>

            <Box mt={2} textAlign="center">
              <Button
                variant="outlined"
                onClick={() => navigate('/shipments')}
              >
                View All Shipments
              </Button>
            </Box>
          </Paper>
        </Grid>

        {/* Notifications Panel */}
        <Grid item xs={12} md={4}>
          <NotificationPanel />
        </Grid>

        {/* Shipment Chart */}
        <Grid item xs={12} md={8}>
          <ShipmentChart />
        </Grid>
      </Grid>

      {/* Account Status Alert */}
      {user && !user.isEmailVerified && (
        <Alert
          severity="warning"
          action={
            <Button color="inherit" size="small">
              Verify Now
            </Button>
          }
          sx={{ mt: 3 }}
        >
          Please verify your email address to unlock all features.
        </Alert>
      )}
    </Container>
  );
};

export default Dashboard;