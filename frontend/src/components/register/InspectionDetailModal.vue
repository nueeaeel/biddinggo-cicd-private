<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({
  inspectionProductImage: {
    type: String,
    required: true,
  },
  item: {
    type: Object,
    required: true,
  },
})

defineEmits(['close', 'start-auction'])

const currentImageIndex = ref(0)

const imageUrls = computed(() => {
  const images = Array.isArray(props.item?.images) ? props.item.images : []
  const urls = images.map((image) => image?.url).filter(Boolean)

  if (urls.length) {
    return urls
  }

  return [props.item?.image || props.inspectionProductImage].filter(Boolean)
})

const currentImage = computed(() => imageUrls.value[currentImageIndex.value] || props.inspectionProductImage)
const canSlideImages = computed(() => imageUrls.value.length > 1)

function showPreviousImage() {
  if (!canSlideImages.value) {
    return
  }

  currentImageIndex.value = (currentImageIndex.value - 1 + imageUrls.value.length) % imageUrls.value.length
}

function showNextImage() {
  if (!canSlideImages.value) {
    return
  }

  currentImageIndex.value = (currentImageIndex.value + 1) % imageUrls.value.length
}

watch(
  () => props.item?.inspectionId,
  () => {
    currentImageIndex.value = 0
  },
  { immediate: true },
)
</script>

<template>
  <div class="inspection-detail-overlay" @click.self="$emit('close')">
    <section class="inspection-detail-modal">
      <button type="button" class="inspection-detail-close" @click="$emit('close')">
        ×
      </button>

      <div class="inspection-detail-grid">
        <div class="inspection-detail-image-card">
          <img :src="currentImage" :alt="item.title" class="inspection-detail-image" />
          <button
            v-if="canSlideImages"
            type="button"
            class="gallery-arrow gallery-arrow-left"
            aria-label="이전 사진"
            @click="showPreviousImage"
          >
            ‹
          </button>
          <button
            v-if="canSlideImages"
            type="button"
            class="gallery-arrow gallery-arrow-right"
            aria-label="다음 사진"
            @click="showNextImage"
          >
            ›
          </button>
        </div>

        <div class="inspection-detail-summary">
          <p class="inspection-detail-kicker">사전 검수 상품</p>
          <div class="inspection-detail-heading">
            <span class="inspection-detail-status">검수 통과</span>
            <p class="inspection-detail-category">{{ item.categoryLabel || '카테고리 정보 없음' }}</p>
          </div>
          <h3>{{ item.title }}</h3>

          <div class="inspection-detail-meta">
            <article class="inspection-detail-meta-card">
              <span>브랜드</span>
              <strong>{{ item.brand }}</strong>
            </article>
            <article class="inspection-detail-meta-card">
              <span>검수일</span>
              <strong>{{ item.inspectionDate }}</strong>
            </article>
            <article class="inspection-detail-meta-card">
              <span>검수 등급</span>
              <strong class="is-grade">{{ item.inspectionGrade }}</strong>
            </article>
          </div>
        </div>
      </div>

      <div class="inspection-detail-info-grid">
        <article class="inspection-detail-section inspection-detail-section--copy">
          <h4>상품 설명</h4>
          <p>{{ item.description }}</p>
        </article>

        <article class="inspection-detail-section inspection-detail-section--shipping">
          <h4>배송 정보</h4>
          <div class="inspection-detail-shipping-list">
            <div>
              <span>택배사</span>
              <strong>{{ item.carrier || '배송 정보 미등록' }}</strong>
            </div>
            <div>
              <span>송장 번호</span>
              <strong>{{ item.trackingNumber || '-' }}</strong>
            </div>
          </div>
        </article>
      </div>

      <div class="inspection-detail-actions">
        <button type="button" class="register-secondary-button" @click="$emit('close')">
          반환 신청
        </button>
        <button type="button" class="register-primary-button" @click="$emit('start-auction')">
          경매 등록
        </button>
      </div>
    </section>
  </div>
</template>
