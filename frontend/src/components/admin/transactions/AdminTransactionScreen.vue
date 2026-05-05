<template>
  <AdminLayout>
    <section class="page-header-block">
      <h1>거래 내역 조회</h1>
    </section>

    <section class="filter-bar admin-filter-bar">
      <div class="filter-chips">
        <button
          v-for="status in adminTransactionStatusFilters"
          :key="status"
          class="chip admin-chip"
          :class="{ active: selectedFilter === status }"
          type="button"
          @click="selectedFilter = status"
        >
          {{ status }}
        </button>
      </div>

      <v-text-field
        v-model="searchQuery"
        class="page-search-field admin-search-field"
        density="comfortable"
        hide-details
        placeholder="상품명, 거래 번호 검색"
        prepend-inner-icon="mdi-magnify"
        variant="solo"
      />
    </section>

    <section class="admin-table-card surface-card">
      <table class="admin-transaction-table">
        <thead>
          <tr>
            <th>거래 번호</th>
            <th>검수 여부</th>
            <th>판매자</th>
            <th>구매자</th>
            <th>상품명</th>
            <th>거래 금액</th>
            <th>거래 일자</th>
            <th>상태</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in filteredRows" :key="row.tradeNo" @click="openDetail(row)">
            <td class="admin-transaction-table__strong">{{ row.tradeNo }}</td>
            <td>{{ inspectionLabel(row) }}</td>
            <td>{{ row.seller }}</td>
            <td>{{ row.buyer }}</td>
            <td>{{ row.productName }}</td>
            <td>{{ row.amount }}</td>
            <td>{{ row.tradedAt }}</td>
            <td>
              <AdminStatusBadge :status="row.status" />
            </td>
          </tr>
          <tr v-if="filteredRows.length === 0">
            <td class="admin-transaction-table__empty" colspan="8">조건에 맞는 거래 내역이 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </section>

    <AdminTransactionDetailModal
      v-if="selectedTrade"
      :item="selectedTrade"
      @close="closeDetail"
      @open-shipping="openShippingModal"
    />

    <AdminShippingInfoModal
      v-if="selectedTrade && isShippingModalOpen"
      :item="selectedTrade"
      :shipping-companies="shippingCompanies"
      @close="isShippingModalOpen = false"
      @submit="saveShippingInfo"
    />
  </AdminLayout>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import AdminLayout from '../../layout/AdminLayout.vue'
import { shippingCompanies } from '../../../data/admin'
import { assets } from '../../../data/marketplaceData'
import AdminStatusBadge from '../shared/AdminStatusBadge.vue'
import AdminShippingInfoModal from './AdminShippingInfoModal.vue'
import AdminTransactionDetailModal from './AdminTransactionDetailModal.vue'
import {
  fetchAdminTransactions,
  fetchAdminTransactionDetail,
  registerAdminTrackingNumber,
} from '../../../api/adminTransactions'

const adminTransactionStatusFilters = ['전체', '거래 완료', '발송 대기', '배송 중', '거래 취소']

const selectedFilter = ref('전체')
const searchQuery = ref('')
const rows = ref([])
const selectedTrade = ref(null)
const isShippingModalOpen = ref(false)

const isLoading = ref(false)
const isDetailLoading = ref(false)
const isSubmittingShipping = ref(false)
const errorMessage = ref('')

const statusLabelToApiStatus = {
  전체: undefined,
  '거래 완료': 'CONFIRMED',
  '발송 대기': 'PAID',
  '배송 중': 'SHIPPED',
  '거래 취소': 'CANCELLED',
}

function statusToLabel(status) {
  if (status === 'PAID') return '발송 대기'
  if (status === 'SHIPPED') return '배송 중'
  if (status === 'CONFIRMED') return '거래 완료'
  if (status === 'CANCELLED') return '거래 취소'
  return String(status || '-')
}

function formatDate(value) {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)

  const yyyy = date.getFullYear()
  const mm = String(date.getMonth() + 1).padStart(2, '0')
  const dd = String(date.getDate()).padStart(2, '0')
  return `${yyyy}.${mm}.${dd}`
}

function formatAmount(value) {
  const amount = Number(value)
  if (!Number.isFinite(amount)) return '-'
  return `${amount.toLocaleString('ko-KR')} 원`
}

function toRow(item) {
  const inspectionYn = String(item.inspectionYn || '').toUpperCase()

  return {
    winnerDealId: item.winnerDealId,
    tradeNo: item.dealNumber || `WD-${item.winnerDealId}`,
    seller: item.sellerName || '-',
    buyer: item.winnerName || '-',
    productName: item.itemName || '-',
    amount: formatAmount(item.winnerPrice),
    tradedAt: formatDate(item.createdAt),
    status: statusToLabel(item.status),
    statusRaw: item.status,
    inspectionYn: inspectionYn || null,
    inspectionItem: inspectionYn ? inspectionYn === 'YES' : null,
    category: '-',
    productTitle: item.itemName || '-',
    finalBid: formatAmount(item.winnerPrice),
    image: assets.listWatchImage,
    carrier: null,
    trackingNumber: null,
    shippingAddress: {
      name: '-',
      phone: '-',
      zip: '-',
      address1: '-',
      address2: '-',
    },
    shippingInfo: null,
    canRegisterTrackingNumber: false,
  }
}

function inspectionLabel(row) {
  if (row?.inspectionItem === true) return '검수 상품'
  if (row?.inspectionItem === false) return '일반 상품'
  if (String(row?.inspectionYn || '').toUpperCase() === 'YES') return '검수 상품'
  if (String(row?.inspectionYn || '').toUpperCase() === 'NO') return '일반 상품'
  return '미확인'
}

function applyDetail(baseRow, detail) {
  const hasShipping = Boolean(detail?.carrier && detail?.trackingNumber)
  const inspectionItem = typeof detail?.inspectionItem === 'boolean'
    ? detail.inspectionItem
    : baseRow.inspectionItem === true

  return {
    ...baseRow,
    winnerDealId: detail?.winnerDealId ?? baseRow.winnerDealId,
    tradeNo: detail?.dealNumber ?? baseRow.tradeNo,
    seller: detail?.sellerName ?? baseRow.seller,
    buyer: detail?.winnerName ?? baseRow.buyer,
    productName: detail?.itemName ?? baseRow.productName,
    amount: formatAmount(detail?.winnerPrice ?? 0),
    tradedAt: formatDate(detail?.createdAt),
    status: statusToLabel(detail?.status),
    statusRaw: detail?.status ?? baseRow.statusRaw,
    inspectionYn: detail?.inspectionYn ?? baseRow.inspectionYn ?? null,
    inspectionItem,
    category: inspectionItem ? '검수 상품' : '일반 상품',
    productTitle: detail?.itemName ?? baseRow.productTitle,
    finalBid: formatAmount(detail?.winnerPrice ?? 0),
    image: detail?.itemImageUrl || baseRow.image,
    carrier: detail?.carrier ?? null,
    trackingNumber: detail?.trackingNumber ?? null,
    shippingAddress: {
      name: detail?.recipient || detail?.winnerName || '-',
      phone: detail?.tel || '-',
      zip: detail?.zipcode || '-',
      address1: detail?.address || '-',
      address2: detail?.detailAddress || '-',
    },
    shippingInfo: hasShipping
      ? {
          carrier: detail.carrier,
          trackingNumber: detail.trackingNumber,
        }
      : null,
    canRegisterTrackingNumber: Boolean(detail?.canRegisterTrackingNumber),
  }
}

const filteredRows = computed(() => {
  const keyword = searchQuery.value.trim().toLowerCase()

  return rows.value.filter((row) => {
    const matchesStatus = selectedFilter.value === '전체' || row.status === selectedFilter.value
    const matchesKeyword =
      !keyword ||
      String(row.productName || '').toLowerCase().includes(keyword) ||
      String(row.tradeNo || '').toLowerCase().includes(keyword)

    return matchesStatus && matchesKeyword
  })
})

async function loadRows() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const page = await fetchAdminTransactions({
      page: 1,
      size: 100,
      order: 'DESC',
      status: statusLabelToApiStatus[selectedFilter.value],
    })

    rows.value = Array.isArray(page?.content) ? page.content.map(toRow) : []
  } catch (error) {
    errorMessage.value = String(error?.message || '거래 내역 조회에 실패했습니다.')
  } finally {
    isLoading.value = false
  }
}

async function openDetail(row) {
  selectedTrade.value = row
  isDetailLoading.value = true

  try {
    const detail = await fetchAdminTransactionDetail(row.winnerDealId)
    const nextTrade = applyDetail(row, detail)
    selectedTrade.value = nextTrade
    rows.value = rows.value.map((item) => (
      item.winnerDealId === nextTrade.winnerDealId
        ? { ...item, inspectionItem: nextTrade.inspectionItem, inspectionYn: nextTrade.inspectionYn }
        : item
    ))
  } catch (error) {
    errorMessage.value = String(error?.message || '거래 상세 조회에 실패했습니다.')
    // 상세 조회 실패 시 조건부 버튼 오해를 줄이기 위해 모달을 닫는다.
    selectedTrade.value = null
  } finally {
    isDetailLoading.value = false
  }
}

function closeDetail() {
  selectedTrade.value = null
  isShippingModalOpen.value = false
}

function openShippingModal() {
  if (!selectedTrade.value || isSubmittingShipping.value) return
  isShippingModalOpen.value = true
}

async function saveShippingInfo(payload) {
  if (!selectedTrade.value?.winnerDealId || isSubmittingShipping.value) return

  isSubmittingShipping.value = true

  try {
    await registerAdminTrackingNumber(selectedTrade.value.winnerDealId, {
      carrier: payload.carrier,
      trackingNumber: payload.trackingNumber,
    })

    const detail = await fetchAdminTransactionDetail(selectedTrade.value.winnerDealId)
    selectedTrade.value = applyDetail(selectedTrade.value, detail)
    isShippingModalOpen.value = false
    await loadRows()
  } catch (error) {
    errorMessage.value = String(error?.message || '배송 정보 등록에 실패했습니다.')
  } finally {
    isSubmittingShipping.value = false
  }
}

watch(selectedFilter, loadRows)
onMounted(loadRows)
</script>

