<template>
  <SalesHistoryScreen
    :error-message="errorMessage"
    :has-next="hasNext"
    :items="salesItems"
    :loading="loading"
    :load-detail="loadSalesDetail"
    :save-shipping="saveSalesShipping"
    @load-more="loadMoreSales"
  />
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getUserSales, getWinnerDealDetail, updateWinnerDealTrackingNumber } from '../api/users'
import noImage from '../assets/no-image.svg'
import SalesHistoryScreen from '../components/mypage/sales/SalesHistoryScreen.vue'

const salesItems = ref([])
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

function getSaleModalType(item = {}) {
  if (item.canRegisterTrackingNumber || (isPaidStatus(item) && hasShippingAddress(item) && !hasShippingInfo(item))) {
    return 'seller-needs-shipping-info'
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
    modalType: getSaleModalType(item),
    shippingAddress: buildShippingAddress(item),
    shippingInfo: buildShippingInfo(item),
    canRegisterShippingAddress: Boolean(item.canRegisterShippingAddress),
    canRegisterTrackingNumber: Boolean(item.canRegisterTrackingNumber),
    canConfirmPurchase: Boolean(item.canConfirmPurchase),
    canWriteReview: Boolean(item.canWriteReview),
  }
}

function updateSalesItem(winnerDealId, updater) {
  salesItems.value = salesItems.value.map((item) => (
    item.winnerDealId === winnerDealId ? updater(item) : item
  ))
}

async function loadSalesDetail(item) {
  if (!item?.winnerDealId) {
    return item
  }

  const detail = await getWinnerDealDetail(item.winnerDealId)

  return normalizeWinnerDeal({ ...item, ...detail })
}

async function saveSalesShipping(item, shippingInfo) {
  await updateWinnerDealTrackingNumber(item.winnerDealId, {
    carrier: shippingInfo.courier,
    trackingNumber: shippingInfo.trackingNumber,
  })

  updateSalesItem(item.winnerDealId, (salesItem) => ({
    ...salesItem,
    shippingInfo: {
      courier: shippingInfo.courier,
      trackingNumber: shippingInfo.trackingNumber,
    },
    status: '발송 완료',
    rawStatus: 'SHIPPED',
    modalType: 'readonly',
    canRegisterTrackingNumber: false,
  }))
}

async function loadMoreSales() {
  if (loading.value || !hasNext.value) {
    return
  }

  const requestedPage = page.value
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getUserSales({
      page: requestedPage,
      size: pageSize,
      order: 'ASC',
      offset: defaultOffset,
    })
    const content = response?.content || []
    const existingIds = new Set(salesItems.value.map((item) => item.id))
    const nextItems = content.map(normalizeWinnerDeal).filter((item) => !existingIds.has(item.id))

    salesItems.value = [...salesItems.value, ...nextItems]
    page.value += 1
    hasNext.value = typeof response?.hasNext === 'boolean'
      ? response.hasNext
      : requestedPage < Number(response?.totalPages ?? requestedPage)
  } catch (error) {
    errorMessage.value = error?.message || '판매 내역을 불러오지 못했습니다.'
    hasNext.value = false
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadMoreSales()
})
</script>
