<template>
  <div class="JDropdown">
    <span :class="{ 'p-float-label': isFloatLabel }">
      <Dropdown
        id="drop-down"
        v-model="vModel"
        :options="options"
        :option-label="optionLabel"
        :data-key="dataKey"
        :placeholder="placeholder"
        :show-clear="showClear"
        :style="{ width }"
      />
      <label for="drop-down">{{ label }}</label>
    </span>
    <small v-if="hasError" class="p-ml-2 block p-error">{{ errorMessage }}</small>
  </div>
</template>

<script>
import * as yup from 'yup'

export default defineComponent({
  name: 'JDropdown',
  props: {
    modelValue: {
      type: [Object, String, Number],
      default: null,
    },
    options: {
      type: Array,
      required: true,
      default: () => [],
    },
    optionLabel: {
      type: String,
      default: 'name',
    },
    dataKey: {
      type: String,
      default: 'id',
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
    showClear: {
      type: Boolean,
      default: false,
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
    const schema = props.required ? yup.object().required(`${props.label} is required field.`) : yup.object()
    const { value, errorMessage } = useField('JDropdown', schema)
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
.JDropdown {
  .p-float-label .p-inputwrapper-focus ~ label,
  .p-float-label .p-inputwrapper-filled ~ label {
    top: 0;
    font-size: 0.75rem;
    background-color: var(--surface-a);
    padding: 0 4px;
  }
}
</style>
