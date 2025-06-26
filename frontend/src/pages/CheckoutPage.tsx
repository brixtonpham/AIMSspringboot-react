import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { Truck, CreditCard, MapPin, Phone, Mail, User, Check } from 'lucide-react';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '../components/ui/form';
import { useCartStore } from '../stores/cartStore';
import { useAuthStore } from '../stores/authStore';
import { orderApi, paymentApi } from '../services/api';
import { CreateOrderRequest } from '../types/api';

interface CheckoutFormData {
  customerName: string;
  customerEmail: string;
  customerPhone: string;
  customerWard: string;
  customerProvince: string;
  customerAddress: string;
  paymentMethod: 'CREDIT_CARD' | 'CASH_ON_DELIVERY' | 'VNPAY';
  rushDelivery: boolean;
}

const CheckoutPage: React.FC = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [step, setStep] = useState<'delivery' | 'payment' | 'review'>('delivery');
  const { items, total, clearCart } = useCartStore();
  const { user } = useAuthStore();
  const navigate = useNavigate();

  const form = useForm<CheckoutFormData>({
    defaultValues: {
      customerName: user?.name || '',
      customerEmail: user?.email || '',
      customerPhone: user?.phone || '',
      customerWard: '',
      customerProvince: '',
      customerAddress: user?.address || '',
      paymentMethod: 'CREDIT_CARD',
      rushDelivery: false,
    },
  });

  const watchedValues = form.watch();

  if (items.length === 0) {
    return (
      <div className="container mx-auto px-4 py-16 text-center">
        <h1 className="text-4xl font-bold mb-4">Your cart is empty</h1>
        <p className="text-muted-foreground mb-8">Add some items to your cart to proceed with checkout.</p>
        <Button onClick={() => navigate('/products')}>
          Continue Shopping
        </Button>
      </div>
    );
  }

  const subtotal = total;
  const deliveryFee = watchedValues.rushDelivery ? 50000 : 30000; // Rush delivery costs more
  const tax = Math.round(subtotal * 0.1); // 10% VAT
  const finalTotal = subtotal + deliveryFee + tax;

  const onSubmit = async (data: CheckoutFormData) => {
    setIsLoading(true);
    try {
      const orderData: CreateOrderRequest = {
        cartItems: items.map(item => ({
          productId: item.product.productId,
          quantity: item.quantity,
          rushOrder: data.rushDelivery,
          productTitle: item.product.title,
          unitPrice: item.product.price,
        })),
        deliveryInfo: {
          name: data.customerName,
          email: data.customerEmail,
          phone: data.customerPhone,
          address: data.customerAddress,
          ward: data.customerWard,
          province: data.customerProvince,
          deliveryFee: deliveryFee,
        },
      };

      const response = await orderApi.create(orderData);
      clearCart();

      if (data.paymentMethod === 'VNPAY') {
        const paymentRequest = {
          amount: finalTotal.toString(),
          language: 'vn',
          vnp_Version: '2.1.0',
          bankCode: "VNBANK"
        };

        // Use paymentApi instead of fetch
        const { paymentUrl } = await paymentApi.createVNPayPayment(paymentRequest);
        window.location.href = paymentUrl;
        return;
      }


      console.log('Order creation:', response.data);
      // For other payment methods, navigate to confirmation page
      // navigate('/order-confirmation', {
      //  state: {
      //    orderId: response.data.orderId,
      //    total: finalTotal,
      //    paymentMethod: data.paymentMethod
      //  }
      // });
    } catch (error) {
      console.error('Order creation failed:', error);
      console.error('Form data:', data);
    } finally {
      setIsLoading(false);
    }
  };

  const nextStep = () => {
    if (step === 'delivery') {
      setStep('payment');
    } else if (step === 'payment') {
      setStep('review');
    }
  };

  const prevStep = () => {
    if (step === 'payment') {
      setStep('delivery');
    } else if (step === 'review') {
      setStep('payment');
    }
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-bold mb-8">Checkout</h1>

        {/* Progress Steps */}
        <div className="flex items-center justify-center mb-8">
          <div className="flex items-center space-x-4">
            <div className={`flex items-center space-x-2 ${step === 'delivery' ? 'text-primary' : 'text-muted-foreground'}`}>
              <div className={`w-8 h-8 rounded-full flex items-center justify-center ${step === 'delivery' ? 'bg-primary text-primary-foreground' : 'bg-muted'}`}>
                {step !== 'delivery' ? <Check className="w-4 h-4" /> : '1'}
              </div>
              <span className="font-medium">Delivery</span>
            </div>
            <div className="w-8 h-px bg-border"></div>
            <div className={`flex items-center space-x-2 ${step === 'payment' ? 'text-primary' : 'text-muted-foreground'}`}>
              <div className={`w-8 h-8 rounded-full flex items-center justify-center ${step === 'payment' ? 'bg-primary text-primary-foreground' : 'bg-muted'}`}>
                {step === 'review' ? <Check className="w-4 h-4" /> : '2'}
              </div>
              <span className="font-medium">Payment</span>
            </div>
            <div className="w-8 h-px bg-border"></div>
            <div className={`flex items-center space-x-2 ${step === 'review' ? 'text-primary' : 'text-muted-foreground'}`}>
              <div className={`w-8 h-8 rounded-full flex items-center justify-center ${step === 'review' ? 'bg-primary text-primary-foreground' : 'bg-muted'}`}>
                3
              </div>
              <span className="font-medium">Review</span>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Form Section */}
          <div className="lg:col-span-2">
            <Form {...form}>
              <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
                {/* Delivery Information */}
                {step === 'delivery' && (
                  <Card>
                    <CardHeader>
                      <CardTitle className="flex items-center space-x-2">
                        <Truck className="w-5 h-5" />
                        <span>Delivery Information</span>
                      </CardTitle>
                      <CardDescription>
                        Please provide your delivery details
                      </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                      <FormField
                        control={form.control}
                        name="customerName"
                        rules={{ required: 'Name is required' }}
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Full Name</FormLabel>
                            <FormControl>
                              <div className="relative">
                                <User className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                                <Input {...field} placeholder="Enter your full name" className="pl-10" />
                              </div>
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="customerEmail"
                        rules={{
                          required: 'Email is required',
                          pattern: {
                            value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                            message: 'Invalid email address',
                          },
                        }}
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Email Address</FormLabel>
                            <FormControl>
                              <div className="relative">
                                <Mail className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                                <Input {...field} type="email" placeholder="Enter your email" className="pl-10" />
                              </div>
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="customerPhone"
                        rules={{ required: 'Phone number is required' }}
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Phone Number</FormLabel>
                            <FormControl>
                              <div className="relative">
                                <Phone className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                                <Input {...field} placeholder="Enter your phone number" className="pl-10" />
                              </div>
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="customerWard"
                        rules={{ required: 'Ward is required' }}
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Ward</FormLabel>
                            <FormControl>
                              <div className="relative">
                                <MapPin className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                                <Input {...field} placeholder="Enter your ward" className="pl-10" />
                              </div>
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="customerProvince"
                        rules={{ required: 'Province/City is required' }}
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Province/City</FormLabel>
                            <FormControl>
                              <div className="relative">
                                <MapPin className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                                <Input {...field} placeholder="Enter your province or city" className="pl-10" />
                              </div>
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="customerAddress"
                        rules={{ required: 'Detailed address is required' }}
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Detailed Address</FormLabel>
                            <FormControl>
                              <div className="relative">
                                <MapPin className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                                <Input {...field} placeholder="Enter street address, building number, etc." className="pl-10" />
                              </div>
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="rushDelivery"
                        render={({ field }) => (
                          <FormItem className="flex flex-row items-start space-x-3 space-y-0">
                            <FormControl>
                              <input
                                type="checkbox"
                                checked={field.value}
                                onChange={field.onChange}
                                className="mt-1"
                              />
                            </FormControl>
                            <div className="space-y-1 leading-none">
                              <FormLabel>Rush Delivery (+20,000 VND)</FormLabel>
                              <p className="text-sm text-muted-foreground">
                                Get your order delivered within 24 hours
                              </p>
                            </div>
                          </FormItem>
                        )}
                      />
                    </CardContent>
                  </Card>
                )}

                {/* Payment Method */}
                {step === 'payment' && (
                  <Card>
                    <CardHeader>
                      <CardTitle className="flex items-center space-x-2">
                        <CreditCard className="w-5 h-5" />
                        <span>Payment Method</span>
                      </CardTitle>
                      <CardDescription>
                        Choose your preferred payment method
                      </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                      <FormField
                        control={form.control}
                        name="paymentMethod"
                        render={({ field }) => (
                          <FormItem>
                            <div className="space-y-3">
                              <div className="flex items-center space-x-2">
                                <input
                                  type="radio"
                                  id="credit-card"
                                  value="CREDIT_CARD"
                                  checked={field.value === 'CREDIT_CARD'}
                                  onChange={field.onChange}
                                />
                                <label htmlFor="credit-card" className="flex items-center space-x-2 cursor-pointer">
                                  <CreditCard className="w-4 h-4" />
                                  <span>Credit/Debit Card</span>
                                </label>
                              </div>
                              <div className="flex items-center space-x-2">
                                <input
                                  type="radio"
                                  id="vnpay"
                                  value="VNPAY"
                                  checked={field.value === 'VNPAY'}
                                  onChange={field.onChange}
                                />
                                <label htmlFor="vnpay" className="flex items-center space-x-2 cursor-pointer">
                                  <div className="w-4 h-4 bg-blue-500 rounded"></div>
                                  <span>VNPay</span>
                                </label>
                              </div>
                              <div className="flex items-center space-x-2">
                                <input
                                  type="radio"
                                  id="cod"
                                  value="CASH_ON_DELIVERY"
                                  checked={field.value === 'CASH_ON_DELIVERY'}
                                  onChange={field.onChange}
                                />
                                <label htmlFor="cod" className="flex items-center space-x-2 cursor-pointer">
                                  <Truck className="w-4 h-4" />
                                  <span>Cash on Delivery</span>
                                </label>
                              </div>
                            </div>
                          </FormItem>
                        )}
                      />
                    </CardContent>
                  </Card>
                )}

                {/* Review Order */}
                {step === 'review' && (
                  <Card>
                    <CardHeader>
                      <CardTitle>Review Your Order</CardTitle>
                      <CardDescription>
                        Please review your order details before confirming
                      </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                          <h4 className="font-medium mb-2">Delivery Information</h4>
                          <div className="text-sm text-muted-foreground space-y-1">
                            <p>{watchedValues.customerName}</p>
                            <p>{watchedValues.customerEmail}</p>
                            <p>{watchedValues.customerPhone}</p>
                            <p>{watchedValues.customerWard}</p>
                            <p>{watchedValues.customerProvince}</p>
                            <p>{watchedValues.customerAddress}</p>
                            {watchedValues.rushDelivery && (
                              <p className="text-orange-600 font-medium">Rush Delivery Selected</p>
                            )}
                          </div>
                        </div>
                        <div>
                          <h4 className="font-medium mb-2">Payment Method</h4>
                          <p className="text-sm text-muted-foreground">
                            {watchedValues.paymentMethod === 'CREDIT_CARD' && 'Credit/Debit Card'}
                            {watchedValues.paymentMethod === 'VNPAY' && 'VNPay Payment Gateway'}
                            {watchedValues.paymentMethod === 'CASH_ON_DELIVERY' && 'Cash on Delivery'}
                          </p>
                        </div>
                      </div>

                      <div className="border-t pt-4">
                        <h4 className="font-medium mb-2">Order Items</h4>
                        <div className="space-y-2">
                          {items.map((item) => (
                            <div key={item.product.productId} className="flex justify-between text-sm">
                              <span>{item.product.title} x {item.quantity}</span>
                              <span>{(item.product.price * item.quantity).toLocaleString()} VND</span>
                            </div>
                          ))}
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                )}

                {/* Navigation Buttons */}
                <div className="flex justify-between">
                  <Button
                    type="button"
                    variant="outline"
                    onClick={prevStep}
                    disabled={step === 'delivery'}
                  >
                    Previous
                  </Button>

                  {step !== 'review' ? (
                    <Button type="button" onClick={nextStep}>
                      Next
                    </Button>
                  ) : (
                    <Button type="submit" disabled={isLoading}>
                      {isLoading ? 'Processing...' : 'Place Order'}
                    </Button>
                  )}
                </div>
              </form>
            </Form>
          </div>

          {/* Order Summary */}
          <div className="lg:col-span-1">
            <Card className="sticky top-24">
              <CardHeader>
                <CardTitle>Order Summary</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                {items.map((item) => (
                  <div key={item.product.productId} className="flex justify-between items-center">
                    <div className="flex-1">
                      <h4 className="font-medium text-sm">{item.product.title}</h4>
                      <p className="text-sm text-muted-foreground">Qty: {item.quantity}</p>
                    </div>
                    <span className="font-medium text-sm">
                      {(item.product.price * item.quantity).toLocaleString()} VND
                    </span>
                  </div>
                ))}

                <div className="border-t pt-4 space-y-2">
                  <div className="flex justify-between text-sm">
                    <span>Subtotal</span>
                    <span>{subtotal.toLocaleString()} VND</span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span>Delivery Fee {watchedValues.rushDelivery && '(Rush)'}</span>
                    <span>{deliveryFee.toLocaleString()} VND</span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span>Tax (10%)</span>
                    <span>{tax.toLocaleString()} VND</span>
                  </div>
                  <div className="flex justify-between font-bold text-lg border-t pt-2">
                    <span>Total</span>
                    <span>{finalTotal.toLocaleString()} VND</span>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CheckoutPage;