<template>
  <div class="layout-wrapper">
    <div class="layout-header"></div>
    <div class="layout-content">
      <router-view />
    </div>
    <div class="layout-footer"></div>
  </div>
</template>

<script>
export default defineComponent({
  name: 'AuthLayout',
})
</script>

<style scoped lang="scss">
.layout-wrapper {
  min-height: 100vh;
  min-width: 100vw;
  overflow-x: hidden;
  background-image: url('@/assets/images/background.jpg');
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
}
</style>
