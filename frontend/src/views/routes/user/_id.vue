<template>
  <div class="UserIndex">
    <Card>
      <template #title>{{ indexTitle }}</template>
      <template #content>
        <form class="p-d-flex p-flex-column">
          <div class="p-pb-3">
            <label class="p-pr-5"><span>이름</span></label>
            <span><InputText v-model="userId" /></span>
          </div>
          <div class="p-pb-3">
            <label class="p-pr-5"><span>패스워드</span></label>
            <span><Password v-model="password" /></span>
          </div>
          <div class="p-pb-3">
            <label class="p-pr-5"><span>권한</span></label>
            <span>
              <Dropdown
                v-model="role"
                :options="options"
                option-label="name"
                data-key="id"
                placeholder="권한을 선택해주세요"
              />
            </span>
          </div>
          <div v-if="pageType === 'Edit'" class="p-pb-3">
            <label class="p-pr-5"><span>찜 목록</span></label>
            <span
              ><MultiSelect
                v-model="selectedFavorites"
                :options="shopList"
                option-label="name"
                option-value="name"
                data-key="id"
                placeholder="찜 목록"
                :max-selected-labels="2"
                class="w-full md:w-20rem"
            /></span>
          </div>
        </form>
      </template>
      <template #footer>
        <div class="p-d-flex p-justify-end" style="width: 100%">
          <Button class="p-mr-2" label="저장" icon="pi pi-check" @click="onSavePage" />
          <Button label="리스트" icon="pi pi-list" @click="onListPage" />
        </div>
      </template>
    </Card>
  </div>
</template>

<script>
import _ from 'lodash'
import { useToast } from 'vue-toastification'
import useCrud from '@/composables/useCrud.js'
import { useRoute } from 'vue-router'
import router from '@/router/index.js'

const API_URL = '/api/user'
const API_SHOP_URL = '/api/shop'

export default {
  name: 'UserIndex',
  setup() {
    /** @data setting */
    const toast = useToast()
    const axios = inject('$axios')
    const route = useRoute()

    /** @data component */
    const pageType = ref('Add')
    const indexTitle = ref('사용자 추가')
    const userId = ref('')
    const password = ref('')
    const role = ref()
    const favoriteShop = ref({})
    const options = ref([
      {
        id: 'USER',
        name: 'USER',
      },
      {
        id: 'ADMIN',
        name: 'ADMIN',
      },
    ])
    const selectedFavorites = ref()
    const shopList = ref([])

    /** @composable CRUD */
    const { onList, onGet, onPost } = useCrud()
    const result = ref()
    const onSavePage = ref(() => {})

    axios.get(API_SHOP_URL).then((res) => {
      shopList.value = res.data.content
    })

    onMounted(async () => {
      result.value = await onGet(API_URL)

      if (result.value) {
        indexTitle.value = '사용자 수정'
        pageType.value = 'Edit'
        userId.value = result.value.userId
        role.value = _.find(options.value, ['id', result.value.role])
        favoriteShop.value = result.value.favoriteShop

        if (!_.isEmpty(favoriteShop.value)) {
          selectedFavorites.value = _.values(favoriteShop.value)
        }

        onSavePage.value = () => {
          if (validationCheck()) {
            const result = {}
            selectedFavorites.value.forEach((name) => {
              const foundShop = shopList.value.find((shop) => shop.name === name)
              if (foundShop) {
                result[foundShop.id] = name
              }
            })

            const params = {
              userId: userId.value,
              password: password.value,
              role: role.value.id,
              favoriteShop: result,
            }

            // favoriteShop의 경우, object로 날려줘야 해서, formData 사용이 안됨.
            axios.put(`${API_URL}/${route.params.id}`, params).then(() => {
              router.push('/routes/user/list')
              toast.success('항목을 수정했습니다.')
            })

            // onPut(API_URL, '/routes/user/list', params)
          }
        }
      } else {
        onSavePage.value = () => {
          if (validationCheck()) {
            const params = {
              userId: userId.value,
              password: password.value,
              role: role.value.id,
            }
            onPost(API_URL, '/routes/user/list', params)
          }
        }
      }
    })

    const onListPage = () => {
      onList('/routes/user/list')
    }

    const validationCheck = () => {
      if (_.isEmpty(userId.value)) {
        toast.warning('아이디 입력은 필수입니다.')
        return false
      }
      if (_.isEmpty(password.value)) {
        toast.warning('패스워드 입력은 필수입니다.')
        return false
      }
      if (_.isEmpty(role.value)) {
        toast.warning('권한 입력은 필수입니다.')
        return false
      }
      return true
    }

    return {
      pageType,
      indexTitle,
      userId,
      password,
      role,
      favoriteShop,
      selectedFavorites,
      shopList,
      options,
      onSavePage,
      onListPage,
    }
  },
}
</script>

<style scoped lang="scss">
.UserIndex {
  form {
    label {
      display: inline-flex;
      width: 200px;
    }
  }
}
</style>
