import React from 'react';
import { Link } from 'react-router-dom';
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from './ui/card';
import { Button } from './ui/button';
import { Product } from '../types/api';
import { useCartStore } from '../stores/cartStore';
import { formatPrice } from '../lib/utils';
import { ShoppingCart, Package, Eye } from 'lucide-react';

interface ProductCardProps {
  product: Product;
}

export const ProductCard: React.FC<ProductCardProps> = ({ product }) => {
  const { addItem, getItemQuantity } = useCartStore();
  const itemQuantity = getItemQuantity(product.productId);

  const handleAddToCart = () => {
    addItem(product);
  };

  const getProductTypeIcon = (type: string) => {
    switch (type.toLowerCase()) {
      case 'book':
        return '📚';
      case 'cd':
        return '💿';
      case 'dvd':
        return '📀';
      default:
        return '📦';
    }
  };

  const getStockStatus = () => {
    if (product.quantity === 0) {
      return { text: 'Out of Stock', color: 'text-red-500' };
    } else if (product.quantity < 10) {
      return { text: `Low Stock (${product.quantity})`, color: 'text-yellow-500' };
    } else {
      return { text: 'In Stock', color: 'text-green-500' };
    }
  };

  const stockStatus = getStockStatus();

  return (
    <Card className="h-full flex flex-col transition-shadow hover:shadow-lg">
      <CardHeader className="pb-3">
        <div className="flex items-start justify-between">
          <div className="flex items-center gap-2">
            <span className="text-2xl">{getProductTypeIcon(product.type)}</span>
            <div className="text-xs text-muted-foreground uppercase tracking-wide">
              {product.type}
            </div>
          </div>
          {product.rushOrderSupported && (
            <div className="text-xs bg-orange-100 text-orange-800 px-2 py-1 rounded-full">
              Rush Order
            </div>
          )}
        </div>
        <CardTitle className="text-lg line-clamp-2">
          <Link 
            to={`/products/${product.productId}`}
            className="hover:text-primary transition-colors"
          >
            {product.title}
          </Link>
        </CardTitle>
      </CardHeader>

      <CardContent className="flex-1">
        {product.imageUrl && (
          <div className="w-full h-32 bg-gray-100 rounded-md mb-3 flex items-center justify-center">
            <img
              src={product.imageUrl}
              alt={product.title}
              className="max-w-full max-h-full object-cover rounded-md"
              onError={(e) => {
                e.currentTarget.src = '/placeholder-image.png';
              }}
            />
          </div>
        )}
        
        {product.introduction && (
          <p className="text-sm text-muted-foreground line-clamp-3 mb-3">
            {product.introduction}
          </p>
        )}

        <div className="space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-lg font-bold text-primary">
              {formatPrice(product.price)}
            </span>
            <span className={`text-sm ${stockStatus.color}`}>
              {stockStatus.text}
            </span>
          </div>

          {product.barcode && (
            <div className="text-xs text-muted-foreground">
              Barcode: {product.barcode}
            </div>
          )}

          {product.weight && (
            <div className="text-xs text-muted-foreground flex items-center gap-1">
              <Package className="w-3 h-3" />
              Weight: {product.weight}kg
            </div>
          )}
        </div>
      </CardContent>

      <CardFooter className="pt-3">
        <div className="w-full space-y-2">
          {itemQuantity > 0 && (
            <div className="text-sm text-center text-green-600">
              {itemQuantity} in cart
            </div>
          )}
          <div className="flex gap-2">
            <Link to={`/products/${product.productId}`} className="flex-1">
              <Button variant="outline" size="sm" className="w-full">
                <Eye className="w-4 h-4 mr-2" />
                View
              </Button>
            </Link>
            <Button
              onClick={handleAddToCart}
              disabled={product.quantity === 0}
              className="flex-1"
              size="sm"
            >
              <ShoppingCart className="w-4 h-4 mr-2" />
              {product.quantity === 0 ? 'Out of Stock' : 'Add to Cart'}
            </Button>
          </div>
        </div>
      </CardFooter>
    </Card>
  );
};