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
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Chip,
  Avatar,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Divider,
  Alert,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Rating,
  Tabs,
  Tab,
  Badge,
  IconButton,
  Tooltip,
  Stepper,
  Step,
  StepLabel,
  StepContent,
} from '@mui/material';
import {
  ExpandMore,
  Phone,
  Email,
  Chat,
  Help,
  Search,
  Send,
  Attachment,
  Schedule,
  CheckCircle,
  Info,
  Warning,
  Error,
  Star,
  LiveHelp,
  ContactSupport,
  Feedback,
  BugReport,
  QuestionAnswer,
  VideoCall,
  Description,
  LocalShipping,
  Payment,
  AccountCircle,
  Security,
  Add,
  Close,
} from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';
import { useNotification } from '../../contexts/NotificationContext';

interface FAQ {
  id: string;
  question: string;
  answer: string;
  category: string;
  helpful: number;
  notHelpful: number;
  tags: string[];
}

interface SupportTicket {
  id: string;
  subject: string;
  description: string;
  status: 'open' | 'in_progress' | 'resolved' | 'closed';
  priority: 'low' | 'medium' | 'high' | 'urgent';
  category: string;
  createdAt: string;
  updatedAt: string;
  responses: TicketResponse[];
}

interface TicketResponse {
  id: string;
  message: string;
  timestamp: string;
  isFromSupport: boolean;
  agentName?: string;
  attachments?: string[];
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
      id={`support-tabpanel-${index}`}
      aria-labelledby={`support-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ py: 3 }}>{children}</Box>}
    </div>
  );
};

const Support: React.FC = () => {
  const { user } = useAuth();
  const { showNotification } = useNotification();
  
  const [activeTab, setActiveTab] = useState(0);
  const [faqs, setFaqs] = useState<FAQ[]>([]);
  const [tickets, setTickets] = useState<SupportTicket[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [filteredFaqs, setFilteredFaqs] = useState<FAQ[]>([]);
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [newTicketDialog, setNewTicketDialog] = useState(false);
  const [ticketDetailsDialog, setTicketDetailsDialog] = useState(false);
  const [selectedTicket, setSelectedTicket] = useState<SupportTicket | null>(null);
  const [chatDialog, setChatDialog] = useState(false);
  const [loading, setLoading] = useState(false);
  
  const [newTicket, setNewTicket] = useState({
    subject: '',
    description: '',
    category: '',
    priority: 'medium' as 'low' | 'medium' | 'high' | 'urgent',
  });

  useEffect(() => {
    loadSupportData();
  }, []);

  useEffect(() => {
    filterFaqs();
  }, [faqs, searchQuery, selectedCategory]);

  const loadSupportData = async () => {
    setLoading(true);
    try {
      // Mock FAQ data
      const mockFaqs: FAQ[] = [
        {
          id: '1',
          question: 'How do I track my shipment?',
          answer: 'You can track your shipment by entering your tracking number on our tracking page, or by logging into your account and viewing your delivery history.',
          category: 'Tracking',
          helpful: 45,
          notHelpful: 3,
          tags: ['tracking', 'shipment', 'delivery'],
        },
        {
          id: '2',
          question: 'What are your delivery timeframes?',
          answer: 'We offer several delivery options: Standard (3-5 business days), Express (1-2 business days), Overnight (next business day), and Same Day (within 24 hours for select areas).',
          category: 'Delivery',
          helpful: 38,
          notHelpful: 2,
          tags: ['delivery', 'timeframe', 'speed'],
        },
        {
          id: '3',
          question: 'How do I change my delivery address?',
          answer: 'You can change your delivery address before your package is out for delivery by logging into your account and updating the shipment details, or by contacting our customer service.',
          category: 'Delivery',
          helpful: 29,
          notHelpful: 5,
          tags: ['address', 'change', 'delivery'],
        },
        {
          id: '4',
          question: 'What payment methods do you accept?',
          answer: 'We accept all major credit cards (Visa, MasterCard, American Express), PayPal, bank transfers, and corporate accounts for business customers.',
          category: 'Billing',
          helpful: 33,
          notHelpful: 1,
          tags: ['payment', 'billing', 'methods'],
        },
        {
          id: '5',
          question: 'How do I file a claim for damaged packages?',
          answer: 'To file a claim, please contact our customer service within 24 hours of delivery with photos of the damage and your tracking number. We will process your claim within 3-5 business days.',
          category: 'Claims',
          helpful: 22,
          notHelpful: 4,
          tags: ['claims', 'damage', 'compensation'],
        },
      ];

      // Mock ticket data
      const mockTickets: SupportTicket[] = [
        {
          id: 'TKT-001',
          subject: 'Package delivered to wrong address',
          description: 'My package was delivered to the wrong address. Tracking shows delivered but I never received it.',
          status: 'in_progress',
          priority: 'high',
          category: 'Delivery Issue',
          createdAt: '2024-01-15T10:30:00Z',
          updatedAt: '2024-01-15T14:20:00Z',
          responses: [
            {
              id: '1',
              message: 'My package was delivered to the wrong address. Tracking shows delivered but I never received it.',
              timestamp: '2024-01-15T10:30:00Z',
              isFromSupport: false,
            },
            {
              id: '2',
              message: 'Thank you for contacting us. I apologize for the inconvenience. We are investigating this issue with the delivery driver and will provide an update within 24 hours.',
              timestamp: '2024-01-15T14:20:00Z',
              isFromSupport: true,
              agentName: 'Sarah Johnson',
            },
          ],
        },
        {
          id: 'TKT-002',
          subject: 'Billing inquiry about additional charges',
          description: 'I was charged extra fees that were not disclosed during booking.',
          status: 'resolved',
          priority: 'medium',
          category: 'Billing',
          createdAt: '2024-01-12T09:15:00Z',
          updatedAt: '2024-01-13T16:45:00Z',
          responses: [
            {
              id: '1',
              message: 'I was charged extra fees that were not disclosed during booking.',
              timestamp: '2024-01-12T09:15:00Z',
              isFromSupport: false,
            },
            {
              id: '2',
              message: 'I have reviewed your account and found that the additional charges were for oversized package handling. This fee should have been disclosed during booking. I will process a refund for the unexpected charges.',
              timestamp: '2024-01-13T16:45:00Z',
              isFromSupport: true,
              agentName: 'Mike Davis',
            },
          ],
        },
      ];

      setFaqs(mockFaqs);
      setTickets(mockTickets);
    } catch (error) {
      showNotification('Failed to load support data', 'error');
    } finally {
      setLoading(false);
    }
  };

  const filterFaqs = () => {
    let filtered = faqs;
    
    if (selectedCategory !== 'all') {
      filtered = filtered.filter(faq => faq.category === selectedCategory);
    }
    
    if (searchQuery) {
      filtered = filtered.filter(faq =>
        faq.question.toLowerCase().includes(searchQuery.toLowerCase()) ||
        faq.answer.toLowerCase().includes(searchQuery.toLowerCase()) ||
        faq.tags.some(tag => tag.toLowerCase().includes(searchQuery.toLowerCase()))
      );
    }
    
    setFilteredFaqs(filtered);
  };

  const handleCreateTicket = async () => {
    if (!newTicket.subject || !newTicket.description || !newTicket.category) {
      showNotification('Please fill in all required fields', 'warning');
      return;
    }
    
    setLoading(true);
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      const ticket: SupportTicket = {
        id: `TKT-${Date.now()}`,
        subject: newTicket.subject,
        description: newTicket.description,
        status: 'open',
        priority: newTicket.priority,
        category: newTicket.category,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
        responses: [
          {
            id: '1',
            message: newTicket.description,
            timestamp: new Date().toISOString(),
            isFromSupport: false,
          },
        ],
      };
      
      setTickets(prev => [ticket, ...prev]);
      setNewTicketDialog(false);
      setNewTicket({
        subject: '',
        description: '',
        category: '',
        priority: 'medium',
      });
      
      showNotification(`Support ticket ${ticket.id} created successfully`, 'success');
    } catch (error) {
      showNotification('Failed to create support ticket', 'error');
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'open':
        return 'info';
      case 'in_progress':
        return 'warning';
      case 'resolved':
        return 'success';
      case 'closed':
        return 'default';
      default:
        return 'default';
    }
  };

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'urgent':
        return 'error';
      case 'high':
        return 'warning';
      case 'medium':
        return 'info';
      case 'low':
        return 'success';
      default:
        return 'default';
    }
  };

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setActiveTab(newValue);
  };

  const faqCategories = ['all', ...Array.from(new Set(faqs.map(faq => faq.category)))];

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" fontWeight="bold" gutterBottom>
        Help & Support
      </Typography>
      
      {/* Contact Options */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent sx={{ textAlign: 'center' }}>
              <Phone color="primary" sx={{ fontSize: 48, mb: 2 }} />
              <Typography variant="h6" fontWeight="bold" gutterBottom>
                Call Us
              </Typography>
              <Typography variant="body2" color="text.secondary" mb={2}>
                24/7 Customer Support
              </Typography>
              <Typography variant="h6" color="primary">
                1-800-EXALT-01
              </Typography>
            </CardContent>
            <CardActions>
              <Button
                fullWidth
                variant="outlined"
                startIcon={<Phone />}
                onClick={() => window.open('tel:1-800-EXALT-01')}
              >
                Call Now
              </Button>
            </CardActions>
          </Card>
        </Grid>
        
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent sx={{ textAlign: 'center' }}>
              <Chat color="primary" sx={{ fontSize: 48, mb: 2 }} />
              <Typography variant="h6" fontWeight="bold" gutterBottom>
                Live Chat
              </Typography>
              <Typography variant="body2" color="text.secondary" mb={2}>
                Instant support online
              </Typography>
              <Chip label="Available" color="success" size="small" />
            </CardContent>
            <CardActions>
              <Button
                fullWidth
                variant="outlined"
                startIcon={<Chat />}
                onClick={() => setChatDialog(true)}
              >
                Start Chat
              </Button>
            </CardActions>
          </Card>
        </Grid>
        
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent sx={{ textAlign: 'center' }}>
              <Email color="primary" sx={{ fontSize: 48, mb: 2 }} />
              <Typography variant="h6" fontWeight="bold" gutterBottom>
                Email Support
              </Typography>
              <Typography variant="body2" color="text.secondary" mb={2}>
                Get help via email
              </Typography>
              <Typography variant="body2" color="primary">
                support@exaltcourier.com
              </Typography>
            </CardContent>
            <CardActions>
              <Button
                fullWidth
                variant="outlined"
                startIcon={<Email />}
                onClick={() => setNewTicketDialog(true)}
              >
                Send Message
              </Button>
            </CardActions>
          </Card>
        </Grid>
      </Grid>

      {/* Support Tabs */}
      <Paper>
        <Tabs value={activeTab} onChange={handleTabChange}>
          <Tab
            label="FAQ"
            icon={<Help />}
            iconPosition="start"
          />
          <Tab
            label={
              <Badge badgeContent={tickets.filter(t => t.status !== 'closed').length} color="primary">
                My Tickets
              </Badge>
            }
            icon={<ContactSupport />}
            iconPosition="start"
          />
          <Tab
            label="Quick Help"
            icon={<LiveHelp />}
            iconPosition="start"
          />
        </Tabs>

        {/* FAQ Tab */}
        <TabPanel value={activeTab} index={0}>
          <Box sx={{ mb: 3 }}>
            <Grid container spacing={2} alignItems="center">
              <Grid item xs={12} md={8}>
                <TextField
                  fullWidth
                  label="Search FAQs"
                  placeholder="Enter your question or keywords"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  InputProps={{
                    startAdornment: <Search sx={{ mr: 1, color: 'text.secondary' }} />,
                  }}
                />
              </Grid>
              <Grid item xs={12} md={4}>
                <FormControl fullWidth>
                  <InputLabel>Category</InputLabel>
                  <Select
                    value={selectedCategory}
                    label="Category"
                    onChange={(e) => setSelectedCategory(e.target.value)}
                  >
                    {faqCategories.map(category => (
                      <MenuItem key={category} value={category}>
                        {category === 'all' ? 'All Categories' : category}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
            </Grid>
          </Box>
          
          <Box>
            {filteredFaqs.map((faq) => (
              <Accordion key={faq.id}>
                <AccordionSummary expandIcon={<ExpandMore />}>
                  <Box sx={{ width: '100%' }}>
                    <Box display="flex" justifyContent="space-between" alignItems="center">
                      <Typography variant="subtitle1" fontWeight="medium">
                        {faq.question}
                      </Typography>
                      <Chip
                        label={faq.category}
                        size="small"
                        color="primary"
                        variant="outlined"
                      />
                    </Box>
                  </Box>
                </AccordionSummary>
                <AccordionDetails>
                  <Typography variant="body2" paragraph>
                    {faq.answer}
                  </Typography>
                  
                  <Divider sx={{ my: 2 }} />
                  
                  <Box display="flex" justifyContent="space-between" alignItems="center">
                    <Box display="flex" gap={1}>
                      {faq.tags.map(tag => (
                        <Chip
                          key={tag}
                          label={tag}
                          size="small"
                          variant="outlined"
                        />
                      ))}
                    </Box>
                    
                    <Box display="flex" alignItems="center" gap={2}>
                      <Typography variant="body2" color="text.secondary">
                        Was this helpful?
                      </Typography>
                      <Button
                        size="small"
                        startIcon={<CheckCircle />}
                        onClick={() => showNotification('Thank you for your feedback!', 'success')}
                      >
                        Yes ({faq.helpful})
                      </Button>
                      <Button
                        size="small"
                        startIcon={<Close />}
                        onClick={() => showNotification('Thank you for your feedback!', 'info')}
                      >
                        No ({faq.notHelpful})
                      </Button>
                    </Box>
                  </Box>
                </AccordionDetails>
              </Accordion>
            ))}
          </Box>
          
          {filteredFaqs.length === 0 && (
            <Alert severity="info" sx={{ mt: 2 }}>
              No FAQs found matching your search criteria. Try adjusting your search terms or contact support directly.
            </Alert>
          )}
        </TabPanel>

        {/* My Tickets Tab */}
        <TabPanel value={activeTab} index={1}>
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
            <Typography variant="h6" fontWeight="bold">
              Your Support Tickets
            </Typography>
            <Button
              variant="contained"
              startIcon={<Add />}
              onClick={() => setNewTicketDialog(true)}
            >
              Create New Ticket
            </Button>
          </Box>
          
          <Grid container spacing={2}>
            {tickets.map((ticket) => (
              <Grid item xs={12} key={ticket.id}>
                <Card>
                  <CardContent>
                    <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
                      <Box>
                        <Typography variant="h6" fontWeight="bold" gutterBottom>
                          {ticket.subject}
                        </Typography>
                        <Typography variant="body2" color="text.secondary" gutterBottom>
                          Ticket ID: {ticket.id}
                        </Typography>
                        <Typography variant="body2" paragraph>
                          {ticket.description.length > 100
                            ? `${ticket.description.substring(0, 100)}...`
                            : ticket.description
                          }
                        </Typography>
                      </Box>
                      
                      <Box display="flex" flexDirection="column" gap={1} alignItems="flex-end">
                        <Chip
                          label={ticket.status.replace('_', ' ').toUpperCase()}
                          color={getStatusColor(ticket.status) as any}
                          size="small"
                        />
                        <Chip
                          label={ticket.priority.toUpperCase()}
                          color={getPriorityColor(ticket.priority) as any}
                          variant="outlined"
                          size="small"
                        />
                      </Box>
                    </Box>
                    
                    <Box display="flex" justifyContent="space-between" alignItems="center">
                      <Box>
                        <Typography variant="body2" color="text.secondary">
                          Created: {new Date(ticket.createdAt).toLocaleDateString()}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          Category: {ticket.category}
                        </Typography>
                      </Box>
                      
                      <Button
                        variant="outlined"
                        onClick={() => {
                          setSelectedTicket(ticket);
                          setTicketDetailsDialog(true);
                        }}
                      >
                        View Details
                      </Button>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
          
          {tickets.length === 0 && (
            <Alert severity="info">
              You don't have any support tickets yet. If you need help, feel free to create a new ticket or use our live chat.
            </Alert>
          )}
        </TabPanel>

        {/* Quick Help Tab */}
        <TabPanel value={activeTab} index={2}>
          <Grid container spacing={3}>
            <Grid item xs={12} md={6}>
              <Card>
                <CardContent>
                  <LocalShipping color="primary" sx={{ fontSize: 48, mb: 2 }} />
                  <Typography variant="h6" fontWeight="bold" gutterBottom>
                    Track a Package
                  </Typography>
                  <Typography variant="body2" color="text.secondary" mb={2}>
                    Enter your tracking number to get real-time updates
                  </Typography>
                  <TextField
                    fullWidth
                    label="Tracking Number"
                    placeholder="e.g., EXL123456789"
                    sx={{ mb: 2 }}
                  />
                  <Button variant="contained" fullWidth>
                    Track Now
                  </Button>
                </CardContent>
              </Card>
            </Grid>
            
            <Grid item xs={12} md={6}>
              <Card>
                <CardContent>
                  <Payment color="primary" sx={{ fontSize: 48, mb: 2 }} />
                  <Typography variant="h6" fontWeight="bold" gutterBottom>
                    Billing Inquiry
                  </Typography>
                  <Typography variant="body2" color="text.secondary" mb={2}>
                    Questions about charges or need an invoice
                  </Typography>
                  <List dense>
                    <ListItem>
                      <ListItemText primary="View billing history" />
                    </ListItem>
                    <ListItem>
                      <ListItemText primary="Download invoices" />
                    </ListItem>
                    <ListItem>
                      <ListItemText primary="Update payment methods" />
                    </ListItem>
                  </List>
                  <Button variant="outlined" fullWidth>
                    Go to Billing
                  </Button>
                </CardContent>
              </Card>
            </Grid>
            
            <Grid item xs={12} md={6}>
              <Card>
                <CardContent>
                  <AccountCircle color="primary" sx={{ fontSize: 48, mb: 2 }} />
                  <Typography variant="h6" fontWeight="bold" gutterBottom>
                    Account Settings
                  </Typography>
                  <Typography variant="body2" color="text.secondary" mb={2}>
                    Manage your profile, addresses, and preferences
                  </Typography>
                  <List dense>
                    <ListItem>
                      <ListItemText primary="Update profile information" />
                    </ListItem>
                    <ListItem>
                      <ListItemText primary="Manage delivery addresses" />
                    </ListItem>
                    <ListItem>
                      <ListItemText primary="Notification preferences" />
                    </ListItem>
                  </List>
                  <Button variant="outlined" fullWidth>
                    Manage Account
                  </Button>
                </CardContent>
              </Card>
            </Grid>
            
            <Grid item xs={12} md={6}>
              <Card>
                <CardContent>
                  <BugReport color="primary" sx={{ fontSize: 48, mb: 2 }} />
                  <Typography variant="h6" fontWeight="bold" gutterBottom>
                    Report an Issue
                  </Typography>
                  <Typography variant="body2" color="text.secondary" mb={2}>
                    Having technical problems or found a bug?
                  </Typography>
                  <List dense>
                    <ListItem>
                      <ListItemText primary="Website issues" />
                    </ListItem>
                    <ListItem>
                      <ListItemText primary="Mobile app problems" />
                    </ListItem>
                    <ListItem>
                      <ListItemText primary="Service delivery issues" />
                    </ListItem>
                  </List>
                  <Button variant="outlined" fullWidth>
                    Report Issue
                  </Button>
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        </TabPanel>
      </Paper>

      {/* Create Ticket Dialog */}
      <Dialog open={newTicketDialog} onClose={() => setNewTicketDialog(false)} maxWidth="md" fullWidth>
        <DialogTitle>Create Support Ticket</DialogTitle>
        <DialogContent>
          <Grid container spacing={3} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Subject"
                value={newTicket.subject}
                onChange={(e) => setNewTicket({ ...newTicket, subject: e.target.value })}
                required
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <FormControl fullWidth required>
                <InputLabel>Category</InputLabel>
                <Select
                  value={newTicket.category}
                  label="Category"
                  onChange={(e) => setNewTicket({ ...newTicket, category: e.target.value })}
                >
                  <MenuItem value="Delivery Issue">Delivery Issue</MenuItem>
                  <MenuItem value="Billing">Billing</MenuItem>
                  <MenuItem value="Account">Account</MenuItem>
                  <MenuItem value="Technical">Technical Support</MenuItem>
                  <MenuItem value="General">General Inquiry</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} md={6}>
              <FormControl fullWidth>
                <InputLabel>Priority</InputLabel>
                <Select
                  value={newTicket.priority}
                  label="Priority"
                  onChange={(e) => setNewTicket({ ...newTicket, priority: e.target.value as any })}
                >
                  <MenuItem value="low">Low</MenuItem>
                  <MenuItem value="medium">Medium</MenuItem>
                  <MenuItem value="high">High</MenuItem>
                  <MenuItem value="urgent">Urgent</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Description"
                value={newTicket.description}
                onChange={(e) => setNewTicket({ ...newTicket, description: e.target.value })}
                multiline
                rows={4}
                required
                placeholder="Please provide detailed information about your issue..."
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setNewTicketDialog(false)}>Cancel</Button>
          <Button onClick={handleCreateTicket} variant="contained" disabled={loading}>
            {loading ? 'Creating...' : 'Create Ticket'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Ticket Details Dialog */}
      <Dialog
        open={ticketDetailsDialog}
        onClose={() => setTicketDetailsDialog(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>
          Ticket Details - {selectedTicket?.id}
        </DialogTitle>
        <DialogContent>
          {selectedTicket && (
            <Box>
              <Box display="flex" gap={2} mb={3}>
                <Chip
                  label={selectedTicket.status.replace('_', ' ').toUpperCase()}
                  color={getStatusColor(selectedTicket.status) as any}
                />
                <Chip
                  label={selectedTicket.priority.toUpperCase()}
                  color={getPriorityColor(selectedTicket.priority) as any}
                  variant="outlined"
                />
                <Chip
                  label={selectedTicket.category}
                  variant="outlined"
                />
              </Box>
              
              <Typography variant="h6" fontWeight="bold" gutterBottom>
                {selectedTicket.subject}
              </Typography>
              
              <Divider sx={{ my: 2 }} />
              
              <Box>
                {selectedTicket.responses.map((response) => (
                  <Box key={response.id} sx={{ mb: 3 }}>
                    <Box display="flex" alignItems="center" gap={2} mb={1}>
                      <Avatar sx={{ width: 32, height: 32 }}>
                        {response.isFromSupport ? 'S' : user?.firstName?.[0] || 'U'}
                      </Avatar>
                      <Box>
                        <Typography variant="subtitle2" fontWeight="bold">
                          {response.isFromSupport 
                            ? `${response.agentName} (Support)`
                            : `${user?.firstName} ${user?.lastName}`
                          }
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          {new Date(response.timestamp).toLocaleString()}
                        </Typography>
                      </Box>
                    </Box>
                    <Paper variant="outlined" sx={{ p: 2, ml: 5 }}>
                      <Typography variant="body2">
                        {response.message}
                      </Typography>
                    </Paper>
                  </Box>
                ))}
              </Box>
              
              {selectedTicket.status !== 'closed' && (
                <Box sx={{ mt: 3 }}>
                  <TextField
                    fullWidth
                    label="Add a response"
                    multiline
                    rows={3}
                    placeholder="Type your message here..."
                  />
                  <Box display="flex" justifyContent="flex-end" gap={1} sx={{ mt: 2 }}>
                    <Button startIcon={<Attachment />}>
                      Attach File
                    </Button>
                    <Button variant="contained" startIcon={<Send />}>
                      Send Response
                    </Button>
                  </Box>
                </Box>
              )}
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setTicketDetailsDialog(false)}>Close</Button>
        </DialogActions>
      </Dialog>

      {/* Live Chat Dialog */}
      <Dialog open={chatDialog} onClose={() => setChatDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Live Chat Support</DialogTitle>
        <DialogContent>
          <Alert severity="info" sx={{ mb: 2 }}>
            You are connected to our live chat support. Average wait time: 2 minutes
          </Alert>
          <Box sx={{ height: 300, border: 1, borderColor: 'divider', p: 2, borderRadius: 1, mb: 2 }}>
            <Typography variant="body2" color="text.secondary" textAlign="center">
              Chat feature coming soon. Please use phone or email support for immediate assistance.
            </Typography>
          </Box>
          <TextField
            fullWidth
            label="Type your message"
            placeholder="How can we help you today?"
            disabled
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setChatDialog(false)}>Close</Button>
          <Button variant="contained" disabled>
            Send
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default Support;