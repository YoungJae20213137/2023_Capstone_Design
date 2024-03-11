<template>
  <div class="card">
    <JDataTable
      v-model:modelValue="selectedProduct"
      :products="products"
      :columns="columns"
      @on-row-dbl-click="onRowDblClick"
    >
      <template #crud-button>
        <Button icon="pi pi-plus" class="p-mr-2" @click="onAddShop" />
        <Button icon="pi pi-pencil" class="p-mr-2" @click="onEditShop" />
        <Button icon="pi pi-times" @click="onDeleteShop" />
      </template>
      <template #image1="slotProps">
        <template v-if="isEmptyImage(slotProps.data.image1)">
          <img class="p-mr-2" src="@/assets/images/noImage.png" alt="noImage.png" height="45px" />
        </template>
        <template v-else>
          <img
            class="p-mr-2"
            :src="`${serverUrl}/images/_shop/${slotProps.data.image1}`"
            :alt="slotProps.data.image1"
            height="45px"
          />
        </template>
      </template>
      <template #rating="slotProps"> {{ slotProps.data.rating }}점 </template>
      <template #times="slotProps">
        {{ setTimeFormat(slotProps.data.times) }}
      </template>
      <template #menuList="slotProps">
        {{ menuList[slotProps.data.id] }}
      </template>
      <template #tags="slotProps">
        <Tag
          v-for="(tag, idx) of slotProps.data.tags.split(';')"
          :key="idx"
          class="p-mr-2 p-mb-2"
          :value="tag"
          severity="info"
        ></Tag>
      </template>
      <template #createdAt="slotProps">
        {{ setDateFormat(slotProps.data.createdAt) }}
      </template>
    </JDataTable>
  </div>
</template>

<script>
import _ from 'lodash'
import moment from 'moment'
import { useRouter } from 'vue-router'
import useCrud from '@/composables/useCrud.js'

const API_URL = '/api/shop'
const SERVER_URL = import.meta.env.VITE_BASE_URL

export default {
  name: 'ShopList',
  setup() {
    /** @data setting */
    const $axios = inject('$axios')
    const router = useRouter()
    const serverUrl = SERVER_URL

    /** @data component */
    const products = ref()
    const selectedProduct = ref()
    const columns = [
      { field: 'name', header: '매장명', sortable: true, width: '120px' },
      { field: 'image1', header: '배너', width: '100px' },
      { field: 'rating', header: '평점', width: '80px' },
      { field: 'phoneNumber', header: '전화번호', sortable: true, width: '120px' },
      { field: 'address', header: '주소', sortable: true, width: '120px' },
      { field: 'times', header: '운영시간', sortable: true, width: '80px' },
      { field: 'menuList', header: '메뉴 목록', width: '120px' },
      { field: 'tags', header: '태그', width: '70px' },
      { field: 'description', header: '정보', sortable: true, width: '120px' },
      { field: 'createdAt', header: '생성일', sortable: true, width: '120px' },
    ]
    const menuList = ref({})

    const onRowDblClick = (data) => {
      router.push(`/routes/shop/${data.data.id}`)
    }

    const setTimeFormat = (data) => {
      return `${data[0]}~${data[1]}`
    }

    const setDateFormat = (data) => {
      return moment(data).format('YYYY-MM-DD hh:mm:ss')
    }

    const isEmptyImage = (data) => {
      return _.isEmpty(data)
    }

    $axios
      .get(`${API_URL}?size=100`)
      .then((res) => {
        products.value = res.data.content
      })
      .then(() => {
        /** 상점별 메뉴 목록 */
        _.forEach(products.value, async (obj) => {
          menuList.value[obj.id] = await $axios
            .get(`${API_URL}/${obj.id}/menu?size=100`)
            .then((res) => _.map(res.data, 'name').join(','))
        })
      })

    /** @composable CRUD */
    const { onAdd, onEdit, onDelete } = useCrud()
    const onAddShop = () => {
      onAdd('/routes/shop')
    }
    const onEditShop = () => {
      onEdit('/routes/shop', selectedProduct)
    }
    const onDeleteShop = async () => {
      await onDelete(API_URL, selectedProduct)
      await $axios.get(`${API_URL}?size=100`).then((res) => {
        products.value = res.data.content
      })
    }

    return {
      serverUrl,
      products,
      selectedProduct,
      columns,
      menuList,
      isEmptyImage,
      onRowDblClick,
      setTimeFormat,
      setDateFormat,
      onAddShop,
      onEditShop,
      onDeleteShop,
    }
  },
}
</script>

<style scoped lang="scss"></style>
