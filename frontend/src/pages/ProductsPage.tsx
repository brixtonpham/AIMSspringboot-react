import React, { useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { ProductGrid } from '../components/ProductGrid';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { ProductSearchParams } from '../types/api';
import { Search, Filter, X } from 'lucide-react';

export const ProductsPage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [filters, setFilters] = useState<ProductSearchParams>({
    title: searchParams.get('title') || '',
    type: searchParams.get('type') || '',
    minPrice: searchParams.get('minPrice') ? Number(searchParams.get('minPrice')) : undefined,
    maxPrice: searchParams.get('maxPrice') ? Number(searchParams.get('maxPrice')) : undefined,
    inStock: searchParams.get('inStock') === 'true' || undefined,
  });
  
  const [showFilters, setShowFilters] = useState(false);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    updateSearchParams();
  };

  const updateSearchParams = () => {
    const params = new URLSearchParams();
    Object.entries(filters).forEach(([key, value]) => {
      if (value !== undefined && value !== '') {
        params.set(key, value.toString());
      }
    });
    setSearchParams(params);
  };

  const clearFilters = () => {
    setFilters({
      title: '',
      type: '',
      minPrice: undefined,
      maxPrice: undefined,
      inStock: undefined,
    });
    setSearchParams({});
  };

  const activeFiltersCount = Object.values(filters).filter(
    (value) => value !== undefined && value !== ''
  ).length;

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold mb-6">Products</h1>
        
        {/* Search and Filter Header */}
        <div className="flex flex-col sm:flex-row gap-4 mb-6">
          <form onSubmit={handleSearch} className="flex-1 flex gap-2">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
              <Input
                type="text"
                placeholder="Search products..."
                value={filters.title}
                onChange={(e) => setFilters({ ...filters, title: e.target.value })}
                className="pl-10"
              />
            </div>
            <Button type="submit">Search</Button>
          </form>
          
          <div className="flex gap-2">
            <Button
              variant="outline"
              onClick={() => setShowFilters(!showFilters)}
              className="relative"
            >
              <Filter className="w-4 h-4 mr-2" />
              Filters
              {activeFiltersCount > 0 && (
                <span className="absolute -top-1 -right-1 bg-primary text-primary-foreground text-xs rounded-full h-5 w-5 flex items-center justify-center">
                  {activeFiltersCount}
                </span>
              )}
            </Button>
            
            {activeFiltersCount > 0 && (
              <Button variant="ghost" onClick={clearFilters}>
                <X className="w-4 h-4 mr-2" />
                Clear
              </Button>
            )}
          </div>
        </div>

        {/* Filters Panel */}
        {showFilters && (
          <Card className="mb-6">
            <CardHeader>
              <CardTitle>Filters</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                {/* Product Type */}
                <div>
                  <label className="block text-sm font-medium mb-2">Product Type</label>
                  <select
                    value={filters.type}
                    onChange={(e) => setFilters({ ...filters, type: e.target.value })}
                    className="w-full px-3 py-2 border border-input rounded-md"
                  >
                    <option value="">All Types</option>
                    <option value="book">Books</option>
                    <option value="cd">CDs</option>
                    <option value="dvd">DVDs</option>
                  </select>
                </div>

                {/* Min Price */}
                <div>
                  <label className="block text-sm font-medium mb-2">Min Price (VND)</label>
                  <Input
                    type="number"
                    placeholder="0"
                    value={filters.minPrice || ''}
                    onChange={(e) => setFilters({ 
                      ...filters, 
                      minPrice: e.target.value ? Number(e.target.value) : undefined 
                    })}
                  />
                </div>

                {/* Max Price */}
                <div>
                  <label className="block text-sm font-medium mb-2">Max Price (VND)</label>
                  <Input
                    type="number"
                    placeholder="1000000"
                    value={filters.maxPrice || ''}
                    onChange={(e) => setFilters({ 
                      ...filters, 
                      maxPrice: e.target.value ? Number(e.target.value) : undefined 
                    })}
                  />
                </div>

                {/* In Stock */}
                <div className="flex items-center space-x-2">
                  <input
                    type="checkbox"
                    id="inStock"
                    checked={filters.inStock || false}
                    onChange={(e) => setFilters({ 
                      ...filters, 
                      inStock: e.target.checked || undefined 
                    })}
                    className="rounded border-gray-300"
                  />
                  <label htmlFor="inStock" className="text-sm font-medium">
                    In Stock Only
                  </label>
                </div>
              </div>

              <div className="flex gap-2 mt-4">
                <Button onClick={updateSearchParams}>Apply Filters</Button>
                <Button variant="outline" onClick={clearFilters}>
                  Clear All
                </Button>
              </div>
            </CardContent>
          </Card>
        )}
      </div>

      {/* Products Grid */}
      <ProductGrid searchParams={filters} />
    </div>
  );
};