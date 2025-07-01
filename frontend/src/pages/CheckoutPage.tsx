import React, { useState, useEffect } from 'react';
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
  customerProvince: string;
  customerDistrict: string;
  customerWard: string;
  customerAddress: string;
  paymentMethod: 'CREDIT_CARD' | 'CASH_ON_DELIVERY' | 'VNPAY';
  rushDelivery: boolean;
}

type LocationData = Record<string, Record<string, string[]>>;

const CheckoutPage: React.FC = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [step, setStep] = useState<'delivery' | 'payment' | 'review'>('delivery');
  const { items, total } = useCartStore();
  const { user } = useAuthStore();
  const navigate = useNavigate();

  const rushEligibleItems = items.filter(item => item.product.rushOrderSupported);
  const regularItems = items.filter(item => !item.product.rushOrderSupported);

  const [locations, setLocations] = useState<LocationData>({});

  useEffect(() => {
    fetch('/vietnam_locations.json')
      .then(res => res.json())
      .then(setLocations);
  }, []);

  const form = useForm<CheckoutFormData>({
    defaultValues: {
      customerName: user?.name || '',
      customerEmail: user?.email || '',
      customerPhone: user?.phone || '',
      customerWard: '',
      customerProvince: '',
      customerAddress: user?.address || '',
      paymentMethod: 'VNPAY',
      rushDelivery: false,
    },
  });

  const watchedValues = form.watch();

  useEffect(() => {
    // Uncheck rushDelivery if province is not 'Hà Nội'
    if (watchedValues.customerProvince !== 'Hà Nội' && form.getValues('rushDelivery')) {
      form.setValue('rushDelivery', false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [watchedValues.customerProvince]);

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

  const rushDeliveryFee = 50000;
  const regularDeliveryFee = 30000;
  const deliveryFee = rushEligibleItems.length > 0
    && regularItems.length > 0 ?
    (rushDeliveryFee + regularDeliveryFee)
    : (regularItems.length == 0 ?
      rushDeliveryFee : regularDeliveryFee);

  const subtotal = total;
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
          province: data.customerProvince,
          district: data.customerDistrict,
          ward: data.customerWard,
          address: data.customerAddress,
          deliveryFee: deliveryFee,
        },
      };

      if (data.paymentMethod === 'VNPAY') {
        const response = await orderApi.create(orderData);
        console.log('Response from Order creation request:', response);

        const paymentRequest = {
          amount: finalTotal.toString(),
          orderId: response.data.orderId,
          language: 'vn',
          vnp_Version: '2.1.0',
          bankCode: "VNBANK"
        };

        // Use paymentApi instead of fetch
        const { paymentUrl } = await paymentApi.createVNPayPayment(paymentRequest);
        window.location.href = paymentUrl;
        return;
      }

    } catch (error) {
      console.error('Order creation failed:', error);
      console.error('Form data:', data);
    } finally {
      setIsLoading(false);
    }
  };

  const nextStep = async (e: React.MouseEvent) => {
    e.preventDefault(); // Prevent any form submission
    const valid = await form.trigger(); // validate current fields
    if (!valid) return;
    if (step === 'delivery') {
      setStep('payment');
    } else if (step === 'payment') {
      setStep('review');
    }
  };

  const prevStep = (e: React.MouseEvent) => {
    e.preventDefault(); // Prevent any form submission
    if (step === 'payment') {
      setStep('delivery');
    } else if (step === 'review') {
      setStep('payment');
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // Only submit if we're on the review step
    if (step === 'review') {
      form.handleSubmit(onSubmit)(e);
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
              <form onSubmit={handleSubmit} className="space-y-6">
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
                        name="customerProvince"
                        rules={{ required: 'Province/City is required' }}
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Province/City</FormLabel>
                            <FormControl>
                              <select
                                {...field}
                                className="pl-3 pr-8 py-2 border rounded w-full"
                                onChange={e => {
                                  field.onChange(e);
                                  form.setValue('customerDistrict', '');
                                  form.setValue('customerWard', '');
                                }}
                              >
                                <option value="">Select province/city</option>
                                {Object.keys(locations).map(province => (
                                  <option key={province} value={province}>{province}</option>
                                ))}
                              </select>
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="customerDistrict"
                        rules={{ required: 'District is required' }}
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>District</FormLabel>
                            <FormControl>
                              <select
                                {...field}
                                className="pl-3 pr-8 py-2 border rounded w-full"
                                disabled={!form.watch('customerProvince')}
                                onChange={e => {
                                  field.onChange(e);
                                  form.setValue('customerWard', '');
                                }}
                              >
                                <option value="">Select district</option>
                                {form.watch('customerProvince') &&
                                  Object.keys(locations[form.watch('customerProvince')] || {}).map(district => (
                                    <option key={district} value={district}>{district}</option>
                                  ))}
                              </select>
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
                              <select
                                {...field}
                                className="pl-3 pr-8 py-2 border rounded w-full"
                                disabled={!form.watch('customerDistrict')}
                              >
                                <option value="">Select ward</option>
                                {form.watch('customerProvince') &&
                                  form.watch('customerDistrict') &&
                                  (locations[form.watch('customerProvince')]?.[form.watch('customerDistrict')] || []).map(ward => (
                                    <option key={ward} value={ward}>{ward}</option>
                                  ))}
                              </select>
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
                                disabled={
                                  watchedValues.customerProvince !== 'Hà Nội' ||
                                  rushEligibleItems.length === 0
                                }
                              />
                            </FormControl>
                            <div className="space-y-1 leading-none">
                              <FormLabel>Rush Delivery (+20,000 VND)</FormLabel>
                              <p className="text-sm text-muted-foreground">
                                {watchedValues.customerProvince === 'Hà Nội' && rushEligibleItems.length > 0
                                  ? 'Get your order delivered within 24 hours'
                                  : (watchedValues.customerProvince !== 'Hà Nội')
                                    ? 'Rush Delivery is only available for Hà Nội'
                                    : `No rush delivery items in your cart`}
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
                                  disabled
                                  readOnly
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
                                  disabled
                                  readOnly
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
                {/* If Rush Delivery is selected and there are eligible items */}
                {watchedValues.rushDelivery && rushEligibleItems.length > 0 ? (
                  <>
                    <div>
                      <h4 className="font-semibold text-orange-600 mb-2">Rush Delivery Items</h4>
                      {rushEligibleItems.map((item) => (
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
                      <div className="flex justify-between text-sm mt-2">
                        <span>Rush Delivery Fee</span>
                        <span>{rushDeliveryFee.toLocaleString()} VND</span>
                      </div>
                    </div>
                    {regularItems.length > 0 && (
                      <div className="mt-4">
                        <h4 className="font-semibold text-blue-600 mb-2">Regular Delivery Items</h4>
                        {regularItems.map((item) => (
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
                        <div className="flex justify-between text-sm mt-2">
                          <span>Regular Delivery Fee</span>
                          <span>{regularDeliveryFee.toLocaleString()} VND</span>
                        </div>
                      </div>
                    )}
                  </>
                ) : (
                  // If Rush Delivery is not selected, show all items as regular
                  items.map((item) => (
                    <div key={item.product.productId} className="flex justify-between items-center">
                      <div className="flex-1">
                        <h4 className="font-medium text-sm">{item.product.title}</h4>
                        <p className="text-sm text-muted-foreground">Qty: {item.quantity}</p>
                      </div>
                      <span className="font-medium text-sm">
                        {(item.product.price * item.quantity).toLocaleString()} VND
                      </span>
                    </div>
                  ))
                )}

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