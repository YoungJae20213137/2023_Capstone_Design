<template>
  <ul class="layout-menu">
    <template v-for="(item, i) in model" :key="item">
      <app-menu-item v-if="!item.separator" :item="item" :index="i"></app-menu-item>
      <li v-if="item.separator" class="menu-separator"></li>
    </template>
  </ul>
</template>

<script>
import { defineComponent, ref } from 'vue'
import AppMenuItem from '@/components/layout/AppMenuItem.vue'

export default defineComponent({
  name: 'LayoutMenu',
  components: { AppMenuItem },
  setup() {
    const model = ref([
      // {
      //   label: 'Home',
      //   items: [{ label: 'Dashboard', icon: 'pi pi-fw pi-home', to: '/' }],
      // },
      {
        label: '관리자 메뉴',
        items: [
          {
            label: '음식점 관리',
            icon: 'fas fa-store',
            to: '/routes/shop/list',
            role: ['ADMIN'],
          },
          {
            label: '메뉴 관리',
            icon: 'fas fa-utensils',
            to: '/routes/menu/list',
            role: ['ADMIN'],
          },
          {
            label: '사용자 관리',
            icon: 'fas fa-users',
            to: '/routes/user/list',
            role: ['ADMIN'],
          },
          {
            label: '리뷰 관리',
            icon: 'fas fa-comments',
            to: '/routes/review/list',
            role: ['ADMIN'],
          },
          {
            label: '예약 관리',
            icon: 'fas fa-calendar',
            to: '/routes/reserve/list',
            role: ['ADMIN'],
          },
        ],
        role: ['ADMIN'],
      },
    ])

    return { model }
  },
})
</script>

<style lang="scss" scoped></style>
