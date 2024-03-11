<template>
  <div class="ReviewIndex">
    <Card>
      <template #title>{{ indexTitle }}</template>
      <template #content>
        <form class="p-d-flex p-flex-column">
          <div class="p-d-flex p-align-center p-pb-3">
            <label class="p-pr-5"><span>사용자명</span></label>
            <span><InputText v-model="userId" /></span>
          </div>
          <div class="p-d-flex p-align-center p-pb-3">
            <label class="p-pr-5"><span>상호명</span></label>
            <span>
              <Dropdown
                v-model="selectedShop"
                :options="options"
                option-label="name"
                data-key="id"
                placeholder="상점을 선택해주세요"
              />
            </span>
          </div>
          <div class="p-d-flex p-align-center p-pb-3">
            <label class="p-pr-5"><span>리뷰 이미지</span></label>
            <template v-if="isEmptyImage1">
              <img class="p-mr-2" src="@/assets/images/noImage.png" alt="noImage.png" height="70px" />
            </template>
            <template v-else>
              <img
                class="p-mr-2"
                :src="`${serverUrl}/images/_review/${serverImage1}`"
                :alt="serverImage1"
                height="70px"
              />
            </template>
            <span>
              <FileUpload
                mode="basic"
                :multiple="true"
                accept="image/*"
                :max-file-size="1000000"
                @select="onSelectFile"
              >
                <template #empty>
                  <p>Drag and drop files to here to upload.</p>
                </template>
              </FileUpload>
            </span>
          </div>
          <div class="p-d-flex p-align-start p-pb-3">
            <label class="p-pr-5"><span>리뷰 내용</span></label>
            <span><Textarea v-model="content" rows="5" cols="30" /></span>
          </div>
          <div class="p-d-flex p-align-center p-pb-3">
            <label class="p-pr-5"><span>평점</span></label>
            <span><InputNumber v-model="rating" show-buttons suffix="점" :min="0" :max="5" :step="0.5" /></span>
          </div>
          <div class="p-d-flex p-align-center p-pb-3">
            <label class="p-pr-5"><span>좋았던 점</span></label>
            <span>
              <MultiSelect
                v-model="selectedAdvantages"
                display="chip"
                :options="advantages"
                option-label="name"
                data-key="code"
                placeholder="이런 점이 좋았어요(최대 3개)"
                :max-selected-labels="3"
                :selection-limit="3"
              />
            </span>
          </div>
          <div class="p-d-flex p-align-center p-pb-3">
            <label class="p-pr-5"><span>아쉬운 점</span></label>
            <span>
              <MultiSelect
                v-model="selectedDisAdvantages"
                display="chip"
                :options="disAdvantages"
                option-label="name"
                data-key="code"
                placeholder="이런 점이 아쉬웠어요(최대 3개)"
                :max-selected-labels="3"
                :selection-limit="3"
              />
            </span>
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
import { ADVANTAGES, DISADVANTAGES } from '@/composables/useData.js'

const API_URL = '/api/review'
const API_SHOP_URL = '/api/shop'
const SERVER_URL = import.meta.env.VITE_BASE_URL

export default {
  name: 'ReviewIndex',
  setup() {
    /** @data setting */
    const $axios = inject('$axios')
    const serverUrl = SERVER_URL
    const toast = useToast()

    /** @data component */
    const indexTitle = ref('리뷰 추가')
    const selectedShop = ref()
    const options = ref([])
    const userId = ref('')
    const image1 = ref('')
    const serverImage1 = ref('')
    const content = ref('')
    const rating = ref(0)
    const selectedAdvantages = ref()
    const selectedDisAdvantages = ref()
    const advantages = ADVANTAGES
    const disAdvantages = DISADVANTAGES

    const onSelectFile = (data) => {
      image1.value = data.files[0]
    }
    const isEmptyImage1 = computed(() => {
      return _.isEmpty(serverImage1.value)
    })

    $axios.get(API_SHOP_URL).then((res) => {
      options.value = res.data.content
    })

    /** @composable CRUD */
    const { onList, onGet, onPost, onPut } = useCrud()
    const result = ref()
    const onSavePage = ref(() => {})

    onMounted(async () => {
      result.value = await onGet(API_URL)

      if (result.value) {
        indexTitle.value = '리뷰 수정'
        selectedShop.value = result.value.shop
        userId.value = result.value.userId
        image1.value = result.value.image1
        serverImage1.value = result.value.image1
        content.value = result.value.content
        rating.value = Number(result.value.rating)
        selectedAdvantages.value = _.filter(ADVANTAGES, (item) => result.value.advantages.includes(item.code))
        selectedDisAdvantages.value = _.filter(DISADVANTAGES, (item) => result.value.disadvantage.includes(item.code))

        onSavePage.value = () => {
          if (validationCheck()) {
            const params = {
              shopId: selectedShop.value.id,
              userId: userId.value,
              image1: image1.value,
              content: content.value,
              rating: rating.value,
              advantages: _.map(selectedAdvantages.value, 'name'),
              disadvantage: _.map(selectedDisAdvantages.value, 'name'),
            }
            onPut(API_URL, '/routes/review/list', params, true)
          }
        }
      } else {
        onSavePage.value = () => {
          if (validationCheck()) {
            const params = {
              shopId: selectedShop.value.id,
              userId: userId.value,
              image1: image1.value,
              content: content.value,
              rating: rating.value,
              advantages: _.map(selectedAdvantages.value, 'name'),
              disadvantage: _.map(selectedDisAdvantages.value, 'name'),
            }
            onPost(API_URL, '/routes/review/list', params, true)
          }
        }
      }
    })

    const onListPage = () => {
      onList('/routes/review/list')
    }

    const validationCheck = () => {
      if (_.isEmpty(userId.value)) {
        toast.warning('사용자명 입력은 필수입니다.')
        return false
      }
      if (_.isEmpty(content.value)) {
        toast.warning('리뷰 내용 입력은 필수입니다.')
        return false
      }
      return true
    }

    return {
      serverUrl,
      indexTitle,
      selectedShop,
      options,
      userId,
      image1,
      serverImage1,
      content,
      rating,
      advantages,
      disAdvantages,
      selectedAdvantages,
      selectedDisAdvantages,
      onSelectFile,
      isEmptyImage1,
      onSavePage,
      onListPage,
    }
  },
}
</script>

<style scoped lang="scss">
.ReviewIndex {
  form {
    label {
      display: inline-flex;
      width: 200px;
    }
  }
}
</style>
