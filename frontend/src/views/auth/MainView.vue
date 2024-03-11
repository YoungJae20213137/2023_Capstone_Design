<template>
  <div class="card">
    <div class="p-inputgroup">
      <InputText v-model="address" placeholder="주소를 입력하세요" style="width: 350px" @keydown.enter="onSearchMap" />
      <Button label="검색" severity="success" @click="onSearchMap" />
    </div>
    <JTestMap :address="searchAddress" :marker-list="markerList" height="800px" />
  </div>
</template>

<script>
import _ from 'lodash'

const API_URL = '/api/shop'

export default defineComponent({
  name: 'NoRoleView',
  setup() {
    /** @data setting */
    const $axios = inject('$axios')

    /** @data component */
    let markerList = ref([])
    const address = ref('')
    const searchAddress = ref('')

    $axios.get(API_URL).then((res) => {
      markerList.value = _.map(res.data.content, ({ longitude, latitude }) => ({
        x: parseFloat(longitude),
        y: parseFloat(latitude),
      }))
    })

    const onSearchMap = () => {
      searchAddress.value = address.value
    }

    return { markerList, address, searchAddress, onSearchMap }
  },
})
</script>

<style scoped lang="scss"></style>
