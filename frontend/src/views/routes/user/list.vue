<template>
  <div class="card">
    <JDataTable
      v-model:modelValue="selectedProduct"
      :products="products"
      :columns="columns"
      @on-row-dbl-click="onRowDblClick"
    >
      <template #crud-button>
        <Button icon="pi pi-plus" class="p-mr-2" @click="onAddUser" />
        <Button icon="pi pi-pencil" class="p-mr-2" @click="onEditUser" />
        <Button icon="pi pi-times" @click="onDeleteUser" />
      </template>
      <template #favoriteShop="slotProps">
        {{ getFavorite(slotProps.data.favoriteShop) }}
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
import { useToast } from 'vue-toastification'
import useCrud from '@/composables/useCrud.js'

const API_URL = '/api/user'

export default {
  name: 'UserList',
  setup() {
    /** @data setting */
    const $axios = inject('$axios')
    const router = useRouter()
    const toast = useToast()

    /** @data component */
    const products = ref()
    const selectedProduct = ref()
    const columns = [
      { field: 'userId', header: '사용자명', sortable: true },
      { field: 'role', header: '권한', sortable: true },
      { field: 'favoriteShop', header: '찜 목록' },
      { field: 'createdAt', header: '생성일', sortable: true },
    ]

    const onRowDblClick = (data) => {
      router.push(`/routes/user/${data.data.id}`)
    }

    const setDateFormat = (data) => {
      return moment(data).format('YYYY-MM-DD hh:mm:ss')
    }

    $axios.get(`${API_URL}?size=100`).then((res) => {
      products.value = res.data.content
    })

    /** @composable CRUD */
    const { onAdd, onEdit, onDelete } = useCrud()
    const onAddUser = () => {
      onAdd('/routes/user')
    }
    const onEditUser = () => {
      onEdit('/routes/user', selectedProduct)
    }
    const onDeleteUser = async () => {
      if (selectedProduct.value.length > 0) {
        if (_.find(selectedProduct.value, ['userId', 'admin'])) {
          toast.error('관리자 아이디는 삭제할 수 없습니다.')
          return
        }
      }

      await onDelete(API_URL, selectedProduct)
      await $axios.get(`${API_URL}?size=100`).then((res) => {
        products.value = res.data.content
      })
    }

    return { products, selectedProduct, columns, onRowDblClick, setDateFormat, onAddUser, onEditUser, onDeleteUser }
  },
  methods: {
    getFavorite(data) {
      return _.values(data).join(',')
    },
  },
}
</script>

<style scoped lang="scss"></style>
