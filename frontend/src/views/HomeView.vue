<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getAuctionList } from '../api/auctions'
import { createWishlist, deleteWishlist, getWishlistStatus } from '../api/wishlists'
import HomeScreen from '../components/HomeScreen.vue'
import { assets, heroSlides } from '../data/marketplaceData'
import { authState } from '../lib/authSession'
import { normalizeAuctionCard } from '../utils/marketplace'

const router = useRouter()
const hasNext = ref(true)
const homeItems = ref([])
const loading = ref(false)
const errorMessage = ref('')
const page = ref(1)
const requestVersion = ref(0)
const wishlistProcessingIds = ref(new Set())

function isExtendedAuction(item = {}) {
  return [
    item.extendAuction,
    item.extend_auction,
    item.extendedAuction,
    item.extended_auction,
    item.extensionYn,
    item.extension_yn,
    item.extendedYn,
    item.extended_yn,
  ].some((value) => ['N', 'NO', 'FALSE', '0'].includes(String(value || '').trim().toUpperCase()))
}

function findFirstBoolean(...values) {
  return values.find((value) => typeof value === 'boolean')
}

function findFirstNumber(...values) {
  for (const value of values) {
    const number = Number(value)

    if (Number.isFinite(number) && number >= 0) {
      return number
    }
  }

  return null
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

    return requestedPage < totalPages
  }

  return content.length === 4
}

function resetHomeAuctions() {
  homeItems.value = []
  page.value = 1
  hasNext.value = true
  loading.value = false
  requestVersion.value += 1
}

function updateAuctionWishlistState(auctionId, patch) {
  homeItems.value = homeItems.value.map((item) => (
    Number(item.auctionId) === Number(auctionId)
      ? { ...item, ...patch }
      : item
  ))
}

async function hydrateWishlistStatuses(auctionItems) {
  if (!authState.isAuthenticated || !auctionItems.length) {
    return
  }

  const statuses = await Promise.allSettled(
    auctionItems
      .filter((item) => item.auctionId)
      .map(async (item) => ({
        auctionId: item.auctionId,
        status: await getWishlistStatus(item.auctionId),
      })),
  )

  statuses.forEach((result) => {
    if (result.status !== 'fulfilled') {
      return
    }

    updateAuctionWishlistState(result.value.auctionId, {
      isWished: Boolean(result.value.status?.wished ?? result.value.status?.isWished ?? result.value.wished ?? result.value.isWished),
    })
  })
}

async function loadMoreHomeAuctions() {
  if (loading.value || !hasNext.value) {
    return
  }

  const requestedPage = page.value
  const currentRequestVersion = requestVersion.value
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getAuctionList({
      page: requestedPage,
      size: 4,
      sortBy: 'POPULARITY',
      status: 'ON_GOING',
      order: 'DESC',
    })

    if (currentRequestVersion !== requestVersion.value) {
      return
    }

    const content = Array.isArray(response?.content) ? response.content : []
    const nextItems = content.map((auction) => ({
      ...normalizeAuctionCard(auction),
      isExtendedAuction: isExtendedAuction(auction),
    }))
    const existingIds = new Set(homeItems.value.map((item) => item.auctionId))
    const uniqueItems = nextItems.filter((item) => !existingIds.has(item.auctionId))

    homeItems.value = [...homeItems.value, ...uniqueItems]
    page.value += 1
    hasNext.value = resolveHasNext(response, content, requestedPage)
    await hydrateWishlistStatuses(uniqueItems)
  } catch (error) {
    if (currentRequestVersion === requestVersion.value) {
      hasNext.value = false
      errorMessage.value = error?.message || '메인 경매를 불러오지 못했습니다.'
    }
  } finally {
    if (currentRequestVersion === requestVersion.value) {
      loading.value = false
    }
  }
}

function openDetail(item) {
  if (!item?.auctionId) {
    return
  }

  router.push(`/auctions/${item.auctionId}`)
}

function openList() {
  router.push('/auctions')
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

  const previousState = {
    isWished: Boolean(item.isWished),
    wishCount: Number(item.wishCount || 0),
  }

  try {
    if (item.isWished) {
      updateAuctionWishlistState(auctionId, {
        isWished: false,
        wishCount: Math.max(previousState.wishCount - 1, 0),
      })
      await deleteWishlist(auctionId)
      return
    }

    updateAuctionWishlistState(auctionId, {
      isWished: true,
      wishCount: previousState.wishCount + 1,
    })
    await createWishlist(auctionId)
  } catch (error) {
    updateAuctionWishlistState(auctionId, previousState)
    errorMessage.value = error?.message || '찜 처리에 실패했습니다.'
  } finally {
    const doneProcessing = new Set(wishlistProcessingIds.value)
    doneProcessing.delete(auctionId)
    wishlistProcessingIds.value = doneProcessing
  }
}

onMounted(() => {
  resetHomeAuctions()
  loadMoreHomeAuctions()
})
</script>

<template>
  <HomeScreen
    :assets="assets"
    :error-message="errorMessage"
    :has-next="hasNext"
    :hero-slides="heroSlides"
    :items="homeItems"
    :loading="loading"
    :wishlist-processing-ids="wishlistProcessingIds"
    @load-more="loadMoreHomeAuctions"
    @open-detail="openDetail"
    @open-list="openList"
    @toggle-wishlist="toggleWishlist"
  />
</template>
