<template>
  <div class="JDataTable">
    <DataTable
      v-model:selection="selectedProduct"
      v-model:filters="filters"
      :value="products"
      data-key="id"
      table-style="min-width: 50rem"
      paginator
      :rows="5"
      :rows-per-page-options="[5, 10, 20, 50]"
      @row-dblclick="onRowDblClick"
      @update:selection="onUpdateSelection"
    >
      <template #header>
        <div class="p-d-flex p-justify-between">
          <span class="p-input-icon-left">
            <i class="pi pi-search" />
            <InputText v-model="filters['global'].value" placeholder="Search" />
          </span>
          <div>
            <slot name="crud-button"></slot>
          </div>
        </div>
      </template>
      <template #empty> 검색 데이터가 없습니다. </template>
      <Column selection-mode="multiple" header-style="width: 3rem"></Column>
      <Column
        v-for="col of columns"
        :key="col.field"
        :field="col.field"
        :header="col.header"
        :filter="true"
        :sortable="col.sortable"
        :style="{ width: col.width }"
      >
        <template #body="{ data, field }">
          <slot :name="field" :data="data">
            {{ data[field] }}
          </slot>
        </template>
      </Column>
    </DataTable>
  </div>
</template>

<script>
import { ref, defineComponent } from 'vue'
import { FilterMatchMode, FilterOperator } from 'primevue/api'

export default defineComponent({
  name: 'JDataTable',
  props: {
    columns: {
      type: Array,
      default: () => [],
    },
    products: {
      type: Array,
      default: () => [],
    },
  },
  emits: ['update:modelValue', 'onRowDblClick'],
  setup(props, { emit }) {
    const selectedProduct = ref()

    /** @filter 검색 */
    const filters = ref()
    filters.value = {
      global: { value: null, matchMode: FilterMatchMode.CONTAINS },
      userId: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
      role: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
    }

    /** @emits */
    const onUpdateSelection = (data) => {
      emit('update:modelValue', data)
    }
    const onRowDblClick = (data) => {
      emit('onRowDblClick', data)
    }

    return { selectedProduct, filters, onUpdateSelection, onRowDblClick }
  },
})
</script>

<style scoped lang="scss"></style>
