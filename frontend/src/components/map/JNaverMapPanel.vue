<template>
  <div class="JNaverMapPanel">
    <div class="panel-content-wrapper">
      <perfect-scrollbar>
        <ul>
          <li v-for="(shop, index) of shopInfo" :key="index" class="panel-content-item" @click="onClickItem(shop)">
            <div class="item-name">{{ shop.name }}</div>
            <div v-for="(tag, idx) of shop.tags.split(';')" :key="idx" class="item-tag">#{{ tag }}</div>
            <div class="p-d-flex">
              <div class="item-times">{{ setTimeStatus(shop.times) }} {{ setTimeFormat(shop.times) }}</div>
              <div class="item-rate">
                <i class="fa fa-star p-mr-1" style="color: #fc4c4e" />
                {{ shop.rating }}
              </div>
            </div>
            <div class="item-address">{{ shop.address }}</div>
            <div class="item-image">
              <img
                v-if="shop.image1"
                :src="`${serverUrl}/images/_shop/${shop.image1}`"
                :alt="shop.image1"
                style="width: auto; height: 100px; margin-right: 10px"
              />
              <img
                v-if="shop.image2"
                :src="`${serverUrl}/images/_shop/${shop.image2}`"
                :alt="shop.image2"
                style="width: auto; height: 100px"
              />
            </div>
            <div class="item-like">
              <span class="menu-count"><i class="fa fa-table-columns" /> {{ menuCountList[shop.id] }} </span>
              <span class="review-count"><i class="fa fa-comment" /> {{ reviewCountList[shop.id] }} </span>
            </div>
          </li>
        </ul>
      </perfect-scrollbar>
    </div>
  </div>
</template>

<script>
import _ from 'lodash'
const API_URL = '/api/shop'
const SERVER_URL = import.meta.env.VITE_BASE_URL

export default defineComponent({
  name: 'JNaverMapPanel',
  props: {
    shopList: {
      type: Array,
      default: () => [],
    },
  },
  emits: ['itemInfo'],
  setup() {
    const axios = inject('$axios')
    return {
      axios,
      serverUrl: SERVER_URL,
      shopInfo: ref([]),
      imageList: ref([]),
      menuCountList: ref({}),
      reviewCountList: ref({}),
    }
  },
  watch: {
    shopList(value) {
      this.shopInfo = value
      this.imageList = _.flatMap(value, ({ image1, image2 }) => [{ src: image1 }, { src: image2 }])

      this.menuCountList = {}
      this.reviewCountList = {}
      _.forEach(value, (shopInfo) => {
        this.getMenuCount(shopInfo)
        this.getReviewCount(shopInfo)
      })
    },
  },
  methods: {
    setTimeFormat(data) {
      return `${data[0]}~${data[1]}`
    },
    setTimeStatus(openingHours) {
      const currentDateTime = new Date()
      const currentHour = currentDateTime.getHours()
      const currentMinute = currentDateTime.getMinutes()

      const [openTime, closeTime] = openingHours.map((time) => {
        const [hour, minute] = time.split(':').map(Number)
        return { hour, minute }
      })

      const openingTime = openTime.hour * 60 + openTime.minute
      const closingTime = closeTime.hour * 60 + closeTime.minute
      const currentTime = currentHour * 60 + currentMinute

      if (currentTime < openingTime) {
        return '영업전'
      } else if (currentTime >= openingTime && currentTime <= closingTime) {
        return '영업중'
      } else {
        return '영업종료'
      }
    },
    // 선택한 아이템의 정보 emit
    onClickItem(data) {
      this.$emit('itemInfo', data)
    },
    // 메뉴 개수
    getMenuCount(shopInfo) {
      this.axios.get(`${API_URL}/${shopInfo.id}/menu/count`).then((res) => {
        this.menuCountList[shopInfo.id] = res.data
      })
    },
    // 리뷰 개수
    getReviewCount(shopInfo) {
      this.axios.get(`${API_URL}/${shopInfo.id}/review/count`).then((res) => {
        this.reviewCountList[shopInfo.id] = res.data
      })
    },
  },
})
</script>

<style lang="scss">
.JNaverMapPanel {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  flex: 0 0 auto;
  flex-direction: column;
  box-sizing: content-box;

  .panel-header {
    font-size: 1.7rem;
    text-align: center;
  }

  .panel-content-wrapper {
    display: flex;
    height: 100%;
    flex: 1 1 auto;
    flex-direction: column;

    .ps {
      height: 800px;
    }

    ul {
      padding: 0;
      margin: 0;
      height: 800px;
    }
    li {
      padding-right: 25px;
      padding-left: 25px;
      border-radius: 30px;
      margin-bottom: 10px;
      margin-right: 20px;
    }
    li:hover {
      background: rgb(236 246 235);
      cursor: pointer;
    }
  }
  .panel-content-item {
    padding: 20px;
    list-style-type: none;

    .item-name {
      display: block;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: normal;
      font-size: 1.7rem;
      color: #0068c3;
    }

    .item-tag {
      display: inline-flex;
      padding-bottom: 5px;
      color: #939396;
    }

    .item-times {
      font-size: 1.2rem;
      font-weight: bold;
      padding: 5px 0 5px 0;
    }

    .item-rate {
      display: flex;
      align-items: center;
      padding-left: 10px;
    }

    .item-address {
      padding-bottom: 10px;
    }

    .item-image {
      display: flex;
      padding: 0;
      text-align: center;
      overflow: hidden;

      img {
        width: 100%;
        height: auto;
        border-radius: 8px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        transition: transform 0.3s ease-in-out;
      }

      img:hover {
        transform: scale(1.1);
      }
    }

    .item-like {
      padding-top: 10px;
      display: flex;
      column-gap: 5px;

      .menu-count {
        color: #76db9b;
      }
      .review-count {
        color: #ffaca7;
      }
      .like-count {
        color: rgb(252, 76, 78);
      }
    }
  }
}
</style>
