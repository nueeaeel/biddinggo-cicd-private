<template>
  <SurfaceCard
    as="article"
    class="winner-deal-card"
    :class="{ 'winner-deal-card--interactive': clickable }"
    @click="handleClick"
  >
    <div class="winner-deal-card__main">
      <StatusBadge :status="item.status" />
      <h3>{{ truncateTitle(item.name) }}</h3>
    </div>

    <div class="winner-deal-card__side">
      <strong>{{ item.price }}</strong>
      <span>{{ item.date }}</span>
    </div>
  </SurfaceCard>
</template>

<script setup>
import SurfaceCard from '../../SurfaceCard.vue'
import StatusBadge from './StatusBadge.vue'

const props = defineProps({
  item: {
    type: Object,
    required: true,
  },
  clickable: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['select'])

function truncateTitle(value) {
  const title = String(value || '')
  return title.length > 12 ? `${title.slice(0, 12)}...` : title
}

function handleClick() {
  if (props.clickable) {
    emit('select', props.item)
  }
}
</script>
