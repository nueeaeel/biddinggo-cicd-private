<template>
  <WishlistScreen
    :has-next="hasNext"
    :items="wishlistItems"
    :loading="loading"
    :selected-sort="selectedSort"
    :wishlist-processing-ids="wishlistProcessingIds"
    @load-more="loadMoreWishlists"
    @open-detail="openAuctionDetail"
    @sort-change="changeSortOption"
    @toggle-wishlist="toggleWishlist"
  />
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { createWishlist, deleteWishlist } from '../api/wishlists'
import { getUserWishlists } from '../api/users'
import noImage from '../assets/no-image.svg'
import WishlistScreen from '../components/mypage/wishlists/WishlistScreen.vue'
import { authState } from '../lib/authSession'
import { getCountdownLabel } from '../utils/marketplace'

const router = useRouter()
const wishlistItems = ref([])
const wishlistProcessingIds = ref(new Set())
const hasNext = ref(true)
const loading = ref(false)
const page = ref(1)
const requestVersion = ref(0)
const selectedSort = ref('latest')
const pageSize = 10

const statusLabels = {
  PENDING: '경매 예정',
  ON_GOING: '경매 진행 중',
  ENDED: '경매 종료',
  CANCELLED: '경매 취소',
}

const sortQueryMap = {
  latest: { sortBy: 'CREATED_AT', order: 'DESC' },
  deadline: { sortBy: 'END_DATE', order: 'ASC' },
  interest: { sortBy: 'WISH_COUNT', order: 'DESC' },
  popularity: { sortBy: 'POPULARITY', order: 'DESC' },
  'price-asc': { sortBy: 'PRICE', order: 'ASC' },
  'price-desc': { sortBy: 'PRICE', order: 'DESC' },
}

function formatAmount(value) {
  return `${Number(value || 0).toLocaleString('ko-KR')} 원`
}

function formatRemainingTime(endDate) {
  if (!endDate) {
    return ''
  }

  const endTime = new Date(endDate).getTime()

  if (Number.isNaN(endTime)) {
    return ''
  }

  const remainingMilliseconds = endTime - Date.now()

  if (remainingMilliseconds <= 0) {
    return '종료'
  }

  if (remainingMilliseconds < 60000) {
    return '1분 미만'
  }

  const remainingMinutes = Math.ceil(remainingMilliseconds / 60000)
  const days = Math.floor(remainingMinutes / 1440)
  const hours = Math.floor((remainingMinutes % 1440) / 60)
  const minutes = remainingMinutes % 60

  if (days > 0) {
    return `${days}일 ${hours}시간`
  }

  if (hours > 0) {
    return `${hours}시간 ${minutes}분`
  }

  return `${minutes}분`
}

function normalizeWishlistItem(item = {}) {
  const price = item.vickreyPrice || item.startPrice
  const title = item.name || `상품 #${item.itemId}`
  const bidCount = `입찰 ${Number(item.bidCount || 0).toLocaleString('ko-KR')}건`
  const auctionType = String(item.type || '').toUpperCase()
  const inspectionYn = String(
    item.inspectionYn
    ?? item.inspection_yn
    ?? item.item?.inspectionYn
    ?? item.item?.inspection_yn
    ?? '',
  ).trim().toUpperCase()
  const isExtendedAuction = [item.extendAuction, item.extend_auction, item.extendedAuction, item.extended_auction, item.extensionYn, item.extension_yn, item.extendedYn, item.extended_yn]
    .some((value) => ['N', 'NO', 'FALSE', '0'].includes(String(value || '').trim().toUpperCase()))

  return {
    id: item.auctionId,
    auctionId: item.auctionId,
    bidCount,
    bids: bidCount,
    brand: item.brand || '',
    currentPrice: formatAmount(price),
    createdAt: item.createdAt,
    deadlineMinutes: Number.MAX_SAFE_INTEGER,
    highlighted: item.status === 'ON_GOING',
    highlight: item.status === 'ON_GOING',
    image: item.representativeImageUrl || noImage,
    interestCount: Number(item.wishCount || 0),
    isInspected: inspectionYn === 'YES',
    isExtendedAuction,
    isTimeDeal: auctionType === 'TIME_DEAL',
    isWished: true,
    itemId: item.itemId,
    name: title,
    numericPrice: Number(price || 0),
    price: formatAmount(price),
    status: item.status,
    tag: statusLabels[item.status] || item.status || '',
    time: getCountdownLabel(item.endDate),
    title,
    type: item.type,
    wishCount: Number(item.wishCount || 0),
  }
}

function updateWishlistState(auctionId, patch) {
  wishlistItems.value = wishlistItems.value.map((item) => (
    item.auctionId === auctionId ? { ...item, ...patch } : item
  ))
}

function resetWishlists() {
  wishlistItems.value = []
  page.value = 1
  hasNext.value = true
  loading.value = false
  requestVersion.value += 1
}

async function loadMoreWishlists() {
  if (loading.value || !hasNext.value) {
    return
  }

  const requestedPage = page.value
  const currentRequestVersion = requestVersion.value
  const sortQuery = sortQueryMap[selectedSort.value] || sortQueryMap.latest
  loading.value = true

  try {
    const response = await getUserWishlists({
      page: requestedPage,
      size: pageSize,
      order: sortQuery.order,
      sortBy: sortQuery.sortBy,
    })

    if (currentRequestVersion !== requestVersion.value) {
      return
    }

    const content = response?.content || []
    const existingIds = new Set(wishlistItems.value.map((item) => item.id))
    const nextItems = content.map(normalizeWishlistItem).filter((item) => !existingIds.has(item.id))

    wishlistItems.value = [...wishlistItems.value, ...nextItems]
    page.value += 1
    hasNext.value = typeof response?.hasNext === 'boolean'
      ? response.hasNext
      : requestedPage < Number(response?.totalPages ?? requestedPage)
  } catch {
    if (currentRequestVersion === requestVersion.value) {
      hasNext.value = false
    }
  } finally {
    if (currentRequestVersion === requestVersion.value) {
      loading.value = false
    }
  }
}

function changeSortOption(sortOption) {
  if (selectedSort.value === sortOption) {
    return
  }

  selectedSort.value = sortOption
  resetWishlists()
  loadMoreWishlists()
}

async function toggleWishlist(item) {
  if (!item?.auctionId) {
    return
  }

  if (!authState.isAuthenticated) {
    router.push('/login')
    return
  }

  const auctionId = item.auctionId
  const nextProcessing = new Set(wishlistProcessingIds.value)

  if (nextProcessing.has(auctionId)) {
    return
  }

  nextProcessing.add(auctionId)
  wishlistProcessingIds.value = nextProcessing

  const previousItems = [...wishlistItems.value]
  const previousState = {
    isWished: Boolean(item.isWished),
    wishCount: Number(item.wishCount || 0),
    interestCount: Number(item.interestCount || item.wishCount || 0),
  }

  try {
    if (item.isWished) {
      wishlistItems.value = wishlistItems.value.filter((entry) => entry.auctionId !== auctionId)
      await deleteWishlist(auctionId)
      return
    }

    updateWishlistState(auctionId, {
      isWished: true,
      wishCount: previousState.wishCount + 1,
      interestCount: previousState.interestCount + 1,
    })
    await createWishlist(auctionId)
  } catch {
    wishlistItems.value = previousItems
  } finally {
    const doneProcessing = new Set(wishlistProcessingIds.value)
    doneProcessing.delete(auctionId)
    wishlistProcessingIds.value = doneProcessing
  }
}

function openAuctionDetail(item) {
  const auctionId = item?.auctionId || item?.id

  if (auctionId) {
    router.push(`/auctions/${auctionId}`)
  }
}

onMounted(() => {
  loadMoreWishlists()
})
</script>
