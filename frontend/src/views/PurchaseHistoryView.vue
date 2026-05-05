<template>
  <PurchaseHistoryScreen
    :create-address="createAddress"
    :delete-address="deleteAddress"
    :error-message="errorMessage"
    :has-next="hasNext"
    :items="purchaseItems"
    :loading="loading"
    :confirm-purchase="confirmPurchase"
    :load-address-book="loadAddressBook"
    :load-detail="loadPurchaseDetail"
    :save-shipping-address="savePurchaseShippingAddress"
    :set-default-address="setDefaultAddress"
    :submit-review="submitReview"
    @load-more="loadMorePurchases"
  />
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { createAuctionReview } from '../api/reviews'
import {
  createUserAddress,
  deleteUserAddress,
  confirmWinnerDeal,
  getUserAddresses,
  getUserPurchases,
  getWinnerDealDetail,
  setDefaultUserAddress,
  updateWinnerDealShippingAddress,
} from '../api/users'
import noImage from '../assets/no-image.svg'
import PurchaseHistoryScreen from '../components/mypage/purchases/PurchaseHistoryScreen.vue'
import { useToast } from '../composables/useToast'

const { showToast } = useToast()

const purchaseItems = ref([])
const hasNext = ref(true)
const loading = ref(false)
const errorMessage = ref('')
const page = ref(1)
const pageSize = 10
const defaultOffset = 1073741824

const enumStatusLabels = {
  PAID: '배송 대기',
  SHIPPED: '발송 완료',
  CONFIRMED: '거래 완료',
  CANCELLED: '취소',
}

function normalizeDealStatus(status) {
  const rawStatus = String(status || '').trim().toUpperCase()

  return enumStatusLabels[rawStatus] || String(status || '-')
}

function isPaidStatus(item = {}) {
  return String(item.status || '').trim().toUpperCase() === 'PAID'
}

function isShippedStatus(item = {}) {
  return String(item.status || '').trim().toUpperCase() === 'SHIPPED'
}

function formatAmount(value) {
  return `${Number(value || 0).toLocaleString('ko-KR')}원`
}

function formatDate(value) {
  if (!value) {
    return '-'
  }

  const date = new Date(value)

  if (Number.isNaN(date.getTime())) {
    return '-'
  }

  return new Intl.DateTimeFormat('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }).format(date)
}

function hasShippingAddress(item = {}) {
  return Boolean(item.recipient || item.tel || item.zipcode || item.address || item.detailAddress)
}

function hasShippingInfo(item = {}) {
  return Boolean(item.carrier || item.trackingNumber)
}

function getAddressRows(response) {
  if (Array.isArray(response)) {
    return response
  }

  if (Array.isArray(response?.content)) {
    return response.content
  }

  if (Array.isArray(response?.addresses)) {
    return response.addresses
  }

  if (Array.isArray(response?.addressList)) {
    return response.addressList
  }

  return []
}

function normalizeBoolean(value) {
  if (typeof value === 'boolean') {
    return value
  }

  if (typeof value === 'string') {
    return ['true', 'y', 'yes', '1'].includes(value.trim().toLowerCase())
  }

  return Number(value) === 1
}

function normalizeAddressOption(address = {}, index = 0) {
  return {
    id: address.id ?? address.addressId ?? address.addressNo ?? `address-${index}`,
    zip: String(
      address.zip
        ?? address.zipcode
        ?? address.zipCode
        ?? address.zonecode
        ?? address.postalCode
        ?? address.postCode
        ?? '',
    ),
    address1: address.address
      ?? address.address1
      ?? address.roadAddress
      ?? address.jibunAddress
      ?? address.basicAddress
      ?? '',
    address2: address.address2
      ?? address.detailAddress
      ?? address.detail
      ?? '',
    primary: normalizeBoolean(
      address.primary
        ?? address.default_yn
        ?? address.defaultYn
        ?? address.defaultAddress
        ?? address.isDefault,
    ),
  }
}

function buildShippingAddress(item = {}) {
  if (!hasShippingAddress(item)) {
    return null
  }

  return {
    name: item.recipient || '',
    phone: item.tel || '',
    zip: item.zipcode || '',
    address1: item.address || '',
    address2: item.detailAddress || '',
  }
}

function buildShippingInfo(item = {}) {
  if (!hasShippingInfo(item)) {
    return null
  }

  return {
    courier: item.carrier || '',
    trackingNumber: item.trackingNumber || '',
  }
}

function getPurchaseModalType(item = {}) {
  if (item.canRegisterShippingAddress || (isPaidStatus(item) && !hasShippingAddress(item))) {
    return 'pending-no-address'
  }

  if (item.canConfirmPurchase || isShippedStatus(item)) {
    return 'delivery-complete-awaiting-confirm'
  }

  return 'readonly'
}

function normalizeWinnerDeal(item = {}) {
  return {
    id: item.winnerDealId,
    winnerDealId: item.winnerDealId,
    dealNumber: item.dealNumber || '',
    auctionId: item.auctionId,
    itemId: item.itemId,
    viewerRole: item.viewerRole,
    name: item.itemName || `상품 #${item.itemId}`,
    image: item.itemImageUrl || item.imageUrl || noImage,
    status: normalizeDealStatus(item.status),
    rawStatus: item.status,
    price: formatAmount(item.winnerPrice),
    winnerPrice: item.winnerPrice,
    sellerName: item.sellerName || '',
    winnerName: item.winnerName || '',
    confirmedAt: item.confirmedAt,
    createdAt: item.createdAt,
    date: formatDate(item.createdAt),
    modalType: getPurchaseModalType(item),
    shippingAddress: buildShippingAddress(item),
    shippingInfo: buildShippingInfo(item),
    canRegisterShippingAddress: Boolean(item.canRegisterShippingAddress),
    canRegisterTrackingNumber: Boolean(item.canRegisterTrackingNumber),
    canConfirmPurchase: Boolean(item.canConfirmPurchase),
    canWriteReview: Boolean(item.canWriteReview),
  }
}

function updatePurchaseItem(winnerDealId, updater) {
  purchaseItems.value = purchaseItems.value.map((item) => (
    item.winnerDealId === winnerDealId ? updater(item) : item
  ))
}

async function loadPurchaseDetail(item) {
  if (!item?.winnerDealId) {
    return item
  }

  const detail = await getWinnerDealDetail(item.winnerDealId)

  return normalizeWinnerDeal({ ...item, ...detail })
}

async function loadAddressBook() {
  const response = await getUserAddresses()

  return getAddressRows(response).map(normalizeAddressOption)
}

async function createAddress(payload) {
  const createdAddress = await createUserAddress({
    zipcode: payload.zipcode,
    address: payload.address,
    detailAddress: payload.detailAddress,
  })

  if (payload.primary) {
    const createdAddressId = createdAddress?.id ?? createdAddress?.addressId ?? createdAddress?.addressNo

    if (createdAddressId) {
      await setDefaultUserAddress(createdAddressId)
    }
  }
}

async function setDefaultAddress(addressId) {
  await setDefaultUserAddress(addressId)
}

async function deleteAddress(addressId) {
  await deleteUserAddress(addressId)
}

async function savePurchaseShippingAddress(item, address) {
  await updateWinnerDealShippingAddress(item.winnerDealId, {
    zipcode: address.zip,
    address: address.address1,
    detailAddress: address.address2,
    recipient: address.name,
    tel: address.phone,
  })

  updatePurchaseItem(item.winnerDealId, (purchaseItem) => ({
    ...purchaseItem,
    shippingAddress: {
      name: address.name,
      phone: address.phone,
      zip: address.zip,
      address1: address.address1,
      address2: address.address2,
    },
    modalType: isShippedStatus(purchaseItem) ? 'delivery-complete-awaiting-confirm' : 'readonly',
  }))
}

async function confirmPurchase(item) {
  await confirmWinnerDeal(item.winnerDealId)

  updatePurchaseItem(item.winnerDealId, (purchaseItem) => ({
    ...purchaseItem,
    status: '거래 완료',
    rawStatus: 'CONFIRMED',
    modalType: 'readonly',
    canConfirmPurchase: false,
  }))
}

async function submitReview(item, payload) {
  if (!item?.auctionId) {
    throw new Error('리뷰를 등록할 경매 ID가 없습니다.')
  }

  const normalizedRating = Math.min(5, Math.max(1, Math.round(Number(payload.rating || 0))))

  await createAuctionReview(item.auctionId, {
    rating: normalizedRating,
    content: String(payload.content || '').trim(),
  })

  updatePurchaseItem(item.winnerDealId, (purchaseItem) => ({
    ...purchaseItem,
    canWriteReview: false,
  }))

  showToast('리뷰가 등록되었습니다.')
}

async function loadMorePurchases() {
  if (loading.value || !hasNext.value) {
    return
  }

  const requestedPage = page.value
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getUserPurchases({
      page: requestedPage,
      size: pageSize,
      order: 'ASC',
      offset: defaultOffset,
    })
    const content = response?.content || []
    const existingIds = new Set(purchaseItems.value.map((item) => item.id))
    const nextItems = content.map(normalizeWinnerDeal).filter((item) => !existingIds.has(item.id))

    purchaseItems.value = [...purchaseItems.value, ...nextItems]
    page.value += 1
    hasNext.value = typeof response?.hasNext === 'boolean'
      ? response.hasNext
      : requestedPage < Number(response?.totalPages ?? requestedPage)
  } catch (error) {
    errorMessage.value = error?.message || '구매 내역을 불러오지 못했습니다.'
    hasNext.value = false
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadMorePurchases()
})
</script>
