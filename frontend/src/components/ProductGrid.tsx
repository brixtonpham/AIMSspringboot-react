import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { ProductCard } from './ProductCard';
import { productApi } from '../services/api';
import { Product } from '../types/api';
import { Loader2 } from 'lucide-react';

interface ProductGridProps {
  type?: string;
  searchParams?: any;
}

export const ProductGrid: React.FC<ProductGridProps> = ({ type, searchParams }) => {
  const { data, isLoading, error } = useQuery({
    queryKey: ['products', type, searchParams],
    queryFn: async () => {
      if (type) {
        return await productApi.getByType(type);
      } else if (searchParams && Object.values(searchParams).some(value => value !== undefined && value !== '')) {
        return await productApi.search(searchParams);
      } else {
        return await productApi.getAll();
      }
    },
  });

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <Loader2 className="w-8 h-8 animate-spin" />
        <span className="ml-2">Loading products...</span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-12">
        <p className="text-red-500">Error loading products. Please try again.</p>
      </div>
    );
  }

  const products = data?.data || [];

  if (products.length === 0) {
    return (
      <div className="text-center py-12">
        <p className="text-muted-foreground">No products found.</p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
      {products.map((product: Product) => (
        <ProductCard key={product.productId} product={product} />
      ))}
    </div>
  );
};