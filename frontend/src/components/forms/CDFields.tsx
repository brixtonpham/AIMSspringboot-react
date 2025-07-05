import React from 'react';
import { Input } from '../ui/input';

export interface CDFormData {
  trackList?: string;
  genre?: string;
  recordLabel?: string;
  artists?: string;
  releaseDate?: string;
}

interface CDFieldsProps {
  data: CDFormData;
  onChange: (field: keyof CDFormData, value: string) => void;
  isReadOnly?: boolean;
}

export const CDFields: React.FC<CDFieldsProps> = ({ data, onChange, isReadOnly = false }) => {
  return (
    <div className="space-y-3">
      <h3 className="text-sm font-semibold text-gray-700 border-b pb-1">CD Details</h3>
      
      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="block text-sm font-medium mb-1">Genre</label>
          <Input
            type="text"
            value={data.genre || ''}
            onChange={(e) => onChange('genre', e.target.value)}
            placeholder="e.g., Rock, Pop, Classical, Jazz"
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
          <label className="block text-sm font-medium mb-1">Artists</label>
          <Input
            type="text"
            value={data.artists || ''}
            onChange={(e) => onChange('artists', e.target.value)}
            placeholder="e.g., The Beatles, John Lennon (separate with commas)"
            disabled={isReadOnly}
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Record Label</label>
          <Input
            type="text"
            value={data.recordLabel || ''}
            onChange={(e) => onChange('recordLabel', e.target.value)}
            placeholder="e.g., Capitol Records, Sony Music"
            disabled={isReadOnly}
          />
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium mb-1">Track List</label>
        <textarea
          value={data.trackList || ''}
          onChange={(e) => onChange('trackList', e.target.value)}
          placeholder="Enter track names separated by commas&#10;e.g., Track 1, Track 2, Track 3"
          disabled={isReadOnly}
          rows={3}
          className="w-full px-3 py-2 border border-input bg-background rounded-md text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 resize-none"
        />
        <p className="text-xs text-gray-500 mt-1">Separate multiple tracks with commas</p>
      </div>
    </div>
  );
};