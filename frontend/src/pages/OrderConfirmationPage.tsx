import React, { useEffect } from 'react';
import { useLocation, useNavigate, Link } from 'react-router-dom';
import { CheckCircle, Truck, CreditCard, Mail, Calendar, XCircle } from 'lucide-react';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { useCartStore } from '@/stores/cartStore';

const OrderConfirmationPage: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { clearCart } = useCartStore();

  // Parse query params from URL
  const params = new URLSearchParams(location.search);
  const status = params.get('status');
  const orderId = params.get('orderId');
  const amount = params.get('amount');
  const orderInfo = params.get('orderInfo');
  const payDate = params.get('payDate');

  // Estimated delivery (example logic)
  const estimatedDelivery = new Date();
  estimatedDelivery.setDate(estimatedDelivery.getDate() + 3);


  useEffect(() => {
    if (status === 'success') {
      clearCart();
    }
  }, [status, clearCart]);


  if (!status) {
    return (
      <div className="container mx-auto px-4 py-16 text-center">
        <h1 className="text-4xl font-bold mb-4">Invalid Order</h1>
        <p className="text-muted-foreground mb-8">No order information found.</p>
        <Button onClick={() => navigate('/')}>
          Go Home
        </Button>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-2xl mx-auto">
        {/* Success or Failure Header */}
        <div className="text-center mb-8">
          {status === 'success' ? (
            <>
              <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <CheckCircle className="w-8 h-8 text-green-600" />
              </div>
              <h1 className="text-3xl font-bold text-green-600 mb-2">Order Confirmed!</h1>
              <p className="text-muted-foreground">
                Thank you for your purchase. Your order has been successfully placed.
              </p>
            </>
          ) : (
            <>
              <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <XCircle className="w-8 h-8 text-red-600" />
              </div>
              <h1 className="text-3xl font-bold text-red-600 mb-2">Payment Failed</h1>
              <p className="text-muted-foreground">
                Unfortunately, your payment was not successful. Please try again or contact support.
              </p>
            </>
          )}
        </div>

        {/* Order Details */}
        <Card className="mb-6">
          <CardHeader>
            <CardTitle>Order Details</CardTitle>
            <CardDescription>Order #{orderId || 'N/A'}</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="flex items-center space-x-3">
                <div className="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                  <Calendar className="w-4 h-4 text-blue-600" />
                </div>
                <div>
                  <p className="font-medium">Order Date</p>
                  <p className="text-sm text-muted-foreground">
                    {payDate
                      ? new Date(
                        payDate.slice(0, 4) +
                        '-' +
                        payDate.slice(4, 6) +
                        '-' +
                        payDate.slice(6, 8) +
                        'T' +
                        payDate.slice(8, 10) +
                        ':' +
                        payDate.slice(10, 12) +
                        ':' +
                        payDate.slice(12, 14)
                      ).toLocaleString('en-US', {
                        weekday: 'long',
                        year: 'numeric',
                        month: 'long',
                        day: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit',
                      })
                      : new Date().toLocaleDateString('en-US', {
                        weekday: 'long',
                        year: 'numeric',
                        month: 'long',
                        day: 'numeric',
                      })}
                  </p>
                </div>
              </div>

              <div className="flex items-center space-x-3">
                <div className="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center">
                  <CreditCard className="w-4 h-4 text-green-600" />
                </div>
                <div>
                  <p className="font-medium">Payment Method</p>
                  <p className="text-sm text-muted-foreground">
                    VNPay
                  </p>
                </div>
              </div>

              <div className="flex items-center space-x-3">
                <div className="w-8 h-8 bg-orange-100 rounded-full flex items-center justify-center">
                  <Truck className="w-4 h-4 text-orange-600" />
                </div>
                <div>
                  <p className="font-medium">Estimated Delivery</p>
                  <p className="text-sm text-muted-foreground">
                    {estimatedDelivery.toLocaleDateString('en-US', {
                      weekday: 'long',
                      month: 'long',
                      day: 'numeric'
                    })}
                  </p>
                </div>
              </div>

              <div className="flex items-center space-x-3">
                <div className="w-8 h-8 bg-purple-100 rounded-full flex items-center justify-center">
                  <span className="text-purple-600 font-bold text-sm">VND</span>
                </div>
                <div>
                  <p className="font-medium">Total Amount</p>
                  <p className="text-sm text-muted-foreground">
                    {amount ? Number(amount).toLocaleString() : 'N/A'} VND
                  </p>
                </div>
              </div>
            </div>
            {orderInfo && (
              <div className="mt-4">
                <p className="font-medium">Order Info</p>
                <p className="text-sm text-muted-foreground">{orderInfo}</p>
              </div>
            )}
          </CardContent>
        </Card>

        {/* Actions */}
        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          <Link to="/profile/orders">
            <Button variant="outline" className="w-full sm:w-auto">
              <Mail className="w-4 h-4 mr-2" />
              View Order History
            </Button>
          </Link>

          <Button onClick={() => navigate('/')} className="w-full sm:w-auto">
            Continue Shopping
          </Button>
        </div>

        {/* Support Info */}
        <div className="mt-8 p-4 bg-muted rounded-lg text-center">
          <p className="text-sm text-muted-foreground">
            Need help with your order? Contact our customer support at{' '}
            <a href="mailto:support@itssstore.com" className="text-primary hover:underline">
              support@itssstore.com
            </a>{' '}
            or call us at{' '}
            <a href="tel:+84123456789" className="text-primary hover:underline">
              +84 123 456 789
            </a>
          </p>
        </div>
      </div>
    </div>
  );
};

export default OrderConfirmationPage;