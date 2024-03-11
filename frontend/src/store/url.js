import { defineStore } from 'pinia'

export const useUrlStore = defineStore({
  id: 'url',
  state: () => ({
    lastUrl: '/login',
  }),
  getters: {
    getLastUrl(state) {
      return state.lastUrl
    },
  },
  actions: {
    init() {
      const _lastUrl = sessionStorage.getItem('lastUrl') || ''

      if (_lastUrl !== '') {
        this.lastUrl = _lastUrl
      }
    },
    setLastUrl(data) {
      this.lastUrl = data
      sessionStorage.setItem('lastUrl', data)
    },
  },
})
