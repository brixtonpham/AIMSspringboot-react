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
}

export interface Book extends Product {
  bookId?: number;
  genre?: string;
  pageCount?: number;
  publicationDate?: string;
  authors?: string;
  publishers?: string;
  coverType?: string;
}

export interface CD extends Product {
  cdId?: number;
  trackList?: string;
  genre?: string;
  recordLabel?: string;
  artists?: string;
  releaseDate?: string;
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
  cartItems: CartItem[];
  deliveryInfo: Omit<DeliveryInformation, 'deliveryId'>;
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
  role?: string;
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
  id: string;
  email: string;
  name: string;
  role: 'ADMIN' | 'CUSTOMER' | 'MANAGER' | 'EMPLOYEE';
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  user: UserProfile;
  token: string;
}