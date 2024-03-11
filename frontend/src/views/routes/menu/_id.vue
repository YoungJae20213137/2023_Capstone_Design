<template>
  <div class="MenuIndex">
    <Card>
      <template #title>{{ indexTitle }}</template>
      <template #content>
        <form class="p-d-flex p-flex-column">
          <div class="p-d-flex p-align-center p-pb-3">
            <label class="p-pr-5"><span>메뉴명</span></label>
            <span><InputText v-model="name" /></span>
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
            <label class="p-pr-5"><span>메뉴 이미지</span></label>
            <template v-if="isEmptyImage1">
              <img class="p-mr-2" src="@/assets/images/noImage.png" alt="noImage.png" height="70px" />
            </template>
            <template v-else>
              <img
                class="p-mr-2"
                :src="`${serverUrl}/images/_menu/${serverImage1}`"
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
          <div class="p-d-flex p-align-center p-pb-3">
            <label class="p-pr-5"><span>가격</span></label>
            <span><InputNumber v-model="price" show-buttons suffix="원" :min="0" :max="99999999" :step="50" /></span>
          </div>
          <div class="p-d-flex p-align-center p-pb-3">
            <label class="p-pr-5"><span>태그</span></label>
            <span
              ><InputText
                v-model="tags"
                placeholder="태그 입력 후 엔터를 쳐주세요."
                style="width: 250px"
                @keydown.enter="onEnterTag"
            /></span>
            <ul class="p-d-flex" style="list-style: none; column-gap: 5px">
              <li v-for="(tag, idx) of tagList" :key="idx">
                <Tag :value="tag"></Tag>
              </li>
            </ul>
          </div>
          <div class="p-d-flex p-align-start p-pb-3">
            <label class="p-pr-5"><span>카테고리</span></label>
            <div class="p-d-flex p-flex-column">
              <div v-for="category of categories" :key="category.key" class="p-pb-2">
                <Checkbox v-model="selectedCategories" :input-id="category.key" name="category" :value="category.key" />
                <label class="p-pb-1 p-pl-2" :for="category.key">{{ category.name }}</label>
              </div>
            </div>
          </div>
          <div class="p-d-flex p-align-start p-pb-3">
            <label class="p-pr-5"><span>설명</span></label>
            <span><Textarea v-model="description" rows="5" cols="50" /></span>
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
import useCrud from '@/composables/useCrud.js'
import { useToast } from 'vue-toastification'
import { CATEGORIES } from '@/composables/useData.js'

const API_URL = '/api/menu'
const API_SHOP_URL = '/api/shop'
const SERVER_URL = import.meta.env.VITE_BASE_URL

export default {
  name: 'MenuIndex',
  setup() {
    /** @data setting */
    const $axios = inject('$axios')
    const serverUrl = SERVER_URL
    const toast = useToast()

    /** @data component */
    const indexTitle = ref('메뉴 추가')
    const name = ref('')
    const selectedShop = ref()
    const options = ref([])
    const image1 = ref('')
    const serverImage1 = ref('')
    const price = ref(1000)
    const selectedCategories = ref([])
    const categories = ref(CATEGORIES)
    const description = ref('')
    const tags = ref('')
    const tagList = ref([])

    $axios.get(API_SHOP_URL).then((res) => {
      options.value = res.data.content
    })

    const onSelectFile = (data) => {
      image1.value = data.files[0]
    }
    const isEmptyImage1 = computed(() => {
      return _.isEmpty(serverImage1.value)
    })

    /** @composable CRUD */
    const { onList, onGet, onPost, onPut } = useCrud()
    const result = ref()
    const onSavePage = ref(() => {})

    onMounted(async () => {
      result.value = await onGet(API_URL)

      if (result.value) {
        indexTitle.value = '메뉴 수정'
        selectedShop.value = result.value.shop
        name.value = result.value.name
        image1.value = result.value.image1
        serverImage1.value = result.value.image1
        price.value = result.value.price
        selectedCategories.value = _.keys(_.pickBy(result.value.categories, _.identity))
        description.value = result.value.description
        tagList.value = result.value.tags?.split(';') || []

        onSavePage.value = () => {
          if (validationCheck()) {
            // 카테고리 데이터 format
            const keyList = _.keyBy(categories.value, 'key')
            const _categories = _.mapValues(keyList, (category) => selectedCategories.value.includes(category.key))

            const params = {
              shopId: selectedShop.value.id,
              name: name.value,
              image1: image1.value,
              price: price.value,
              categories: _categories,
              description: description.value,
              tags: tagList.value.join(';'),
            }
            onPut(API_URL, '/routes/menu/list', params, true)
          }
        }
      } else {
        onSavePage.value = () => {
          if (validationCheck()) {
            // 카테고리 데이터 format
            const keyList = _.keyBy(categories.value, 'key')
            const _categories = _.mapValues(keyList, (category) => selectedCategories.value.includes(category.key))

            const params = {
              shopId: selectedShop.value.id,
              name: name.value,
              image1: image1.value,
              price: price.value,
              categories: _categories,
              description: description.value,
              tags: tagList.value.join(';'),
            }
            onPost(API_URL, '/routes/menu/list', params, true)
          }
        }
      }
    })

    const onListPage = () => {
      onList('/routes/menu/list')
    }

    const validationCheck = () => {
      if (_.isEmpty(name.value)) {
        toast.warning('메뉴명 입력은 필수입니다.')
        return false
      }
      if (_.isEmpty(selectedShop.value)) {
        toast.warning('매장명 입력은 필수입니다.')
        return false
      }
      if (_.isEmpty(description.value)) {
        toast.warning('정보 입력은 필수입니다.')
        return false
      }
      return true
    }

    return {
      serverUrl,
      indexTitle,
      name,
      selectedShop,
      options,
      image1,
      serverImage1,
      price,
      selectedCategories,
      categories,
      description,
      tags,
      tagList,
      isEmptyImage1,
      onSelectFile,
      onSavePage,
      onListPage,
    }
  },
  methods: {
    onEnterTag() {
      this.tagList.push(this.tags)
      this.tags = ''
    },
  },
}
</script>

<style scoped lang="scss">
.MenuIndex {
  form {
    label {
      display: inline-flex;
      width: 200px;
    }
  }
}
</style>
