import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { CartState, CartItemWithProduct, Product } from '../types/api';

interface CartStore extends CartState {
  addItem: (product: Product, quantity?: number) => void;
  removeItem: (productId: number) => void;
  updateQuantity: (productId: number, quantity: number) => void;
  clearCart: () => void;
  getItemQuantity: (productId: number) => number;
}

export const useCartStore = create<CartStore>()(
  persist(
    (set, get) => ({
      items: [],
      total: 0,
      itemCount: 0,

      addItem: (product: Product, quantity = 1) => {
        set((state) => {
          const existingItem = state.items.find(item => item.product.productId === product.productId);
          
          let newItems: CartItemWithProduct[];
          
          if (existingItem) {
            newItems = state.items.map(item =>
              item.product.productId === product.productId
                ? { ...item, quantity: item.quantity + quantity }
                : item
            );
          } else {
            newItems = [...state.items, { product, quantity }];
          }

          const newTotal = newItems.reduce((sum, item) => sum + (item.product.price * item.quantity), 0);
          const newItemCount = newItems.reduce((sum, item) => sum + item.quantity, 0);

          return {
            items: newItems,
            total: newTotal,
            itemCount: newItemCount,
          };
        });
      },

      removeItem: (productId: number) => {
        set((state) => {
          const newItems = state.items.filter(item => item.product.productId !== productId);
          const newTotal = newItems.reduce((sum, item) => sum + (item.product.price * item.quantity), 0);
          const newItemCount = newItems.reduce((sum, item) => sum + item.quantity, 0);

          return {
            items: newItems,
            total: newTotal,
            itemCount: newItemCount,
          };
        });
      },

      updateQuantity: (productId: number, quantity: number) => {
        if (quantity <= 0) {
          get().removeItem(productId);
          return;
        }

        set((state) => {
          const newItems = state.items.map(item =>
            item.product.productId === productId
              ? { ...item, quantity }
              : item
          );
          
          const newTotal = newItems.reduce((sum, item) => sum + (item.product.price * item.quantity), 0);
          const newItemCount = newItems.reduce((sum, item) => sum + item.quantity, 0);

          return {
            items: newItems,
            total: newTotal,
            itemCount: newItemCount,
          };
        });
      },

      clearCart: () => {
        set({
          items: [],
          total: 0,
          itemCount: 0,
        });
      },

      getItemQuantity: (productId: number) => {
        const item = get().items.find(item => item.product.productId === productId);
        return item?.quantity || 0;
      },
    }),
    {
      name: 'cart-storage',
    }
  )
);