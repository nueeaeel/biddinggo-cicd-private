<script setup>
import { computed } from 'vue'

const props = defineProps({
  assets: {
    type: Object,
    required: true,
  },
  item: {
    type: Object,
    required: true,
  },
  rows: {
    type: Array,
    required: true,
  },
})

defineEmits(['close', 'open-bid'])

const thumbnailUrl = computed(() => {
  const imageUrls = Array.isArray(props.item?.images)
    ? props.item.images.map((image) => image?.url || image?.publicUrl || image?.imageUrl).filter(Boolean)
    : []

  return props.item?.image || imageUrls[0] || props.assets.listWatchImage
})
</script>

<template>
  <div class="detail-history-drawer-overlay" @click.self="$emit('close')">
    <aside class="detail-history-drawer">
      <div class="detail-history-drawer-top">
        <button type="button" class="detail-history-drawer-close" @click="$emit('close')">
          ×
        </button>
      </div>

      <div class="detail-history-drawer-summary">
        <img :src="thumbnailUrl" :alt="item.title" class="detail-inquiry-thumb" />
        <div class="detail-inquiry-summary-copy">
          <strong>{{ item.title }}</strong>
          <span>현재 입찰가</span>
          <em>{{ item.price }}원</em>
        </div>
      </div>

      <div class="detail-history-table">
        <div class="detail-history-table-head">
          <span>입찰자</span>
          <span>입찰액</span>
          <span>입찰 일시</span>
        </div>

        <div v-if="rows.length" class="detail-history-table-body">
          <div
            v-for="(row, index) in rows"
            :key="row.id ?? `${row.bidder}-${index}`"
            class="detail-history-table-row"
          >
            <span>{{ row.bidder }}</span>
            <strong>{{ row.amount }}</strong>
            <span>{{ row.date }}</span>
          </div>
        </div>
        <p v-else class="detail-history-table-empty">
          {{ item.bidHistoryRequiresLogin ? '입찰 기록은 로그인 후 확인할 수 있습니다.' : '아직 입찰 기록이 없습니다.' }}
        </p>
      </div>

      <button type="button" class="detail-history-action" @click="$emit('open-bid')">
        입찰하기
      </button>
    </aside>
  </div>
</template>
