import React, { useState, useEffect } from 'react';
import { X } from 'lucide-react';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';

interface ProductFormData {
  title: string;
  price: number;
  quantity: number;
  barcode: string;
  type: 'book' | 'cd' | 'dvd' | 'lp';
  weight?: number;
  rushOrderSupported?: boolean;
  introduction?: string;
}

interface ProductModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: ProductFormData) => Promise<void>;
  mode: 'create' | 'edit' | 'view';
  initialData?: Partial<ProductFormData>;
}

export const ProductModal: React.FC<ProductModalProps> = ({
  isOpen,
  onClose,
  onSubmit,
  mode,
  initialData = {}
}) => {
  const [formData, setFormData] = useState<ProductFormData>({
    title: '',
    price: 0,
    quantity: 0,
    barcode: '',
    type: 'book',
    weight: 0,
    rushOrderSupported: false,
    introduction: ''
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    if (isOpen) {
      if (initialData && Object.keys(initialData).length > 0) {
        setFormData({
          title: initialData.title || '',
          price: initialData.price || 0,
          quantity: initialData.quantity || 0,
          barcode: initialData.barcode || '',
          type: initialData.type || 'book',
          weight: initialData.weight || 0,
          rushOrderSupported: initialData.rushOrderSupported || false,
          introduction: initialData.introduction || ''
        });
      } else if (mode === 'create') {
        setFormData({
          title: '',
          price: 0,
          quantity: 0,
          barcode: '',
          type: 'book',
          weight: 0,
          rushOrderSupported: false,
          introduction: ''
        });
      }
      setError('');
    }
  }, [isOpen, mode]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    try {
      await onSubmit(formData);
      onClose();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to save product');
    } finally {
      setIsLoading(false);
    }
  };

  const handleInputChange = (field: keyof ProductFormData, value: string | number | boolean) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  if (!isOpen) return null;

  const isReadOnly = mode === 'view';
  console.log(isReadOnly);
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <Card className="w-full max-w-lg max-h-[90vh] flex flex-col">
        <CardHeader className="flex-shrink-0">
          <div className="flex items-center justify-between">
            <CardTitle>
              {mode === 'create' && 'Add New Product'}
              {mode === 'edit' && 'Edit Product'}
              {mode === 'view' && 'Product Details'}
            </CardTitle>
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
        <CardContent className="flex-1 overflow-y-auto">
          {error && (
            <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-md">
              <p className="text-sm text-red-600">{error}</p>
            </div>
          )}
          
          <form onSubmit={handleSubmit} className="space-y-3">
            <div>
              <label className="block text-sm font-medium mb-1">Title</label>
              <Input
                type="text"
                value={formData.title}
                onChange={(e) => handleInputChange('title', e.target.value)}
                placeholder="Enter product title"
                disabled={isReadOnly}
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">Price (VND)</label>
              <Input
                type="number"
                value={formData.price}
                onChange={(e) => handleInputChange('price', parseFloat(e.target.value) || 0)}
                placeholder="Enter price"
                disabled={isReadOnly}
                min="0"
                required
              />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-sm font-medium mb-1">Quantity</label>
                <Input
                  type="number"
                  value={formData.quantity}
                  onChange={(e) => handleInputChange('quantity', parseInt(e.target.value) || 0)}
                  placeholder="Quantity"
                  disabled={isReadOnly}
                  min="0"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Weight (kg)</label>
                <Input
                  type="number"
                  value={formData.weight || ''}
                  onChange={(e) => handleInputChange('weight', parseFloat(e.target.value) || 0)}
                  placeholder="Weight"
                  disabled={isReadOnly}
                  min="0"
                  step="0.01"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-sm font-medium mb-1">Barcode</label>
                <Input
                  type="text"
                  value={formData.barcode}
                  onChange={(e) => handleInputChange('barcode', e.target.value)}
                  placeholder="Barcode"
                  disabled={isReadOnly}
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Product Type</label>
                <select
                  value={formData.type}
                  onChange={(e) => handleInputChange('type', e.target.value)}
                  disabled={isReadOnly}
                  className="w-full px-3 py-2 border border-input bg-background rounded-md text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                  required
                >
                  <option value="book">📚 Book</option>
                  <option value="cd">💿 CD</option>
                  <option value="dvd">📀 DVD</option>
                  <option value="lp">🎵 LP</option>
                </select>
              </div>
            </div>

            <div>
              <label className="flex items-center gap-2 text-sm font-medium">
                <input
                  type="checkbox"
                  checked={formData.rushOrderSupported || false}
                  onChange={(e) => handleInputChange('rushOrderSupported', e.target.checked)}
                  disabled={isReadOnly}
                  className="rounded border-input"
                />
                Rush Order Supported
              </label>
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">Introduction</label>
              <textarea
                value={formData.introduction || ''}
                onChange={(e) => handleInputChange('introduction', e.target.value)}
                placeholder="Enter product introduction"
                disabled={isReadOnly}
                rows={2}
                className="w-full px-3 py-2 border border-input bg-background rounded-md text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 resize-none"
              />
            </div>

            <div className="flex justify-end gap-2 pt-4">
              <Button type="button" variant="outline" onClick={onClose} disabled={isLoading}>
                {isReadOnly ? 'Close' : 'Cancel'}
              </Button>
              {!isReadOnly && (
                <Button type="submit" disabled={isLoading}>
                  {isLoading ? (
                    <>
                      <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                      {mode === 'edit' ? 'Updating...' : 'Creating...'}
                    </>
                  ) : (
                    mode === 'edit' ? 'Update Product' : 'Create Product'
                  )}
                </Button>
              )}
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};