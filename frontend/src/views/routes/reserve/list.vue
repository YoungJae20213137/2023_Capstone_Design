<template>
  <div class="card">
    <JDataTable v-model:modelValue="selectedProduct" :products="products" :columns="columns">
      <template #crud-button>
        <Button icon="pi pi-times" @click="onDeleteReserve" />
      </template>
      <template #shop="slotProps">
        {{ slotProps.data.shop.name }}
      </template>
      <template #user="slotProps">
        {{ slotProps.data.user.userId }}
      </template>
      <template #person="slotProps"> {{ slotProps.data.person }}명 </template>
      <template #times="slotProps">
        {{ slotProps.data.startDate }}
        {{ slotProps.data.startTime }}
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
import useCrud from '@/composables/useCrud.js'

const API_URL = '/api/reservation'
const SERVER_URL = import.meta.env.VITE_BASE_URL

export default {
  name: 'ReserveList',
  setup() {
    /** @data setting */
    const $axios = inject('$axios')
    const serverUrl = SERVER_URL

    /** @data component */
    const products = ref()
    const selectedProduct = ref()
    const columns = [
      { field: 'shop', header: '매장명', width: '250px' },
      { field: 'user', header: '예약자', sortable: true, width: '200px' },
      { field: 'person', header: '예약 인원', sortable: true, width: '200px' },
      { field: 'times', header: '예약 날짜', width: '200px' },
      { field: 'option', header: '요청 사항', sortable: true, width: '200px' },
      { field: 'createdAt', header: '생성일', sortable: true, width: '200px' },
    ]

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
    const { onDelete } = useCrud()
    const onDeleteReserve = async () => {
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
      setDateFormat,
      onDeleteReserve,
    }
  },
}
</script>

<style scoped lang="scss"></style>
