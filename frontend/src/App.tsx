import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { Header } from './components/Header';
import { HomePage } from './pages/HomePage';
import { ProductsPage } from './pages/ProductsPage';
import { CartPage } from './pages/CartPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import CheckoutPage from './pages/CheckoutPage';
import OrderConfirmationPage from './pages/OrderConfirmationPage';
import ProductDetailPage from './pages/ProductDetailPage';
import ProfilePage from './pages/ProfilePage';
import OrderHistoryPage from './pages/OrderHistoryPage';
import AdminDashboard from './pages/AdminDashboard';

// Create a client
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5, // 5 minutes
      gcTime: 1000 * 60 * 30, // 30 minutes
    },
  },
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <Router>
        <div className="min-h-screen bg-background">
          <Header />
          <main>
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/products" element={<ProductsPage />} />
              <Route path="/products/:id" element={<ProductDetailPage />} />
              <Route path="/cart" element={<CartPage />} />
              <Route path="/categories" element={<ProductsPage />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />
              <Route path="/checkout" element={<CheckoutPage />} />
              <Route path="/order-confirmation" element={<OrderConfirmationPage />} />
              <Route path="/profile" element={<ProfilePage />} />
              <Route path="/profile/orders" element={<OrderHistoryPage />} />
              <Route path="/admin" element={<AdminDashboard />} />
              {/* Additional routes will be added later */}
              <Route path="*" element={
                <div className="container mx-auto px-4 py-16 text-center">
                  <h1 className="text-4xl font-bold mb-4">404 - Page Not Found</h1>
                  <p className="text-muted-foreground">The page you're looking for doesn't exist.</p>
                </div>
              } />
            </Routes>
          </main>
          
          {/* Footer */}
          <footer className="bg-muted mt-16">
            <div className="container mx-auto px-4 py-8">
              <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
                <div>
                  <h3 className="font-semibold mb-4">ITSS Store</h3>
                  <p className="text-sm text-muted-foreground">
                    Your one-stop shop for books, CDs, and DVDs.
                  </p>
                </div>
                <div>
                  <h4 className="font-medium mb-4">Categories</h4>
                  <ul className="space-y-2 text-sm">
                    <li><a href="/products?type=book" className="text-muted-foreground hover:text-foreground">Books</a></li>
                    <li><a href="/products?type=cd" className="text-muted-foreground hover:text-foreground">CDs</a></li>
                    <li><a href="/products?type=dvd" className="text-muted-foreground hover:text-foreground">DVDs</a></li>
                  </ul>
                </div>
                <div>
                  <h4 className="font-medium mb-4">Customer Service</h4>
                  <ul className="space-y-2 text-sm">
                    <li><a href="/contact" className="text-muted-foreground hover:text-foreground">Contact Us</a></li>
                    <li><a href="/shipping" className="text-muted-foreground hover:text-foreground">Shipping Info</a></li>
                    <li><a href="/returns" className="text-muted-foreground hover:text-foreground">Returns</a></li>
                  </ul>
                </div>
                <div>
                  <h4 className="font-medium mb-4">Connect</h4>
                  <ul className="space-y-2 text-sm">
                    <li><a href="/about" className="text-muted-foreground hover:text-foreground">About Us</a></li>
                    <li><a href="/news" className="text-muted-foreground hover:text-foreground">News</a></li>
                    <li><a href="/careers" className="text-muted-foreground hover:text-foreground">Careers</a></li>
                  </ul>
                </div>
              </div>
              <div className="border-t mt-8 pt-8 text-center text-sm text-muted-foreground">
                <p>&copy; 2024 ITSS Store. All rights reserved.</p>
              </div>
            </div>
          </footer>
        </div>
      </Router>
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  );
}

export default App;