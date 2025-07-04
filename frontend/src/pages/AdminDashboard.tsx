import React, { useState } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { 
  BarChart3, 
  Package, 
  ShoppingCart, 
  Users, 
  TrendingUp, 
  AlertTriangle,
  Plus,
  Eye,
  Edit,
  Trash2,
  Search,
  CheckCircle,
  X,
  Lock,
  Unlock
} from 'lucide-react';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { useAuthStore } from '../stores/authStore';
import { productApi, orderApi, userApi } from '../services/api';
import { ProductModal } from '../components/modals/ProductModal';
import { OrderModal } from '../components/modals/OrderModal';
import { UserModal } from '../components/modals/UserModal';
import { Product, Order, User } from '../types/api';

// Define form data types to match modal interfaces  
interface ProductFormData {
  title: string;
  price: number;
  quantity: number;
  barcode: string;
  type: 'book' | 'cd' | 'dvd' | 'lp';
  weight?: number;
  rushOrderSupported?: boolean;
  introduction?: string;
  // Type-specific fields
  bookData?: {
    genre?: string;
    pageCount?: number;
    publicationDate?: string;
    authors?: string;
    publishers?: string;
    coverType?: string;
  };
  cdData?: {
    trackList?: string;
    genre?: string;
    recordLabel?: string;
    artists?: string;
    releaseDate?: string;
  };
  dvdData?: {
    releaseDate?: string;
    dvdType?: string;
    genre?: string;
    studio?: string;
    directors?: string;
    durationMinutes?: number;
    rating?: string;
  };
  lpData?: {
    artist?: string;
    recordLabel?: string;
    musicType?: string;
    releaseDate?: string;
    tracklist?: string;
    rpm?: number;
    sizeInches?: number;
    vinylCondition?: string;
    sleeveCondition?: string;
  };
}

interface UserFormData {
  name: string;
  email: string;
  phone?: string;
  role: 'USER' | 'ADMIN' | 'MANAGER';
  isActive: boolean;
  password?: string;
}

const AdminDashboard: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'overview' | 'products' | 'orders' | 'users'>('overview');
  const [searchQuery, setSearchQuery] = useState('');
  const [modalState, setModalState] = useState<{
    isOpen: boolean;
    mode: 'create' | 'edit' | 'view';
    product?: Product;
  }>({ isOpen: false, mode: 'create' });
  const [orderModalState, setOrderModalState] = useState<{
    isOpen: boolean;
    order?: Order;
  }>({ isOpen: false });
  const [userModalState, setUserModalState] = useState<{
    isOpen: boolean;
    mode: 'create' | 'edit' | 'view';
    user?: User;
  }>({ isOpen: false, mode: 'create' });
  const { user } = useAuthStore();
  const queryClient = useQueryClient();

  // Helper function to get product icon
  const getProductIcon = (type: string) => {
    if (type === 'book') return '📚';
    if (type === 'cd') return '💿';
    if (type === 'dvd') return '📀';
    if (type === 'lp') return '🎵';
    return '📦';
  };

  // Check if user is admin or manager
  const isAdmin = user?.role === 'ADMIN';
  const isManager = user?.role === 'MANAGER';
  const canAccessDashboard = isAdmin || isManager;

  console.log('Admin Dashboard Rendered', { user, isAdmin });

  // Add error handling and loading states
  const { 
    data: productsResponse, 
    error: productsError, 
    isLoading: productsLoading 
  } = useQuery({
    queryKey: ['products'],
    queryFn: () => productApi.getAll(),
    retry: 3,
    retryDelay: 1000,
  });

  const { 
    data: ordersResponse, 
    error: ordersError, 
    isLoading: ordersLoading 
  } = useQuery({
    queryKey: ['orders'],
    queryFn: () => orderApi.getAll(),
    retry: 3,
    retryDelay: 1000,
  });

  const { 
    data: usersResponse, 
    error: usersError, 
    isLoading: usersLoading 
  } = useQuery({
    queryKey: ['users'],
    queryFn: () => userApi.getAll(),
    enabled: isAdmin, // Only ADMIN can fetch users
    retry: 3,
    retryDelay: 1000,
  });

  const { 
    data: lowStockResponse, 
    error: lowStockError, 
    isLoading: lowStockLoading 
  } = useQuery({
    queryKey: ['low-stock'],
    queryFn: () => productApi.getLowStock(10),
    retry: 3,
    retryDelay: 1000,
  });

  // Safe data extraction with better error handling
  const extractedProductsData = productsResponse?.data ?? productsResponse;
  const products = Array.isArray(extractedProductsData) ? extractedProductsData : [];
  
  const extractedOrdersData = ordersResponse?.data ?? ordersResponse;
  const orders = Array.isArray(extractedOrdersData) ? extractedOrdersData : [];
  
  const extractedUsersData = usersResponse?.data ?? usersResponse;
  const users = Array.isArray(extractedUsersData) ? extractedUsersData : [];
  
  const extractedLowStockData = lowStockResponse?.data ?? lowStockResponse;
  const lowStockProducts = Array.isArray(extractedLowStockData) ? extractedLowStockData : [];

  // Show error state if any critical API fails
  if (productsError || ordersError || (canAccessDashboard && usersError) || lowStockError) {
    return (
      <div className="container mx-auto px-4 py-16 text-center">
        <h1 className="text-4xl font-bold mb-4 text-red-600">Error Loading Dashboard</h1>
        <p className="text-muted-foreground mb-4">
          Unable to load dashboard data. Please try refreshing the page.
        </p>
        <div className="text-sm text-left max-w-md mx-auto">
          {productsError && <p>Products: {productsError.message}</p>}
          {ordersError && <p>Orders: {ordersError.message}</p>}
          {usersError && <p>Users: {usersError.message}</p>}
        </div>
        <Button onClick={() => {
          queryClient.invalidateQueries({ queryKey: ['products'] });
          queryClient.invalidateQueries({ queryKey: ['orders'] });
          queryClient.invalidateQueries({ queryKey: ['users'] });
          queryClient.invalidateQueries({ queryKey: ['low-stock'] });
        }} className="mt-4">
          Retry Loading
        </Button>
      </div>
    );
  }

  // Show loading state
  if (productsLoading || ordersLoading || (canAccessDashboard && usersLoading) || lowStockLoading) {
    return (
      <div className="container mx-auto px-4 py-16 text-center">
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-primary mx-auto mb-4"></div>
        <h1 className="text-2xl font-bold mb-2">Loading Dashboard...</h1>
        <p className="text-muted-foreground">Please wait while we load your data.</p>
      </div>
    );
  }

  // Safe filtering with null checks
  const filteredProducts = products.filter(product =>
    (product?.title?.toLowerCase().includes(searchQuery.toLowerCase()) ?? false) ||
    (product?.type?.toLowerCase().includes(searchQuery.toLowerCase()) ?? false)
  );

  const filteredOrders = orders.filter(order =>
    (order?.orderId?.toString().includes(searchQuery) ?? false) ||
    (order?.status?.toLowerCase().includes(searchQuery.toLowerCase()) ?? false)
  );

  const filteredUsers = users.filter(user =>
    (user?.name?.toLowerCase().includes(searchQuery.toLowerCase()) ?? false) ||
    (user?.email?.toLowerCase().includes(searchQuery.toLowerCase()) ?? false)
  );

  const handleConfirmOrder = async (orderId: number) => {
    try {
      await orderApi.confirm(orderId);
      // Invalidate and refetch orders data
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      queryClient.invalidateQueries({ queryKey: ['low-stock'] });
    } catch (error) {
      console.error('Failed to confirm order:', error);
      throw error;
    }
  };

  const handleCancelOrder = async (orderId: number) => {
    try {
      await orderApi.cancel(orderId);
      // Invalidate and refetch orders data
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      queryClient.invalidateQueries({ queryKey: ['low-stock'] });
    } catch (error) {
      console.error('Failed to cancel order:', error);
      throw error;
    }
  };

  const handleCreateUser = async (userData: any) => {
    try {
      // Format data to match CreateUserRequest interface
      const createUserData = {
        name: userData.name,
        email: userData.email,
        password: userData.password,
        phone: userData.phone || undefined,
        role: userData.role, // Should be 'ADMIN' or 'MANAGER' string
        salary: userData.salary || 0.0,
        isActive: userData.isActive !== undefined ? userData.isActive : true
      };
      
      await userApi.create(userData);
      console.log('User created successfully:', createUserData);
      // Invalidate and refetch users data
      queryClient.invalidateQueries({ queryKey: ['users'] });
    } catch (error) {
      console.error('Failed to create user:', error);
      throw error;
    }
  };

  const handleUpdateUser = async (userData: any) => {
    try {
      if (userModalState.user) {
        await userApi.update(userModalState.user.userId, userData);
        // Invalidate and refetch users data
        queryClient.invalidateQueries({ queryKey: ['users'] });
      }
    } catch (error) {
      console.error('Failed to update user:', error);
      throw error;
    }
  };

  const handleDeleteUser = async (userId: number) => {
    try {
      await userApi.delete(userId);
      // Invalidate and refetch users data
      queryClient.invalidateQueries({ queryKey: ['users'] });
    } catch (error) {
      console.error('Failed to delete user:', error);
      throw error;
    }
  };

  const handleBlockUser = async (userId: number, reason?: string) => {
    try {
      await userApi.block(userId, reason, user?.name || 'Administrator');
      // Invalidate and refetch users data
      queryClient.invalidateQueries({ queryKey: ['users'] });
    } catch (error) {
      console.error('Failed to block user:', error);
      throw error;
    }
  };

  const handleUnblockUser = async (userId: number) => {
    try {
      await userApi.unblock(userId, user?.name || 'Administrator');
      // Invalidate and refetch users data
      queryClient.invalidateQueries({ queryKey: ['users'] });
    } catch (error) {
      console.error('Failed to unblock user:', error);
      throw error;
    }
  };

  const handleUpdatePassword = async (userId: number, newPassword: string) => {
    try {
      await userApi.updatePassword(userId, newPassword);
      // Invalidate and refetch users data
      queryClient.invalidateQueries({ queryKey: ['users'] });
    } catch (error) {
      console.error('Failed to update password:', error);
      throw error;
    }
  };

  // Calculate stats with safe operations
  const totalRevenue = orders.reduce((sum, order) => {
    const amount = order?.totalAmount ?? 0;
    return sum + (typeof amount === 'number' ? amount : 0);
  }, 0);
  
  const pendingOrders = orders.filter(order => order?.status === 'PENDING').length;
  const outOfStockProducts = products.filter(product => (product?.quantity ?? 0) === 0).length;

  if (!canAccessDashboard) {
    return (
      <div className="container mx-auto px-4 py-16 text-center">
        <h1 className="text-4xl font-bold mb-4">Access Denied</h1>
        <p className="text-muted-foreground">You don't have permission to access the admin dashboard.</p>
      </div>
    );
  }

  const getOrderStatusColor = (status: string) => {
    if (!status) return 'text-gray-600 bg-gray-100';
    
    switch (status.toLowerCase()) {
      case 'completed':
        return 'text-green-600 bg-green-100';
      case 'pending':
        return 'text-yellow-600 bg-yellow-100';
      case 'cancelled':
        return 'text-red-600 bg-red-100';
      case 'confirmed':
        return 'text-blue-600 bg-blue-100';
      default:
        return 'text-gray-600 bg-gray-100';
    }
  };

  // Safe date formatting
  const formatDate = (dateString: string | null | undefined) => {
    if (!dateString) return 'N/A';
    try {
      return new Date(dateString).toLocaleDateString();
    } catch {
      return 'Invalid Date';
    }
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-7xl mx-auto">
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-3xl font-bold mb-2">Admin Dashboard</h1>
            <p className="text-muted-foreground">
              Manage your store's products, orders, and customers
            </p>
          </div>
        </div>

        {/* Navigation Tabs */}
        <div className="flex space-x-1 mb-8 border-b">
          {[
            { key: 'overview', label: 'Overview', icon: BarChart3 },
            { key: 'products', label: 'Products', icon: Package },
            { key: 'orders', label: 'Orders', icon: ShoppingCart },
            { key: 'users', label: 'Users', icon: Users },
          ].map((tab) => {
            const Icon = tab.icon;
            return (
              <button
                key={tab.key}
                onClick={() => setActiveTab(tab.key as any)}
                className={`flex items-center gap-2 px-4 py-2 rounded-t-lg transition-colors ${
                  activeTab === tab.key
                    ? 'bg-primary text-primary-foreground'
                    : 'text-muted-foreground hover:text-foreground'
                }`}
              >
                <Icon className="w-4 h-4" />
                {tab.label}
              </button>
            );
          })}
        </div>

        {/* Overview Tab */}
        {activeTab === 'overview' && (
          <div className="space-y-6">
            {/* Stats Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
              <Card>
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                  <CardTitle className="text-sm font-medium">Total Revenue</CardTitle>
                  <TrendingUp className="h-4 w-4 text-muted-foreground" />
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">{totalRevenue.toLocaleString()} VND</div>
                  <p className="text-xs text-muted-foreground">
                    From {orders.length} orders
                  </p>
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                  <CardTitle className="text-sm font-medium">Total Products</CardTitle>
                  <Package className="h-4 w-4 text-muted-foreground" />
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">{products.length}</div>
                  <p className="text-xs text-muted-foreground">
                    {outOfStockProducts} out of stock
                  </p>
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                  <CardTitle className="text-sm font-medium">Pending Orders</CardTitle>
                  <ShoppingCart className="h-4 w-4 text-muted-foreground" />
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">{pendingOrders}</div>
                  <p className="text-xs text-muted-foreground">
                    Require attention
                  </p>
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                  <CardTitle className="text-sm font-medium">Total Users</CardTitle>
                  <Users className="h-4 w-4 text-muted-foreground" />
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">{users.length}</div>
                  <p className="text-xs text-muted-foreground">
                    Registered customers
                  </p>
                </CardContent>
              </Card>
            </div>

            {/* Low Stock Alert */}
            {lowStockProducts.length > 0 && (
              <Card className="border-orange-200">
                <CardHeader>
                  <CardTitle className="flex items-center gap-2 text-orange-600">
                    <AlertTriangle className="w-5 h-5" />
                    Low Stock Alert
                  </CardTitle>
                  <CardDescription>
                    {lowStockProducts.length} products are running low on stock
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-2">                  {lowStockProducts.slice(0, 5).map((product) => (
                    <div key={product?.productId ?? Math.random()} className="flex justify-between items-center">
                      <span className="font-medium">{product?.title ?? 'Unknown Product'}</span>
                      <span className="text-sm text-orange-600">
                        {product?.quantity ?? 0} remaining
                      </span>
                    </div>
                  ))}
                  </div>
                </CardContent>
              </Card>
            )}

            {/* Recent Orders */}
            <Card>
              <CardHeader>
                <CardTitle>Recent Orders</CardTitle>
                <CardDescription>Latest orders that need attention</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {orders.slice(0, 5).map((order) => (
                    <div key={order?.orderId ?? Math.random()} className="flex items-center justify-between">
                      <div>
                        <p className="font-medium">Order #{order?.orderId ?? 'N/A'}</p>
                        <p className="text-sm text-muted-foreground">
                          {formatDate(order?.createdAt)}
                        </p>
                      </div>
                      <div className="flex items-center gap-2">
                        <span className={`px-2 py-1 text-xs font-medium rounded-full ${getOrderStatusColor(order?.status)}`}>
                          {order?.status ?? 'Unknown'}
                        </span>
                        <span className="font-medium">
                          {(order?.totalAmount ?? 0).toLocaleString()} VND
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </div>
        )}

        {/* Products Tab */}
        {activeTab === 'products' && (
          <div className="space-y-6">
            <div className="flex items-center gap-4">
              <div className="relative flex-1 max-w-md">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
                <Input
                  placeholder="Search products..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10"
                />
              </div>
              <Button onClick={() => setModalState({ isOpen: true, mode: 'create' })}>
                <Plus className="w-4 h-4 mr-2" />
                Add Product
              </Button>
            </div>

            <Card>
              <CardHeader>
                <CardTitle>Products ({filteredProducts.length})</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {filteredProducts.slice(0, 10).map((product) => (
                    <div key={product?.productId ?? Math.random()} className="flex items-center justify-between p-4 border rounded-lg">
                      <div className="flex items-center gap-4">
                        <div className="w-12 h-12 bg-gray-100 rounded flex items-center justify-center">
                          {getProductIcon(product?.type ?? 'unknown')}
                        </div>
                        <div>
                          <p className="font-medium">{product?.title ?? 'Unknown Product'}</p>
                          <p className="text-sm text-muted-foreground capitalize">
                            {product?.type ?? 'unknown'} • {(product?.price ?? 0).toLocaleString()} VND
                          </p>
                        </div>
                      </div>
                      <div className="flex items-center gap-2">
                        <span className={`px-2 py-1 text-xs rounded ${
                          (product?.quantity ?? 0) > 0 ? 'bg-green-100 text-green-600' : 'bg-red-100 text-red-600'
                        }`}>
                          {(product?.quantity ?? 0) > 0 ? `${product?.quantity} in stock` : 'Out of stock'}
                        </span>
                        <Button 
                          variant="outline" 
                          size="sm"
                          onClick={() => setModalState({ isOpen: true, mode: 'view', product })}
                        >
                          <Eye className="w-4 h-4 mr-1" />
                          View
                        </Button>
                        <Button 
                          variant="outline" 
                          size="sm"
                          onClick={() => {
                            console.log('Edit button clicked:', { product, user, isAdmin, isManager });
                            setModalState({ isOpen: true, mode: 'edit', product });
                          }}
                        >
                          <Edit className="w-4 h-4 mr-1" />
                          Edit
                        </Button>
                        <Button 
                          variant="outline" 
                          size="sm" 
                          className="text-red-600"
                          onClick={async () => {
                            if (window.confirm('Are you sure you want to delete this product?')) {
                              try {
                                await productApi.delete(product.productId);
                                // Invalidate and refetch products data
                                queryClient.invalidateQueries({ queryKey: ['products'] });
                                queryClient.invalidateQueries({ queryKey: ['low-stock'] });
                              } catch (error) {
                                console.error('Error deleting product:', error);
                                alert('Failed to delete product. Please try again.');
                              }
                            }
                          }}
                        >
                          <Trash2 className="w-4 h-4 mr-1" />
                          Delete
                        </Button>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </div>
        )}

        {/* Orders Tab */}
        {activeTab === 'orders' && (
          <div className="space-y-6">
            <div className="relative max-w-md">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
              <Input
                placeholder="Search orders..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>

            <Card>
              <CardHeader>
                <CardTitle>Orders ({filteredOrders.length})</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {filteredOrders.slice(0, 10).map((order) => (
                    <div key={order?.orderId ?? Math.random()} className="flex items-center justify-between p-4 border rounded-lg">
                      <div>
                        <p className="font-medium">Order #{order?.orderId ?? 'N/A'}</p>
                        <p className="text-sm text-muted-foreground">
                          {formatDate(order?.createdAt)} • {order?.orderLines?.length ?? 0} items
                        </p>
                      </div>
                      <div className="flex items-center gap-2">
                        <span className={`px-2 py-1 text-xs font-medium rounded-full ${getOrderStatusColor(order?.status)}`}>
                          {order?.status ?? 'Unknown'}
                        </span>
                        <Button 
                          variant="outline" 
                          size="sm"
                          onClick={() => setOrderModalState({ isOpen: true, order })}
                        >
                          <Eye className="w-4 h-4 mr-1" />
                          View
                        </Button>
                        {order?.status?.toLowerCase() === 'pending' && (
                          <Button 
                            variant="outline" 
                            size="sm"
                            className="text-green-600 border-green-200 hover:bg-green-50"
                            onClick={async () => {
                              if (window.confirm('Are you sure you want to confirm this order?')) {
                                try {
                                  await handleConfirmOrder(order.orderId);
                                } catch (error) {
                                  console.error('Error confirming order:', error);
                                }
                              }
                            }}
                          >
                            <CheckCircle className="w-4 h-4 mr-1" />
                            Confirm
                          </Button>
                        )}
                        {['pending', 'confirmed'].includes(order?.status?.toLowerCase() ?? '') && (
                          <Button 
                            variant="outline" 
                            size="sm"
                            className="text-red-600 border-red-200 hover:bg-red-50"
                            onClick={async () => {
                              if (window.confirm('Are you sure you want to cancel this order? This action cannot be undone.')) {
                                try {
                                  await handleCancelOrder(order.orderId);
                                } catch (error) {
                                  console.error('Error cancelling order:', error);
                                }
                              }
                            }}
                          >
                            <X className="w-4 h-4 mr-1" />
                            Cancel
                          </Button>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </div>
        )}

        {/* Users Tab */}
        {activeTab === 'users' && (
          isAdmin ? (
          <div className="space-y-6">
            <div className="flex items-center gap-4">
              <div className="relative flex-1 max-w-md">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
                <Input
                  placeholder="Search users..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10"
                />
              </div>
              <Button onClick={() => setUserModalState({ isOpen: true, mode: 'create' })}>
                <Plus className="w-4 h-4 mr-2" />
                Add User
              </Button>
            </div>

            <Card>
              <CardHeader>
                <CardTitle>Users ({filteredUsers.length})</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {filteredUsers.slice(0, 10).map((user) => (
                    <div key={user?.userId ?? Math.random()} className="flex items-center justify-between p-4 border rounded-lg">
                      <div>
                        <p className="font-medium">{user?.name ?? 'Unknown User'}</p>
                        <p className="text-sm text-muted-foreground">{user?.email ?? 'No email'}</p>
                      </div>
                      <div className="flex items-center gap-2">
                        <span className={`px-2 py-1 text-xs font-medium rounded-full capitalize ${
                          user?.isActive ? 'bg-green-100 text-green-600' : 'bg-red-100 text-red-600'
                        }`}>
                          {user?.isActive ? 'Active' : 'Inactive'}
                        </span>
                        <span className="px-2 py-1 bg-blue-100 text-blue-600 text-xs font-medium rounded-full capitalize">
                          {user?.role?.toLowerCase() ?? 'user'}
                        </span>
                        <Button 
                          variant="outline" 
                          size="sm"
                          onClick={() => setUserModalState({ isOpen: true, mode: 'view', user })}
                        >
                          <Eye className="w-4 h-4 mr-1" />
                          View
                        </Button>
                        <Button 
                          variant="outline" 
                          size="sm"
                          onClick={() => setUserModalState({ isOpen: true, mode: 'edit', user })}
                        >
                          <Edit className="w-4 h-4 mr-1" />
                          Edit
                        </Button>
                        {user?.isActive ? (
                          <Button 
                            variant="outline" 
                            size="sm"
                            className="text-orange-600 border-orange-200 hover:bg-orange-50"
                            onClick={async () => {
                              const reason = prompt('Please enter a reason for blocking this user:');
                              if (reason !== null) {
                                try {
                                  await handleBlockUser(user.userId, reason);
                                } catch (error) {
                                  console.error('Error blocking user:', error);
                                }
                              }
                            }}
                          >
                            <Lock className="w-4 h-4 mr-1" />
                            Block
                          </Button>
                        ) : (
                          <Button 
                            variant="outline" 
                            size="sm"
                            className="text-green-600 border-green-200 hover:bg-green-50"
                            onClick={async () => {
                              if (window.confirm('Are you sure you want to unblock this user?')) {
                                try {
                                  await handleUnblockUser(user.userId);
                                } catch (error) {
                                  console.error('Error unblocking user:', error);
                                }
                              }
                            }}
                          >
                            <Unlock className="w-4 h-4 mr-1" />
                            Unblock
                          </Button>
                        )}
                        <Button 
                          variant="outline" 
                          size="sm" 
                          className="text-red-600"
                          onClick={async () => {
                            if (window.confirm('Are you sure you want to delete this user? This action cannot be undone.')) {
                              try {
                                await handleDeleteUser(user.userId);
                              } catch (error) {
                                console.error('Error deleting user:', error);
                              }
                            }
                          }}
                        >
                          <Trash2 className="w-4 h-4 mr-1" />
                          Delete
                        </Button>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </div>
          ) : (
            <div className="container mx-auto px-4 py-16 text-center">
              <h1 className="text-4xl font-bold mb-4">No Authorization</h1>
              <p className="text-muted-foreground">You don't have permission to view user management.</p>
            </div>
          )
        )}
      </div>
      
      <ProductModal
        key={`${modalState.mode}-${modalState.product?.productId || 'new'}`}
        isOpen={modalState.isOpen}
        mode={modalState.mode}
        initialData={modalState.product ? {
          title: modalState.product.title,
          price: modalState.product.price,
          quantity: modalState.product.quantity,
          barcode: modalState.product.barcode || '',
          type: (modalState.product.type as 'book' | 'cd' | 'dvd' | 'lp') || 'book',
          weight: modalState.product.weight,
          rushOrderSupported: modalState.product.rushOrderSupported,
          introduction: modalState.product.introduction,
          bookData: modalState.product.type === 'book' ? {
            genre: (modalState.product as any).genre,
            pageCount: (modalState.product as any).pageCount,
            publicationDate: (modalState.product as any).publicationDate,
            authors: (modalState.product as any).authors,
            publishers: (modalState.product as any).publishers,
            coverType: (modalState.product as any).coverType
          } : {},
          cdData: modalState.product.type === 'cd' ? {
            trackList: (modalState.product as any).trackList,
            genre: (modalState.product as any).genre,
            recordLabel: (modalState.product as any).recordLabel,
            artists: (modalState.product as any).artists,
            releaseDate: (modalState.product as any).releaseDate
          } : {},
          dvdData: modalState.product.type === 'dvd' ? {
            releaseDate: (modalState.product as any).releaseDate,
            dvdType: (modalState.product as any).dvdType,
            genre: (modalState.product as any).genre,
            studio: (modalState.product as any).studio,
            directors: (modalState.product as any).directors,
            durationMinutes: (modalState.product as any).durationMinutes,
            rating: (modalState.product as any).rating
          } : {},
          lpData: modalState.product.type === 'lp' ? {
            artist: (modalState.product as any).artist,
            recordLabel: (modalState.product as any).recordLabel,
            musicType: (modalState.product as any).musicType,
            releaseDate: (modalState.product as any).releaseDate,
            tracklist: (modalState.product as any).tracklist,
            rpm: (modalState.product as any).rpm,
            sizeInches: (modalState.product as any).sizeInches,
            vinylCondition: (modalState.product as any).vinylCondition,
            sleeveCondition: (modalState.product as any).sleeveCondition
          } : {}
        } : undefined}
        onClose={() => setModalState({ isOpen: false, mode: 'create' })}
        onSubmit={async (data) => {
          console.log('Form data received:', data);
          
          // Create wrapped data structure for backend
          const submitData = {
            productData: {
              title: data.title,
              price: data.price,
              quantity: data.quantity,
              barcode: data.barcode,
              type: data.type,
              weight: data.weight || 0,
              rushOrderSupported: data.rushOrderSupported || false,
              introduction: data.introduction || ''
            },
            // Add type-specific data based on product type (only include non-empty data)
            ...(data.type === 'book' && data.bookData && Object.keys(data.bookData).length > 0 ? { bookData: data.bookData } : {}),
            ...(data.type === 'cd' && data.cdData && Object.keys(data.cdData).length > 0 ? { cdData: data.cdData } : {}),
            ...(data.type === 'dvd' && data.dvdData && Object.keys(data.dvdData).length > 0 ? { dvdData: data.dvdData } : {}),
            ...(data.type === 'lp' && data.lpData && Object.keys(data.lpData).length > 0 ? { lpData: data.lpData } : {})
          };
          
          console.log('Submitting data:', submitData);

          try {

            if (modalState.mode === 'create') {
              await productApi.create(submitData);
            } else if (modalState.mode === 'edit' && modalState.product) {
              await productApi.update(modalState.product.productId, submitData);
            }
            // Invalidate and refetch products data
            queryClient.invalidateQueries({ queryKey: ['products'] });
            queryClient.invalidateQueries({ queryKey: ['low-stock'] });
            // Close modal
            setModalState({ isOpen: false, mode: 'create' });
          } catch (error: any) {
            console.error('Failed to save product:', error);
            console.error('Request data:', submitData);
            if (error?.response) {
              console.error('Response data:', error.response.data);
              console.error('Response status:', error.response.status);
              console.error('Response headers:', error.response.headers);
            }
            
            // Show more detailed error message
            const errorMessage = error?.response?.data?.message || error?.response?.data?.error || error?.message || 'Unknown error';
            throw error;
          }
        }}
      />
      
      <OrderModal
        isOpen={orderModalState.isOpen}
        order={orderModalState.order ? {
          ...orderModalState.order,
          createdAt: orderModalState.order.createdAt || '',
          orderLines: undefined,
          orderItems: orderModalState.order.orderLines?.map(line => ({
            orderItemId: line.orderLineId,
            productId: line.product?.productId || 0,
            title: line.product?.title || 'Unknown Product',
            price: line.product?.price || 0,
            quantity: line.quantity,
            type: line.product?.type || 'unknown',
            totalFee: line.totalFee,
            rushOrder: line.rushOrder,
            status: line.status,
            deliveryTime: line.deliveryTime,
            instructions: line.instructions,
            product: line.product
          }))
        } : null}
        onClose={() => setOrderModalState({ isOpen: false })}
        onConfirmOrder={handleConfirmOrder}
        onCancelOrder={handleCancelOrder}
      />
      
      <UserModal
        isOpen={userModalState.isOpen}
        mode={userModalState.mode}
        initialData={userModalState.user ? {
          userId: userModalState.user.userId,
          name: userModalState.user.name,
          email: userModalState.user.email,
          phone: userModalState.user.phone,
          role: userModalState.user.role as 'ADMIN' | 'MANAGER',
          isActive: userModalState.user.isActive,
          createdAt: userModalState.user.createdAt,
          updatedAt: userModalState.user.updatedAt
        } : undefined}
        onClose={() => setUserModalState({ isOpen: false, mode: 'create' })}
        onSubmit={async (data) => {
          try {
            if (userModalState.mode === 'create') {
              await handleCreateUser(data);
            } else if (userModalState.mode === 'edit') {
              await handleUpdateUser(data);
            }
            // Close modal after successful operation
            setUserModalState({ isOpen: false, mode: 'create' });
          } catch (error) {
            console.error('Failed to save user:', error);
            throw error;
          }
        }}
        onPasswordUpdate={handleUpdatePassword}
        onBlockUser={handleBlockUser}
        onUnblockUser={handleUnblockUser}
      />
    </div>
  );
};

export default AdminDashboard;