<template>
  <div class="login-wrapper">
    <form>
      <div class="p-d-flex p-justify-center p-mb-4">
        <h2 style="color: white">로그인</h2>
      </div>
      <div class="p-d-flex p-flex-column p-mb-4">
        <label for="id" class="p-mb-1">아이디</label>
        <InputText id="id" v-model="id" type="text" placeholder="type your username" style="width: 250px" />
      </div>
      <div class="p-d-flex p-flex-column">
        <label for="password" class="p-mb-1">비밀번호</label>
        <Password
          id="password"
          v-model="password"
          :feedback="false"
          placeholder="type your password"
          :input-style="{ width: '250px' }"
          @keydown.enter="onEnterPassword"
        />
      </div>
      <div class="p-pt-4 p-d-flex">
        <Checkbox v-model="checked" :binary="true" />
        <span class="p-pl-2" style="margin-top: 2px">아이디 기억하기</span>
      </div>
      <div class="p-pt-3 p-d-flex">
        <Button class="p-mr-2" label="로그인" text style="width: 100%" @click="loginFn" />
        <Button class="p-ml-2" label="회원가입" text style="width: 100%" @click="registerFn" />
      </div>
    </form>
  </div>
</template>

<script>
import _ from 'lodash'
import { useUserStore } from '@/store/user'

export default defineComponent({
  setup() {
    const id = ref('')
    const password = ref('')
    const router = useRouter()
    const userStore = useUserStore()
    const checked = ref(false)
    const localUserId = localStorage.getItem('userId') || []
    if (!_.isEmpty(localUserId)) {
      id.value = localUserId
    }

    const loginFn = async () => {
      await userStore.login({ id, password, checked })
    }

    const registerFn = async () => {
      await router.push('/signup')
    }

    const onEnterPassword = () => {
      loginFn()
    }

    return { userStore, id, password, checked, loginFn, registerFn, onEnterPassword }
  },
})
</script>

<style lang="scss" scoped>
.login-wrapper {
  position: absolute;
  top: 50%;
  left: 50%;
  background-color: rgba(0, 0, 0, 0.8);
  border-radius: 10px;
  box-shadow: 0 15px 25px rgba(0, 0, 0, 0.8);
  color: #fff;
  margin: auto auto;
  padding: 40px;
  transform: translate(-50%, -50%);
  text-align: left;
}
</style>
