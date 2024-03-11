<template>
  <div class="JPassword">
    <span :class="{ 'p-float-label': isFloatLabel }">
      <Password
        id="password"
        v-model="vModel"
        :placeholder="placeholder"
        :class="{ 'p-invalid': errorMessage }"
        :style="{ width }"
      />
      <label for="input-text">{{ label }}</label>
    </span>
    <small v-if="hasError" class="p-ml-2 block p-error">{{ errorMessage }}</small>
  </div>
</template>

<script>
import * as yup from 'yup'

export default defineComponent({
  name: 'JPassword',
  props: {
    modelValue: {
      type: String,
      default: '',
    },
    label: {
      type: String,
      default: '',
    },
    isFloatLabel: {
      type: Boolean,
      default: false,
    },
    placeholder: {
      type: String,
      default: '',
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
    const schema = props.required ? yup.string().required(`${props.label} is required field.`) : yup.string()
    const { value, errorMessage } = useField('JPassword', schema)
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
.JPassword {
  display: inline-flex;
  flex-direction: column;

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
