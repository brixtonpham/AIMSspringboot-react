import React from 'react';
import { Link } from 'react-router-dom';
import { useCartStore } from '../stores/cartStore';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { formatPrice } from '../lib/utils';
import { Minus, Plus, Trash2, ShoppingBag, ArrowLeft } from 'lucide-react';

export const CartPage: React.FC = () => {
  const { items, total, updateQuantity, removeItem, clearCart } = useCartStore();

  if (items.length === 0) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="text-center py-16">
          <ShoppingBag className="w-16 h-16 mx-auto text-muted-foreground mb-4" />
          <h1 className="text-3xl font-bold mb-4">Your cart is empty</h1>
          <p className="text-muted-foreground mb-8">
            Looks like you haven't added any items to your cart yet.
          </p>
          <Button asChild>
            <Link to="/products">Start Shopping</Link>
          </Button>
        </div>
      </div>
    );
  }

  const handleQuantityChange = (productId: number, newQuantity: number) => {
    if (newQuantity >= 1) {
      updateQuantity(productId, newQuantity);
    }
  };

  const vatRate = 0.1; // 10% VAT
  const subtotal = total;
  const vatAmount = subtotal * vatRate;
  const totalWithVat = subtotal + vatAmount;

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex items-center gap-4 mb-8">
        <Button variant="ghost" size="icon" asChild>
          <Link to="/products">
            <ArrowLeft className="w-4 h-4" />
          </Link>
        </Button>
        <h1 className="text-3xl font-bold">Shopping Cart</h1>
        <span className="text-muted-foreground">({items.length} items)</span>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Cart Items */}
        <div className="lg:col-span-2 space-y-4">
          {items.map((item) => (
            <Card key={item.product.productId}>
              <CardContent className="p-6">
                <div className="flex items-center gap-4">
                  {/* Product Image */}
                  <div className="w-20 h-20 bg-gray-100 rounded-md flex items-center justify-center flex-shrink-0">
                    {item.product.imageUrl ? (
                      <img
                        src={item.product.imageUrl}
                        alt={item.product.title}
                        className="max-w-full max-h-full object-cover rounded-md"
                      />
                    ) : (
                      <span className="text-2xl">
                        {item.product.type === 'book' ? '📚' : 
                         item.product.type === 'cd' ? '💿' : '📀'}
                      </span>
                    )}
                  </div>

                  {/* Product Details */}
                  <div className="flex-1 min-w-0">
                    <h3 className="font-semibold truncate">{item.product.title}</h3>
                    <p className="text-sm text-muted-foreground capitalize">
                      {item.product.type}
                    </p>
                    <p className="text-sm font-medium text-primary">
                      {formatPrice(item.product.price)}
                    </p>
                    
                    {/* Stock warning */}
                    {item.product.quantity < item.quantity && (
                      <p className="text-sm text-red-500">
                        Only {item.product.quantity} available
                      </p>
                    )}
                  </div>

                  {/* Quantity Controls */}
                  <div className="flex items-center gap-2">
                    <Button
                      variant="outline"
                      size="icon"
                      onClick={() => handleQuantityChange(item.product.productId, item.quantity - 1)}
                      disabled={item.quantity <= 1}
                    >
                      <Minus className="w-3 h-3" />
                    </Button>
                    
                    <Input
                      type="number"
                      value={item.quantity}
                      onChange={(e) => handleQuantityChange(item.product.productId, parseInt(e.target.value) || 1)}
                      className="w-16 text-center"
                      min="1"
                      max={item.product.quantity}
                    />
                    
                    <Button
                      variant="outline"
                      size="icon"
                      onClick={() => handleQuantityChange(item.product.productId, item.quantity + 1)}
                      disabled={item.quantity >= item.product.quantity}
                    >
                      <Plus className="w-3 h-3" />
                    </Button>
                  </div>

                  {/* Item Total */}
                  <div className="text-right min-w-0">
                    <p className="font-semibold">
                      {formatPrice(item.product.price * item.quantity)}
                    </p>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => removeItem(item.product.productId)}
                      className="text-red-500 hover:text-red-700"
                    >
                      <Trash2 className="w-4 h-4 mr-1" />
                      Remove
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}

          {/* Clear Cart Button */}
          <div className="flex justify-end">
            <Button variant="outline" onClick={clearCart}>
              <Trash2 className="w-4 h-4 mr-2" />
              Clear Cart
            </Button>
          </div>
        </div>

        {/* Order Summary */}
        <div className="lg:col-span-1">
          <Card className="sticky top-24">
            <CardHeader>
              <CardTitle>Order Summary</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <div className="flex justify-between">
                  <span>Subtotal</span>
                  <span>{formatPrice(subtotal)}</span>
                </div>
                <div className="flex justify-between">
                  <span>VAT (10%)</span>
                  <span>{formatPrice(vatAmount)}</span>
                </div>
                <div className="border-t pt-2">
                  <div className="flex justify-between font-semibold text-lg">
                    <span>Total</span>
                    <span>{formatPrice(totalWithVat)}</span>
                  </div>
                </div>
              </div>

              <div className="space-y-2">
                <Button className="w-full" size="lg" asChild>
                  <Link to="/checkout">
                    Proceed to Checkout
                  </Link>
                </Button>
                <Button variant="outline" className="w-full" asChild>
                  <Link to="/products">
                    Continue Shopping
                  </Link>
                </Button>
              </div>

              {/* Delivery Info */}
              <div className="text-sm text-muted-foreground border-t pt-4">
                <p className="mb-2">🚚 Free delivery on orders over {formatPrice(500000)}</p>
                <p>⚡ Rush delivery available for select items</p>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
};