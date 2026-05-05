<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAuctionList } from '../api/auctions'
import { getCategoryList } from '../api/categories'
import { createWishlist, deleteWishlist, getWishlistStatus } from '../api/wishlists'
import AuctionListScreen from '../components/AuctionListScreen.vue'
import { assets } from '../data/marketplaceData'
import { authState } from '../lib/authSession'
import { buildCategoryTreeItems, getFallbackCategories, normalizeCategoryRows } from '../utils/category'
import { normalizeAuctionCard } from '../utils/marketplace'

const EXPANDED_CATEGORY_STORAGE_KEY = 'biddinggo.auction-list.expanded-categories'

const router = useRouter()
const route = useRoute()
const categoryRows = ref([])
const errorMessage = ref('')
const expandedCategoryIds = ref(new Set())
const hasNext = ref(true)
const items = ref([])
const loading = ref(false)
const page = ref(1)
const requestVersion = ref(0)
const selectedCategoryId = ref(readCategoryIdFromQuery())
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

const sortOptions = [
  { key: 'wishlist', label: '관심순', sortBy: 'WISH_COUNT', order: 'DESC' },
  { key: 'popularity', label: '인기순', sortBy: 'POPULARITY', order: 'DESC' },
  { key: 'latest', label: '최신순', sortBy: 'CREATED_AT', order: 'DESC' },
  { key: 'deadline', label: '마감 임박순', sortBy: 'END_DATE', order: 'ASC' },
  { key: 'price-low', label: '가격 낮은 순', sortBy: 'PRICE', order: 'ASC' },
  { key: 'price-high', label: '가격 높은 순', sortBy: 'PRICE', order: 'DESC' },
]
const selectedSortKey = ref(readSortKeyFromQuery())

const categories = computed(() => buildCategoryTreeItems(categoryRows.value, selectedCategoryId.value, expandedCategoryIds.value))
const selectedSortOption = computed(() => (
  sortOptions.find((option) => option.key === selectedSortKey.value) || sortOptions[2]
))
const selectedSortLabel = computed(() => selectedSortOption.value.label)

function readCategoryIdFromQuery() {
  const categoryId = Number(route.query.categoryId)

  return Number.isFinite(categoryId) && categoryId > 0 ? categoryId : null
}

function readSortKeyFromQuery() {
  const sortKey = String(route.query.sort || '')

  return sortOptions.some((option) => option.key === sortKey) ? sortKey : 'latest'
}

function readStoredExpandedCategoryIds() {
  if (typeof window === 'undefined') {
    return new Set()
  }

  try {
    const raw = window.localStorage.getItem(EXPANDED_CATEGORY_STORAGE_KEY)
    const ids = raw ? JSON.parse(raw) : []

    return new Set(
      Array.isArray(ids)
        ? ids.map((id) => Number(id)).filter((id) => Number.isFinite(id) && id > 0)
        : [],
    )
  } catch {
    return new Set()
  }
}

function persistExpandedCategoryIds(nextExpandedCategoryIds) {
  if (typeof window === 'undefined') {
    return
  }

  window.localStorage.setItem(
    EXPANDED_CATEGORY_STORAGE_KEY,
    JSON.stringify(Array.from(nextExpandedCategoryIds)),
  )
}

function includeSelectedCategoryPath(baseExpandedCategoryIds = new Set()) {
  const selectedId = Number(selectedCategoryId.value)

  if (!Number.isFinite(selectedId) || selectedId <= 0 || !categoryRows.value.length) {
    return new Set(baseExpandedCategoryIds)
  }

  const next = new Set(baseExpandedCategoryIds)
  const categoriesById = new Map(categoryRows.value.map((category) => [Number(category.id), category]))
  let current = categoriesById.get(selectedId) || null

  while (current?.parentId) {
    next.add(Number(current.parentId))
    current = categoriesById.get(Number(current.parentId)) || null
  }

  return next
}

function buildListQuery({
  categoryId = selectedCategoryId.value,
  sortKey = selectedSortKey.value,
} = {}) {
  const query = {}

  if (categoryId) {
    query.categoryId = categoryId
  }

  if (sortKey && sortKey !== 'latest') {
    query.sort = sortKey
  }

  return query
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

function resetAuctionList() {
  items.value = []
  page.value = 1
  hasNext.value = true
  loading.value = false
  requestVersion.value += 1
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

  return content.length === 12
}

function updateAuctionWishlistState(auctionId, patch) {
  items.value = items.value.map((item) => (
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

async function loadCategories() {
  try {
    const response = await getCategoryList()
    const rows = response?.categories || response || []
    categoryRows.value = normalizeCategoryRows(Array.isArray(rows) && rows.length ? rows : getFallbackCategories())
  } catch {
    categoryRows.value = normalizeCategoryRows(getFallbackCategories())
  }

  expandedCategoryIds.value = includeSelectedCategoryPath(readStoredExpandedCategoryIds())
  persistExpandedCategoryIds(expandedCategoryIds.value)
}

async function loadMoreAuctionList() {
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
      size: 12,
      sortBy: selectedSortOption.value.sortBy,
      status: 'ON_GOING',
      order: selectedSortOption.value.order,
      categoryId: selectedCategoryId.value,
    })

    if (currentRequestVersion !== requestVersion.value) {
      return
    }

    const content = Array.isArray(response?.content) ? response.content : []
    const nextItems = content.map((auction) => ({
      ...normalizeAuctionCard(auction),
      isExtendedAuction: isExtendedAuction(auction),
    }))
    const existingIds = new Set(items.value.map((item) => item.auctionId))
    const uniqueItems = nextItems.filter((item) => !existingIds.has(item.auctionId))

    items.value = [...items.value, ...uniqueItems]
    page.value += 1
    hasNext.value = resolveHasNext(response, content, requestedPage)
    await hydrateWishlistStatuses(uniqueItems)
  } catch (error) {
    if (currentRequestVersion === requestVersion.value) {
      hasNext.value = false
      errorMessage.value = error?.message || '경매 목록을 불러오지 못했습니다.'
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

  router.push({
    name: 'auction-detail',
    params: { id: item.auctionId },
    query: buildListQuery(),
  })
}

function selectCategory(category) {
  if (category?.hasChildren) {
    toggleCategory(category)
    return
  }

  selectedCategoryId.value = Number(category?.id || 0) || null
  resetAuctionList()
  router.replace({
    name: 'auction-list',
    query: buildListQuery(),
  })
  loadMoreAuctionList()
}

function toggleCategory(category) {
  if (!category?.hasChildren) {
    return
  }

  const next = new Set(expandedCategoryIds.value)
  const categoryId = Number(category.id)

  if (next.has(categoryId)) {
    next.delete(categoryId)
  } else {
    next.add(categoryId)
  }

  expandedCategoryIds.value = next
  persistExpandedCategoryIds(next)
}

function selectSort(option) {
  if (!option?.key || selectedSortKey.value === option.key) {
    return
  }

  selectedSortKey.value = option.key
  resetAuctionList()
  router.replace({
    name: 'auction-list',
    query: buildListQuery(),
  })
  loadMoreAuctionList()
}

function submitSearch(keyword) {
  if (!keyword) {
    return
  }

  router.push({
    name: 'auction-search',
    query: {
      q: keyword,
    },
  })
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

onMounted(async () => {
  await loadCategories()
  await loadMoreAuctionList()
})

watch(
  () => [route.query.categoryId, route.query.sort],
  () => {
    const nextCategoryId = readCategoryIdFromQuery()
    const nextSortKey = readSortKeyFromQuery()

    if (selectedCategoryId.value === nextCategoryId && selectedSortKey.value === nextSortKey) {
      return
    }

    selectedCategoryId.value = nextCategoryId
    selectedSortKey.value = nextSortKey
    expandedCategoryIds.value = includeSelectedCategoryPath(expandedCategoryIds.value)
    persistExpandedCategoryIds(expandedCategoryIds.value)
    resetAuctionList()
    loadMoreAuctionList()
  },
)
</script>

<template>
  <AuctionListScreen
    :assets="assets"
    :categories="categories"
    :error-message="errorMessage"
    :has-next="hasNext"
    :items="items"
    :loading="loading"
    :selected-sort-key="selectedSortKey"
    :selected-sort-label="selectedSortLabel"
    :sort-options="sortOptions"
    :toolbar-search-value="''"
    :wishlist-processing-ids="wishlistProcessingIds"
    @load-more="loadMoreAuctionList"
    @open-detail="openDetail"
    @select-category="selectCategory"
    @select-sort="selectSort"
    @submit-search="submitSearch"
    @toggle-category="toggleCategory"
    @toggle-wishlist="toggleWishlist"
  />
</template>
