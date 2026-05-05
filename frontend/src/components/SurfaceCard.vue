<template>
  <component :is="as" class="surface-card" v-bind="$attrs">
    <slot />
  </component>
</template>

<script setup>
defineOptions({
  inheritAttrs: false,
})

defineProps({
  as: {
    type: String,
    default: 'div',
  },
})
</script>
