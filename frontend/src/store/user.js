import { defineStore } from 'pinia'
import router from '@/router/index.js'
import { useToast } from 'vue-toastification'
import { jwtDecode } from 'jwt-decode'
import CryptoJS from 'crypto-js'
import { inject } from 'vue'

const toast = useToast()
const KEY = import.meta.env.VITE_SECRET_KEY

export const useUserStore = defineStore({
  id: 'user',
  state: () => ({
    authorization: '',
    accessToken: '',
    refreshToken: '',
    user: {},
    uid: '',
    role: '',
    tokenExpired: Number(import.meta.env.VITE_TOKEN_EXPIRED),
    $axios: inject('$axios'),
  }),
  getters: {
    getUser(state) {
      return state
    },
    getTokenExpired(state) {
      return state.tokenExpired
    },
  },
  actions: {
    /** #1. sessionStorage 연동 */
    init() {
      const _authorization = sessionStorage.getItem('authorization') || ''
      const _accessToken = sessionStorage.getItem('accessToken') || ''
      const _refreshToken = sessionStorage.getItem('refreshToken') || ''
      const _user = sessionStorage.getItem('user') || {}
      const _uid = sessionStorage.getItem('uid') || ''
      const _role = sessionStorage.getItem('role') || ''
      const CRYPTO_KEY = `${KEY}_${_uid}`

      if (_accessToken !== '') {
        this.authorization = CryptoJS.AES.decrypt(_authorization, CRYPTO_KEY).toString(CryptoJS.enc.Utf8)
        this.accessToken = CryptoJS.AES.decrypt(_accessToken, CRYPTO_KEY).toString(CryptoJS.enc.Utf8)
        this.refreshToken = CryptoJS.AES.decrypt(_refreshToken, CRYPTO_KEY).toString(CryptoJS.enc.Utf8)
        this.user = _user
        this.uid = _uid
        this.role = _role
      }
    },
    /** #1. JWT 토큰 */
    setToken(data) {
      const decoded = jwtDecode(data.accessToken)
      const authorization = `Bearer ${data.accessToken}`
      const CRYPTO_KEY = `${KEY}_${decoded.id}`

      this.authorization = authorization
      this.refreshToken = data.refreshToken
      this.user = decoded
      this.uid = decoded.id
      this.role = decoded.role

      let encryptedAccessToken = ''
      let encryptedRefreshToken = ''
      let encryptedAuthorization = ''

      if (data.accessToken !== '') {
        encryptedAccessToken = CryptoJS.AES.encrypt(data.accessToken, CRYPTO_KEY).toString()
        encryptedRefreshToken = CryptoJS.AES.encrypt(data.refreshToken, CRYPTO_KEY).toString()
        encryptedAuthorization = CryptoJS.AES.encrypt(authorization, CRYPTO_KEY).toString()
      }

      sessionStorage.setItem('accessToken', encryptedAccessToken)
      sessionStorage.setItem('refreshToken', encryptedRefreshToken)
      sessionStorage.setItem('authorization', encryptedAuthorization)
      sessionStorage.setItem('user', JSON.stringify(this.user))
      sessionStorage.setItem('uid', this.uid)
      sessionStorage.setItem('role', this.role)
    },
    setTokenClear() {
      // sessionStorage 초기화
      sessionStorage.clear()

      // state 초기화
      this.authorization = ''
      this.accessToken = ''
      this.refreshToken = ''
      this.user = {}
      this.uid = ''
      this.role = ''
    },
    setTokenExpired(data) {
      this.tokenExpired = Number(data)
    },
    /** #1. 회원가입 */
    async register(params) {
      const _params = {
        userId: params.id.value,
        password: params.password.value,
      }
      await this.$axios
        .post('/signup', _params)
        .then((res) => {
          if (res.status === 200 || res.status === 201) {
            toast.success('회원가입 성공')
            router.push('/login')
          } else {
            toast.error('회원가입 실패')
          }
        })
        .catch(() => {
          toast.error('회원가입 실패')
        })
    },
    /** #1. 로그인 */
    async login(params) {
      const _params = {
        userId: params.id.value,
        password: params.password.value,
      }
      await this.$axios
        .post('/login', _params)
        .then(async (res) => {
          if (res.status === 200 || res.status === 201) {
            this.setToken(res.data)
            await router.push('/routes/shop/list')
            toast.success('로그인 성공')

            if (params.checked.value) {
              this.rememberUserId(params.id.value)
            } else {
              this.notRememberUserId()
            }
          } else {
            toast.error('로그인 실패')
          }
        })
        .catch(() => {
          toast.error('로그인 실패')
        })
    },
    /** 로그인 시, 아이디 기억하기 */
    rememberUserId(userId) {
      localStorage.setItem('userId', userId)
    },
    notRememberUserId() {
      localStorage.removeItem('userId')
    },
    /** #1. 로그아웃 */
    async logout() {
      this.setTokenClear()
      await router.push('/login')
      toast.success('로그아웃 성공')
    },
    /** #1. 로그인 연장 기능 */
    async loginExtension() {
      const _params = {
        accessToken: this.accessToken,
        refreshToken: this.refreshToken,
      }
      let checkToken = false

      await this.$axios
        .post('/api/token/check', _params)
        .then(() => {
          checkToken = true
        })
        .catch(() => {
          checkToken = false
        })
      return checkToken
    },
  },
})
