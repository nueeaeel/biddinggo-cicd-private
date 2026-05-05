<template>
  <section class="page-header-block">
    <h1>구매 내역</h1>
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
    <span v-else-if="loading">구매 내역을 불러오는 중입니다.</span>
    <span v-else-if="!hasNext && items.length">마지막 구매 내역입니다.</span>
    <span v-else-if="!items.length">구매 내역이 없습니다.</span>
  </div>

  <WinnerDealDetailModal
    v-if="selectedItem"
    variant="purchase"
    :addresses="addresses"
    :address-book-error-message="addressBookErrorMessage"
    :address-book-loading="addressBookLoading"
    :address-book-saving="addressBookSaving"
    :deleting-id="addressDeletingId"
    :item="selectedItem"
    :mode="modalMode"
    :form="modalMode === 'review-form' ? reviewForm : shippingForm"
    :saving="savingAddress || confirmingPurchase || submittingReview"
    :error-message="modalErrorMessage"
    :setting-default-id="addressSettingDefaultId"
    @close="closeModal"
    @next="handleNext"
    @back="modalMode = $event"
    @create-address="handleCreateAddress"
    @delete-address="handleDeleteAddress"
    @save-address="saveAddress"
    @select-address="selectAddress"
    @set-default-address="handleSetDefaultAddress"
    @submit-review="submitReview"
    @update-form="updateForm"
    @confirm-purchase="confirmPurchase"
  />
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useToast } from '../../../composables/useToast'
import { usePurchaseModal } from '../../../composables/usePurchaseModal'
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
  confirmPurchase: {
    type: Function,
    default: null,
  },
  createAddress: {
    type: Function,
    default: null,
  },
  deleteAddress: {
    type: Function,
    default: null,
  },
  loadAddressBook: {
    type: Function,
    default: async () => [],
  },
  items: {
    type: Array,
    default: () => [],
  },
  loadDetail: {
    type: Function,
    default: async (item) => item,
  },
  saveShippingAddress: {
    type: Function,
    default: null,
  },
  loading: {
    type: Boolean,
    default: false,
  },
  setDefaultAddress: {
    type: Function,
    default: null,
  },
  submitReview: {
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
  reviewForm,
  savingAddress,
  confirmingPurchase,
  submittingReview,
  modalErrorMessage,
  openModal,
  closeModal,
  selectAddress,
  updateForm,
  saveAddress,
  confirmPurchase,
  submitReview,
} = usePurchaseModal({
  onConfirmPurchase: props.confirmPurchase,
  onSaveAddress: props.saveShippingAddress,
  onSubmitReview: async (item, payload) => {
    if (!props.submitReview) {
      showToast('리뷰 작성 API는 아직 연결되지 않았습니다.', { color: 'error' })
      throw new Error('리뷰 작성 API는 아직 연결되지 않았습니다.')
    }

    await props.submitReview(item, payload)
  },
})

const selectedTag = ref('전체')
const loadMoreTarget = ref(null)
const detailLoadingId = ref(null)
const detailErrorMessage = ref('')
const addresses = ref([])
const addressBookLoading = ref(false)
const addressBookErrorMessage = ref('')
const addressBookSaving = ref(false)
const addressDeletingId = ref(null)
const addressSettingDefaultId = ref(null)
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

async function loadAddressBook() {
  if (addressBookLoading.value) {
    return
  }

  addressBookLoading.value = true
  addressBookErrorMessage.value = ''

  try {
    addresses.value = await props.loadAddressBook()
  } catch (error) {
    const message = error?.message || '배송지 목록을 불러오지 못했습니다.'
    addresses.value = []
    addressBookErrorMessage.value = message
    showToast(message, { color: 'error' })
  } finally {
    addressBookLoading.value = false
  }
}

async function handleNext(mode) {
  if (mode === 'address-book') {
    await loadAddressBook()
  }

  modalMode.value = mode
}

async function handleCreateAddress(payload) {
  if (!props.createAddress || addressBookSaving.value) {
    return
  }

  addressBookSaving.value = true
  addressBookErrorMessage.value = ''

  try {
    await props.createAddress(payload)
    await loadAddressBook()
    modalMode.value = 'address-book'
  } catch (error) {
    const message = error?.message || '배송지를 등록하지 못했습니다.'
    addressBookErrorMessage.value = message
    showToast(message, { color: 'error' })
  } finally {
    addressBookSaving.value = false
  }
}

async function handleSetDefaultAddress(addressId) {
  if (!props.setDefaultAddress) {
    return
  }

  addressSettingDefaultId.value = addressId
  addressBookErrorMessage.value = ''

  try {
    await props.setDefaultAddress(addressId)
    await loadAddressBook()
  } catch (error) {
    const message = error?.message || '기본 배송지를 설정하지 못했습니다.'
    addressBookErrorMessage.value = message
    showToast(message, { color: 'error' })
  } finally {
    addressSettingDefaultId.value = null
  }
}

async function handleDeleteAddress(addressId) {
  if (!props.deleteAddress) {
    return
  }

  addressDeletingId.value = addressId
  addressBookErrorMessage.value = ''

  try {
    await props.deleteAddress(addressId)
    await loadAddressBook()
  } catch (error) {
    const message = error?.message || '배송지를 삭제하지 못했습니다.'
    addressBookErrorMessage.value = message
    showToast(message, { color: 'error' })
  } finally {
    addressDeletingId.value = null
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
