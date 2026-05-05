<script setup>
import SectionHeader from '../shared/SectionHeader.vue'

defineProps({
  item: {
    type: Object,
    required: true,
  },
  rows: {
    type: Array,
    default: () => [],
  },
})

defineEmits(['view-all'])
</script>

<template>
  <div class="history-panel">
    <SectionHeader
      action-class="history-view-all"
      action-label="View All"
      title="입찰 기록"
      title-class="history-heading"
      wrapper-class="history-header"
      @action="$emit('view-all')"
    />
    <div v-if="rows.length" class="history-list">
      <div v-for="(row, index) in rows" :key="row.id ?? `${row.bidder}-${index}`" class="history-row">
        <span>{{ row.bidder }}</span>
        <strong>{{ row.amount }}</strong>
        <span>{{ row.date }}</span>
      </div>
    </div>
    <p v-else class="history-empty">
      {{ item.bidHistoryRequiresLogin ? '입찰 기록은 로그인 후 확인할 수 있습니다.' : '아직 입찰 기록이 없습니다.' }}
    </p>
  </div>
</template>
