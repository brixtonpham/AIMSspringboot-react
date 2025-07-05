import React from 'react';
import { Input } from '../ui/input';

export interface LPFormData {
  artist?: string;
  recordLabel?: string;
  musicType?: string;
  releaseDate?: string;
  tracklist?: string;
  rpm?: number;
  sizeInches?: number;
  vinylCondition?: string;
  sleeveCondition?: string;
}

interface LPFieldsProps {
  data: LPFormData;
  onChange: (field: keyof LPFormData, value: string | number) => void;
  isReadOnly?: boolean;
}

export const LPFields: React.FC<LPFieldsProps> = ({ data, onChange, isReadOnly = false }) => {
  return (
    <div className="space-y-3">
      <h3 className="text-sm font-semibold text-gray-700 border-b pb-1">LP (Vinyl Record) Details</h3>
      
      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="block text-sm font-medium mb-1">Artist</label>
          <Input
            type="text"
            value={data.artist || ''}
            onChange={(e) => onChange('artist', e.target.value)}
            placeholder="e.g., The Beatles, Pink Floyd"
            disabled={isReadOnly}
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Music Type/Genre</label>
          <Input
            type="text"
            value={data.musicType || ''}
            onChange={(e) => onChange('musicType', e.target.value)}
            placeholder="e.g., Rock, Jazz, Classical"
            disabled={isReadOnly}
          />
        </div>
      </div>

      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="block text-sm font-medium mb-1">Record Label</label>
          <Input
            type="text"
            value={data.recordLabel || ''}
            onChange={(e) => onChange('recordLabel', e.target.value)}
            placeholder="e.g., Capitol Records, Blue Note"
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
          <label className="block text-sm font-medium mb-1">RPM</label>
          <select
            value={data.rpm || ''}
            onChange={(e) => onChange('rpm', parseInt(e.target.value) || 0)}
            disabled={isReadOnly}
            className="w-full px-3 py-2 border border-input bg-background rounded-md text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
          >
            <option value="">Select RPM</option>
            <option value="33">33⅓ RPM (LP)</option>
            <option value="45">45 RPM (Single)</option>
            <option value="78">78 RPM (Vintage)</option>
          </select>
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Size (inches)</label>
          <select
            value={data.sizeInches || ''}
            onChange={(e) => onChange('sizeInches', parseFloat(e.target.value) || 0)}
            disabled={isReadOnly}
            className="w-full px-3 py-2 border border-input bg-background rounded-md text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
          >
            <option value="">Select size</option>
            <option value="7">7" (Single)</option>
            <option value="10">10" (EP)</option>
            <option value="12">12" (LP)</option>
          </select>
        </div>
      </div>

      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="block text-sm font-medium mb-1">Vinyl Condition</label>
          <select
            value={data.vinylCondition || ''}
            onChange={(e) => onChange('vinylCondition', e.target.value)}
            disabled={isReadOnly}
            className="w-full px-3 py-2 border border-input bg-background rounded-md text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
          >
            <option value="">Select condition</option>
            <option value="Mint">Mint (M)</option>
            <option value="Near Mint">Near Mint (NM)</option>
            <option value="Very Good Plus">Very Good Plus (VG+)</option>
            <option value="Very Good">Very Good (VG)</option>
            <option value="Good Plus">Good Plus (G+)</option>
            <option value="Good">Good (G)</option>
            <option value="Fair">Fair (F)</option>
            <option value="Poor">Poor (P)</option>
          </select>
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Sleeve Condition</label>
          <select
            value={data.sleeveCondition || ''}
            onChange={(e) => onChange('sleeveCondition', e.target.value)}
            disabled={isReadOnly}
            className="w-full px-3 py-2 border border-input bg-background rounded-md text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
          >
            <option value="">Select condition</option>
            <option value="Mint">Mint (M)</option>
            <option value="Near Mint">Near Mint (NM)</option>
            <option value="Very Good Plus">Very Good Plus (VG+)</option>
            <option value="Very Good">Very Good (VG)</option>
            <option value="Good Plus">Good Plus (G+)</option>
            <option value="Good">Good (G)</option>
            <option value="Fair">Fair (F)</option>
            <option value="Poor">Poor (P)</option>
          </select>
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium mb-1">Track List</label>
        <textarea
          value={data.tracklist || ''}
          onChange={(e) => onChange('tracklist', e.target.value)}
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