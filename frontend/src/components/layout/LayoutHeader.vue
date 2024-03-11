<template>
  <div class="layout-topbar">
    <router-link :to="userStore.role === 'ADMIN' ? '/routes/shop/list' : '/home'" class="layout-topbar-logo">
      <span>VEGETARIAN</span>
    </router-link>
    <Button
      v-if="userStore.role === 'USER'"
      class="p-ml-2 help-button"
      icon="fa fa-circle-question"
      severity="success"
      text
      rounded
      style="font-size: 2rem"
      @click="onShowVeganType"
    />
    <button
      v-if="userStore.role === 'ADMIN'"
      class="p-link layout-menu-button layout-topbar-button"
      @click="onMenuToggle"
    >
      <i class="pi pi-bars"></i>
    </button>
    <button v-if="userStore.role === 'ADMIN'" class="p-link layout-topbar-menu-button layout-topbar-button">
      <i class="pi pi-ellipsis-v"></i>
    </button>
    <ul class="layout-topbar-menu hidden lg:flex origin-top">
      <li class="p-pr-4">
        <Button
          v-tooltip.bottom="'로그인 연장'"
          :label="formatTime(tokenExpired)"
          class="topbar-button-expired"
          text
          @click="loginExtend"
        />
      </li>
      <li v-if="userStore.role === 'USER'" class="p-pr-3">
        <i
          v-tooltip.bottom="'예약 알림'"
          v-badge="reservationList.length"
          class="fa fa-bell p-overlay-badge"
          :class="{ 'shake-button': reservationTodayList.length > 0 }"
          style="font-size: 1.5rem; cursor: pointer; top: 10px"
          @click="onAlarmToggle($event)"
        />
        <OverlayPanel ref="op">
          <div class="p-d-flex p-flex-column">
            <span style="font-size: 20px; font-weight: 600"
              >예약 <span style="color: rgb(147, 147, 150)">{{ reservationList.length }}</span
              >건</span
            >
            <table v-if="reservationTodayList.length > 0 && !isOpenReservationList" style="width: 500px">
              <thead>
                <tr>
                  <th style="width: 30%; text-align: start; padding: 7px">예약 일자</th>
                  <th style="width: 40%; text-align: start; padding: 7px">지점명</th>
                  <th style="width: 15%; text-align: start; padding: 7px">인원수</th>
                  <th style="width: 15%; text-align: start; padding: 7px">예약 취소</th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="(reserve, index) of reservationTodayList"
                  :key="index"
                  :style="{
                    opacity: onDisabledReserve(reserve) ? 0.7 : 1,
                    textDecoration: onDisabledReserve(reserve) ? 'line-through' : 'inherit',
                  }"
                >
                  <td style="padding: 7px">{{ reserve.startDate }} {{ reserve.startTime }}</td>
                  <td style="padding: 7px">{{ reserve.shop.name }}</td>
                  <td style="padding: 7px">{{ reserve.person }}명</td>
                  <td style="padding: 7px; text-align: center">
                    <Button
                      severity="danger"
                      icon="fa fa-ban"
                      text
                      :disabled="onDisabledReserve(reserve)"
                      @click="onDeleteReserve(reserve)"
                    />
                  </td>
                </tr>
              </tbody>
            </table>
            <Inplace v-model:active="isOpenReservationList" class="detail-reserve p-mt-3" :closable="true">
              <template #display>
                <span class="fa fa-search p-pr-2"></span>
                <span>전체 예약목록</span>
              </template>
              <template #content>
                <table v-if="reservationList.length > 0" style="width: 400px">
                  <thead>
                    <tr>
                      <th style="width: 40%; text-align: start; padding: 7px">지점명</th>
                      <th style="width: 20%; text-align: start; padding: 7px">인원수</th>
                      <th style="width: 20%; text-align: start; padding: 7px">예약 일자</th>
                      <th style="width: 20%; text-align: start; padding: 7px">예약 취소</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr
                      v-for="(reserve, index) of reservationList"
                      :key="index"
                      :style="{
                        opacity: onDisabledReserve(reserve) ? 0.7 : 1,
                        textDecoration: onDisabledReserve(reserve) ? 'line-through' : 'inherit',
                      }"
                    >
                      <td style="padding: 7px">{{ reserve.shop.name }}</td>
                      <td style="padding: 7px">{{ reserve.person }}명</td>
                      <td style="padding: 7px">{{ reserve.startDate }} {{ reserve.startTime }}</td>
                      <td style="padding: 7px; text-align: center">
                        <Button
                          severity="danger"
                          icon="fa fa-ban"
                          text
                          :disabled="onDisabledReserve(reserve)"
                          @click="onDeleteReserve(reserve)"
                        />
                      </td>
                    </tr>
                  </tbody>
                </table>
              </template>
            </Inplace>
          </div>
        </OverlayPanel>
      </li>
      <li>
        <Button
          v-tooltip.bottom="'로그아웃'"
          class="topbar-button-logout"
          text
          icon="fa fa-right-from-bracket"
          @click="logout"
        />
      </li>
    </ul>

    <!-- 비건 타입 모달 -->
    <Dialog v-model:visible="isShowVeganType" modal header="단계별 채식주의자" :style="{ width: '50rem' }">
      <template #default>
        <img src="@/assets/images/단계별 채식주의자.png" alt="단계별 채식주의자" />
      </template>
    </Dialog>

    <!-- 예약 관련 모달 -->
    <ConfirmDialog> </ConfirmDialog>
  </div>
</template>

<script>
import _ from 'lodash'
import moment from 'moment'
import { defineComponent, ref, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/store/user.js'
import { useToast } from 'vue-toastification'
import { useRouter } from 'vue-router'
import ConfirmDialog from 'primevue/confirmdialog'
import { useConfirm } from 'primevue/useconfirm'

const TOKEN_EXPIRED = import.meta.env.VITE_TOKEN_EXPIRED

export default defineComponent({
  name: 'LayoutHeader',
  components: { ConfirmDialog },
  emits: ['menu-toggle'],
  setup(props, { emit }) {
    /** @data setting */
    const axios = inject('$axios')
    const router = useRouter()
    const toast = useToast()
    const userStore = useUserStore()
    const confirm = useConfirm()

    /** @data component */
    const tokenExpired = ref(userStore.getTokenExpired)
    const tokenExpiredInterval = ref()

    const formatTime = (seconds) => {
      const duration = moment.duration(seconds, 'seconds')
      return moment.utc(duration.asMilliseconds()).format('mm:ss')
    }

    const decreaseTokenExpired = () => {
      if (tokenExpired.value > 0) {
        tokenExpired.value--
        userStore.setTokenExpired(tokenExpired.value)
      } else {
        toast.warning('로그인 연장 시간이 만료되었습니다.')
        clearInterval(tokenExpiredInterval.value)
        tokenExpiredInterval.value = null
        userStore.setTokenExpired(TOKEN_EXPIRED)
        router.push('/login')
      }
    }

    onMounted(() => {
      if (tokenExpiredInterval.value) {
        clearInterval(tokenExpiredInterval.value)
      }
      tokenExpiredInterval.value = setInterval(decreaseTokenExpired, 1000)
    })

    onUnmounted(() => {
      if (tokenExpiredInterval.value) {
        clearInterval(tokenExpiredInterval.value)
      }
    })

    const loginExtend = () => {
      userStore.loginExtension().then((res) => {
        if (res) {
          tokenExpired.value = TOKEN_EXPIRED
          toast.success('로그인 연장을 성공했습니다.')
        } else {
          toast.error('로그인 연장을 실패했습니다.')
        }
      })
    }
    const logout = () => {
      userStore.logout()
    }
    const onMenuToggle = (event) => {
      emit('menu-toggle', event)
    }

    return {
      axios,
      userStore,
      confirm,
      toast,
      tokenExpired,
      formatTime,
      logout,
      loginExtend,
      onMenuToggle,
      reservationList: ref([]),
      reservationTodayList: ref([]),
      isOpenReservationList: ref(false),
      isShowDelete: ref(false),
      isShowVeganType: ref(false),
    }
  },
  mounted() {
    this.eventBus.on('reserve-add', () => {
      this.getReservation()
    })

    this.getReservation()
  },
  methods: {
    onShowVeganType() {
      this.isShowVeganType = true
    },
    getReservation() {
      this.axios.get('/api/reservation').then((res) => {
        this.reservationList = _.filter(
          res.data.content,
          (item) => item.user.id.toString() === this.userStore.uid.toString(),
        )
        this.reservationTodayList = _.filter(
          res.data.content,
          (item) =>
            item.user.id.toString() === this.userStore.uid.toString() &&
            item.startDate === moment().format('YYYY-MM-DD'),
        )
      })
    },
    onAlarmToggle(event) {
      this.getReservation()
      this.isOpenReservationList = false
      this.$refs.op.toggle(event)
    },
    onDisabledReserve(data) {
      const reserveTime = `${data.startDate} ${data.startTime}`
      return moment().isSameOrAfter(reserveTime)
    },
    onDeleteReserve(data) {
      this.confirm.require({
        message: '예약을 취소하시겠습니까?',
        header: '예약 취소',
        icon: 'fa fa-exclamation-triangle',
        acceptClass: 'p-button-danger',
        rejectClass: 'p-button-danger p-button-outlined',
        rejectLabel: '아니오',
        acceptLabel: '예',
        accept: () => {
          this.axios.delete(`/api/reservation/${data.id}`).then(() => {
            this.toast.success('예약을 취소했습니다.')
            this.getReservation()
          })
        },
        reject: () => {},
      })
    },
  },
})
</script>

<style lang="scss" scoped>
.layout-topbar {
  .p-button.p-button-success:enabled:focus {
    box-shadow: none;
  }
  .p-button.p-button-success:not(:disabled):focus {
    box-shadow: none;
  }
  .p-button:focus {
    box-shadow: none;
  }
  .topbar-button-expired {
    display: inline-flex;
    justify-content: center;
    align-items: center;
    position: relative;
    color: var(--text-color-secondary);
    width: 3rem;
    height: 3rem;
    cursor: pointer;

    &:hover {
      color: var(--text-color);
      background-color: var(--surface-hover);
    }
  }

  .topbar-button-logout {
    display: inline-flex;
    justify-content: center;
    align-items: center;
    position: relative;
    color: var(--text-color-secondary);
    border-radius: 50%;
    width: 3rem;
    height: 3rem;
    font-size: 1.5rem;
    cursor: pointer;

    &:hover {
      color: var(--text-color);
      background-color: var(--surface-hover);
    }
  }

  .shake-button {
    animation: shake 2s ease-in-out infinite;
  }

  @keyframes shake {
    0%,
    100% {
      transform: translateX(0);
    }
    10%,
    30%,
    50%,
    70%,
    90% {
      transform: translateX(0) rotate(-20deg);
    }
    20%,
    40%,
    60%,
    80% {
      transform: translateX(0) rotate(20deg);
    }
  }
}
</style>
