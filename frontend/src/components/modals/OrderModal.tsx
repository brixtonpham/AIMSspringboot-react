import React, { useState } from 'react';
import { X, Package, Truck, CreditCard, MapPin, Clock, User, Phone, Mail, DollarSign, CheckCircle } from 'lucide-react';
import { Button } from '../ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';

interface OrderItem {
  orderItemId?: number;
  productId: number;
  title: string;
  price: number;
  quantity: number;
  type: string;
  totalFee?: number;
  rushOrder?: boolean;
  status?: string;
  deliveryTime?: string;
  instructions?: string;
  product?: {
    dimensions?: string;
    weight?: number;
    barcode?: string;
    imageUrl?: string;
    introduction?: string;
  };
}

interface DeliveryInfo {
  deliveryId?: number;
  name: string;
  phone: string;
  email?: string;
  province?: string;
  district?: string;
  ward?: string;
  address: string;
  deliveryMessage?: string;
  deliveryFee?: number;
}

interface Invoice {
  invoiceId?: number;
  transactionId?: string;
  paymentStatus?: string;
  paymentMethod?: string;
  paidAt?: string;
  totalAmount?: number;
}

interface Order {
  orderId: number;
  status: string;
  totalAmount?: number;
  totalBeforeVat?: number;
  totalAfterVat?: number;
  vatPercentage?: number;
  vatAmount?: number;
  createdAt?: string;
  updatedAt?: string;
  orderLines?: OrderItem[];
  orderItems?: OrderItem[];
  userId?: number;
  deliveryInfo?: DeliveryInfo;
  invoice?: Invoice;
  totalItemCount?: number;
  hasRushItems?: boolean;
}

interface OrderModalProps {
  isOpen: boolean;
  onClose: () => void;
  order: Order | null;
  onConfirmOrder?: (orderId: number) => Promise<void>;
  onCancelOrder?: (orderId: number) => Promise<void>;
}

export const OrderModal: React.FC<OrderModalProps> = ({
  isOpen,
  onClose,
  order,
  onConfirmOrder,
  onCancelOrder
}) => {
  const [isConfirming, setIsConfirming] = useState(false);
  const [isCancelling, setIsCancelling] = useState(false);
  if (!isOpen || !order) return null;

  const getOrderStatusColor = (status: string) => {
    switch (status?.toLowerCase()) {
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

  const formatDateTime = (dateString?: string) => {
    if (!dateString) return 'N/A';
    try {
      return new Date(dateString).toLocaleString('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch {
      return 'Invalid Date';
    }
  };

  const getPaymentStatusColor = (status?: string) => {
    if (!status) return 'text-gray-600 bg-gray-100';
    
    switch (status.toLowerCase()) {
      case 'paid':
        return 'text-green-600 bg-green-100';
      case 'pending':
        return 'text-yellow-600 bg-yellow-100';
      case 'failed':
        return 'text-red-600 bg-red-100';
      case 'refunded':
        return 'text-blue-600 bg-blue-100';
      case 'cancelled':
        return 'text-gray-600 bg-gray-100';
      default:
        return 'text-gray-600 bg-gray-100';
    }
  };

  const items = order.orderLines || order.orderItems || [];
  const totalAmount = order.totalAfterVat || order.totalAmount || 0;
  const totalBeforeVat = order.totalBeforeVat || 0;
  const vatAmount = order.vatAmount || (totalAmount - totalBeforeVat);
  const vatPercentage = order.vatPercentage || 10;

  const handleConfirmOrder = async () => {
    if (!onConfirmOrder) return;
    
    setIsConfirming(true);
    try {
      await onConfirmOrder(order.orderId);
      onClose();
    } catch (error) {
      console.error('Error confirming order:', error);
      alert('Failed to confirm order. Please try again.');
    } finally {
      setIsConfirming(false);
    }
  };

  const handleCancelOrder = async () => {
    if (!onCancelOrder) return;
    
    if (!window.confirm('Are you sure you want to cancel this order? This action cannot be undone.')) {
      return;
    }

    setIsCancelling(true);
    try {
      await onCancelOrder(order.orderId);
      onClose();
    } catch (error) {
      console.error('Error cancelling order:', error);
      alert('Failed to cancel order. Please try again.');
    } finally {
      setIsCancelling(false);
    }
  };

  const canConfirm = order.status?.toLowerCase() === 'pending';
  const canCancel = ['pending', 'confirmed'].includes(order.status?.toLowerCase() || '');

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <Card className="w-full max-w-2xl max-h-[90vh] flex flex-col">
        <CardHeader className="flex-shrink-0">
          <div className="flex items-center justify-between">
            <CardTitle>Order Details - #{order.orderId}</CardTitle>
            <Button
              variant="ghost"
              size="sm"
              onClick={onClose}
              className="h-6 w-6 p-0"
            >
              <X className="h-4 w-4" />
            </Button>
          </div>
        </CardHeader>
        <CardContent className="flex-1 overflow-y-auto space-y-6">
          {/* Order Status & Timeline */}
          <div className="bg-gradient-to-r from-blue-50 to-indigo-50 p-4 rounded-lg border">
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-2">
                <Package className="w-5 h-5 text-blue-600" />
                <h3 className="font-semibold text-blue-900">Order Status</h3>
              </div>
              <span className={`px-3 py-1 text-sm font-medium rounded-full ${getOrderStatusColor(order.status)}`}>
                {order.status}
              </span>
            </div>
            <div className="grid grid-cols-2 gap-4 text-sm">
              <div className="space-y-2">
                <div className="flex items-center gap-2">
                  <Clock className="w-4 h-4 text-gray-500" />
                  <span className="text-gray-600">Created:</span>
                  <span className="font-medium">{formatDateTime(order.createdAt)}</span>
                </div>
                {order.updatedAt && (
                  <div className="flex items-center gap-2">
                    <Clock className="w-4 h-4 text-gray-500" />
                    <span className="text-gray-600">Updated:</span>
                    <span className="font-medium">{formatDateTime(order.updatedAt)}</span>
                  </div>
                )}
              </div>
              <div className="space-y-2">
                <div className="flex items-center gap-2">
                  <Package className="w-4 h-4 text-gray-500" />
                  <span className="text-gray-600">Total Items:</span>
                  <span className="font-medium">{order.totalItemCount || items.length}</span>
                </div>
                {order.hasRushItems && (
                  <div className="flex items-center gap-2">
                    <Truck className="w-4 h-4 text-orange-500" />
                    <span className="text-orange-600 font-medium">Rush Delivery Items</span>
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Customer & Delivery Information */}
          {order.deliveryInfo && (
            <div className="bg-green-50 p-4 rounded-lg border">
              <div className="flex items-center gap-2 mb-3">
                <MapPin className="w-5 h-5 text-green-600" />
                <h3 className="font-semibold text-green-900">Delivery Information</h3>
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                <div className="space-y-2">
                  <div className="flex items-center gap-2">
                    <User className="w-4 h-4 text-gray-500" />
                    <span className="text-gray-600">Name:</span>
                    <span className="font-medium">{order.deliveryInfo.name || 'N/A'}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Phone className="w-4 h-4 text-gray-500" />
                    <span className="text-gray-600">Phone:</span>
                    <span className="font-medium">{order.deliveryInfo.phone || 'N/A'}</span>
                  </div>
                  {order.deliveryInfo.email && (
                    <div className="flex items-center gap-2">
                      <Mail className="w-4 h-4 text-gray-500" />
                      <span className="text-gray-600">Email:</span>
                      <span className="font-medium">{order.deliveryInfo.email}</span>
                    </div>
                  )}
                </div>
                <div className="space-y-2">
                  <div>
                    <span className="text-gray-600">Address:</span>
                    <div className="mt-1 p-2 bg-white rounded border text-sm">
                      {order.deliveryInfo.address}
                      {order.deliveryInfo.ward && `, ${order.deliveryInfo.ward}`}
                      {order.deliveryInfo.district && `, ${order.deliveryInfo.district}`}
                      {order.deliveryInfo.province && `, ${order.deliveryInfo.province}`}
                    </div>
                  </div>
                  {order.deliveryInfo.deliveryMessage && (
                    <div>
                      <span className="text-gray-600">Delivery Note:</span>
                      <div className="mt-1 p-2 bg-white rounded border text-sm italic">
                        "{order.deliveryInfo.deliveryMessage}"
                      </div>
                    </div>
                  )}
                  {order.deliveryInfo.deliveryFee !== undefined && (
                    <div className="flex items-center gap-2">
                      <Truck className="w-4 h-4 text-gray-500" />
                      <span className="text-gray-600">Delivery Fee:</span>
                      <span className="font-medium">
                        {order.deliveryInfo.deliveryFee === 0 ? 'FREE' : `${order.deliveryInfo.deliveryFee.toLocaleString()} VND`}
                      </span>
                    </div>
                  )}
                </div>
              </div>
            </div>
          )}

          {/* Payment Information */}
          {order.invoice && (
            <div className="bg-purple-50 p-4 rounded-lg border">
              <div className="flex items-center gap-2 mb-3">
                <CreditCard className="w-5 h-5 text-purple-600" />
                <h3 className="font-semibold text-purple-900">Payment Information</h3>
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                <div className="space-y-2">
                  <div className="flex items-center gap-2">
                    <span className="text-gray-600">Status:</span>
                    <span className={`px-2 py-1 text-xs font-medium rounded-full ${getPaymentStatusColor(order.invoice.paymentStatus)}`}>
                      {order.invoice.paymentStatus || 'Unknown'}
                    </span>
                  </div>
                  {order.invoice.paymentMethod && (
                    <div className="flex items-center gap-2">
                      <span className="text-gray-600">Method:</span>
                      <span className="font-medium">{order.invoice.paymentMethod}</span>
                    </div>
                  )}
                  {order.invoice.transactionId && (
                    <div className="flex items-center gap-2">
                      <span className="text-gray-600">Transaction ID:</span>
                      <span className="font-mono text-sm bg-white px-2 py-1 rounded">
                        {order.invoice.transactionId}
                      </span>
                    </div>
                  )}
                </div>
                <div className="space-y-2">
                  {order.invoice.paidAt && (
                    <div className="flex items-center gap-2">
                      <Clock className="w-4 h-4 text-gray-500" />
                      <span className="text-gray-600">Paid At:</span>
                      <span className="font-medium">{formatDateTime(order.invoice.paidAt)}</span>
                    </div>
                  )}
                  <div className="flex items-center gap-2">
                    <DollarSign className="w-4 h-4 text-gray-500" />
                    <span className="text-gray-600">Amount:</span>
                    <span className="font-medium">{(order.invoice.totalAmount || 0).toLocaleString()} VND</span>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Order Items */}
          <div>
            <h3 className="font-semibold mb-3 flex items-center gap-2">
              <Package className="w-5 h-5" />
              Order Details ({items.length} items)
            </h3>
            <div className="space-y-3">
              {items.length > 0 ? (
                items.map((item, index) => {
                  const itemTotal = item.totalFee || ((item.price || 0) * (item.quantity || 0));
                  return (
                    <div key={item.orderItemId || index} className="border rounded-lg p-4 hover:bg-gray-50 transition-colors">
                      <div className="flex items-start justify-between">
                        <div className="flex items-start gap-3 flex-1">
                          <div className="w-12 h-12 bg-gray-100 rounded-lg flex items-center justify-center flex-shrink-0">
                            {item.type === 'book' ? '📚' : 
                             item.type === 'cd' ? '💿' : 
                             item.type === 'dvd' ? '📀' : '🎵'}
                          </div>
                          <div className="flex-1 min-w-0">
                            <div className="flex items-start justify-between">
                              <div>
                                <h4 className="font-medium text-gray-900 mb-1">{item.title || 'Unknown Product'}</h4>
                                <div className="flex items-center gap-4 text-sm text-gray-500 mb-2">
                                  <span className="capitalize">{item.type}</span>
                                  <span>•</span>
                                  <span>{(item.price || 0).toLocaleString()} VND each</span>
                                  {item.product?.weight && (
                                    <>
                                      <span>•</span>
                                      <span>{item.product.weight}kg</span>
                                    </>
                                  )}
                                </div>
                                {item.product?.dimensions && (
                                  <p className="text-sm text-gray-500 mb-1">Dimensions: {item.product.dimensions}</p>
                                )}
                                {item.product?.barcode && (
                                  <p className="text-sm text-gray-500 font-mono">Barcode: {item.product.barcode}</p>
                                )}
                              </div>
                              <div className="text-right ml-4">
                                <div className="font-semibold text-lg">{itemTotal.toLocaleString()} VND</div>
                                <div className="text-sm text-gray-500">Qty: {item.quantity || 0}</div>
                              </div>
                            </div>
                            
                            <div className="flex items-center gap-4 mt-2">
                              {item.status && (
                                <span className={`px-2 py-1 text-xs font-medium rounded-full ${getOrderStatusColor(item.status)}`}>
                                  {item.status}
                                </span>
                              )}
                              {item.rushOrder && (
                                <span className="px-2 py-1 text-xs font-medium rounded-full bg-orange-100 text-orange-600">
                                  🚀 Rush Delivery
                                </span>
                              )}
                              {item.deliveryTime && (
                                <span className="text-sm text-gray-500">
                                  <Clock className="w-3 h-3 inline mr-1" />
                                  {item.deliveryTime}
                                </span>
                              )}
                            </div>
                            
                            {item.instructions && (
                              <div className="mt-2 p-2 bg-yellow-50 rounded text-sm italic border-l-2 border-yellow-200">
                                <strong>Special Instructions:</strong> {item.instructions}
                              </div>
                            )}
                          </div>
                        </div>
                      </div>
                    </div>
                  );
                })
              ) : (
                <div className="text-center py-8 text-gray-500">
                  <Package className="w-12 h-12 mx-auto mb-2 text-gray-300" />
                  <p>No items found in this order</p>
                </div>
              )}
            </div>
          </div>

          {/* Order Summary */}
          <div className="bg-gray-50 p-4 rounded-lg border">
            <h3 className="font-semibold mb-3 flex items-center gap-2">
              <DollarSign className="w-5 h-5" />
              Order Summary
            </h3>
            <div className="space-y-2 text-sm">
              {totalBeforeVat > 0 && (
                <div className="flex justify-between">
                  <span className="text-gray-600">Subtotal (Before VAT):</span>
                  <span>{totalBeforeVat.toLocaleString()} VND</span>
                </div>
              )}
              {vatAmount > 0 && (
                <div className="flex justify-between">
                  <span className="text-gray-600">VAT ({vatPercentage}%):</span>
                  <span>{vatAmount.toLocaleString()} VND</span>
                </div>
              )}
              {order.deliveryInfo?.deliveryFee !== undefined && (
                <div className="flex justify-between">
                  <span className="text-gray-600">Delivery Fee:</span>
                  <span>
                    {order.deliveryInfo.deliveryFee === 0 ? 'FREE' : `${order.deliveryInfo.deliveryFee.toLocaleString()} VND`}
                  </span>
                </div>
              )}
              <div className="border-t pt-2 mt-2">
                <div className="flex justify-between items-center">
                  <span className="text-lg font-semibold">Total Amount:</span>
                  <span className="text-lg font-bold text-green-600">{totalAmount.toLocaleString()} VND</span>
                </div>
              </div>
            </div>
          </div>

          <div className="flex justify-between pt-4 border-t">
            <div className="flex gap-2">
              {canConfirm && onConfirmOrder && (
                <Button 
                  onClick={handleConfirmOrder}
                  disabled={isConfirming}
                  className="bg-green-600 hover:bg-green-700 text-white"
                >
                  {isConfirming ? (
                    <>
                      <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                      Confirming...
                    </>
                  ) : (
                    <>
                      <CheckCircle className="w-4 h-4 mr-2" />
                      Confirm Order
                    </>
                  )}
                </Button>
              )}
              {canCancel && onCancelOrder && (
                <Button 
                  onClick={handleCancelOrder}
                  disabled={isCancelling}
                  variant="destructive"
                >
                  {isCancelling ? (
                    <>
                      <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                      Cancelling...
                    </>
                  ) : (
                    <>
                      <X className="w-4 h-4 mr-2" />
                      Cancel Order
                    </>
                  )}
                </Button>
              )}
            </div>
            <Button onClick={onClose} variant="outline" className="px-6">Close</Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};