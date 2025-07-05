import apiClient, { ApiResponse, PaginatedResponse } from './apiClient';

export interface ShipmentQuoteRequest {
  origin: {
    address: string;
    city: string;
    state: string;
    zipCode: string;
    country?: string;
  };
  destination: {
    address: string;
    city: string;
    state: string;
    zipCode: string;
    country?: string;
  };
  package: {
    weight: number;
    dimensions: {
      length: number;
      width: number;
      height: number;
    };
    type: 'document' | 'package' | 'fragile' | 'hazardous';
    value?: number;
    description?: string;
  };
  serviceType: 'standard' | 'express' | 'overnight' | 'same_day';
  pickupDate?: string;
  deliveryDate?: string;
  insurance?: boolean;
  signatureRequired?: boolean;
}

export interface ShipmentQuote {
  id: string;
  basePrice: number;
  totalPrice: number;
  currency: string;
  serviceType: string;
  estimatedDelivery: string;
  transitTime: string;
  breakdown: {
    baseRate: number;
    fuelSurcharge: number;
    insurance?: number;
    taxes: number;
    additionalFees: number;
  };
  validUntil: string;
}

export interface CreateShipmentRequest {
  quoteId: string;
  sender: {
    name: string;
    email: string;
    phone: string;
    address: string;
    city: string;
    state: string;
    zipCode: string;
    country?: string;
    company?: string;
  };
  recipient: {
    name: string;
    email?: string;
    phone: string;
    address: string;
    city: string;
    state: string;
    zipCode: string;
    country?: string;
    company?: string;
  };
  package: {
    weight: number;
    dimensions: {
      length: number;
      width: number;
      height: number;
    };
    type: string;
    value?: number;
    description: string;
    contents: string[];
  };
  preferences: {
    pickupDate?: string;
    deliveryInstructions?: string;
    signatureRequired: boolean;
    insurance: boolean;
    notifications: {
      email: boolean;
      sms: boolean;
    };
  };
  paymentMethodId: string;
}

export interface Shipment {
  id: string;
  trackingNumber: string;
  status: 'created' | 'picked_up' | 'in_transit' | 'out_for_delivery' | 'delivered' | 'cancelled' | 'returned';
  serviceType: string;
  sender: any;
  recipient: any;
  package: any;
  origin: any;
  destination: any;
  timeline: ShipmentEvent[];
  pricing: {
    totalCost: number;
    currency: string;
    breakdown: any;
  };
  estimatedDelivery: string;
  actualDelivery?: string;
  createdAt: string;
  updatedAt: string;
}

export interface ShipmentEvent {
  id: string;
  timestamp: string;
  status: string;
  location: string;
  description: string;
  type: 'pickup' | 'transit' | 'delivery' | 'exception';
}

export interface TrackingInfo {
  trackingNumber: string;
  status: string;
  currentLocation?: string;
  estimatedDelivery: string;
  actualDelivery?: string;
  events: ShipmentEvent[];
  shipment?: Shipment;
}

export interface ShipmentFilter {
  status?: string;
  serviceType?: string;
  dateFrom?: string;
  dateTo?: string;
  search?: string;
}

class ShipmentService {
  private readonly basePath = '/shipments';

  // Quote management
  async getQuote(request: ShipmentQuoteRequest): Promise<ApiResponse<ShipmentQuote[]>> {
    return apiClient.post(`${this.basePath}/quote`, request);
  }

  async getQuoteById(quoteId: string): Promise<ApiResponse<ShipmentQuote>> {
    return apiClient.get(`${this.basePath}/quotes/${quoteId}`);
  }

  async saveQuote(quoteId: string): Promise<ApiResponse> {
    return apiClient.post(`${this.basePath}/quotes/${quoteId}/save`);
  }

  async getSavedQuotes(): Promise<ApiResponse<ShipmentQuote[]>> {
    return apiClient.get(`${this.basePath}/quotes/saved`);
  }

  // Shipment creation and management
  async createShipment(request: CreateShipmentRequest): Promise<ApiResponse<Shipment>> {
    return apiClient.post(`${this.basePath}`, request);
  }

  async getShipment(shipmentId: string): Promise<ApiResponse<Shipment>> {
    return apiClient.get(`${this.basePath}/${shipmentId}`);
  }

  async getShipments(
    filter?: ShipmentFilter,
    page?: number,
    limit?: number
  ): Promise<PaginatedResponse<Shipment>> {
    return apiClient.getPaginated(`${this.basePath}`, {
      ...filter,
      page,
      limit,
    });
  }

  async updateShipment(
    shipmentId: string, 
    updates: Partial<CreateShipmentRequest>
  ): Promise<ApiResponse<Shipment>> {
    return apiClient.put(`${this.basePath}/${shipmentId}`, updates);
  }

  async cancelShipment(shipmentId: string, reason?: string): Promise<ApiResponse> {
    return apiClient.post(`${this.basePath}/${shipmentId}/cancel`, { reason });
  }

  // Tracking
  async trackShipment(trackingNumber: string): Promise<ApiResponse<TrackingInfo>> {
    return apiClient.get(`/tracking/${trackingNumber}`);
  }

  async trackMultipleShipments(trackingNumbers: string[]): Promise<ApiResponse<TrackingInfo[]>> {
    return apiClient.post('/tracking/multiple', { trackingNumbers });
  }

  async subscribeToTracking(trackingNumber: string, notifications: {
    email?: string;
    phone?: string;
    webhook?: string;
  }): Promise<ApiResponse> {
    return apiClient.post(`/tracking/${trackingNumber}/subscribe`, notifications);
  }

  async unsubscribeFromTracking(trackingNumber: string): Promise<ApiResponse> {
    return apiClient.delete(`/tracking/${trackingNumber}/subscribe`);
  }

  // Delivery management
  async schedulePickup(shipmentId: string, pickupDate: string, timeWindow?: {
    start: string;
    end: string;
  }): Promise<ApiResponse> {
    return apiClient.post(`${this.basePath}/${shipmentId}/pickup`, {
      pickupDate,
      timeWindow,
    });
  }

  async rescheduleDelivery(shipmentId: string, newDate: string, timeWindow?: {
    start: string;
    end: string;
  }): Promise<ApiResponse> {
    return apiClient.post(`${this.basePath}/${shipmentId}/reschedule`, {
      newDate,
      timeWindow,
    });
  }

  async updateDeliveryInstructions(
    shipmentId: string, 
    instructions: string
  ): Promise<ApiResponse> {
    return apiClient.put(`${this.basePath}/${shipmentId}/instructions`, {
      instructions,
    });
  }

  async authorizeRelease(shipmentId: string, authorization: {
    releaseLocation?: string;
    recipientName?: string;
    signatureWaiver?: boolean;
  }): Promise<ApiResponse> {
    return apiClient.post(`${this.basePath}/${shipmentId}/authorize-release`, authorization);
  }

  // Documentation and labels
  async generateLabel(shipmentId: string, format: 'pdf' | 'png' = 'pdf'): Promise<void> {
    return apiClient.downloadFile(
      `${this.basePath}/${shipmentId}/label?format=${format}`,
      `label-${shipmentId}.${format}`
    );
  }

  async generateInvoice(shipmentId: string): Promise<void> {
    return apiClient.downloadFile(
      `${this.basePath}/${shipmentId}/invoice`,
      `invoice-${shipmentId}.pdf`
    );
  }

  async generateBOL(shipmentId: string): Promise<void> {
    return apiClient.downloadFile(
      `${this.basePath}/${shipmentId}/bol`,
      `bol-${shipmentId}.pdf`
    );
  }

  async uploadDocuments(shipmentId: string, files: File[]): Promise<ApiResponse<{
    uploadedFiles: Array<{
      id: string;
      filename: string;
      url: string;
    }>;
  }>> {
    const formData = new FormData();
    files.forEach((file, index) => {
      formData.append(`document_${index}`, file);
    });

    return apiClient.post(`${this.basePath}/${shipmentId}/documents`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  }

  async getDocuments(shipmentId: string): Promise<ApiResponse<Array<{
    id: string;
    filename: string;
    type: string;
    uploadedAt: string;
    url: string;
  }>>> {
    return apiClient.get(`${this.basePath}/${shipmentId}/documents`);
  }

  async deleteDocument(shipmentId: string, documentId: string): Promise<ApiResponse> {
    return apiClient.delete(`${this.basePath}/${shipmentId}/documents/${documentId}`);
  }

  // Analytics and reporting
  async getShipmentAnalytics(period: 'week' | 'month' | 'quarter' | 'year'): Promise<ApiResponse<{
    totalShipments: number;
    totalCost: number;
    onTimeDelivery: number;
    averageTransitTime: number;
    topDestinations: Array<{ location: string; count: number }>;
    statusBreakdown: Record<string, number>;
    monthlyTrend: Array<{ month: string; shipments: number; cost: number }>;
  }>> {
    return apiClient.get(`${this.basePath}/analytics?period=${period}`);
  }

  async exportShipments(
    filter?: ShipmentFilter,
    format: 'csv' | 'excel' = 'csv'
  ): Promise<void> {
    const params = new URLSearchParams({
      format,
      ...filter,
    });
    
    return apiClient.downloadFile(
      `${this.basePath}/export?${params.toString()}`,
      `shipments-export.${format}`
    );
  }

  // Bulk operations
  async createBulkShipments(
    shipments: CreateShipmentRequest[]
  ): Promise<ApiResponse<{
    successful: Shipment[];
    failed: Array<{
      index: number;
      error: string;
      shipment: CreateShipmentRequest;
    }>;
  }>> {
    return apiClient.post(`${this.basePath}/bulk`, { shipments });
  }

  async cancelMultipleShipments(shipmentIds: string[], reason?: string): Promise<ApiResponse<{
    successful: string[];
    failed: Array<{
      shipmentId: string;
      error: string;
    }>;
  }>> {
    return apiClient.post(`${this.basePath}/bulk/cancel`, {
      shipmentIds,
      reason,
    });
  }

  // Service areas and capabilities
  async getServiceAreas(): Promise<ApiResponse<Array<{
    country: string;
    states: Array<{
      state: string;
      cities: string[];
      serviceTypes: string[];
    }>;
  }>>> {
    return apiClient.get('/service-areas');
  }

  async validateAddress(address: {
    address: string;
    city: string;
    state: string;
    zipCode: string;
    country?: string;
  }): Promise<ApiResponse<{
    valid: boolean;
    suggestions?: Array<{
      address: string;
      city: string;
      state: string;
      zipCode: string;
      confidence: number;
    }>;
  }>> {
    return apiClient.post('/validate-address', address);
  }

  async getServiceTypes(): Promise<ApiResponse<Array<{
    code: string;
    name: string;
    description: string;
    transitTime: string;
    features: string[];
  }>>> {
    return apiClient.get('/service-types');
  }
}

// Create and export singleton instance
const shipmentService = new ShipmentService();
export default shipmentService;