<template>
  <section class="page-header-block">
    <h1>마이페이지</h1>
  </section>

  <SurfaceCard as="section" class="user-summary-card">
    <div class="user-summary-card__actions">
      <button
        class="user-summary-card__delete"
        type="button"
        :disabled="deleteAccountLoading"
        @click="handleDeleteAccount"
      >
        {{ deleteAccountLoading ? '탈퇴 처리 중...' : '회원 탈퇴' }}
      </button>
    </div>
    <div class="user-summary__identity">
      <img
        :src="overviewUser.avatar"
        :alt="overviewUser.name"
        class="avatar avatar--small"
        @error="setDefaultAvatar"
      />
      <strong>{{ overviewUser.name }}</strong>
    </div>
    <div class="user-summary__points">
      <p>보유 포인트</p>
      <strong>{{ overviewUser.points }}</strong>
    </div>
  </SurfaceCard>

  <section class="section-block">
    <div class="section-heading">
      <h2>입찰 내역</h2>
      <RouterLink to="/mypage/bids">View All</RouterLink>
    </div>
    <div class="bid-history-list">
      <ProductBidCard
        v-for="item in bidItems.slice(0, 2)"
        :key="item.date + item.status"
        :item="item"
        @open-detail="$emit('open-detail', item)"
      />
    </div>
  </section>

  <section class="section-block">
    <div class="section-heading">
      <h2>진행 중 구매 현황</h2>
      <RouterLink to="/mypage/purchases">View All</RouterLink>
    </div>
    <div class="winner-deal-list">
      <WinnerDealCard
        v-for="item in purchaseStatusItems.slice(0, 3)"
        :key="item.id"
        :item="item"
        clickable
        @select="openPurchaseDetailModal"
      />
    </div>
  </section>

  <section class="section-block">
    <div class="section-heading">
      <h2>진행 중 판매 현황</h2>
      <RouterLink to="/mypage/sales">View All</RouterLink>
    </div>
    <div class="winner-deal-list">
      <WinnerDealCard
        v-for="item in salesHistoryItems.slice(0, 3)"
        :key="item.id"
        :item="item"
        clickable
        @select="openSalesDetailModal"
      />
    </div>
  </section>

  <WinnerDealDetailModal
    v-if="selectedPurchaseItem"
    variant="purchase"
    :addresses="addresses"
    :address-book-error-message="addressBookErrorMessage"
    :address-book-loading="addressBookLoading"
    :address-book-saving="addressBookSaving"
    :deleting-id="addressDeletingId"
    :item="selectedPurchaseItem"
    :mode="purchaseModalMode"
    :form="purchaseModalMode === 'review-form' ? reviewForm : purchaseShippingForm"
    :saving="savingAddress || confirmingPurchase || submittingReview"
    :error-message="purchaseDetailErrorMessage"
    :setting-default-id="addressSettingDefaultId"
    @close="closePurchaseModal"
    @next="handlePurchaseNext"
    @back="purchaseModalMode = $event"
    @create-address="handleCreateAddress"
    @delete-address="handleDeleteAddress"
    @save-address="savePurchaseAddress"
    @select-address="selectPurchaseAddress"
    @set-default-address="handleSetDefaultAddress"
    @submit-review="submitReview"
    @update-form="updatePurchaseForm"
    @confirm-purchase="confirmPurchase"
  />

  <WinnerDealDetailModal
    v-if="selectedSalesItem"
    variant="sale"
    :item="selectedSalesItem"
    :mode="salesModalMode"
    :form="salesShippingForm"
    :saving="savingShipping"
    :error-message="salesDetailErrorMessage"
    @close="closeSalesModal"
    @next="salesModalMode = $event"
    @update-form="updateSalesForm"
    @save-shipping="saveSalesShipping"
  />
</template>

<script setup>
import { ref } from 'vue'
import { RouterLink } from 'vue-router'
import defaultAvatar from '../../../assets/default-avatar.svg'
import SurfaceCard from '../../SurfaceCard.vue'
import ProductBidCard from '../cards/ProductBidCard.vue'
import WinnerDealCard from '../cards/WinnerDealCard.vue'
import WinnerDealDetailModal from '../winner-deals/WinnerDealDetailModal.vue'
import { usePurchaseModal } from '../../../composables/usePurchaseModal'
import { useSalesModal } from '../../../composables/useSalesModal'
import { useToast } from '../../../composables/useToast'
import {
  bidItems as fallbackBidItems,
  overviewUser as fallbackOverviewUser,
  purchaseStatusItems as fallbackPurchaseStatusItems,
} from '../../../data/mypage'
import { salesHistoryItems as fallbackSalesHistoryItems } from '../../../data/salesHistory'

const props = defineProps({
  bidItems: {
    type: Array,
    default: () => fallbackBidItems,
  },
  createAddress: {
    type: Function,
    default: null,
  },
  deleteAccountAction: {
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
  loadPurchaseDetail: {
    type: Function,
    default: async (item) => item,
  },
  loadSalesDetail: {
    type: Function,
    default: async (item) => item,
  },
  overviewUser: {
    type: Object,
    default: () => fallbackOverviewUser,
  },
  purchaseStatusItems: {
    type: Array,
    default: () => fallbackPurchaseStatusItems,
  },
  salesHistoryItems: {
    type: Array,
    default: () => fallbackSalesHistoryItems,
  },
  savePurchaseShippingAddress: {
    type: Function,
    default: null,
  },
  saveSalesShipping: {
    type: Function,
    default: null,
  },
  setDefaultAddress: {
    type: Function,
    default: null,
  },
  confirmPurchaseAction: {
    type: Function,
    default: null,
  },
  submitReviewAction: {
    type: Function,
    default: null,
  },
})

defineEmits(['open-detail'])

const { showToast } = useToast()
const purchaseDetailErrorMessage = ref('')
const salesDetailErrorMessage = ref('')
const addressBookLoading = ref(false)
const addressBookSaving = ref(false)
const addressBookErrorMessage = ref('')
const addressDeletingId = ref(null)
const addressSettingDefaultId = ref(null)
const addresses = ref([])
const deleteAccountLoading = ref(false)
const purchaseDetailLoadingId = ref(null)
const salesDetailLoadingId = ref(null)

const {
  selectedItem: selectedPurchaseItem,
  modalMode: purchaseModalMode,
  shippingForm: purchaseShippingForm,
  reviewForm,
  savingAddress,
  confirmingPurchase,
  submittingReview,
  openModal: openPurchaseModal,
  closeModal: closePurchaseModal,
  selectAddress: selectPurchaseAddress,
  updateForm: updatePurchaseForm,
  saveAddress: savePurchaseAddress,
  confirmPurchase,
  submitReview,
} = usePurchaseModal({
  onConfirmPurchase: props.confirmPurchaseAction,
  onSaveAddress: props.savePurchaseShippingAddress,
  onSubmitReview: async (item, payload) => {
    if (!props.submitReviewAction) {
      throw new Error('리뷰 작성 기능을 사용할 수 없습니다.')
    }

    await props.submitReviewAction(item, payload)
  },
})

const {
  selectedItem: selectedSalesItem,
  modalMode: salesModalMode,
  shippingForm: salesShippingForm,
  savingShipping,
  openModal: openSalesModal,
  closeModal: closeSalesModal,
  updateForm: updateSalesForm,
  saveShipping: saveSalesShipping,
} = useSalesModal({
  onSaveShipping: props.saveSalesShipping,
})

async function handleDeleteAccount() {
  if (!props.deleteAccountAction || deleteAccountLoading.value) {
    return
  }

  const confirmed = window.confirm('정말 회원 탈퇴하시겠습니까?\n탈퇴 후 계정 정보를 복구할 수 없습니다.')
  if (!confirmed) {
    return
  }

  deleteAccountLoading.value = true

  try {
    await props.deleteAccountAction()
  } catch (error) {
    showToast(error?.message || '회원 탈퇴에 실패했습니다.', { color: 'error' })
  } finally {
    deleteAccountLoading.value = false
  }
}

async function openPurchaseDetailModal(item) {
  if (purchaseDetailLoadingId.value) {
    return
  }

  purchaseDetailLoadingId.value = item.winnerDealId || item.id
  purchaseDetailErrorMessage.value = ''

  try {
    const detailItem = await props.loadPurchaseDetail(item)
    openPurchaseModal(detailItem)
  } catch (error) {
    const message = error?.message || '구매 상세 정보를 불러오지 못했습니다.'
    purchaseDetailErrorMessage.value = message
    showToast(message, { color: 'error' })
  } finally {
    purchaseDetailLoadingId.value = null
  }
}

async function openSalesDetailModal(item) {
  if (salesDetailLoadingId.value) {
    return
  }

  salesDetailLoadingId.value = item.winnerDealId || item.id
  salesDetailErrorMessage.value = ''

  try {
    const detailItem = await props.loadSalesDetail(item)
    openSalesModal(detailItem)
  } catch (error) {
    const message = error?.message || '판매 상세 정보를 불러오지 못했습니다.'
    salesDetailErrorMessage.value = message
    showToast(message, { color: 'error' })
  } finally {
    salesDetailLoadingId.value = null
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

async function handlePurchaseNext(mode) {
  if (mode === 'address-book') {
    await loadAddressBook()
  }

  purchaseModalMode.value = mode
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
    purchaseModalMode.value = 'address-book'
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

function setDefaultAvatar(event) {
  event.target.src = defaultAvatar
}
</script>
