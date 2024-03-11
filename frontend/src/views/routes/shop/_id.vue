<template>
  <div class="ShopIndex">
    <Card>
      <template #title>{{ indexTitle }}</template>
      <template #content>
        <form class="p-d-flex p-flex-column">
          <div class="p-d-flex p-align-center p-pb-3">
            <label class="p-pr-5"><span>매장명</span></label>
            <span><InputText v-model="name" /></span>
          </div>
          <div class="p-d-flex p-align-center p-pb-3">
            <label class="p-pr-5"><span>대표 이미지</span></label>
            <template v-if="isEmptyImage1">
              <img class="p-mr-2" src="@/assets/images/noImage.png" alt="noImage.png" height="70px" />
            </template>
            <template v-else>
              <img
                class="p-mr-2"
                :src="`${serverUrl}/images/_shop/${serverImage1}`"
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
            <label class="p-pr-5"><span>대표 이미지2</span></label>
            <template v-if="isEmptyImage2">
              <img class="p-mr-2" src="@/assets/images/noImage.png" alt="noImage.png" height="70px" />
            </template>
            <template v-else>
              <img
                class="p-mr-2"
                :src="`${serverUrl}/images/_shop/${serverImage2}`"
                :alt="serverImage2"
                height="70px"
              />
            </template>
            <span>
              <FileUpload
                mode="basic"
                :multiple="true"
                accept="image/*"
                :max-file-size="1000000"
                @select="onSelectFile2"
              >
                <template #empty>
                  <p>Drag and drop files to here to upload.</p>
                </template>
              </FileUpload>
            </span>
          </div>
          <div class="p-d-flex p-align-center p-pb-3">
            <label class="p-pr-5"><span>평점</span></label>
            <span><InputNumber v-model="rating" show-buttons suffix="점" :min="0" :max="5" :step="0.5" /></span>
          </div>
          <div class="p-d-flex p-align-center p-pb-3">
            <label class="p-pr-5"><span>전화번호</span></label>
            <span>
              <InputMask
                v-model="phoneNumber"
                :mask="'010-9999-9999'"
                :slot-char="'_'"
                placeholder="010-____-____"
                :auto-clear="false"
              />
            </span>
          </div>
          <div class="p-d-flex p-align-start p-pb-3">
            <label class="p-pr-5"><span>주소</span></label>
            <div class="p-d-flex p-flex-column">
              <span class="p-mb-2">{{ searchAddress }}</span>
              <Button label="주소 검색" icon="pi pi-map" style="width: 150px" @click="onShowMap" />
            </div>
          </div>
          <div class="p-d-flex p-align-center p-pb-3">
            <label class="p-pr-5"><span>위도</span></label>
            <span><InputText v-model="latitude" /></span>
          </div>
          <div class="p-d-flex p-align-center p-pb-3">
            <label class="p-pr-5"><span>경도</span></label>
            <span><InputText v-model="longitude" /></span>
          </div>
          <div class="p-d-flex p-align-center p-pb-3">
            <label class="p-pr-5"
              ><span
                >태그<i
                  v-tooltip="'세미콜론(;)으로 구분해서 입력해주세요'"
                  class="p-ml-2 fa fa-regular fa-circle-question"
                  style="cursor: pointer" /></span
            ></label>
            <span><InputText v-model="tags" /></span>
          </div>
          <div class="p-d-flex p-align-center p-pb-3">
            <label class="p-pr-5"><span>운영시간</span></label>
            <Calendar v-model="startTimes" class="p-mr-2" time-only show-icon icon-display="input" />
            <Calendar v-model="endTimes" time-only show-icon icon-display="input" />
          </div>
          <div class="p-d-flex p-align-start p-pb-3">
            <label class="p-pr-5"><span>정보</span></label>
            <span><Textarea v-model="description" rows="5" cols="30" /></span>
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
    <!-- 주소 검색 모달 -->
    <Dialog v-model:visible="isShowMap" modal header="주소 검색" :style="{ width: '50rem' }">
      <div class="p-inputgroup">
        <InputText
          v-model="address"
          placeholder="주소를 입력하세요"
          style="width: 350px"
          @keydown.enter="onSearchMap"
        />
        <Button label="검색" severity="success" @click="onSearchMap" />
      </div>
      <JNaverMap :is-map-show="false" :address="searchAddress" height="400px" @search-result="onSearchResult" />
    </Dialog>
  </div>
</template>

<script>
import _ from 'lodash'
import moment from 'moment'
import { useToast } from 'vue-toastification'
import useCrud from '@/composables/useCrud.js'

const API_URL = '/api/shop'
const SERVER_URL = import.meta.env.VITE_BASE_URL

export default {
  name: 'ShopIndex',
  setup() {
    /** @data setting */
    const serverUrl = SERVER_URL
    const toast = useToast()

    /** @data component */
    const indexTitle = ref('음식점 추가')
    const name = ref('')
    const image1 = ref('')
    const image2 = ref('')
    const serverImage1 = ref('')
    const serverImage2 = ref('')
    const rating = ref(0)
    const phoneNumber = ref('')
    const address = ref('')
    const searchAddress = ref('')
    const latitude = ref('')
    const longitude = ref('')
    const position = ref({ latitude: 37.3243906, longitude: 126.952716 })
    const startTimes = ref(moment().startOf('day').format('HH:mm'))
    const endTimes = ref(moment().endOf('day').format('HH:mm'))
    const tags = ref('')
    const description = ref('')

    const onSelectFile = (data) => {
      image1.value = data.files[0]
    }
    const onSelectFile2 = (data) => {
      image2.value = data.files[0]
    }
    const isEmptyImage1 = computed(() => {
      return _.isEmpty(serverImage1.value)
    })
    const isEmptyImage2 = computed(() => {
      return _.isEmpty(serverImage2.value)
    })
    const onSearchMap = () => {
      searchAddress.value = `${_.random(0, 9999999)}-${address.value}`
    }

    /** @composable CRUD */
    const { onList, onGet, onPost, onPut } = useCrud()
    const result = ref()
    const onSavePage = ref(() => {})

    const getStartTimes = () => {
      if (typeof startTimes.value !== 'object') {
        return moment().startOf('day').format('HH:mm')
      }
      return moment(startTimes.value).format('HH:mm')
    }
    const getEndTimes = () => {
      if (typeof endTimes.value !== 'object') {
        return moment().endOf('day').format('HH:mm')
      }
      return moment(endTimes.value).format('HH:mm')
    }

    onMounted(async () => {
      result.value = await onGet(API_URL)

      if (result.value) {
        indexTitle.value = '음식점 수정'
        name.value = result.value.name
        image1.value = result.value.image1
        image2.value = result.value.image2
        serverImage1.value = result.value.image1
        serverImage2.value = result.value.image2
        rating.value = Number(result.value.rating)
        phoneNumber.value = result.value.phoneNumber
        address.value = result.value.address
        searchAddress.value = result.value.address
        latitude.value = result.value.latitude
        longitude.value = result.value.longitude
        position.value['latitude'] = Number(latitude.value)
        position.value['longitude'] = Number(longitude.value)
        tags.value = result.value.tags
        description.value = result.value.description

        onSavePage.value = () => {
          if (validationCheck()) {
            const params = {
              name: name.value,
              image1: image1.value,
              image2: image2.value,
              rating: rating.value,
              phoneNumber: phoneNumber.value,
              address: address.value,
              latitude: latitude.value,
              longitude: longitude.value,
              times: [getStartTimes(), getEndTimes()],
              tags: tags.value,
              description: description.value,
            }
            onPut(API_URL, '/routes/shop/list', params, true)
          }
        }
      } else {
        onSavePage.value = () => {
          if (validationCheck()) {
            const params = {
              name: name.value,
              image1: image1.value,
              image2: image2.value,
              rating: rating.value,
              phoneNumber: phoneNumber.value,
              address: address.value,
              latitude: latitude.value,
              longitude: longitude.value,
              times: [getStartTimes(), getEndTimes()],
              tags: tags.value,
              description: description.value,
            }
            onPost(API_URL, '/routes/shop/list', params, true)
          }
        }
      }
    })

    const onListPage = () => {
      onList('/routes/shop/list')
    }

    const validationCheck = () => {
      if (_.isEmpty(name.value)) {
        toast.warning('매장명 입력은 필수입니다.')
        return false
      }
      if (_.isEmpty(phoneNumber.value)) {
        toast.warning('전화번호 입력은 필수입니다.')
        return false
      }
      if (_.isEmpty(address.value)) {
        toast.warning('주소 입력은 필수입니다.')
        return false
      }
      return true
    }

    return {
      serverUrl,
      indexTitle,
      name,
      image1,
      image2,
      serverImage1,
      serverImage2,
      rating,
      phoneNumber,
      address,
      searchAddress,
      latitude,
      longitude,
      position,
      startTimes,
      endTimes,
      tags,
      description,
      isShowMap: ref(false),
      onSearchMap,
      isEmptyImage1,
      isEmptyImage2,
      onSelectFile,
      onSelectFile2,
      onSavePage,
      onListPage,
    }
  },
  methods: {
    onShowMap() {
      this.isShowMap = true
    },
    onSearchResult(data) {
      this.address = data.address
      this.searchAddress = data.address
      this.latitude = data.latitude
      this.longitude = data.longitude

      this.isShowMap = false
    },
  },
}
</script>

<style scoped lang="scss">
.ShopIndex {
  form {
    label {
      display: inline-flex;
      width: 200px;
    }
  }
}
</style>
