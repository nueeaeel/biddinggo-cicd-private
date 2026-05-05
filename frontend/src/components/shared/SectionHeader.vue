<script setup>
defineProps({
  actionClass: {
    type: String,
    default: '',
  },
  actionLabel: {
    type: String,
    default: '',
  },
  title: {
    type: String,
    required: true,
  },
  titleClass: {
    type: String,
    default: '',
  },
  wrapperClass: {
    type: String,
    required: true,
  },
})

defineEmits(['action'])
</script>

<template>
  <div :class="wrapperClass">
    <component :is="titleClass ? 'div' : 'h3'" :class="titleClass || undefined">
      {{ title }}
    </component>
    <button
      v-if="actionLabel"
      type="button"
      :class="actionClass || undefined"
      @click="$emit('action')"
    >
      {{ actionLabel }}
    </button>
  </div>
</template>
