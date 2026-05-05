<script setup>
import { computed } from 'vue'

const props = defineProps({
  assets: {
    type: Object,
    required: true,
  },
  bidButtonDisabled: {
    type: Boolean,
    default: false,
  },
  bidButtonLabel: {
    type: String,
    default: '지금 입찰하기',
  },
  item: {
    type: Object,
    required: true,
  },
  isOwnAuction: {
    type: Boolean,
    default: false,
  },
  wishlistProcessing: {
    type: Boolean,
    default: false,
  },
})

const showBuyNowPrice = computed(() => {
  const buyNowAmount = Number(String(props.item?.buyNowPrice || '0').replace(/[^\d]/g, '')) || 0
  return buyNowAmount > 0
})

const showBrand = computed(() => {
  const brand = String(props.item?.brand || '').trim()

  return Boolean(brand) && brand !== '브랜드 미정'
})

const detailTimeLabel = computed(() => {
  const time = String(props.item?.time || '').trim()

  if (!time || time === '-' || time === '마감' || time.endsWith('남음')) {
    return time
  }

  return `${time} 남음`
})

const qualityLabel = computed(() => {
  const quality = String(props.item?.inspectionGrade || props.item?.quality || '').trim()

  return quality && quality !== '-' ? quality : ''
})

defineEmits(['open-bid', 'toggle-wishlist'])
</script>

<template>
  <div class="price-panel">
    <div class="price-top-line">
      <div v-if="item.isTimeDeal || item.isExtendedAuction" class="price-tags">
        <span v-if="item.isTimeDeal" class="price-tag is-danger">TIME DEAL</span>
        <span v-if="item.isExtendedAuction" class="price-tag is-extend">연장 경매</span>
      </div>
      <div v-else class="price-tags" aria-hidden="true"></div>
      <div class="detail-top-actions">
        <span v-if="qualityLabel" class="detail-quality-badge">{{ qualityLabel }}</span>
        <button
          type="button"
          class="detail-heart-button"
          :class="{ 'is-wished': item.isWished }"
          :disabled="wishlistProcessing"
          :aria-pressed="item.isWished"
          :aria-label="item.isWished ? '찜 취소' : '찜하기'"
          @click="$emit('toggle-wishlist')"
        >
          <v-icon :icon="item.isWished ? 'mdi-heart' : 'mdi-heart-outline'" size="22" aria-hidden="true" />
        </button>
      </div>
    </div>

    <h4 v-if="showBrand" class="detail-product-brand">{{ item.brand }}</h4>
    <h2 class="detail-product-title">{{ item.title }}</h2>

    <div class="detail-price-block">
      <span>현재 입찰가</span>
      <strong>{{ item.price }}</strong>
    </div>

    <p class="detail-price-meta">{{ item.bids }} | 시작가 {{ item.startPrice || item.price }}</p>
    <p v-if="item.pricePredictionLabel" class="detail-price-prediction">{{ item.pricePredictionLabel }}</p>
    <p class="detail-time-left">{{ detailTimeLabel }}</p>

    <div class="detail-bid-box">
      <label class="detail-bid-field">
        <span>입찰 금액</span>
        <input type="text" :value="item.price" />
      </label>
      <button
        type="button"
        class="detail-bid-button"
        :disabled="bidButtonDisabled"
        @click="$emit('open-bid')"
      >
        {{ bidButtonLabel }}
      </button>
    </div>

    <div class="detail-stats">
      <div><span>입찰 단위</span><strong>{{ item.bidUnit }}</strong></div>
      <div v-if="showBuyNowPrice"><span>즉시 구매가</span><strong>{{ item.buyNowPrice }}</strong></div>
      <div><span>시작일</span><strong>{{ item.startDate }}</strong></div>
      <div><span>종료일</span><strong>{{ item.endDate }}</strong></div>
    </div>
  </div>
</template>
