import React, { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import {
  Box,
  AppBar,
  Toolbar,
  Typography,
  Button,
  IconButton,
  Avatar,
  Menu,
  MenuItem,
  Drawer,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  ListItemButton,
  Divider,
  Badge,
  useTheme,
  useMediaQuery,
  Container,
  Breadcrumbs,
  Link,
  Chip,
} from '@mui/material';
import {
  Menu as MenuIcon,
  Dashboard,
  LocalShipping,
  History,
  AccountCircle,
  Support,
  Notifications,
  Settings,
  ExitToApp,
  Home,
  Business,
  Receipt,
  CreditCard,
  LocationOn,
  ContactSupport,
  Phone,
  Email,
  Star,
  Add,
  Search,
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import { useNotification } from '../contexts/NotificationContext';

interface NavigationItem {
  path: string;
  label: string;
  icon: React.ReactNode;
  badge?: number;
  divider?: boolean;
  requiresAuth?: boolean;
  requiresCorporate?: boolean;
}

const MainLayout: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  
  const { user, logout, isAuthenticated } = useAuth();
  const { showNotification } = useNotification();
  
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [userMenuAnchor, setUserMenuAnchor] = useState<null | HTMLElement>(null);
  const [notificationMenuAnchor, setNotificationMenuAnchor] = useState<null | HTMLElement>(null);

  const navigationItems: NavigationItem[] = [
    { path: '/', label: 'Home', icon: <Home /> },
    { path: '/quote', label: 'Get Quote', icon: <Search /> },
    { path: '/book', label: 'Book Shipment', icon: <Add /> },
    { path: '/track', label: 'Track Package', icon: <LocalShipping /> },
    { divider: true, path: '', label: '', icon: null },
    { 
      path: '/dashboard', 
      label: 'Dashboard', 
      icon: <Dashboard />, 
      requiresAuth: true 
    },
    { 
      path: '/shipments', 
      label: 'My Shipments', 
      icon: <History />, 
      requiresAuth: true 
    },
    { 
      path: '/account/profile', 
      label: 'Profile', 
      icon: <AccountCircle />, 
      requiresAuth: true 
    },
    { 
      path: '/account/billing', 
      label: 'Billing', 
      icon: <Receipt />, 
      requiresAuth: true 
    },
    { 
      path: '/account/addresses', 
      label: 'Address Book', 
      icon: <LocationOn />, 
      requiresAuth: true 
    },
    { 
      path: '/account/payment-methods', 
      label: 'Payment Methods', 
      icon: <CreditCard />, 
      requiresAuth: true 
    },
    { divider: true, path: '', label: '', icon: null },
    {
      path: '/corporate/dashboard',
      label: 'Corporate Dashboard',
      icon: <Business />,
      requiresAuth: true,
      requiresCorporate: true,
    },
    { divider: true, path: '', label: '', icon: null },
    { path: '/support', label: 'Help Center', icon: <Support /> },
    { path: '/contact', label: 'Contact Us', icon: <ContactSupport /> },
  ];

  const mockNotifications = [
    { id: '1', title: 'Package Delivered', message: 'Your package EXL123456789 has been delivered', read: false },
    { id: '2', title: 'Shipment Update', message: 'Your package is out for delivery', read: false },
    { id: '3', title: 'Payment Processed', message: 'Your payment of $24.99 has been processed', read: true },
  ];

  const handleDrawerToggle = () => {
    setDrawerOpen(!drawerOpen);
  };

  const handleUserMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setUserMenuAnchor(event.currentTarget);
  };

  const handleUserMenuClose = () => {
    setUserMenuAnchor(null);
  };

  const handleNotificationMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setNotificationMenuAnchor(event.currentTarget);
  };

  const handleNotificationMenuClose = () => {
    setNotificationMenuAnchor(null);
  };

  const handleNavigation = (path: string) => {
    navigate(path);
    setDrawerOpen(false);
  };

  const handleLogout = () => {
    logout();
    setUserMenuAnchor(null);
    showNotification('Successfully logged out', 'success');
    navigate('/');
  };

  const filteredNavigationItems = navigationItems.filter(item => {
    if (item.requiresAuth && !isAuthenticated) return false;
    if (item.requiresCorporate && user?.accountType !== 'corporate') return false;
    return true;
  });

  const generateBreadcrumbs = () => {
    const pathSegments = location.pathname.split('/').filter(segment => segment !== '');
    const breadcrumbs = [{ label: 'Home', path: '/' }];
    
    let currentPath = '';
    pathSegments.forEach(segment => {
      currentPath += `/${segment}`;
      const item = navigationItems.find(nav => nav.path === currentPath);
      if (item) {
        breadcrumbs.push({ label: item.label, path: currentPath });
      } else {
        // Generate a readable label from the segment
        const label = segment.split('-').map(word => 
          word.charAt(0).toUpperCase() + word.slice(1)
        ).join(' ');
        breadcrumbs.push({ label, path: currentPath });
      }
    });
    
    return breadcrumbs;
  };

  const breadcrumbs = generateBreadcrumbs();

  const drawerContent = (
    <Box sx={{ width: 280, pt: 2 }}>
      {/* Logo and Company Info */}
      <Box sx={{ px: 2, pb: 2 }}>
        <Typography variant="h6" fontWeight="bold" color="primary" gutterBottom>
          Exalt Courier
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Fast, Reliable, Secure Delivery
        </Typography>
      </Box>
      
      <Divider />
      
      {/* Navigation Items */}
      <List sx={{ pt: 1 }}>
        {filteredNavigationItems.map((item, index) => {
          if (item.divider) {
            return <Divider key={`divider-${index}`} sx={{ my: 1 }} />;
          }
          
          const isActive = location.pathname === item.path;
          
          return (
            <ListItem key={item.path} disablePadding>
              <ListItemButton
                onClick={() => handleNavigation(item.path)}
                selected={isActive}
                sx={{
                  mx: 1,
                  borderRadius: 1,
                  '&.Mui-selected': {
                    backgroundColor: theme.palette.primary.main,
                    color: 'white',
                    '& .MuiListItemIcon-root': {
                      color: 'white',
                    },
                  },
                }}
              >
                <ListItemIcon>
                  <Badge badgeContent={item.badge} color="error">
                    {item.icon}
                  </Badge>
                </ListItemIcon>
                <ListItemText primary={item.label} />
              </ListItemButton>
            </ListItem>
          );
        })}
      </List>
      
      {/* Quick Actions */}
      {isAuthenticated && (
        <>
          <Divider sx={{ mt: 2 }} />
          <Box sx={{ p: 2 }}>
            <Typography variant="subtitle2" color="text.secondary" gutterBottom>
              Quick Actions
            </Typography>
            <Box display="flex" flexDirection="column" gap={1}>
              <Button
                variant="outlined"
                size="small"
                startIcon={<Add />}
                onClick={() => handleNavigation('/book')}
                fullWidth
              >
                Book Shipment
              </Button>
              <Button
                variant="outlined"
                size="small"
                startIcon={<LocalShipping />}
                onClick={() => handleNavigation('/track')}
                fullWidth
              >
                Track Package
              </Button>
            </Box>
          </Box>
        </>
      )}
    </Box>
  );

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      {/* Top Navigation */}
      <AppBar position="sticky" elevation={1}>
        <Toolbar>
          <IconButton
            edge="start"
            color="inherit"
            aria-label="menu"
            onClick={handleDrawerToggle}
            sx={{ mr: 2 }}
          >
            <MenuIcon />
          </IconButton>
          
          <Typography
            variant="h6"
            component="div"
            sx={{ flexGrow: 1, cursor: 'pointer' }}
            onClick={() => navigate('/')}
          >
            Exalt Courier
          </Typography>
          
          {/* Desktop Navigation */}
          {!isMobile && (
            <Box sx={{ display: 'flex', gap: 1, mr: 2 }}>
              <Button color="inherit" onClick={() => navigate('/quote')}>
                Get Quote
              </Button>
              <Button color="inherit" onClick={() => navigate('/book')}>
                Book
              </Button>
              <Button color="inherit" onClick={() => navigate('/track')}>
                Track
              </Button>
              <Button color="inherit" onClick={() => navigate('/support')}>
                Support
              </Button>
            </Box>
          )}
          
          {/* User Actions */}
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            {isAuthenticated ? (
              <>
                {/* Notifications */}
                <IconButton
                  color="inherit"
                  onClick={handleNotificationMenuOpen}
                >
                  <Badge badgeContent={mockNotifications.filter(n => !n.read).length} color="error">
                    <Notifications />
                  </Badge>
                </IconButton>
                
                {/* User Menu */}
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  {user?.accountType === 'corporate' && (
                    <Chip
                      label="Business"
                      size="small"
                      color="secondary"
                      variant="outlined"
                    />
                  )}
                  <IconButton onClick={handleUserMenuOpen} color="inherit">
                    <Avatar
                      src={user?.avatar}
                      sx={{ width: 32, height: 32 }}
                    >
                      {user?.firstName?.[0]}{user?.lastName?.[0]}
                    </Avatar>
                  </IconButton>
                </Box>
              </>
            ) : (
              <Box sx={{ display: 'flex', gap: 1 }}>
                <Button color="inherit" onClick={() => navigate('/login')}>
                  Login
                </Button>
                <Button
                  variant="outlined"
                  color="inherit"
                  onClick={() => navigate('/register')}
                >
                  Sign Up
                </Button>
              </Box>
            )}
          </Box>
        </Toolbar>
      </AppBar>

      {/* Side Navigation Drawer */}
      <Drawer
        variant={isMobile ? 'temporary' : 'temporary'}
        open={drawerOpen}
        onClose={handleDrawerToggle}
        ModalProps={{
          keepMounted: true, // Better mobile performance
        }}
      >
        {drawerContent}
      </Drawer>

      {/* User Menu */}
      <Menu
        anchorEl={userMenuAnchor}
        open={Boolean(userMenuAnchor)}
        onClose={handleUserMenuClose}
        transformOrigin={{ horizontal: 'right', vertical: 'top' }}
        anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
      >
        <Box sx={{ px: 2, py: 1, borderBottom: 1, borderColor: 'divider' }}>
          <Typography variant="subtitle2">{user?.firstName} {user?.lastName}</Typography>
          <Typography variant="body2" color="text.secondary">{user?.email}</Typography>
        </Box>
        <MenuItem onClick={() => { handleNavigation('/account/profile'); handleUserMenuClose(); }}>
          <AccountCircle sx={{ mr: 1 }} /> Profile
        </MenuItem>
        <MenuItem onClick={() => { handleNavigation('/dashboard'); handleUserMenuClose(); }}>
          <Dashboard sx={{ mr: 1 }} /> Dashboard
        </MenuItem>
        <MenuItem onClick={() => { handleNavigation('/account/billing'); handleUserMenuClose(); }}>
          <Settings sx={{ mr: 1 }} /> Settings
        </MenuItem>
        <Divider />
        <MenuItem onClick={handleLogout}>
          <ExitToApp sx={{ mr: 1 }} /> Logout
        </MenuItem>
      </Menu>

      {/* Notifications Menu */}
      <Menu
        anchorEl={notificationMenuAnchor}
        open={Boolean(notificationMenuAnchor)}
        onClose={handleNotificationMenuClose}
        transformOrigin={{ horizontal: 'right', vertical: 'top' }}
        anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
        PaperProps={{
          sx: { width: 360, maxWidth: '90vw' }
        }}
      >
        <Box sx={{ px: 2, py: 1, borderBottom: 1, borderColor: 'divider' }}>
          <Typography variant="h6">Notifications</Typography>
        </Box>
        {mockNotifications.map((notification) => (
          <MenuItem key={notification.id} onClick={handleNotificationMenuClose}>
            <Box sx={{ width: '100%' }}>
              <Box display="flex" justifyContent="space-between" alignItems="center">
                <Typography variant="subtitle2">{notification.title}</Typography>
                {!notification.read && (
                  <Badge color="primary" variant="dot" />
                )}
              </Box>
              <Typography variant="body2" color="text.secondary">
                {notification.message}
              </Typography>
            </Box>
          </MenuItem>
        ))}
        <Divider />
        <MenuItem onClick={() => { handleNavigation('/notifications'); handleNotificationMenuClose(); }}>
          <Typography variant="body2" color="primary" textAlign="center" width="100%">
            View All Notifications
          </Typography>
        </MenuItem>
      </Menu>

      {/* Breadcrumbs */}
      {breadcrumbs.length > 1 && (
        <Container maxWidth="lg" sx={{ py: 1 }}>
          <Breadcrumbs aria-label="breadcrumb">
            {breadcrumbs.map((crumb, index) => {
              const isLast = index === breadcrumbs.length - 1;
              return isLast ? (
                <Typography key={crumb.path} color="text.primary">
                  {crumb.label}
                </Typography>
              ) : (
                <Link
                  key={crumb.path}
                  color="inherit"
                  href={crumb.path}
                  onClick={(e) => {
                    e.preventDefault();
                    navigate(crumb.path);
                  }}
                  sx={{ textDecoration: 'none', '&:hover': { textDecoration: 'underline' } }}
                >
                  {crumb.label}
                </Link>
              );
            })}
          </Breadcrumbs>
        </Container>
      )}

      {/* Main Content */}
      <Box component="main" sx={{ flexGrow: 1, bgcolor: 'background.default' }}>
        <Outlet />
      </Box>

      {/* Footer */}
      <Box
        component="footer"
        sx={{
          py: 3,
          px: 2,
          mt: 'auto',
          backgroundColor: theme.palette.grey[100],
        }}
      >
        <Container maxWidth="lg">
          <Box display="flex" justifyContent="space-between" alignItems="center" flexWrap="wrap" gap={2}>
            <Typography variant="body2" color="text.secondary">
              © 2024 Exalt Courier. All rights reserved.
            </Typography>
            <Box display="flex" gap={3}>
              <Link href="/privacy" color="text.secondary" underline="hover">
                Privacy Policy
              </Link>
              <Link href="/terms" color="text.secondary" underline="hover">
                Terms of Service
              </Link>
              <Link href="/contact" color="text.secondary" underline="hover">
                Contact
              </Link>
            </Box>
            <Box display="flex" alignItems="center" gap={2}>
              <IconButton size="small" onClick={() => window.open('tel:1-800-EXALT-01')}>
                <Phone fontSize="small" />
              </IconButton>
              <IconButton size="small" onClick={() => window.open('mailto:support@exaltcourier.com')}>
                <Email fontSize="small" />
              </IconButton>
              <Typography variant="body2" color="text.secondary">
                24/7 Support
              </Typography>
            </Box>
          </Box>
        </Container>
      </Box>
    </Box>
  );
};

export default MainLayout;