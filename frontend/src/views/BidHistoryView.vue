<template>
  <BidHistoryScreen
    :has-next="hasNext"
    :items="bidItems"
    :loading="loading"
    @open-detail="openDetail"
    @load-more="loadMoreBids"
  />
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getUserBids } from '../api/users'
import noImage from '../assets/no-image.svg'
import BidHistoryScreen from '../components/mypage/bids/BidHistoryScreen.vue'
import { getCountdownLabel } from '../utils/marketplace'

const router = useRouter()
const bidItems = ref([])
const hasNext = ref(true)
const loading = ref(false)
const page = ref(1)
const pageSize = 10
const auctionStatusLabels = {
  PENDING: '경매 예정',
  ON_GOING: '경매 진행 중',
  ENDED: '경매 종료',
  CANCELLED: '경매 취소',
}

function formatBidAmount(value) {
  if (value === null || value === undefined || value === '') {
    return '-'
  }

  return String(value)
}

function formatDate(date) {
  if (!date) {
    return ''
  }

  return String(date).replace('T', ' ')
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

function normalizeBidItem(item = {}) {
  return {
    id: item.id,
    auctionId: item.auctionId,
    status: auctionStatusLabels[item.status] || item.status || '-',
    name: item.name || '-',
    image: item.representativeImageUrl || item.imageUrl || noImage,
    time: getCountdownLabel(item.endDate),
    currentPrice: formatBidAmount(item.vickreyPrice ?? item.startPrice),
    myBid: formatBidAmount(item.amount),
    date: formatDate(item.createdAt),
  }
}

function openDetail(item) {
  if (!item?.auctionId) {
    return
  }

  router.push(`/auctions/${item.auctionId}`)
}

function findFirstNumber(...values) {
  const value = values.find((candidate) => Number.isFinite(Number(candidate)))
  return value === undefined ? null : Number(value)
}

function findFirstBoolean(...values) {
  return values.find((candidate) => typeof candidate === 'boolean')
}

function resolveHasNext(response, content, requestedPage) {
  const explicitHasNext = findFirstBoolean(
    response?.hasNext,
    response?.pageInfo?.hasNext,
    response?.pagination?.hasNext,
  )

  if (explicitHasNext !== undefined) {
    return explicitHasNext
  }

  const explicitLast = findFirstBoolean(
    response?.last,
    response?.isLast,
    response?.pageInfo?.last,
    response?.pagination?.last,
  )

  if (explicitLast !== undefined) {
    return !explicitLast
  }

  const totalPages = findFirstNumber(
    response?.totalPages,
    response?.totalPage,
    response?.pageInfo?.totalPages,
    response?.pageInfo?.totalPage,
    response?.pagination?.totalPages,
    response?.pagination?.totalPage,
  )

  if (totalPages !== null) {
    const zeroBasedPage = findFirstNumber(response?.number, response?.pageable?.pageNumber)

    if (zeroBasedPage !== null) {
      return zeroBasedPage + 1 < totalPages
    }

    const oneBasedPage = findFirstNumber(
      response?.currentPage,
      response?.page,
      response?.pageInfo?.currentPage,
      response?.pageInfo?.page,
      response?.pagination?.currentPage,
      response?.pagination?.page,
    )

    return (oneBasedPage ?? requestedPage) < totalPages
  }

  return content.length >= pageSize
}

async function loadMoreBids() {
  if (loading.value || !hasNext.value) {
    return
  }

  const requestedPage = page.value
  loading.value = true

  try {
    const response = await getUserBids({
      page: requestedPage,
      size: pageSize,
      order: 'ASC',
    })

    const content = response?.content || []
    const existingIds = new Set(bidItems.value.map((item) => item.id))
    const nextItems = content.map(normalizeBidItem).filter((item) => !existingIds.has(item.id))

    if (content.length === 0) {
      hasNext.value = false
      return
    }

    bidItems.value = [
      ...bidItems.value,
      ...nextItems,
    ]
    page.value += 1
    hasNext.value = resolveHasNext(response, content, requestedPage)
  } catch {
    hasNext.value = false
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadMoreBids()
})
</script>
