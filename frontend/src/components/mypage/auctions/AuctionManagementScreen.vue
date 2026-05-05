<template>
  <section class="page-header-inline">
    <h1>경매 관리</h1>
    <button class="primary-button" type="button" @click="$emit('register')">+ 경매 등록하기</button>
  </section>

  <section class="stats-grid">
    <SurfaceCard as="article" v-for="card in summaryItems" :key="card.label" class="stat-card">
      <span class="stat-card__icon" :class="`stat-card__icon--${getSummaryIcon(card).tone}`">
        <v-icon :icon="getSummaryIcon(card).icon" size="22" />
      </span>
      <div>
        <p>{{ card.label }}</p>
        <strong>{{ card.value }}</strong>
      </div>
    </SurfaceCard>
  </section>

  <section class="filter-bar">
    <div class="filter-chips">
      <button
        v-for="tag in filterTags"
        :key="tag"
        class="chip"
        :class="{ active: selectedTag === tag }"
        type="button"
        @click="$emit('filter-change', tag)"
      >
        {{ tag }}
      </button>
    </div>
  </section>

  <div class="list-grid mypage-auction-card-grid">
    <AuctionCard
      v-for="item in items"
      :key="item.id"
      class="wishlist-auction-card"
      :image-src="noImage"
      :item="item"
      :show-live-tag="true"
      :wishlist-processing="wishlistProcessingIds.has(item.auctionId)"
      @select="openDetail"
      @toggle-wishlist="toggleWishlist"
    />
  </div>

  <div ref="loadMoreTarget" class="load-more-sentinel">
    <span v-if="loading">경매 관리 목록을 불러오는 중입니다.</span>
    <span v-else-if="!hasNext && items.length">마지막 경매입니다.</span>
    <span v-else-if="!items.length">등록한 경매가 없습니다.</span>
  </div>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import AuctionCard from '../../AuctionCard.vue'
import SurfaceCard from '../../SurfaceCard.vue'
import noImage from '../../../assets/no-image.svg'

const props = defineProps({
  hasNext: {
    type: Boolean,
    default: false,
  },
  items: {
    type: Array,
    default: () => [],
  },
  loading: {
    type: Boolean,
    default: false,
  },
  selectedTag: {
    type: String,
    default: '전체',
  },
  summaryItems: {
    type: Array,
    default: () => [],
  },
  wishlistProcessingIds: {
    type: Object,
    default: () => new Set(),
  },
})

const emit = defineEmits(['filter-change', 'load-more', 'open-detail', 'register', 'toggle-wishlist'])
const loadMoreTarget = ref(null)
const filterTags = ['전체', '예정', '진행 중', '낙찰', '유찰', '취소']
let observer = null

const summaryIconMap = {
  '전체 경매': { icon: 'mdi-view-grid-outline', tone: 'all' },
  '진행 중': { icon: 'mdi-timer-sand', tone: 'ongoing' },
  낙찰: { icon: 'mdi-gavel', tone: 'success' },
  유찰: { icon: 'mdi-alert-circle-outline', tone: 'failed' },
}

function getSummaryIcon(card) {
  return summaryIconMap[card.label] || summaryIconMap['전체 경매']
}

function openDetail(item) {
  emit('open-detail', item)
}

function toggleWishlist(item) {
  emit('toggle-wishlist', item)
}

function requestLoadMore() {
  if (props.hasNext && !props.loading) {
    emit('load-more')
  }
}

function handleScroll() {
  const scrollTop = window.scrollY || document.documentElement.scrollTop
  const viewportHeight = window.innerHeight
  const scrollHeight = document.documentElement.scrollHeight

  if (scrollTop + viewportHeight >= scrollHeight - 160) {
    requestLoadMore()
  }
}

watch(
  () => [props.items.length, props.hasNext, props.loading],
  () => nextTick(handleScroll),
)

onMounted(() => {
  observer = new IntersectionObserver(
    ([entry]) => {
      if (entry.isIntersecting) {
        requestLoadMore()
      }
    },
    { rootMargin: '120px' },
  )

  if (loadMoreTarget.value) {
    observer.observe(loadMoreTarget.value)
  }

  window.addEventListener('scroll', handleScroll, { passive: true })
  nextTick(handleScroll)
})

onBeforeUnmount(() => {
  observer?.disconnect()
  window.removeEventListener('scroll', handleScroll)
})
</script>
