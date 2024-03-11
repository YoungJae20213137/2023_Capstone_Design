<template>
  <div class="card">
    <JDataTable
      v-model:modelValue="selectedProduct"
      :products="products"
      :columns="columns"
      @on-row-dbl-click="onRowDblClick"
    >
      <template #crud-button>
        <Button icon="pi pi-plus" class="p-mr-2" @click="onAddMenu" />
        <Button icon="pi pi-pencil" class="p-mr-2" @click="onEditMenu" />
        <Button icon="pi pi-times" @click="onDeleteMenu" />
      </template>
      <template #image1="slotProps">
        <template v-if="isEmptyImage(slotProps.data.image1)">
          <img class="p-mr-2" src="@/assets/images/noImage.png" alt="noImage.png" height="45px" />
        </template>
        <template v-else>
          <img
            class="p-mr-2"
            :src="`${serverUrl}/images/_menu/${slotProps.data.image1}`"
            :alt="slotProps.data.image1"
            height="45px"
          />
        </template>
      </template>
      <template #shopName="slotProps">
        {{ slotProps.data.shop.name }}
      </template>
      <template #price="slotProps"> {{ setNumberFormat(slotProps.data.price) }}원 </template>
      <template #categories="slotProps">
        {{ setCategories(slotProps.data.categories) }}
      </template>
      <template #tags="slotProps">
        {{ setTags(slotProps.data.tags) }}
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
import { CATEGORIES } from '@/composables/useData.js'

const API_URL = '/api/menu'
const SERVER_URL = import.meta.env.VITE_BASE_URL

export default {
  name: 'MenuList',
  setup() {
    /** @data setting */
    const $axios = inject('$axios')
    const router = useRouter()
    const serverUrl = SERVER_URL

    /** @data component */
    const products = ref()
    const selectedProduct = ref()
    const columns = [
      { field: 'name', header: '메뉴명', sortable: true, width: '150px' },
      { field: 'image1', header: '사진', width: '100px' },
      { field: 'shopName', header: '매장명', width: '150px' },
      { field: 'price', header: '가격', sortable: true, width: '90px' },
      { field: 'categories', header: '카테고리', width: '150px' },
      { field: 'tags', header: '태그', width: '150px' },
      { field: 'description', header: '설명', sortable: true, width: '150px' },
      { field: 'createdAt', header: '생성일', sortable: true, width: '120px' },
    ]
    const categories = CATEGORIES

    const onRowDblClick = (data) => {
      router.push(`/routes/menu/${data.data.id}`)
    }

    const setNumberFormat = (data) => {
      const _number = String(data)
      return _number.replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    }

    const setCategories = (data) => {
      const pickedList = _.map(
        _.pickBy(categories, (category) => data[category.key]),
        'name',
      )
      return pickedList.join(', ')
    }

    const setTags = (data) => {
      if (data) {
        return data.split(';').join(',')
      }
    }

    const setDateFormat = (data) => {
      return moment(data).format('YYYY-MM-DD hh:mm:ss')
    }

    const isEmptyImage = (data) => {
      return _.isEmpty(data)
    }

    $axios.get(`${API_URL}?size=100`).then((res) => {
      products.value = res.data.content
    })

    /** @composable CRUD */
    const { onAdd, onEdit, onDelete } = useCrud()
    const onAddMenu = () => {
      onAdd('/routes/menu')
    }
    const onEditMenu = () => {
      onEdit('/routes/menu', selectedProduct)
    }
    const onDeleteMenu = async () => {
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
      isEmptyImage,
      onRowDblClick,
      setNumberFormat,
      setCategories,
      setTags,
      setDateFormat,
      onAddMenu,
      onEditMenu,
      onDeleteMenu,
    }
  },
}
</script>

<style scoped lang="scss"></style>
