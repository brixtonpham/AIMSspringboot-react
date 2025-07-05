import React from 'react';
import { Input } from '../ui/input';

export interface DVDFormData {
  releaseDate?: string;
  dvdType?: string;
  genre?: string;
  studio?: string;
  directors?: string;
  durationMinutes?: number;
  rating?: string;
}

interface DVDFieldsProps {
  data: DVDFormData;
  onChange: (field: keyof DVDFormData, value: string | number) => void;
  isReadOnly?: boolean;
}

export const DVDFields: React.FC<DVDFieldsProps> = ({ data, onChange, isReadOnly = false }) => {
  return (
    <div className="space-y-3">
      <h3 className="text-sm font-semibold text-gray-700 border-b pb-1">DVD Details</h3>
      
      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="block text-sm font-medium mb-1">Genre</label>
          <Input
            type="text"
            value={data.genre || ''}
            onChange={(e) => onChange('genre', e.target.value)}
            placeholder="e.g., Action, Comedy, Drama, Horror"
            disabled={isReadOnly}
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Release Date</label>
          <Input
            type="date"
            value={data.releaseDate || ''}
            onChange={(e) => onChange('releaseDate', e.target.value)}
            disabled={isReadOnly}
          />
        </div>
      </div>

      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="block text-sm font-medium mb-1">DVD Type</label>
          <select
            value={data.dvdType || ''}
            onChange={(e) => onChange('dvdType', e.target.value)}
            disabled={isReadOnly}
            className="w-full px-3 py-2 border border-input bg-background rounded-md text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
          >
            <option value="">Select DVD type</option>
            <option value="DVD">Standard DVD</option>
            <option value="Blu-ray">Blu-ray</option>
            <option value="4K Ultra HD">4K Ultra HD</option>
            <option value="HD-DVD">HD-DVD</option>
          </select>
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Duration (minutes)</label>
          <Input
            type="number"
            value={data.durationMinutes || ''}
            onChange={(e) => onChange('durationMinutes', parseInt(e.target.value) || 0)}
            placeholder="Runtime in minutes"
            disabled={isReadOnly}
            min="1"
          />
        </div>
      </div>

      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="block text-sm font-medium mb-1">Studio</label>
          <Input
            type="text"
            value={data.studio || ''}
            onChange={(e) => onChange('studio', e.target.value)}
            placeholder="e.g., Warner Bros, Universal Studios"
            disabled={isReadOnly}
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Rating</label>
          <select
            value={data.rating || ''}
            onChange={(e) => onChange('rating', e.target.value)}
            disabled={isReadOnly}
            className="w-full px-3 py-2 border border-input bg-background rounded-md text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
          >
            <option value="">Select rating</option>
            <option value="G">G - General Audiences</option>
            <option value="PG">PG - Parental Guidance</option>
            <option value="PG-13">PG-13 - Parents Strongly Cautioned</option>
            <option value="R">R - Restricted</option>
            <option value="NC-17">NC-17 - Adults Only</option>
            <option value="NR">NR - Not Rated</option>
          </select>
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium mb-1">Directors</label>
        <Input
          type="text"
          value={data.directors || ''}
          onChange={(e) => onChange('directors', e.target.value)}
          placeholder="e.g., Steven Spielberg, Christopher Nolan (separate with commas)"
          disabled={isReadOnly}
        />
      </div>
    </div>
  );
};