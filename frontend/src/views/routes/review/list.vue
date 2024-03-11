<template>
  <div class="card">
    <JDataTable
      v-model:modelValue="selectedProduct"
      :products="products"
      :columns="columns"
      @on-row-dbl-click="onRowDblClick"
    >
      <template #crud-button>
        <Button icon="pi pi-plus" class="p-mr-2" @click="onAddReview" />
        <Button icon="pi pi-pencil" class="p-mr-2" @click="onEditReview" />
        <Button icon="pi pi-times" @click="onDeleteReview" />
      </template>
      <template #shop="slotProps">
        {{ slotProps.data.shop.name }}
      </template>
      <template #image1="slotProps">
        <template v-if="isEmptyImage(slotProps.data.image1)">
          <img class="p-mr-2" src="@/assets/images/noImage.png" alt="noImage.png" height="45px" />
        </template>
        <template v-else>
          <img
            class="p-mr-2"
            :src="`${serverUrl}/images/_review/${slotProps.data.image1}`"
            :alt="slotProps.data.image1"
            height="45px"
          />
        </template>
      </template>
      <template #rating="slotProps"> {{ slotProps.data.rating }}점 </template>
      <template #advantages="slotProps">
        {{ slotProps.data.advantages.join(',') }}
      </template>
      <template #disadvantage="slotProps">
        {{ slotProps.data.disadvantage.join(',') }}
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

const API_URL = '/api/review'
const SERVER_URL = import.meta.env.VITE_BASE_URL

export default {
  name: 'ReviewList',
  setup() {
    /** @data setting */
    const $axios = inject('$axios')
    const router = useRouter()
    const serverUrl = SERVER_URL

    /** @data component */
    const products = ref()
    const selectedProduct = ref()
    const columns = [
      { field: 'shop', header: '매장명', width: '250px' },
      { field: 'userId', header: '사용자명', sortable: true, width: '80px' },
      { field: 'content', header: '리뷰 내용', sortable: true, width: '200px' },
      { field: 'image1', header: '리뷰 이미지', width: '100px' },
      { field: 'rating', header: '평점', sortable: true, width: '80px' },
      { field: 'advantages', header: '좋았던 점', width: '120px' },
      { field: 'disadvantage', header: '아쉬웠던 점', width: '120px' },
      { field: 'createdAt', header: '생성일', sortable: true, width: '120px' },
    ]
    const onRowDblClick = (data) => {
      router.push(`/routes/review/${data.data.id}`)
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
    const onAddReview = () => {
      onAdd('/routes/review')
    }
    const onEditReview = () => {
      onEdit('/routes/review', selectedProduct)
    }
    const onDeleteReview = async () => {
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
      setDateFormat,
      onAddReview,
      onEditReview,
      onDeleteReview,
    }
  },
}
</script>

<style scoped lang="scss"></style>
