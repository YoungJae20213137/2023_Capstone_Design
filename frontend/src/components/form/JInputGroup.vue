<template>
  <div class="JInputGroup">
    <div class="p-inputgroup" :style="{ width }">
      <InputText v-model="vModel" :placeholder="placeholder" />
      <Button :icon="icon" :severity="color" />
    </div>
  </div>
</template>

<script>
export default defineComponent({
  name: 'JInputGroup',
  props: {
    modelValue: {
      type: String,
      default: '',
    },
    placeholder: {
      type: String,
      default: 'input group',
    },
    icon: {
      type: String,
      default: 'fa fa-search',
    },
    color: {
      type: String,
      default: 'primary',
    },
    width: {
      type: String,
      default: '200px',
    },
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    /** @data component */
    const vModel = computed({
      get() {
        return props.modelValue
      },
      set(newValue) {
        emit('update:modelValue', newValue)
      },
    })

    return { vModel }
  },
})
</script>

<style scoped lang="scss"></style>
