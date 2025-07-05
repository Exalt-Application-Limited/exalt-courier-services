export interface ApiResponse<T = any> {
  success: boolean;
  data?: T;
  message?: string;
  error?: string;
  errors?: { [key: string]: string[] };
}

export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  phone: string;
  avatar?: string;
  preferences: UserPreferences;
  addresses: Address[];
  paymentMethods: PaymentMethod[];
  isEmailVerified: boolean;
  isPhoneVerified: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface UserPreferences {
  notifications: {
    email: boolean;
    sms: boolean;
    push: boolean;
  };
  language: string;
  currency: string;
  theme: 'light' | 'dark' | 'auto';
}

export interface Address {
  id: string;
  label: string;
  firstName: string;
  lastName: string;
  company?: string;
  streetAddress: string;
  apartment?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  phone?: string;
  isDefault: boolean;
  coordinates?: {
    latitude: number;
    longitude: number;
  };
}

export interface PaymentMethod {
  id: string;
  type: 'card' | 'bank' | 'wallet';
  label: string;
  last4?: string;
  brand?: string;
  expiryMonth?: number;
  expiryYear?: number;
  isDefault: boolean;
  isVerified: boolean;
}

export interface LoginRequest {
  email: string;
  password: string;
  rememberMe?: boolean;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string;
  agreeToTerms: boolean;
  agreeToMarketing?: boolean;
}

export interface Shipment {
  id: string;
  trackingNumber: string;
  status: ShipmentStatus;
  service: ShippingService;
  sender: ShipmentParty;
  recipient: ShipmentParty;
  package: Package;
  pricing: ShipmentPricing;
  timeline: ShipmentTimeline;
  tracking: TrackingEvent[];
  documents: ShipmentDocument[];
  createdAt: string;
  updatedAt: string;
}

export type ShipmentStatus = 
  | 'draft'
  | 'pending_payment'
  | 'confirmed'
  | 'picked_up'
  | 'in_transit'
  | 'out_for_delivery'
  | 'delivered'
  | 'failed_delivery'
  | 'returned'
  | 'cancelled';

export interface ShippingService {
  id: string;
  name: string;
  type: 'express' | 'standard' | 'economy';
  estimatedDays: number;
  cutoffTime: string;
  features: string[];
}

export interface ShipmentParty {
  firstName: string;
  lastName: string;
  company?: string;
  email: string;
  phone: string;
  address: Address;
}

export interface Package {
  weight: number;
  weightUnit: 'kg' | 'lb';
  dimensions: {
    length: number;
    width: number;
    height: number;
    unit: 'cm' | 'in';
  };
  value: number;
  currency: string;
  description: string;
  category: string;
  isFragile: boolean;
  requiresSignature: boolean;
  insurance?: {
    amount: number;
    type: string;
  };
}

export interface ShipmentPricing {
  subtotal: number;
  tax: number;
  insurance?: number;
  additionalServices: number;
  discount?: number;
  total: number;
  currency: string;
  breakdown: PricingBreakdown[];
}

export interface PricingBreakdown {
  label: string;
  amount: number;
  description?: string;
}

export interface ShipmentTimeline {
  estimatedPickup: string;
  estimatedDelivery: string;
  actualPickup?: string;
  actualDelivery?: string;
  businessDays: number;
}

export interface TrackingEvent {
  id: string;
  timestamp: string;
  status: string;
  description: string;
  location?: {
    city: string;
    state?: string;
    country: string;
    coordinates?: {
      latitude: number;
      longitude: number;
    };
  };
  facility?: string;
  nextUpdate?: string;
}

export interface ShipmentDocument {
  id: string;
  type: 'label' | 'invoice' | 'receipt' | 'customs' | 'insurance';
  name: string;
  url: string;
  downloadUrl: string;
  createdAt: string;
}

export interface Quote {
  id: string;
  services: QuoteService[];
  validUntil: string;
  createdAt: string;
}

export interface QuoteService {
  service: ShippingService;
  price: number;
  currency: string;
  estimatedDelivery: string;
  features: string[];
  isRecommended?: boolean;
  restrictions?: string[];
}

export interface QuoteRequest {
  sender: {
    postalCode: string;
    country: string;
  };
  recipient: {
    postalCode: string;
    country: string;
  };
  package: {
    weight: number;
    weightUnit: 'kg' | 'lb';
    dimensions: {
      length: number;
      width: number;
      height: number;
      unit: 'cm' | 'in';
    };
    value: number;
    currency: string;
  };
  services?: string[];
  insurance?: boolean;
}

export interface Notification {
  id: string;
  title: string;
  body: string;
  type: 'general' | 'shipment_update' | 'support_message' | 'promotion';
  data: { [key: string]: any };
  timestamp: Date;
  isRead: boolean;
  priority?: 'low' | 'normal' | 'high';
  actionUrl?: string;
}

export interface SupportTicket {
  id: string;
  subject: string;
  description: string;
  status: 'open' | 'in_progress' | 'resolved' | 'closed';
  priority: 'low' | 'medium' | 'high' | 'urgent';
  category: string;
  attachments: TicketAttachment[];
  messages: TicketMessage[];
  assignedTo?: string;
  createdAt: string;
  updatedAt: string;
}

export interface TicketMessage {
  id: string;
  content: string;
  sender: {
    id: string;
    name: string;
    role: 'customer' | 'agent';
    avatar?: string;
  };
  attachments: TicketAttachment[];
  timestamp: string;
}

export interface TicketAttachment {
  id: string;
  name: string;
  type: string;
  size: number;
  url: string;
  thumbnail?: string;
}

export interface ChatMessage {
  id: string;
  content: string;
  sender: {
    id: string;
    name: string;
    role: 'customer' | 'agent' | 'bot';
    avatar?: string;
  };
  timestamp: string;
  type: 'text' | 'image' | 'file' | 'quick_reply' | 'typing';
  status: 'sending' | 'sent' | 'delivered' | 'read';
  replyTo?: string;
  attachments?: {
    type: string;
    url: string;
    name?: string;
    size?: number;
  }[];
}

export interface Invoice {
  id: string;
  number: string;
  status: 'pending' | 'paid' | 'overdue' | 'cancelled';
  amount: number;
  currency: string;
  dueDate: string;
  issuedDate: string;
  paidDate?: string;
  items: InvoiceItem[];
  customer: {
    name: string;
    email: string;
    address: Address;
  };
  paymentMethod?: PaymentMethod;
  downloadUrl: string;
}

export interface InvoiceItem {
  description: string;
  quantity: number;
  unitPrice: number;
  total: number;
  shipmentId?: string;
}

export interface FormFieldError {
  field: string;
  message: string;
}

export interface PaginatedResponse<T> {
  data: T[];
  pagination: {
    page: number;
    limit: number;
    total: number;
    totalPages: number;
    hasNext: boolean;
    hasPrev: boolean;
  };
}

export interface FilterOptions {
  status?: string[];
  service?: string[];
  dateRange?: {
    from: string;
    to: string;
  };
  search?: string;
}

export interface SortOptions {
  field: string;
  direction: 'asc' | 'desc';
}