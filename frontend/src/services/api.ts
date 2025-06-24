import axios from 'axios';
import { 
  Product, 
  Book, 
  CD, 
  DVD, 
  User, 
  Order, 
  Invoice,
  ApiResponse, 
  ProductSearchParams,
  CreateOrderRequest,
  CreateUserRequest,
  UpdateUserProfileRequest,
  LoginRequest,
  LoginResponse
} from '../types/api';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    console.error('API Error:', error);
    return Promise.reject(error);
  }
);

// Product API
export const productApi = {
  // Get all products
  getAll: (): Promise<ApiResponse<Product[]>> => 
    api.get('/products'),

  // Get product by ID
  getById: (id: number): Promise<ApiResponse<Product>> => 
    api.get(`/products/${id}`),

  // Get product by barcode
  getByBarcode: (barcode: string): Promise<ApiResponse<Product>> => 
    api.get(`/products/barcode/${barcode}`),

  // Get products by type
  getByType: (type: string): Promise<ApiResponse<Product[]>> => 
    api.get(`/products/type/${type}`),

  // Search products
  search: (params: ProductSearchParams): Promise<ApiResponse<Product[]>> => 
    api.get('/products/search', { params }),

  // Create product
  create: (product: Partial<Product>): Promise<ApiResponse<Product>> => 
    api.post('/products', product),

  // Update product
  update: (id: number, product: Partial<Product>): Promise<ApiResponse<Product>> => 
    api.put(`/products/${id}`, product),

  // Delete product
  delete: (id: number): Promise<ApiResponse<void>> => 
    api.delete(`/products/${id}`),

  // Update stock
  updateStock: (id: number, quantity: number): Promise<ApiResponse<Product>> => 
    api.patch(`/products/${id}/stock`, null, { params: { quantity } }),

  // Check availability
  checkAvailability: (id: number, quantity: number): Promise<ApiResponse<boolean>> => 
    api.get(`/products/${id}/availability`, { params: { quantity } }),

  // Get low stock products
  getLowStock: (threshold: number = 10): Promise<ApiResponse<Product[]>> => 
    api.get('/products/low-stock', { params: { threshold } }),

  // Get all books
  getAllBooks: (): Promise<ApiResponse<Book[]>> => 
    api.get('/products/books'),

  // Get all CDs
  getAllCDs: (): Promise<ApiResponse<CD[]>> => 
    api.get('/products/cds'),

  // Get all DVDs
  getAllDVDs: (): Promise<ApiResponse<DVD[]>> => 
    api.get('/products/dvds'),

  // Create book
  createBook: (book: Partial<Book>): Promise<ApiResponse<Book>> => 
    api.post('/products/books', book),

  // Create CD
  createCD: (cd: Partial<CD>): Promise<ApiResponse<CD>> => 
    api.post('/products/cds', cd),

  // Create DVD
  createDVD: (dvd: Partial<DVD>): Promise<ApiResponse<DVD>> => 
    api.post('/products/dvds', dvd),
};

// Order API
export const orderApi = {
  // Get all orders
  getAll: (): Promise<ApiResponse<Order[]>> => 
    api.get('/orders'),

  // Get order by ID
  getById: (id: number): Promise<ApiResponse<Order>> => 
    api.get(`/orders/${id}`),

  // Get orders by status
  getByStatus: (status: string): Promise<ApiResponse<Order[]>> => 
    api.get(`/orders/status/${status}`),

  // Get orders by customer email
  getByCustomerEmail: (email: string): Promise<ApiResponse<Order[]>> => 
    api.get(`/orders/customer/${email}`),

  // Create order
  create: (orderData: CreateOrderRequest): Promise<ApiResponse<Order>> => 
    api.post('/orders', orderData),

  // Confirm order
  confirm: (id: number): Promise<ApiResponse<Order>> => 
    api.patch(`/orders/${id}/confirm`),

  // Cancel order
  cancel: (id: number, reason?: string): Promise<ApiResponse<Order>> => 
    api.patch(`/orders/${id}/cancel`, null, { params: { reason } }),

  // Update order status
  updateStatus: (id: number, status: string): Promise<ApiResponse<Order>> => 
    api.patch(`/orders/${id}/status`, null, { params: { status } }),

  // Get pending orders
  getPending: (): Promise<ApiResponse<Order[]>> => 
    api.get('/orders/pending'),

  // Get recent orders
  getRecent: (days: number = 30): Promise<ApiResponse<Order[]>> => 
    api.get('/orders/recent', { params: { days } }),

  // Get order statistics
  getStatistics: (): Promise<ApiResponse<any[]>> => 
    api.get('/orders/statistics'),

  // Get total revenue
  getTotalRevenue: (startDate: string, endDate: string): Promise<ApiResponse<number>> => 
    api.get('/orders/revenue', { params: { startDate, endDate } }),
};

// User API
export const userApi = {
  // Get all users
  getAll: (): Promise<ApiResponse<User[]>> => 
    api.get('/users'),

  // Get user by ID
  getById: (id: number): Promise<ApiResponse<User>> => 
    api.get(`/users/${id}`),

  // Get user by email
  getByEmail: (email: string): Promise<ApiResponse<User>> => 
    api.get(`/users/email/${email}`),

  // Create user
  create: (userData: CreateUserRequest): Promise<ApiResponse<User>> => 
    api.post('/users', userData),

  // Update user
  update: (id: number, userData: Partial<User>): Promise<ApiResponse<User>> => 
    api.put(`/users/${id}`, userData),

  // Delete user
  delete: (id: number): Promise<ApiResponse<void>> => 
    api.delete(`/users/${id}`),

  // Search users
  search: (name?: string, email?: string, phone?: string): Promise<ApiResponse<User[]>> => 
    api.get('/users/search', { params: { name, email, phone } }),

  // Update user profile
  updateProfile: (id: number, profileData: UpdateUserProfileRequest): Promise<ApiResponse<User>> => 
    api.patch(`/users/${id}/profile`, profileData),

  // Update user email
  updateEmail: (id: number, email: string): Promise<ApiResponse<User>> => 
    api.patch(`/users/${id}/email`, null, { params: { email } }),

  // Get user count
  getCount: (): Promise<ApiResponse<number>> => 
    api.get('/users/count'),
};

// Invoice API
export const invoiceApi = {
  // Get all invoices
  getAll: (): Promise<ApiResponse<Invoice[]>> => 
    api.get('/invoices'),

  // Get invoice by ID
  getById: (id: number): Promise<ApiResponse<Invoice>> => 
    api.get(`/invoices/${id}`),

  // Get invoice by transaction ID
  getByTransactionId: (transactionId: string): Promise<ApiResponse<Invoice>> => 
    api.get(`/invoices/transaction/${transactionId}`),

  // Get invoice by order ID
  getByOrderId: (orderId: number): Promise<ApiResponse<Invoice>> => 
    api.get(`/invoices/order/${orderId}`),

  // Create invoice
  create: (orderId: number, paymentMethod: string, description?: string): Promise<ApiResponse<Invoice>> => 
    api.post('/invoices', { orderId, paymentMethod, description }),

  // Process payment
  processPayment: (id: number, transactionId: string, paymentMethod: string): Promise<ApiResponse<Invoice>> => 
    api.patch(`/invoices/${id}/payment`, { transactionId, paymentMethod }),

  // Get invoices by status
  getByStatus: (status: string): Promise<ApiResponse<Invoice[]>> => 
    api.get(`/invoices/status/${status}`),

  // Get pending invoices
  getPending: (): Promise<ApiResponse<Invoice[]>> => 
    api.get('/invoices/pending'),

  // Get paid invoices
  getPaid: (): Promise<ApiResponse<Invoice[]>> => 
    api.get('/invoices/paid'),

  // Get total revenue
  getTotalRevenue: (startDate: string, endDate: string): Promise<ApiResponse<number>> => 
    api.get('/invoices/revenue', { params: { startDate, endDate } }),

  // Refund invoice
  refund: (id: number, reason?: string): Promise<ApiResponse<Invoice>> => 
    api.patch(`/invoices/${id}/refund`, null, { params: { reason } }),
};

// Authentication API (mock for now since backend doesn't have auth yet)
export const authApi = {
  login: async (loginData: LoginRequest): Promise<LoginResponse> => {
    // Mock login - in real app this would authenticate with backend
    const mockUser: User = {
      userId: 1,
      name: 'John Doe',
      email: loginData.email,
      role: 'CUSTOMER',
      isActive: true,
    };
    
    const mockToken = 'mock-jwt-token';
    localStorage.setItem('authToken', mockToken);
    
    return {
      user: mockUser,
      token: mockToken,
    };
  },

  logout: async (): Promise<void> => {
    localStorage.removeItem('authToken');
  },

  getCurrentUser: async (): Promise<User | null> => {
    const token = localStorage.getItem('authToken');
    if (!token) return null;
    
    // Mock user data - in real app would validate token with backend
    return {
      userId: 1,
      name: 'John Doe',
      email: 'john@example.com',
      role: 'CUSTOMER',
      isActive: true,
    };
  },
};

export default api;