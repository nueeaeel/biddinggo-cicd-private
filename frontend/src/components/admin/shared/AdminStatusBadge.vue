<template>
  <span class="admin-status-badge" :class="badgeClass">{{ status }}</span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  status: {
    type: String,
    required: true,
  },
})

const toneMap = {
  '거래 완료': 'complete',
  '발송 대기': 'pending',
  '배송 중': 'shipping',
  '배송 완료': 'delivered',
  '답변 완료': 'answered',
  '답변 대기': 'waiting',
  활성: 'active',
  정지: 'blocked',
  '검수 대기': 'pending',
  승인: 'approved',
  반려: 'rejected',
  게시중: 'active',
  비공개: 'waiting',
  '정산 대기': 'pending',
  '정산 완료': 'complete',
}

const badgeClass = computed(() => `admin-status-badge--${toneMap[props.status] ?? 'default'}`)
</script>
