<template>
  <div class="JInputNumber">
    <span :class="{ 'p-float-label': isFloatLabel }">
      <InputNumber
        id="input-number"
        v-model="vModel"
        :prefix="prefix"
        :suffix="suffix"
        :min="min"
        :max="max"
        :show-buttons="showButtons"
        :button-layout="buttonLayout"
        :step="step"
        :class="{ 'p-invalid': errorMessage }"
        :style="{ width }"
      />
      <label for="input-number">{{ label }}</label>
    </span>
    <small v-if="hasError" class="p-ml-2 block p-error">{{ errorMessage }}</small>
  </div>
</template>

<script>
import * as yup from 'yup'

export default defineComponent({
  name: 'JInputNumber',
  props: {
    modelValue: {
      type: Number,
      default: 0,
    },
    label: {
      type: String,
      default: '',
    },
    isFloatLabel: {
      type: Boolean,
      default: false,
    },
    prefix: {
      type: String,
      default: '',
    },
    suffix: {
      type: String,
      default: '',
    },
    min: {
      type: Number,
      default: 0,
    },
    max: {
      type: Number,
      default: 9999999,
    },
    showButtons: {
      type: Boolean,
      default: false,
    },
    buttonLayout: {
      type: String,
      default: 'stacked',
    },
    step: {
      type: Number,
      default: 100,
    },
    required: {
      type: Boolean,
      default: false,
    },
    width: {
      type: String,
      default: '200px',
    },
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    /** @validator */
    const schema = props.required
      ? yup.number().required(`${props.label} is required field.`).min(props.min).max(props.max)
      : yup.number().min(props.min).max(props.max)
    const { value, errorMessage } = useField('JInputNumber', schema)
    const hasError = computed(() => !!errorMessage.value)

    /** @data component */
    const vModel = computed({
      get() {
        return props.modelValue
      },
      set(newValue) {
        value.value = newValue
        emit('update:modelValue', newValue)
      },
    })

    return { vModel, hasError, errorMessage }
  },
})
</script>

<style scoped lang="scss">
.JInputNumber {
  .p-float-label .p-inputwrapper-focus ~ label,
  .p-float-label .p-inputwrapper-filled ~ label {
    top: 0;
    font-size: 0.75rem;
    background-color: var(--surface-a);
    padding: 0 4px;
  }
  .p-error {
    position: relative;
    top: 3px;
    left: 3px;
  }
}
</style>
