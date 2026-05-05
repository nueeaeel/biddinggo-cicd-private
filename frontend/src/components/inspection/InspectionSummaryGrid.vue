<script setup>
const summaryIcons = {
  total: 'mdi-clipboard-text-outline',
  review: 'mdi-timer-sand',
  approve: 'mdi-check-decagram-outline',
  auction: 'mdi-gavel',
}

defineProps({
  summary: {
    type: Array,
    required: true,
  },
})
</script>

<template>
  <div class="inspection-summary-grid">
    <article
      v-for="item in summary"
      :key="item.label"
      class="inspection-summary-card"
    >
      <div class="inspection-summary-icon" :class="`is-${item.tone}`">
        <v-icon :icon="summaryIcons[item.tone]" class="inspection-summary-icon-symbol" :aria-label="item.label" />
      </div>
      <div class="inspection-summary-copy">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
      </div>
    </article>
  </div>
</template>
