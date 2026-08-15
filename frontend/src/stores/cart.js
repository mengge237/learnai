import { defineStore } from 'pinia'

const CART_KEY = 'learnai_cart'

/**
 * 购物车（唯一实现，Pinia + localStorage 持久化，修复旧系统双购物车问题）。
 * item: { modelId, name, price, previewUrl, quantity, licenseType }
 */
export const useCartStore = defineStore('cart', {
  state: () => ({
    items: JSON.parse(localStorage.getItem(CART_KEY) || '[]'),
  }),

  getters: {
    totalCount: (s) => s.items.reduce((sum, it) => sum + it.quantity, 0),
    totalPrice: (s) => s.items.reduce((sum, it) => sum + Number(it.price || 0) * it.quantity, 0),
  },

  actions: {
    persist() {
      localStorage.setItem(CART_KEY, JSON.stringify(this.items))
    },
    add(model, quantity = 1, licenseType = '个人') {
      const exist = this.items.find((it) => it.modelId === model.modelId)
      if (exist) {
        exist.quantity += quantity
        exist.licenseType = licenseType
      } else {
        this.items.push({
          modelId: model.modelId,
          name: model.name,
          price: model.price,
          previewUrl: model.previewUrl,
          quantity,
          licenseType,
        })
      }
      this.persist()
    },
    updateQuantity(modelId, quantity) {
      const it = this.items.find((i) => i.modelId === modelId)
      if (it) {
        it.quantity = Math.max(1, quantity)
        this.persist()
      }
    },
    remove(modelId) {
      this.items = this.items.filter((i) => i.modelId !== modelId)
      this.persist()
    },
    clear() {
      this.items = []
      this.persist()
    },
  },
})
