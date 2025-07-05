// Export all services for easy importing
export { default as apiClient } from './apiClient';
export { default as authService } from './authService';
export { default as shipmentService } from './shipmentService';

// Export types
export type { ApiResponse, PaginatedResponse } from './apiClient';
export type {
  LoginRequest,
  LoginResponse,
  ForgotPasswordRequest,
  ResetPasswordRequest,
  ChangePasswordRequest,
  ProfileUpdateRequest,
} from './authService';
export type {
  ShipmentQuoteRequest,
  ShipmentQuote,
  CreateShipmentRequest,
  Shipment,
  ShipmentEvent,
  TrackingInfo,
  ShipmentFilter,
} from './shipmentService';