import axios from 'axios'
import { useUserStore } from '@/store/user.js'

export default {
  install: (app) => {
    const axiosInstance = axios.create({
      baseURL: import.meta.env.VITE_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
    })

    axiosInstance.interceptors.request.use(
      (config) => {
        const useStore = useUserStore()
        config.headers.Authorization = `Bearer ${useStore.accessToken}`
        return config
      },
      (error) => {
        return Promise.reject(error)
      },
    )

    axiosInstance.interceptors.response.use(
      (res) => {
        return res
      },
      (error) => {
        return Promise.reject(error)
      },
    )

    app.config.globalProperties.$axios = axiosInstance
    app.provide('$axios', axiosInstance)
  },
}
