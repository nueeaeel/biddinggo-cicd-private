<template>
  <DashboardScreen
    :bid-items="bidItems"
    :confirm-purchase-action="confirmPurchaseAction"
    :create-address="createAddress"
    :delete-account-action="deleteAccount"
    :delete-address="deleteAddress"
    :load-address-book="loadAddressBook"
    :load-purchase-detail="loadPurchaseDetail"
    :load-sales-detail="loadSalesDetail"
    :overview-user="overviewUser"
    :purchase-status-items="purchaseStatusItems"
    :save-purchase-shipping-address="savePurchaseShippingAddress"
    :save-sales-shipping="saveSalesShipping"
    :sales-history-items="salesHistoryItems"
    :set-default-address="setDefaultAddress"
    :submit-review-action="submitReviewAction"
    @open-detail="openAuctionDetail"
  />
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { createAuctionReview } from '../api/reviews'
import {
  createUserAddress,
  deleteMemberAccount,
  deleteUserAddress,
  confirmWinnerDeal,
  getUserAddresses,
  getUserDashboard,
  getWinnerDealDetail,
  setDefaultUserAddress,
  updateWinnerDealShippingAddress,
  updateWinnerDealTrackingNumber,
} from '../api/users'
import defaultAvatar from '../assets/default-avatar.svg'
import noImage from '../assets/no-image.svg'
import DashboardScreen from '../components/mypage/dashboard/DashboardScreen.vue'
import { useToast } from '../composables/useToast'
import {
  bidItems as fallbackBidItems,
  overviewUser as fallbackOverviewUser,
  purchaseStatusItems as fallbackPurchaseStatusItems,
} from '../data/mypage'
import { salesHistoryItems as fallbackSalesHistoryItems } from '../data/salesHistory'
import { clearSession, markInitialized } from '../lib/authSession'
import { getCountdownLabel } from '../utils/marketplace'

const router = useRouter()
const { showToast } = useToast()
const overviewUser = ref(fallbackOverviewUser)
const bidItems = ref(fallbackBidItems)
const purchaseStatusItems = ref(fallbackPurchaseStatusItems)
const salesHistoryItems = ref(fallbackSalesHistoryItems)

const biddingStatusLabels = {
  PENDING: '경매 예정',
  ON_GOING: '경매 진행 중',
  ENDED: '경매 종료',
  CANCELLED: '경매 취소',
}

const winnerDealStatusLabels = {
  PAID: '배송 대기',
  SHIPPED: '배송 완료',
  CONFIRMED: '거래 완료',
  CANCELLED: '취소',
}

function formatPoints(point) {
  const pointValue = Number(point ?? 0)
  if (Number.isNaN(pointValue)) return `${point} P`
  return `${pointValue.toLocaleString('ko-KR')} P`
}

function formatPrice(price) {
  if (price === null || price === undefined || price === '') return '-'
  const priceValue = Number(price)
  if (Number.isNaN(priceValue)) return String(price)
  return `${priceValue.toLocaleString('ko-KR')}원`
}

function formatBidAmount(value) {
  if (value === null || value === undefined || value === '') return '-'
  return String(value)
}

function formatDate(date) {
  if (!date) return '-'
  const parsedDate = new Date(date)
  if (Number.isNaN(parsedDate.getTime())) return String(date).replace('T', ' ')
  return new Intl.DateTimeFormat('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }).format(parsedDate)
}

function normalizeOverviewUser(user = {}) {
  return {
    ...fallbackOverviewUser,
    avatar: user.imageUrl || user.profileImageUrl || fallbackOverviewUser.avatar || defaultAvatar,
    name: user.nickname || fallbackOverviewUser.name,
    points: formatPoints(user.point ?? fallbackOverviewUser.points),
  }
}

function normalizeBiddingItem(item = {}) {
  const auctionId = item.auctionId
    ?? item.auction?.auctionId
    ?? item.auction?.id
    ?? item.saleAuctionId
    ?? item.id

  return {
    id: item.bidId ?? item.itemId ?? item.endDate ?? item.itemName,
    auctionId,
    status: biddingStatusLabels[item.status] || item.status || '-',
    name: item.itemName || '-',
    image: item.itemImageUrl || item.representativeImageUrl || item.imageUrl || noImage,
    time: getCountdownLabel(item.endDate),
    currentPrice: formatPrice(item.currentPrice),
    myBid: formatBidAmount(item.myBidPrice),
    date: formatDate(item.endDate),
  }
}

function normalizeDealStatus(status) {
  const rawStatus = String(status || '').trim().toUpperCase()
  return winnerDealStatusLabels[rawStatus] || String(status || '-')
}

function hasShippingAddress(item = {}) {
  return Boolean(item.recipient || item.tel || item.zipcode || item.address || item.detailAddress)
}

function hasShippingInfo(item = {}) {
  return Boolean(item.carrier || item.trackingNumber)
}

function isPaidStatus(item = {}) {
  return String(item.status || '').trim().toUpperCase() === 'PAID'
}

function isShippedStatus(item = {}) {
  return String(item.status || '').trim().toUpperCase() === 'SHIPPED'
}

function buildShippingAddress(item = {}) {
  if (!hasShippingAddress(item)) return null
  return {
    name: item.recipient || '',
    phone: item.tel || '',
    zip: item.zipcode || '',
    address1: item.address || '',
    address2: item.detailAddress || '',
  }
}

function buildShippingInfo(item = {}) {
  if (!hasShippingInfo(item)) return null
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

function getSaleModalType(item = {}) {
  if (item.canRegisterTrackingNumber || (isPaidStatus(item) && hasShippingAddress(item) && !hasShippingInfo(item))) {
    return 'seller-needs-shipping-info'
  }
  return 'readonly'
}

function normalizePurchaseDeal(item = {}) {
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
    price: formatPrice(item.winnerPrice),
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

function normalizeSalesDeal(item = {}) {
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
    price: formatPrice(item.winnerPrice),
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

function normalizeSummaryDeal(item = {}) {
  const winnerDealId = item.id ?? item.winnerDealId

  return {
    id: winnerDealId,
    winnerDealId,
    auctionId: item.auctionId,
    itemId: item.itemId,
    viewerRole: item.viewerRole,
    status: winnerDealStatusLabels[item.status] || item.status || '-',
    name: item.itemName || '-',
    image: item.itemImageUrl || item.representativeImageUrl || item.imageUrl || noImage,
    price: formatPrice(item.winnerPrice ?? item.price ?? item.finalPrice ?? item.currentPrice ?? item.myBidPrice),
    date: formatDate(item.createdAt ?? item.endDate ?? item.wonDate ?? item.date),
    modalType: 'readonly',
  }
}

function openAuctionDetail(item) {
  if (!item?.auctionId) return
  router.push(`/auctions/${item.auctionId}`)
}

function normalizeBoolean(value) {
  if (typeof value === 'boolean') return value
  if (typeof value === 'string') return ['true', 'y', 'yes', '1'].includes(value.trim().toLowerCase())
  return Number(value) === 1
}

function normalizeAddressOption(address = {}, index = 0) {
  return {
    id: address.id ?? address.addressId ?? address.addressNo ?? `address-${index}`,
    zip: String(address.zip ?? address.zipcode ?? address.zipCode ?? address.zonecode ?? address.postalCode ?? address.postCode ?? ''),
    address1: address.address ?? address.address1 ?? address.roadAddress ?? address.jibunAddress ?? address.basicAddress ?? '',
    address2: address.address2 ?? address.detailAddress ?? address.detail ?? '',
    primary: normalizeBoolean(
      address.primary
        ?? address.default_yn
        ?? address.defaultYn
        ?? address.defaultAddress
        ?? address.isDefault,
    ),
  }
}

function getAddressRows(response) {
  if (Array.isArray(response)) return response
  if (Array.isArray(response?.content)) return response.content
  if (Array.isArray(response?.addresses)) return response.addresses
  if (Array.isArray(response?.addressList)) return response.addressList
  return []
}

async function loadPurchaseDetail(item) {
  const winnerDealId = item?.winnerDealId ?? item?.id
  if (!winnerDealId) return item
  const detail = await getWinnerDealDetail(winnerDealId)
  return normalizePurchaseDeal({ ...item, ...detail })
}

async function loadSalesDetail(item) {
  const winnerDealId = item?.winnerDealId ?? item?.id
  if (!winnerDealId) return item
  const detail = await getWinnerDealDetail(winnerDealId)
  return normalizeSalesDeal({ ...item, ...detail })
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

async function deleteAccount() {
  await deleteMemberAccount()
  clearSession()
  markInitialized()
  showToast('회원 탈퇴가 완료되었습니다.')
  await router.replace('/')
}

async function savePurchaseShippingAddress(item, address) {
  await updateWinnerDealShippingAddress(item.winnerDealId, {
    zipcode: address.zip,
    address: address.address1,
    detailAddress: address.address2,
    recipient: address.name,
    tel: address.phone,
  })
}

async function confirmPurchaseAction(item) {
  await confirmWinnerDeal(item.winnerDealId)
}

async function submitReviewAction(item, payload) {
  if (!item?.auctionId) {
    throw new Error('리뷰를 등록할 경매 ID가 없습니다.')
  }

  const normalizedRating = Math.min(5, Math.max(1, Math.round(Number(payload.rating || 0))))
  await createAuctionReview(item.auctionId, {
    rating: normalizedRating,
    content: String(payload.content || '').trim(),
  })
}

async function saveSalesShipping(item, shippingInfo) {
  await updateWinnerDealTrackingNumber(item.winnerDealId, {
    carrier: shippingInfo.courier,
    trackingNumber: shippingInfo.trackingNumber,
  })
}

async function loadUserDashboard() {
  try {
    const userDashboard = await getUserDashboard()
    overviewUser.value = normalizeOverviewUser(userDashboard)
    bidItems.value = (userDashboard?.biddingItems || []).map(normalizeBiddingItem)
    purchaseStatusItems.value = (userDashboard?.purchaseItems || []).map(normalizeSummaryDeal)
    salesHistoryItems.value = (userDashboard?.salesItems || []).map(normalizeSummaryDeal)
  } catch {
    overviewUser.value = fallbackOverviewUser
    bidItems.value = fallbackBidItems
    purchaseStatusItems.value = fallbackPurchaseStatusItems
    salesHistoryItems.value = fallbackSalesHistoryItems
  }
}

onMounted(() => {
  loadUserDashboard()
})
</script>
