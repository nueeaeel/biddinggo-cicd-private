<template>
  <section class="page-header-block">
    <h1>구매 / 판매 문의 내역</h1>
  </section>

  <section class="filter-bar">
    <div class="filter-chips">
      <button
        v-for="tag in filterTags"
        :key="tag.type"
        class="chip"
        :class="{ active: selectedType === tag.type }"
        type="button"
        @click="$emit('filter-change', tag.type)"
      >
        {{ tag.label }}
      </button>
    </div>
  </section>

  <div class="stack-list">
    <InquiryCard
      v-for="inquiry in inquiries"
      :key="inquiry.id"
      :inquiry="inquiry"
      :allow-reply="Boolean(inquiry.pendingAction)"
      @open-auction="$emit('open-auction', $event)"
      @submit-reply="submitReply(inquiry, $event)"
    />
  </div>

  <div ref="loadMoreTarget" class="load-more-sentinel">
    <span v-if="loading">구매/판매 문의 내역을 불러오는 중입니다.</span>
    <span v-else-if="!hasNext && inquiries.length">마지막 문의 내역입니다.</span>
    <span v-else-if="!inquiries.length">구매/판매 문의 내역이 없습니다.</span>
  </div>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import InquiryCard from '../cards/InquiryCard.vue'

const props = defineProps({
  errorMessage: {
    type: String,
    default: '',
  },
  hasNext: {
    type: Boolean,
    default: false,
  },
  inquiries: {
    type: Array,
    default: () => [],
  },
  loading: {
    type: Boolean,
    default: false,
  },
  selectedType: {
    type: String,
    default: 'ALL',
  },
})

const emit = defineEmits(['filter-change', 'load-more', 'open-auction', 'submit-reply'])
const loadMoreTarget = ref(null)
let observer = null

const filterTags = [
  { label: '전체', type: 'ALL' },
  { label: '구매', type: 'PURCHASE' },
  { label: '판매', type: 'SALES' },
]

function submitReply(inquiry, content) {
  emit('submit-reply', inquiry, content)
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
  () => [props.inquiries.length, props.hasNext, props.loading],
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
