import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { UserProfile, LoginRequest } from '../types/api';
import { authApi } from '../services/api';

interface AuthState {
  user: UserProfile | null;
  isAuthenticated: boolean;
  login: (credentials: LoginRequest) => Promise<UserProfile>;
  logout: () => Promise<void>;
  getCurrentUser: () => Promise<void>;
  initializeAuth: () => Promise<void>;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      isAuthenticated: false,

      login: async (credentials: LoginRequest): Promise<UserProfile> => {
        try {
          const response = await authApi.login(credentials);
          localStorage.setItem('auth_token', response.token);
          set({ user: response.user, isAuthenticated: true });
          return response.user;
        } catch (error) {
          // Clear any existing auth state on login failure
          localStorage.removeItem('auth_token');
          set({ user: null, isAuthenticated: false });
          throw error;
        }
      },

      logout: async () => {
        try {
          await authApi.logout();
        } catch (error) {
          console.error('Logout API call failed:', error);
        } finally {
          // Always clear the state and token
          localStorage.removeItem('auth_token');
          set({ user: null, isAuthenticated: false });
        }
      },

      getCurrentUser: async () => {
        try {
          const user = await authApi.getCurrentUser();
          set({ user, isAuthenticated: true });
        } catch (error) {
          localStorage.removeItem('auth_token');
          set({ user: null, isAuthenticated: false });
          throw error;
        }
      },

      initializeAuth: async () => {
        const token = localStorage.getItem('auth_token');
        if (token) {
          try {
            await get().getCurrentUser();
          } catch (error) {
            console.warn('Failed to initialize auth:', error);
            // Auth state already cleared by getCurrentUser
          }
        }
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({ user: state.user, isAuthenticated: state.isAuthenticated }),
    }
  )
);