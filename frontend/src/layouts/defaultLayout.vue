<template>
  <div :class="containerClass">
    <LayoutHeader @menu-toggle="onMenuToggle" />
    <div v-if="userStore.role === 'ADMIN'" class="layout-sidebar">
      <AppMenu />
    </div>
    <div class="layout-main-container">
      <div class="layout-main">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script>
import { useUserStore } from '@/store/user.js'

export default defineComponent({
  name: 'DefaultLayout',
  setup() {
    const layoutMode = ref('static')
    let menuClick = ref(false)

    const containerClass = computed(() => {
      return [
        'layout-wrapper',
        {
          'layout-overlay': layoutMode.value === 'overlay',
          'layout-static': layoutMode.value === 'static',
          'layout-static-inactive': menuClick.value,
        },
      ]
    })

    const onMenuToggle = () => {
      menuClick.value = !menuClick.value
    }

    return { layoutMode, containerClass, onMenuToggle, userStore: useUserStore() }
  },
  watch: {
    'userStore.role': {
      handler(value) {
        if (value === 'USER') {
          this.layoutMode = 'overlay'
        }
      },
      immediate: true,
    },
  },
})
</script>

<style scoped lang="scss">
.layout-wrapper {
  min-height: 100vh;
  min-width: 100vw;
  overflow-x: hidden;
}

.layout-main-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  justify-content: space-between;
  padding: 7rem 2rem 2rem 4rem;
  transition: margin-left 0.2s;
  overflow-x: hidden;
}

.layout-main {
  flex: 1 1 auto;
}

.layout-content {
  width: 100%;
  min-width: 800px;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow-y: hidden;
}

@media (min-width: 992px) {
  .layout-wrapper.layout-static .layout-main-container {
    margin-left: 300px;
  }
}

@media (min-width: 992px) {
  .layout-wrapper.layout-static.layout-static-inactive .layout-sidebar {
    transform: translateX(-100%);
    left: 0;
  }
}

@media (min-width: 992px) {
  .layout-wrapper.layout-static.layout-static-inactive .layout-main-container {
    margin-left: 0;
    padding-left: 2rem;
    width: 100%;
  }
}
</style>
