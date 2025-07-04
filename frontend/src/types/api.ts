// Base API Response type
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  error?: string;
}

// Product types
export interface Product {
  productId: number;
  title: string;
  price: number;
  weight?: number;
  rushOrderSupported: boolean;
  imageUrl?: string;
  barcode?: string;
  importDate?: string;
  introduction?: string;
  quantity: number;
  type: string;
  createdAt?: string;
  updatedAt?: string;
  dimensions?: string;
  condition?: string;
  stockStatus?: string;
}

export interface Book extends Product {
  bookId?: number;
  genre?: string;
  pageCount?: number;
  publicationDate?: string;
  authors?: string;
  publishers?: string;
  coverType?: string;
  language?: string;
  pages?: number;
  author?: string;
  publisher?: string;
}

export interface CD extends Product {
  cdId?: number;
  trackList?: string;
  genre?: string;
  recordLabel?: string;
  artists?: string;
  releaseDate?: string;
  artist?: string;
  musicType?: string;
  tracklist?: string;
}

export interface DVD extends Product {
  dvdId?: number;
  releaseDate?: string;
  dvdType?: string;
  genre?: string;
  studio?: string;
  directors?: string;
  durationMinutes?: number;
  rating?: string;
  director?: string;
  runtime?: number;
  discType?: string;
  subtitles?: string;
  languageDvd?: string;
}

export interface LP extends Product {
  lpId?: number;
  artist?: string;
  recordLabel?: string;
  musicType?: string;
  releaseDate?: string;
  tracklist?: string;
  rpm?: number;
  sizeInches?: number;
  vinylCondition?: string;
  sleeveCondition?: string;
}

// User types
export interface User {
  userId: number;
  name: string;
  email: string;
  phone?: string;
  role: 'ADMIN' | 'CUSTOMER' | 'MANAGER' | 'EMPLOYEE';
  registrationDate?: string;
  salary?: number;
  isActive: boolean;
  createdAt?: string;
  updatedAt?: string;
}

// Order types
export interface Order {
  orderId: number;
  totalBeforeVat: number;
  totalAfterVat: number;
  status: 'PENDING' | 'CONFIRMED' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
  vatPercentage: number;
  createdAt?: string;
  updatedAt?: string;
  deliveryInfo?: DeliveryInformation;
  orderLines?: OrderLine[];
  invoice?: Invoice;
  totalAmount: number;
}

export interface OrderLine {
  orderLineId: number;
  orderId: number;
  status: 'PENDING' | 'CONFIRMED' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
  rushOrder: boolean;
  quantity: number;
  totalFee: number;
  deliveryTime?: string;
  instructions?: string;
  product?: Product;
}

export interface DeliveryInformation {
  deliveryId: number;
  name: string;
  phone: string;
  email: string;
  address: string;
  province: string;
  deliveryMessage?: string;
  deliveryFee: number;
}

export interface DeliveryInformationDTO {
  deliveryId?: number;
  name: string;
  phone: string;
  email: string;
  province: string;
  district: string;
  ward: string;
  address: string;
  deliveryMessage?: string;
  deliveryFee?: number;
  deliveryTime?: string;
  rushDeliveryInstruction?: string;
}

// Invoice types
export interface Invoice {
  invoiceId: number;
  orderId: number;
  transactionId?: string;
  description?: string;
  createdAt?: string;
  paymentStatus: 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED' | 'CANCELLED';
  paymentMethod?: string;
  paidAt?: string;
  totalAmount: number;
}

// Request types
export interface CreateOrderRequest {
  cartItems: CartItemDTO[];
  deliveryInfo: DeliveryInformationDTO;
}

export interface CartItemDTO {
  productId: number;
  quantity: number;
  rushOrder?: boolean;
  instructions?: string;
  productTitle?: string;  // For display
  unitPrice?: number;     // For display
}

export interface CartItem {
  productId: number;
  quantity: number;
}

export interface CreateUserRequest {
  name: string;
  email: string;
  password: string;
  phone?: string;
  address?: string;
  role: string;
  salary?: number;
  isActive?: boolean;
}

export interface UpdateUserProfileRequest {
  name: string;
  phone?: string;
  address?: string;
}

// Search and filter types
export interface ProductSearchParams {
  title?: string;
  type?: string;
  minPrice?: number;
  maxPrice?: number;
  inStock?: boolean;
}

export interface ProductFilters {
  type: string;
  genre?: string;
  priceRange: [number, number];
  inStock: boolean;
}

// Shopping cart types
export interface CartState {
  items: CartItemWithProduct[];
  total: number;
  itemCount: number;
}

export interface CartItemWithProduct {
  product: Product;
  quantity: number;
}

// Authentication types
export interface AuthState {
  user: UserProfile | null;
  isAuthenticated: boolean;
  token: string | null;
}

export interface UserProfile {
  userId: number;
  email: string;
  name: string;
  isActive: boolean;
  phone?: string;
  address?: string;
  role: 'ADMIN' | 'CUSTOMER' | 'MANAGER' | 'EMPLOYEE';
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  user: UserProfile;
  //token: string;
}

// Payment type
export interface PaymentRequest {
  amount: string;           // Payment amount in VND (numeric string)
  bankCode?: string;        // Optional bank code
  language: 'vn' | 'en';    // Interface language
  vnp_Version: string;      // VNPAY API version
  vnp_ExpireDate?: string;  // Optional expiry date (yyyyMMddHHmmss)
}