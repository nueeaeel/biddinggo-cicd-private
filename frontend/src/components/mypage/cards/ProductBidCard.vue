<template>
  <SurfaceCard
    as="article"
    class="history-card history-card--interactive"
    role="button"
    tabindex="0"
    @click="$emit('open-detail')"
    @keydown.enter="$emit('open-detail')"
    @keydown.space.prevent="$emit('open-detail')"
  >
    <img :src="item.image" :alt="item.name" class="history-card__thumb" @error="setNoImage" />
    <div class="history-card__body">
      <div class="history-card__content">
        <StatusBadge :status="item.status" />
        <h3>{{ truncateTitle(item.name) }}</h3>
        <div class="history-card__time">
          <v-icon icon="mdi-alarm" />
          <p class="history-card__meta">{{ item.time }}</p>
        </div>
      </div>
      <div class="history-card__pricebox">
        <p>현재가</p>
        <strong>{{ item.currentPrice }}</strong>
        <p>내 입찰가</p>
        <strong>{{ item.myBid }}</strong>
        <span>{{ item.date }}</span>
      </div>
    </div>
  </SurfaceCard>
</template>

<script setup>
import SurfaceCard from '../../SurfaceCard.vue'
import StatusBadge from './StatusBadge.vue'
import noImage from '../../../assets/no-image.svg'

defineProps({
  item: {
    type: Object,
    required: true,
  },
})

defineEmits(['open-detail'])

function truncateTitle(value) {
  const title = String(value || '')
  return title.length > 12 ? `${title.slice(0, 12)}...` : title
}

function setNoImage(event) {
  event.target.src = noImage
}
</script>
