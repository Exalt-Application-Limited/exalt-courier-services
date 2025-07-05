export interface DeliveryOrder {
  id: string;
  orderNumber: string;
  customerId: string;
  courierId?: string;
  status: OrderStatus;
  serviceType: ServiceType;
  priority: Priority;
  pickup: DeliveryLocation;
  dropoff: DeliveryLocation;
  packages: Package[];
  pricing: OrderPricing;
  timeline: OrderTimeline;
  tracking: TrackingInfo;
  instructions: DeliveryInstructions;
  documents: OrderDocument[];
  payments: PaymentInfo[];
  createdAt: string;
  updatedAt: string;
}

export enum OrderStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  ASSIGNED = 'ASSIGNED',
  PICKUP_SCHEDULED = 'PICKUP_SCHEDULED',
  EN_ROUTE_TO_PICKUP = 'EN_ROUTE_TO_PICKUP',
  AT_PICKUP = 'AT_PICKUP',
  PICKED_UP = 'PICKED_UP',
  IN_TRANSIT = 'IN_TRANSIT',
  AT_DESTINATION = 'AT_DESTINATION',
  DELIVERED = 'DELIVERED',
  FAILED = 'FAILED',
  CANCELLED = 'CANCELLED',
  RETURNED = 'RETURNED',
}

export enum ServiceType {
  STANDARD = 'STANDARD',
  EXPRESS = 'EXPRESS',
  OVERNIGHT = 'OVERNIGHT',
  SAME_DAY = 'SAME_DAY',
  SCHEDULED = 'SCHEDULED',
  WHITE_GLOVE = 'WHITE_GLOVE',
  FRAGILE = 'FRAGILE',
  FOOD_DELIVERY = 'FOOD_DELIVERY',
  PHARMACEUTICAL = 'PHARMACEUTICAL',
  DOCUMENTS = 'DOCUMENTS',
  FURNITURE = 'FURNITURE',
  INTERNATIONAL = 'INTERNATIONAL',
}

export enum Priority {
  LOW = 'LOW',
  NORMAL = 'NORMAL',
  HIGH = 'HIGH',
  URGENT = 'URGENT',
  EMERGENCY = 'EMERGENCY',
}

export interface DeliveryLocation {
  id?: string;
  type: LocationType;
  name: string;
  address: Address;
  coordinates: Coordinates;
  contact: ContactInfo;
  accessInstructions?: string;
  businessHours?: BusinessHours[];
  notes?: string;
}

export enum LocationType {
  RESIDENTIAL = 'RESIDENTIAL',
  BUSINESS = 'BUSINESS',
  RETAIL = 'RETAIL',
  WAREHOUSE = 'WAREHOUSE',
  OFFICE = 'OFFICE',
  HOSPITAL = 'HOSPITAL',
  SCHOOL = 'SCHOOL',
  AIRPORT = 'AIRPORT',
  OTHER = 'OTHER',
}

export interface Address {
  street: string;
  apartment?: string;
  city: string;
  state: string;
  zipCode: string;
  country: string;
  formattedAddress: string;
}

export interface Coordinates {
  latitude: number;
  longitude: number;
  accuracy?: number;
}

export interface ContactInfo {
  name: string;
  phone: string;
  email?: string;
  alternatePhone?: string;
}

export interface BusinessHours {
  dayOfWeek: DayOfWeek;
  isOpen: boolean;
  openTime?: string;
  closeTime?: string;
  isAlwaysOpen?: boolean;
}

export enum DayOfWeek {
  MONDAY = 'MONDAY',
  TUESDAY = 'TUESDAY',
  WEDNESDAY = 'WEDNESDAY',
  THURSDAY = 'THURSDAY',
  FRIDAY = 'FRIDAY',
  SATURDAY = 'SATURDAY',
  SUNDAY = 'SUNDAY',
}

export interface Package {
  id: string;
  description: string;
  type: PackageType;
  dimensions: PackageDimensions;
  weight: number;
  value?: number;
  quantity: number;
  specialHandling: SpecialHandling[];
  contents: string;
  fragile: boolean;
  requiresSignature: boolean;
  requiresId: boolean;
  photos?: string[];
}

export enum PackageType {
  DOCUMENT = 'DOCUMENT',
  SMALL_PACKAGE = 'SMALL_PACKAGE',
  MEDIUM_PACKAGE = 'MEDIUM_PACKAGE',
  LARGE_PACKAGE = 'LARGE_PACKAGE',
  ENVELOPE = 'ENVELOPE',
  BOX = 'BOX',
  TUBE = 'TUBE',
  FRAGILE = 'FRAGILE',
  PERISHABLE = 'PERISHABLE',
  LIQUID = 'LIQUID',
  FURNITURE = 'FURNITURE',
}

export interface PackageDimensions {
  length: number;
  width: number;
  height: number;
  unit: DimensionUnit;
}

export enum DimensionUnit {
  INCHES = 'INCHES',
  CENTIMETERS = 'CENTIMETERS',
}

export enum SpecialHandling {
  FRAGILE = 'FRAGILE',
  TEMPERATURE_CONTROLLED = 'TEMPERATURE_CONTROLLED',
  UPRIGHT_ONLY = 'UPRIGHT_ONLY',
  DO_NOT_STACK = 'DO_NOT_STACK',
  KEEP_DRY = 'KEEP_DRY',
  HANDLE_WITH_CARE = 'HANDLE_WITH_CARE',
  RUSH_DELIVERY = 'RUSH_DELIVERY',
}

export interface OrderPricing {
  basePrice: number;
  distancePrice: number;
  timePrice: number;
  surcharges: PricingSurcharge[];
  discounts: PricingDiscount[];
  tax: number;
  totalPrice: number;
  currency: string;
  priceBreakdown: PriceBreakdown[];
}

export interface PricingSurcharge {
  type: SurchargeType;
  name: string;
  amount: number;
  description?: string;
}

export enum SurchargeType {
  FUEL = 'FUEL',
  WEEKEND = 'WEEKEND',
  HOLIDAY = 'HOLIDAY',
  NIGHT = 'NIGHT',
  RUSH_HOUR = 'RUSH_HOUR',
  HEAVY_PACKAGE = 'HEAVY_PACKAGE',
  FRAGILE = 'FRAGILE',
  STAIRS = 'STAIRS',
  WAITING_TIME = 'WAITING_TIME',
  TOLL = 'TOLL',
  PARKING = 'PARKING',
}

export interface PricingDiscount {
  type: DiscountType;
  name: string;
  amount: number;
  percentage?: number;
  description?: string;
}

export enum DiscountType {
  FIRST_TIME = 'FIRST_TIME',
  BULK = 'BULK',
  LOYALTY = 'LOYALTY',
  PROMOTIONAL = 'PROMOTIONAL',
  CORPORATE = 'CORPORATE',
  SEASONAL = 'SEASONAL',
}

export interface PriceBreakdown {
  category: string;
  amount: number;
  description?: string;
}

export interface OrderTimeline {
  estimatedPickupTime: string;
  estimatedDeliveryTime: string;
  actualPickupTime?: string;
  actualDeliveryTime?: string;
  events: TimelineEvent[];
}

export interface TimelineEvent {
  id: string;
  type: EventType;
  title: string;
  description: string;
  timestamp: string;
  location?: Coordinates;
  metadata?: Record<string, any>;
}

export enum EventType {
  ORDER_CREATED = 'ORDER_CREATED',
  ORDER_CONFIRMED = 'ORDER_CONFIRMED',
  COURIER_ASSIGNED = 'COURIER_ASSIGNED',
  PICKUP_SCHEDULED = 'PICKUP_SCHEDULED',
  EN_ROUTE_TO_PICKUP = 'EN_ROUTE_TO_PICKUP',
  AT_PICKUP = 'AT_PICKUP',
  PICKED_UP = 'PICKED_UP',
  IN_TRANSIT = 'IN_TRANSIT',
  AT_DESTINATION = 'AT_DESTINATION',
  DELIVERED = 'DELIVERED',
  DELIVERY_FAILED = 'DELIVERY_FAILED',
  ORDER_CANCELLED = 'ORDER_CANCELLED',
  EXCEPTION = 'EXCEPTION',
}

export interface TrackingInfo {
  trackingNumber: string;
  currentLocation?: Coordinates;
  currentAddress?: string;
  lastUpdated: string;
  estimatedArrival?: string;
  route?: RoutePoint[];
  courierLocation?: Coordinates;
  courierPhone?: string;
}

export interface RoutePoint {
  latitude: number;
  longitude: number;
  timestamp: string;
  speed?: number;
  heading?: number;
}

export interface DeliveryInstructions {
  pickup: LocationInstructions;
  delivery: LocationInstructions;
  general?: string;
  courierNotes?: string;
}

export interface LocationInstructions {
  accessCode?: string;
  buzzerNumber?: string;
  floorNumber?: string;
  roomNumber?: string;
  parkingInstructions?: string;
  contactOnArrival: boolean;
  leaveAtDoor: boolean;
  signatureRequired: boolean;
  idRequired: boolean;
  photoRequired: boolean;
  notes?: string;
}

export interface OrderDocument {
  id: string;
  type: DocumentType;
  name: string;
  url: string;
  uploadedAt: string;
  uploadedBy: string;
}

export enum DocumentType {
  PICKUP_PHOTO = 'PICKUP_PHOTO',
  DELIVERY_PHOTO = 'DELIVERY_PHOTO',
  SIGNATURE = 'SIGNATURE',
  ID_VERIFICATION = 'ID_VERIFICATION',
  DAMAGE_REPORT = 'DAMAGE_REPORT',
  INVOICE = 'INVOICE',
  RECEIPT = 'RECEIPT',
  MANIFEST = 'MANIFEST',
}

export interface PaymentInfo {
  id: string;
  amount: number;
  method: PaymentMethod;
  status: PaymentStatus;
  transactionId?: string;
  processedAt?: string;
  refundedAt?: string;
  refundAmount?: number;
}

export enum PaymentMethod {
  CREDIT_CARD = 'CREDIT_CARD',
  DEBIT_CARD = 'DEBIT_CARD',
  PAYPAL = 'PAYPAL',
  APPLE_PAY = 'APPLE_PAY',
  GOOGLE_PAY = 'GOOGLE_PAY',
  CASH = 'CASH',
  CHECK = 'CHECK',
  CORPORATE_ACCOUNT = 'CORPORATE_ACCOUNT',
}

export enum PaymentStatus {
  PENDING = 'PENDING',
  AUTHORIZED = 'AUTHORIZED',
  CAPTURED = 'CAPTURED',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
  CANCELLED = 'CANCELLED',
  REFUNDED = 'REFUNDED',
  DISPUTED = 'DISPUTED',
}

export interface DeliveryQuote {
  id: string;
  customerId?: string;
  pickup: QuoteLocation;
  dropoff: QuoteLocation;
  packages: QuotePackage[];
  serviceType: ServiceType;
  priority: Priority;
  pricing: QuotePricing;
  availableCouriers: QuoteCourier[];
  estimatedTime: TimeEstimate;
  validUntil: string;
  createdAt: string;
}

export interface QuoteLocation {
  address: string;
  coordinates?: Coordinates;
  type: LocationType;
}

export interface QuotePackage {
  type: PackageType;
  weight: number;
  dimensions?: PackageDimensions;
  quantity: number;
  specialHandling: SpecialHandling[];
}

export interface QuotePricing {
  basePrice: number;
  totalPrice: number;
  priceRange: PriceRange;
  breakdown: PriceBreakdown[];
  currency: string;
}

export interface PriceRange {
  min: number;
  max: number;
  average: number;
}

export interface QuoteCourier {
  id: string;
  name: string;
  rating: number;
  vehicleType: VehicleType;
  estimatedTime: number;
  price: number;
  available: boolean;
}

export interface TimeEstimate {
  pickupTime: TimeRange;
  deliveryTime: TimeRange;
  totalDuration: number;
}

export interface TimeRange {
  earliest: string;
  latest: string;
  estimated: string;
}

export enum VehicleType {
  BIKE = 'BIKE',
  MOTORCYCLE = 'MOTORCYCLE',
  CAR = 'CAR',
  VAN = 'VAN',
  TRUCK = 'TRUCK',
  WALKING = 'WALKING',
}