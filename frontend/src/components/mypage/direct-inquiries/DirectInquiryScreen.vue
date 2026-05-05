<template>
  <section class="page-header-inline">
    <h1>1:1 문의 내역</h1>
    <button class="primary-button" type="button" @click="openModal">+ 문의하기</button>
  </section>

  <section class="filter-bar">
    <div class="filter-chips">
      <button
        v-for="tag in filterTags"
        :key="tag"
        class="chip"
        :class="{ active: selectedTag === tag }"
        type="button"
        @click="selectedTag = tag"
      >
        {{ tag }}
      </button>
    </div>
  </section>

  <div class="stack-list">
    <InquiryCard v-for="inquiry in filteredInquiries" :key="inquiry.id" :inquiry="inquiry" />
  </div>

  <div ref="loadMoreTarget" class="load-more-sentinel">
    <span v-if="loading">1:1 문의 내역을 불러오는 중입니다.</span>
    <span v-else-if="!hasNext && inquiries.length">마지막 문의 내역입니다.</span>
    <span v-else-if="!inquiries.length">1:1 문의 내역이 없습니다.</span>
  </div>

  <DirectInquiryModal
    v-model="draftInquiry"
    :open="isModalOpen"
    @close="closeModal"
    @submit="submitInquiry"
  />
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import InquiryCard from '../cards/InquiryCard.vue'
import DirectInquiryModal from './DirectInquiryModal.vue'
import { useToast } from '../../../composables/useToast'

const props = defineProps({
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
  submitInquiry: {
    type: Function,
    default: null,
  },
})

const emit = defineEmits(['load-more'])
const { showToast } = useToast()
const isModalOpen = ref(false)
const selectedTag = ref('전체')
const loadMoreTarget = ref(null)
const draftInquiry = ref({
  type: '기타',
  content: '',
})
let observer = null

const filterTags = ['전체', '답변 대기', '답변 완료']

const filteredInquiries = computed(() => {
  if (selectedTag.value === '전체') {
    return props.inquiries
  }

  return props.inquiries.filter((inquiry) => inquiry.status === selectedTag.value)
})

function openModal() {
  isModalOpen.value = true
}

function closeModal() {
  isModalOpen.value = false
}

function resetDraft() {
  draftInquiry.value = {
    type: '기타',
    content: '',
  }
}

async function submitInquiry() {
  if (!draftInquiry.value.content.trim()) {
    showToast('문의 내용을 입력해주세요.', { color: 'error' })
    return
  }

  try {
    await props.submitInquiry?.({
      type: draftInquiry.value.type,
      content: draftInquiry.value.content.trim(),
    })
    selectedTag.value = '전체'
    closeModal()
    resetDraft()
  } catch {
    // Parent handles toast; keep the modal open for correction.
  }
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
