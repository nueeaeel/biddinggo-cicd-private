<template>
  <AuctionManagementScreen
    :has-next="hasNext"
    :items="auctionItems"
    :loading="loading"
    :selected-tag="selectedTag"
    :summary-items="summaryItems"
    :wishlist-processing-ids="wishlistProcessingIds"
    @filter-change="changeFilter"
    @load-more="loadMoreAuctions"
    @open-detail="openAuctionDetail"
    @register="openRegister"
    @toggle-wishlist="toggleWishlist"
  />
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { createWishlist, deleteWishlist, getWishlistStatus } from '../api/wishlists'
import { getUserManagedAuctions } from '../api/users'
import noImage from '../assets/no-image.svg'
import AuctionManagementScreen from '../components/mypage/auctions/AuctionManagementScreen.vue'
import { useToast } from '../composables/useToast'
import { getCountdownLabel } from '../utils/marketplace'

const router = useRouter()
const { showToast } = useToast()

const FILTERS = [
  { label: '전체', value: 'ALL' },
  { label: '예정', value: 'PENDING' },
  { label: '진행 중', value: 'ONGOING' },
  { label: '낙찰', value: 'SUCCESS' },
  { label: '유찰', value: 'FAILED' },
  { label: '취소', value: 'CANCELLED' },
]

const auctionItems = ref([])
const wishlistProcessingIds = ref(new Set())
const loading = ref(false)
const hasNext = ref(true)
const page = ref(1)
const pageSize = 10
const defaultOffset = 1073741824
const requestVersion = ref(0)
const selectedTag = ref('전체')
const summary = ref({
  totalCount: 0,
  ongoingCount: 0,
  successCount: 0,
  failedCount: 0,
})

const filterLabelToTypeMap = Object.fromEntries(FILTERS.map((filter) => [filter.label, filter.value]))

const summaryItems = computed(() => ([
  { label: '전체 경매', value: formatCount(summary.value.totalCount) },
  { label: '진행 중', value: formatCount(summary.value.ongoingCount) },
  { label: '낙찰', value: formatCount(summary.value.successCount) },
  { label: '유찰', value: formatCount(summary.value.failedCount) },
]))

function formatCount(value) {
  return Number(value || 0).toLocaleString('ko-KR')
}

function formatAmount(value) {
  return `${Number(value || 0).toLocaleString('ko-KR')}원`
}

function formatRemainingTime(endDate) {
  if (!endDate) {
    return '-'
  }

  const endTime = new Date(endDate).getTime()

  if (Number.isNaN(endTime)) {
    return '-'
  }

  const remainingMilliseconds = endTime - Date.now()

  if (remainingMilliseconds <= 0) {
    return '종료'
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

function normalizeAuctionItem(item = {}) {
  const price = item.currentPrice ?? item.startPrice ?? 0
  const bidCount = Number(item.bidCount || 0)
  const auctionType = String(item.auctionType || '').trim().toUpperCase()
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
    title: item.itemName || `경매 #${item.auctionId}`,
    name: item.itemName || `경매 #${item.auctionId}`,
    image: item.imageUrl || noImage,
    price: formatAmount(price),
    bids: `입찰 ${bidCount}건`,
    bidCount,
    time: getCountdownLabel(item.endDate),
    isExtendedAuction,
    isTimeDeal: auctionType === 'TIME_DEAL',
    isInspected: inspectionYn === 'YES',
    highlight: item.saleStatus === 'ONGOING',
    highlighted: item.saleStatus === 'ONGOING',
    saleStatus: item.saleStatus || '',
    endDate: item.endDate || '',
    isWished: false,
  }
}

function updateAuctionWishlistState(auctionId, patch) {
  auctionItems.value = auctionItems.value.map((item) => (
    item.auctionId === auctionId ? { ...item, ...patch } : item
  ))
}

async function hydrateWishlistStatuses(items) {
  const statuses = await Promise.allSettled(
    items.map(async (item) => ({
      auctionId: item.auctionId,
      status: await getWishlistStatus(item.auctionId),
    })),
  )

  statuses.forEach((result) => {
    if (result.status !== 'fulfilled') {
      return
    }

    updateAuctionWishlistState(result.value.auctionId, {
      isWished: Boolean(
        result.value.status?.wished
          ?? result.value.status?.isWished
          ?? result.value.wished
          ?? result.value.isWished,
      ),
    })
  })
}

function resetAuctions() {
  auctionItems.value = []
  page.value = 1
  hasNext.value = true
  loading.value = false
  requestVersion.value += 1
}

async function loadMoreAuctions() {
  if (loading.value || !hasNext.value) {
    return
  }

  const requestedPage = page.value
  const currentRequestVersion = requestVersion.value
  const type = filterLabelToTypeMap[selectedTag.value] || 'ALL'
  loading.value = true

  try {
    const response = await getUserManagedAuctions({
      type,
      page: requestedPage,
      size: pageSize,
      order: 'ASC',
      offset: defaultOffset,
    })

    if (currentRequestVersion !== requestVersion.value) {
      return
    }

    summary.value = response?.summary || {
      totalCount: 0,
      ongoingCount: 0,
      successCount: 0,
      failedCount: 0,
    }

    const auctionPage = response?.auctions || {}
    const content = Array.isArray(auctionPage?.content) ? auctionPage.content : []
    const normalizedItems = content.map(normalizeAuctionItem)
    const existingIds = new Set(auctionItems.value.map((item) => item.id))
    const nextItems = normalizedItems.filter((item) => !existingIds.has(item.id))

    auctionItems.value = [...auctionItems.value, ...nextItems]
    page.value += 1
    hasNext.value = typeof auctionPage?.hasNext === 'boolean'
      ? auctionPage.hasNext
      : requestedPage < Number(auctionPage?.totalPages ?? requestedPage)

    await hydrateWishlistStatuses(nextItems)
  } catch (error) {
    if (currentRequestVersion === requestVersion.value) {
      hasNext.value = false
      showToast(error?.message || '경매 관리 목록을 불러오지 못했습니다.', { color: 'error' })
    }
  } finally {
    if (currentRequestVersion === requestVersion.value) {
      loading.value = false
    }
  }
}

function changeFilter(label) {
  if (selectedTag.value === label) {
    return
  }

  selectedTag.value = label
  resetAuctions()
  loadMoreAuctions()
}

async function toggleWishlist(item) {
  if (!item?.auctionId) {
    return
  }

  const auctionId = item.auctionId
  const nextProcessing = new Set(wishlistProcessingIds.value)

  if (nextProcessing.has(auctionId)) {
    return
  }

  nextProcessing.add(auctionId)
  wishlistProcessingIds.value = nextProcessing

  const previousState = {
    isWished: Boolean(item.isWished),
  }

  try {
    if (item.isWished) {
      updateAuctionWishlistState(auctionId, { isWished: false })
      await deleteWishlist(auctionId)
      return
    }

    updateAuctionWishlistState(auctionId, { isWished: true })
    await createWishlist(auctionId)
  } catch (error) {
    updateAuctionWishlistState(auctionId, previousState)
    showToast(error?.message || '관심 경매 처리에 실패했습니다.', { color: 'error' })
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

function openRegister() {
  router.push('/register')
}

onMounted(() => {
  loadMoreAuctions()
})
</script>
