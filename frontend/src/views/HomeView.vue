<template>
  <div class="HomeView">
    <div class="card" style="width: 500px; margin-right: 30px; padding-bottom: 50px; margin-bottom: 0">
      <div class="p-inputgroup p-mb-3">
        <InputText v-model="searchAddress" placeholder="장소 검색" style="width: 350px" @keydown.enter="onSearchMap" />
        <Button label="검색" severity="success" @click="onSearchMap" />
      </div>
      <div class="search-filter p-mb-3 p-d-flex p-align-center p-justify-between">
        <div>
          <Button
            class="p-mr-2"
            icon="pi pi-filter-fill"
            outlined
            rounded
            aria-label="filter"
            severity="success"
            @click="onShowFilter"
          />
          <OverlayPanel ref="op" class="filter-section" append-to="body">
            <div class="p-d-flex p-flex-column p-align-start p-pb-3">
              <label class="p-pb-2"><span style="font-size: 1.2rem; font-weight: 800">메뉴</span></label>
              <div>
                <InputText v-model="searchMenu" />
              </div>
            </div>
            <div class="p-d-flex p-flex-column p-align-start p-pb-3">
              <label><span style="font-size: 1.2rem; font-weight: 800">가격</span></label>
              <div class="p-d-flex p-justify-center p-align-center" style="width: 380px; padding: 15px 0 15px 10px">
                <Slider v-model="priceRange" :step="1000" :max="100000" class="p-mr-5" style="width: 100%" />
                <span style="width: 130px">~{{ priceRange }}원</span>
              </div>
            </div>
            <div class="p-d-flex p-flex-column p-align-start p-pb-3" style="row-gap: 8px">
              <label><span style="font-size: 1.2rem; font-weight: 800">카테고리</span></label>
              <div style="display: grid; grid-template-columns: auto auto auto; column-gap: 10px">
                <div v-for="category of categories" :key="category.key" class="p-pb-2">
                  <Checkbox
                    v-model="selectedCategories"
                    :input-id="category.key"
                    name="category"
                    :value="category.key"
                  />
                  <label class="p-pb-1 p-pl-2" :for="category.key">{{ category.name }}</label>
                </div>
              </div>
            </div>
            <Button class="p-mr-2" outlined severity="success" label="검색" @click="onSearchFilter" />
            <Button outlined severity="warning" label="초기화" @click="onClearFilter" />
          </OverlayPanel>
          <Button
            class="p-mr-2"
            label="영업중"
            :outlined="!isSearchOpening"
            :raised="isSearchOpening"
            rounded
            severity="success"
            @click="onSearchOpenShop"
          />
          <Button
            icon="fa fa-heart"
            :outlined="!isSearchFavorite"
            :raised="isSearchFavorite"
            rounded
            severity="danger"
            @click="onSearchFavorite"
          />
        </div>
        <Button icon="pi pi-filter-slash" outlined rounded severity="warning" @click="onCancelFilter" />
      </div>
      <div class="p-d-flex p-flex-column p-align-start p-pb-3">
        <div style="display: grid; grid-template-columns: auto auto auto auto; row-gap: 5px; column-gap: 10px">
          <ToggleButton
            v-for="(tags, index) of tagList"
            :key="index"
            v-model="tagList[index].checked"
            class="toggle-button"
            rounded
            :on-label="tags.name"
            :off-label="tags.name"
            @change="onSearchTag(tags)"
          />
        </div>
      </div>
      <JNaverMapPanel :shop-list="shopList" @item-info="onGetItemInfo" />
    </div>
    <div class="card" style="width: 100%; min-width: 800px">
      <JNaverMap
        :address="address"
        :selected-id="shopId"
        :shop-list="shopList"
        height="100%"
        @marker-click="onMarkerClick"
      />
    </div>

    <!-- 상세 정보 모달 -->
    <Dialog v-model:visible="isShowDetail" modal :header="detailInfo?.name" :draggable="false" style="width: 60rem">
      <template #header>
        <div class="p-d-flex p-align-center" style="font-weight: 700; font-size: 1.25rem">
          {{ detailInfo?.name }}
          <Button
            v-tooltip="'찜 해주세요'"
            class="p-ml-2"
            :class="!favoriteMap[detailInfo.id].favorite ? 'p-button-secondary' : 'p-button-danger'"
            text
            rounded
            icon="fa fa-heart"
            style="width: 2rem; height: 2rem"
            @click="onAddFavorite(detailInfo)"
          />
        </div>
      </template>
      <TabView @tab-click="onTabClick">
        <TabPanel header="홈">
          <div class="home-content">
            <div class="content-info">
              <div class="content-info-item p-d-flex">
                <i class="fa fa-circle-info p-mt-1" style="color: var(--teal-500)" />
                <span class="p-ml-3">{{ detailInfo.description }}</span>
              </div>
              <div class="content-info-item p-d-flex">
                <i class="fa fa-tags p-mt-1" style="color: var(--teal-500)" />
                <span class="p-ml-3">{{ detailInfo.tags }}</span>
              </div>
              <div class="content-info-item">
                <i class="fa fa-location-dot" style="color: var(--teal-500)" />
                <span class="p-ml-3">{{ detailInfo.address }}</span>
              </div>
              <div class="content-info-item">
                <i class="fa fa-clock" style="color: var(--teal-500)" />
                <span class="p-ml-3">{{ setTimeFormat(detailInfo.times) }}</span>
              </div>
              <div class="content-info-item">
                <i class="fa fa-phone" style="color: var(--teal-500)" />
                <span class="p-ml-3">{{ detailInfo.phoneNumber }}</span>
              </div>
              <div class="content-info-item">
                <i class="fa fa-star" style="color: var(--teal-500)" />
                <span class="p-ml-3">{{ detailInfo.rating }}</span>
              </div>
            </div>
            <div class="content-image">
              <img
                v-if="detailInfo.image1"
                :src="`${serverUrl}/images/_shop/${detailInfo.image1}`"
                :alt="detailInfo.image1"
                style="width: auto; height: 200px"
              />
              <img
                v-if="detailInfo.image2"
                :src="`${serverUrl}/images/_shop/${detailInfo.image2}`"
                :alt="detailInfo.image2"
                style="width: auto; height: 200px"
              />
            </div>
          </div>
        </TabPanel>
        <TabPanel header="메뉴">
          <perfect-scrollbar>
            <div class="menu-section">
              <div class="menu-content">
                <div v-for="(menu, idx) of menuList[detailInfo.id]" :key="idx" class="menu-item">
                  <div
                    class="img-wrapper"
                    style="position: relative; border: solid 0.15rem #e2e2e2; border-radius: 8px"
                  >
                    <template v-if="isEmptyImage(menu.image1)">
                      <img class="p-mr-2" src="@/assets/images/noImage.png" alt="noImage.png" />
                    </template>
                    <template v-else>
                      <img :src="`${serverUrl}/images/_menu/${menu.image1}`" alt="menu-item" style="height: 140px" />
                    </template>
                    <span style="position: absolute; top: 10px; right: 10px">
                      <Tag
                        v-for="(tag, i) of menu.tags?.split(';')"
                        :key="i"
                        class="p-mr-1"
                        :value="tag"
                        severity="warning"
                        style="background-color: #1daca2"
                      ></Tag>
                    </span>
                  </div>
                  <p style="font-size: 18px">{{ menu.name }}</p>
                  <p style="font-weight: 100; text-align: justify; text-indent: 10px">{{ menu.description }}</p>
                  <p>{{ menu.price }}원</p>
                  <p>{{ getCategories(menu.categories) }}</p>
                </div>
              </div>
            </div>
          </perfect-scrollbar>
        </TabPanel>
        <TabPanel header="리뷰">
          <perfect-scrollbar>
            <div v-if="!isShowReview" class="review-section">
              <div class="review-section-header">
                <h2 style="margin: 0">
                  리뷰
                  <span class="review-count">{{ reviewList.length }}</span>
                </h2>
                <Button icon="pi pi-pencil" severity="success" text label="작성하기" @click="onClickReview" />
              </div>
              <div class="review-section-content">
                <Carousel
                  v-if="reviewList.length > 0"
                  :value="reviewList"
                  :num-visible="3"
                  :num-scroll="1"
                  :responsive-options="responsiveOptions"
                  :show-indicators="false"
                >
                  <template #item="slotProps">
                    <div class="carousel-item" :class="{ disabled: slotProps.data.userId !== currentUser }">
                      <div class="p-mb-3 p-d-flex p-justify-center carousel-image-wrapper" style="height: 140px">
                        <template v-if="isEmptyImage(slotProps.data.image1)">
                          <img class="p-mr-2" src="@/assets/images/noImage.png" alt="noImage.png" />
                        </template>
                        <template v-else>
                          <img
                            :src="`${serverUrl}/images/_review/${slotProps.data.image1}`"
                            :alt="slotProps.data.name"
                          />
                        </template>
                      </div>
                      <div class="carousel-user-wrapper">
                        <Avatar :label="slotProps.data.userId[0]" class="p-mr-2" shape="circle" />
                        <span>{{ slotProps.data.userId }}</span>
                      </div>
                      <div class="p-mb-3 p-d-flex p-dir-col p-justify-center p-align-center carousel-review-content">
                        <div v-if="isReviewCollapsed[slotProps.data.id]" class="collapsed-text">
                          <p class="ellipsis-text">
                            {{ slotProps.data.content }}
                          </p>
                        </div>
                        <div v-else>
                          <p class="full-text" style="">{{ slotProps.data.content }}</p>
                        </div>
                        <i
                          class="collapse-icon"
                          :class="[isReviewCollapsed[slotProps.data.id] ? 'fa fa-caret-down' : 'fa fa-caret-up']"
                          @click="onClickCollapse(slotProps.data.id)"
                        />
                        <div
                          v-for="(value, idx) of slotProps.data.advantages"
                          :key="idx"
                          style="width: 100%; display: flex; justify-content: center"
                        >
                          <Tag :value="value" severity="success" :icon="setAdTagIcon(value)" />
                        </div>
                      </div>
                      <div class="review-content p-d-flex p-justify-center">
                        <Button label="수정" severity="success" @click="onClickReviewEdit(slotProps.data)" />
                        <Button
                          class="p-ml-3"
                          label="삭제"
                          severity="warning"
                          @click="onDeleteReview(slotProps.data)"
                        />
                      </div>
                    </div>
                  </template>
                </Carousel>
                <div v-else>첫 리뷰를 남겨주세요.</div>
              </div>
            </div>
            <div v-else class="review-content-user">
              <div class="review-content p-d-flex p-dir-col">
                <span>리뷰를 남겨주세요.</span>
                <Textarea v-model="review" rows="5" cols="30" auto-resize />
              </div>
              <div class="review-content p-d-flex p-dir-col">
                <span>이미지를 업로드해주세요.</span>
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
              </div>
              <div class="review-content p-d-flex p-dir-col">
                <span>평점</span>
                <Rating v-model="rating" :cancel="false" />
              </div>
              <div class="review-content p-d-flex p-dir-col">
                <span>어떤 점이 좋으셨나요?</span>
                <MultiSelect
                  v-model="selectedAdvantages"
                  display="chip"
                  :options="advantages"
                  option-label="name"
                  data-key="code"
                  placeholder="이런 점이 좋았어요(최대 3개)"
                  :max-selected-labels="3"
                  :selection-limit="3"
                  class="w-full md:w-20rem"
                />
              </div>
              <div class="review-content p-d-flex p-dir-col">
                <span>어떤 점이 아쉬웠나요?</span>
                <MultiSelect
                  v-model="selectedDisAdvantages"
                  display="chip"
                  :options="disAdvantages"
                  option-label="name"
                  data-key="code"
                  placeholder="이런 점이 아쉬웠어요(최대 3개)"
                  :max-selected-labels="3"
                  :selection-limit="3"
                  class="w-full md:w-20rem"
                />
              </div>
              <div class="review-content p-d-flex p-justify-center">
                <Button :label="!isReviewEdit ? '저장' : '수정'" severity="success" @click="onSaveReview" />
              </div>
            </div>
          </perfect-scrollbar>
        </TabPanel>
        <TabPanel header="예약">
          <perfect-scrollbar>
            <div class="reservation-section">
              <div class="p-pb-4">
                <div class="p-d-flex p-flex-column">
                  <span style="font-size: 20px; font-weight: 600"
                    ><i class="fa fa-user p-pr-2" />인원을 선택해주세요.</span
                  >
                  <span style="font-size: 14px; line-height: 18px; color: #939396; margin-top: 2px"
                    >2~7명까지 선택 가능합니다.</span
                  >
                </div>
                <div class="p-pt-3">
                  <SelectButton v-model="reservationUser" :options="reservationUserOption" severty="success" />
                </div>
              </div>
              <div class="p-pb-4">
                <div class="p-d-flex p-flex-column">
                  <span style="font-size: 20px; font-weight: 600"
                    ><i class="fa fa-calendar p-pr-2" />1주일까지 예약 가능합니다.</span
                  >
                  <span style="font-size: 14px; line-height: 18px; color: #939396; margin-top: 2px">
                    {{ setDateFormat(reservationDate) }}</span
                  >
                </div>
                <div class="p-pt-3" style="display: grid; width: 470px">
                  <Calendar v-model="reservationDate" inline :min-date="minDate" :max-date="maxDate" />
                </div>
              </div>
              <div class="p-pb-4">
                <div class="p-d-flex p-flex-column">
                  <span style="font-size: 20px; font-weight: 600"
                    ><i class="fa fa-clock p-pr-2" />시간을 선택해주세요.</span
                  >
                </div>
                <div class="p-pt-3">
                  <div class="select-buttons-container" style="display: flex; flex-wrap: wrap">
                    <Button
                      v-for="time in getReservationTimeOption(detailInfo)"
                      :key="time"
                      severity="success"
                      outlined
                      class="select-button"
                      style="flex-basis: 19%; margin: 5px; justify-content: center"
                      :disabled="onDisabledReservationTime(time)"
                      @click="onClickReservationTime($event, time)"
                    >
                      {{ time }}
                    </Button>
                  </div>
                </div>
              </div>
              <div class="p-pb-4">
                <div class="p-d-flex p-flex-column">
                  <span style="font-size: 20px; font-weight: 600"
                    ><i class="fa fa-comment-dots p-pr-2" />요청 사항</span
                  >
                  <div class="p-pt-3">
                    <Textarea v-model="reservationRequest" rows="5" cols="75" />
                  </div>
                </div>
              </div>
              <div>
                <Button label="예약하기" severity="success" @click="onAddReserve" />
              </div>
            </div>
          </perfect-scrollbar>
        </TabPanel>
      </TabView>
    </Dialog>
  </div>
</template>

<script>
import _ from 'lodash'
import { useToast } from 'vue-toastification'
import { useUserStore } from '@/store/user.js'
import { ADVANTAGES, CATEGORIES, DISADVANTAGES } from '@/composables/useData.js'
import moment from 'moment'

const API_URL = '/api/shop'
const API_MENU_URL = '/api/menu'
const API_USER_URL = '/api/user'
const API_REVIEW_URL = '/api/review'
const API_RESERVE_URL = '/api/reservation'
const SERVER_URL = import.meta.env.VITE_BASE_URL

export default defineComponent({
  name: 'HomeView',
  setup() {
    /** @data setting */
    const axios = inject('$axios')
    const userStore = useUserStore()
    const currentUser = JSON.parse(userStore.user).userId
    const currentUserId = JSON.parse(userStore.user).id

    /** @data component */
    let shopList = ref([])
    let menuList = ref([])
    let tagList = ref([])
    let tagListOrigin = ref([])
    let favoriteMap = ref({})

    const op = ref(null)
    const onShowFilter = (event) => {
      op.value.toggle(event)
    }
    const onHideFilter = (event) => {
      op.value.toggle(event)
    }

    const responsiveOptions = ref([
      {
        breakpoint: '1400px',
        numVisible: 2,
        numScroll: 1,
      },
      {
        breakpoint: '1199px',
        numVisible: 3,
        numScroll: 1,
      },
      {
        breakpoint: '767px',
        numVisible: 2,
        numScroll: 1,
      },
      {
        breakpoint: '575px',
        numVisible: 1,
        numScroll: 1,
      },
    ])

    axios
      .get(API_URL)
      .then((res) => {
        shopList.value = res.data.content
      })
      .then(() => {
        let tags = []

        /** 상점별 메뉴 목록 */
        _.forEach(shopList.value, async (obj) => {
          menuList.value[obj.id] = await axios.get(`${API_URL}/${obj.id}/menu`).then((res) => res.data)
          tags.push(obj.tags.split(';'))
          tagList.value = _.uniq(_.flattenDeep(tags)).map((value) => ({ checked: false, name: value }))
          tagListOrigin.value = _.uniq(_.flattenDeep(tags)).map((value) => ({ checked: false, name: value }))
          favoriteMap.value[obj.id] = {
            name: obj.name,
            favorite: false,
          }
        })
      })
      .then(async () => {
        await axios.get(`${API_USER_URL}/${currentUserId}`).then((res) => {
          if (!_.isEmpty(res.data.favoriteShop)) {
            // A의 키를 사용하여 B의 favorite 값을 변경
            _.forEach(res.data.favoriteShop, (name, key) => {
              const item = favoriteMap.value[key]
              if (item) {
                item.favorite = true
              }
            })
          }
        })
      })

    return {
      serverUrl: SERVER_URL,
      categories: CATEGORIES,
      toast: useToast(),
      currentUser,
      currentUserId,
      axios,
      shopList,
      menuList,
      address: ref(''),
      shopId: ref(''),
      searchAddress: ref(''),
      isShowDetail: ref(false),
      detailInfo: ref(),
      op,
      onShowFilter,
      onHideFilter,
      menuCount: ref(0),
      reviewList: ref([]),
      responsiveOptions,
      isShowReview: ref(false),
      review: ref(''),
      reviewImage: ref(''),
      rating: ref(),
      selectedAdvantages: ref(),
      advantages: ADVANTAGES,
      selectedDisAdvantages: ref(),
      disAdvantages: DISADVANTAGES,
      selectedReview: ref(),
      isReviewEdit: ref(false),
      isSearchOpening: ref(false),
      isSearchFavorite: ref(false),
      tagList,
      tagListOrigin,
      checkedTagList: ref([]),
      searchMenu: ref(''),
      selectedCategories: ref(),
      priceRange: ref(0),
      isReviewCollapsed: ref({}),
      reservationUser: ref(),
      reservationUserOption: ref(['2명', '3명', '4명', '5명', '6명', '7명']),
      reservationDate: ref(new Date()),
      minDate: ref(new Date()),
      maxDate: ref(moment().add(7, 'days').toDate()),
      reservationTime: ref(),
      reservationRequest: ref(''),
      existReservation: ref([]),
      favoriteMap,
    }
  },
  methods: {
    setDateFormat(data) {
      return moment(data).format('YYYY-MM-DD')
    },
    onClickCollapse(id) {
      this.isReviewCollapsed[id] = !this.isReviewCollapsed[id]
    },
    onSearchTag(data) {
      if (data.checked) {
        if (!this.checkedTagList.includes(data.name)) {
          this.checkedTagList.push(data.name)
        }
      } else {
        if (this.checkedTagList.includes(data.name)) {
          this.checkedTagList = _.filter(this.checkedTagList, (item) => item !== data.name)
        }
      }

      const filterTags = this.checkedTagList.join(';')
      this.axios.get(`${API_URL}?tags=${filterTags}`).then((res) => {
        this.shopList = res.data.content
      })

      // 필터 초기화
      this.onClearFilter()
    },
    getShopList() {
      return this.axios.get(API_URL).then((res) => res.data.content)
    },
    onSearchOpenShop() {
      this.getShopList().then((result) => {
        this.shopList = _.filter(result, { shopOpen: true })
        this.isSearchOpening = true
      })
    },
    onSearchFavorite() {
      this.axios.get(`${API_USER_URL}/${this.currentUserId}`).then((res) => {
        this.shopList = _.filter(this.shopList, (item) => res.data.favoriteShop[item.id])
        this.isSearchFavorite = true
      })
    },
    onCancelFilter() {
      this.getShopList().then((result) => {
        this.shopList = result
        this.isSearchOpening = false
        this.isSearchFavorite = false
        this.searchAddress = ''

        // 검색 필터 초기화
        this.searchMenu = ''
        this.selectedCategories = null
        this.priceRange = 0
        this.tagList = _.cloneDeep(this.tagListOrigin)
      })
    },
    // 음식점명으로 검색
    onSearchMap() {
      this.axios
        .get(API_URL, {
          params: {
            name: this.searchAddress,
          },
        })
        .then((res) => {
          this.shopList = res.data.content
        })
    },
    searchMenuByFilter(data, query) {
      const { name, price, categories } = query

      return _.filter(data, (item) => {
        const nameMatch = name ? item.name.toLowerCase().includes(name.toLowerCase()) : true
        const priceMatch = price ? item.price <= price : true
        const categoriesMatch = categories ? _.every(categories, (category) => item.categories[category]) : true

        return nameMatch && priceMatch && categoriesMatch
      })
    },
    // 필터 조건으로 검색
    onSearchFilter() {
      // 태그 검색 조건 초기화
      this.tagList = _.cloneDeep(this.tagListOrigin)

      const filterMenu = this.searchMenu
      const filterPrice = this.priceRange
      const filterCategories = this.selectedCategories
      this.onHideFilter()

      // 메뉴, 카테코리, 가격 -> 메뉴로 검색
      let searchParams = {}
      if (!_.isEmpty(filterMenu)) {
        searchParams['name'] = filterMenu
      }
      if (filterPrice !== 0) {
        searchParams['price'] = filterPrice
      }
      if (!_.isEmpty(filterCategories)) {
        searchParams['categories'] = filterCategories
      }

      this.axios(`${API_MENU_URL}`).then((res) => {
        const result = this.searchMenuByFilter(res.data.content, searchParams)
        this.shopList = _.uniqBy(result, (item) => item.shop.id).map((item) => item.shop)
      })
    },
    // 필터 조건 초기화
    onClearFilter() {
      this.searchMenu = ''
      this.selectedCategories = null
      this.priceRange = 0
    },
    onGetItemInfo(data) {
      this.shopId = `${_.random(0, 99999)}-${data.id}`

      // 패널에서 상점 클릭 시, 모달창 띄워주기
      setTimeout(() => {
        this.detailInfo = data
        this.isShowDetail = true

        // this.getMenuCount()
        this.getReviewList()
        this.getReserveList()
      }, 500)
    },
    onMarkerClick(data) {
      this.detailInfo = data
      this.isShowDetail = true

      // this.getMenuCount()
      this.getReviewList()
      this.getReserveList()
    },
    setTimeFormat(data) {
      return `${data[0]}~${data[1]}`
    },
    getCategories(data) {
      const selectedKeys = _.pickBy(data, (value) => value)
      const selectedNames = _.map(selectedKeys, (value, key) => {
        const category = _.find(this.categories, { key })
        return category ? category.name : null
      })

      return selectedNames.join(',')
    },
    initReviewData() {
      this.review = ''
      this.reviewImage = null
      this.rating = 0
      this.selectedAdvantages = null
      this.selectedDisAdvantages = null
      this.selectedReview = null
    },
    isEmptyImage(data) {
      return _.isEmpty(data)
    },
    // 찜하기
    onAddFavorite(data) {
      this.favoriteMap[data.id].favorite = !this.favoriteMap[data.id].favorite

      // 필터링된 항목을 원하는 형식으로 매핑
      const result = _.mapValues(
        _.pickBy(this.favoriteMap, (item) => item.favorite),
        (item) => item.name,
      )

      this.axios.put(`/api/user/${this.currentUserId}/favoriteShop`, result).then(() => {})
    },
    onTabClick(data) {
      // 리뷰 탭 클릭 시, 리스트 화면으로 돌아가기 + 기존 데이터 초기화
      if (data.index === 2) {
        this.isShowReview = false
        this.initReviewData()
      }
    },
    setAdTagIcon(data) {
      return _.find(ADVANTAGES, ['code', data]).icon
    },
    getMenuCount() {
      this.axios.get(`${API_URL}/${this.detailInfo.id}/menu/count`).then((res) => {
        this.menuCount = res.data
      })
    },
    getReserveList() {
      this.axios.get(API_RESERVE_URL).then((res) => {
        this.existReservation = _.filter(res.data.content, (item) => {
          return item.shop.id === this.detailInfo.id
        }).map((data) => {
          return `${data.startDate} ${data.startTime}`
        })
      })
    },
    getReviewList() {
      this.axios.get(`${API_URL}/${this.detailInfo.id}/review`).then((res) => {
        this.reviewList = res.data
        _.forEach(res.data, (_data) => {
          this.isReviewCollapsed[_data.id] = true
        })
      })
    },
    getReviewIndex(dataId) {
      this.axios(`${API_REVIEW_URL}/${dataId}`).then((res) => {
        this.review = res.data.content
        this.reviewImage = res.data.image1
        this.rating = Number(res.data.rating)
        this.selectedAdvantages = _.filter(ADVANTAGES, (item) => res.data.advantages.includes(item.code))
        this.selectedDisAdvantages = _.filter(DISADVANTAGES, (item) => res.data.disadvantage.includes(item.code))
      })
    },
    onClickReview() {
      this.isShowReview = true
      this.isReviewEdit = false
    },
    onClickReviewEdit(data) {
      this.isShowReview = true
      this.isReviewEdit = true
      this.selectedReview = data
      this.getReviewIndex(data.id)
    },
    onSelectFile(data) {
      this.reviewImage = data.files[0]
    },
    createForm(params) {
      const formData = new FormData()
      const headers = { 'Content-Type': 'application/json' }

      formData.append('image1', params.image1)
      formData.append('data', new Blob([JSON.stringify(_.omit(params, ['image1']))], { type: 'application/json' }))
      headers['Content-Type'] = 'multipart/form-data'

      return { formData, headers }
    },
    onSaveReview() {
      const params = {
        shopId: this.detailInfo.id,
        content: this.review,
        image1: this.reviewImage,
        rating: this.rating,
        advantages: _.map(this.selectedAdvantages, 'name'),
        disadvantage: _.map(this.selectedDisAdvantages, 'name'),
      }
      const { formData, headers } = this.createForm(params)

      const axiosConfig = {
        method: this.isReviewEdit ? 'put' : 'post',
        url: this.isReviewEdit ? `${API_REVIEW_URL}/${this.selectedReview.id}` : API_REVIEW_URL,
        data: formData,
        headers,
      }

      this.axios(axiosConfig).then(() => {
        this.isShowReview = false
        this.toast.success('리뷰를 작성했습니다.')

        // 기존 데이터 초기화 및 getList
        this.initReviewData()
        this.getReviewList()
        this.getShopList().then((result) => {
          this.shopList = result
        })
      })
    },
    onDeleteReview(data) {
      this.axios.delete(`${API_REVIEW_URL}/${data.id}`).then(() => {
        this.isShowReview = false
        this.toast.success('리뷰를 삭제했습니다.')

        // 기존 데이터 초기화 및 getList
        this.initReviewData()
        this.getReviewList()
        this.getShopList().then((result) => {
          this.shopList = result
        })
      })
    },
    initReserveData() {
      this.reservationUser = 0
      this.reservationDate = new Date()
      this.reservationTime = null
      this.reservationRequest = ''

      this.eventBus.emit('reserve-add', '')
    },
    getReservationTimeOption(data) {
      const times = data.times
      const startHour = parseInt(times[0].split(':')[0])
      const endHour = parseInt(times[1].split(':')[0])

      const hoursRange = _.range(startHour, endHour + 1)
      return _.map(hoursRange, (hour) => _.padStart(hour, 2, '0') + ':00')
    },
    onDisabledReservationTime(data) {
      const startDate = moment(this.reservationDate).format('YYYY-MM-DD')
      const reserveTime = `${startDate} ${data}`
      return this.existReservation.includes(reserveTime) || moment().isSameOrAfter(reserveTime)
    },
    onClickReservationTime(e, data) {
      const selector = document.querySelectorAll('.select-button')
      _.forEach(selector, (value) => {
        value.classList.remove('active')
      })
      e.target.classList.add('active')
      this.reservationTime = data
    },
    onAddReserve() {
      if (_.isEmpty(this.reservationUser)) {
        this.toast.warning('예약 인원을 선택해주세요.')
        return
      }
      if (_.isEmpty(this.reservationTime)) {
        this.toast.warning('예약 시간을 선택해주세요.')
        return
      }

      this.axios
        .post('/api/reservation', {
          userId: this.currentUserId,
          shopId: this.detailInfo.id,
          startDate: this.setDateFormat(this.reservationDate),
          startTime: this.reservationTime,
          person: parseInt(this.reservationUser.split('명')[0]),
          option: this.reservationRequest,
        })
        .then(() => {
          this.toast.success('예약을 완료했습니다.')
          this.initReserveData()
          this.isShowDetail = false
        })
        .catch(() => {
          this.toast.error('이미 예약 건이 있습니다.')
          this.initReserveData()
        })
    },
  },
})
</script>

<style lang="scss">
.HomeView {
  display: flex;
  overflow-y: hidden;
}
/* 탭 스타일 */
.p-tabview-nav {
  background-color: #f5f5f5;
  border-bottom: 1px solid #ddd;
}

.p-tabview-nav-link {
  padding: 10px 20px;
  color: #333;
  border: none;
  border-radius: 0;
  background-color: transparent;
  transition: background-color 0.3s ease-in-out;
}

.p-tabview-nav-link:hover {
  background-color: #ddd;
}

.p-tabview-nav-link.p-highlight {
  background-color: #4285f4; /* 클릭한 탭 색상 */
  color: #fff; /* 클릭한 탭 폰트 색상 */
  font-weight: bold; /* 클릭한 탭 글자 강조 */
}

.p-tabview-nav-link:not(.p-highlight) {
  opacity: 0.7; /* 클릭되지 않은 탭 투명도 조절 */
}

/* 탭 패널 스타일 */
.p-tabview-panel {
  padding: 1.25rem 0 1.25rem 0;
}

.p-tabview .p-tabview-nav li.p-highlight .p-tabview-nav-link {
  //background-color: #4285f4 !important;
  color: #fff !important;
  border-color: #10b981;
  background-color: #10b981;
  font-weight: bold !important;
}

/* 홈 탭 컨텐츠 스타일 */
.home-content {
  font-size: 16px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;

  .content-info {
    width: 100%;
    display: flex;
    flex-direction: column;
    row-gap: 13px;

    .content-info-item {
      i {
        width: 20px;
      }
    }
  }
  .content-image {
    width: 100%;
    display: flex;
    padding: 30px 0 0;
    text-align: center;
    overflow: hidden;
    justify-content: space-evenly;

    img {
      width: 100%;
      height: auto;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }
  }
}
.ps {
  height: 400px;
}
/* 메뉴 탭 컨텐츠 스타일 */
.menu-section {
  .menu-section-header {
    display: flex;
    margin-top: 5px;
    justify-content: space-between;
    align-items: center;
    margin-right: 5px;
    position: relative;
    top: 5px;

    .menu-count {
      color: rgb(147, 147, 150);
    }
  }
  .menu-content {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;
    padding-right: 20px;
    padding-top: 5px;
  }
  .menu-item {
    display: grid;
    grid-template-rows: 1fr 65px 75px 40px 35px;
    max-width: 200px;
    text-align: center;
    margin-bottom: 20px;
  }
  .menu-item img {
    width: 100%;
    height: auto;
    border-radius: 8px;
    transition: transform 0.3s ease-in-out;
  }
  .img-wrapper:hover img {
    transform: scale(1.1);
  }
  .menu-item p {
    margin: 10px 0;
    font-weight: bold;
  }
}
/* 리뷰 탭 컨텐츠 스타일 */
.review-section {
  .review-section-header {
    display: flex;
    margin-top: 5px;
    justify-content: space-between;
    align-items: center;
    margin-right: 5px;

    .review-count {
      color: rgb(147, 147, 150);
    }
  }
  .review-section-content {
    padding-top: 5px;

    .p-carousel {
      img {
        max-width: 200px;
        width: 100%;
        height: auto;
        border-radius: 8px;
        transition: transform 0.3s ease-in-out;
        box-shadow:
          0 4px 10px #00000008,
          0 0 2px #0000000f,
          0 2px 6px #0000001f !important;
      }
    }
    .carousel-image-wrapper:hover img {
      transform: scale(1.1);
    }
    .carousel-item {
      padding: 10px 25px 5px;
      border-radius: 30px;
    }
    //.carousel-item:hover {
    //  background: rgb(236 246 235);
    //  cursor: pointer;
    //}
    .carousel-item.disabled {
      pointer-events: none;
    }
    .carousel-user-wrapper {
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 10px;
    }
    .carousel-review-content {
      position: relative;
      margin-top: 15px;
      font-size: 1.2rem;
      font-weight: 600;
      color: #555558;
      row-gap: 5px;

      .collapsed-text {
        cursor: pointer;
        overflow: hidden;
        max-height: 3em; /* 2줄까지의 높이 */
        line-height: 1.5em; /* 라인 높이 */
      }
      .ellipsis-text {
        display: -webkit-box;
        -webkit-box-orient: vertical;
        overflow: hidden;
        max-height: 3em; /* 2줄까지의 높이 */
        line-height: 1.5em; /* 라인 높이 */
        -webkit-line-clamp: 2; /* 라인 개수 */
        text-overflow: ellipsis;
        white-space: normal;
      }
      .full-text {
        white-space: normal;
      }
      .collapse-icon {
        display: inline;
        cursor: pointer;
      }
    }
  }
}
.review-content-user {
  display: flex;
  flex-direction: column;
  row-gap: 20px;
  padding-right: 30px;
  margin-bottom: 5px;

  span {
    font-size: 1.2rem;
    font-weight: 600;
    color: rgb(34, 34, 37);
    padding-bottom: 8px;
  }

  .review-content {
    margin-left: 5px;

    .p-rating {
      svg {
        color: #0e9d6e;
      }
    }
    .p-button {
      color: #ffffff;
      background: #10b981;
      border: 1px solid #10b981;

      span {
        padding: 0.25rem 0.25rem;
        color: #ffffff;
      }
    }
    .p-button-success {
      color: #ffffff;
      background: #10b981;
      border: 1px solid #10b981;
      font-size: 1.2rem;
    }
  }
}
/* 정보 탭 컨텐츠 스타일 */
.info-content {
  /* 설명란에 대한 스타일링을 추가할 수 있습니다. */
}
.p-togglebutton.p-button:not(.p-disabled).p-focus {
  outline: 0 none;
  outline-offset: 0;
  box-shadow: 0 0 0 0.2rem #a7f3d0;
  border-color: #10b981;
}
.p-togglebutton.p-button.p-highlight {
  color: #ffffff;
  background: #22c55e;
  border: 1px solid #22c55e;
}
.p-togglebutton.p-button.p-highlight:hover {
  color: #ffffff;
  background: #22c55e;
  border: 1px solid #22c55e;
}
.toggle-button {
  width: 90px;
  font-size: 0.8rem;
  padding: 5px;
  margin: 0;
  color: #22c55e;
  border: 1px solid;
}
.filter-section {
  .p-checkbox .p-checkbox-box.p-highlight {
    border-color: #10b981;
    background: #10b981;
  }
  .p-checkbox:not(.p-checkbox-disabled) .p-checkbox-box.p-focus {
    outline: 0 none;
    outline-offset: 0;
    box-shadow: 0 0 0 0.2rem #a7f3d0;
    border-color: #10b981;
  }
  .p-checkbox:not(.p-checkbox-disabled) .p-checkbox-box:hover {
    border-color: #10b981;
  }
  .p-slider .p-slider-range {
    background: #10b981;
  }
  .p-slider .p-slider-handle {
    height: 1.143rem;
    width: 1.143rem;
    background: #ffffff;
    border: 2px solid #10b981;
    border-radius: 50%;
    transition:
      background-color 0.2s,
      color 0.2s,
      border-color 0.2s,
      box-shadow 0.2s;
  }
  .p-slider:not(.p-disabled) .p-slider-handle:hover {
    background: #10b981;
    border-color: #10b981;
  }
  .p-slider .p-slider-handle:focus {
    outline: 0 none;
    outline-offset: 0;
    box-shadow: 0 0 0 0.2rem #a7f3d0;
  }
}
.reservation-section {
  .p-selectbutton .p-button:focus {
    outline: 0 none;
    outline-offset: 0;
    box-shadow: 0 0 0 0.2rem #a7f3d0;
    border-color: #10b981;
  }
  .p-selectbutton .p-button:not(.p-disabled).p-focus {
    outline: 0 none;
    outline-offset: 0;
    box-shadow: 0 0 0 0.2rem #a7f3d0;
    border-color: #10b981;
  }
  .p-selectbutton .p-button.p-highlight {
    color: #ffffff;
    background: #22c55e;
    border: 1px solid #22c55e;
  }
  .p-selectbutton .p-button.p-highlight:hover {
    color: #ffffff;
    background: #22c55e;
    border: 1px solid #22c55e;
  }
  .select-buttons-container {
    .select-button.active {
      color: #ffffff;
      background: #22c55e;
      border: 1px solid #22c55e;
    }
  }
}
</style>
