<script setup>
import { computed } from 'vue'
import BaseModal from '../shared/BaseModal.vue'

const props = defineProps({
  assets: {
    type: Object,
    required: true,
  },
  bidAmount: {
    type: String,
    required: true,
  },
  buyNowAmount: {
    type: Number,
    required: true,
  },
  formatAmount: {
    type: Function,
    required: true,
  },
  item: {
    type: Object,
    required: true,
  },
  minimumBidAmount: {
    type: Number,
    required: true,
  },
})

defineEmits(['buy-now', 'close', 'step-bid', 'submit-bid', 'update:bidAmount'])

const thumbnailUrl = computed(() => {
  const imageUrls = Array.isArray(props.item?.images)
    ? props.item.images.map((image) => image?.url || image?.publicUrl || image?.imageUrl).filter(Boolean)
    : []

  return props.item?.image || imageUrls[0] || props.assets.listWatchImage
})

const canBuyNow = computed(() => props.buyNowAmount > 0)
</script>

<template>
  <BaseModal panel-class="detail-bid-modal" title="입찰하기" @close="$emit('close')">
    <div class="detail-inquiry-summary">
      <img :src="thumbnailUrl" :alt="item.title" class="detail-inquiry-thumb" />
      <div class="detail-inquiry-summary-copy">
        <strong>{{ item.title }}</strong>
        <span>현재 입찰가</span>
        <em>{{ item.price }}</em>
      </div>
    </div>

    <div class="detail-bid-modal-body">
      <div class="detail-bid-meta-row">
        <span>입찰 단위</span>
        <strong>{{ item.bidUnit }}</strong>
      </div>

      <label class="detail-bid-modal-label">입찰 금액</label>

      <label class="detail-bid-input-row">
        <input
          :value="bidAmount"
          type="text"
          :placeholder="`최소 ${formatAmount(minimumBidAmount)}`"
          @input="$emit('update:bidAmount', $event.target.value)"
        />
        <span>원</span>
      </label>

      <div class="detail-bid-quick-actions">
        <button type="button" class="detail-bid-quick-chip" @click="$emit('step-bid', -1)">- {{ item.bidUnit }}</button>
        <button type="button" class="detail-bid-quick-chip" @click="$emit('step-bid', 1)">+ {{ item.bidUnit }}</button>
      </div>
    </div>

    <div class="detail-bid-modal-actions">
      <button
        type="button"
        class="detail-buy-now-button"
        :class="{ 'is-disabled': !canBuyNow }"
        :disabled="!canBuyNow"
        @click="canBuyNow && $emit('buy-now')"
      >
        <template v-if="canBuyNow">
          <span>{{ `${formatAmount(buyNowAmount)}원으로` }}</span>
          <strong>즉시 구매</strong>
        </template>
        <strong v-else>즉시 구매 불가</strong>
      </button>
      <button type="button" class="detail-bid-submit-button" @click="$emit('submit-bid')">
        입찰하기
      </button>
    </div>
  </BaseModal>
</template>
