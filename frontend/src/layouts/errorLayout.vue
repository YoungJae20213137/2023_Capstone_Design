<template>
  <div class="layout-wrapper">
    <router-view />
  </div>
</template>

<script>
export default defineComponent({
  name: 'ErrorLayout',
})
</script>

<style scoped lang="scss">
.layout-wrapper {
  min-height: 100vh;
  min-width: 100vw;
  overflow-x: hidden;
}
</style>
