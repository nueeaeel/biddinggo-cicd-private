<script setup>
import { computed, ref, watch } from 'vue'
import { getSellerReviews } from '../../api/reviews'
import defaultAvatar from '../../assets/default-avatar.svg'
import { useToast } from '../../composables/useToast'
import BaseModal from '../shared/BaseModal.vue'

const props = defineProps({
  assets: {
    type: Object,
    required: true,
  },
  item: {
    type: Object,
    required: true,
  },
  sellerId: {
    type: [Number, String],
    default: null,
  },
  sellerProfile: {
    type: Object,
    required: true,
  },
})

defineEmits(['close'])

const { showToast } = useToast()

const reviewItems = ref([])
const reviewPage = ref(1)
const reviewLoading = ref(false)
const reviewHasNext = ref(false)

const sellerAvatar = computed(() => props.sellerProfile?.avatar || defaultAvatar)

const summaryStats = computed(() => [
  { label: '총 판매 건수', value: props.sellerProfile?.totalSalesCount ?? '-' },
  { label: '판매 취소', value: props.sellerProfile?.cancelCount ?? '-' },
  { label: '응답률', value: props.sellerProfile?.responseRate ?? '-' },
])

function formatReviewDate(date) {
  if (!date) {
    return '-'
  }

  const parsedDate = new Date(date)

  if (Number.isNaN(parsedDate.getTime())) {
    return String(date)
  }

  return parsedDate.toLocaleDateString('ko-KR')
}

function normalizeReview(review = {}, index = 0) {
  return {
    id: `${review.buyerNickname || 'review'}-${review.reviewDate || index}`,
    author: review.buyerNickname || '구매자',
    date: formatReviewDate(review.reviewDate),
    rating: Number(review.rating || 0),
    content: review.reviewText || '등록된 리뷰 내용이 없습니다.',
  }
}

async function loadReviews({ reset = false } = {}) {
  const sellerId = props.sellerId

  if (!sellerId || reviewLoading.value) {
    return
  }

  const requestedPage = reset ? 1 : reviewPage.value
  reviewLoading.value = true

  try {
    const response = await getSellerReviews(sellerId, {
      page: requestedPage,
      size: 10,
      order: 'ASC',
      offset: 1073741824,
    })
    const content = Array.isArray(response?.content) ? response.content : []
    const nextItems = content.map(normalizeReview)

    reviewItems.value = reset ? nextItems : [...reviewItems.value, ...nextItems]
    reviewPage.value = requestedPage + 1
    reviewHasNext.value = Boolean(response?.hasNext)
  } catch (error) {
    showToast(error?.message || '판매자 리뷰를 불러오지 못했습니다.', { color: 'error' })
  } finally {
    reviewLoading.value = false
  }
}

watch(
  () => props.sellerId,
  (sellerId) => {
    if (!sellerId) {
      reviewItems.value = Array.isArray(props.sellerProfile?.reviews)
        ? props.sellerProfile.reviews.map(normalizeReview)
        : []
      reviewPage.value = 1
      reviewHasNext.value = false
      return
    }

    reviewItems.value = []
    reviewPage.value = 1
    reviewHasNext.value = false
    loadReviews({ reset: true })
  },
  { immediate: true },
)
</script>

<template>
  <BaseModal panel-class="detail-seller-modal" @close="$emit('close')">
    <template #header>
      <div class="detail-seller-modal-header">
        <button type="button" class="detail-inquiry-close" @click="$emit('close')">×</button>
      </div>
    </template>

    <div class="detail-seller-profile">
      <img :src="sellerAvatar" :alt="item.seller" class="detail-seller-avatar" @error="$event.target.src = defaultAvatar" />
      <div class="detail-seller-copy">
        <div class="detail-seller-name-row">
          <img
            v-if="sellerProfile.badge"
            :src="sellerProfile.badge"
            :alt="`${item.sellerGrade} 등급`"
            class="detail-seller-badge"
          />
          <strong>{{ item.seller }}</strong>
        </div>

        <div class="detail-seller-meta-row">
          <div class="detail-seller-rating">
            <span
              v-for="starIndex in 5"
              :key="`seller-star-${starIndex}`"
              class="detail-seller-star"
            >
              {{ starIndex <= Math.round(Number(sellerProfile.rating)) ? '★' : '☆' }}
            </span>
            <span class="detail-seller-rating-value">{{ sellerProfile.rating }}</span>
            <span class="detail-seller-review-count">(리뷰 {{ sellerProfile.reviewCount }})</span>
          </div>

          <div class="detail-seller-joined">가입일: {{ sellerProfile.joinedAt }}</div>
        </div>
      </div>
    </div>

    <div class="detail-seller-stats">
      <article
        v-for="stat in summaryStats"
        :key="stat.label"
        class="detail-seller-stat-card"
      >
        <span>{{ stat.label }}</span>
        <strong>{{ stat.value }}</strong>
      </article>
    </div>

    <div class="detail-seller-reviews">
      <h3>구매자 리뷰 ({{ sellerProfile.reviewCount }})</h3>

      <p v-if="!reviewItems.length && !reviewLoading" class="detail-seller-empty-review">
        아직 등록된 구매자 리뷰가 없습니다.
      </p>

      <article
        v-for="review in reviewItems"
        :key="review.id"
        class="detail-seller-review"
      >
        <div class="detail-seller-review-top">
          <div>
            <strong>{{ review.author }}</strong>
            <span>{{ review.date }}</span>
          </div>
          <div class="detail-seller-review-stars">
            <span
              v-for="starIndex in 5"
              :key="`${review.id}-star-${starIndex}`"
              class="detail-seller-star"
            >
              {{ starIndex <= review.rating ? '★' : '☆' }}
            </span>
          </div>
        </div>

        <p>{{ review.content }}</p>
      </article>

      <div v-if="reviewLoading || reviewHasNext" class="detail-seller-review-actions">
        <button
          v-if="reviewHasNext"
          type="button"
          class="ghost-button"
          :disabled="reviewLoading"
          @click="loadReviews()"
        >
          {{ reviewLoading ? '불러오는 중...' : '리뷰 더보기' }}
        </button>
        <span v-else-if="reviewLoading">불러오는 중...</span>
      </div>
    </div>
  </BaseModal>
</template>
