import React, { useState, useEffect } from 'react';
import { X, User, Mail, Phone, MapPin, Shield, Lock, Unlock } from 'lucide-react';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';

interface UserFormData {
  name: string;
  email: string;
  phone?: string;
  address?: string;
  role: 'ADMIN' | 'MANAGER';
  isActive: boolean;
  password?: string;
  salary?: number;
}

interface UserModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: UserFormData) => Promise<void>;
  onPasswordUpdate?: (userId: number, newPassword: string) => Promise<void>;
  mode: 'create' | 'edit' | 'view';
  initialData?: Partial<UserFormData> & {
    userId?: number;
    createdAt?: string;
    updatedAt?: string;
  };
  onBlockUser?: (userId: number, reason?: string) => Promise<void>;
  onUnblockUser?: (userId: number) => Promise<void>;
}

export const UserModal: React.FC<UserModalProps> = ({
  isOpen,
  onClose,
  onSubmit,
  onPasswordUpdate,
  mode,
  initialData = {},
  onBlockUser,
  onUnblockUser
}) => {
  const [formData, setFormData] = useState<UserFormData>({
    name: '',
    email: '',
    phone: '',
    address: '',
    role: 'MANAGER',
    isActive: true,
    password: '',
    salary: 0
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string>('');
  const [emailError, setEmailError] = useState<string>('');
  const [isBlocking, setIsBlocking] = useState(false);
  const [isUnblocking, setIsUnblocking] = useState(false);
  const [isUpdatingPassword, setIsUpdatingPassword] = useState(false);
  const [passwordError, setPasswordError] = useState<string>('');

  useEffect(() => {
    if (isOpen) {
      if (initialData && Object.keys(initialData).length > 0) {
        setFormData({
          name: initialData.name || '',
          email: initialData.email || '',
          phone: initialData.phone || '',
          address: initialData.address || '',
          role: initialData.role || 'MANAGER',
          isActive: initialData.isActive !== undefined ? initialData.isActive : true,
          password: '',
          salary: initialData.salary || 0
        });
      } else if (mode === 'create') {
        setFormData({
          name: '',
          email: '',
          phone: '',
          address: '',
          role: 'MANAGER',
          isActive: true,
          password: '',
          salary: 0
        });
      }
      setError('');
      setEmailError('');
      setPasswordError('');
    }
  }, [isOpen, mode, initialData?.userId, initialData?.name, initialData?.email, initialData?.phone, initialData?.address, initialData?.role, initialData?.isActive, initialData?.salary]);

  const validateForm = () => {
    const errors: string[] = [];

    // Name validation
    if (!formData.name.trim()) {
      errors.push('Name is required');
    } else if (formData.name.length > 255) {
      errors.push('Name must not exceed 255 characters');
    }

    // Email validation
    if (!formData.email.trim()) {
      errors.push('Email is required');
    } else {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(formData.email)) {
        errors.push('Please enter a valid email address');
      } else if (formData.email.length > 255) {
        errors.push('Email must not exceed 255 characters');
      }
    }

    // Password validation
    if (mode === 'create' && !formData.password?.trim()) {
      errors.push('Password is required for new users');
    } else if (formData.password && formData.password.length < 6) {
      errors.push('Password must be at least 6 characters');
    }

    // Phone validation
    if (formData.phone && formData.phone.trim()) {
      const phoneRegex = /^[0-9]{10,11}$/;
      if (!phoneRegex.test(formData.phone.replace(/[^0-9]/g, ''))) {
        errors.push('Phone number must be 10-11 digits');
      }
    }

    // Salary validation
    if (formData.salary && formData.salary < 0) {
      errors.push('Salary cannot be negative');
    }

    return errors;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');
    setEmailError('');

    try {
      const validationErrors = validateForm();
      if (validationErrors.length > 0) {
        throw new Error(validationErrors.join(', '));
      }

      // Exclude password from regular form submission in edit mode
      const submitData = mode === 'edit' 
        ? { ...formData, password: undefined }
        : formData;
      
      await onSubmit(submitData);
      onClose();
    } catch (err: any) {
      const errorMessage = err?.response?.data?.message || err?.message || 'Failed to save user';
      
      // Check if it's an email-related error
      if (errorMessage.toLowerCase().includes('email') && errorMessage.toLowerCase().includes('exists')) {
        setEmailError('This email address is already in use. Please try another email address.');
      } else {
        setError(errorMessage);
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleInputChange = (field: keyof UserFormData, value: string | boolean | number) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    
    // Clear email error when user changes email
    if (field === 'email') {
      setEmailError('');
    }
    
    // Clear password error when user changes password
    if (field === 'password') {
      setPasswordError('');
    }
  };

  const handlePasswordUpdate = async () => {
    if (!onPasswordUpdate || !initialData.userId || !formData.password) return;
    
    // Validate password
    if (formData.password.length < 6) {
      setPasswordError('Password must be at least 6 characters long');
      return;
    }
    
    setIsUpdatingPassword(true);
    setPasswordError('');
    
    try {
      await onPasswordUpdate(initialData.userId, formData.password);
      // Clear password field after successful update
      setFormData(prev => ({ ...prev, password: '' }));
    } catch (error: any) {
      const errorMessage = error?.response?.data?.message || error?.message || 'Failed to update password';
      setPasswordError(errorMessage);
    } finally {
      setIsUpdatingPassword(false);
    }
  };

  const handleBlockUser = async () => {
    if (!onBlockUser || !initialData.userId) return;
    
    const reason = prompt('Please enter a reason for blocking this user:');
    if (reason === null) return; // User cancelled
    
    setIsBlocking(true);
    try {
      await onBlockUser(initialData.userId, reason);
      onClose();
    } catch (error) {
      console.error('Error blocking user:', error);
      setError('Failed to block user. Please try again.');
    } finally {
      setIsBlocking(false);
    }
  };

  const handleUnblockUser = async () => {
    if (!onUnblockUser || !initialData.userId) return;
    
    if (!window.confirm('Are you sure you want to unblock this user? They will regain access to their account.')) {
      return;
    }
    
    setIsUnblocking(true);
    try {
      await onUnblockUser(initialData.userId);
      onClose();
    } catch (error) {
      console.error('Error unblocking user:', error);
      setError('Failed to unblock user. Please try again.');
    } finally {
      setIsUnblocking(false);
    }
  };

  const formatDate = (dateString?: string) => {
    if (!dateString) return 'N/A';
    try {
      return new Date(dateString).toLocaleString('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch {
      return 'Invalid Date';
    }
  };

  if (!isOpen) return null;

  const isReadOnly = mode === 'view';

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <Card className="w-full max-w-lg max-h-[90vh] flex flex-col">
        <CardHeader className="flex-shrink-0">
          <div className="flex items-center justify-between">
            <CardTitle className="flex items-center gap-2">
              <User className="w-5 h-5" />
              {mode === 'create' && 'Add New User'}
              {mode === 'edit' && 'Edit User'}
              {mode === 'view' && 'User Details'}
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
          
          {/* User Info for View Mode */}
          {mode === 'view' && initialData && (
            <div className="mb-6 space-y-4">
              <div className="bg-gray-50 p-4 rounded-lg">
                <h3 className="font-semibold mb-3 flex items-center gap-2">
                  <User className="w-4 h-4" />
                  User Information
                </h3>
                <div className="grid grid-cols-2 gap-3 text-sm">
                  <div>
                    <span className="text-gray-600">User ID:</span>
                    <div className="font-medium">{initialData.userId || 'N/A'}</div>
                  </div>
                  <div>
                    <span className="text-gray-600">Status:</span>
                    <div className="font-medium">
                      <span className={`px-2 py-1 text-xs rounded-full ${
                        initialData.isActive ? 'bg-green-100 text-green-600' : 'bg-red-100 text-red-600'
                      }`}>
                        {initialData.isActive ? 'Active' : 'Inactive'}
                      </span>
                    </div>
                  </div>
                  <div>
                    <span className="text-gray-600">Created:</span>
                    <div className="font-medium">{formatDate(initialData.createdAt)}</div>
                  </div>
                  <div>
                    <span className="text-gray-600">Updated:</span>
                    <div className="font-medium">{formatDate(initialData.updatedAt)}</div>
                  </div>
                </div>
              </div>
            </div>
          )}
          
          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Name Field */}
            <div>
              <label className="block text-sm font-medium mb-1 flex items-center gap-2">
                <User className="w-4 h-4" />
                Full Name
              </label>
              <Input
                type="text"
                value={formData.name}
                onChange={(e) => handleInputChange('name', e.target.value)}
                placeholder="Enter full name"
                disabled={isReadOnly}
                required
                maxLength={255}
              />
            </div>

            {/* Email Field */}
            <div>
              <label className="block text-sm font-medium mb-1 flex items-center gap-2">
                <Mail className="w-4 h-4" />
                Email Address
              </label>
              <Input
                type="email"
                value={formData.email}
                onChange={(e) => handleInputChange('email', e.target.value)}
                placeholder="Enter email address"
                disabled={isReadOnly || mode === 'edit'}
                required
                maxLength={255}
                className={emailError ? 'border-red-300 focus:border-red-500 focus:ring-red-500' : ''}
              />
              {emailError && (
                <p className="text-sm text-red-600 mt-1">{emailError}</p>
              )}
            </div>

            {/* Password Field (only for create mode) */}
            {mode === 'create' && (
              <div>
                <label className="block text-sm font-medium mb-1">Password</label>
                <Input
                  type="password"
                  value={formData.password || ''}
                  onChange={(e) => handleInputChange('password', e.target.value)}
                  placeholder="Enter password (min 6 characters)"
                  disabled={isReadOnly}
                  required
                  minLength={6}
                />
              </div>
            )}

            {/* Password Field (optional for edit mode) */}
            {mode === 'edit' && (
              <div>
                <label className="block text-sm font-medium mb-1">
                  New Password
                </label>
                <div className="flex gap-2">
                  <Input
                    type="password"
                    value={formData.password || ''}
                    onChange={(e) => handleInputChange('password', e.target.value)}
                    placeholder="Enter new password (min 6 characters)"
                    disabled={isReadOnly}
                    minLength={6}
                    className={passwordError ? 'border-red-300 focus:border-red-500 focus:ring-red-500' : ''}
                  />
                  <Button
                    type="button"
                    onClick={handlePasswordUpdate}
                    disabled={isReadOnly || !formData.password || isUpdatingPassword}
                    className="whitespace-nowrap"
                  >
                    {isUpdatingPassword ? (
                      <>
                        <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                        Updating...
                      </>
                    ) : (
                      'Update Password'
                    )}
                  </Button>
                </div>
                {passwordError && (
                  <p className="text-sm text-red-600 mt-1">{passwordError}</p>
                )}
                <p className="text-sm text-gray-500 mt-1">
                  Password updates are processed separately and will send a confirmation email.
                </p>
              </div>
            )}

            {/* Phone Field */}
            <div>
              <label className="block text-sm font-medium mb-1 flex items-center gap-2">
                <Phone className="w-4 h-4" />
                Phone Number
              </label>
              <Input
                type="tel"
                value={formData.phone || ''}
                onChange={(e) => handleInputChange('phone', e.target.value)}
                placeholder="Enter phone number (10-11 digits)"
                disabled={isReadOnly}
                pattern="[0-9]{10,11}"
              />
            </div>

            {/* Address Field */}
            <div>
              <label className="block text-sm font-medium mb-1 flex items-center gap-2">
                <MapPin className="w-4 h-4" />
                Address
              </label>
              <textarea
                value={formData.address || ''}
                onChange={(e) => handleInputChange('address', e.target.value)}
                placeholder="Enter address"
                disabled={isReadOnly}
                rows={2}
                className="w-full px-3 py-2 border border-input bg-background rounded-md text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 resize-none"
              />
            </div>

            {/* Salary Field */}
            <div>
              <label className="block text-sm font-medium mb-1">
                Salary (VND)
              </label>
              <Input
                type="number"
                value={formData.salary || 0}
                onChange={(e) => handleInputChange('salary', parseFloat(e.target.value) || 0)}
                placeholder="Enter salary"
                disabled={isReadOnly}
                min="0"
                step="1000"
              />
            </div>

            {/* Role and Status */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium mb-1 flex items-center gap-2">
                  <Shield className="w-4 h-4" />
                  Role
                </label>
                <select
                  value={formData.role}
                  onChange={(e) => handleInputChange('role', e.target.value)}
                  disabled={isReadOnly}
                  className="w-full px-3 py-2 border border-input bg-background rounded-md text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                  required
                >
                  <option value="MANAGER">🏢 Manager</option>
                  <option value="ADMIN">👑 Admin</option>
                </select>
              </div>
              
              <div>
                <label className="block text-sm font-medium mb-1">Status</label>
                <div className="flex items-center gap-2 mt-2">
                  <input
                    type="checkbox"
                    checked={formData.isActive}
                    onChange={(e) => handleInputChange('isActive', e.target.checked)}
                    disabled={isReadOnly}
                    className="rounded border-input"
                  />
                  <span className="text-sm">Active User</span>
                </div>
              </div>
            </div>

            <div className="flex justify-between pt-4 border-t">
              <div className="flex gap-2">
                {/* Block/Unblock buttons for edit mode */}
                {mode === 'edit' && initialData.userId && onBlockUser && onUnblockUser && (
                  <>
                    {initialData.isActive ? (
                      <Button 
                        type="button"
                        onClick={handleBlockUser}
                        disabled={isBlocking}
                        className="bg-red-600 hover:bg-red-700 text-white"
                      >
                        {isBlocking ? (
                          <>
                            <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                            Blocking...
                          </>
                        ) : (
                          <>
                            <Lock className="w-4 h-4 mr-2" />
                            Block User
                          </>
                        )}
                      </Button>
                    ) : (
                      <Button 
                        type="button"
                        onClick={handleUnblockUser}
                        disabled={isUnblocking}
                        className="bg-green-600 hover:bg-green-700 text-white"
                      >
                        {isUnblocking ? (
                          <>
                            <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                            Unblocking...
                          </>
                        ) : (
                          <>
                            <Unlock className="w-4 h-4 mr-2" />
                            Unblock User
                          </>
                        )}
                      </Button>
                    )}
                  </>
                )}
              </div>
              
              <div className="flex gap-2">
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
                      <>
                        {mode === 'edit' ? 'Update User' : 'Create User'}
                      </>
                    )}
                  </Button>
                )}
              </div>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};

