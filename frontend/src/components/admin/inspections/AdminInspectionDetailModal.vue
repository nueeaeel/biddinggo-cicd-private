<script setup>
import { computed, ref, watch } from 'vue'
import BaseModal from '../../shared/BaseModal.vue'

const props = defineProps({
  assets: { type: Object, required: true },
  badgeClass: { type: Function, required: true },
  item: { type: Object, required: true },
  canAdminProcess: { type: Boolean, default: true },
})

defineEmits(['close', 'approve', 'reject'])

const currentImageIndex = ref(0)

const imageUrls = computed(() => {
  const images = Array.isArray(props.item?.images) ? props.item.images : []
  const urls = images.map((image) => image?.url).filter(Boolean)
  if (urls.length) return urls
  return [props.item?.image || props.assets.listWatchImage].filter(Boolean)
})

const currentImage = computed(() => imageUrls.value[currentImageIndex.value] || props.assets.listWatchImage)
const canSlideImages = computed(() => imageUrls.value.length > 1)

function showPreviousImage() {
  if (!canSlideImages.value) return
  currentImageIndex.value = (currentImageIndex.value - 1 + imageUrls.value.length) % imageUrls.value.length
}

function showNextImage() {
  if (!canSlideImages.value) return
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
  <BaseModal overlay-class="inspection-status-overlay" panel-class="inspection-status-modal" @close="$emit('close')">
    <template #header>
      <button type="button" class="inspection-status-close" @click="$emit('close')">×</button>
    </template>

    <div class="inspection-status-grid">
<div class="inspection-status-image-card">
  <img :src="currentImage" :alt="item.title" class="inspection-status-image" />
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

  <div class="inspection-status-summary">
    <p class="inspection-status-category">{{ item.categoryLabel || '카테고리 정보 없음' }}</p>
    <span class="inspection-status-badge" :class="badgeClass(item.status)">
      {{ item.statusLabel || item.status }}
    </span>
    <h3>{{ item.title }}</h3>

    <div class="inspection-status-meta">
      <div>
        <span>브랜드</span>
        <strong>{{ item.brand }}</strong>
      </div>
      <div>
        <span>검수일</span>
        <strong>{{ item.inspectionDate }}</strong>
      </div>
      <div>
        <span>검수 등급</span>
        <strong class="is-grade">{{ item.inspectionGrade }}</strong>
      </div>
    </div>
  </div>

  <div class="inspection-status-section">
    <h4>배송 정보</h4>

    <div
      v-if="(item.status === 'PENDING' || item.status === '검수 대기') && !item.carrier && !item.trackingNumber"
      class="inspection-status-alert"
    >
      <span class="inspection-status-alert-icon">!</span>
      <p>배송 정보를 등록해주세요</p>
    </div>

    <template v-else>
      <p>{{ item.carrier || '등록된 택배사 없음' }}</p>
      <p>{{ item.trackingNumber || '등록된 송장 번호 없음' }}</p>
    </template>
  </div>
</div>

<div class="inspection-status-actions">
  <button
    type="button"
    class="register-secondary-button"
    :disabled="!canAdminProcess"
    @click="$emit('reject')"
  >
    반려
  </button>
  <button
    type="button"
    class="register-primary-button"
    :disabled="!canAdminProcess"
    @click="$emit('approve')"
  >
    승인
  </button>
</div>

  </BaseModal>
</template>
