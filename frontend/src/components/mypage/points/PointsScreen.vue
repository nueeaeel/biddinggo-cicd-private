<template>
  <section class="page-header-block">
      <h1>포인트 관리</h1>
  </section>

  <SurfaceCard as="section" class="hero-card hero-card--left">
      <p>보유 포인트</p>
      <strong>{{ formatAmount(currentPoints) }}</strong>
      <div class="button-row point-actions">
        <button class="primary-button" type="button" @click="openChargeModal">충전하기</button>
        <button class="secondary-button" type="button" @click="openWithdrawModal">인출하기</button>
      </div>
  </SurfaceCard>

  <section class="section-block">
      <div class="section-heading">
        <h2>포인트 내역</h2>
      </div>
      <div class="stack-list">
        <SurfaceCard as="article" v-for="entry in history" :key="entry.id ?? `${entry.title}-${entry.date}`" class="point-row">
          <div class="point-row__left">
            <span class="point-icon" :class="entry.tone" aria-hidden="true">
              <v-icon :icon="entry.tone === 'minus' ? 'mdi-arrow-left' : 'mdi-arrow-right'" />
            </span>
            <div>
              <h3>{{ entry.title }}</h3>
              <p>{{ entry.date }}</p>
            </div>
          </div>
          <strong :class="entry.tone">{{ entry.amount }}</strong>
        </SurfaceCard>
      </div>
      <div ref="loadMoreTarget" class="load-more-sentinel">
        <span v-if="loading">포인트 내역을 불러오는 중입니다.</span>
        <span v-else-if="!hasNext && history.length">마지막 포인트 내역입니다.</span>
        <span v-else-if="!history.length">포인트 내역이 없습니다.</span>
      </div>
  </section>

  <PointActionModal
      :open="Boolean(modalMode)"
      :mode="modalMode"
      :title="modalTitle"
      :current-points="currentPoints"
      :amount="amount"
      :presets="presets"
      :selected-preset="selectedPreset"
      :expected-points="expectedPoints"
      :expected-label="expectedLabel"
      :amount-label="amountLabel"
      :action-label="actionLabel"
      :virtual-account="virtualAccount"
      @close="handleModalClose"
      @preset="setPreset"
      @submit="submitModal"
      @submit-charge-details="submitChargeDetails"
      @confirm="handleVirtualAccountConfirm"
  />
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import SurfaceCard from '../../SurfaceCard.vue'
import PointActionModal from './PointActionModal.vue'
import { usePointModal } from '../../../composables/usePointModal'

const props = defineProps({
  currentPoints: {
    type: Number,
    default: 0,
  },
  history: {
    type: Array,
    default: () => [],
  },
  hasNext: {
    type: Boolean,
    default: false,
  },
  issuedVirtualAccount: {
    type: Object,
    default: null,
  },
  loading: {
    type: Boolean,
    default: false,
  },
  showIssuedVirtualAccount: {
    type: Boolean,
    default: false,
  },
  prepareChargePayment: {
    type: Function,
    default: null,
  },
  withdrawPoints: {
    type: Function,
    default: null,
  },
})

const emit = defineEmits(['close-issued-virtual-account', 'load-more'])
const modalCurrentPoints = ref(props.currentPoints)
const modalHistory = ref([])
const loadMoreTarget = ref(null)
let observer = null

const {
  actionLabel,
  amount,
  amountLabel,
  closeModal,
  confirmVirtualAccount,
  expectedLabel,
  expectedPoints,
  formatAmount,
  modalMode,
  modalTitle,
  openChargeModal,
  openVirtualAccountModal,
  openWithdrawModal,
  presets,
  selectedPreset,
  setPreset,
  submitChargeDetails,
  submitModal,
  virtualAccount,
} = usePointModal({
  currentPoints: modalCurrentPoints,
  history: modalHistory,
  onCharge: (amount) => props.prepareChargePayment?.(amount),
  onWithdraw: (amount) => props.withdrawPoints?.(amount),
})

function closeIssuedVirtualAccount() {
  emit('close-issued-virtual-account')
}

function handleModalClose() {
  if (modalMode.value === 'virtual-account' && props.showIssuedVirtualAccount) {
    closeIssuedVirtualAccount()
  }

  closeModal()
}

function handleVirtualAccountConfirm() {
  confirmVirtualAccount()

  if (props.showIssuedVirtualAccount) {
    closeIssuedVirtualAccount()
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
  () => props.currentPoints,
  (value) => {
    modalCurrentPoints.value = value
  },
  { immediate: true },
)

watch(
  () => [props.history.length, props.hasNext, props.loading],
  () => {
    modalHistory.value = [...props.history]
    nextTick(handleScroll)
  },
  { immediate: true },
)

watch(
  () => [props.showIssuedVirtualAccount, props.issuedVirtualAccount],
  ([show, account]) => {
    if (show && account) {
      openVirtualAccountModal(account)
    }
  },
  { immediate: true },
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
