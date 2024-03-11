<template>
  <div class="login-wrapper">
    <form>
      <div class="p-d-flex p-justify-center p-mb-4">
        <h2 style="color: white">회원가입</h2>
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
      <div class="p-pt-3 p-d-flex">
        <Button label="등록" text style="width: 100%" @click="registerFn" />
      </div>
    </form>
  </div>
</template>

<script>
import { useUserStore } from '@/store/user'

export default defineComponent({
  setup() {
    const id = ref('')
    const password = ref('')
    const userStore = useUserStore()

    const registerFn = async () => {
      await userStore.register({ id, password })
    }

    const onEnterPassword = () => {
      registerFn()
    }

    return { id, password, registerFn, onEnterPassword }
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
