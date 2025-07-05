export interface Courier {
  id: string;
  userId: string;
  firstName: string;
  lastName: string;
  displayName: string;
  email: string;
  phone: string;
  profileImage?: string;
  status: CourierStatus;
  availability: CourierAvailability;
  location: CourierLocation;
  vehicle: CourierVehicle;
  serviceAreas: ServiceArea[];
  services: CourierService[];
  ratings: CourierRating;
  documents: CourierDocument[];
  bankDetails?: BankDetails;
  preferences: CourierPreferences;
  workingHours: WorkingHours[];
  createdAt: string;
  updatedAt: string;
  lastActiveAt?: string;
}

export enum CourierStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED',
  PENDING_VERIFICATION = 'PENDING_VERIFICATION',
  OFFLINE = 'OFFLINE',
  BUSY = 'BUSY',
}

export interface CourierAvailability {
  isAvailable: boolean;
  currentCapacity: number;
  maxCapacity: number;
  busyUntil?: string;
  nextAvailable?: string;
  autoAcceptOrders: boolean;
}

export interface CourierLocation {
  latitude: number;
  longitude: number;
  address: string;
  city: string;
  state: string;
  zipCode: string;
  country: string;
  lastUpdated: string;
  accuracy?: number;
}

export interface CourierVehicle {
  type: VehicleType;
  make?: string;
  model?: string;
  year?: number;
  color?: string;
  licensePlate: string;
  maxWeight: number;
  maxVolume: number;
  features: VehicleFeature[];
  insurance: VehicleInsurance;
}

export enum VehicleType {
  BIKE = 'BIKE',
  MOTORCYCLE = 'MOTORCYCLE',
  CAR = 'CAR',
  VAN = 'VAN',
  TRUCK = 'TRUCK',
  WALKING = 'WALKING',
}

export enum VehicleFeature {
  REFRIGERATED = 'REFRIGERATED',
  INSULATED = 'INSULATED',
  GPS_TRACKING = 'GPS_TRACKING',
  SECURE_STORAGE = 'SECURE_STORAGE',
  LIFT_GATE = 'LIFT_GATE',
  DOLLY = 'DOLLY',
  BLANKETS = 'BLANKETS',
}

export interface VehicleInsurance {
  provider: string;
  policyNumber: string;
  expiryDate: string;
  coverage: number;
  verified: boolean;
}

export interface ServiceArea {
  id: string;
  name: string;
  type: ServiceAreaType;
  coordinates: AreaCoordinate[];
  radius?: number;
  centerLatitude?: number;
  centerLongitude?: number;
  isActive: boolean;
}

export enum ServiceAreaType {
  CIRCLE = 'CIRCLE',
  POLYGON = 'POLYGON',
  CITY = 'CITY',
  STATE = 'STATE',
  ZIP_CODE = 'ZIP_CODE',
}

export interface AreaCoordinate {
  latitude: number;
  longitude: number;
  order: number;
}

export interface CourierService {
  type: ServiceType;
  name: string;
  description?: string;
  isAvailable: boolean;
  pricing: ServicePricing;
  requirements: ServiceRequirement[];
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

export interface ServicePricing {
  baseRate: number;
  perMileRate: number;
  perMinuteRate?: number;
  minimumCharge: number;
  maximumCharge?: number;
  surcharges: Surcharge[];
}

export interface Surcharge {
  type: SurchargeType;
  name: string;
  amount: number;
  isPercentage: boolean;
  conditions?: string;
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
}

export interface ServiceRequirement {
  type: RequirementType;
  description: string;
  isOptional: boolean;
}

export enum RequirementType {
  SIGNATURE_REQUIRED = 'SIGNATURE_REQUIRED',
  ID_VERIFICATION = 'ID_VERIFICATION',
  PHOTO_PROOF = 'PHOTO_PROOF',
  SPECIAL_HANDLING = 'SPECIAL_HANDLING',
  TEMPERATURE_CONTROL = 'TEMPERATURE_CONTROL',
  FRAGILE_HANDLING = 'FRAGILE_HANDLING',
}

export interface CourierRating {
  averageRating: number;
  totalRatings: number;
  ratingBreakdown: RatingBreakdown;
  recentRatings: Rating[];
}

export interface RatingBreakdown {
  5: number;
  4: number;
  3: number;
  2: number;
  1: number;
}

export interface Rating {
  id: string;
  orderId: string;
  customerId: string;
  customerName: string;
  rating: number;
  comment?: string;
  categories: RatingCategory[];
  createdAt: string;
}

export interface RatingCategory {
  category: string;
  rating: number;
}

export interface CourierDocument {
  id: string;
  type: DocumentType;
  name: string;
  url: string;
  status: DocumentStatus;
  expiryDate?: string;
  uploadedAt: string;
  verifiedAt?: string;
  verifiedBy?: string;
}

export enum DocumentType {
  DRIVERS_LICENSE = 'DRIVERS_LICENSE',
  VEHICLE_REGISTRATION = 'VEHICLE_REGISTRATION',
  VEHICLE_INSURANCE = 'VEHICLE_INSURANCE',
  BACKGROUND_CHECK = 'BACKGROUND_CHECK',
  PROFILE_PHOTO = 'PROFILE_PHOTO',
  VEHICLE_PHOTO = 'VEHICLE_PHOTO',
  BUSINESS_LICENSE = 'BUSINESS_LICENSE',
  TAX_FORM = 'TAX_FORM',
}

export enum DocumentStatus {
  PENDING = 'PENDING',
  VERIFIED = 'VERIFIED',
  REJECTED = 'REJECTED',
  EXPIRED = 'EXPIRED',
}

export interface BankDetails {
  accountHolderName: string;
  bankName: string;
  accountNumber: string;
  routingNumber: string;
  accountType: AccountType;
  isVerified: boolean;
  verifiedAt?: string;
}

export enum AccountType {
  CHECKING = 'CHECKING',
  SAVINGS = 'SAVINGS',
  BUSINESS = 'BUSINESS',
}

export interface CourierPreferences {
  notifications: NotificationPreferences;
  autoAcceptRadius: number;
  maxDeliveryDistance: number;
  preferredPaymentMethod: PaymentMethod;
  workingDays: DayOfWeek[];
  breakTimes: BreakTime[];
}

export interface NotificationPreferences {
  newOrderAlerts: boolean;
  orderUpdates: boolean;
  paymentNotifications: boolean;
  promotionalEmails: boolean;
  smsNotifications: boolean;
  pushNotifications: boolean;
}

export enum PaymentMethod {
  DIRECT_DEPOSIT = 'DIRECT_DEPOSIT',
  PAYPAL = 'PAYPAL',
  CHECK = 'CHECK',
  CRYPTO = 'CRYPTO',
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

export interface WorkingHours {
  dayOfWeek: DayOfWeek;
  isWorking: boolean;
  startTime?: string;
  endTime?: string;
  isAlwaysAvailable?: boolean;
}

export interface BreakTime {
  name: string;
  startTime: string;
  endTime: string;
  isActive: boolean;
}

export interface CourierSearchParams {
  location?: string;
  latitude?: number;
  longitude?: number;
  radius?: number;
  serviceType?: ServiceType[];
  vehicleType?: VehicleType[];
  minRating?: number;
  maxPrice?: number;
  availableNow?: boolean;
  features?: VehicleFeature[];
  sortBy?: CourierSortOption;
  limit?: number;
  offset?: number;
}

export enum CourierSortOption {
  DISTANCE = 'DISTANCE',
  RATING = 'RATING',
  PRICE = 'PRICE',
  DELIVERY_TIME = 'DELIVERY_TIME',
  AVAILABILITY = 'AVAILABILITY',
}

export interface CourierSearchResult {
  couriers: Courier[];
  totalCount: number;
  hasMore: boolean;
  searchParams: CourierSearchParams;
  searchId: string;
}