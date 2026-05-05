<template>
  <AdminLayout>
    <section class="page-header-inline">
      <h1>검수 관리</h1>
      <div class="admin-summary-strip">
        <span>총 {{ totalCount }}건</span>
        <span>대기 {{ waitingCount }}건</span>
      </div>
    </section>

    <section class="filter-bar admin-filter-bar">
      <div class="filter-chips">
        <button
          v-for="status in adminInspectionFilters"
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
        placeholder="상품명 검색"
        prepend-inner-icon="mdi-magnify"
        variant="solo"
      />
    </section>

    <section v-if="errorMessage" class="admin-placeholder-card surface-card">
      <h2>검수 요청 목록을 불러오지 못했습니다.</h2>
      <p>{{ errorMessage }}</p>
      <button class="primary-button" type="button" @click="loadInspections">다시 시도</button>
    </section>

    <section v-else class="admin-table-card surface-card">
      <table class="admin-transaction-table">
        <thead>
          <tr>
            <th>검수 번호</th>
            <th>상품명</th>
            <th>판매자</th>
            <th>요청일</th>
            <th>등급</th>
            <th>상태</th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in displayedInspections" :key="item.inspectionNo">
            <td class="admin-transaction-table__strong">
              <button class="topbar-link-button" type="button" @click="openInspectionDetail(item.inspectionNo)">
                {{ item.inspectionNo }}
              </button>
            </td>
            <td>{{ item.productName }}</td>
            <td>{{ item.seller }}</td>
            <td>{{ item.requestedAt }}</td>
            <td>{{ item.grade }}</td>
            <td><AdminStatusBadge :status="item.status" /></td>
            <td>
              <button class="ghost-button admin-inline-button admin-inline-button--detail" type="button" @click="openInspectionDetail(item.inspectionNo)">
                관리
              </button>
            </td>
          </tr>
          <tr v-if="displayedInspections.length === 0">
            <td class="admin-transaction-table__empty" colspan="7">조건에 맞는 검수 건이 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </section>
    <AdminInspectionApproveModal
      v-if="isApproveModalOpen"
      :grades="gradeOptions"
      @close="closeApproveModal"
      @submit="submitApprove"
    />

    <AdminInspectionRejectModal
      v-if="isRejectModalOpen"
      @close="closeRejectModal"
      @submit="submitReject"
    />

    <AdminInspectionDetailModal
      v-if="selectedDetailItem"
      :assets="assets"
      :badge-class="(status) => {
        if (status === 'PASSED') return 'is-passed'
        if (status === 'FAILED') return 'is-rejected'
        return 'is-pending'
      }"
      :item="selectedDetailItem"
      footer-mode="admin"
      :can-admin-process="isPendingStatus(selectedDetailItem.status) && !isProcessing && !isDetailLoading"
      @close="closeInspectionDetail"
      @approve="openApproveFromDetail"
      @reject="openRejectFromDetail"
    />
  </AdminLayout>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import AdminLayout from '../../layout/AdminLayout.vue'
import AdminStatusBadge from '../shared/AdminStatusBadge.vue'
import AdminInspectionApproveModal from './AdminInspectionApproveModal.vue'
import AdminInspectionRejectModal from './AdminInspectionRejectModal.vue'
import { getInspectionDetail } from '../../../api/inspections'
import { assets } from '../../../data/marketplaceData'
import AdminInspectionDetailModal from './AdminInspectionDetailModal.vue'

import {
  fetchAdminInspections,
  approveAdminInspection,
  rejectAdminInspection,
} from '../../../api/adminInspections'

const adminInspectionFilters = ['전체', '검수 대기', '승인', '반려']
const selectedDetailItem = ref(null)
const isDetailLoading = ref(false)

const selectedFilter = ref('전체')
const searchQuery = ref('')
const inspections = ref([])
const isLoading = ref(false)
const isProcessing = ref(false)
const errorMessage = ref('')
const totalElements = ref(0)

let searchTimer = null
let requestSeq = 0

const gradeOptions = ['새상품', 'S', 'A+', 'A', 'B', 'C']
const approveTargetInspectionNo = ref(null)
const rejectTargetInspectionNo = ref(null)

const isApproveModalOpen = computed(() => approveTargetInspectionNo.value !== null)
const isRejectModalOpen = computed(() => rejectTargetInspectionNo.value !== null)

const totalCount = computed(() => totalElements.value || inspections.value.length)
const waitingCount = computed(() => inspections.value.filter((item) => item.status === '검수 대기').length)

const filterToApiStatus = {
  전체: undefined,
  '검수 대기': 'PENDING',
  승인: 'PASSED',
  반려: 'FAILED',
}

const displayedInspections = computed(() => {
  const keyword = searchQuery.value.trim().toLowerCase()

  return inspections.value.filter((item) => {
    const matchesStatus = selectedFilter.value === '전체' || item.status === selectedFilter.value
    const matchesKeyword =
      !keyword ||
      String(item.productName || '').toLowerCase().includes(keyword) ||
      String(item.seller || '').toLowerCase().includes(keyword)

    return matchesStatus && matchesKeyword
  })
})

function statusToLabel(status) {
  if (status === 'PENDING') return '검수 대기'
  if (status === 'PASSED') return '승인'
  if (status === 'FAILED') return '반려'
  return String(status || '-')
}

function formatDateTime(value) {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)

  const yyyy = date.getFullYear()
  const mm = String(date.getMonth() + 1).padStart(2, '0')
  const dd = String(date.getDate()).padStart(2, '0')
  const hh = String(date.getHours()).padStart(2, '0')
  const mi = String(date.getMinutes()).padStart(2, '0')

  return `${yyyy}.${mm}.${dd} ${hh}:${mi}`
}

function toUiInspection(item) {
  return {
    inspectionNo: item.inspectionId,
    productName: item.name || '-',
    seller: item.sellerNickname || '-',
    requestedAt: formatDateTime(item.createdAt),
    grade: item.quality || '-',

    status: statusToLabel(item.status),   // 테이블 표시용
    statusRaw: item.status,               // 상세 모달 로직용
  }
}

function toDetailItemFromRow(row) {
  return {
    inspectionId: row.inspectionNo,
    title: row.productName,
    brand: '-',
    status: row.statusRaw,
    statusLabel: row.status,
    inspectionDate: row.requestedAt,
    inspectionGrade: row.grade,
    description: '',
    categoryLabel: '',
    image: assets.listWatchImage,
    images: [{ url: assets.listWatchImage }],
    carrier: '',
    trackingNumber: '',
  }
}

async function openInspectionDetail(inspectionNo) {
  const row = inspections.value.find((x) => x.inspectionNo === inspectionNo)
  if (!row) return

  selectedDetailItem.value = toDetailItemFromRow(row)
  isDetailLoading.value = true

  try {
    const detail = await getInspectionDetail(inspectionNo)
    const detailItem = detail?.item || {}
    const detailImages = Array.isArray(detailItem.images) ? detailItem.images.filter((x) => x?.url) : []

    selectedDetailItem.value = {
      ...selectedDetailItem.value,
      title: detailItem.name || selectedDetailItem.value.title,
      brand: detailItem.brand || selectedDetailItem.value.brand,
      description: detailItem.description || selectedDetailItem.value.description,
      categoryLabel: detailItem?.category?.name || selectedDetailItem.value.categoryLabel,
      inspectionGrade: detailItem.quality || selectedDetailItem.value.inspectionGrade,
      carrier: detail?.carrier || '',
      trackingNumber: detail?.trackingNumber || '',
      status: detail?.status || selectedDetailItem.value.status,
      statusLabel: statusToLabel(detail?.status || selectedDetailItem.value.status),
      images: detailImages.length ? detailImages : selectedDetailItem.value.images,
      image: detailImages[0]?.url || selectedDetailItem.value.image,
    }
  } catch {
    // 목록 데이터 기반으로 모달 유지
  } finally {
    isDetailLoading.value = false
  }
}

function closeInspectionDetail() {
  selectedDetailItem.value = null
}

function isPendingStatus(status) {
  return status === 'PENDING' || status === '검수 대기'
}

function openApproveFromDetail() {
  if (!selectedDetailItem.value) return
  const inspectionNo = selectedDetailItem.value.inspectionId
  closeInspectionDetail()
  openApproveModal(inspectionNo)
}

function openRejectFromDetail() {
  if (!selectedDetailItem.value) return
  const inspectionNo = selectedDetailItem.value.inspectionId
  closeInspectionDetail()
  openRejectModal(inspectionNo)
}

async function submitApprove(grade) {
  if (!approveTargetInspectionNo.value) return

  isProcessing.value = true
  errorMessage.value = ''

  try {
    await approveAdminInspection(approveTargetInspectionNo.value, grade)
    closeApproveModal()
    await loadInspections()
  } catch (error) {
    errorMessage.value = String(error?.message || '검수 상태 변경에 실패했습니다.')
  } finally {
    isProcessing.value = false
  }
}

function openApproveModal(inspectionNo) {
  if (isProcessing.value) return
  approveTargetInspectionNo.value = inspectionNo
}

function closeApproveModal(inspectionNo) {
  approveTargetInspectionNo.value = null
}

function openRejectModal(inspectionNo) {
  if (isProcessing.value) return
  rejectTargetInspectionNo.value = inspectionNo
}

function closeRejectModal() {
  rejectTargetInspectionNo.value = null
}

async function submitReject(reason) {
  if (!rejectTargetInspectionNo.value) return

  isProcessing.value = true
  errorMessage.value = ''

  try {
    await rejectAdminInspection(rejectTargetInspectionNo.value, reason)
    closeRejectModal()
    await loadInspections()
  } catch (error) {
    errorMessage.value = String(error?.message || '검수 상태 변경에 실패했습니다.')
  } finally {
    isProcessing.value = false
  }
}

async function loadInspections() {
  const seq = ++requestSeq
  isLoading.value = true
  errorMessage.value = ''

  try {
    const page = await fetchAdminInspections({
      page: 1,
      size: 100,
      order: 'DESC',
      status: filterToApiStatus[selectedFilter.value],
      name: searchQuery.value.trim() || undefined,
    })

    if (seq !== requestSeq) return

    inspections.value = Array.isArray(page?.content) ? page.content.map(toUiInspection) : []
    totalElements.value = Number(page?.totalElements || 0)
  } catch (error) {
    if (seq !== requestSeq) return
    errorMessage.value = String(error?.message || '요청 처리 중 오류가 발생했습니다.')
  } finally {
    if (seq === requestSeq) {
      isLoading.value = false
    }
  }
}

watch([selectedFilter, searchQuery], () => {
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  searchTimer = setTimeout(() => {
    loadInspections()
  }, 300)
})

onMounted(loadInspections)
</script>
