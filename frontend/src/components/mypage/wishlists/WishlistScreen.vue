<template>
  <section class="page-header-block wishlist-page-header">
    <h1>관심 경매 내역</h1>
    <v-menu location="bottom end" offset="12">
      <template #activator="{ props: menuProps }">
        <button class="sort-menu__trigger wishlist-sort-trigger" type="button" v-bind="menuProps">
          <span>{{ currentSortLabel }}</span>
          <v-icon icon="mdi-chevron-down" />
        </button>
      </template>

      <div class="sort-menu__panel">
        <button
          v-for="option in sortOptions"
          :key="option.value"
          class="sort-menu__item"
          :class="{ 'sort-menu__item--active': selectedSort === option.value }"
          type="button"
          @click="$emit('sort-change', option.value)"
        >
          {{ option.label }}
        </button>
      </div>
    </v-menu>
  </section>

  <div class="list-grid mypage-auction-card-grid">
    <AuctionCard
      v-for="item in items"
      :key="item.id"
      class="wishlist-auction-card"
      :class="{ 'wishlist-auction-card--inspection': item.isInspected }"
      :image-src="noImage"
      :item="item"
      :wishlist-processing="wishlistProcessingIds.has(item.auctionId)"
      @click="openDetail(item)"
      @select="openDetail"
      @toggle-wishlist="toggleWishlist"
    />
  </div>

  <div ref="loadMoreTarget" class="load-more-sentinel">
    <span v-if="loading">관심 경매를 불러오는 중입니다.</span>
    <span v-else-if="!hasNext && items.length">마지막 관심 경매입니다.</span>
    <span v-else-if="!items.length">관심 경매가 없습니다.</span>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import AuctionCard from '../../AuctionCard.vue'
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
  selectedSort: {
    type: String,
    default: 'latest',
  },
  wishlistProcessingIds: {
    type: Object,
    default: () => new Set(),
  },
})

const emit = defineEmits(['load-more', 'open-detail', 'sort-change', 'toggle-wishlist'])
const loadMoreTarget = ref(null)
let observer = null

const sortOptions = [
  { value: 'latest', label: '최신순' },
  { value: 'deadline', label: '마감 임박순' },
  { value: 'interest', label: '관심순' },
  { value: 'popularity', label: '인기순' },
  { value: 'price-asc', label: '낮은 가격순' },
  { value: 'price-desc', label: '높은 가격순' },
]

const currentSortLabel = computed(() => {
  return sortOptions.find((option) => option.value === props.selectedSort)?.label ?? '최신순'
})

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
