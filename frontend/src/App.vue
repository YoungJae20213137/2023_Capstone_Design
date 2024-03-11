<template>
  <div id="App" :key="$route.fullPath">
    <component :is="currentLayout" />
  </div>
</template>

<script>
import AuthLayout from '@/layouts/authLayout.vue'
import DefaultLayout from '@/layouts/defaultLayout.vue'
import ErrorLayout from '@/layouts/errorLayout.vue'

export default {
  setup() {
    const layouts = new Map([
      ['authLayout', AuthLayout],
      ['defaultLayout', DefaultLayout],
      ['errorLayout', ErrorLayout],
    ])

    const route = useRoute()
    let currentLayout = computed(() => {
      const { meta } = route
      return layouts.get(`${meta.layout}Layout`)
    })

    return { layouts, route, currentLayout }
  },
}
</script>

<style scoped lang="scss">
#App {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  overflow-x: hidden;
}
</style>
