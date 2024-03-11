<template>
  <div v-show="isMapShow" id="map" :style="{ width: '100%', height }"></div>
</template>

<script>
import _ from 'lodash'
import { useToast } from 'vue-toastification'

const MAP_KEY = import.meta.env.VITE_MAP_KEY
const SERVER_URL = import.meta.env.VITE_BASE_URL

export default {
  props: {
    isMapShow: {
      type: Boolean,
      default: true,
    },
    // 음식점 정보
    shopList: {
      type: Array,
      default: () => [],
    },
    // 주소 검색
    address: {
      type: String,
      default: '',
    },
    // 음식점명 검색
    selectedId: {
      type: String,
      default: '',
    },
    height: {
      type: String,
      default: '100%',
    },
  },
  emits: ['markerClick', 'searchResult'],
  setup() {
    return {
      serverUrl: SERVER_URL,
      toast: useToast(),
      isInitMap: ref(false),
      naverMap: ref(),
      naverMarker: ref([]),
    }
  },
  watch: {
    address(value) {
      const _this = this
      this.getCoordinate(_this, value.split('-')[1])
    },
    // random key 파싱
    selectedId(value) {
      this.setMarker(Number(value.split('-')[1]))
    },
    shopList() {
      if (this.isInitMap) {
        const _this = this
        this.setMarkerList(_this)
      }
    },
  },
  mounted() {
    this.loadNaverMap()
  },
  methods: {
    /** 네이버 OPEN API 연동 */
    loadNaverMap() {
      // script load 대기 시간을 고려해서, 안정적으로 load 할 수 있도록 setTimeout 적용
      setTimeout(() => {
        const script = document.createElement('script')
        script.src = `https://openapi.map.naver.com/openapi/v3/maps.js?ncpClientId=${MAP_KEY}&submodules=geocoder`
        script.async = true
        script.defer = true
        document.head.appendChild(script)

        script.onload = async () => {
          this.isInitMap = true
          const _this = this

          await this.setMap(_this)
          await this.setMarkerList(_this)
        }
      }, 100)
    },
    /** @Map 객체 생성  */
    setMap(_this) {
      this.naverMap = new naver.maps.Map('map', {
        center: new naver.maps.LatLng(37.3947948, 127.1109897), // 판교역 기준으로 지도 생성
        zoom: 15, // 지도 초기 줌 레벨
        maxZoom: 17, // 지도 최대 줌 레벨
        minZoom: 14, // 지도 최소 줌 레벨
        zoomControl: true, // 줌 컨트롤 표시 여부
        mapTypeControl: true, // 지도 유형 컨트롤 표시 여부
        zoomControlOptions: {
          position: naver.maps.Position.TOP_RIGHT, // 줌 컨트롤의 위치를 우측 상단으로 배치함
        },
        mapDataControl: false, // 지도 데이터 저작권 컨트롤 표시 여부
      })

      // 지도 줌 인/아웃 시 마커 업데이트 이벤트 핸들러
      naver.maps.Event.addListener(this.naverMap, 'zoom_changed', () => {
        _this.updateMarker(_this.naverMap, _this.naverMarker)
      })
      // 지도 드래그 시 마커 업데이트 이벤트 핸들러
      naver.maps.Event.addListener(this.naverMap, 'dragend', () => {
        _this.updateMarker(_this.naverMap, _this.naverMarker)
      })
    },
    /** @Marker 음식점 이름을 기준으로 마커 생성 */
    setMarker(dataId) {
      const selectedShop = _.find(this.shopList, ['id', dataId])
      if (selectedShop) {
        const selectedShopLatLng = new naver.maps.LatLng(selectedShop.latitude, selectedShop.longitude)
        // 마커 리스트에서 LatLng 비교
        _.forEach(this.naverMarker, (marker) => {
          // 기존 마커의 애니메이션 효과 제거
          marker.setAnimation(0)
          // 찾은 마커의 애니메이션 효과 추가
          if (selectedShopLatLng.equals(marker.getPosition())) {
            this.naverMap.setCenter(marker.getPosition())
            marker.setAnimation(1)
          }
        })
      } else {
        // 기존 마커의 애니메이션 효과 제거
        _.forEach(this.naverMarker, (marker) => {
          marker.setAnimation(0)
        })
      }
    },
    /** @Icon 마커의 커스텀 아이콘 생성 */
    setMarkerIcon(data) {
      return `
        <div class="marker-container">
          <div class="marker-wrapper">
            <div class="marker">
              <div class="icon_area">
                <span class="icon-item">
                  <div class="icon fa fa-utensils"></div>
                </span>
              </div>
              <div class="text_area">
                <div class="main_text">
                  <strong class="name">${data.name}</strong>
                </div>
                <div class="sub_text">
                  <span>
                    <div class="fa fa-star" style="color: #fc4c4e"></div>
                    ${data.rating}
                  </span>
                </div>
              </div>
            </div>
            <button type="button" class="btn_more"></button>
            <span class="dummy"></span>
          </div>
        </div>
      `
    },
    /** @Marker 전체 음식점 리스트 기준으로 마커 생성 */
    setMarkerList(_this) {
      // 기존에 생성되어 있는 마커 삭제
      _.forEach(this.naverMarker, (_marker) => {
        this.hideMarker(this.naverMap, _marker)
      })

      const markerList = _.map(_this.shopList, ({ longitude, latitude }) => ({
        x: parseFloat(longitude),
        y: parseFloat(latitude),
      }))

      _.forEach(markerList, (value, index) => {
        const _position = new naver.maps.LatLng(value.y, value.x)
        const _markerInfo = this.shopList[index]
        const _marker = new naver.maps.Marker({
          map: this.naverMap,
          position: _position,
          zIndex: 100,
          icon: {
            content: this.setMarkerIcon(_markerInfo),
          },
        })
        this.naverMarker.push(_marker)

        naver.maps.Event.addListener(_marker, 'click', function () {
          // 기존 마커의 애니메이션 효과 제거
          _.forEach(_this.naverMarker, (marker) => {
            marker.setAnimation(0)
          })

          _this.$emit('markerClick', _markerInfo)
        })
      })
    },
    /** @Marker 객체 업데이트 */
    updateMarker(map, markers) {
      let marker = null
      let position = null

      // 현재 지도의 경계선 구하기
      const mapBounds = this.naverMap.getBounds()

      _.forEach(markers, (value, index) => {
        marker = markers[index]
        position = marker.getPosition()

        if (mapBounds.hasLatLng(position)) {
          this.showMarker(map, marker)
        } else {
          this.hideMarker(map, marker)
        }
      })
    },
    /** @Marker 보여주기 */
    showMarker(map, marker) {
      marker.setMap(map)
    },
    /** @Marker 숨기기 */
    hideMarker(map, marker) {
      marker.setMap(null)
    },
    /** @Coordinate 주소 -> 위도/경도 */
    async getCoordinate(_this, _address) {
      if (_.isEmpty(_address)) {
        _this.toast.warning('주소를 입력해주세요.')
        return
      }

      await naver.maps.Service.geocode({ query: _address }, function (status, response) {
        if (status === naver.maps.Service.Status.ERROR) {
          _this.toast.error('지도를 불러오는데 문제가 발생했습니다.')
          return
        }

        // 성공 시의 response 처리
        const result = response.v2.addresses // 검색 결과의 컨테이너
        if (result.length > 0) {
          const item = response.v2.addresses[0]

          _this.$emit('searchResult', {
            address: item.roadAddress,
            latitude: item.y,
            longitude: item.x,
          })
        } else {
          _this.toast.warning('검색 결과가 없습니다.')
        }
      })
    },
  },
}
</script>

<style lang="scss">
.marker-container {
  cursor: pointer;
}
.marker-wrapper {
  position: absolute;
  bottom: 3px;
  left: -18px;
  z-index: 10;
}
.marker {
  display: table;
  table-layout: auto;
  padding: 4px;
  border-radius: 23px;
  border: 1px solid #0e8962;
  background: rgb(255, 255, 255);
}
.icon_area {
  overflow: hidden;
  position: relative;
  z-index: 1;
  border-radius: 50%;
}
.icon-item {
  overflow: hidden;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  font-size: 1px;
  line-height: 1px;
  color: transparent;
  vertical-align: top;
  background-size: 285px 276px;
  background-position: -29px -199px;
  background-color: #6bc26b;
  width: 28px;
  height: 28px;
  mask-image: none;

  .icon {
    color: rgb(255, 255, 255);
    font-size: 1rem;
  }
}
.blind {
  overflow: hidden !important;
  position: absolute !important;
  width: 1px !important;
  height: 1px !important;
  margin: -1px !important;
  color: transparent !important;
  clip: rect(0px, 0px, 0px, 0px) !important;
}
.text_area {
  overflow: hidden;
  white-space: nowrap;
  padding: 0 9px 0 5px;
  display: table-cell;
  vertical-align: middle;
}
.main_text {
  padding: 2px 0;
  margin: -2px 0;
  max-width: 162px;
  overflow: hidden;
  line-height: 16px;
  text-overflow: ellipsis;
}
.sub_text {
  max-width: 162px;
  overflow: hidden;
  text-overflow: ellipsis;
  height: 12px;
  font-size: 10px;
  color: rgb(102, 102, 102);
  line-height: 12px;
  letter-spacing: -0.27px;
}
.btn_more {
  cursor: pointer;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 23px;
  margin: 0;
  padding: 0;
  appearance: none;
  border: 0 #10b981;
  background-color: transparent;
}
.btn_more:hover {
  background: rgba(236, 246, 235, 0.5);
  cursor: pointer;
}
.btn_more::after {
  position: absolute;
  bottom: 1px;
  left: 12px;
  width: 15px;
  height: 15px;
  transform: rotate(55deg) skew(17deg);
  content: '';
}
.dummy {
  position: absolute;
  bottom: -5px;
  left: 15px;
  width: 0;
  height: 0;
  border-width: 6px 4px 0;
  border-style: solid;
  border-color: #10b981 transparent transparent;
  pointer-events: none;
}
.dummy::before {
  opacity: 0.1;
  position: absolute;
  bottom: -2px;
  left: -5px;
  width: 10px;
  height: 3px;
  background-color: rgb(0, 0, 0);
  filter: blur(1px);
  content: '';
}
.dummy::after {
  width: 0;
  height: 0;
  border-width: 9px 6px 0;
  border-style: solid;
  border-color: rgb(255, 255, 255) transparent transparent;
  position: absolute;
  top: -11px;
  left: -6px;
  content: '';
}
</style>
