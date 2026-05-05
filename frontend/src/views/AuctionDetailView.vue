<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { cancelAuction, getAuctionDetail } from '../api/auctions'
import { getAuctionInquiryList } from '../api/auctionInquiries'
import { getAuctionBids } from '../api/bids'
import { getCategoryList } from '../api/categories'
import { getUserSellerProfile } from '../api/users'
import { createWishlist, deleteWishlist, getWishlistStatus } from '../api/wishlists'
import AuctionDetailScreen from '../components/AuctionDetailScreen.vue'
import { useToast } from '../composables/useToast'
import { assets } from '../data/marketplaceData'
import { authState } from '../lib/authSession'
import { buildCategoryPathLabelById, getFallbackCategories, normalizeCategoryRows } from '../utils/category'
import { normalizeAuctionDetail } from '../utils/marketplace'

const route = useRoute()
const router = useRouter()
const { showToast } = useToast()
const errorMessage = ref('')
const item = ref(null)
const loading = ref(false)
const cancelProcessing = ref(false)
const wishlistProcessing = ref(false)

function resolveSellerUserId(detail = {}) {
  const candidates = [
    detail.sellerMemberId,
    detail.memberId,
    detail.userId,
    detail.ownerId,
    detail.sellerId,
  ]

  const resolvedId = candidates.find((value) => value !== null && value !== undefined && value !== '')

  if (resolvedId !== null && resolvedId !== undefined && resolvedId !== '') {
    return resolvedId
  }

  const currentNickname = String(authState.nickname || '').trim()
  const currentName = String(authState.name || '').trim()
  const sellerNickname = String(detail.sellerNickname || '').trim()

  if (authState.memberId && sellerNickname && (sellerNickname === currentNickname || sellerNickname === currentName)) {
    return authState.memberId
  }

  return null
}

async function loadAuctionDetail(auctionId) {
  if (!auctionId) {
    item.value = null
    errorMessage.value = ''
    showToast('경매 정보를 찾을 수 없습니다.', { color: 'error' })
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    const detail = await getAuctionDetail(auctionId)
    const sellerUserId = resolveSellerUserId(detail)
    const [categoryResponse, bidResponse, inquiryResponse, sellerProfileResponse, wishlistStatusResponse] = await Promise.allSettled([
      getCategoryList(),
      authState.isAuthenticated ? getAuctionBids(auctionId, { page: 1, size: 20 }) : Promise.resolve({ content: [] }),
      authState.isAuthenticated ? getAuctionInquiryList(auctionId, { page: 1, size: 20 }) : Promise.resolve({ content: [] }),
      authState.isAuthenticated && sellerUserId ? getUserSellerProfile(sellerUserId) : Promise.resolve(null),
      authState.isAuthenticated ? getWishlistStatus(auctionId) : Promise.resolve({ wished: false }),
    ])
    const categorySource = categoryResponse.status === 'fulfilled'
      ? categoryResponse.value?.categories || categoryResponse.value || []
      : []
    const categoryRows = normalizeCategoryRows(
      Array.isArray(categorySource) && categorySource.length ? categorySource : getFallbackCategories(),
    )
    const categoryPathLabel = buildCategoryPathLabelById(categoryRows, detail?.item?.category?.id)

    item.value = normalizeAuctionDetail(detail, {
      bidHistory: bidResponse.status === 'fulfilled' ? bidResponse.value?.content || [] : [],
      categoryPathLabel,
      inquiries: inquiryResponse.status === 'fulfilled' ? inquiryResponse.value?.content || [] : [],
      isAuthenticated: authState.isAuthenticated,
      sellerProfileData: sellerProfileResponse.status === 'fulfilled' ? sellerProfileResponse.value : null,
      wishlistStatus: wishlistStatusResponse.status === 'fulfilled' ? wishlistStatusResponse.value : { wished: false },
    })
  } catch (error) {
    item.value = null
    errorMessage.value = ''
    showToast(error?.message || '경매 상세 정보를 불러오지 못했습니다.', { color: 'error' })
  } finally {
    loading.value = false
  }
}

async function toggleWishlist() {
  if (!item.value?.auctionId || wishlistProcessing.value) {
    return
  }

  if (!authState.isAuthenticated) {
    router.push('/login')
    return
  }

  wishlistProcessing.value = true

  try {
    if (item.value.isWished) {
      await deleteWishlist(item.value.auctionId)
      item.value = {
        ...item.value,
        isWished: false,
        wishCount: Math.max(Number(item.value.wishCount || 0) - 1, 0),
      }
      return
    }

    await createWishlist(item.value.auctionId)
    item.value = {
      ...item.value,
      isWished: true,
      wishCount: Number(item.value.wishCount || 0) + 1,
    }
  } catch (error) {
    showToast(error?.message || '찜 처리에 실패했습니다.', { color: 'error' })
  } finally {
    wishlistProcessing.value = false
  }
}

function backToList() {
  if (route.query.from === 'search' && String(route.query.q || '').trim()) {
    const query = { q: String(route.query.q || '').trim() }

    if (String(route.query.sort || '').trim()) {
      query.sort = String(route.query.sort || '').trim()
    }

    router.push({
      name: 'auction-search',
      query,
    })
    return
  }

  const categoryId = item.value?.categoryId || Number(route.query.categoryId) || null
  const sortKey = String(route.query.sort || '')
  const query = {}

  if (categoryId) {
    query.categoryId = categoryId
  }

  if (sortKey) {
    query.sort = sortKey
  }

  router.push({
    name: 'auction-list',
    query,
  })
}

function openEditPage() {
  const auctionId = item.value?.auctionId || route.params.id

  if (!auctionId) {
    return
  }

  router.push(`/auctions/${auctionId}/edit`)
}

async function handleCancelAuction() {
  const auctionId = item.value?.auctionId || route.params.id

  if (!auctionId || cancelProcessing.value) {
    return
  }

  const confirmed = window.confirm('이 경매를 삭제하시겠습니까?')

  if (!confirmed) {
    return
  }

  cancelProcessing.value = true
  errorMessage.value = ''

  try {
    await cancelAuction(auctionId)
    backToList()
  } catch (error) {
    showToast(error?.message || '경매 삭제에 실패했습니다.', { color: 'error' })
  } finally {
    cancelProcessing.value = false
  }
}

watch(
  () => route.params.id,
  (value) => {
    loadAuctionDetail(String(value || ''))
  },
  { immediate: true },
)
</script>

<template>
  <AuctionDetailScreen
    :assets="assets"
    :cancel-processing="cancelProcessing"
    :error-message="errorMessage"
    :item="item"
    :loading="loading"
    :wishlist-processing="wishlistProcessing"
    @back="backToList"
    @cancel-auction="handleCancelAuction"
    @edit-auction="openEditPage"
    @refresh="loadAuctionDetail(String(route.params.id || ''))"
    @toggle-wishlist="toggleWishlist"
  />
</template>
