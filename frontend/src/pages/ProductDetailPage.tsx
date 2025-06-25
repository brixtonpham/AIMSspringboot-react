import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { ArrowLeft, ShoppingCart, Plus, Minus, Star, Truck, Shield, RotateCcw } from 'lucide-react';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { useCartStore } from '../stores/cartStore';
import { productApi } from '../services/api';
import { Product, Book, CD, DVD } from '../types/api';

const ProductDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [quantity, setQuantity] = useState(1);
  const { addItem } = useCartStore();

  const { data: response, isLoading, error } = useQuery({
    queryKey: ['product', id],
    queryFn: () => productApi.getById(Number(id)),
    enabled: !!id,
  });

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="animate-pulse space-y-4">
          <div className="h-4 bg-gray-300 rounded w-1/4"></div>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div className="h-96 bg-gray-300 rounded"></div>
            <div className="space-y-4">
              <div className="h-8 bg-gray-300 rounded"></div>
              <div className="h-4 bg-gray-300 rounded w-3/4"></div>
              <div className="h-6 bg-gray-300 rounded w-1/2"></div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (error || !response?.data) {
    return (
      <div className="container mx-auto px-4 py-16 text-center">
        <h1 className="text-4xl font-bold mb-4">Product Not Found</h1>
        <p className="text-muted-foreground mb-8">The product you're looking for doesn't exist.</p>
        <Button onClick={() => navigate('/products')}>
          Back to Products
        </Button>
      </div>
    );
  }

  const product = response.data;
  const isInStock = product.quantity > 0;
  const maxQuantity = Math.min(product.quantity, 10);

  const handleAddToCart = () => {
    addItem(product, quantity);
    // Optionally show a toast notification
  };

  const adjustQuantity = (delta: number) => {
    const newQuantity = quantity + delta;
    if (newQuantity >= 1 && newQuantity <= maxQuantity) {
      setQuantity(newQuantity);
    }
  };

  const renderTypeSpecificInfo = () => {
    switch (product.type) {
      case 'book':
        const book = product as Book;
        return (
          <Card>
            <CardHeader>
              <CardTitle>Book Details</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              {book.authors && (
                <div>
                  <span className="font-medium">Authors: </span>
                  <span className="text-muted-foreground">{book.authors.join(', ')}</span>
                </div>
              )}
              {book.publishers && (
                <div>
                  <span className="font-medium">Publishers: </span>
                  <span className="text-muted-foreground">{book.publishers.join(', ')}</span>
                </div>
              )}
              {book.genre && (
                <div>
                  <span className="font-medium">Genre: </span>
                  <span className="text-muted-foreground">{book.genre}</span>
                </div>
              )}
              {book.pageCount && (
                <div>
                  <span className="font-medium">Pages: </span>
                  <span className="text-muted-foreground">{book.pageCount}</span>
                </div>
              )}
              {book.coverType && (
                <div>
                  <span className="font-medium">Cover: </span>
                  <span className="text-muted-foreground">{book.coverType}</span>
                </div>
              )}
            </CardContent>
          </Card>
        );

      case 'cd':
        const cd = product as CD;
        return (
          <Card>
            <CardHeader>
              <CardTitle>CD Details</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              {cd.artists && (
                <div>
                  <span className="font-medium">Artists: </span>
                  <span className="text-muted-foreground">{cd.artists.join(', ')}</span>
                </div>
              )}
              {cd.recordLabel && (
                <div>
                  <span className="font-medium">Record Label: </span>
                  <span className="text-muted-foreground">{cd.recordLabel}</span>
                </div>
              )}
              {cd.releaseDate && (
                <div>
                  <span className="font-medium">Release Date: </span>
                  <span className="text-muted-foreground">
                    {new Date(cd.releaseDate).toLocaleDateString()}
                  </span>
                </div>
              )}
              {cd.trackList && cd.trackList.length > 0 && (
                <div>
                  <span className="font-medium">Track List:</span>
                  <ul className="mt-2 space-y-1 text-sm text-muted-foreground ml-4">
                    {cd.trackList.map((track, index) => (
                      <li key={index}>{index + 1}. {track}</li>
                    ))}
                  </ul>
                </div>
              )}
            </CardContent>
          </Card>
        );

      case 'dvd':
        const dvd = product as DVD;
        return (
          <Card>
            <CardHeader>
              <CardTitle>DVD Details</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              {dvd.directors && (
                <div>
                  <span className="font-medium">Directors: </span>
                  <span className="text-muted-foreground">{dvd.directors.join(', ')}</span>
                </div>
              )}
              {dvd.studio && (
                <div>
                  <span className="font-medium">Studio: </span>
                  <span className="text-muted-foreground">{dvd.studio}</span>
                </div>
              )}
              {dvd.rating && (
                <div>
                  <span className="font-medium">Rating: </span>
                  <span className="text-muted-foreground">{dvd.rating}</span>
                </div>
              )}
              {dvd.durationMinutes && (
                <div>
                  <span className="font-medium">Duration: </span>
                  <span className="text-muted-foreground">{dvd.durationMinutes} minutes</span>
                </div>
              )}
              {dvd.dvdType && (
                <div>
                  <span className="font-medium">Type: </span>
                  <span className="text-muted-foreground">{dvd.dvdType}</span>
                </div>
              )}
            </CardContent>
          </Card>
        );

      default:
        return null;
    }
  };

  return (
    <div className="container mx-auto px-4 py-8">
      {/* Back Button */}
      <Button variant="ghost" onClick={() => navigate(-1)} className="mb-6">
        <ArrowLeft className="w-4 h-4 mr-2" />
        Back
      </Button>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Product Image */}
        <div className="aspect-square bg-gray-100 rounded-lg flex items-center justify-center">
          {product.imageUrl ? (
            <img
              src={product.imageUrl}
              alt={product.title}
              className="max-w-full max-h-full object-cover rounded-lg"
            />
          ) : (
            <span className="text-8xl">
              {product.type === 'book' ? '📚' : 
               product.type === 'cd' ? '💿' : '📀'}
            </span>
          )}
        </div>

        {/* Product Information */}
        <div className="space-y-6">
          <div>
            <div className="flex items-center gap-2 mb-2">
              <span className="px-2 py-1 bg-primary/10 text-primary text-xs font-medium rounded-full capitalize">
                {product.type}
              </span>
              {product.rushDeliverySupported && (
                <span className="px-2 py-1 bg-orange-100 text-orange-600 text-xs font-medium rounded-full">
                  Rush Delivery
                </span>
              )}
            </div>
            <h1 className="text-3xl font-bold mb-2">{product.title}</h1>
            {product.description && (
              <p className="text-muted-foreground">{product.description}</p>
            )}
          </div>

          {/* Price and Stock */}
          <div className="space-y-2">
            <div className="text-3xl font-bold text-primary">
              {product.price.toLocaleString()} VND
            </div>
            <div className="flex items-center gap-2">
              <span className={`text-sm font-medium ${isInStock ? 'text-green-600' : 'text-red-600'}`}>
                {isInStock ? `In Stock (${product.quantity} available)` : 'Out of Stock'}
              </span>
            </div>
            {product.weight && (
              <p className="text-sm text-muted-foreground">Weight: {product.weight}g</p>
            )}
          </div>

          {/* Quantity Selector and Add to Cart */}
          {isInStock && (
            <div className="space-y-4">
              <div className="flex items-center gap-4">
                <span className="font-medium">Quantity:</span>
                <div className="flex items-center gap-2">
                  <Button
                    variant="outline"
                    size="icon"
                    onClick={() => adjustQuantity(-1)}
                    disabled={quantity <= 1}
                  >
                    <Minus className="w-4 h-4" />
                  </Button>
                  <Input
                    type="number"
                    value={quantity}
                    onChange={(e) => {
                      const val = parseInt(e.target.value) || 1;
                      if (val >= 1 && val <= maxQuantity) {
                        setQuantity(val);
                      }
                    }}
                    className="w-20 text-center"
                    min="1"
                    max={maxQuantity}
                  />
                  <Button
                    variant="outline"
                    size="icon"
                    onClick={() => adjustQuantity(1)}
                    disabled={quantity >= maxQuantity}
                  >
                    <Plus className="w-4 h-4" />
                  </Button>
                </div>
              </div>

              <Button
                onClick={handleAddToCart}
                size="lg"
                className="w-full md:w-auto"
              >
                <ShoppingCart className="w-4 h-4 mr-2" />
                Add to Cart
              </Button>
            </div>
          )}

          {/* Product Features */}
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div className="flex items-center gap-2 text-sm">
              <Truck className="w-4 h-4 text-blue-600" />
              <span>Free Shipping</span>
            </div>
            <div className="flex items-center gap-2 text-sm">
              <Shield className="w-4 h-4 text-green-600" />
              <span>Quality Guaranteed</span>
            </div>
            <div className="flex items-center gap-2 text-sm">
              <RotateCcw className="w-4 h-4 text-orange-600" />
              <span>Easy Returns</span>
            </div>
          </div>
        </div>
      </div>

      {/* Type-specific Information */}
      <div className="mt-12">
        {renderTypeSpecificInfo()}
      </div>

      {/* Product Specifications */}
      <div className="mt-8">
        <Card>
          <CardHeader>
            <CardTitle>Product Specifications</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <span className="font-medium">Product ID: </span>
                <span className="text-muted-foreground">{product.productId}</span>
              </div>
              {product.barcode && (
                <div>
                  <span className="font-medium">Barcode: </span>
                  <span className="text-muted-foreground">{product.barcode}</span>
                </div>
              )}
              <div>
                <span className="font-medium">Category: </span>
                <span className="text-muted-foreground capitalize">{product.type}</span>
              </div>
              {product.weight && (
                <div>
                  <span className="font-medium">Weight: </span>
                  <span className="text-muted-foreground">{product.weight}g</span>
                </div>
              )}
              <div>
                <span className="font-medium">Rush Delivery: </span>
                <span className="text-muted-foreground">
                  {product.rushDeliverySupported ? 'Available' : 'Not Available'}
                </span>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default ProductDetailPage;