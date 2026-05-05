<template>
  <section class="page-header-block">
    <h1>판매 내역</h1>
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

  <div class="winner-deal-list">
    <WinnerDealCard
      v-for="item in filteredItems"
      :key="item.id"
      :item="item"
      clickable
      @select="openDetailModal"
    />
  </div>

  <div ref="loadMoreTarget" class="load-more-sentinel">
    <span v-if="detailLoadingId">상세 내역을 불러오는 중입니다.</span>
    <span v-else-if="loading">판매 내역을 불러오는 중입니다.</span>
    <span v-else-if="!hasNext && items.length">마지막 판매 내역입니다.</span>
    <span v-else-if="!items.length">판매 내역이 없습니다.</span>
  </div>

  <WinnerDealDetailModal
    v-if="selectedItem"
    variant="sale"
    :item="selectedItem"
    :mode="modalMode"
    :form="shippingForm"
    :saving="savingShipping"
    @close="closeModal"
    @next="modalMode = $event"
    @update-form="updateForm"
    @save-shipping="saveShipping"
  />
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useToast } from '../../../composables/useToast'
import { useSalesModal } from '../../../composables/useSalesModal'
import WinnerDealCard from '../cards/WinnerDealCard.vue'
import WinnerDealDetailModal from '../winner-deals/WinnerDealDetailModal.vue'

const props = defineProps({
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
    default: () => [],
  },
  loadDetail: {
    type: Function,
    default: async (item) => item,
  },
  loading: {
    type: Boolean,
    default: false,
  },
  saveShipping: {
    type: Function,
    default: null,
  },
})

const emit = defineEmits(['load-more'])
const { showToast } = useToast()
const {
  selectedItem,
  modalMode,
  shippingForm,
  savingShipping,
  openModal,
  closeModal,
  updateForm,
  saveShipping,
} = useSalesModal({
  onSaveShipping: props.saveShipping,
})

const selectedTag = ref('전체')
const loadMoreTarget = ref(null)
const detailLoadingId = ref(null)
const detailErrorMessage = ref('')
let observer = null

const filterTags = ['전체', '배송 대기', '발송 완료', '거래 완료', '취소']

const filteredItems = computed(() => (
  props.items.filter((item) => selectedTag.value === '전체' || item.status === selectedTag.value)
))

async function openDetailModal(item) {
  if (detailLoadingId.value) {
    return
  }

  detailLoadingId.value = item.winnerDealId || item.id
  detailErrorMessage.value = ''

  try {
    const detailItem = await props.loadDetail(item)
    openModal(detailItem)
  } catch (error) {
    const message = error?.message || '상세 내역을 불러오지 못했습니다.'
    detailErrorMessage.value = message
    showToast(message, { color: 'error' })
  } finally {
    detailLoadingId.value = null
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
  () => [props.items.length, props.hasNext, props.loading],
  () => nextTick(handleScroll),
)

watch(
  () => props.errorMessage,
  (message) => {
    if (message) {
      showToast(message, { color: 'error' })
    }
  },
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
