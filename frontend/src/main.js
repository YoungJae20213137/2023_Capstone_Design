import 'primevue/resources/themes/lara-light-indigo/theme.css'
import 'primevue/resources/primevue.min.css'
import 'primeicons/primeicons.css'
import 'primeflex/primeflex.min.css'
import 'vue-toastification/dist/index.css'
import '@/assets/styles.scss'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import axiosInstance from './plugins/axios'
import PrimeVue from 'primevue/config'
import BadgeDirective from 'primevue/badgedirective'
import Ripple from 'primevue/ripple'
import Tooltip from 'primevue/tooltip'
import ConfirmationService from 'primevue/confirmationservice'
import Toast, { POSITION } from 'vue-toastification'
import VueCryptojs from 'vue-cryptojs'
import PerfectScrollbar from 'vue3-perfect-scrollbar'
import 'vue3-perfect-scrollbar/dist/vue3-perfect-scrollbar.css'
import mitt from 'mitt'

const app = createApp(App)
const pinia = createPinia()
const eventBus = mitt()

pinia.use(({ store }) => {
  store.$router = router
})

app.use(axiosInstance)
app.use(pinia)
app.use(router)
app.use(PrimeVue, { ripple: true })
app.use(Toast, {
  position: POSITION.TOP_RIGHT,
  transition: 'Vue-Toastification__bounce',
  maxToasts: 20,
  newestOnTop: true,
  timeout: 1994,
  closeOnClick: true,
  draggable: false,
  showCloseButtonOnHover: false,
  hideProgressBar: true,
  closeButton: false,
})
app.use(ConfirmationService)
app.use(VueCryptojs)
app.use(PerfectScrollbar)

app.directive('tooltip', Tooltip)
app.directive('badge', BadgeDirective)
app.directive('ripple', Ripple)

app.config.globalProperties.$axios = axiosInstance
app.config.globalProperties.eventBus = eventBus

app.mount('#app')
