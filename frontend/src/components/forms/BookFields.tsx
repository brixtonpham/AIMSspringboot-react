import React from 'react';
import { Input } from '../ui/input';

export interface BookFormData {
  genre?: string;
  pageCount?: number;
  publicationDate?: string;
  authors?: string;
  publishers?: string;
  coverType?: string;
}

interface BookFieldsProps {
  data: BookFormData;
  onChange: (field: keyof BookFormData, value: string | number) => void;
  isReadOnly?: boolean;
}

export const BookFields: React.FC<BookFieldsProps> = ({ data, onChange, isReadOnly = false }) => {
  return (
    <div className="space-y-3">
      <h3 className="text-sm font-semibold text-gray-700 border-b pb-1">Book Details</h3>
      
      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="block text-sm font-medium mb-1">Genre</label>
          <Input
            type="text"
            value={data.genre || ''}
            onChange={(e) => onChange('genre', e.target.value)}
            placeholder="e.g., Fiction, Non-fiction, Mystery"
            disabled={isReadOnly}
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Page Count</label>
          <Input
            type="number"
            value={data.pageCount || ''}
            onChange={(e) => onChange('pageCount', parseInt(e.target.value) || 0)}
            placeholder="Number of pages"
            disabled={isReadOnly}
            min="1"
          />
        </div>
      </div>

      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="block text-sm font-medium mb-1">Publication Date</label>
          <Input
            type="date"
            value={data.publicationDate || ''}
            onChange={(e) => onChange('publicationDate', e.target.value)}
            disabled={isReadOnly}
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Cover Type</label>
          <select
            value={data.coverType || ''}
            onChange={(e) => onChange('coverType', e.target.value)}
            disabled={isReadOnly}
            className="w-full px-3 py-2 border border-input bg-background rounded-md text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
          >
            <option value="">Select cover type</option>
            <option value="hardcover">Hardcover</option>
            <option value="paperback">Paperback</option>
            <option value="spiral">Spiral Bound</option>
            <option value="board">Board Book</option>
          </select>
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium mb-1">Authors</label>
        <Input
          type="text"
          value={data.authors || ''}
          onChange={(e) => onChange('authors', e.target.value)}
          placeholder="e.g., John Doe, Jane Smith (separate multiple authors with commas)"
          disabled={isReadOnly}
        />
      </div>

      <div>
        <label className="block text-sm font-medium mb-1">Publishers</label>
        <Input
          type="text"
          value={data.publishers || ''}
          onChange={(e) => onChange('publishers', e.target.value)}
          placeholder="Publisher name"
          disabled={isReadOnly}
        />
      </div>
    </div>
  );
};