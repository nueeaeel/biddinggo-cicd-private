<script setup>
defineProps({
  overlayClass: {
    type: String,
    default: 'detail-inquiry-overlay',
  },
  panelClass: {
    type: String,
    required: true,
  },
  title: {
    type: String,
    default: '',
  },
  closeClass: {
    type: String,
    default: 'detail-inquiry-close',
  },
})

defineEmits(['close'])
</script>

<template>
  <div :class="overlayClass" @click.self="$emit('close')">
    <section :class="panelClass">
      <slot name="header">
        <div v-if="title" class="detail-inquiry-header">
          <h3>{{ title }}</h3>
          <button type="button" :class="closeClass" @click="$emit('close')">×</button>
        </div>
      </slot>

      <slot />
    </section>
  </div>
</template>
