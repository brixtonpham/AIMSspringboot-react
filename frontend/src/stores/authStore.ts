import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { AuthState, User, LoginRequest } from '../types/api';
import { authApi } from '../services/api';

interface AuthStore extends AuthState {
  login: (credentials: LoginRequest) => Promise<void>;
  logout: () => void;
  checkAuth: () => Promise<void>;
  setUser: (user: User) => void;
}

export const useAuthStore = create<AuthStore>()(
  persist(
    (set, get) => ({
      user: null,
      isAuthenticated: false,
      token: null,

      login: async (credentials: LoginRequest) => {
        try {
          const response = await authApi.login(credentials);
          set({
            user: response.user,
            isAuthenticated: true,
            token: response.token,
          });
        } catch (error) {
          console.error('Login failed:', error);
          throw error;
        }
      },

      logout: () => {
        authApi.logout();
        set({
          user: null,
          isAuthenticated: false,
          token: null,
        });
      },

      checkAuth: async () => {
        try {
          const user = await authApi.getCurrentUser();
          if (user) {
            set({
              user,
              isAuthenticated: true,
              token: localStorage.getItem('authToken'),
            });
          } else {
            get().logout();
          }
        } catch (error) {
          console.error('Auth check failed:', error);
          get().logout();
        }
      },

      setUser: (user: User) => {
        set({ user });
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({
        user: state.user,
        isAuthenticated: state.isAuthenticated,
        token: state.token,
      }),
    }
  )
);