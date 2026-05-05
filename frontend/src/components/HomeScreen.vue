<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import AuctionCard from './AuctionCard.vue'

const props = defineProps({
  assets: {
    type: Object,
    required: true,
  },
  heroSlides: {
    type: Array,
    default: () => [],
  },
  errorMessage: {
    type: String,
    default: '',
  },
  hasNext: {
    type: Boolean,
    default: false,
  },
  items: {
    type: Array,
    required: true,
  },
  loading: {
    type: Boolean,
    default: false,
  },
  wishlistProcessingIds: {
    type: Object,
    default: () => new Set(),
  },
})

const emit = defineEmits(['loadMore', 'openDetail', 'openList', 'toggleWishlist'])
const loadMoreTriggerRef = ref(null)
let observer = null

const currentHeroIndex = ref(0)
const currentHero = computed(() => props.heroSlides[currentHeroIndex.value] || null)
const isFirstHero = computed(() => currentHeroIndex.value === 0)
const isLastHero = computed(() => currentHeroIndex.value === props.heroSlides.length - 1)

function showPreviousHero() {
  currentHeroIndex.value = currentHeroIndex.value > 0
    ? currentHeroIndex.value - 1
    : props.heroSlides.length - 1
}

function showNextHero() {
  currentHeroIndex.value = currentHeroIndex.value < props.heroSlides.length - 1
    ? currentHeroIndex.value + 1
    : 0
}

function openCurrentHero() {
  emit('openList')
}

function requestLoadMore() {
  if (props.loading || !props.hasNext) {
    return
  }

  emit('loadMore')
}

function bindObserver() {
  if (typeof window === 'undefined' || !loadMoreTriggerRef.value) {
    return
  }

  observer = new IntersectionObserver(
    (entries) => {
      const [entry] = entries

      if (entry?.isIntersecting) {
        requestLoadMore()
      }
    },
    {
      root: null,
      rootMargin: '0px 0px 240px 0px',
      threshold: 0,
    },
  )

  observer.observe(loadMoreTriggerRef.value)
}

function unbindObserver() {
  if (observer) {
    observer.disconnect()
    observer = null
  }
}

onMounted(bindObserver)
onBeforeUnmount(unbindObserver)

watch(loadMoreTriggerRef, () => {
  unbindObserver()
  bindObserver()
})

watch(
  () => [props.hasNext, props.loading, props.items.length],
  () => {
    requestLoadMore()
  },
)
</script>

<template>
  <div>
    <section class="hero-section">
      <img :src="currentHero?.backgroundImage || assets.heroBackground" alt="" class="hero-background" />
      <div class="hero-overlay"></div>

      <button
        type="button"
        class="hero-nav-button hero-nav-button--prev"
        :aria-label="isFirstHero ? '마지막 슬라이드 보기' : '이전 슬라이드 보기'"
        @click="showPreviousHero"
      >
        ‹
      </button>

      <button
        type="button"
        class="hero-nav-button hero-nav-button--next"
        :aria-label="isLastHero ? '첫 슬라이드 보기' : '다음 슬라이드 보기'"
        @click="showNextHero"
      >
        ›
      </button>

      <transition name="hero-fade" mode="out-in">
        <div :key="currentHero.key" class="hero-copy">
          <h2>
            {{ currentHero.title[0] }}<br />
            {{ currentHero.title[1] }}
          </h2>
          <p>
            {{ currentHero.description[0] }}<br />
            {{ currentHero.description[1] }}
          </p>
          <button type="button" class="hero-button" @click="openCurrentHero">{{ currentHero.buttonLabel }}</button>
        </div>
      </transition>

      <div class="hero-indicators" aria-label="메인 배너 슬라이드">
        <button
          v-for="(slide, index) in props.heroSlides"
          :key="slide.key"
          type="button"
          class="hero-indicator"
          :class="{ 'is-active': currentHeroIndex === index }"
          :aria-label="`${index + 1}번 슬라이드 보기`"
          @click="currentHeroIndex = index"
        ></button>
      </div>
    </section>

    <section class="best-section">
      <div class="section-heading">
        <h3>Best Items</h3>
        <p>현재 가장 인기 있는 경매</p>
      </div>

      <p v-if="errorMessage" class="feedback-strip is-error">{{ errorMessage }}</p>
      <p v-else-if="loading" class="feedback-strip">메인 경매를 불러오는 중입니다.</p>
      <div v-else-if="items.length" class="card-grid">
        <AuctionCard
          v-for="(item, index) in items"
          :key="`${item.title}-${index}`"
          :image-src="assets.watchImage"
          :item="item"
          :wishlist-processing="wishlistProcessingIds.has(item.auctionId)"
          @select="$emit('openDetail', $event)"
          @toggle-wishlist="$emit('toggleWishlist', $event)"
        />
      </div>
      <p v-else class="feedback-strip">현재 노출할 경매가 없습니다.</p>

      <div ref="loadMoreTriggerRef" class="best-load-more-trigger" aria-hidden="true"></div>
      <p v-if="loading && items.length" class="feedback-strip">경매를 더 불러오는 중입니다.</p>
    </section>
  </div>
</template>

<style scoped>
.hero-fade-enter-active,
.hero-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.hero-fade-enter-from,
.hero-fade-leave-to {
  opacity: 0;
  transform: translateY(8px);
}

.feedback-strip {
  margin: 0;
  border-radius: 14px;
  background: #f8fafc;
  padding: 14px 15px;
  color: #64748b;
  font-size: 11px;
  text-align: center;
}

.feedback-strip.is-error {
  background: #fef2f2;
  color: #b91c1c;
}

.best-load-more-trigger {
  width: 100%;
  height: 1px;
  margin-top: 24px;
}
</style>
