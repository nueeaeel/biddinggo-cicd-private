<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({
  assets: {
    type: Object,
    required: true,
  },
  item: {
    type: Object,
    required: true,
  },
})

const currentImageIndex = ref(0)

const imageUrls = computed(() => {
  const urls = Array.isArray(props.item.images)
    ? props.item.images.map((image) => image?.url || image?.publicUrl || image?.imageUrl).filter(Boolean)
    : []

  if (props.item.image) {
    return [props.item.image, ...urls.filter((url) => url !== props.item.image)]
  }

  return urls.length ? urls : [props.assets.listWatchImage]
})

const currentImageUrl = computed(() => imageUrls.value[currentImageIndex.value] || props.assets.listWatchImage)
const hasMultipleImages = computed(() => imageUrls.value.length > 1)

function moveImage(direction) {
  if (!hasMultipleImages.value) {
    return
  }

  currentImageIndex.value = (currentImageIndex.value + direction + imageUrls.value.length) % imageUrls.value.length
}

watch(
  () => props.item.auctionId,
  () => {
    currentImageIndex.value = 0
  },
)
</script>

<template>
  <div class="detail-left">
    <div class="detail-image-card">
      <img :src="currentImageUrl" :alt="item.title" class="detail-image" />
      <button
        v-if="hasMultipleImages"
        type="button"
        class="gallery-arrow gallery-arrow-left"
        aria-label="이전 사진 보기"
        @click="moveImage(-1)"
      >
        ‹
      </button>
      <button
        v-if="hasMultipleImages"
        type="button"
        class="gallery-arrow gallery-arrow-right"
        aria-label="다음 사진 보기"
        @click="moveImage(1)"
      >
        ›
      </button>
      <div v-if="hasMultipleImages" class="gallery-counter">
        {{ currentImageIndex + 1 }} / {{ imageUrls.length }}
      </div>
    </div>

    <div v-if="item.isInspected" class="inspection-banner">
      <div class="inspection-icon">✓</div>
      <div>
        <strong>{{ item.inspectionLabel }}</strong>
        <p>{{ item.inspectionDescription }}</p>
      </div>
    </div>
  </div>
</template>
